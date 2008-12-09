package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.data.tuple.Tuple5D;

/**
 * @author chad takahashi
 */
public final class FlyweightTuple implements Tuple5D {
  private int dataPointIndex = -1;
  private final int tupleLength;
  private MipMapChain mipMapChain;
  private MipMap mipMap;
  
  public FlyweightTuple(MipMapChain mipMapChain) {
    this.mipMapChain = mipMapChain;
    this.tupleLength = 1 + mipMapChain.getRangeTupleSize();
  }
  
  public void setDataPointIndex(int index) {
    this.dataPointIndex = index;
  }

  public void setMipLevel(int mipLevel) {
    this.mipMap = this.mipMapChain.getMipMap(mipLevel);
  }

  public Tuple2D copy() {
    throw new UnsupportedOperationException();
  }

  public double get(int tupleIndex) {
    if (tupleIndex == 0) {
      return mipMap.getDomain().get(dataPointIndex);
    }
    else {
      return mipMap.getRange(tupleIndex - 1).get(dataPointIndex);
    }
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
    return tupleLength;
  }

  public String toString() {
    return "[" + get(0) + ", " + get(1) + ", " + get(2) + ", " + get(3) + ", "
        + get(4) + "]";
  }
}
