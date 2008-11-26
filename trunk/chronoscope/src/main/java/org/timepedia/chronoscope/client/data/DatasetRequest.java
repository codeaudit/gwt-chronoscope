package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Array2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a request to construct an instance of {@link Dataset} using
 * {@link DatasetFactory}.
 * 
 * @see DatasetFactory
 * 
 * @author Chad Takahashi
 */
public abstract class DatasetRequest {
 
  /**
   * Request in which client provides their own n-tuple data, and the
   * mipmapped data is then computed by the provided
   * {@link #setDefaultMipMapStrategy(MipMapStrategy) MipMapStrategy} object.
   */
  public static final class Basic extends DatasetRequest {
    // tupleData[n] represents the Nth dimension value of each tuple in the dataset
    List<double[]> tupleData = new ArrayList<double[]>();
    
    public int getTupleLength() {
      return tupleData.size();
    }
    
    /**
     * Returns an array containing the Nth element of every tuple in this request,
     * where N is the specified index. 
     */
    public double[] getTupleSlice(int tupleIndex) {
      return tupleData.get(tupleIndex);
    }
    
    /**
     * The first tuple slice (dimension) added is assumed to be the domain,
     * whose values must be in sorted ascending order.  Subsequent slices are
     * assumed to be components of an n-tuple range.
     *  
     * See {@link #getTupleSlice(int)}.
     */
    public void addTupleSlice(double[] slice) {
      ArgChecker.isNotNull(slice, "slice");
      tupleData.add(slice);
    }
    
    /**
     * Replaces the tuple slice at the specified index with the new slice.
     * 
     * @return the tuple slice previously at the specified index.
     */
    public double[] setTupleSlice(int index, double[] slice) {
      return tupleData.set(index, slice);
    }
    
    public void validate() {
      super.validate();
      
      // Make sure all list elements are non-null
      for (int i = 0; i < tupleData.size(); i++) {
        ArgChecker.isNotNull(tupleData.get(i), "tupleData.get(" + i + ")");
      }
      
      // Make sure all double[] elements are the same length
      int prevLength = tupleData.get(0).length;
      for (int i = 1; i < tupleData.size(); i++) {
        double[] tupleSlice = tupleData.get(i);
        int currLength = tupleSlice.length;
        if (currLength != prevLength) {
          throw new IllegalArgumentException("tupleData[" + i + "] has " + 
              currLength + " elements, but tupleData[" + (i - 1) + "] has " +
              prevLength);
        }
        prevLength = currLength;
      }
    }
  }
  /**
   * Request in which the n-tuple values at each mipmap level must be 
   * manually specified.
   */
  public static final class MultiRes extends DatasetRequest {
    private List<Array2D> mipmappedTupleData = new ArrayList<Array2D>();

    public Array2D getMultiresTupleSlice(int tupleIndex) {
      return mipmappedTupleData.get(tupleIndex);
    }
    
    public void addMultiresTupleSlice(Array2D slice) {
      ArgChecker.isNotNull(slice, "slice");
      mipmappedTupleData.add(slice);
    }

    public int getTupleLength() {
      return mipmappedTupleData.size();
    }

    public void validate() {
      for (int i = 0; i < mipmappedTupleData.size(); i++) {
        ArgChecker.isNotNull(mipmappedTupleData.get(i), "mipmappedTupleData.get(" + i + ")");
      }

      // Verify that multiDomain and multiRange have same number
      // of elements at each level
      Array2D prevSlice = mipmappedTupleData.get(0);
      for (int i = 1; i < mipmappedTupleData.size(); i++) {
        Array2D mipmappedTupleSlice = mipmappedTupleData.get(i);
        if (!mipmappedTupleSlice.isSameSize(prevSlice)) {
          throw new IllegalArgumentException(
              "i=" + i + ": domain and range mipmaps differ in size");
        }
        prevSlice = mipmappedTupleSlice;
      }
    }
  }

  private String axisId, identifier, label;
  private MipMapStrategy defaultMipMapStrategy = BinaryMipMapStrategy.MEAN;
  private double rangeBottom = Double.NaN, rangeTop = Double.NaN;
  private String preferredRenderer;
  
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
  
  /**
   * See {@link Dataset#getPreferredRenderer()}.
   */
  @Deprecated
  public String getPreferredRenderer() {
    return this.preferredRenderer;
  }
  
  public double getRangeBottom() {
    return rangeBottom;
  }

  public double getRangeTop() {
    return rangeTop;
  }
  
  /**
   * Returns the number of elements that each tuple is capable of storing.
   */
  public abstract int getTupleLength();
  
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
  
  @Deprecated
  public void setPreferredRenderer(String preferredRenderer) {
    this.preferredRenderer = preferredRenderer;
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
  }

}
