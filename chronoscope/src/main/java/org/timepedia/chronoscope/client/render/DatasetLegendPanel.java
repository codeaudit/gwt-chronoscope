package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
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
  private View view;
  
  public void init() {
    ArgChecker.isNotNull(plot, "plot");
    ArgChecker.isNotNull(view, "view");
    ArgChecker.isNotNull(gssProperties, "gssProperties");
    
    lblHeight = stringSizer.getHeight("X", gssProperties);
    
    this.maxLabelWidths = calcInitialLabelWidths(plot, layer);
    
    bounds.width = view.getWidth(); 
    
    Bounds b = calcBounds(layer, plot.getDatasets().size());
    this.bounds.height = b.height;
    this.bounds.width = view.getWidth();
  }
  
  public void setPlot(XYPlot plot) {
    this.plot = plot;
  }
  
  public void setView(View view) {
    this.view = view;
  }
  
  public void draw() {
    draw(layer, plot.getDatasets().size());
  }
  
  private Bounds calcBounds(Layer layer, int numDatasets) {
    Bounds b = new Bounds();
    draw(layer, numDatasets, b);
    return b;
  }
  
  private void draw(Layer layer, int numDatasets) {
    draw(layer, numDatasets, null);
  }
  
  private void draw(Layer layer, int numDatasets, Bounds b) {
    final boolean onlyCalcSize = (b != null);
    double xCursor = bounds.x;
    double yCursor = bounds.y;
    
    for (int i = 0; i < numDatasets; i++) {
      double lblWidth = drawLegendLabel(xCursor, yCursor, layer, i, onlyCalcSize);
      boolean enoughRoomInCurrentRow = (lblWidth >= 0);

      if (enoughRoomInCurrentRow) {
        xCursor += lblWidth;
      } else {
        xCursor = bounds.x;
        yCursor += lblHeight;
        xCursor += drawLegendLabel(xCursor, yCursor, layer, i, onlyCalcSize);
      }
      
      xCursor +=  DATASET_LEGEND_PAD;
    }
    
    if (b != null) {
      b.x = this.bounds.x;
      b.y = this.bounds.y;
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
   * @param datasetIdx - corresponding the dataset index 
   * @param onlyCalcWidth - if true, only width is calculated; otherwise the 
   *            label is actually drawn as well.
   *            
   * @return the full width of the label, or -1 if the label was too wide to fit
   * in the panel given the specified lblX and lblY coordinates.
   */
  private double drawLegendLabel(double lblX, double lblY, Layer layer, int datasetIdx, boolean onlyCalcWidth) {
    DatasetRenderer renderer = plot.getDatasetRenderer(datasetIdx);
    
    int hoverPoint = plot.getHoverPoints()[datasetIdx];
    String seriesLabel = createDatasetLabel(plot, datasetIdx, hoverPoint);
    
    // Compute the width of the dataset text label, taking into account historical
    // widths of this label.
    double txtWidth = stringSizer.getWidth(seriesLabel, gssProperties);
    if (txtWidth > maxLabelWidths[datasetIdx]) {
      maxLabelWidths[datasetIdx] = txtWidth;
    }
    else {
      txtWidth = maxLabelWidths[datasetIdx];
    }
    
    double iconWidth = renderer.calcLegendIconWidth(view);
    double totalWidth = txtWidth + LEGEND_ICON_PAD + iconWidth;
    
    if (lblX + totalWidth >= bounds.rightX()) {
      return -1;
    }
    
    if (!onlyCalcWidth) {
      renderer.drawLegendIcon(layer, lblX, lblY + lblHeight / 2);
  
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
    Datasets datasets = plot.getDatasets();
    double[] estMaxWidths = new double[datasets.size()];
    for (int i = 0; i < estMaxWidths.length; i++) {
      int medianIdx = datasets.get(i).getNumSamples() >> 1;
      String lbl = createDatasetLabel(plot, i, medianIdx);
      estMaxWidths[i] = stringSizer.getWidth(lbl, gssProperties);
    }
    
    return estMaxWidths;
  }
  
  /**
   * Generates the dataset label for a given point on a dataset.  The point 
   * index is needed in order to determine the range value to be displayed
   * for hovered data points. If pointIdx == -1, then the range value is 
   * omitted.
   */
  private static String createDatasetLabel(XYPlot plot, int datasetIdx, int pointIdx) {
    Dataset ds = plot.getDatasets().get(datasetIdx);
    RangeAxis rangeAxis = plot.getRangeAxis(datasetIdx);
    String lbl = ds.getRangeLabel() + rangeAxis.getLabelSuffix();
    
    final boolean doShowRangeValue = (pointIdx > -1);
    if (doShowRangeValue) {
      double yData = plot.getDataY(datasetIdx, pointIdx);
      lbl += " (" + rangeAxis.getFormattedLabel(yData) + ")";
    }
    return lbl;
  }
  
}
