package me.dusan.sfr.graph;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.dusan.sfr.domains.Airport;
import me.dusan.sfr.domains.SearchResult;
import me.dusan.sfr.services.CsvMapperService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import org.reactivestreams.Publisher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GraphComponent implements CommandLineRunner {

    private final CsvMapperService csvMapper;
    private final Sinks.Many<SearchResult> currentSearchPathSink;
    private final Flux<SearchResult> currentSearchPathFlux;        
    @Getter
    MutableValueGraph<Airport, Double> weightedGraph = ValueGraphBuilder
                                                                .directed()
                                                                //TODO: is this data related ?
                                                                .allowsSelfLoops(true)
                                                                .build();
    private double maxSpeed;
                                                                

    @Override
    public void run(String... args) throws Exception {
        csvMapper.loadAirports(weightedGraph);
        System.out.println("Done loading: " + weightedGraph.nodes().size() + " nodes, " + weightedGraph.edges().size() + " edges..");
        this.maxSpeed = Heuristic.calculateMaxSpeed(weightedGraph);
        System.out.println("Max speed: " + maxSpeed);
    }

    public Publisher<SearchResult> currentSearchPath() {
      return currentSearchPathFlux;
  }    

    public SearchResult bfs(String start, String destination) {
      Airport startAirport = this.weightedGraph.nodes().stream().filter(airport -> airport.getIata().equals(start)).collect(Collectors.toList()).get(0);
      Airport destinationAirport = this.weightedGraph.nodes().stream().filter(airport -> airport.getIata().equals(destination)).collect(Collectors.toList()).get(0);

        Map<Airport, AirportWrapper<Airport>> airportWrappers = new HashMap<>();
        PriorityQueue<AirportWrapper<Airport>> frontier = new PriorityQueue<>();
        Set<Airport> visited = new HashSet<>();

        AirportWrapper<Airport> startWrapper = new AirportWrapper<>(startAirport, null, 0.0,0.0);
        airportWrappers.put(startAirport, startWrapper);
        frontier.add(startWrapper);        

        int nodeCounter = 0;
        while (! frontier.isEmpty()){
            AirportWrapper<Airport> currentAirportWrapper = frontier.poll();
            Airport currentAirport = currentAirportWrapper.getNode();
            visited.add(currentAirport);
            nodeCounter++;
            currentSearchPathSink.tryEmitNext(buildSearchResult(currentAirportWrapper, "BFS", nodeCounter));
            if (currentAirport.equals(destinationAirport)){
              return buildSearchResult(currentAirportWrapper, "BFS", nodeCounter);
            }
            for (Airport successorAirport : weightedGraph.successors(currentAirport)) {
              if (visited.contains(successorAirport)) continue;
              AirportWrapper<Airport> successorAirportWrapper = airportWrappers.get(successorAirport);
              if (successorAirportWrapper == null) {
                successorAirportWrapper =
                    new AirportWrapper<>(successorAirport, 
                                          currentAirportWrapper, 
                                          0.0, 
                                          0.0);
                airportWrappers.put(successorAirport, successorAirportWrapper);
                frontier.add(successorAirportWrapper);
              }
            }
        }
        return new SearchResult(0, "BFS", nodeCounter, 0, 0, Collections.emptyList());
    }

  public SearchResult astar(String start, String destination) {
    Airport startAirport = this.weightedGraph.nodes().stream().filter(airport -> airport.getIata().equals(start)).collect(Collectors.toList()).get(0);  //this.graph.get(start);
    Airport destinationAirport = this.weightedGraph.nodes().stream().filter(airport -> airport.getIata().equals(destination)).collect(Collectors.toList()).get(0); // this.graph.get(destination);
    Function<Airport, Double> heuristic = new Heuristic(maxSpeed, destinationAirport);
    Map<Airport, AirportWrapper<Airport>> airportWrappers = new HashMap<>();
    PriorityQueue<AirportWrapper<Airport>> frontier = new PriorityQueue<>();
    Set<Airport> visited = new HashSet<>();

    AirportWrapper<Airport> startWrapper = new AirportWrapper<>(startAirport, null, 0.0, heuristic.apply(startAirport));
    airportWrappers.put(startAirport, startWrapper);
    frontier.add(startWrapper);

    long nodeCounter = 0;
    while (!frontier.isEmpty()) {
      AirportWrapper<Airport> currentAirportWrapper = frontier.poll();
      Airport currentAirport = currentAirportWrapper.getNode();
      visited.add(currentAirport);
      nodeCounter++;
      currentSearchPathSink.tryEmitNext(buildSearchResult(currentAirportWrapper, "A*", nodeCounter));
      if (currentAirport.equals(destinationAirport)) {
        return buildSearchResult(currentAirportWrapper, "A*", nodeCounter);
      }
      for (Airport successorAirport : weightedGraph.successors(currentAirport)) {
        if (visited.contains(successorAirport)) continue;
        double cost = weightedGraph.edgeValue(currentAirport, successorAirport).orElseThrow(IllegalStateException::new);
        double totalCostFromStart = cost + currentAirportWrapper.getTotalCostFromStart();
        AirportWrapper<Airport> successorAirportWrapper = airportWrappers.get(successorAirport);
        if (successorAirportWrapper == null) {
          successorAirportWrapper =
              new AirportWrapper<>(successorAirport, 
                                    currentAirportWrapper, 
                                    totalCostFromStart, 
                                    heuristic.apply(successorAirport));
          airportWrappers.put(successorAirport, successorAirportWrapper);
          frontier.add(successorAirportWrapper);
        } else if (totalCostFromStart < successorAirportWrapper.getTotalCostFromStart()) {
          successorAirportWrapper.setTotalCostFromStart(totalCostFromStart);
          successorAirportWrapper.setPredecessor(currentAirportWrapper);
          frontier.remove(successorAirportWrapper);
          frontier.add(successorAirportWrapper);
        }
      }
    }
    return new SearchResult(0, "A*", nodeCounter, 0, 0, Collections.emptyList());
  }

  public SearchResult dijkstra(String start, String destination) {

    Airport sourceAirport = this.weightedGraph.nodes().stream().filter(airport -> airport.getIata().equals(start)).collect(Collectors.toList()).get(0);
    Airport destinationAirport = this.weightedGraph.nodes().stream().filter(airport -> airport.getIata().equals(destination)).collect(Collectors.toList()).get(0);
    Map<Airport, AirportWrapper<Airport>> nodeWrappers = new HashMap<>();
    PriorityQueue<AirportWrapper<Airport>> frontier = new PriorityQueue<>();
    Set<Airport> visited = new HashSet<>();

    AirportWrapper<Airport> sourceWrapper = new AirportWrapper<>(sourceAirport, null, 0.0,0.0);
    nodeWrappers.put(sourceAirport, sourceWrapper);
    frontier.add(sourceWrapper);

    int nodeCounter = 0;
    while (!frontier.isEmpty()){
      AirportWrapper<Airport> currentNodeWrapper = frontier.poll();
      Airport currentAirport = currentNodeWrapper.getNode();
      visited.add(currentAirport);
      nodeCounter++;
      currentSearchPathSink.tryEmitNext(buildSearchResult(currentNodeWrapper,"dijkstra",nodeCounter));
      if (currentAirport.equals(destinationAirport)) {
        return buildSearchResult(currentNodeWrapper,"dijkstra",nodeCounter);
      }

      for (Airport neighbor : weightedGraph.successors/*adjacentNodes*/(currentAirport)){

        if (visited.contains(neighbor)) {
          continue;
        }

        // Calculate total distance from start to neighbor via current node
        double distance = weightedGraph.edgeValue(currentAirport, neighbor).orElseThrow(IllegalStateException::new);
        double totalDistance = currentNodeWrapper.getTotalCostFromStart() + distance;

        // Neighbor not yet discovered?
        AirportWrapper<Airport> neighborWrapper = nodeWrappers.get(neighbor);
        if (neighborWrapper == null) {
          neighborWrapper = new AirportWrapper<>(neighbor, currentNodeWrapper, totalDistance,0.0);
          nodeWrappers.put(neighbor, neighborWrapper);
          frontier.add(neighborWrapper);
        }

        // Neighbor discovered, but total distance via current node is shorter?
        // --> Update total distance and predecessor
        else if (totalDistance < neighborWrapper.getTotalCostFromStart()) {
          neighborWrapper.setTotalCostFromStart(totalDistance);
          neighborWrapper.setPredecessor(currentNodeWrapper);

          // The position in the PriorityQueue won't change automatically;
          // we have to remove and reinsert the node
          frontier.remove(neighborWrapper);
          frontier.add(neighborWrapper);
        }        
      }
    }
    return new SearchResult(0, "dijkstra", nodeCounter, 0, 0, Collections.emptyList());
  }

  private static SearchResult buildSearchResult(AirportWrapper<Airport> nodeWrapper, String algo, long nodeCount) {
    List<Airport> path = new ArrayList<>();
    double distance = 0;
    double price = nodeWrapper.getTotalCostFromStart();
    while (nodeWrapper != null) {
      path.add(nodeWrapper.getNode());
      if (nodeWrapper.getPredecessor()!=null) 
        distance += Heuristic.calculateEuclideanDistance(nodeWrapper.getNode(), nodeWrapper.getPredecessor().getNode());
      nodeWrapper = nodeWrapper.getPredecessor();
    }
    Collections.reverse(path);

    return new SearchResult(
      0,
      algo, 
      nodeCount, 
      distance, 
      price, 
      path);
  }
}