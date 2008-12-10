package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.data.tuple.Tuple5D;
import org.timepedia.chronoscope.client.util.Array1D;

/**
 * @author chad takahashi
 */
public final class FlyweightTuple implements Tuple5D {
  private int dataPointIndex = -1;
  private MipMapChain mipMapChain;
  private Array1D[] tupleData;
  private Array1D domainX, rangeY;
  private int mipLevel = -1;
  private final int tupleLength;
  
  public FlyweightTuple(MipMapChain mipMapChain) {
    this.mipMapChain = mipMapChain;
    this.tupleLength = 1 + mipMapChain.getRangeTupleSize();
    this.tupleData = new Array1D[this.tupleLength];
  }
  
  public void setDataPointIndex(int index) {
    this.dataPointIndex = index;
  }

  public void setMipLevel(int mipLevel) {
    if (mipLevel != this.mipLevel) {
      MipMap mipMap = this.mipMapChain.getMipMap(mipLevel);
      this.mipLevel = mipLevel;
      this.domainX = mipMap.getDomain();
      this.rangeY = mipMap.getRange(0);
      this.tupleData[0] = this.domainX;
      for (int i = 1; i < this.tupleLength; i++) {
        this.tupleData[i] = mipMap.getRange(i - 1);
      }
    }
  }

  public double get(int tupleIndex) {
    return this.tupleData[tupleIndex].get(this.dataPointIndex);
  }

  public double getFirst() {
    return this.domainX.get(this.dataPointIndex);
    //return get(0);
  }

  public double getSecond() {
    return this.rangeY.get(this.dataPointIndex);
    //return get(1);
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
