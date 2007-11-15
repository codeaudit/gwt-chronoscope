package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.XYDataset;

/**
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class ArrayXYDataset implements XYDataset {
    private final double[] domain;
    private final double[] range;
    private final String identifier;
    private final String label;
    private final String axisId;
    private double rangeBottom, rangeTop;
    private double[][] multiDomain;
    private double[][] multiRange;


    public ArrayXYDataset(String identifier, double[] domain, double[] range, String label, String axisId) {
        this(identifier, domain, range, label, axisId, XYMultiresolution.MEAN_STRATEGY);
    }


    public ArrayXYDataset(String identifier, double[] domain, double[] range, String label, String axisId,
                          XYMultiresolution.XYStrategy strategy) {
        this.label = label;
        this.identifier = identifier;
        this.domain = domain;
        this.range = range;
        this.axisId = axisId;
        genMultiresolution(strategy);
    }


    public ArrayXYDataset(String identifier, double[][] domains, double[][] ranges, double top, double bottom,
                          String label, String axisId) {
        this.identifier = identifier;
        this.label = label;
        this.domain = domains[0];
        this.range = ranges[0];
        multiDomain = domains;
        multiRange = ranges;
        rangeTop = top;
        rangeBottom = bottom;
        this.axisId = axisId;
    }


    public int getNumSamples() {
        return domain.length;
    }


    protected void genMultiresolution(XYMultiresolution.XYStrategy strategy) {
        XYMultiresolution xy = computeMultiresolution(strategy);
        multiDomain = xy.getMultiDomain();
        multiRange = xy.getMultiRange();
        rangeTop = xy.getRangeTop();
        rangeBottom = xy.getRangeBottom();
        rangeBottom = Math.min(rangeBottom, 0);
    }

    protected XYMultiresolution computeMultiresolution(XYMultiresolution.XYStrategy strategy) {
        return XYMultiresolution.createMultiresolutionWithStrategy(domain, range, strategy);
    }


    public int getNumSamples(int mipLevel) {
        return multiDomain[mipLevel].length;
    }

    public double getX(int index) {
        return domain[index];
    }

    public double getY(int index) {
        return range[index];
    }

    public double getX(int index, int mipLevel) {
        return multiDomain[mipLevel][index];
    }

    public double getY(int index, int mipLevel) {
        return multiRange[mipLevel][index];
    }


    public double getRangeBottom() {
        return rangeBottom;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getRangeLabel() {
        return label;
    }

    public String getAxisId() {
        return axisId;
    }

    public double getRangeTop() {
        return rangeTop;

    }

    public double[] getDomain() {
        return domain;
    }

    public double[] getRange() {
        return range;
    }
}
