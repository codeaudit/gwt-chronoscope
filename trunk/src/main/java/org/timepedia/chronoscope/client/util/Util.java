package org.timepedia.chronoscope.client.util;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlot;

/**
 * Utility class
 */
public class Util {
    public static double computeDomainStart(XYPlot plot, XYDataset[] dataSets) {
        double start = Double.MAX_VALUE;
        for (int i = 0; i < dataSets.length; i++) {
            start = Math.min(start, findRenderedStart(plot, dataSets[i]));
        }
        return start;
    }

    private static double findRenderedStart(XYPlot plot, XYDataset dataSet) {
        int mipLevel = -1;
        int domainStart, domainEnd;
        double domainOrigin = dataSet.getX(0);
        double currentDomain = dataSet.getX(dataSet.getNumSamples() - 1) - domainOrigin;


        do {
            mipLevel++;

            domainStart = binarySearch(dataSet, domainOrigin, mipLevel);
            domainEnd = binarySearch(dataSet, domainOrigin + currentDomain, mipLevel);
        } while (domainEnd - domainStart > plot.getMaxDrawableDataPoints());
        return dataSet.getX(domainStart, mipLevel);
    }

    private static double findRenderedEnd(XYPlot plot, XYDataset dataSet) {
        int mipLevel = -1;
        int domainStart, domainEnd;
        double domainOrigin = dataSet.getX(0);
        double currentDomain = dataSet.getX(dataSet.getNumSamples() - 1) - domainOrigin;


        do {
            mipLevel++;

            domainStart = binarySearch(dataSet, domainOrigin, mipLevel);
            domainEnd = binarySearch(dataSet, domainOrigin + currentDomain, mipLevel);
        } while (domainEnd - domainStart > plot.getMaxDrawableDataPoints());
        return dataSet.getX(domainEnd, mipLevel);
    }


    public static double computeDomainEnd(XYPlot view, XYDataset[] dataSets) {
        double end = Double.MIN_VALUE;
        for (int i = 0; i < dataSets.length; i++) {
            end = Math.max(end, findRenderedEnd(view, dataSets[i]));
        }
        return end;
    }

    public static int binarySearch(XYDataset mds, double domainOrigin, int mipLevel) {
        int low = 0;
        int high = mds.getNumSamples(mipLevel) - 1;

        while (low <= high) {
            int mid = ( low + high ) >> 1;
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
}
