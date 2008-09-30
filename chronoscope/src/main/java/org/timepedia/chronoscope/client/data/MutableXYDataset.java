package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.data.Mutation.AppendMutation;
import org.timepedia.chronoscope.client.util.ArgChecker;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link XYDataset} that permits certain types of mutations (e.g. appending new
 * data points, modifying Y-value of existing data points).
 * <p>
 * 
 * @see Mutation
 * 
 * @author Chad Takahashi
 */
public class MutableXYDataset extends ArrayXYDataset {
  private MipMapStrategy mipMapStrategy;
  private List<XYDatasetListener> listeners = new ArrayList<XYDatasetListener>();

  public MutableXYDataset(XYDatasetRequest request) {
    super(request);
    mipMapStrategy = (MipMapStrategy) ArgChecker.isNotNull(
        request.getDefaultMipMapStrategy(), "request.mipMapStrategy");
  }

  public void addXYDatasetListener(XYDatasetListener listener) {
    ArgChecker.isNotNull(listener, "listener");
    this.listeners.add(listener);
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
      mipMapStrategy.setRangeValue(m.getPointIndex(), newY, multiRange);
      newX = this.getX(m.getPointIndex());
    } 
    else {
      // TODO: Can add more mutation handlers later
      throw new UnsupportedOperationException("mutation of type "
          + mutation.getClass().getName() + " currently not supported");
    }
    
    rangeBottom = Math.min(rangeBottom, newY);
    rangeTop = Math.max(rangeTop, newY);
    notifyListeners(this, newX, newX);
  }

  private void appendXY(double x, double y) {
    if (x <= getDomainEnd()) {
      throw new IllegalArgumentException(
          "Insertions not allowed; x was <= domainEnd: " + x + ":"
              + getDomainEnd());
    }

    mipMapStrategy.appendDomainValue(x, multiDomain);
    mipMapStrategy.appendRangeValue(y, multiRange);
  }

  private void notifyListeners(XYDataset ds, double domainStart, double domainEnd) {
    for (XYDatasetListener l : this.listeners) {
      l.onDatasetChanged(ds, domainStart, domainEnd);
    }
  }
}
