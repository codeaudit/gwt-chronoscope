package org.timepedia.chronoscope.client.data;

import com.google.gwt.core.client.JavaScriptObject;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.MutableDataset;
import org.timepedia.chronoscope.client.data.Mutation.AppendMutation;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import java.util.ArrayList;
import java.util.List;

/**
 * Dataset that permits certain types of mutations (e.g. appending new data
 * points, modifying Y-value of existing data points). <p>
 *
 * @author Chad Takahashi
 * @see Mutation
 */
@ExportPackage("chronoscope")
public class MutableDatasetND<T extends Tuple2D> extends AbstractArrayDataset<T>
    implements MutableDataset<T>, Exportable {

  private MipMapStrategy mipMapStrategy;

  private List<DatasetListener<T>> listeners
      = new ArrayList<DatasetListener<T>>();

  public MutableDatasetND(DatasetRequest request) {
    super(request);
    mipMapStrategy = (MipMapStrategy) ArgChecker
        .isNotNull(request.getDefaultMipMapStrategy(),
            "request.mipMapStrategy");
  }

  public void addListener(DatasetListener<T> listener) {
    ArgChecker.isNotNull(listener, "listener");
    this.listeners.add(listener);
  }

  public void removeListener(DatasetListener<T> listener) {
    listeners.remove(listener);
  }

  public void mutate(Mutation mutation) {
    ArgChecker.isNotNull(mutation, "mutation");

    double newX, newY;

    if (mutation instanceof Mutation.AppendMutation) {
      AppendMutation m = (Mutation.AppendMutation) mutation;
      newY = m.getY();
      newX = m.getX();
      double newInterval = newX - getX(getNumSamples() - 1);
      this.minDomainInterval = Math.min(this.minDomainInterval, newInterval);
      appendXY(newX, newY);
    } else if (mutation instanceof Mutation.RangeMutation) {
      Mutation.RangeMutation m = (Mutation.RangeMutation) mutation;
      newY = m.getY();
      mipMapStrategy.setRangeValue(m.getPointIndex(), newY,
          mipMapChain.getMipMappedRangeTuples()[0]);
      newX = this.getX(m.getPointIndex());
    } else {
      // TODO: Can add more mutation handlers later
      throw new UnsupportedOperationException(
          "mutation of type " + mutation.getClass().getName()
              + " currently not supported");
    }

    //TODO: mutations currently only work for 2-tuples.  Need to add mutation support
    // for  n-tuples.
    rangeIntervals[0].expand(newY);

    notifyListeners(this, newX, newX);
  }

  public static native int len(JavaScriptObject len) /*-{
     return len.length;
  }-*/;

  public static native double get(JavaScriptObject len, int i) /*-{
     return len[i];
  }-*/;

  public static native double get2(JavaScriptObject len, int i, int j) /*-{
     return len[i][j];
  }-*/;

  @Export("mutate")
  public void mutateArray(JavaScriptObject jsDomain, JavaScriptObject jsRange) {
    int insertionPoint = 0;
    double domain[] = new double[len(jsDomain)];
    double range[][] = new double[len(jsRange)][];
    for (int j = 0; j < len(jsRange); j++) {
      range[j] = new double[domain.length];
    }

    for (int i = 0; i < domain.length; i++) {
      domain[i] = get(jsDomain, i);
      for(int j=0; j<range.length; j++) {
        range[j][i] = get2(jsRange, j, i);
      }
    }
    double firstDomainVal = domain[0];
    double lastDomainVal = domain[domain.length - 1];
    double[] oldDomain = this.mipMapChain.getMipMap(0).getDomain()
        .backingArray();
    if (lastDomainVal < oldDomain[0]) {
      // insert before beginning
      double newDomain[] = new double[oldDomain.length + domain.length];
      double newRange[][] = new double[this.mipMapChain.getRangeTupleSize()][];
      for (int i = 0; i < newRange.length; i++) {
        newRange[i] = new double[newDomain.length];
      }
      System.arraycopy(domain, 0, newDomain, 0, domain.length);
      System
          .arraycopy(oldDomain, 0, newDomain, domain.length, oldDomain.length);
      for (int i = 0; i < newRange.length; i++) {
        double oldRange[] = this.mipMapChain.getMipMap(0).getRange(i)
            .backingArray();
        System.arraycopy(range[i], 0, newRange[i], 0, range[i].length);
        System.arraycopy(oldRange, 0, newRange[i], domain.length,
            oldDomain.length);
      }
      DatasetRequest.Basic dr = new DatasetRequest.Basic();
      dr.setAxisId(getAxisId(0));
      dr.setDefaultMipMapStrategy(mipMapStrategy);
      dr.setDomain(newDomain);
      for (int d = 0; d < newRange.length; d++) {
        dr.addRangeTupleSlice(newRange[d]);
      }
      loadDataset(dr);
    } else if (firstDomainVal > oldDomain[oldDomain.length - 1]) {
      // insert after end
      double newDomain[] = new double[oldDomain.length + domain.length];
      double newRange[][] = new double[this.mipMapChain.getRangeTupleSize()][];
      for (int i = 0; i < newRange.length; i++) {
        newRange[i] = new double[newDomain.length];
      }
      System.arraycopy(oldDomain, 0, newDomain, 0, oldDomain.length);
      System.arraycopy(domain, 0, newDomain, oldDomain.length, domain.length);

      for (int i = 0; i < newRange.length; i++) {
        double oldRange[] = this.mipMapChain.getMipMap(0).getRange(i)
            .backingArray();
        System.arraycopy(oldRange, 0, newRange[i], 0, oldDomain.length);
        System.arraycopy(range[i], 0, newRange[i], oldDomain.length,
            range[i].length);
      }
      DatasetRequest.Basic dr = new DatasetRequest.Basic();
      dr.setAxisId(getAxisId(0));
      dr.setDefaultMipMapStrategy(mipMapStrategy);
      dr.setDomain(newDomain);
      for (int d = 0; d < newRange.length; d++) {
        dr.addRangeTupleSlice(newRange[d]);
      }
      loadDataset(dr);
    } else {
      // insert middle
      int newDomainSize = 0;
      int j = 0;
      for (int i = 0; i < domain.length; i++) {
        while (j < oldDomain.length && domain[i] > oldDomain[j]) {
          j++;
        }
        if (j >= oldDomain.length || domain[i] != oldDomain[j]) {
          newDomainSize++;
        }
      }
      double newDomain[] = new double[oldDomain.length + newDomainSize];
      double newRange[][] = new double[this.mipMapChain.getRangeTupleSize()][];
      double oldRange[][] = new double[newRange.length][];
      for (int i = 0; i < newRange.length; i++) {
        newRange[i] = new double[newDomain.length];
        oldRange[i] = this.mipMapChain.getMipMap(0).getRange(i).backingArray();
      }
      j = 0;
      int k = 0;
      for (int i = 0; i < domain.length; i++) {
        while (j < oldDomain.length && domain[i] > oldDomain[j]) {
          newDomain[k] = oldDomain[j];
          for (int d = 0; d < newRange.length; d++) {
            newRange[d][k] = oldRange[d][j];
          }
          j++;
          k++;
        }
        newDomain[k] = domain[i];
        for (int d = 0; d < newRange.length; d++) {
          newRange[d][k] = range[d][i];
        }
        if (domain[i] == oldDomain[j]) {
          j++;
        }
        k++;
      }
      while (j < oldDomain.length) {
        newDomain[k] = oldDomain[j];
        for (int d = 0; d < newRange.length; d++) {
          newRange[d][k] = oldRange[d][j];
        }
        j++;
        k++;
      }
      DatasetRequest.Basic dr = new DatasetRequest.Basic();
      dr.setAxisId(getAxisId(0));
      dr.setDefaultMipMapStrategy(mipMapStrategy);
      dr.setDomain(newDomain);
      for (int d = 0; d < newRange.length; d++) {
        dr.addRangeTupleSlice(newRange[d]);
      }
      loadDataset(dr);
    }

    notifyListeners(this, domain[0], domain[domain.length - 1]);
  }

  private void appendXY(double x, double y) {
    if (x <= getDomainExtrema().getEnd()) {
      throw new IllegalArgumentException(
          "Insertions not allowed; x was <= domainEnd: " + x + ":"
              + getDomainExtrema().getEnd());
    }

    mipMapStrategy.appendXY(x, y, mipMapChain);
  }

  private void notifyListeners(Dataset<T> ds, double domainStart,
      double domainEnd) {
    for (DatasetListener<T> l : this.listeners) {
      l.onDatasetChanged(ds, domainStart, domainEnd);
    }
  }
}
