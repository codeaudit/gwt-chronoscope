package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.data.tuple.Tuple5D;
import org.timepedia.chronoscope.client.util.Array2D;

/**
 * @author chad takahashi
 * 
 */
public final class FlyweightTuple implements Tuple5D {
  private Array2D[] tupleDimensions;
  private int mipLevel = -1;
  private int dataPointIndex = -1;

  public FlyweightTuple(Array2D[] tupleDimensions) {
    this.tupleDimensions = tupleDimensions;
  }
  
  public void setDataPointIndex(int index) {
    this.dataPointIndex = index;
  }

  public void setMipLevel(int mipLevel) {
    this.mipLevel = mipLevel;
  }

  public Tuple2D copy() {
    throw new UnsupportedOperationException();
  }

  public double get(int index) {
    return tupleDimensions[index].get(mipLevel, dataPointIndex);
  }

  public double getFirst() {
    return get(0);
  }

  public double getSecond() {
    return get(1);
  }

  public double getThird() {
    return get(2);
  }

  public double getFourth() {
    return get(3);
  }

  public double getFifth() {
    return get(4);
  }

  public int size() {
    return 5;
  }

  public String toString() {
    return "[" + get(0) + ", " + get(1) + ", " + get(2) + ", " + get(3) + ", "
        + get(4) + "]";
  }
}
