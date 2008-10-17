package org.timepedia.chronoscope.client.data.tuple;

/**
 * @author chad takahashi
 */
public class BasicTuple2D implements Tuple2D {
  private double x, y;

  public BasicTuple2D(double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  public double getFirst() {
    return x;
  }

  public double getSecond() {
    return y;
  }

  public Tuple copy() {
    return new BasicTuple2D(this.x, this.y);
  }

  public double getCoordinate(int i) {
    switch (i) {
      case 0:
        return x;
      case 1:
        return y;
      default:
        throw new IllegalArgumentException("Illegal coordinate: " + i);
    }
  }

  public int getDimension() {
    return 2;
  }
  
  public void setCoordinates(double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  public String toString() {
    return "[x=" + x + ", y=" + y + "]";
  }
}
