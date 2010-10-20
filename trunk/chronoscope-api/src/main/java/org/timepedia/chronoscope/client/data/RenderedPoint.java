package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.data.tuple.Tuple2D;

/**
 * @author chad takahashi
 */
public final class RenderedPoint implements Tuple2D {
  private double domain = -1;
  private double range = -1;
  private double x = -1;
  private double y = -1;

  // private int datasetIndex;
  // private int datasetId;

  public String toString() {
    String ret = "";
    ret += " domain:" + domain;
    ret += ", range:" + range;
    ret += ", x:" + x;
    ret += ", y:" + y;
    return ret;
  }

  public RenderedPoint(double domain, double range, double x, double y) {
    this.domain = domain;
    this.range = range;
    this.x = x;
    this.y = y;
  }

  public double getRange(int rangeTupleIndex) {
    return range;
  }

  public int size() {
    return (x > -1 && y>  -1 && domain > -1) ? 1 : 0;
  }

  public double getDomain() {
    return domain;
  }

  public double getRange0() {
    return range;
  }
}
