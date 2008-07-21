package org.timepedia.chronoscope.client.util;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlot;

/**
 * Conglomeration of common utility functions.
 */
public final class Util {

  /**
   * Analagous to <tt>java.lang.System.arraycopy()</tt>.
   */
  public static void arraycopy(double[] src, int srcOffset, double[] dest,
      int destOffset, int length) {
    for (int i = 0; i < length; i++) {
      dest[destOffset + i] = src[srcOffset + i];
    }
  }
  
  public static int binarySearch(XYDataset mds, double domainOrigin,
      int mipLevel) {
    int low = 0;
    int high = mds.getNumSamples(mipLevel) - 1;

    while (low <= high) {
      int mid = (low + high) >> 1;
      double midVal = mds.getX(mid, mipLevel);

      if (midVal < domainOrigin) {
        low = mid + 1;
      } else if (midVal > domainOrigin) {
        high = mid - 1;
      } else {
        return mid; // key found
      }
    }
    return Math.max(0, Math.min(mds.getNumSamples(mipLevel) - 1, low));
  }

  public static double computeDomainEnd(XYPlot view, XYDataset[] dataSets) {
    double end = Double.MIN_VALUE;
    for (int i = 0; i < dataSets.length; i++) {
      end = Math.max(end, findRenderedEnd(view, dataSets[i]));
    }
    return end;
  }

  public static double computeDomainStart(XYPlot plot, XYDataset[] dataSets) {
    double start = Double.MAX_VALUE;
    for (int i = 0; i < dataSets.length; i++) {
      start = Math.min(start, findRenderedStart(plot, dataSets[i]));
    }
    return start;
  }

  /**
   * Determines if a and b are equal, taking into consideration that a or b (or
   * both a and b) could be null.
   * 
   * TODO: Move this into a utility class
   */
  public static boolean isEqual(Object a, Object b) {
    if (a == b) {
      return true;
    }

    if (a == null && b == null) {
      return true;
    }

    if ((a == null && b != null) || (b == null && a != null)) {
      return false;
    }

    return a.equals(b);
  }

  public static boolean isSameDomain(String url1, String url2) {
    // won't current handle case if hostpage is https: and url2 is relative
    int ss1 = url1.indexOf("//");
    int ss2 = url2.indexOf("//");
    if (ss1 == -1 || ss2 == -1) {
      return true;
    }

    String scheme1 = "http";
    if (ss1 != -1) {
      scheme1 = url1.substring(0, ss1);
      url1 = url1.substring(ss1 + 2);
    }

    String scheme2 = "http";
    if (ss2 != -1) {
      scheme2 = url2.substring(0, ss2);
      url2 = url2.substring(ss2 + 2);
    }
    if (scheme1.equals(scheme2)) {
      String parts1[] = url1.split("/");
      String parts2[] = url2.split("/");
      String hostpart1[] = parts1[0].split(":");
      String hostpart2[] = parts2[0].split(":");
      String port1 = scheme1.equals("https") ? "443" : "80";
      String port2 = port1;
      String host1 = hostpart1[0];
      String host2 = hostpart2[0];

      if (hostpart1.length > 1) {
        port1 = hostpart1[1];
      }
      if (hostpart2.length > 1) {
        port2 = hostpart2[1];
      }

      return host1.equals(host2) && port1.equals(port2);
    }
    return false;
  }

  private static double findRenderedEnd(XYPlot plot, XYDataset dataSet) {
    int mipLevel = -1;
    int domainStart, domainEnd;
    double domainOrigin = dataSet.getDomainBegin();
    double currentDomain = dataSet.getDomainEnd() - domainOrigin;

    do {
      mipLevel++;

      domainStart = binarySearch(dataSet, domainOrigin, mipLevel);
      domainEnd = binarySearch(dataSet, domainOrigin + currentDomain, mipLevel);
    } while (domainEnd - domainStart > plot.getMaxDrawableDataPoints());
    return dataSet.getX(domainEnd, mipLevel);
  }

  private static double findRenderedStart(XYPlot plot, XYDataset dataSet) {
    int mipLevel = -1;
    int domainStart, domainEnd;
    double domainOrigin = dataSet.getDomainBegin();
    double currentDomain = dataSet.getDomainEnd() - domainOrigin;

    do {
      mipLevel++;

      domainStart = binarySearch(dataSet, domainOrigin, mipLevel);
      domainEnd = binarySearch(dataSet, domainOrigin + currentDomain, mipLevel);
    } while (domainEnd - domainStart > plot.getMaxDrawableDataPoints());
    return dataSet.getX(domainStart, mipLevel);
  }
}
