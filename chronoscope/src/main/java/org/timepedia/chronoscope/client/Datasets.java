package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.data.DatasetListener;
import org.timepedia.chronoscope.client.data.MutableDataset2D;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.util.ArgChecker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Container for {@link Dataset} objects that provides indexed access to the
 * datasets as well as maintaining aggregate information. <p> This container
 * registers itself as a {@link DatasetListener} to any added {@link
 * MutableDataset2D} objects in order to guarantee that aggregate information is
 * kept up-to-date whenever constituent elements are modified.
 *
 * @author chad takahashi
 */
public final class Datasets<T extends Tuple2D> implements Iterable<Dataset<T>> {

  private double minInterval = Double.POSITIVE_INFINITY;

  private double minDomain = Double.POSITIVE_INFINITY, maxDomain
      = Double.NEGATIVE_INFINITY;

  private List<Dataset<T>> datasets = new ArrayList<Dataset<T>>();

  private List<DatasetListener<T>> listeners
      = new ArrayList<DatasetListener<T>>();

  private PrivateDatasetListener<T> myDatasetListener;

  /**
   * Constructs an empty dataset container.
   */
  public Datasets() {
    this.myDatasetListener = new PrivateDatasetListener<T>(this);
  }

  /**
   * Constructs a {@link Datasets} from the specified array of datasets.
   */
  public Datasets(Dataset<T>[] datasets) {
    ArgChecker.isNotNull(datasets, "datasets");
    this.myDatasetListener = new PrivateDatasetListener<T>(this);
    for (Dataset<T> dataset : datasets) {
      add(dataset);
    }
  }

  /**
   * Adds the specified dataset to the existing datasets in this container.
   */
  public void add(Dataset<T> dataset) {
    ArgChecker.isNotNull(dataset, "dataset");
    datasets.add(dataset);
    if (dataset instanceof MutableDataset) {
      ((MutableDataset) dataset).addListener(this.myDatasetListener);
    }

    updateAggregateInfo(dataset);
    this.myDatasetListener.onDatasetAdded(dataset);
  }

  /**
   * Registers listeners who wish to be notified of changes to this container as
   * well as its elements.
   */
  public void addListener(DatasetListener<T> listener) {
    ArgChecker.isNotNull(listener, "listener");
    this.listeners.add(listener);
  }

  /**
   * Returns the 0-based index of the specified dataset, or -1 if it is not in
   * this container.
   */
  public int indexOf(Dataset<T> dataset) {
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
  public Dataset<T> get(int index) {
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
   * Returns the minimum value of {@link Dataset#getApproximateMinimumInterval()}
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
  public Iterator<Dataset<T>> iterator() {
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
  public Dataset<T> remove(int index) {
    verifyDatasetNotEmpty();
    Dataset<T> removedDataset = datasets.remove(index);
    recalcAggregateInfo();
    if (removedDataset instanceof MutableDataset) {
      ((MutableDataset) removedDataset).removeListener(myDatasetListener);
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
   * Returns the contained dataset elements as an array of {@link Dataset}.
   */
  public Dataset<T>[] toArray() {
    return (Dataset<T>[]) this.datasets.toArray(new Dataset[0]);
  }

  private void recalcAggregateInfo() {
    minDomain = Double.POSITIVE_INFINITY;
    maxDomain = Double.NEGATIVE_INFINITY;
    minInterval = Double.POSITIVE_INFINITY;

    for (Dataset<T> ds : datasets) {
      updateAggregateInfo(ds);
    }
  }

  private void updateAggregateInfo(Dataset<T> dataset) {
    minDomain = Math.min(minDomain, dataset.getMinValue(0));
    maxDomain = Math.max(maxDomain, dataset.getMaxValue(0));
    minInterval = Math
        .min(minInterval, dataset.getApproximateMinimumInterval());
  }

  private void verifyDatasetNotEmpty() {
    if (this.datasets.isEmpty()) {
      throw new IllegalStateException(
          "method call not valid for empty container");
    }
  }

  private static final class PrivateDatasetListener<S extends Tuple2D>
      implements DatasetListener<S> {

    private Datasets<S> datasets;

    public PrivateDatasetListener(Datasets<S> datasets) {
      this.datasets = datasets;
    }

    public void onDatasetAdded(Dataset<S> dataset) {
      // forward event to external listeners
      for (DatasetListener<S> l : this.datasets.listeners) {
        l.onDatasetAdded(dataset);
      }
    }

    public void onDatasetChanged(Dataset<S> dataset, double domainStart,
        double domainEnd) {
      // update aggregate stats as changes to this dataset may have
      // affected them.
      this.datasets.updateAggregateInfo(dataset);

      // forward event to external listeners
      for (DatasetListener<S> l : this.datasets.listeners) {
        l.onDatasetChanged(dataset, domainStart, domainEnd);
      }
    }

    public void onDatasetRemoved(Dataset<S> dataset, int datasetIndex) {
      // forward event to external listeners
      for (DatasetListener<S> l : this.datasets.listeners) {
        l.onDatasetRemoved(dataset, datasetIndex);
      }
    }
  }
}
