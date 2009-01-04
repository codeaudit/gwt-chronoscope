package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.util.Array1D;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.Util;

/**
 * Provides skeletal implementation of an {@link Dataset} to simplify
 * implementation of a concrete class.
 *
 * @author Chad Takahashi
 */
public abstract class AbstractDataset<T extends Tuple2D> implements Dataset<T> {

  protected double minDomainInterval;

  protected String identifier, rangeLabel;

  protected String preferredRenderer;

  protected Interval preferredRangeAxisInterval;

  private Interval domainExtrema = new Interval(0.0, 0.0);

  public final Interval getDomainExtrema() {
    this.domainExtrema.setEndpoints(getX(0), getX(getNumSamples() - 1));
    return this.domainExtrema;
  }

  public final String getIdentifier() {
    return identifier;
  }

  public final double getMinDomainInterval() {
    return minDomainInterval;
  }

  public MipMapRegion getLowestMipMapForInterval(Interval region, int maxSamples) {
    int domainStartIdx = 0;
    int domainEndIdx = 0;
    MipMapChain mipMapChain = getMipMapChain();
    MipMap bestMipMap = mipMapChain.getMipMap(0);
    while (true) {
      Array1D domain = bestMipMap.getDomain();
      domainStartIdx = Util.binarySearch(domain, region.getStart());
      domainEndIdx = Util.binarySearch(domain, region.getEnd());
      if ((domainEndIdx - domainStartIdx) <= maxSamples) {
        break;
      }
      bestMipMap = bestMipMap.next();
    }
    return new MipMapRegion(bestMipMap, domainStartIdx, domainEndIdx);
  }

  public final Interval getPreferredRangeAxisInterval() {
    return preferredRangeAxisInterval;
  }

  public final String getPreferredRenderer() {
    return preferredRenderer;
  }

  public final String getRangeLabel() {
    return rangeLabel;
  }

}
