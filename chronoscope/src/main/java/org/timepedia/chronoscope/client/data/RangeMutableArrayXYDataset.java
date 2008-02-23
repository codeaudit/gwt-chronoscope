package org.timepedia.chronoscope.client.data;

import java.util.Iterator;

/**
 * An implementation of RangeMutableXYDataset using Arrays.
 */
public class RangeMutableArrayXYDataset extends AppendableArrayXYDataset
    implements RangeMutableXYDataset {

  public RangeMutableArrayXYDataset(String identifier, double[] domain,
      double[] range, String label, String axisId,
      XYMultiresolution.XYStrategy strategy) {
    super(identifier, domain, range, label, axisId, strategy);
  }

  public RangeMutableArrayXYDataset(String identifier, double[][] mdomains,
      double[][] mranges, double rangeTop, double rangeBottom, String label,
      String axisId) {
    super(identifier, mdomains, mranges, rangeTop, rangeBottom, label, axisId);
  }

  public RangeMutableArrayXYDataset(String s, double[] domainVal,
      double[] rangeVal, String s1, String s2) {
    super(s, domainVal, rangeVal, s1, s2);
  }

  public void setY(int index, double y) {
    if (!updating) {
      throw new IllegalStateException(
          "setY() cannot be called before beginUpdate()");
    }

    mutationList.add(Mutation.setRange(index, y));
  }

  protected void processMutations() {
    super.processMutations();
    MutableXYMultiresolution mxy = getMutableXYMultiresolution();
    for (Iterator iterator = mutationList.iterator(); iterator.hasNext();) {
      Mutation mutation = (Mutation) iterator.next();
      if (mutation.type == Mutation.SETRANGE) {
        if (mutation.index < getNumSamples()) {
          mxy.setRangeValue(mutation.index, mutation.y);
          mxy.updateMultiresolution(mutation.index, 0, strategy);
          modificationStart = Math.min(modificationStart, getX(mutation.index));
          modificationEnd = Math.max(modificationEnd, getX(mutation.index));
          iterator.remove();
        } else {
          throw new IllegalArgumentException(
              "RangeMutableArrayXYDataset: index of set range operation "
                  + mutation.index + " exceeds number of samples in dataset "
                  + getNumSamples());
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
