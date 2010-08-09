package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.XYDataset;

import java.util.Date;

/**
 * Sample canned dataset used for testing
 */
public class MockXYDataset implements XYDataset {
    private static final Date b1970 = new Date(70, 1, 0);
    private static double[][] xcache;
    private static double[][] ycache;
    private static boolean cache = false;
    public static final String IDENT = "mock";


    public MockXYDataset() {
        if (!cache) {
            genCache();
        }
    }

    public void genCache() {

        int levels = (int) ( Math.log(1000) / Math.log(2) ) + 1;
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

    public int getNumSamples() {
        return 1000;
    }

    public int getNumSamples(int mipLevel) {
        return (int) ( getNumSamples() / Math.pow(2, mipLevel) );
    }

    public double getX(int index) {
        if (cache) {
            return xcache[0][index];
        }
        return (double) ( b1970.getTime() + ( index * 86400l * 1000l ) );
    }

    public double getY(int index) {
        if (cache) {
            return ycache[0][index];
        }
        double arg = 5.0 * (double) index / (double) getNumSamples();
        return Math.sin(Math.PI * arg) / Math.exp(arg / 5.0);

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

    public double getY(int index, int mipLevel) {
        if (cache) {
            return ycache[mipLevel][index];
        }
        if (mipLevel == 0) {
            return getY(index);
        }
        int ind = index * 2;
        return ( getY(ind + 1, mipLevel - 1) + getY(ind, mipLevel - 1) ) / 2.0;

    }

    public double getRangeBottom() {
        return -1;
    }

    public String getIdentifier() {
        return IDENT;
    }

    public String getRangeLabel() {
        return "Mock";
    }

    public String getAxisId() {
        return "none";
    }

    public double getRangeTop() {
        return 1;
    }
}
