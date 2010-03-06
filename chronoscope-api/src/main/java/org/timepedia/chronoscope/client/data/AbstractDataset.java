package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.util.Array1D;
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

  public MipMapRegion getBestMipMapForInterval(Interval region, int maxSamples,
      int lodBias) {
    int domainStartIdx = 0;
    int domainEndIdx = 0;
    MipMapChain mipMapChain = getMipMapChain();
    MipMap bestMipMap = mipMapChain.getMipMap(0);
    while (true) {
      Array1D domain = bestMipMap.getDomain();
      domainStartIdx = Util.binarySearch(domain, region.getStart());
      domainEndIdx = Util.binarySearch(domain, region.getEnd());
      if ((domainEndIdx - domainStartIdx) <= maxSamples) {
        if (lodBias == 0) {
          break;
        } else {
          lodBias--;
        }
      }
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
}
