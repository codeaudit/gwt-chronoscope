package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.data.tuple.Tuple5D;
import org.timepedia.chronoscope.client.util.Array1D;

/**
 * @author chad takahashi
 */
public final class FlyweightTuple implements Tuple5D {
  private int index;
  private double[][] tupleData;
  private int tupleLength;
  
  public FlyweightTuple() {
    // do nothing
  }
  
  public FlyweightTuple(int index, Array1D domain, Array1D[] rangeTuples) {
    init(index, domain, rangeTuples);
  }
  
  public void init(int index, Array1D domain, Array1D[] rangeTuples) {
    this.index = index;
    this.tupleLength = 1 + rangeTuples.length;
    if (tupleData == null || tupleData.length != this.tupleLength) {
      tupleData = new double[tupleLength][];
    }
    tupleData[0] = domain.backingArray();
    for (int i = 1; i < this.tupleLength; i++) {
      tupleData[i] = rangeTuples[i - 1].backingArray();
    }
  }
  
  public double get(int tupleIndex) {
    return tupleData[tupleIndex][this.index];
  }

  public int size() {
    return tupleLength;
  }

  public double getFirst() {
    return tupleData[0][this.index];
  }

  public double getSecond() {
    return tupleData[1][this.index];
  }

  public double getThird() {
    return tupleData[2][this.index];
  }

  public double getFourth() {
    return tupleData[3][this.index];
  }

  public double getFifth() {
    return tupleData[4][this.index];
  }
  
  public void next() {
    ++this.index;
  }
}
