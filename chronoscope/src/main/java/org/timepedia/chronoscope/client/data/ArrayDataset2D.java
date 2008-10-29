package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.data.tuple.BasicTuple2D;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.util.Array2D;

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

  @Override
  protected void loadTupleData(DatasetRequest tupleData) {
    dimensions = new Array2D[2];
    
    if (tupleData instanceof DatasetRequest.MultiRes) {
      // multiDomain and multiRange explicitly specified in request object.
      DatasetRequest.MultiRes multiResReq = (DatasetRequest.MultiRes) tupleData;
      dimensions[0] = multiResReq.getMultiresTupleSlice(0);
      dimensions[1] = multiResReq.getMultiresTupleSlice(1);
    } else if (tupleData instanceof DatasetRequest.Basic) {
      // Use MipMapStrategy to calculate multiDomain and MultiRange from
      // the domain[] and range[] specified in the basic request.
      DatasetRequest.Basic basicReq = (DatasetRequest.Basic) tupleData;
      MipMapStrategy mms = basicReq.getDefaultMipMapStrategy();
      dimensions[0] = mms.calcMultiDomain(basicReq.getTupleSlice(0));
      dimensions[1] = mms.calcMultiRange(basicReq.getTupleSlice(1));
    }
    else {
      throw new RuntimeException("Unsupported request type: " 
          + tupleData.getClass().getName());
    }
  }

}
