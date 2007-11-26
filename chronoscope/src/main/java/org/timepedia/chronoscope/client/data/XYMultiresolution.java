package org.timepedia.chronoscope.client.data;

/**
 * Class used to pre-process a domain/range of values into a multiresolution representation using a given strategy
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class XYMultiresolution {
    private double[] domain;
    private double[] range;
    private double[][] multiDomain;
    private double[][] multiRange;
    private double rangeBottom = Double.MAX_VALUE, rangeTop = Double.MIN_VALUE;

    public XYMultiresolution(double[] domain, double[] range) {

        this.domain = domain;
        this.range = range;
    }

    private void compute(XYStrategy strategy) {

        int levels = strategy.getNumLevels(this);
        multiDomain = new double[levels][];
        multiRange = new double[levels][];
        for (int level = 0; level < levels; level++) {
            int numSamples = strategy.getNumSamples(this, level);
            multiDomain[level] = new double[numSamples];
            multiRange[level] = new double[numSamples];
            for (int index = 0; index < multiDomain[level].length; index++) {
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
        return domain.length;
    }

    public double getX(int index) {
        return domain[index];
    }

    public double getY(int index) {
        return range[index];
    }


    public interface XYStrategy {
        public int getNumSamples(XYMultiresolution m, int level);

        public double getDomainValue(XYMultiresolution m, int level, int index);

        public double getRangeValue(XYMultiresolution m, int level, int index);

        int getNumLevels(XYMultiresolution multiresolution);
    }

    protected static abstract class AbstractMemoizedXYStrategy implements XYStrategy {
        protected double getMemoizedRangeValue(XYMultiresolution m, int previousLevel, int index) {
            return m.getMultiRange()[previousLevel][index];
        }

        protected double getMemoizedDomainValue(XYMultiresolution m, int previousLevel, int index) {
            return m.getMultiDomain()[previousLevel][index];
        }


    }

    public static final XYStrategy MEAN_STRATEGY = new AbstractMemoizedXYStrategy() {
        public int getNumSamples(XYMultiresolution m, int level) {
            if (level == 0) {
                return m.getNumSamples();
            }
            return getNumSamples(m, level - 1) / 2;
        }

        public double getDomainValue(XYMultiresolution m, int level, int index) {
            if (level == 0) {
                return m.getX(index);
            }
            return getMemoizedDomainValue(m, level - 1, index * 2);
        }


        public double getRangeValue(XYMultiresolution m, int level, int index) {
            if (level == 0) {
                return m.getY(index);
            }
            int ind = index * 2;
            return ( getMemoizedRangeValue(m, level - 1, ind + 1) + getMemoizedRangeValue(m, level - 1, ind) ) / 2.0;
        }


        public int getNumLevels(XYMultiresolution m) {
            return (int) ( Math.log(m.getNumSamples()) / Math.log(2) ) + 1;
        }
    };


    public static final XYStrategy MAX_STRATEGY = new AbstractMemoizedXYStrategy() {
        public int getNumSamples(XYMultiresolution m, int level) {
            if (level == 0) {
                return m.getNumSamples();
            }
            return getNumSamples(m, level - 1) / 2;
        }

        public double getDomainValue(XYMultiresolution m, int level, int index) {
            if (level == 0) {
                return m.getX(index);
            }
            return getMemoizedDomainValue(m, level - 1, index * 2);
        }


        public double getRangeValue(XYMultiresolution m, int level, int index) {
            if (level == 0) {
                return m.getY(index);
            }
            int ind = index * 2;
            return Math.max(getMemoizedRangeValue(m, level - 1, ind + 1), getMemoizedRangeValue(m, level - 1, ind));
        }


        public int getNumLevels(XYMultiresolution m) {
            return (int) ( Math.log(m.getNumSamples()) / Math.log(2) ) + 1;
        }
    };

    public double getRangeBottom() {
        return rangeBottom;
    }

    public double getRangeTop() {
        return rangeTop;
    }

    public double[][] getMultiRange() {
        return multiRange;
    }

    public double[][] getMultiDomain() {
        return multiDomain;
    }


    public static XYMultiresolution createMultiresolutionWithMean(double[] domain, double[] range) {
        return createMultiresolutionWithStrategy(domain, range, MEAN_STRATEGY);
    }

    public static XYMultiresolution createMultiresolutionWithMax(double[] domain, double[] range) {
        return createMultiresolutionWithStrategy(domain, range, MAX_STRATEGY);
    }

    public static XYMultiresolution createMultiresolutionWithStrategy(double[] domain, double[] range,
                                                                      XYStrategy strategy) {
        XYMultiresolution xy = new XYMultiresolution(domain, range);
        xy.compute(strategy);
        return xy;
    }
}
