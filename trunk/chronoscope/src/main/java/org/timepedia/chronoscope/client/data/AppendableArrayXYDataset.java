package org.timepedia.chronoscope.client.data;

import java.util.Iterator;

/**
 * An implementation of the AppendableXYDataset interface using arrays.
 * insertXY() and setXY() where the X value is not changed or greater than the
 * last x-value are quick and average O(1) running time. Mutating an X value
 * means the element must be repositioned, causing a number of array copies, as
 * does removeXY(). The implementation will try to optimize the case of batch
 * updates where it may be cheaper to rebuild the entire dataset. <p/> In
 * general, this implementation is best suited for streaming datasets where
 * insertXY() is the primary operation and used to append points at the tail
 * end. Essentially, complexity is similar to insertion in insertion sort *
 * height of multiresolution stack.
 */
public class AppendableArrayXYDataset extends AbstractUpdateableArrayXYDataset
    implements AppendableXYDataset {

  static class MutableXYMultiresolution extends XYMultiresolution {

//        public static native void arraycopy(double[] src, int srcOfs, double[] dest, int destOfs, int len) /*-{
//             Array.prototype.splice.apply(dest, [destOfs, len].concat(src));
//
//        }-*/;

    public static void arraycopy(double[] src, int srcOfs, double[] dest,
        int destOfs, int len) {
      for (int i = 0; i < len; i++) {
        dest[destOfs + i] = src[srcOfs + i];
      }
    }

    public MutableXYMultiresolution(double[] domain, double[] range, int length,
        double[][] multiDomain, double[][] multiRange, int[] multiLength,
        double rangeTop, double rangeBottom) {
      super(domain, range, length);
      this.multiDomain = multiDomain;
      this.multiRange = multiRange;
      this.multiLength = multiLength;
      this.rangeTop = rangeTop;
      this.rangeBottom = rangeBottom;
    }

    public void setDomainValue(int index, double x) {
      if (index >= domain.length) {
        realloc();
      }
      this.domain[index] = x;
    }

    public void setRangeValue(int index, double y) {
      if (index >= domain.length) {
        realloc();
      }
      this.range[index] = y;
    }

    public void updateMultiresolution(int index, int level,
        XYStrategy strategy) {
      int evenIndex = index - index % 2;
      if (level == 0) {
        multiDomain[level][index] = domain[index];
        multiRange[level][index] = range[index];
        multiLength[level] = index;
        rangeTop = Math.max(rangeTop, range[index]);
        rangeBottom = Math.min(rangeBottom, range[index]);
        updateMultiresolution(evenIndex / 2, level + 1, strategy);
      } else
      if (level < multiDomain.length && evenIndex < multiDomain[level].length) {
        multiDomain[level][evenIndex] = strategy
            .getDomainValue(this, level, evenIndex);
        multiRange[level][evenIndex] = strategy
            .getRangeValue(this, level, evenIndex);
        multiLength[level] = evenIndex + 1;
        evenIndex = evenIndex / 2;
        updateMultiresolution(evenIndex, level + 1, strategy);
      }
    }

    double[] allocMultiresolution(int numSamples) {
      return new double[numSamples * GROWTH_FACTOR];
    }

    private void realloc() {
      double newdomain[] = new double[domain.length * GROWTH_FACTOR];
      double newrange[] = new double[range.length * GROWTH_FACTOR];
      double newMultiDomain[][] = new double[multiDomain.length][];
      double newMultiRange[][] = new double[multiRange.length][];
      for (int i = 0; i < multiDomain.length; i++) {
        newMultiDomain[i] = new double[multiDomain[i].length * GROWTH_FACTOR];
        newMultiRange[i] = new double[multiRange[i].length * GROWTH_FACTOR];
        arraycopy(multiDomain[i], 0, newMultiDomain[i], 0, multiLength[i]);
        arraycopy(multiRange[i], 0, newMultiRange[i], 0, multiLength[i]);
      }
      arraycopy(domain, 0, newdomain, 0, length);
      arraycopy(range, 0, newrange, 0, length);
      domain = newdomain;
      range = newrange;
      multiDomain = newMultiDomain;
      multiRange = newMultiRange;
    }
  }

  private static final int GROWTH_FACTOR = 2;

  private XYMultiresolution.XYStrategy strategy;

  public AppendableArrayXYDataset(String identifier, double[] domain,
      double[] range, String label, String axisId,
      XYMultiresolution.XYStrategy strategy) {
    super(identifier, domain, range, label, axisId, domain.length);
    this.strategy = strategy;
    genMultiresolution(strategy);
  }

  public AppendableArrayXYDataset(String identifier, double[][] mdomains,
      double[][] mranges, double rangeTop, double rangeBottom, String label,
      String axisId) {
    super(identifier, mdomains[0], mranges[0], label, axisId,
        mdomains[0].length);
    this.strategy = XYMultiresolution.MEAN_STRATEGY;
    this.multiDomain = mdomains;
    this.multiRange = mranges;
    this.rangeTop = rangeTop;
    this.rangeBottom = rangeBottom;
  }

  public AppendableArrayXYDataset(String s, double[] domainVal,
      double[] rangeVal, String s1, String s2) {
    this(s, domainVal, rangeVal, s1, s2, XYMultiresolution.MEAN_STRATEGY);
  }

  public void insertXY(double x, double y) {
    mutationList.add(Mutation.insert(x, y));
  }

  protected void processMutations() {
    MutableXYMultiresolution mxy = new MutableXYMultiresolution(domain, range,
        length, multiDomain, multiRange, multiLengths, rangeTop, rangeBottom);
    for (Iterator iterator = mutationList.iterator(); iterator.hasNext();) {
      Mutation mutation = (Mutation) iterator.next();
      if (mutation.type == Mutation.INSERT) {
        if (mutation.x > getX(getNumSamples() - 1)) {
          mxy.setDomainValue(length, mutation.x);
          mxy.setRangeValue(length, mutation.y);
          mxy.updateMultiresolution(length, 0, strategy);
          length++;
          modificationStart = Math.min(modificationStart, mutation.x);
          modificationEnd = Math.max(modificationEnd, mutation.x);
        } else {
          throw new IllegalArgumentException(
              "AppendableXYDataset only supports append operations");
        }
      }
    }
    this.domain = mxy.domain;
    this.range = mxy.range;
    this.multiDomain = mxy.multiDomain;
    this.multiRange = mxy.multiRange;
    this.multiLengths = mxy.multiLength;
    this.rangeTop = mxy.rangeTop;
    this.rangeBottom = mxy.rangeBottom;
  }
}
