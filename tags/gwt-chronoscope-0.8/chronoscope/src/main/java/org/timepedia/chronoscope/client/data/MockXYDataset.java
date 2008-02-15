package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.XYDataset;

import java.util.Date;

/**
 * Sample canned dataset used for testing
 */
public class MockXYDataset implements XYDataset {

  public static final String IDENT = "mock";

  private static final Date b1970 = new Date(70, 1, 0);

  private static double[][] xcache;

  private static double[][] ycache;

  private static boolean cache = false;

  public MockXYDataset() {
    if (!cache) {
      genCache();
    }
  }

  public void genCache() {

    int levels = (int) (Math.log(1000) / Math.log(2)) + 1;
    xcache = new double[levels][];
    ycache = new double[levels][];
    for (int i = 0; i < levels; i++) {
      xcache[i] = new double[getNumSamples(i)];
      ycache[i] = new double[getNumSamples(i)];
      for (int j = 0; j < xcache[i].length; j++) {
        xcache[i][j] = getX(j, i);
        ycache[i][j] = getY(j, i);
      }
    }
    cache = true;
  }

  public String getAxisId() {
    return "none";
  }

  public String getIdentifier() {
    return IDENT;
  }

  public int getNumSamples() {
    return 1000;
  }

  public int getNumSamples(int mipLevel) {
    return (int) (getNumSamples() / Math.pow(2, mipLevel));
  }

  public double getRangeBottom() {
    return -1;
  }

  public String getRangeLabel() {
    return "Mock";
  }

  public double getRangeTop() {
    return 1;
  }

  public double getX(int index) {
    if (cache) {
      return xcache[0][index];
    }
    return (double) (b1970.getTime() + (index * 86400L * 1000L));
  }

  public double getX(int index, int mipLevel) {
    if (cache) {
      return xcache[mipLevel][index];
    }
    if (mipLevel == 0) {
      return getX(index);
    }
    int ind = index * 2;
    return getX(ind, mipLevel - 1);
  }

  public double getY(int index) {
    if (cache) {
      return ycache[0][index];
    }
    double arg = 5.0 * (double) index / (double) getNumSamples();
    return Math.sin(Math.PI * arg) / Math.exp(arg / 5.0);
  }

  public double getY(int index, int mipLevel) {
    if (cache) {
      return ycache[mipLevel][index];
    }
    if (mipLevel == 0) {
      return getY(index);
    }
    int ind = index * 2;
    return (getY(ind + 1, mipLevel - 1) + getY(ind, mipLevel - 1)) / 2.0;
  }
}
