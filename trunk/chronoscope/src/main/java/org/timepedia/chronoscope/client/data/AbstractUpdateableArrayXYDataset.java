package org.timepedia.chronoscope.client.data;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Base class for datasets wishing to provide the UpdateableXYDataset
 * functionality
 */
public abstract class AbstractUpdateableArrayXYDataset extends ArrayXYDataset
    implements UpdateableXYDataset {

  protected static class Mutation {

    public static final int SET = 0, INSERT = 1, REMOVE = 2;

    public static Mutation insert(double x, double y) {
      return new Mutation(INSERT, -1, x, y);
    }

    public static Mutation remove(int i) {
      return new Mutation(REMOVE, i, 0, 0);
    }

    public static Mutation set(int i, double x, double y) {
      return new Mutation(SET, i, x, y);
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

  private boolean updating;

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
      xyDatasetListener.onDatasetChanged(modificationStart, modificationEnd);
    }
  }
}
