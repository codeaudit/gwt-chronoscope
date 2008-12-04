package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;

/**
 * {@link Dataset} composed of {@link Tuple2D} data points.
 * 
 * @author Chad Takahashi
 */
public class ArrayDataset2D extends AbstractArrayDataset<Tuple2D> {
  
  public ArrayDataset2D(DatasetRequest request) {
    super(request);
  }

  @Override
  public Tuple2D getFlyweightTuple(int index, int mipLevel) {
    flyweightTuple.setDataPointIndex(index);
    flyweightTuple.setMipLevel(mipLevel);
    return flyweightTuple;
  }

}
