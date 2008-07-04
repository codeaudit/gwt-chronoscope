package org.timepedia.chronoscope.client.render;

import com.google.gwt.core.client.GWT;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.Focus;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.XYPlotListener;
import org.timepedia.chronoscope.client.axis.LegendAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.util.ArgChecker;

/**
 * Renderer used to draw Legend.
 */
public class LegendAxisRenderer implements AxisRenderer, GssElement,
    ZoomListener {

  private static final double DAY_INTERVAL = 86400 * 1000;

  private static final double MONTH_INTERVAL = DAY_INTERVAL * 30;

  private static final double YEAR_INTERVAL = MONTH_INTERVAL * 12;

  /**
   * Dictates the X-padding between each legend label.
   */
  private static final int LABEL_X_PAD = 28;

  /**
   * Dictates the Y-padding between the top of the legend and whatever's on top
   * of it.
   */
  private static final int LEGEND_Y_TOP_PAD = 3;

  /**
   * Dictates the Y-padding between the bottom of the legend and whatever's
   * below it.
   */
  private static final int LEGEND_Y_BOTTOM_PAD = 9;

  private LegendAxis axis;

  private GssProperties axisProperties;

  private Bounds bounds;

  private GssProperties labelProperties;

  private int prevHoveredDatasetIdx;

  private int prevHoveredPointIdx;

  private String textLayerName;

  private ZoomPanel zoomPanel;

  private DateRangePanel dateRangePanel;

  private XYPlot plot;

  public LegendAxisRenderer(LegendAxis axis) {
    ArgChecker.isNotNull(axis, "axis");
    this.axis = axis;
  }

  public boolean click(XYPlot plot, int x, int y) {
    zoomPanel.setLocation(bounds.x, bounds.y);
    return zoomPanel.click(x, y);
  }

  public void drawLegend(XYPlot xyplot, Layer layer, Bounds axisBounds,
      boolean gridOnly) {
    
    DefaultXYPlot plot = (DefaultXYPlot) xyplot;
    View view = plot.getChart().getView();
    final int labelHeight = getLabelHeight(view, "X");

    copyState(axisBounds, bounds);
    clearAxis(layer, axisBounds);

    zoomPanel.setLocation(axisBounds.x, axisBounds.y);
    
    double startDate = plot.getDomainOrigin();
    double endDate = startDate + plot.getCurrentDomain();
    dateRangePanel.updateDomainInterval(startDate, endDate);
    rightJustify(dateRangePanel, axisBounds);
    
    layoutPanels(axisBounds);
    
    // Draw the panels
    zoomPanel.draw(layer);
    dateRangePanel.draw(layer);
    
    updateHoverInfo(plot);

    double x = axisBounds.x;
    double y = axisBounds.y + labelHeight + LEGEND_Y_TOP_PAD;

    for (int i = 0; i < plot.getSeriesCount(); i++) {
      double width = drawLegendLabel(x, y, plot, layer, i, textLayerName);
      boolean enoughRoomInCurrentRow = (width >= 0);

      if (enoughRoomInCurrentRow) {
        x += width;
      } else {
        x = axisBounds.x;
        y += labelHeight;
        x += drawLegendLabel(x, y, plot, layer, i, textLayerName);
      }
    }
  }

  public int getLabelHeight(View view, String str) {
    return view.getCanvas().getRootLayer().stringHeight(str,
        axisProperties.fontFamily, axisProperties.fontWeight,
        axisProperties.fontSize);
  }

  public int getLabelWidth(View view, String str) {
    return view.getCanvas().getRootLayer().stringWidth(str,
        axisProperties.fontFamily, axisProperties.fontWeight,
        axisProperties.fontSize)
        + +LABEL_X_PAD;
  }

  public Bounds getLegendLabelBounds(DefaultXYPlot plot, Layer layer,
      Bounds axisBounds) {
    View view = plot.getChart().getView();
    int labelHeight = getLabelHeight(view, "X");

    Bounds b = new Bounds();
    double x = axisBounds.x;
    b.height = axisBounds.y + labelHeight + LEGEND_Y_TOP_PAD;

    for (int i = 0; i < plot.getSeriesCount(); i++) {
      int labelWidth = getLabelWidth(view, plot.getSeriesLabel(i));
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

  public GssElement getParentGssElement() {
    return axis.getAxisPanel();
  }

  public String getType() {
    return "axislegend";
  }

  public String getTypeClass() {
    return null;
  }

  public void init(XYPlot plot, LegendAxis axis) {
    View view = plot.getChart().getView();

    if (axisProperties == null) {
      axisProperties = view.getGssProperties(this, "");
      labelProperties = view.getGssProperties(
          new GssElementImpl("label", this), "");
      textLayerName = axis.getAxisPanel().getPanelName()
          + axis.getAxisPanel().getAxisNumber(axis);
      
      ZoomIntervals zoomIntervals = createDefaultZoomIntervals();
      zoomIntervals.applyFilter(plot.getDomainMin(), plot.getDomainMax(), 0);
      
      Layer rootLayer = view.getCanvas().getRootLayer();
      
      zoomPanel = new ZoomPanel();
      zoomPanel.setGssProperties(labelProperties);
      zoomPanel.setTextLayerName(textLayerName);
      zoomPanel.addListener(this);
      zoomPanel.setZoomIntervals(zoomIntervals);
      zoomPanel.init(rootLayer);
      
      dateRangePanel = new DateRangePanel();
      dateRangePanel.setGssProperties(labelProperties);
      dateRangePanel.setTextLayerName(textLayerName);
      dateRangePanel.init(rootLayer);

      dateRangePanel.updateDomainInterval(plot.getDomainMin(),
          plot.getDomainMax());

      this.plot = plot;
      this.bounds = new Bounds();
    }
  }

  public void onZoom(double intervalInMillis) {
    if (intervalInMillis == Double.MAX_VALUE) {
      plot.maxZoomOut();
    } else {
      double cd = intervalInMillis;
      double dc = plot.getDomainOrigin() + plot.getCurrentDomain() / 2;
      plot.animateTo(dc - cd / 2, cd, XYPlotListener.ZOOMED, null);
    }
  }

  private void clearAxis(Layer layer, Bounds bounds) {
    layer.save();
    layer.setFillColor(axisProperties.bgColor);
    layer.translate(-1, bounds.y - 1);
    layer.scale(layer.getWidth() + 1, bounds.height + 1);
    layer.beginPath();
    layer.rect(0, 0, 1, 1);
    layer.fill();
    layer.restore();
    layer.clearTextLayer(textLayerName);
  }

  private double drawLegendLabel(double x, double y, DefaultXYPlot plot,
      Layer layer, int seriesNum, String layerName) {
    String seriesLabel = plot.getSeriesLabel(seriesNum);
    boolean isThisSeriesHovered = prevHoveredDatasetIdx != -1
        & prevHoveredPointIdx != -1 && seriesNum == prevHoveredDatasetIdx;
    if (isThisSeriesHovered) {
      seriesLabel += " ("
          + plot.getRangeAxis(seriesNum).getFormattedLabel(
              plot.getDataY(prevHoveredDatasetIdx, prevHoveredPointIdx)) + ")";
    }
    XYRenderer renderer = plot.getRenderer(seriesNum);

    double height = getLabelHeight(plot.getChart().getView(), "X");
    double lWidth = this.getLabelWidth(plot.getChart().getView(), seriesLabel);

    if (x + lWidth >= bounds.x + bounds.width) {
      return -1;
    }

    Bounds b = renderer.drawLegendIcon(plot, layer, x, y + height / 2,
        seriesNum);

    layer.setStrokeColor(labelProperties.color);
    layer.drawText(x + b.width + 2, y, seriesLabel, labelProperties.fontFamily,
        labelProperties.fontWeight, labelProperties.fontSize, layerName,
        Cursor.DEFAULT);

    return lWidth;
  }

  private void updateHoverInfo(XYPlot plot) {
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

  private static void copyState(Bounds source, Bounds target) {
    target.x = source.x;
    target.y = source.y;
    target.height = source.height;
    target.width = source.width;
  }

  private static ZoomIntervals createDefaultZoomIntervals() {
    ZoomIntervals zooms = new ZoomIntervals();
    zooms.add(new ZoomInterval("1d", DAY_INTERVAL));
    zooms.add(new ZoomInterval("5d", DAY_INTERVAL * 5));
    zooms.add(new ZoomInterval("1m", MONTH_INTERVAL));
    zooms.add(new ZoomInterval("3m", MONTH_INTERVAL * 3));
    zooms.add(new ZoomInterval("6m", MONTH_INTERVAL * 6));
    zooms.add(new ZoomInterval("1y", YEAR_INTERVAL));
    zooms.add(new ZoomInterval("5y", YEAR_INTERVAL * 5));
    zooms.add(new ZoomInterval("10y", YEAR_INTERVAL * 10));
    zooms.add(new ZoomInterval("max", Double.MAX_VALUE).filterExempt(true));

    return zooms;
  }

  
  /**
   * Currently, this method naively lays out the ZoomPanel and
   * DateRangePanel on the X-axis.  Ultimately, layout rules and 
   * heuristics will be split out into a separate LayoutStrategy 
   * interface of some sort.
   */
  private void layoutPanels(Bounds parentBounds) {
    final int minCushion = 8;
    double parentWidth = parentBounds.width;
    
    dateRangePanel.resizeToIdealWidth();
    zoomPanel.resizeToIdealWidth();
    double idealZoomPanelWidth = zoomPanel.getWidth();
    
    // First, see if the panels in their pretties form will 
    //fit within the container's bounds
    double cushion = parentWidth - zoomPanel.getWidth() - dateRangePanel.getWidth();
    if (cushion >= minCushion) {
      return;
    }
    
    // Doesn't fit? Then compress only the date range panel
    dateRangePanel.resizeToMinimalWidth();
    rightJustify(dateRangePanel, parentBounds);
    cushion = parentWidth - idealZoomPanelWidth - dateRangePanel.getWidth();
    if (cushion >= minCushion) {
      return;
    }
    
    // Still doesn't fit? Then compress only the zoom link panel
    zoomPanel.resizeToMinimalWidth();
    dateRangePanel.resizeToIdealWidth();
    rightJustify(dateRangePanel, parentBounds);
    cushion = parentWidth - zoomPanel.getWidth() - dateRangePanel.getWidth();
    if (cushion >= minCushion) {
      return;
    }
    
    // Still doesn't fit? Then compress both panels
    dateRangePanel.resizeToMinimalWidth();
    rightJustify(dateRangePanel, parentBounds);

    // TODO:
    // If still not enough cushion, maybe resort to hiding one of
    // the panels, or dropping it to the next line.  Although the
    // latter approach might not be desirable, as it would produce
    // an ugly transition when dynamic chart resizing is available.
  }
  
  private void rightJustify(Panel p, Bounds parentBounds) {
    p.setLocation(parentBounds.x + parentBounds.width - dateRangePanel.getWidth() - 2, parentBounds.y);
  }
  
  /**
   * For debugging purposes
   */
  private static void hiliteBounds(Bounds b, Layer layer) {
    layer.save();

    layer.setFillColor("#50D0FF");
    layer.fillRect(b.x, b.y, b.width, b.height);

    layer.restore();
  }
  
  /**
   * For debugging purposes
   */
  private static void hiliteBounds(Panel p, Layer layer) {
    Bounds b = new Bounds(p.getX(), p.getY(), p.getWidth(), p.getHeight());
    hiliteBounds(b, layer);
  }
  
}
