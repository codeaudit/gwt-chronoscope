package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.data.MutableXYDataset;
import org.timepedia.chronoscope.client.data.XYDatasetListener;
import org.timepedia.chronoscope.client.util.ArgChecker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Container for {@link XYDataset} objects that provides indexed access to the
 * datasets as well as maintaining aggregate information. <p> This container
 * registers itself as a {@link XYDatasetListener} to any added {@link
 * MutableXYDataset} objects in order to guarantee that aggregate information is
 * kept up-to-date whenever constituent elements are modified.
 *
 * @author chad takahashi
 */
public final class XYDatasets<T extends XYDataset> implements Iterable<T> {

  private double minInterval = Double.POSITIVE_INFINITY;

  private double minDomain = Double.POSITIVE_INFINITY, maxDomain
      = Double.NEGATIVE_INFINITY;

  private double minRange = Double.POSITIVE_INFINITY, maxRange
      = Double.NEGATIVE_INFINITY;

  private List<T> datasets = new ArrayList<T>();

  private List<XYDatasetListener<T>> listeners
      = new ArrayList<XYDatasetListener<T>>();

  private DatasetListener<T> myDatasetListener;

  /**
   * Constructs an empty dataset container.
   */
  public XYDatasets() {
    this.myDatasetListener = new DatasetListener<T>(this);
  }

  /**
   * Constructs a {@link XYDatasets} from the specified array of datasets.
   */
  public XYDatasets(T[] datasets) {
    ArgChecker.isNotNull(datasets, "datasets");
    this.myDatasetListener = new DatasetListener<T>(this);
    for (T dataset : datasets) {
      add(dataset);
    }
  }

  /**
   * Adds the specified dataset to the existing datasets in this container.
   */
  public void add(T dataset) {
    ArgChecker.isNotNull(dataset, "dataset");
    datasets.add(dataset);
    if (dataset instanceof MutableXYDataset) {
      ((MutableXYDataset) dataset).addListener(this.myDatasetListener);
    }

    updateAggregateInfo(dataset);
    this.myDatasetListener.onDatasetAdded(dataset);
  }

  /**
   * Registers listeners who wish to be notified of changes to this container as
   * well as its elements.
   */
  public void addListener(XYDatasetListener<T> listener) {
    ArgChecker.isNotNull(listener, "listener");
    this.listeners.add(listener);
  }

  /**
   * Returns the 0-based index of the specified dataset, or -1 if it is not in
   * this container.
   */
  public int indexOf(T dataset) {
    ArgChecker.isNotNull(dataset, "dataset");
    for (int i = 0; i < datasets.size(); i++) {
      if (datasets.get(i) == dataset) {
        return i;
      }
    }

    return -1;
  }

  /**
   * Returns the dataset at the specified 0-based index within this container.
   */
  public T get(int index) {
    return this.datasets.get(index);
  }

  /**
   * Returns the minimum domain value across all contained datasets.
   */
  public double getMinDomain() {
    verifyDatasetNotEmpty();
    return this.minDomain;
  }

  /**
   * Returns the minimum value of {@link XYDataset#getApproximateMinimumInterval()}
   * across all datasets in this container.
   */
  public double getMinInterval() {
    verifyDatasetNotEmpty();
    return this.minInterval;
  }

  /**
   * Returns the maximum domain value across all contained datasets.
   */
  public double getMaxDomain() {
    verifyDatasetNotEmpty();
    return this.maxDomain;
  }

  /**
   * Returns the minimum range value across all contained datasets.
   */
  public double getMinRange() {
    verifyDatasetNotEmpty();
    return this.minRange;
  }

  /**
   * Returns the maximum range value across all contained datasets.
   */
  public double getMaxRange() {
    verifyDatasetNotEmpty();
    return this.maxRange;
  }

  /**
   * Returns true if this container has 0 elements.
   */
  public boolean isEmpty() {
    return size() == 0;
  }

  /**
   * Iterator over the dataset elements of this container.
   */
  public Iterator<T> iterator() {
    return this.datasets.iterator();
  }

  /**
   * Removes the element at the specified index in this container. Shifts any
   * subsequent elements to the left (subtracts one from their indices). Returns
   * the element that was removed from the container. <p> Be aware that this
   * container de-registers itself as an {@link XYDatasetListener} to the
   * dataset being removed.  In other words, mutations applied to a dataset that
   * once belonged to this container will no longer signal this container, which
   * in turn, will no longer forward the mutation event to its listeners.
   */
  public T remove(int index) {
    verifyDatasetNotEmpty();
    T removedDataset = datasets.remove(index);
    recalcAggregateInfo();
    if (removedDataset instanceof MutableXYDataset) {
      ((MutableXYDataset) removedDataset).removeListener(myDatasetListener);
    }
    myDatasetListener.onDatasetRemoved(removedDataset);
    return removedDataset;
  }

  /**
   * Returns the number of datasets in this container.
   */
  public int size() {
    return this.datasets.size();
  }

  /**
   * Returns the contained dataset elements as an array of {@link XYDataset}.
   */
  public T[] toArray() {
    return (T[]) this.datasets.toArray(new XYDataset[0]);
  }

  private void recalcAggregateInfo() {
    minDomain = Double.POSITIVE_INFINITY;
    minRange = Double.POSITIVE_INFINITY;
    maxDomain = Double.NEGATIVE_INFINITY;
    maxRange = Double.NEGATIVE_INFINITY;
    minInterval = Double.POSITIVE_INFINITY;

    for (T ds : datasets) {
      updateAggregateInfo(ds);
    }
  }

  private void updateAggregateInfo(XYDataset dataset) {
    minDomain = Math.min(minDomain, dataset.getDomainBegin());
    maxDomain = Math.max(maxDomain, dataset.getDomainEnd());
    minRange = Math.min(minRange, dataset.getRangeBottom());
    maxRange = Math.max(maxRange, dataset.getRangeTop());
    minInterval = Math
        .min(minInterval, dataset.getApproximateMinimumInterval());
  }

  private void verifyDatasetNotEmpty() {
    if (this.datasets.isEmpty()) {
      throw new IllegalStateException(
          "method call not valid for empty container");
    }
  }

  private static final class DatasetListener<T extends XYDataset>
      implements XYDatasetListener<T> {

    private XYDatasets<T> datasets;

    public DatasetListener(XYDatasets<T> datasets) {
      this.datasets = datasets;
    }

    public void onDatasetAdded(T dataset) {
      // forward event to external listeners
      for (XYDatasetListener<T> l : this.datasets.listeners) {
        l.onDatasetAdded(dataset);
      }
    }

    public void onDatasetChanged(T dataset, double domainStart,
        double domainEnd) {
      // update aggregate stats as changes to this dataset may have
      // affected them.
      this.datasets.updateAggregateInfo(dataset);

      // forward event to external listeners
      for (XYDatasetListener<T> l : this.datasets.listeners) {
        l.onDatasetChanged(dataset, domainStart, domainEnd);
      }
    }

    public void onDatasetRemoved(T dataset) {
      // forward event to external listeners
      for (XYDatasetListener<T> l : this.datasets.listeners) {
        l.onDatasetRemoved(dataset);
      }
    }
  }
}
