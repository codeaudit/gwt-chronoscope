package org.timepedia.chronoscope.client;

/**
 * Represents a specific point within the dataset that currently has the focus.
 * 
 * @author chad takahashi &lt;chad@timepedia.org&gt;
 */
public final class Focus {
  private int datasetIndex;
  private int pointIndex;
  private int dimensionIndex = 0;

  /**
   * Constructs a <tt>Focus</tt> whose {@link #getDatasetIndex()} and
   * {@link #getPointIndex()} properties are initialized to <tt>-1</tt>.
   */
  public Focus() {
    this.datasetIndex = -1;
    this.pointIndex = -1;
  }

  private Focus(int datasetIndex, int pointIndex) {
    this.datasetIndex = datasetIndex;
    this.pointIndex = pointIndex;
  }

   private Focus(int datasetIndex, int pointIndex, int dim) {
    this.datasetIndex = datasetIndex;
    this.pointIndex = pointIndex;
    this.dimensionIndex = dim;
  }
  
  /**
   * Returns a copy of this object
   */
  public Focus copy() {
    return new Focus(this.datasetIndex, this.pointIndex, this.dimensionIndex);
  }

  public int getDimensionIndex() {
    return dimensionIndex;
  }

  public void setDimensionIndex(int dimensionIndex) {
    this.dimensionIndex = dimensionIndex;
  }

  public boolean equals(Object obj) {
    if (obj == null || obj.getClass() != Focus.class) {
      return false;
    } else if (obj == this) {
      return true;
    }

    Focus otherFocus = (Focus) obj;
    return this.pointIndex == otherFocus.pointIndex
    && this.datasetIndex == otherFocus.datasetIndex &&
       this.dimensionIndex == otherFocus.dimensionIndex;
  }

  /**
   * Returns the index of the dataset that currently has focus.
   */
  public int getDatasetIndex() {
    if (datasetIndex == -1) {
      throw new IllegalArgumentException("datasetIndex must be >= 0");
    }
    return datasetIndex;
  }

  /**
   * Return an index to the point which has the focus within the selected
   * {@link #getDatasetIndex() dataset}.
   */
  public int getPointIndex() {
    return pointIndex;
  }

  public int hashCode() {
    final int multiplier = 23;
    return this.datasetIndex * multiplier + this.pointIndex;
  }

  public void setDatasetIndex(int datasetIndex) {
    if (datasetIndex == -1) {
      throw new IllegalArgumentException("datasetIndex must be >= 0");
    }
    this.datasetIndex = datasetIndex;
  }

  public void setPointIndex(int pointIndex) {
    this.pointIndex = pointIndex;
  }

  public String toString() {
    return "datasetIndex=" + datasetIndex + ";pointIndex=" + pointIndex;
  }
}
