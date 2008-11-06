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
  private final int tupleLength;
  
  public FlyweightTuple(Array2D[] tupleDimensions) {
    this.tupleDimensions = tupleDimensions;
    this.tupleLength = tupleDimensions.length;
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
    return tupleDimensions[0].get(mipLevel, dataPointIndex);
  }

  public double getSecond() {
    return tupleDimensions[1].get(mipLevel, dataPointIndex);
  }

  public double getThird() {
    return tupleDimensions[2].get(mipLevel, dataPointIndex);
  }

  public double getFourth() {
    return tupleDimensions[3].get(mipLevel, dataPointIndex);
  }

  public double getFifth() {
    return tupleDimensions[4].get(mipLevel, dataPointIndex);
  }

  public int size() {
    return tupleLength;
  }

  public String toString() {
    return "[" + get(0) + ", " + get(1) + ", " + get(2) + ", " + get(3) + ", "
        + get(4) + "]";
  }
}
