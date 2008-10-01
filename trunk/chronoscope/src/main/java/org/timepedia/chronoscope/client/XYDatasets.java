package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.data.MutableXYDataset;
import org.timepedia.chronoscope.client.data.XYDatasetListener;
import org.timepedia.chronoscope.client.util.ArgChecker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Container for {@link XYDataset} objects that provides indexed access to the 
 * datasets as well as maintaining aggregate information.
 * <p>
 * This container registers itself as a {@link XYDatasetListener} to any added
 * {@link MutableXYDataset} objects in order to guarantee that aggregate 
 * information is kept up-to-date whenever constituent elements are modified.
 * 
 * @author chad takahashi
 */
public final class XYDatasets implements XYDatasetListener, Iterable<XYDataset> {
  private double minInterval;
  private double minDomain = Double.POSITIVE_INFINITY, maxDomain = Double.NEGATIVE_INFINITY;
  private double minRange = Double.POSITIVE_INFINITY, maxRange = Double.NEGATIVE_INFINITY;
  private List<XYDataset> datasets = new ArrayList<XYDataset>();
  
  /**
   * Constructs a container having a single dataset.
   */
  public XYDatasets(XYDataset dataset) {
    this(new XYDataset[] {dataset});
  }
  
  /**
   * Constructs a {@link XYDatasets} from the specified array of datasets.
   */
  public XYDatasets(XYDataset[] datasets) {
    ArgChecker.isNotNull(datasets, "datasets");
    for (XYDataset dataset : datasets) {
      addDataset(dataset);
    }
  }
  
  /**
   * Adds the specified dataset to the existing datasets in this container.
   */
  public void addDataset(XYDataset dataset) {
    ArgChecker.isNotNull(dataset, "dataset");
    datasets.add(dataset);
    if (dataset instanceof MutableXYDataset) {
      ((MutableXYDataset)dataset).addXYDatasetListener(this);
    }
    
    updateAggregateInfo(dataset);
  }
  
  /**
   * Returns the 0-based index of the specified dataset, or -1 if it
   * is not in this container.
   */
  public int indexOf(XYDataset dataset) {
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
  public XYDataset get(int index) {
    return this.datasets.get(index);
  }
  
  /**
   * Returns the minimum domain value across all contained datasets.
   */
  public double getMinDomain() {
    return this.minDomain;
  }
  
  /**
   * Returns the maximum domain value across all contained datasets.
   */
  public double getMaxDomain() {
    return this.maxDomain;
  }
  
  /**
   * Returns the minimum range value across all contained datasets.
   */
  public double getMinRange() {
    return this.minRange;
  }
  
  /**
   * Returns the maximum range value across all contained datasets.
   */
  public double getMaxRange() {
    return this.maxRange;
  }

  public Iterator<XYDataset> iterator() {
    return this.datasets.iterator();
  }

  public void onDatasetChanged(XYDataset dataset, double domainStart,
      double domainEnd) {
    
    updateAggregateInfo(dataset);
  }
  
  /**
   * Returns the number of datasets in this container.
   */
  public int size() {
    return this.datasets.size();
  }
  
  private void updateAggregateInfo(XYDataset dataset) {
    minDomain = Math.min(minDomain, dataset.getDomainBegin());
    maxDomain = Math.max(maxDomain, dataset.getDomainEnd());
    minRange = Math.min(minRange, dataset.getRangeBottom());
    maxRange = Math.max(maxRange, dataset.getRangeTop());
    //minInterval = ??;
    
    //System.out.println("TESTING: new rangeBottom = " + rangeBottom);
  }
}
