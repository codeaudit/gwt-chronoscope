package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Array1D;
import org.timepedia.chronoscope.client.util.Array2D;
import org.timepedia.chronoscope.client.util.ExtremaArrayFunction;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.MinIntervalArrayFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides most of the implementation necessary for an N-tuple dataset backed
 * by {@link Array2D} objects.
 *  
 * @author Chad Takahashi
 */
public abstract class AbstractArrayDataset<T extends Tuple2D> extends AbstractDataset<T> {
  
  protected MipMapChain mipMapChain;
  
  /**
   * Stores the min/max range values for each tuple coordinate in {@link #mmRangeTuple}.
   */
  protected Interval[] rangeIntervals;
  
  /**
   * Mip level 0 of the {@link #mipMapChain}.
   */
  private MipMap rawData;
  
  /**
   * Constructs an {@link Dataset} from the specified request object.
   */
  public AbstractArrayDataset(DatasetRequest request) {
    ArgChecker.isNotNull(request, "request");
    request.validate();
    axisId = (String) ArgChecker.isNotNull(request.getAxisId(), "axisId");
    rangeLabel = (String) ArgChecker.isNotNull(request.getRangeLabel(), "label");
    identifier = request.getIdentifier();
    preferredRenderer = request.getPreferredRenderer();
    
    mipMapChain = loadTupleData(request);
    rawData = this.mipMapChain.getMipMap(0);
    
    // TODO: implement validate()
    //mipMapChain.validate(); 
    
    MinIntervalArrayFunction minIntervalFn = new MinIntervalArrayFunction();
    rawData.getDomain().execFunction(minIntervalFn);
    minDomainInterval = minIntervalFn.getMinInterval();

    // Assign min/max range-Y values
    rangeIntervals = new Interval[mipMapChain.getRangeTupleSize()];
    ExtremaArrayFunction extremaFn = new ExtremaArrayFunction();
    for (int i = 0; i < rangeIntervals.length; i++) {
      Array1D rangeVals = rawData.getRange(i);
      rangeVals.execFunction(extremaFn);
      rangeIntervals[i] = extremaFn.getExtrema();
    }

    preferredRangeAxisInterval = request.getPreferredRangeAxisInterval();
  }

  public final T getFlyweightTuple(int index) {
    return (T)rawData.getTuple(index);
  }

  public Interval getRangeExtrema(int tupleCoordinate) {
    return rangeIntervals[tupleCoordinate].copy();
  }
  
  public MipMapChain getMipMapChain() {
    return this.mipMapChain;
  }
  
  public int getNumSamples() {
    return this.rawData.size();
  }
  
  public int getTupleLength() {
    return 1 + this.mipMapChain.getRangeTupleSize();
  }

  public double getX(int index) {
    return this.rawData.getDomain().get(index);
  }

  /**
   * Calculates or extracts the mipmapped domain and range datastructures from
   * the specified dataset request creates a {@link MipMapChain}.
   */
  private MipMapChain loadTupleData(DatasetRequest datasetReq) {
    final int rangeTupleLength = datasetReq.getTupleLength() - 1;
    
    MipMapChain mipMapChain = null;
    
    if (datasetReq instanceof DatasetRequest.MultiRes) {
      // multiDomain and multiRange explicitly specified in request object.
      DatasetRequest.MultiRes multiResReq = (DatasetRequest.MultiRes) datasetReq;
      Array2D mipMappedDomain = multiResReq.getMultiresTupleSlice(0);
      List<Array2D> mipMappedRangeTuples = multiResReq.getMultiResTuples();
      mipMapChain = new MipMapChain(mipMappedDomain, mipMappedRangeTuples);
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
      
      mipMapChain = mms.mipmap(domain, tupleRange);
    }
    else {
      throw new RuntimeException("Unsupported request type: " 
          + datasetReq.getClass().getName());
    }
    
    return mipMapChain;
  }
  
}
