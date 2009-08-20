package org.timepedia.chronoscope.client;

/**
 * DataShape captures the relevant properties of a dataset for test fixtures
 */

public class DataShape {
    public double min;
    public double max;

    public DataShape(double min, double max) {
        this.min = min;
        this.max = max;
    }
}
