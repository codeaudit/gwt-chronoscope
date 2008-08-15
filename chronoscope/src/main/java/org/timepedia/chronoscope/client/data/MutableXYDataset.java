/**
 * 
 */
package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.data.Mutation.AppendMutation;
import org.timepedia.chronoscope.client.util.ArgChecker;

/**
 * @author Chad Takahashi
 */
public class MutableXYDataset extends ArrayXYDataset {
  private MipMapStrategy mipMapStrategy;
  
  public MutableXYDataset(XYDatasetRequest request) {
    super(request);
    mipMapStrategy = (MipMapStrategy) ArgChecker.isNotNull(
        request.getDefaultMipMapStrategy(), "request.mipMapStrategy");
  }

  public void mutate(Mutation mutation) {
    ArgChecker.isNotNull(mutation, "mutation");
    
    double newY;
    
    if (mutation instanceof Mutation.AppendMutation) {
      AppendMutation m = (Mutation.AppendMutation) mutation;
      newY = m.getY();
      appendXY(m.getX(), newY);
    } else if (mutation instanceof Mutation.RangeMutation) {
      Mutation.RangeMutation m = (Mutation.RangeMutation) mutation;
      newY = m.getY();
      mipMapStrategy.setRangeValue(m.getPointIndex(), newY, multiRange);
    } else {
      // TODO: Can add more mutation handlers later
      throw new UnsupportedOperationException("mutation of type "
          + mutation.getClass().getName() + " currently not supported");
    }

    rangeBottom = Math.min(rangeBottom, newY);
    rangeTop = Math.max(rangeTop, newY);
    
    
  }

  private void appendXY(double x, double y) {
    if (x <= getDomainEnd()) {
      throw new IllegalArgumentException("Insertions not allowed; x was <= domainEnd: " + x + ":"
          + getDomainEnd());
    }

    mipMapStrategy.appendDomainValue(x, multiDomain);
    mipMapStrategy.appendRangeValue(y, multiRange);
  }
}
