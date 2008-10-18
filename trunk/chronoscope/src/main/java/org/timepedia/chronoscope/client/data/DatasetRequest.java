package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.util.ArgChecker;

/**
 * Represents a request to construct an instance of {@link Dataset}.
 * 
 * @author Chad Takahashi
 */
public abstract class DatasetRequest {

  /**
   * Request in which client provides their own domain and range data, and the
   * multiDomain and multiRange data is then computed by the provided
   * {@link #setDefaultMipMapStrategy(MipMapStrategy) MipMapStrategy} object. 
   */
  public static final class Basic extends DatasetRequest {
    private double[] domain, range;

    public double[] getDomain() {
      return domain;
    }

    public double[] getRange() {
      return range;
    }

    public void setDomain(double[] domain) {
      this.domain = domain;
    }

    public void setRange(double[] range) {
      this.range = range;
    }

    public void validate() {
      super.validate();
      ArgChecker.isNotNull(domain, "domain");
      ArgChecker.isNotNull(range, "range");

      if (domain.length != range.length) {
        throw new IllegalArgumentException(
            "domain[] and range[] are different lengths" + ": " + domain.length
                + ", " + range.length);
      }
    }
  }
  /**
   * XYDatasetRequest in which the domain and range values at each MIP level
   * (i.e. the multiDomain and multiRange) must be manually specified.
   */
  public static final class MultiRes extends DatasetRequest {
    private Array2D multiDomain;
    private Array2D multiRange;

    public Array2D getMultiDomain() {
      return multiDomain;
    }

    public Array2D getMultiRange() {
      return multiRange;
    }

    public void setMultiDomain(Array2D multiDomain) {
      this.multiDomain = multiDomain;
    }

    public void setMultiRange(Array2D multiRange) {
      this.multiRange = multiRange;
    }

    public void validate() {
      ArgChecker.isNotNull(multiDomain, "multiDomain");
      ArgChecker.isNotNull(multiRange, "multiRange");

      // Verify that multiDomain and multiRange have same number
      // of elements at each level
      if (!multiDomain.isSameSize(multiRange)) {
        throw new IllegalArgumentException(
            "multiDomain and multiRange differ in size");
      }
    }
  }

  private double approximateMinimumInterval = Double.NaN;
  private String axisId, identifier, label;
  private MipMapStrategy defaultMipMapStrategy = DefaultMipMapStrategy.MEAN;
  private double rangeBottom = Double.NaN, rangeTop = Double.NaN;

  public double getApproximateMinimumInterval() {
    return approximateMinimumInterval;
  }

  public String getAxisId() {
    return axisId;
  }

  public String getIdentifier() {
    return identifier;
  }

  public String getLabel() {
    return label;
  }

  public MipMapStrategy getDefaultMipMapStrategy() {
    return defaultMipMapStrategy;
  }

  public double getRangeBottom() {
    return rangeBottom;
  }

  public double getRangeTop() {
    return rangeTop;
  }

  public void setApproximateMinimumInterval(double approximateMinimumInterval) {
    this.approximateMinimumInterval = approximateMinimumInterval;
  }

  public void setAxisId(String axisId) {
    this.axisId = axisId;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public void setDefaultMipMapStrategy(MipMapStrategy mipMapStrategy) {
    this.defaultMipMapStrategy = mipMapStrategy;
  }

  public void setRangeBottom(double rangeBottom) {
    this.rangeBottom = rangeBottom;
  }

  public void setRangeTop(double rangeTop) {
    this.rangeTop = rangeTop;
  }

  /**
   * Validates the state of this request object.
   */
  public void validate() {
    ArgChecker.isNotNull(defaultMipMapStrategy, "mipMapStrategy");
  }

}
