package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.util.ArgChecker;

/**
 * Represents a request to construct an instance of {@link Dataset} using
 * {@link DatasetFactory}.
 * 
 * @see DatasetFactory
 * 
 * @author Chad Takahashi
 */
public abstract class DatasetRequest {
  private static int MAX_TUPLE_DIMENSION = 10;
 
  /**
   * Request in which client provides their own n-tuple data, and the
   * mipmapped data is then computed by the provided
   * {@link #setDefaultMipMapStrategy(MipMapStrategy) MipMapStrategy} object.
   */
  public static final class Basic extends DatasetRequest {
    // tupleData[n] represents the Nth dimension value of each tuple in the dataset
    private double[][] tupleData = new double[MAX_TUPLE_DIMENSION][];
    
    /**
     * Returns an array containing the Nth element of every tuple in this request,
     * where N is the specified index. 
     */
    public double[] getTupleSlice(int index) {
      return tupleData[index];
    }
    
    /**
     * See {@link #getTupleSlice(int)}.
     */
    public void setTupleSlice(int index, double[] slice) {
      tupleData[index] = slice;
    }
    
    public void validate() {
      super.validate();
      ArgChecker.isNotNull(tupleData[0], "domain");
      ArgChecker.isNotNull(tupleData[1], "range");

      if (tupleData[0].length != tupleData[1].length) {
        throw new IllegalArgumentException("domain[] and range[] are different lengths");
      }
    }
  }
  /**
   * Request in which the n-tuple values at each mipmap level must be 
   * manually specified.
   */
  public static final class MultiRes extends DatasetRequest {
    private Array2D[] mipmappedTupleData = new Array2D[MAX_TUPLE_DIMENSION];

    public Array2D getMultiresTupleSlice(int index) {
      return mipmappedTupleData[index];
    }
    
    public void setMultiresTupleSlice(int index, Array2D a) {
      mipmappedTupleData[index] = a;
    }
    
    public void validate() {
      ArgChecker.isNotNull(mipmappedTupleData[0], "multiDomain");
      ArgChecker.isNotNull(mipmappedTupleData[1], "multiRange");

      // Verify that multiDomain and multiRange have same number
      // of elements at each level
      if (!mipmappedTupleData[0].isSameSize(mipmappedTupleData[1])) {
        throw new IllegalArgumentException(
            "multiDomain and multiRange differ in size");
      }
    }
  }

  private double approximateMinimumInterval = Double.NaN;
  private String axisId, identifier, label;
  private MipMapStrategy defaultMipMapStrategy = DefaultMipMapStrategy.MEAN;
  private double rangeBottom = Double.NaN, rangeTop = Double.NaN;

  public double getApproximateMinimumInterval() {
    return approximateMinimumInterval;
  }

  public String getAxisId() {
    return axisId;
  }

  public String getIdentifier() {
    return identifier;
  }

  public String getLabel() {
    return label;
  }

  public MipMapStrategy getDefaultMipMapStrategy() {
    return defaultMipMapStrategy;
  }

  public double getRangeBottom() {
    return rangeBottom;
  }

  public double getRangeTop() {
    return rangeTop;
  }

  public void setApproximateMinimumInterval(double approximateMinimumInterval) {
    this.approximateMinimumInterval = approximateMinimumInterval;
  }

  public void setAxisId(String axisId) {
    this.axisId = axisId;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public void setDefaultMipMapStrategy(MipMapStrategy mipMapStrategy) {
    this.defaultMipMapStrategy = mipMapStrategy;
  }

  public void setRangeBottom(double rangeBottom) {
    this.rangeBottom = rangeBottom;
  }

  public void setRangeTop(double rangeTop) {
    this.rangeTop = rangeTop;
  }

  /**
   * Validates the state of this request object.
   */
  public void validate() {
    ArgChecker.isNotNull(defaultMipMapStrategy, "defaultMipMapStrategy");
    //TODO: add checker that can compare two args and report an error in the
    //relationship between them
    ArgChecker.isLT(rangeBottom, rangeTop, "rangeBottom>rangeTop");
  }

}
