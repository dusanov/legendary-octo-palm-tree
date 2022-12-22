package me.dusan.sfr.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.graph.MutableValueGraph;

import lombok.RequiredArgsConstructor;
import me.dusan.sfr.configs.StorageProperties;
import me.dusan.sfr.domains.Airport;
import me.dusan.sfr.domains.Route;

@Service
@RequiredArgsConstructor
public class CsvMapperService {


    private final StorageProperties storageProps;
    private final FileSystemStorageService storageService;

    public void loadAirports(MutableValueGraph<Airport, Double> weightedGraph) throws Exception {
        Resource fileResource = storageService.loadAsResource(storageProps.getAirport());
        CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();//.withoutHeader();
        CsvMapper mapper = new CsvMapper();
        mapper.registerModule(new JavaTimeModule());
        MappingIterator<Airport> readValues = mapper
                                                .readerFor(Airport.class)
                                                .with(bootstrapSchema)
                                                .readValues(fileResource.getFile());

        Map<String,Airport> iataMap = new HashMap<>();

        while (readValues.hasNext()){
            Airport airport = readValues.next();
            weightedGraph.addNode(airport);
            iataMap.put(airport.getIata(), airport);
        }

        fileResource = storageService.loadAsResource(storageProps.getRoutes());
        MappingIterator<Route> readRoute = mapper.readerFor(Route.class).with(bootstrapSchema).readValues(fileResource.getFile());
        List<Route> failedRoutes = new ArrayList<>();
        List<Route> duplicateRoutes = new ArrayList<>();
        while (readRoute.hasNext()){
            Route route = readRoute.next();
            Airport start = iataMap.get(route.getSourceAirport());
            Airport destination = iataMap.get(route.getDestinationAirport());
            if (start != null && destination != null) {
                if (weightedGraph.edgeValue(start, destination).isPresent()){duplicateRoutes.add(route);}
                else{
                    weightedGraph.putEdgeValue(
                        start, 
                        destination, 
                        route.getPrice()==null? 0.33 : route.getPrice());
                }
            } else { failedRoutes.add(route); }
        }
        System.out.println(" Failed to import this many routes: " + failedRoutes.size());
        System.out.println(" Duplicate routes: " + duplicateRoutes.size());
    }

}
