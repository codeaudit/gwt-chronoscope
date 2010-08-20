package org.timepedia.chronoscope.client.data;

/**
 * Used exclusively for unit testing.
 * 
 * @author chad takahashi
 */
public final class JUnitDataset {
  public String id;
  public double[] domain, range;
  
  public JUnitDataset(String id, double[] domain, double[] range) {
    this.domain = domain;
    this.range = range;
    this.id = id;
  }
  
  public String toString() {
    return id;
  }
}
