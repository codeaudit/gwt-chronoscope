package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.XYDataset;

/**
 * Provides skeletal implementation of an {@link XYDataset} to simplify implementation
 * of a concrete class.
 * 
 * @author Chad Takahashi
 */
public abstract class AbstractXYDataset implements XYDataset {
  protected double rangeBottom, rangeTop;

  protected double approximateMinimumInterval;

  protected String axisId, identifier, rangeLabel;

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

  public final int getNumSamples() {
    return getNumSamples(0);
  }

  public final double getRangeBottom() {
    return rangeBottom;
  }

  public final String getRangeLabel() {
    return rangeLabel;
  }

  public final double getRangeTop() {
    return rangeTop;
  }

  public final double getX(int index) {
    return getX(index, 0);
  }

  public final double getY(int index) {
    return getY(index, 0);
  }

}
