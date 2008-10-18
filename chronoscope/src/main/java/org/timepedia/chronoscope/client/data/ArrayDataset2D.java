package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.data.tuple.BasicTuple2D;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Interval;

/**
 * {@link Dataset} composed of {@link Tuple2D} data points and backed
 * by {@link Array2D} objects.
 * 
 * @author Chad Takahashi
 */
public class ArrayDataset2D extends AbstractDataset<Tuple2D> {

  /*
   * Stores the multiresolution domain and range values.
   */
  protected Array2D multiDomain, multiRange;
  
  private BasicTuple2D reusableTuple = new BasicTuple2D(0, 0);
  
  /**
   * Constructs an {@link Dataset} from the specified request object.
   */
  public ArrayDataset2D(DatasetRequest request) {
    ArgChecker.isNotNull(request, "request");
    request.validate();
    axisId = (String) ArgChecker.isNotNull(request.getAxisId(), "axisId");
    rangeLabel = (String) ArgChecker.isNotNull(request.getLabel(), "label");
    identifier = request.getIdentifier();

    if (request instanceof DatasetRequest.MultiRes) {
      // multiDomain and multiRange explicitly specified in request object.
      DatasetRequest.MultiRes multiResReq = (DatasetRequest.MultiRes) request;
      multiDomain = multiResReq.getMultiDomain();
      multiRange = multiResReq.getMultiRange();
    } else if (request instanceof DatasetRequest.Basic) {
      // Use MipMapStrategy to calculate multiDomain and MultiRange from
      // the domain[] and range[] specified in the basic request.
      DatasetRequest.Basic basicReq = (DatasetRequest.Basic) request;
      MipMapStrategy mms = basicReq.getDefaultMipMapStrategy();
      multiDomain = mms.calcMultiDomain(basicReq.getDomain());
      multiRange = mms.calcMultiRange(basicReq.getRange());
    }
    else {
      throw new RuntimeException("Unsupported request type: " 
          + request.getClass().getName());
    }
    validate(multiDomain, multiRange);

    if (Double.isNaN(request.getApproximateMinimumInterval())) {
      approximateMinimumInterval = calcMinInterval(multiDomain);
    } else {
      approximateMinimumInterval = request.getApproximateMinimumInterval();
    }

    // Assign rangeBottom and rangeTop
    final int numLevels = multiDomain.numRows();
    maxRange = request.getRangeTop();
    minRange = request.getRangeBottom();
    if (Double.isNaN(maxRange) || Double.isNaN(minRange)) {
      // Question: Will the max range at mip level 1 or greater ever be greater
      // than the max range at mip level 0? If not, then can we just find
      // min/max values at level 0?
      Interval rangeInterval = calcRangeInterval(multiRange, numLevels);
      minRange = rangeInterval.getStart();
      maxRange = rangeInterval.getEnd();
    }
  }

  public double getApproximateMinimumInterval() {
    return approximateMinimumInterval;
  }

  public Tuple2D getFlyweightTuple(int index) {
    return getFlyweightTuple(index, 0);
  }

  public Tuple2D getFlyweightTuple(int index, int mipLevel) {
    double yValue = multiRange.get(mipLevel, index);
    reusableTuple.setCoordinates(getX(index, mipLevel), yValue);
    return reusableTuple;
  }

  public double getMaxValue(int coordinate) {
    switch (coordinate) {
      case 0:
        return multiDomain.get(0, getNumSamples() - 1);
      case 1:
        return this.maxRange;
      default:
        throw new IllegalArgumentException("coordinate out of range: " + coordinate);
    }
  }
  
  public double getMinValue(int coordinate) {
    switch (coordinate) {
      case 0:
        return multiDomain.get(0, 0);
      case 1:
        return this.minRange;
      default:
        throw new IllegalArgumentException("coordinate out of range: " + coordinate);
    }
  }

  public int getNumSamples(int mipLevel) {
    return multiDomain.numColumns(mipLevel);
  }

  public double getX(int index, int mipLevel) {
    return multiDomain.get(mipLevel, index);
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

}
