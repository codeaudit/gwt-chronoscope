package org.timepedia.chronoscope.client.data;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.util.*;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.NoExport;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides skeletal implementation of an {@link Dataset} to simplify
 * implementation of a concrete class.
 *
 * @author Chad Takahashi
 */
@ExportPackage("chronoscope")
public abstract class AbstractDataset<T extends Tuple2D>
    implements Dataset<T>, Exportable {

  protected double minDomainInterval;

  protected String identifier, rangeLabel;

  protected String preferredRenderer;

  protected Interval preferredRangeAxisInterval;

  private Interval domainExtrema = new Interval(0.0, 0.0);

  private Map<String, Object> userData = new HashMap<String, Object>();

  private IncrementalHandler incrementalHandler;

  private Datasets<T> datasets;

  private long outgoingRequest  =  -1;

  private boolean firing;

  MipMap incremental;
  Interval incrementalInterval;

  @Export
  public final Interval getDomainExtrema() {
    this.domainExtrema.setEndpoints(getX(0), getX(getNumSamples() - 1));
    return this.domainExtrema;
  }

  @Export
  public final String getIdentifier() {
    return identifier;
  }

  @Export
  public final void setIdentifier(String id) {
    this.identifier = id;
  }


  @Export
  public final double getMinDomainInterval() {
    return minDomainInterval;
  }

  void setIncrementalData(JsArrayNumber domain, JsArray<JsArrayNumber> rangeArray) {
    int len = domain.length();  // using this length for both 
    double d[] = new double[len];
    for(int i=0; i<d.length; i++) {
      d[i] = domain.get(i);
    }
    int rangeArrayLength = rangeArray.length();
    Array1D[] ranges = new Array1DImpl[rangeArray.length()];
    for (int dimension = 0; dimension < rangeArrayLength; dimension++) {
        double[] r = new double[len];
        for(int j=0; j < r.length; j++) {
            r[j] = rangeArray.get(dimension).get(j);
        }
        ranges[dimension] = new Array1DImpl(r);
    }

    incremental = new MipMap(new Array1DImpl(d), ranges, -1, getMipMapChain().getMipMap(0));

    Interval region = new Interval(d[0], d[d.length - 1]);
    incrementalInterval = region;
    outgoingRequest = System.currentTimeMillis();
    firing = true;
    datasets.fireChanged(this, region);
    firing = false;
  }
  
  private static class Array1DImpl implements Array1D {

    private double[] backingArray;

    public Array1DImpl(double[] backingArray) {
      this.backingArray = backingArray;
    }

    @Override
    public double[] backingArray() {
      return backingArray;
    }

    @Override
    public double get(int index) {
      return backingArray[index];
    }

    @Override
    public double getLast() {
      return backingArray[backingArray.length-1];
    }

    @Override
    public int size() {
      return backingArray.length;
    }

    @Override
    public boolean isEmpty() {
      return backingArray.length > 0;
    }

    @Override
    public void execFunction(ArrayFunction f) {
    }

    @Override
    public double[] toArray() {
      return backingArray();
    }

    @Override
    public void clear() {
      backingArray = null;
    }
  }

  public MipMapRegion getBestMipMapForInterval(Interval region, int maxSamples, int lodBias) {
    int domainStartIdx = 0;
    int domainEndIdx = 0;
    if (lodBias == 0 && incrementalHandler != null && incremental != null && incrementalInterval != null
            && incrementalInterval.contains(region.getStart())
            && incrementalInterval.contains(region.getEnd())) {
      domainStartIdx = Util.binarySearch(incremental.getDomain(), region.getStart());
      domainEndIdx = Util.binarySearch(incremental.getDomain(), region.getEnd());
      return new MipMapRegion(incremental, domainStartIdx, domainEndIdx);
    }
    if (!firing && lodBias == 0 && incrementalHandler != null
            && (outgoingRequest < 0 || System.currentTimeMillis() - outgoingRequest > 1000 )) {
      // widening region for redraw issue when slighlty under needed span
       double delta = region.getEnd() - region.getStart();
       double epsilon = delta * 0.01;
       Interval datasetRegion = new Interval(Math.max(this.domainExtrema.getStart(), region.getStart()-epsilon),
                           Math.min(this.domainExtrema.getEnd(), region.getEnd()+epsilon));
      incrementalHandler.onDataNeeded(datasetRegion, this, new IncrementalDatasetResponseImpl(this));
      outgoingRequest = System.currentTimeMillis();
    }
    if (lodBias < 0) { lodBias = 0; }
    
    MipMapChain mipMapChain = getMipMapChain();
    MipMap bestMipMap = mipMapChain.getMipMap(0);
    MipMap prevBestMipMap = bestMipMap;
    int prevStartIdx, prevEndIdx;
    domainEndIdx = bestMipMap.size() - 1;
    while (true) {
      Array1D domain = bestMipMap.getDomain();
      prevStartIdx = domainStartIdx;
      prevEndIdx = domainEndIdx;
      
      domainStartIdx = Util.binarySearch(domain, region.getStart());
      domainEndIdx = Util.binarySearch(domain, region.getEnd());
      if(domainEndIdx - domainStartIdx < 2) {
        return new MipMapRegion(prevBestMipMap, prevStartIdx, prevEndIdx);
      }
      if ((domainEndIdx - domainStartIdx) <= maxSamples) {
        if (lodBias == 0) {
          break;
        } else {
          lodBias--;
        }
      } else {
        lodBias = 0;
      }
      prevBestMipMap = bestMipMap;
      bestMipMap = bestMipMap.next();
    }
    return new MipMapRegion(bestMipMap, domainStartIdx, domainEndIdx);
  }


  @Export
  public final Interval getPreferredRangeAxisInterval() {
    return preferredRangeAxisInterval;
  }

  public final String getPreferredRenderer() {
    return preferredRenderer;
  }

  @Export
  public final String getRangeLabel() {
    return rangeLabel;
  }

  @NoExport
  public <T> T getUserData(String key) {
    return (T) userData.get(key);
  }

  @NoExport
  public void setUserData(String key, Object val) {
    userData.put(key, val);
  }

  @Override
  @Export
  public void setIncrementalHandler(IncrementalHandler handler) {
    incrementalHandler = handler;
  }

  @Export
  public void setDatasets(Datasets<T> datasets) {
    this.datasets = datasets;
  }

  public void setIncremental(MipMap incremental) {
      this.incremental = incremental;
  }

  public void setIncrementalInterval(Interval incrementalInterval) {
      this.incrementalInterval = incrementalInterval;
  }
}
