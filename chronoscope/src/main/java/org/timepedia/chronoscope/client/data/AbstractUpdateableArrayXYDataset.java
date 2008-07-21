package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.util.Util;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Base class for datasets wishing to provide the UpdateableXYDataset
 * functionality
 */
public abstract class AbstractUpdateableArrayXYDataset extends ArrayXYDataset
    implements UpdateableXYDataset {

  static final int GROWTH_FACTOR = 2;

  protected static class Mutation {

    public static final int SETRANGE = 0, INSERT = 1, REMOVE = 2;

    public static Mutation insert(double x, double y) {
      return new Mutation(INSERT, -1, x, y);
    }

    public static Mutation remove(int i) {
      return new Mutation(REMOVE, i, 0, 0);
    }

    public static Mutation setRange(int i,double y) {
      return new Mutation(SETRANGE, i, 0, y);
    }

    public int index;

    public double x;

    public double y;

    public int type;

    public Mutation(int type, int i, double x, double y) {
      this.type = type;
      index = i;
      this.x = x;
      this.y = y;
    }
  }

  protected ArrayList mutationList = new ArrayList();

  protected double modificationStart, modificationEnd;

  private ArrayList listeners = new ArrayList();

  protected boolean updating;

  protected AbstractUpdateableArrayXYDataset(String identifier, double[] domain,
      double[] range, String label, String axisId) {
    super(identifier, domain, range, label, axisId);
  }

  protected AbstractUpdateableArrayXYDataset(String identifier, double[] domain,
      double[] range, String label, String axisId, int capacity) {
    super(identifier, domain, range, label, axisId, capacity);
  }

  protected AbstractUpdateableArrayXYDataset(String identifier, double[] domain,
      double[] range, String label, String axisId,
      XYMultiresolution.XYStrategy strategy) {
    super(identifier, domain, range, label, axisId, strategy);
  }

  protected AbstractUpdateableArrayXYDataset(String identifier,
      double[][] domains, double[][] ranges, double top, double bottom,
      String label, String axisId) {
    super(identifier, domains, ranges, top, bottom, label, axisId);
  }

  public void addXYDatasetListener(XYDatasetListener dataListener) {
    listeners.add(dataListener);
  }

  public void beginUpdate() {
    if (!updating) {
      updating = true;
      mutationList.clear();
    } else {
      throw new IllegalStateException(
          "Called beginUpdate() while alreadying updating (forgot to call endUpdate()?)");
    }
  }

  public void endUpdate() {
    if (updating) {
      updating = false;
      modificationStart = Double.MAX_VALUE;
      modificationEnd = Double.MIN_VALUE;
      processMutations();
      fireDatasetChangeListeners();
    } else {
      throw new IllegalStateException(
          "Called endUpdate() with no matching beginUpdate()");
    }
  }

  public void removeXYDatasetListener(XYDatasetListener dataListener) {
    listeners.remove(dataListener);
  }

  protected abstract void processMutations();

  private void fireDatasetChangeListeners() {
    for (Iterator iterator = listeners.iterator(); iterator.hasNext();) {
      XYDatasetListener xyDatasetListener = (XYDatasetListener) iterator.next();
      xyDatasetListener.onDatasetChanged(this, modificationStart, modificationEnd);
    }
  }

  static class MutableXYMultiresolution extends XYMultiresolution {

//        public static native void arraycopy(double[] src, int srcOfs, double[] dest, int destOfs, int len) /*-{
//             Array.prototype.splice.apply(dest, [destOfs, len].concat(src));
//
//        }-*/;

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
        Util.arraycopy(multiDomain[i], 0, newMultiDomain[i], 0, multiLength[i]);
        Util.arraycopy(multiRange[i], 0, newMultiRange[i], 0, multiLength[i]);
      }
      Util.arraycopy(domain, 0, newdomain, 0, length);
      Util.arraycopy(range, 0, newrange, 0, length);
      domain = newdomain;
      range = newrange;
      multiDomain = newMultiDomain;
      multiRange = newMultiRange;
    }
  }
}
