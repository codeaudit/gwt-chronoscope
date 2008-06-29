package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.util.MathUtil;

/**
 * Class used to pre-process a domain/range of values into a multiresolution
 * representation using a given strategy
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class XYMultiresolution {

  public interface XYStrategy {

    double getDomainValue(XYMultiresolution m, int level, int index);

    int getNumLevels(XYMultiresolution multiresolution);

    int getNumSamples(XYMultiresolution m, int level);

    double getRangeValue(XYMultiresolution m, int level, int index);
  }

  protected abstract static class AbstractMemoizedXYStrategy
      implements XYStrategy {

    protected double getMemoizedDomainValue(XYMultiresolution m,
        int previousLevel, int index) {
      return m.getMultiDomain()[previousLevel][index];
    }

    protected double getMemoizedRangeValue(XYMultiresolution m,
        int previousLevel, int index) {
      return m.getMultiRange()[previousLevel][index];
    }
  }

  public static final XYStrategy MEAN_STRATEGY
      = new AbstractMemoizedXYStrategy() {

    public double getDomainValue(XYMultiresolution m, int level, int index) {
      if (level == 0) {
        return m.getX(index);
      }
      return getMemoizedDomainValue(m, level - 1, index * 2);
    }

    public int getNumLevels(XYMultiresolution m) {
      return (int) MathUtil.log2(m.getNumSamples()) + 1;
    }

    public int getNumSamples(XYMultiresolution m, int level) {
      if (level == 0) {
        return m.getNumSamples();
      }
      return getNumSamples(m, level - 1) / 2;
    }

    public double getRangeValue(XYMultiresolution m, int level, int index) {
      if (level == 0) {
        return m.getY(index);
      }
      int ind = index * 2;
      return
          (getMemoizedRangeValue(m, level - 1, ind + 1) + getMemoizedRangeValue(
              m, level - 1, ind)) / 2.0;
    }
  };

  public static final XYStrategy MAX_STRATEGY
      = new AbstractMemoizedXYStrategy() {

    public double getDomainValue(XYMultiresolution m, int level, int index) {
      if (level == 0) {
        return m.getX(index);
      }
      return getMemoizedDomainValue(m, level - 1, index * 2);
    }

    public int getNumLevels(XYMultiresolution m) {
      return (int) MathUtil.log2(m.getNumSamples()) + 1;
    }

    public int getNumSamples(XYMultiresolution m, int level) {
      if (level == 0) {
        return m.getNumSamples();
      }
      return getNumSamples(m, level - 1) / 2;
    }

    public double getRangeValue(XYMultiresolution m, int level, int index) {
      if (level == 0) {
        return m.getY(index);
      }
      int ind = index * 2;
      return Math.max(getMemoizedRangeValue(m, level - 1, ind + 1),
          getMemoizedRangeValue(m, level - 1, ind));
    }
  };

  public static XYMultiresolution createMultiresolutionWithMax(double[] domain,
      double[] range) {
    return createMultiresolutionWithStrategy(domain, range, domain.length,
        MAX_STRATEGY);
  }

  public static XYMultiresolution createMultiresolutionWithMean(double[] domain,
      double[] range) {
    return createMultiresolutionWithStrategy(domain, range, domain.length,
        MEAN_STRATEGY);
  }

  public static XYMultiresolution createMultiresolutionWithStrategy(
      double[] domain, double[] range, int length, XYStrategy strategy) {
    XYMultiresolution xy = new XYMultiresolution(domain, range, length);
    xy.compute(strategy);
    return xy;
  }

  protected double[] domain;

  protected double[] range;

  protected int length;

  protected double[][] multiDomain;

  protected double[][] multiRange;

  protected double rangeBottom = Double.MAX_VALUE, rangeTop = Double.MIN_VALUE;

  protected int[] multiLength;

  public XYMultiresolution(double[] domain, double[] range, int length) {

    this.domain = domain;
    this.range = range;
    this.length = length;
  }

  public double[][] getMultiDomain() {
    return multiDomain;
  }

  public int[] getMultiLength() {
    return multiLength;
  }

  public double[][] getMultiRange() {
    return multiRange;
  }

  public double getRangeBottom() {
    return rangeBottom;
  }

  public double getRangeTop() {
    return rangeTop;
  }

  public double getX(int index) {
    return domain[index];
  }

  public double getY(int index) {
    return range[index];
  }

  double[] allocMultiresolution(int numSamples) {
    return new double[numSamples];
  }

  private void compute(XYStrategy strategy) {

    int levels = strategy.getNumLevels(this);
    multiDomain = new double[levels][];
    multiRange = new double[levels][];
    multiLength = new int[levels];
    for (int level = 0; level < levels; level++) {
      int numSamples = strategy.getNumSamples(this, level);
      multiDomain[level] = allocMultiresolution(numSamples);
      multiRange[level] = allocMultiresolution(numSamples);
      multiLength[level] = multiDomain[level].length;
      for (int index = 0; index < multiLength[level]; index++) {
        multiDomain[level][index] = strategy.getDomainValue(this, level, index);
        multiRange[level][index] = strategy.getRangeValue(this, level, index);
        if (level == 0) {
          rangeBottom = Math.min(rangeBottom, multiRange[level][index]);
          rangeTop = Math.max(rangeTop, multiRange[level][index]);
        }
      }
    }
  }

  private int getNumSamples() {
    return length;
  }
}
