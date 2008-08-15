package org.timepedia.chronoscope.client.data;

/**
 * @author chad takahashi
 *
 */
public final class SimpleDataset {
  public String id;
  public double[] domain, range;
  
  public SimpleDataset(String id, double[] domain, double[] range) {
    this.domain = domain;
    this.range = range;
    this.id = id;
  }
  
  public String toString() {
    return id;
  }
}
