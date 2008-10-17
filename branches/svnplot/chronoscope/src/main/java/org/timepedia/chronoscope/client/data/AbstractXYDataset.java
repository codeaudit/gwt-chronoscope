package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;

import java.util.Comparator;

/**
 * Provides skeletal implementation of an {@link XYDataset} to simplify implementation
 * of a concrete class.
 * 
 * @author Chad Takahashi
 */
public abstract class AbstractXYDataset<T extends Tuple2D> implements XYDataset<T> {
  protected double rangeBottom, rangeTop;

  protected double approximateMinimumInterval;

  protected String axisId, identifier, rangeLabel;

  /**
   * Returns the maximum tuple value in this dataset.
   * 
   * @param c - Determines the ordering of the tuples
   */
  public T getMaxValue(Comparator<Tuple2D> c, int mipLevel) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the minimum tuple value in this dataset.
   * 
   * @param c - Determines the ordering of the tuples
   */
  public T getMinValue(Comparator<Tuple2D> c, int mipLevel) {
    throw new UnsupportedOperationException();
  }


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

  public T getFlyweightTuple(int index) {
    throw new UnsupportedOperationException();
  }

  public T getFlyweightTuple(int index, int mipLevel) {
    throw new UnsupportedOperationException();
  }

}
