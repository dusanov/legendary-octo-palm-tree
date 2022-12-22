package me.dusan.sfr.controller;

import java.util.stream.Collectors;

import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

import me.dusan.sfr.domains.Airport;
import me.dusan.sfr.domains.SearchResult;
import me.dusan.sfr.graph.GraphComponent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class GraphQL{

    private final GraphComponent graph;
    public GraphQL(GraphComponent graph) {
        this.graph = graph;
    };

    @QueryMapping
    public Mono<Integer> countAllAirports() {
        return Mono.just(graph.getWeightedGraph().nodes().size() );
    }

    @QueryMapping
    public Flux<Airport> getAllAirports(){
        return Flux.fromStream(graph.getWeightedGraph().nodes().stream()) 
            .filter( airport -> graph.getWeightedGraph().successors(airport).size() > 0 )
            .map(airport -> {airport.setDestinations(graph.getWeightedGraph().successors(airport));return airport;} );
            // .collect(Collectors.toList());
    }

    @QueryMapping
    public Mono<Airport> getAirport(@Argument String iata){
        return Mono.just(this.graph.getWeightedGraph().nodes().stream().filter(airport -> airport.getIata().equals(iata)).collect(Collectors.toList()).get(0));
    }

    @QueryMapping
    public Mono<SearchResult> search(@Argument String from, 
                                        @Argument String destination, 
                                        @Argument String algo) {
        long start = System.currentTimeMillis();
        SearchResult searchResult = new SearchResult();
        if (algo.equals("BFS"))
            searchResult =  graph.bfs(from,destination);
        else if (algo.equals("astar"))
            searchResult = graph.astar(from,destination);
        else if (algo.equals("dijkstra"))
            searchResult = graph.dijkstra(from,destination);            

        searchResult.setTime(System.currentTimeMillis()-start);
        return Mono.just(searchResult) ;
    }

    @SubscriptionMapping
    public Publisher<SearchResult> currentSearchPath(){
        return graph.currentSearchPath();
    }
}
