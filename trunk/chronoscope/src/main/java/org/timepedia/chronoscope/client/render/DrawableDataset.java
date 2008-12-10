package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.data.MipMap;

/**
 * Represents a dataset along with all the associated information needed to 
 * render it.
 * 
 * @author chad takahashi
 */
public class DrawableDataset {
  
  public void setCurrMipLevel(int mipLevel) {
    if (mipLevel != this.currMipLevel) {
      this.currMipLevel = mipLevel;
      this.currMipMap = this.dataset.getMipMapChain().getMipMap(mipLevel);
    }
  }
  
  /**
   * The dataset model to be rendered
   */
  public Dataset dataset;
  
  /**
   * The renderer responsible for drawing the dataset.
   */
  public DatasetRenderer renderer;
  
  /**
   * The maximum number of domain points that this dataset's associated renderer
   * is capable of handling.
   */
  public int maxDrawablePoints;
  
  /**
   * Stores the start and end data point indices that are currently visible in
   * the plot.
   */
  public int visDomainStartIndex, visDomainEndIndex;
  
  /**
   * Stores the min and max range values within the sub-domain of this dataset
   * corresponding to the currently-visible plot.
   */
  public double visRangeMin, visRangeMax;
  
  /**
   * Returns the {@link MipMap} that's currently being used by the active
   * {@link #renderer}. 
   */
  public MipMap currMipMap;
  
  /**
   * The most recent mip level used to render this dataset.
   */
  private int currMipLevel = -1;

}
