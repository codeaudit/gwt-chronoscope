package org.timepedia.chronoscope.client.util;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;

/**
 * Conglomeration of common utility functions.
 */
public final class Util {

  /**
   * Searches the datapoints in a dataset for a domain value and returns the
   * corresponding domain index at a given mip level.  If the dataset doesn't
   * contain the specified domain value, then the domain index of the next
   * largest domain value is returned. The one exception to this rule is when
   * <tt>domainValue</tt> is greater than all domain values within the dataset,
   * in which case the largest domain value in the dataset is returned. <p> The
   * dataset's domain values are assumed to be in sorted ascending order (this
   * should be enforced by all {@link Dataset} implementations).
   *
   * @param ds          - The dataset to search on
   * @param domainValue - The sought-after domain value
   * @param mipLevel    - The mip level to search on within the dataset
   */
  public static <T extends Tuple2D> int binarySearch(Dataset<T> ds, double domainValue, 
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

  /**
   * Returns a copy of the specified array.
   */
  public static double[] copyArray(double[] a) {
    if (a == null) {
      return null;
    }
    double[] copy = new double[a.length];
    System.arraycopy(a, 0, copy, 0, a.length);
    return copy;
  }

  /**
   * Returns a copy of the specified array.
   */
  public static int[] copyArray(int[] a) {
    if (a == null) {
      return null;
    }
    int[] copy = new int[a.length];
    System.arraycopy(a, 0, copy, 0, a.length);
    return copy;
  }

  /**
   * Returns a copy of the specified 2-dimensional array.
   */
  public static double[][] copyArray(double[][] a) {
    double[][] copy = new double[a.length][];
    for (int i = 0; i < a.length; i++) {
      if (a[i] != null) {
        copy[i] = new double[a[i].length];
        System.arraycopy(a[i], 0, copy[i], 0, a[i].length);
      } else {
        copy[i] = null;
      }
    }
    return copy;
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

}
