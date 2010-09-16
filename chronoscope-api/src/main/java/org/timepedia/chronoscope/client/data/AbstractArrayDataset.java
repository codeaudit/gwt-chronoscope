package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Array1D;
import org.timepedia.chronoscope.client.util.Array2D;
import org.timepedia.chronoscope.client.util.ExtremaArrayFunction;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.MinIntervalArrayFunction;
import org.timepedia.chronoscope.client.util.Util;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.Export;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides most of the implementation necessary for an N-tuple dataset backed
 * by {@link Array2D} objects.
 *
 * @author Chad Takahashi
 */
@ExportPackage("chronoscope")
public abstract class AbstractArrayDataset<T extends Tuple2D>
    extends AbstractDataset<T> implements Exportable {

  protected MipMapChain mipMapChain;

  /**
   * Stores the min/max range values for each tuple coordinate in 
   * {@link #rawData}.
   */
  protected Interval[] rangeIntervals;

  /**
   * Stores the axis IDs for each range tuple coordinate
   */
  private String[] axisIds;

  /**
   * Mip level 0 of the {@link #mipMapChain}.
   */
  private MipMap rawData;

  public String toString() {
    String ret = "";
    ret += "\n label:" + rangeLabel;
    ret += "\n axisIds:" + Util.arrayToString(axisIds);
    ret += "\n rawData: " + rawData.toString();
    ret += "\n mipMapChain: " + mipMapChain.toString();
    if (preferredRangeAxisInterval != null) 
      ret += "\n preferredRange: " + preferredRangeAxisInterval.getStart() + "," + preferredRangeAxisInterval.getEnd();
    ret += "\ndomainExtrema: " + getDomainExtrema().getStart() + "," + getDomainExtrema().getEnd();
    ret += "\n mipMapChain: " + mipMapChain.toString();
    return ret;
  }
  
  public String toJson() {
    String ret = "{\nid:'" + identifier + "'\n,label: '" + rangeLabel;
    ret += "',\naxis: '" + axisIds[0];
    ret += "',\nmipped: true,\n";
    String domains = "";
    String ranges = "";
    for (int i = 0; i < mipMapChain.size(); i++) {
      if (i>0) {
        domains += ",\n";
        ranges += ",\n";
      }
      MipMap m = mipMapChain.getMipMap(i);
      domains += Util.arrayToString(m.getDomain().toArray());
      ranges += Util.arrayToString(m.getRange(0).toArray());

    }
    ret += "domain: [" + domains + "],\nrange: [" + ranges + "],\n";
    Interval i = preferredRangeAxisInterval != null ? preferredRangeAxisInterval : getDomainExtrema();
    ret += "rangeTop: " + i.getEnd() +  ",\nrangeBottom: " + i.getStart() + "\n}";
    return ret;
  }
  
  /**
   * Constructs an {@link Dataset} from the specified request object.
   */
  public AbstractArrayDataset(DatasetRequest request) {
    ArgChecker.isNotNull(request, "request");
    request.validate();
    rangeLabel = (String) ArgChecker
        .isNotNull(request.getRangeLabel(), "label");
    identifier = request.getIdentifier();
    preferredRenderer = request.getPreferredRenderer();
    loadDataset(request);
    preferredRangeAxisInterval = request.getPreferredRangeAxisInterval();
  }

  protected void loadDataset(DatasetRequest request) {
    mipMapChain = loadTupleData(request);
    computeIntervals(request);
  }

  protected void computeIntervals(DatasetRequest request) {
    rawData = this.mipMapChain.getMipMap(0);

    axisIds = new String[mipMapChain.getRangeTupleSize()];
    axisIds[0] = (String) ArgChecker.isNotNull(request.getAxisId(), "axisId");

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
  }

  @Export
  public final String getAxisId(int rangeTupleCoordinate) {
    if (rangeTupleCoordinate > 0) {
      throw new UnsupportedOperationException(
          "rangeTupleCoordinate values > 0 not supported yet");
    }
    return axisIds[rangeTupleCoordinate];
  }

  public final T getFlyweightTuple(int index) {
    return (T) rawData.getTuple(index);
  }

  @Export
  public Interval getRangeExtrema(int tupleCoordinate) {
    return rangeIntervals[tupleCoordinate].copy();
  }

  public MipMapChain getMipMapChain() {
    return this.mipMapChain;
  }

  @Export
  public int getNumSamples() {
    return this.rawData.size();
  }

  public int getTupleLength() {
    return 1 + this.mipMapChain.getRangeTupleSize();
  }

  @Export
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
      Array2D mipMappedDomain = multiResReq.getMultiresDomain();
      List<Array2D> mipMappedRangeTuples = multiResReq.getMultiResRangeTuples();
      mipMapChain = createMipMapChain(mipMappedDomain, mipMappedRangeTuples);
    } else if (datasetReq instanceof DatasetRequest.Basic) {
      // Use MipMapStrategy to calculate multiDomain and MultiRange from
      // the domain[] and range[] specified in the basic request.
      DatasetRequest.Basic basicReq = (DatasetRequest.Basic) datasetReq;
      MipMapStrategy mms = basicReq.getDefaultMipMapStrategy();

      double[] domain = basicReq.getDomain();

      List<double[]> rangeTuples = new ArrayList<double[]>();
      for (int i = 0; i < rangeTupleLength; i++) {
        rangeTuples.add(basicReq.getRangeTupleSlice(i));
      }

      mipMapChain = mms.mipmap(domain, rangeTuples);
    } else {
      throw new RuntimeException(
          "Unsupported request type: " + datasetReq.getClass().getName());
    }

    return mipMapChain;
  }

  protected MipMapChain createMipMapChain(Array2D mipMappedDomain,
      List<Array2D> mipMappedRangeTuples) {
    return new MipMapChain(mipMappedDomain, mipMappedRangeTuples);
  }
  

}
