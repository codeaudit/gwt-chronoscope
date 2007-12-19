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

    public static boolean isSameDomain(String URL1, String URL2) {
        // won't current handle case if hostpage is https: and URL2 is relative
        int ss1 = URL1.indexOf("//");
        int ss2 = URL2.indexOf("//");
        if(ss1 == -1 || ss2 == -1) return true;

        String scheme1 = "http";
        if(ss1 != -1) {
            scheme1 = URL1.substring(0, ss1);
            URL1 = URL1.substring(ss1+2);
        }

        String scheme2 = "http";
        if(ss2 != -1) {
            scheme2 = URL2.substring(0, ss2);
            URL2 = URL2.substring(ss2+2);
        }
        if(scheme1.equals(scheme2)) {
           String parts1[]= URL1.split("/");
           String parts2[]= URL2.split("/");
           String hostpart1[] = parts1[0].split(":");
           String hostpart2[] = parts2[0].split(":");
           String port1 = scheme1.equals("https") ? "443" : "80";
           String port2 = port1;
           String host1 = hostpart1[0];
           String host2 = hostpart2[0];

           if(hostpart1.length > 1) port1=hostpart1[1];
           if(hostpart2.length > 1) port2=hostpart2[1];

           return host1.equals(host2) && port1.equals(port2);
        }
        return false;

    }
}
