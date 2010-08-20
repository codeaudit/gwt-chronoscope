package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;

/**
 * Returned as the result of {@link Dataset#getBestMipMapForInterval} containing the
 * resulting MipMap, start, and end index.
 */
public class MipMapRegion {

  private MipMap mipMap;

  private int startIndex, endIndex;

  public MipMapRegion(MipMap mipMap, int startIndex, int endIndex) {
    this.endIndex = endIndex;
    this.mipMap = mipMap;
    this.startIndex = startIndex;
  }

  public int getEndIndex() {

    return endIndex;
  }

  public MipMap getMipMap() {
    return mipMap;
  }

  public int getStartIndex() {
    return startIndex;
  }
}
