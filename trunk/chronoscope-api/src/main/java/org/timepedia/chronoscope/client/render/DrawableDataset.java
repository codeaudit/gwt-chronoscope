package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.data.MipMap;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;

/**
 * Represents a dataset along with all the associated information needed to 
 * render it.
 * 
 * @author chad takahashi
 */
public class DrawableDataset<T extends Tuple2D> {
  
  /**
   * The renderer responsible for drawing the dataset.
   */
  public DatasetRenderer<T> getRenderer() {
    return this.renderer;
  }
  
  /**
   * Sets all associated objects to null and all primitive number vales to -1.
   */
  public void invalidate() {
    this.dataset = null;
    this.renderer = null;
    this.currMipLevel = -1;
    this.maxDrawablePoints = -1;
    this.visDomainEndIndex = -1;
    this.visDomainStartIndex = -1;
  }
  
  /**
   * Sets the most recent mip level used by {@link org.timepedia.chronoscope.client.plot.DefaultXYPlot} to draw
   * this dataset.
   */
  public void setCurrMipLevel(int mipLevel) {
    if (mipLevel != this.currMipLevel) {
      this.currMipLevel = mipLevel;
      this.currMipMap = this.dataset.getMipMapChain().getMipMap(mipLevel);
    }
  }
  
  void setRenderer(DatasetRenderer<T> renderer) {
    this.renderer = renderer;
  }
  
  /**
   * The dataset model to be rendered
   */
  public Dataset<T> dataset;
  
  private DatasetRenderer<T> renderer;
  
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
   * Returns the {@link MipMap} that's currently being used by the active
   * {@link #renderer}. 
   */
  public MipMap currMipMap;
  
  /**
   * The most recent mip level used to render this dataset.
   */
  private int currMipLevel = -1;

}
