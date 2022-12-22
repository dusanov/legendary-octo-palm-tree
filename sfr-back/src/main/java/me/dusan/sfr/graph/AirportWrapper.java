package me.dusan.sfr.graph;

import me.dusan.sfr.domains.Airport;

public class AirportWrapper<T> implements Comparable<AirportWrapper<Airport>> {
  private final Airport airport;
  private AirportWrapper<Airport> predecessor;
  private Double totalCostFromStart;
  private final Double minimumRemainingCostToTarget;
  private Double costSum;

  public AirportWrapper(
    Airport node,
    AirportWrapper<Airport> predecessor,
    Double totalCostFromStart,
    Double minimumRemainingCostToTarget) {
    this.airport = node;
    this.predecessor = predecessor;
    this.totalCostFromStart = totalCostFromStart;
    this.minimumRemainingCostToTarget = minimumRemainingCostToTarget;
    calculateCostSum();
  }

  private void calculateCostSum() {
    this.costSum = this.totalCostFromStart + this.minimumRemainingCostToTarget;
  }

  public Airport getNode() {
    return airport;
  }

  public void setPredecessor(AirportWrapper<Airport> predecessor) {
    this.predecessor = predecessor;
  }

  public AirportWrapper<Airport> getPredecessor() {
    return predecessor;
  }

  public void setTotalCostFromStart(Double totalCostFromStart) {
    this.totalCostFromStart = totalCostFromStart;
    calculateCostSum();
  }

  public Double getTotalCostFromStart() {
    return totalCostFromStart;
  }

  @Override
  public int compareTo(AirportWrapper<Airport> other) {
    int compare = this.costSum.compareTo(other.costSum);
    if (compare == 0) {
      compare = airport.compareTo(other.airport);
    }
    return compare;
  }
}
