package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.util.ArgChecker;

/**
 * {@link XYDataset} backed by {@link Array2D} objects.
 * 
 * @author Chad Takahashi
 */
public class ArrayXYDataset implements XYDataset {

  protected Array2D multiDomain, multiRange;
  
  protected double rangeBottom, rangeTop;

  protected double approximateMinimumInterval;

  private String axisId;

  private String identifier;

  private String rangeLabel;

  /**
   * Constructs an {@link XYDataset} from the specified request object.
   */
  public ArrayXYDataset(XYDatasetRequest request) {
    ArgChecker.isNotNull(request, "request");
    request.validate();
    axisId = (String) ArgChecker.isNotNull(request.getAxisId(), "axisId");
    rangeLabel = (String) ArgChecker.isNotNull(request.getLabel(), "label");
    identifier = request.getIdentifier();

    if (request instanceof XYDatasetRequest.MultiRes) {
      // multiDomain and multiRange explicitly specified in request object.
      XYDatasetRequest.MultiRes multiResReq = (XYDatasetRequest.MultiRes) request;
      multiDomain = multiResReq.getMultiDomain();
      multiRange = multiResReq.getMultiRange();
    } else if (request instanceof XYDatasetRequest.Basic) {
      // Use MipMapStrategy to calculate multiDomain and MultiRange from
      // the domain[] and range[] specified in the basic request.
      XYDatasetRequest.Basic basicReq = (XYDatasetRequest.Basic) request;
      MipMapStrategy mms = basicReq.getDefaultMipMapStrategy();
      multiDomain = mms.calcMultiDomain(basicReq.getDomain());
      multiRange = mms.calcMultiRange(basicReq.getRange());
    }
    else {
      throw new RuntimeException("Unsupported request type: " 
          + request.getClass().getName());
    }
    validate(multiDomain, multiRange);

    // Assign approximateMinimumInterval
    if (Double.isNaN(request.getApproximateMinimumInterval())) {
      approximateMinimumInterval = calcMinInterval(multiDomain);
      //approximateMinimumInterval = (getDomainEnd() - getDomainBegin())
      //    / getNumSamples();
    } else {
      approximateMinimumInterval = request.getApproximateMinimumInterval();
    }

    // Assign rangeBottom and rangeTop
    final int numLevels = multiDomain.numRows();
    rangeTop = request.getRangeTop();
    rangeBottom = request.getRangeBottom();
    if (Double.isNaN(rangeTop) || Double.isNaN(rangeBottom)) {
      // Question: Will the max range at mip level 1 or greater ever be greater
      // than the max range at mip level 0? If not, then can we just find
      // min/max values at level 0?
      Interval rangeInterval = calcRangeInterval(multiRange, numLevels);
      rangeBottom = rangeInterval.low;
      rangeTop = rangeInterval.high;
    }
  }

  public double getApproximateMinimumInterval() {
    return approximateMinimumInterval;
  }

  public String getAxisId() {
    return axisId;
  }

  public double getDomainBegin() {
    return getX(0);
  }

  public double getDomainEnd() {
    return getX(getNumSamples() - 1);
  }

  public String getIdentifier() {
    return identifier;
  }

  public int getNumSamples() {
    return multiDomain.numColumns(0);
  }

  public int getNumSamples(int mipLevel) {
    return multiDomain.numColumns(mipLevel);
  }

  public double getRangeBottom() {
    return rangeBottom;
  }

  public String getRangeLabel() {
    return rangeLabel;
  }

  public double getRangeTop() {
    return rangeTop;
  }

  public double getX(int index) {
    return multiDomain.get(0, index);
  }

  public double getX(int index, int mipLevel) {
    return multiDomain.get(mipLevel, index);
  }

  public double getY(int index) {
    return multiRange.get(0, index);
  }

  public double getY(int index, int mipLevel) {
    return multiRange.get(mipLevel, index);
  }

  /**
   * Calculates the bottom and top of the range values in the specified dataset.
   */
  private Interval calcRangeInterval(Array2D multiRange, int numLevels) {
    // Calculate min and max range values across all resolutions
    double lo = Double.POSITIVE_INFINITY;
    double hi = Double.NEGATIVE_INFINITY;

    for (int i = 0; i < numLevels; i++) {
      for (int j = 0; j < multiRange.numColumns(i); j++) {
        double value = multiRange.get(i, j);
        lo = Math.min(lo, value);
        hi = Math.max(hi, value);
      }
    }

    return new Interval(lo, hi);
  }

  /**
   * Validates multiDomain and multiRange objects.
   */
  private static void validate(Array2D multiDomain, Array2D multiRange) {
    ArgChecker.isNotNull(multiDomain, "multiDomain");
    ArgChecker.isNotNull(multiRange, "multiRange");
    if (!multiDomain.isSameSize(multiRange)) {
      throw new IllegalArgumentException(
          "multiDomain and multiRange differ in size");
    }
  }

  /**
   * Returns the smallest domain interval at row 0 in the specified Array2D object.
   * If only 1 column exists at row 0, then 0 is returned as the minimum interval.
   */
  private static double calcMinInterval(Array2D a) {
    double min = Double.MAX_VALUE;
    final int numColumns = a.numColumns(0);
    
    if (numColumns < 2) {
      // An interval requires at least 2 points, so in this case, just return 0.
      min = 0.0;
      //throw new RuntimeException("Array2D must have at least 2 columns at MIP level 0: " + numColumns);
    }
    else {
      double prevValue = a.get(0, 0);
      for (int i = 1; i < numColumns; i++) {
        double currValue = a.get(0, i);
        min = Math.min(min, currValue - prevValue);
        prevValue = currValue;
      }
    }
    
    return min;
  }

  /**
   * C-style struct that represents a range of values from <tt>low</tt> to
   * <tt>high</tt>.
   */
  private static final class Interval {
    public Interval(double low, double high) {
      this.low = low;
      this.high = high;
    }

    public String toString() {
      return "[" + low + ", " + high + "]";
    }

    public double low;
    public double high;
  }
  
}
