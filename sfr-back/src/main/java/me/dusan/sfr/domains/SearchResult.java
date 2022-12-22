package me.dusan.sfr.domains;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult {

    private long time;
    private String algo;
    private long numNodes;
    private double distance;
    private double price;
    private List<Airport> path;
    
}
