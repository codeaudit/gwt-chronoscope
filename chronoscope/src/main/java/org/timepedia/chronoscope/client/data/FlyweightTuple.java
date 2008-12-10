package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.data.tuple.Tuple5D;
import org.timepedia.chronoscope.client.util.Array1D;

/**
 * @author chad takahashi
 */
public final class FlyweightTuple implements Tuple5D {
  private int dataPointIndex = 0;
  private double[][] tupleData;
  private int tupleLength;
  
  public FlyweightTuple(Array1D domain, Array1D[] rangeTuples) {
    this.tupleLength = 1 + rangeTuples.length;
    tupleData = new double[tupleLength][];
    setDomainAndRange(domain, rangeTuples);
  }
  
  public void setDomainAndRange(Array1D domain, Array1D[] rangeTuples) {
    tupleData[0] = domain.backingArray();
    for (int i = 1; i < this.tupleLength; i++) {
      tupleData[i] = rangeTuples[i - 1].backingArray();
    }
  }
  
  public double get(int tupleIndex) {
    return tupleData[tupleIndex][this.dataPointIndex];
  }

  public int size() {
    return tupleLength;
  }

  public double getFirst() {
    return tupleData[0][this.dataPointIndex];
  }

  public double getSecond() {
    return tupleData[1][this.dataPointIndex];
  }

  public double getThird() {
    return tupleData[2][this.dataPointIndex];
  }

  public double getFourth() {
    return tupleData[3][this.dataPointIndex];
  }

  public double getFifth() {
    return tupleData[4][this.dataPointIndex];
  }
  
  public void setDataPointIndex(int dataPointIndex) {
    this.dataPointIndex = dataPointIndex;
  }
}
