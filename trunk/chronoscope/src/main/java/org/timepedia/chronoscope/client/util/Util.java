package org.timepedia.chronoscope.client.util;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYDatasets;

/**
 * Conglomeration of common utility functions.
 */
public final class Util {

  /**
   * Searches the datapoints in a dataset for a domain value and returns the corresponding
   * domain index at a given mip level.  If the dataset doesn't contain the specified 
   * domain value, then the domain index of the next largest domain value is returned.
   * The one exception to this rule is when <tt>domainValue</tt> is greater than all
   * domain values within the dataset, in which case the largest domain value in the
   * dataset is returned.
   * <p>
   * The dataset's domain values are assumed to be in sorted ascending order (this
   * should be enforced by all {@link XYDataset} implementations).  
   * 
   * @param ds - The dataset to search on
   * @param domainValue - The sought-after domain value
   * @param mipLevel - The mip level to search on within the dataset
   */
  public static int binarySearch(XYDataset ds, double domainValue,
      int mipLevel) {
    int low = 0;
    int high = ds.getNumSamples(mipLevel) - 1;

    while (low <= high) {
      int mid = (low + high) >> 1;
      double midVal = ds.getX(mid, mipLevel);

      if (midVal < domainValue) {
        low = mid + 1;
      } else if (midVal > domainValue) {
        high = mid - 1;
      } else {
        return mid; // key found
      }
    }
    
    return MathUtil.bound(low, 0, ds.getNumSamples(mipLevel) - 1);
  }

  public static double calcVisibleDomainMax(int maxDrawableDataPts, XYDatasets dataSets) {
    double end = Double.MIN_VALUE;
    for (int i = 0; i < dataSets.size(); i++) {
      XYDataset ds = dataSets.get(i);
      // find the lowest mip level whose # of data points is not greater
      // than maxDrawablePts
      int lowestMipLevel = findLowestMipLevel(ds, maxDrawableDataPts);
      int numSamples = ds.getNumSamples(lowestMipLevel);
      end = Math.max(end, ds.getX(numSamples - 1, lowestMipLevel));
    }
    return end;
  }

  /**
   * Determines if a and b are equal, taking into consideration that a or b (or
   * both a and b) could be null.
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
  
  /**
   * Finds the lowest mip level of the specified dataset whose data point cardinality 
   * is not greater than <tt>maxDrawablePts</tt>.
   */
  private static int findLowestMipLevel(XYDataset ds, int maxDrawablePts) {
    int mipLevel = 0;
    while (true) {
      int numPoints = ds.getNumSamples(mipLevel);
      if (numPoints <= maxDrawablePts) {
        return mipLevel;
      }
      ++mipLevel;
    }
  }

}
