package org.timepedia.chronoscope.client.render;

import com.google.gwt.core.client.GWT;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.Focus;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.util.ArgChecker;

/**
 * UI panel containing the dataset legend (a colored line followed by the 
 * dataset's name).
 * 
 * @author Chad Takahashi
 */
public class DatasetLegendPanel extends AbstractPanel {
  
  // Dictates the X-padding between a given legend icen and its
  //associated dataset name
  private static final double LEGEND_ICON_SPACER = 2;
  
  // Dictates the X-padding between each dataset legend item
  static final int DATASET_LEGEND_SPACER = 22;

  private XYPlot plot;
  private double lblHeight;
  private int prevHoveredDatasetIdx;
  private int prevHoveredPointIdx;
  
  public void init(Layer layer) {
    ArgChecker.isNotNull(plot, "plot");
    lblHeight = this.calcHeight("X", layer);
    
    // TODO: For this panel, might make more sense for container
    // to set this panel's width
    this.width = layer.getWidth(); 
    
    // TODO: calculate and assign this.height
    
  }
  
  public double getHeight() {
    // height calculation not supported yet.  Need to move
    // LegendAxisRenderer.getLegendLabelBounds() into this class
    // and do some refactoring.
    throw new UnsupportedOperationException();
  }
  
  public void setPlot(XYPlot plot) {
    this.plot = plot;
  }
  
  public void draw(Layer layer) {
    updateHoverInfo();
    
    double xCursor = this.x;
    double yCursor = this.y;
    
    for (int i = 0; i < plot.getSeriesCount(); i++) {
      double lblWidth = drawLegendLabel(xCursor, yCursor, layer, i, false);
      boolean enoughRoomInCurrentRow = (lblWidth >= 0);

      if (enoughRoomInCurrentRow) {
        xCursor += lblWidth;
      } else {
        xCursor = this.x;
        yCursor += lblHeight;
        xCursor += drawLegendLabel(xCursor, yCursor, layer, i, false);
      }
      
      xCursor +=  DATASET_LEGEND_SPACER;
    }
  }
  
  private void calcDimensions(Layer layer) {
    updateHoverInfo();
    
    double xCursor = this.x;
    double yCursor = this.y;
    
    for (int i = 0; i < plot.getSeriesCount(); i++) {
      //TODO: Need method that just returns width of legend label
      double lblWidth = drawLegendLabel(xCursor, yCursor, layer, i, false);
      boolean enoughRoomInCurrentRow = (lblWidth >= 0);

      if (enoughRoomInCurrentRow) {
        xCursor += lblWidth;
      } else {
        xCursor = this.x;
        yCursor += lblHeight;

        //TODO: Need method that just returns width of legend label w/out actually drawing.
        xCursor += drawLegendLabel(xCursor, yCursor, layer, i, false);
      }
      
      xCursor +=  DATASET_LEGEND_SPACER;
    }
    
    width = xCursor - this.x;
    height = yCursor - this.y;
  }
  
  public void resizeToIdealWidth() {
    throw new UnsupportedOperationException();
  }
  
  public void resizeToMinimalWidth() {
    throw new UnsupportedOperationException();
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
    String seriesLabel = plot.getSeriesLabel(seriesNum);
    boolean isThisSeriesHovered = prevHoveredDatasetIdx != -1
        & prevHoveredPointIdx != -1 && seriesNum == prevHoveredDatasetIdx;
    if (isThisSeriesHovered) {
      seriesLabel += " ("
          + plot.getRangeAxis(seriesNum).getFormattedLabel(
              plot.getDataY(prevHoveredDatasetIdx, prevHoveredPointIdx)) + ")";
    }
    
    XYRenderer renderer = plot.getRenderer(seriesNum);

    double txtWidth = this.calcWidth(seriesLabel, layer);
    double iconWidth = renderer.calcLegendIconWidth(plot);
    double totalWidth = txtWidth + LEGEND_ICON_SPACER + iconWidth;
    
    if (lblX + totalWidth >= this.x + this.width) {
      return -1;
    }
    
    renderer.drawLegendIcon(plot, layer, lblX, lblY + lblHeight / 2, seriesNum);

    layer.setStrokeColor(gssProperties.color);
    layer.drawText(lblX + iconWidth + LEGEND_ICON_SPACER, lblY, seriesLabel, gssProperties.fontFamily,
        gssProperties.fontWeight, gssProperties.fontSize, textLayerName,
        Cursor.DEFAULT);

    return totalWidth;
  }

  private void updateHoverInfo() {
    int hoveredDatasetIdx = plot.getHoverSeries();
    int hoveredPointIdx = plot.getHoverPoint();

    if (hoveredPointIdx == -1) {
      Focus focus = plot.getFocus();
      if (focus != null) {
        hoveredDatasetIdx = focus.getDatasetIndex();
        hoveredPointIdx = focus.getPointIndex();
      } else {
        hoveredDatasetIdx = -1;
        hoveredPointIdx = -1;
      }
    }
    prevHoveredDatasetIdx = hoveredDatasetIdx;
    prevHoveredPointIdx = hoveredPointIdx;
  }

  /*
  private Bounds getLegendLabelBounds(Layer layer, Bounds axisBounds) {
    int labelHeight = calcHeight("X", layer);

    Bounds b = new Bounds();
    double x = axisBounds.x;
    b.height = axisBounds.y + labelHeight + LEGEND_Y_TOP_PAD;

    for (int i = 0; i < plot.getSeriesCount(); i++) {
      int labelWidth = calcWidth(plot.getSeriesLabel(i), layer);
      boolean enoughRoomInCurrentRow = (x + labelWidth) < axisBounds.width;
      if (enoughRoomInCurrentRow) {
        x += labelWidth;
        b.width = Math.max(b.width, x);
      } else {
        b.height += labelHeight;
        x = axisBounds.x + labelWidth;
      }
    }

    // Issue #41: For now, we add a LEGEND_Y_BOTTOM_PAD that's tall enough to
    // allow for the possibility of an extra row of legend labels. This is to
    // account for the case where the user hovers over a dataset point, causing
    // the corresponding range value to be appended to the legend label, which
    // in some cases could cause the remaining legend labels to run over into a
    // new row.
    b.height += labelHeight + LEGEND_Y_BOTTOM_PAD;

    b.x = 0;
    b.y = 0;

    return b;
  }
  */
}
