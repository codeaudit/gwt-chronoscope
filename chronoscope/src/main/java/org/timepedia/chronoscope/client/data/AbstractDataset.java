package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;

/**
 * Provides skeletal implementation of an {@link Dataset} to simplify implementation
 * of a concrete class.
 * 
 * @author Chad Takahashi
 */
public abstract class AbstractDataset<T extends Tuple2D> implements Dataset<T> {

  protected double minInterval;

  protected String axisId, identifier, rangeLabel;
  
  protected String preferredRenderer;
  
  public final String getAxisId() {
    return axisId;
  }

  public final double getDomainBegin() {
    return getX(0);
  }

  public final double getDomainEnd() {
    return getX(getNumSamples() - 1);
  }

  public final String getIdentifier() {
    return identifier;
  }

  public final double getMinInterval() {
    return minInterval;
  }
  
  public final int getNumSamples() {
    return getNumSamples(0);
  }
  
  public final String getPreferredRenderer() {
    return preferredRenderer;
  }

  public final String getRangeLabel() {
    return rangeLabel;
  }

  public final double getX(int index) {
    return getX(index, 0);
  }

}
