package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Array2D;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides most of the implementation necessary for an N-tuple dataset backed
 * by {@link Array2D} objects.
 *  
 * @author Chad Takahashi
 */
public abstract class AbstractArrayDataset<T extends Tuple2D> extends AbstractDataset<T> {
  
  /**
   * Stores the mipmapped data for the domain.
   */
  protected Array2D mmDomain;
  
  /**
   * Stores the mipmapped data for each tuple coordinate in the range.
   */
  protected Array2D[] mmRangeTuple;

  protected FlyweightTuple flyweightTuple;

  protected double minRange, maxRange;

  /**
   * Constructs an {@link Dataset} from the specified request object.
   */
  public AbstractArrayDataset(DatasetRequest request) {
    ArgChecker.isNotNull(request, "request");
    request.validate();
    axisId = (String) ArgChecker.isNotNull(request.getAxisId(), "axisId");
    rangeLabel = (String) ArgChecker.isNotNull(request.getLabel(), "label");
    identifier = request.getIdentifier();
    preferredRenderer = request.getPreferredRenderer();
    
    loadTupleData(request);
    validate(mmDomain, mmRangeTuple);

    minInterval = calcMinDomainInterval(mmDomain);

    // Assign rangeBottom and rangeTop
    final int numLevels = mmDomain.numRows();
    minRange = request.getRangeBottom();
    maxRange = request.getRangeTop();
    if (Double.isNaN(maxRange) || Double.isNaN(minRange)) {
      // Question: Will the max range at mip level 1 or greater ever be greater
      // than the max range at mip level 0? If not, then can we just find
      // min/max values at level 0?
      Interval rangeInterval = calcRangeInterval(mmRangeTuple[0], numLevels);
      minRange = rangeInterval.getStart();
      maxRange = rangeInterval.getEnd();
    }
    
    this.flyweightTuple = new FlyweightTuple(this.mmDomain, this.mmRangeTuple);
  }

  public final T getFlyweightTuple(int index) {
    return getFlyweightTuple(index, 0);
  }

  public abstract T getFlyweightTuple(int index, int mipLevel);

  public double getMaxValue(int coordinate) {
    switch (coordinate) {
      case 0:
        return mmDomain.get(0, getNumSamples() - 1);
      case 1:
        return this.maxRange;
      default:
        throw new IllegalArgumentException("coordinate out of range: " + coordinate);
    }
  }
  
  public double getMinValue(int coordinate) {
    switch (coordinate) {
      case 0:
        return mmDomain.get(0, 0);
      case 1:
        return this.minRange;
      default:
        throw new IllegalArgumentException("coordinate out of range: " + coordinate);
    }
  }

  public int getNumSamples(int mipLevel) {
    return mmDomain.numColumns(mipLevel);
  }
  
  public int getTupleLength() {
    return 1 + mmRangeTuple.length;
  }

  public double getX(int index, int mipLevel) {
    return mmDomain.get(mipLevel, index);
  }

  /**
   * Calculates the bottom and top of the range values in the specified dataset.
   */
  private Interval calcRangeInterval(Array2D rangeMipmap, int numLevels) {
    // Calculate min and max range values across all mip levels
    double lo = Double.POSITIVE_INFINITY;
    double hi = Double.NEGATIVE_INFINITY;

    for (int i = 0; i < numLevels; i++) {
      for (int j = 0; j < rangeMipmap.numColumns(i); j++) {
        double value = rangeMipmap.get(i, j);
        lo = MathUtil.min(lo, value);
        hi = MathUtil.max(hi, value);
      }
    }

    return new Interval(lo, hi);
  }

  /**
   * Calculates or extracts the mipmapped domain and range datastructures from
   * the specified dataset request and assigns them to {@link #mmDomain}
   * and {@link #mmRangeTuple}.
   * 
   * @param datasetReq
   */
  private void loadTupleData(DatasetRequest datasetReq) {
    final int rangeTupleLength = datasetReq.getTupleLength() - 1;
    
    mmRangeTuple = new Array2D[rangeTupleLength];
    
    if (datasetReq instanceof DatasetRequest.MultiRes) {
      // multiDomain and multiRange explicitly specified in request object.
      DatasetRequest.MultiRes multiResReq = (DatasetRequest.MultiRes) datasetReq;
      mmDomain = multiResReq.getMultiresTupleSlice(0);
      for (int i = 0; i < rangeTupleLength; i++) {
        mmRangeTuple[i] = multiResReq.getMultiresTupleSlice(i + 1);
      }
    } 
    else if (datasetReq instanceof DatasetRequest.Basic) {
      // Use MipMapStrategy to calculate multiDomain and MultiRange from
      // the domain[] and range[] specified in the basic request.
      DatasetRequest.Basic basicReq = (DatasetRequest.Basic) datasetReq;
      MipMapStrategy mms = basicReq.getDefaultMipMapStrategy();
      
      double[] domain = basicReq.getTupleSlice(0);
      
      List<double[]> tupleRange = new ArrayList<double[]>();
      for (int i = 0; i < rangeTupleLength; i++) {
        tupleRange.add(basicReq.getTupleSlice(i + 1));
      }
      
      MipMapResult mipmapResult = mms.mipmap(domain, tupleRange);
      mmDomain = mipmapResult.domain;
      for (int i = 0; i < rangeTupleLength; i++) {
        mmRangeTuple[i] = mipmapResult.tupleRange.get(i);
      }
    }
    else {
      throw new RuntimeException("Unsupported request type: " 
          + datasetReq.getClass().getName());
    }
  }
  
  /**
   * Validates mipmapped domain and range objects
   */
  private static void validate(Array2D mipmappedDomain, Array2D[] mipmappedRange) {
    ArgChecker.isNotNull(mipmappedDomain, "mipmappedDomain");
    ArgChecker.isNotNull(mipmappedRange, "mipmappedRange");
    
    for (int i = 0; i < mipmappedRange.length; i++) {
      if (!mipmappedDomain.isSameSize(mipmappedRange[i])) {
        throw new IllegalArgumentException("mipmappedrange[" + i + "] " +
            "not same size as domain");
      }
    }
  }

  /**
   * Returns the smallest domain interval at row 0 in the specified Array2D object.
   * If only 1 column exists at row 0, then 0 is returned as the minimum interval.
   * 
   * @param mmDomain the mipmapped domain data
   */
  private static double calcMinDomainInterval(Array2D mmDomain) {
    double min = Double.MAX_VALUE;
    final int numColumns = mmDomain.numColumns(0);
    
    if (numColumns < 2) {
      // An interval requires at least 2 points, so in this case, just return 0.
      min = 0.0;
    }
    else {
      double prevValue = mmDomain.get(0, 0);
      for (int i = 1; i < numColumns; i++) {
        double currValue = mmDomain.get(0, i);
        min = Math.min(min, currValue - prevValue);
        prevValue = currValue;
      }
    }
    
    return min;
  }

}
