package org.timepedia.chronoscope.client.data;

import com.google.gwt.core.client.JsArrayNumber;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.util.Array1D;
import org.timepedia.chronoscope.client.util.ArrayFunction;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.Util;
import org.timepedia.exporter.client.Export;
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
  public final double getMinDomainInterval() {
    return minDomainInterval;
  }

  void setIncrementalData(JsArrayNumber domain, JsArrayNumber range) {
    double d[] = new double[domain.length()];
    for(int i=0; i<d.length; i++) {
      d[i] = domain.get(i);
    }
    double r[] = new double[range.length()];
    for(int i=0; i<r.length; i++) {
      r[i] = range.get(i);
    }
    this.incremental = new MipMap(new Array1DImpl(d), new Array1D[] { new Array1DImpl(r) }, -1, getMipMapChain().getMipMap(0));
    Interval region = new Interval(d[0], d[d.length - 1]);
    incrementalInterval = region;
    outgoingRequest = -1;
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
      //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double[] toArray() {
      return backingArray();
    }
  }
  
  public MipMapRegion getBestMipMapForInterval(Interval region, int maxSamples,
      int lodBias) {
    int domainStartIdx = 0;
    int domainEndIdx = 0;
    if (lodBias == 0 && incrementalHandler != null && incremental != null && incrementalInterval != null && incrementalInterval.contains(region.getStart()) && 
        incrementalInterval.contains(region.getEnd())) {
      domainStartIdx = Util.binarySearch(incremental.getDomain(), region.getStart());
      domainEndIdx = Util.binarySearch(incremental.getDomain(), region.getEnd());
      return new MipMapRegion(incremental, domainStartIdx, domainEndIdx);
    }
    if (!firing && lodBias == 0 && incrementalHandler != null && (outgoingRequest < 0 || System.currentTimeMillis() - outgoingRequest > 5000 )) {
      incrementalHandler.onDataNeeded(region, this, new IncrementalDatasetResponseImpl(this));
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
      }
      prevBestMipMap = bestMipMap;
      bestMipMap = bestMipMap.next();
    }
    return new MipMapRegion(bestMipMap, domainStartIdx, domainEndIdx);
  }

  MipMap incremental;
  Interval incrementalInterval;
  
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

  public void setDatasets(Datasets<T> datasets) {
    this.datasets = datasets;
  }
}
