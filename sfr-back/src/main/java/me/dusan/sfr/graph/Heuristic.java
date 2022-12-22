package me.dusan.sfr.graph;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableValueGraph;

import me.dusan.sfr.domains.Airport;

import java.util.function.Function;


public class Heuristic implements Function<Airport, Double> {


  private final Airport target;
  private final double maxSpeed;

  public Heuristic(double maxSpeed, Airport target) {
    this.target = target;
    this.maxSpeed = maxSpeed;
  }

  public static double calculateMaxSpeed(MutableValueGraph<Airport, Double> mutableValueGraph) {
    return mutableValueGraph.edges().stream()
        .map(edge -> calculateSpeed(mutableValueGraph, edge))
        .max(Double::compareTo)
        .orElseThrow(() -> new IllegalArgumentException("Graph is empty"));
  }

  private static double calculateSpeed(MutableValueGraph<Airport, Double> mutableValueGraph, 
                                            EndpointPair<Airport> edge) {
      double euclideanDistance = calculateEuclideanDistance(edge.nodeU(), edge.nodeV());
      double cost = mutableValueGraph.edgeValue(edge).orElseThrow(() -> new IllegalArgumentException("Edge value is missing"));
      double speed = euclideanDistance / cost;
      return speed;
  }

  public static double calculateEuclideanDistance(Airport source, Airport target) {
    if (source == null || target == null) throw new IllegalArgumentException("Nodes can not be empty");
    final int R = 6371; // Radius of the earth
    double latDistance = Math.toRadians(target.getLatitude() - source.getLatitude());
    double lonDistance = Math.toRadians(target.getLongitude() - source.getLongitude());
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(source.getLatitude())) * Math.cos(Math.toRadians(target.getLatitude()))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double distance = R * c; //* 1000; // convert to meters
    double height = source.getAltitude() - target.getAltitude();
    distance = Math.pow(distance, 2) + Math.pow(height, 2);
    return  Math.sqrt(distance);
  }

  @Override
  public Double apply(Airport node) {
    double euclideanDistance = calculateEuclideanDistance(node, this.target);
    double minimumCost = euclideanDistance / maxSpeed;

    return minimumCost;
  }
}
