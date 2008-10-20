package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.MutableDataset;
import org.timepedia.chronoscope.client.data.Mutation.AppendMutation;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.util.ArgChecker;

import java.util.ArrayList;
import java.util.List;

/**
 * Dataset that permits certain types of mutations (e.g. appending new
 * data points, modifying Y-value of existing data points).
 * <p>
 * 
 * @see Mutation
 * 
 * @author Chad Takahashi
 */
public class MutableDataset2D extends ArrayDataset2D implements MutableDataset<Tuple2D> {
  private MipMapStrategy mipMapStrategy;
  private List<DatasetListener<Tuple2D>> listeners = new ArrayList<DatasetListener<Tuple2D>>();

  public MutableDataset2D(DatasetRequest request) {
    super(request);
    mipMapStrategy = (MipMapStrategy) ArgChecker.isNotNull(
        request.getDefaultMipMapStrategy(), "request.mipMapStrategy");
  
  }

  public void addListener(DatasetListener<Tuple2D> listener) {
    ArgChecker.isNotNull(listener, "listener");
    this.listeners.add(listener);
  }
  
  public void removeListener(DatasetListener<Tuple2D> listener) {
    listeners.remove(listener);
  }

  public void mutate(Mutation mutation) {
    ArgChecker.isNotNull(mutation, "mutation");

    double newX, newY;

    if (mutation instanceof Mutation.AppendMutation) {
      AppendMutation m = (Mutation.AppendMutation) mutation;
      newY = m.getY();
      newX = m.getX();
      double newInterval = newX - getX(getNumSamples() - 1);
      this.approximateMinimumInterval = Math.min(
          this.approximateMinimumInterval, newInterval);
      appendXY(newX, newY);
    } 
    else if (mutation instanceof Mutation.RangeMutation) {
      Mutation.RangeMutation m = (Mutation.RangeMutation) mutation;
      newY = m.getY();
      mipMapStrategy.setRangeValue(m.getPointIndex(), newY, dimensions[1]);
      newX = this.getX(m.getPointIndex());
    } 
    else {
      // TODO: Can add more mutation handlers later
      throw new UnsupportedOperationException("mutation of type "
          + mutation.getClass().getName() + " currently not supported");
    }
    
    minRange = Math.min(minRange, newY);
    maxRange = Math.max(maxRange, newY);
    notifyListeners(this, newX, newX);
  }

  private void appendXY(double x, double y) {
    if (x <= getDomainEnd()) {
      throw new IllegalArgumentException(
          "Insertions not allowed; x was <= domainEnd: " + x + ":"
              + getDomainEnd());
    }

    mipMapStrategy.appendDomainValue(x, dimensions[0]);
    mipMapStrategy.appendRangeValue(y, dimensions[1]);
  }

  private void notifyListeners(Dataset<Tuple2D> ds, double domainStart, double domainEnd) {
    for (DatasetListener<Tuple2D> l : this.listeners) {
      l.onDatasetChanged(ds, domainStart, domainEnd);
    }
  }
}
