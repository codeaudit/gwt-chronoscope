package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.data.tuple.BasicTuple2D;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;

/**
 * {@link Dataset} composed of {@link Tuple2D} data points.
 * 
 * @author Chad Takahashi
 */
public class ArrayDataset2D extends AbstractArrayDataset<Tuple2D> {
  private BasicTuple2D flyweightTuple = new BasicTuple2D(0, 0);

  public ArrayDataset2D(DatasetRequest request) {
    super(request);
  }

  @Override
  public Tuple2D getFlyweightTuple(int index, int mipLevel) {
    double domain = dimensions[0].get(mipLevel, index);
    double range = dimensions[1].get(mipLevel, index);
    flyweightTuple.setCoordinates(domain, range);
    return flyweightTuple;
  }

}
