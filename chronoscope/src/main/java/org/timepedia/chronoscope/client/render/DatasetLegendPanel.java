package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.util.ArgChecker;

/**
 * UI panel containing the dataset legend (a colored line followed by the 
 * dataset's name).
 * 
 * @author Chad Takahashi
 */
public class DatasetLegendPanel extends AbstractPanel {
  
  // Dictates the X-padding between a given legend icon and its
  //associated dataset name
  private static final double LEGEND_ICON_PAD = 2;
  
  // Dictates the X-padding between each dataset legend item
  static final int DATASET_LEGEND_PAD = 22;

  private double lblHeight;
  private double[] maxLabelWidths;
  private XYPlot plot;
  
  public void init(Layer layer) {
    ArgChecker.isNotNull(plot, "plot");
    ArgChecker.isNotNull(gssProperties, "gssProperties");
    
    lblHeight = this.calcHeight("X", layer);
    
    // TODO: might make more sense for container to set this panel's width
    this.width = layer.getWidth(); 
    
    this.maxLabelWidths = calcInitialLabelWidths(plot, layer);
    
    Bounds b = new Bounds();
    draw(layer, true, b);
    this.height = b.height;
  }
  
  public void setPlot(XYPlot plot) {
    this.plot = plot;
  }
  
  public void draw(Layer layer) {
    draw(layer, false, null);
  }
  
  public void resizeToIdealWidth() {
    throw new UnsupportedOperationException();
  }
  
  public void resizeToMinimalWidth() {
    throw new UnsupportedOperationException();
  }

  private void draw(Layer layer, boolean onlyCalcSize, Bounds b) {
    double xCursor = this.x;
    double yCursor = this.y;
    
    for (int i = 0; i < plot.getDatasets().size(); i++) {
      double lblWidth = drawLegendLabel(xCursor, yCursor, layer, i, onlyCalcSize);
      boolean enoughRoomInCurrentRow = (lblWidth >= 0);

      if (enoughRoomInCurrentRow) {
        xCursor += lblWidth;
      } else {
        xCursor = this.x;
        yCursor += lblHeight;
        xCursor += drawLegendLabel(xCursor, yCursor, layer, i, onlyCalcSize);
      }
      
      xCursor +=  DATASET_LEGEND_PAD;
    }
    
    if (b != null) {
      b.x = this.x;
      b.y = this.y;
      b.width = xCursor - b.x;
      // Note: since the (x,y) coordinate refers to the upper-left corner of the
      // bounds, we need to add 'lblHeight' to the final yCursor value to obtain
      // the total height of all legend item rows.
      b.height = yCursor - b.y + lblHeight; 
    }
  }

  /**
   * Draws a single legend label (consists of legend icon and text label).
   * 
   * @param lblX - x coordinate of label
   * @param lblY - y coordinate of label
   * @param layer - the graphics layer on which this artifact will be drawn
   * @param seriesNum - corresponding the dataset index 
   * @param onlyCalcWidth - if true, only width is calculated; otherwise the 
   *            label is actually drawn as well.
   *            
   * @return the full width of the label, or -1 if the label was too wide to fit
   * in the panel given the specified lblX and lblY coordinates.
   */
  private double drawLegendLabel(double lblX, double lblY, Layer layer, int seriesNum, boolean onlyCalcWidth) {
    XYRenderer renderer = plot.getRenderer(seriesNum);
    
    int hoverPoint = plot.getHoverPoints()[seriesNum];
    String seriesLabel = createDatasetLabel(plot, seriesNum, hoverPoint);
    
    // Compute the width of the dataset text label, taking into account historical
    // widths of this label.
    double txtWidth = calcWidth(seriesLabel, layer);
    if (txtWidth > maxLabelWidths[seriesNum]) {
      maxLabelWidths[seriesNum] = txtWidth;
    }
    else {
      txtWidth = maxLabelWidths[seriesNum];
    }
    
    double iconWidth = renderer.calcLegendIconWidth(plot);
    double totalWidth = txtWidth + LEGEND_ICON_PAD + iconWidth;
    
    if (lblX + totalWidth >= this.x + this.width) {
      return -1;
    }
    
    if (!onlyCalcWidth) {
      renderer.drawLegendIcon(plot, layer, lblX, lblY + lblHeight / 2, seriesNum);
  
      layer.setStrokeColor(gssProperties.color);
      layer.drawText(lblX + iconWidth + LEGEND_ICON_PAD, lblY, seriesLabel, gssProperties.fontFamily,
          gssProperties.fontWeight, gssProperties.fontSize, textLayerName,
          Cursor.DEFAULT);
    }
    
    return totalWidth;
  }
  
  /**
   * Calculates the width of the dataset label for the median point of 
   * each dataset in the plot and returns the resulting array of 
   * label widths.
   */
  private double[] calcInitialLabelWidths(XYPlot plot, Layer layer) {
    double[] estMaxWidths = new double[plot.getDatasets().size()];
    for (int i = 0; i < estMaxWidths.length; i++) {
      int medianIdx = plot.getDataset(i).getNumSamples() >> 1;
      String lbl = createDatasetLabel(plot, i, medianIdx);
      estMaxWidths[i] = this.calcWidth(lbl, layer);
    }
    
    return estMaxWidths;
  }
  
  /**
   * Generates the dataset label for a given point on a dataset.  The point 
   * index is needed in order to determine the range value to be displayed
   * for hovered data points. If pointIdx == -1, then the range value is 
   * omitted.
   */
  private String createDatasetLabel(XYPlot plot, int datasetIdx, int pointIdx) {
    String lbl = plot.getSeriesLabel(datasetIdx);
    final boolean doShowRangeValue = (pointIdx > -1);
    if (doShowRangeValue) {
      double yData = plot.getDataY(datasetIdx, pointIdx);
      lbl += " (" + plot.getRangeAxis(datasetIdx).getFormattedLabel(yData) + ")";
    }
    return lbl;
  }
}
