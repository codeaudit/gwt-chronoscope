package org.timepedia.chronoscope.client.data;

import java.util.Iterator;

/**
 * An implementation of the AppendableXYDataset interface using arrays.
 * insertXY() and setY() where the X value is not changed or greater than the
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

  protected XYMultiresolution.XYStrategy strategy;

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
    if (!updating) {
      throw new IllegalStateException(
          "insertXY called without matching beginUpdate()");
    }
    mutationList.add(Mutation.insert(x, y));
  }

  protected void processMutations() {
    MutableXYMultiresolution mxy = getMutableXYMultiresolution();
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
          iterator.remove();
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

  protected MutableXYMultiresolution getMutableXYMultiresolution() {
    return new MutableXYMultiresolution(domain, range, length, multiDomain,
        multiRange, multiLengths, rangeTop, rangeBottom);
  }
}
