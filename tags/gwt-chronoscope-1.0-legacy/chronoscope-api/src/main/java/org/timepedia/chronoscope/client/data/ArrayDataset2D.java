package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.exporter.client.Exportable;

/**
 * {@link Dataset} composed of {@link Tuple2D} data points.
 * 
 * @author Chad Takahashi
 */
public class ArrayDataset2D extends AbstractArrayDataset<Tuple2D> implements
    Exportable {
  
  public ArrayDataset2D(DatasetRequest request) {
    super(request);
  }

}
