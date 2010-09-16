package org.timepedia.chronoscope.client;

import com.google.gwt.user.client.Command;

import org.timepedia.chronoscope.client.data.AbstractDataset;
import org.timepedia.chronoscope.client.data.DatasetListener;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Container for {@link Dataset} objects that provides indexed access to the
 * datasets as well as maintaining aggregate information. <p> This container
 * registers itself as a {@link DatasetListener} to any added {@link
 * org.timepedia.chronoscope.client.data.MutableDatasetND} objects in order to guarantee that aggregate information is
 * kept up-to-date whenever constituent elements are modified.
 *
 * @author chad takahashi
 */
@ExportPackage("chronoscope")
public final class Datasets<T extends Tuple2D>
    implements Iterable<Dataset<T>>, Exportable {

  private double minInterval = Double.POSITIVE_INFINITY;

  private double minDomain = Double.POSITIVE_INFINITY, maxDomain
      = Double.NEGATIVE_INFINITY;

  private List<Dataset<T>> datasets = new ArrayList<Dataset<T>>();

  private List<DatasetListener<T>> listeners
      = new ArrayList<DatasetListener<T>>();

  private PrivateDatasetListener<T> myDatasetListener;

  private int mutating = 0;

  private List<Command> pending = new ArrayList<Command>();

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
  @Export
  public void add(Dataset<T> dataset) {
    addPrivate(dataset);
    this.myDatasetListener.onDatasetAdded(dataset);
  }

  /**
   * Adds the specified dataset to the existing datasets in this container
   * without firing events.
   */
  public void addPrivate(Dataset<T> dataset) {
    ArgChecker.isNotNull(dataset, "dataset");
    datasets.add(dataset);
    ((AbstractDataset)dataset).setDatasets(this);
    if (dataset instanceof MutableDataset) {
      ((MutableDataset) dataset).addListener(this.myDatasetListener);
    }
    updateAggregateInfo(dataset);
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
   * Suspends all firing up dataset events until corresponding endMutation() as
   * well as allows more efficient processing of multiple updates.
   */
  @Export
  public void beginMutation() {
    mutating++;
  }

  /**
   * Disables mutation mode and fires all pending change events.
   */
  @Export
  public void endMutation() {
    mutating--;
    if (mutating == 0) {
      firePendingEvents();
    }
  }


  private void firePendingEvents() {
    for (Command c : pending) {
      c.execute();
    }
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
  @Export
  public Dataset<T> get(int index) {
    return this.datasets.get(index);
  }

  /**
   * Returns the dataset with the specified identifier.
   */
  @Export
  public Dataset<T> getById(String id) {
    for (Dataset<T> d : datasets) {
      if (id.equals(d.getIdentifier())) {
        return d;
      }
    }
    return null;
  }

  /**
   * Returns an interval that contains the minimum and maximum domain value
   * across all {@link Dataset} elements within this container.
   */
  @Export
  public Interval getDomainExtrema() {
    verifyDatasetNotEmpty();
    return new Interval(this.minDomain, this.maxDomain);
  }

  /**
   * Returns the minimum value of {@link Dataset#getMinDomainInterval()} across
   * all datasets in this container.
   */
  public double getMinInterval() {
    verifyDatasetNotEmpty();
    return this.minInterval;
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
   * container de-registers itself as an {@link DatasetListener} to the dataset
   * being removed.  In other words, mutations applied to a dataset that once
   * belonged to this container will no longer signal this container, which in
   * turn, will no longer forward the mutation event to its listeners.
   */
  @Export
  public Dataset<T> remove(int index) {
    Dataset<T> removedDataset = removePrivate(index);
    myDatasetListener.onDatasetRemoved(removedDataset, index);
    return removedDataset;
  }

  /**
   * Remove a dataset without firing listeners.
   */
  public Dataset<T> removePrivate(int index) {
    verifyDatasetNotEmpty();
    Dataset<T> removedDataset = datasets.remove(index);
    recalcAggregateInfo();
    if (removedDataset instanceof MutableDataset) {
      ((MutableDataset) removedDataset).removeListener(myDatasetListener);
    }
    return removedDataset;
  }

  /**
   * Returns the number of datasets in this container.
   */
  @Export
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
    minDomain = MathUtil.min(minDomain, dataset.getDomainExtrema().getStart());
    maxDomain = MathUtil.max(maxDomain, dataset.getDomainExtrema().getEnd());
    minInterval = Math.min(minInterval, dataset.getMinDomainInterval());
  }

  private void verifyDatasetNotEmpty() {
    if (this.datasets.isEmpty()) {
      throw new IllegalStateException(
          "method call not valid for empty container");
    }
  }

  public void fireChanged(Dataset dataset, Interval region) {
     myDatasetListener.onDatasetChanged(dataset, region.getStart(), region.getEnd());
  }

  private abstract class ListenerCommand implements Command {

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }

            if ( ((ListenerCommand)obj).getName() == null || !((ListenerCommand)obj).getName().equals(getName())) {
                return false;
            }
            return true;
        }

        protected abstract String getName();

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + (this.getName() != null ? this.getName().hashCode() : 0);
            return hash;
        }

  }

  private static final String ON_DATASET_ADDED = "onDatasetAdded";
  private static final String ON_DATASET_CHANGED = "onDatasetChanged";
  private static final String ON_DATASET_REMOVED = "onDatasetRemoved";

  private final class PrivateDatasetListener<S extends Tuple2D>
      implements DatasetListener<S> {

    private Datasets<S> datasets;

    public PrivateDatasetListener(Datasets<S> datasets) {
      this.datasets = datasets;
    }

    public void onDatasetAdded(final Dataset<S> dataset) {
      // forward event to external listeners
      for (final DatasetListener<S> l : this.datasets.listeners) {
        Command c = new ListenerCommand() {

          protected String getName() {
             return ON_DATASET_ADDED;
          }

          @Override
          public void execute() {
            l.onDatasetAdded(dataset);
          }


        };
        if (mutating == 0) {
          c.execute();
        } else {
            replaceCommand(pending,c);
        }
      }
    }

   private void replaceCommand(List<Command> pending, Command cmd ){
        if (pending.contains(cmd)){
           pending.remove(cmd);
       }
       pending.add(cmd);
   }

    public void onDatasetChanged(final Dataset<S> dataset,
        final double domainStart, final double domainEnd) {
      // update aggregate stats as changes to this dataset may have
      // affected them.
      this.datasets.updateAggregateInfo(dataset);

      // forward event to external listeners
      for (final DatasetListener<S> l : this.datasets.listeners) {
        Command c = new ListenerCommand() {

          protected String getName(){
             return ON_DATASET_CHANGED;
          }

          @Override
          public void execute() {
            l.onDatasetChanged(dataset, domainStart, domainEnd);
          }
        };
        if (mutating == 0) {
          c.execute();
        } else {
            replaceCommand(pending,c);
        }
      }
    }

    public void onDatasetRemoved(final Dataset<S> dataset,
        final int datasetIndex) {
      // forward event to external listeners
      for (final DatasetListener<S> l : this.datasets.listeners) {
        Command c = new ListenerCommand() {

          protected String getName(){
             return ON_DATASET_REMOVED;
          }
          
          @Override
          public void execute() {
            l.onDatasetRemoved(dataset, datasetIndex);
          }
        };
        if (mutating == 0) {
          c.execute();
        } else {
           replaceCommand(pending,c);
        }
      }
    }
  }

  public String toJson() {
    String ret = "";
    for (int i = 0; i < datasets.size(); i++) {
      if (i>0) ret += ",";
      ret += datasets.get(i).toJson();
    }
    return "[" + ret + "]";
  }
  
  public String toString() {
    String ret = "";
    for (int i = 0; i < datasets.size(); i++) {
      if (i>0) ret += ",";
      ret += datasets.get(i).toString();
    }
    return ret;
  }
}
