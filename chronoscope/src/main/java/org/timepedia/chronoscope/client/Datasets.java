package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.data.MutableXYDataset;
import org.timepedia.chronoscope.client.data.DatasetListener;
import org.timepedia.chronoscope.client.data.tuple.Tuple;
import org.timepedia.chronoscope.client.util.ArgChecker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Container for {@link Dataset} objects that provides indexed access to the
 * datasets as well as maintaining aggregate information. <p> This container
 * registers itself as a {@link DatasetListener} to any added {@link
 * MutableXYDataset} objects in order to guarantee that aggregate information is
 * kept up-to-date whenever constituent elements are modified.
 *
 * @author chad takahashi
 */
public final class Datasets<S extends Tuple, T extends Dataset<S>> implements Iterable<T> {

  private double minInterval = Double.POSITIVE_INFINITY;

  private double minDomain = Double.POSITIVE_INFINITY, maxDomain
      = Double.NEGATIVE_INFINITY;

  private List<T> datasets = new ArrayList<T>();

  private List<DatasetListener<S,T>> listeners
      = new ArrayList<DatasetListener<S,T>>();

  private PrivateDatasetListener<S,T> myDatasetListener;

  /**
   * Constructs an empty dataset container.
   */
  public Datasets() {
    this.myDatasetListener = new PrivateDatasetListener<S,T>(this);
  }

  /**
   * Constructs a {@link Datasets} from the specified array of datasets.
   */
  public Datasets(T[] datasets) {
    ArgChecker.isNotNull(datasets, "datasets");
    this.myDatasetListener = new PrivateDatasetListener<S,T>(this);
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
  public void addListener(DatasetListener<S,T> listener) {
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
   * container de-registers itself as an {@link DatasetListener} to the
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
    myDatasetListener.onDatasetRemoved(removedDataset, index);
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
    return (T[]) this.datasets.toArray(new Dataset[0]);
  }

  private void recalcAggregateInfo() {
    minDomain = Double.POSITIVE_INFINITY;
    maxDomain = Double.NEGATIVE_INFINITY;
    minInterval = Double.POSITIVE_INFINITY;

    for (T ds : datasets) {
      updateAggregateInfo(ds);
    }
  }

  private void updateAggregateInfo(Dataset<S> dataset) {
    // FIXME: Need to factor out this typecast.  Something's wrong with
    // this model.  Maybe this class should by XYDataset and only operate
    // on XYDataset elements?  Or some generic max() function on the 
    // Tuple? ...
    XYDataset xyDataset = (XYDataset)dataset;
    
    minDomain = Math.min(minDomain, xyDataset.getDomainBegin());
    maxDomain = Math.max(maxDomain, xyDataset.getDomainEnd());
    minInterval = Math
        .min(minInterval, dataset.getApproximateMinimumInterval());
  }

  private void verifyDatasetNotEmpty() {
    if (this.datasets.isEmpty()) {
      throw new IllegalStateException(
          "method call not valid for empty container");
    }
  }

  private static final class PrivateDatasetListener<S extends Tuple, T extends Dataset<S>>
      implements DatasetListener<S,T> {

    private Datasets<S,T> datasets;

    public PrivateDatasetListener(Datasets<S,T> datasets) {
      this.datasets = datasets;
    }

    public void onDatasetAdded(T dataset) {
      // forward event to external listeners
      for (DatasetListener<S,T> l : this.datasets.listeners) {
        l.onDatasetAdded(dataset);
      }
    }

    public void onDatasetChanged(T dataset, double domainStart,
        double domainEnd) {
      // update aggregate stats as changes to this dataset may have
      // affected them.
      this.datasets.updateAggregateInfo(dataset);

      // forward event to external listeners
      for (DatasetListener<S,T> l : this.datasets.listeners) {
        l.onDatasetChanged(dataset, domainStart, domainEnd);
      }
    }

    public void onDatasetRemoved(T dataset, int datasetIndex) {
      // forward event to external listeners
      for (DatasetListener<S,T> l : this.datasets.listeners) {
        l.onDatasetRemoved(dataset, datasetIndex);
      }
    }
  }
}
