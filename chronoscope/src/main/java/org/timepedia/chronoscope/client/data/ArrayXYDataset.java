package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Util;

/**
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class ArrayXYDataset implements XYDataset {

  double[] domain;

  double[] range;

  double rangeBottom, rangeTop;

  double[][] multiDomain;

  double[][] multiRange;

  int length;

  int multiLengths[];

  private final String identifier;

  private final String label;

  private final String axisId;

  protected double approximateMinimumInterval;

  public ArrayXYDataset(String identifier, double[] domain, double[] range,
      String label, String axisId) {
    this(identifier, domain, range, label, axisId,
        XYMultiresolution.MEAN_STRATEGY);
  }

  public ArrayXYDataset(String identifier, double[] domain, double[] range,
      String label, String axisId, XYMultiresolution.XYStrategy strategy) {

    this(identifier, domain, range, label, axisId, domain.length);
    genMultiresolution(strategy);
  }

  public ArrayXYDataset(String identifier, double[][] domains,
      double[][] ranges, double top, double bottom, String label,
      String axisId) {
    this(identifier, domains[0], ranges[0], label, axisId, domains[0].length);
    multiDomain = domains;
    multiRange = ranges;
    rangeTop = top;
    rangeBottom = bottom;
    multiLengths = new int[multiDomain.length];
    for (int i = 0; i < domains.length; i++) {
      multiLengths[i] = domains[i].length;
    }
  }

  protected ArrayXYDataset(String identifier, double[] domain, double[] range,
      String label, String axisId, int capacity) {
    validateDomainAndRange(domain, range);
    this.label = label;
    this.identifier = identifier;
    if (capacity > domain.length) {
      this.domain = new double[capacity];
      this.range = new double[capacity];
      Util.arraycopy(domain, 0, this.domain, 0, domain.length);
      Util.arraycopy(range, 0, this.range, 0, range.length);
    } else {
      this.domain = domain;
      this.range = range;
    }
    this.length = domain.length;
    this.axisId = axisId;
    this.approximateMinimumInterval = (getDomainEnd() - getDomainBegin())
        / getNumSamples();
  }

  public ArrayXYDataset(String identifier, double[][] domains,
      double[][] ranges, double top, double bottom, String label, String axisId,
      double approximateMinInterval) {
    this(identifier, domains, ranges, top, bottom, label, axisId);
    this.approximateMinimumInterval = approximateMinInterval;
  }

  public ArrayXYDataset(String identifier, double[] domainVal,
      double[] rangeVal, String label, String axisId,
      double approximateMinInterval) {
    this(identifier, domainVal, rangeVal, label, axisId);
    this.approximateMinimumInterval = approximateMinInterval;
  }

  public double getApproximateMinimumInterval() {
    return approximateMinimumInterval;
  }

  public String getAxisId() {
    return axisId;
  }

  public double[] getDomain() {
    return domain;
  }

  public String getIdentifier() {
    return identifier;
  }

  public int getNumSamples() {
    return length;
  }

  public int getNumSamples(int mipLevel) {
    return multiLengths[mipLevel];
  }

  public double[] getRange() {
    return range;
  }

  public double getRangeBottom() {
    return rangeBottom;
  }

  public String getRangeLabel() {
    return label;
  }

  public double getRangeTop() {
    return rangeTop;
  }

  public double getX(int index) {
    return domain[index];
  }

  public double getX(int index, int mipLevel) {
    return multiDomain[mipLevel][index];
  }

  public double getY(int index) {
    return range[index];
  }

  public double getY(int index, int mipLevel) {
    return multiRange[mipLevel][index];
  }

  public double getDomainBegin() {
    return getX(0);
  }

  public double getDomainEnd() {
    return getX(getNumSamples() - 1);
  }

  protected XYMultiresolution computeMultiresolution(
      XYMultiresolution.XYStrategy strategy) {
    return XYMultiresolution
        .createMultiresolutionWithStrategy(domain, range, length, strategy);
  }

  protected void genMultiresolution(XYMultiresolution.XYStrategy strategy) {
    XYMultiresolution xy = computeMultiresolution(strategy);
    multiDomain = xy.getMultiDomain();
    multiRange = xy.getMultiRange();
    multiLengths = xy.getMultiLength();
    rangeTop = xy.getRangeTop();
    rangeBottom = xy.getRangeBottom();
    approximateMinimumInterval = xy.getMinInterval();
  }
  
  private void validateDomainAndRange(double[] domain, double[] range) {
    ArgChecker.isNotNull(domain, "domain");
    ArgChecker.isNotNull(range, "range");
    if (domain.length != range.length) {
      throw new IllegalArgumentException("domain and range arrays are different lengths: "
          + "(domain.length=" + domain.length + ", range.length=" + range.length + ")");
    }
  }
}
