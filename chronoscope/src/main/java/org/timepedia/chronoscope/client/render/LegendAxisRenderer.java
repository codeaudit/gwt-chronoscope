package org.timepedia.chronoscope.client.render;

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
import org.timepedia.chronoscope.client.util.MathUtil;

import java.util.Date;

/**
 * Renderer used to draw Legend
 */
public class LegendAxisRenderer implements AxisRenderer, GssElement {

  private static final String ZOOM_COLON = "Zoom:";

  private static final String ZSPACE = "\u00A0";


  private static final double DAY_INTERVAL = 86400 * 1000;
  
  private static final double MONTH_INTERVAL = DAY_INTERVAL * 30;

  private static final double YEAR_INTERVAL = MONTH_INTERVAL * 12;

  /**
   * Dictates the X-padding between each legend label.
   */
  private static final int LABEL_X_PAD = 28;

  /**
   * Dictates the Y-padding between the top of the legend and whatever's 
   * on top of it.
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

  private int lastSerNum;

  private int lastSerPer;

  private int legendStringHeight = -1;

  private String textLayerName;

  // Temporary array of the zoom link label widths.  In the middle
  // of working on "zoom link crowding" issue (#44)
  private int[] zoomLinkWidths;
  
  private String fullZoomString;
  
  private int zcolon;

  private int zoomStringWidth = -1;

  private int zspace;

  private ZoomIntervals zoomIntervals;
  
  public LegendAxisRenderer(LegendAxis axis) {
    ArgChecker.isNotNull(axis, "axis"); 
    this.axis = axis;
    
    // Configure the zoom intervals
    zoomIntervals = new ZoomIntervals();
    zoomIntervals.add(new ZoomInterval("1d", DAY_INTERVAL));
    zoomIntervals.add(new ZoomInterval("5d", DAY_INTERVAL * 5));
    zoomIntervals.add(new ZoomInterval("1m", MONTH_INTERVAL));
    zoomIntervals.add(new ZoomInterval("3m", MONTH_INTERVAL * 3));
    zoomIntervals.add(new ZoomInterval("6m", MONTH_INTERVAL * 6));
    zoomIntervals.add(new ZoomInterval("1y", YEAR_INTERVAL));
    zoomIntervals.add(new ZoomInterval("5y", YEAR_INTERVAL * 5));
    zoomIntervals.add(new ZoomInterval("10y", YEAR_INTERVAL * 10));
    zoomIntervals.add(new ZoomInterval("max", Double.MAX_VALUE));
    
    // this array is temporary!  Will go away when zoom refactoring is complete.
    this.zoomLinkWidths = new int[9]; 
    
    StringBuilder str = new StringBuilder();
    str.append(ZOOM_COLON);
    for (ZoomInterval zoom : zoomIntervals) {
      str.append(ZSPACE);
      str.append(zoom.getName());
    }
    this.fullZoomString = str.toString();
  }

  public void begin(XYPlot plot, LegendAxis legendAxis) {
    if (axisProperties == null) {
      axis = legendAxis;

      axisProperties = plot.getChart().getView().getGssProperties(this, "");
      labelProperties = plot.getChart().getView().getGssProperties(
          new GssElementImpl("label", this), "");
      textLayerName = axis.getAxisPanel().getPanelName()
          + axis.getAxisPanel().getAxisNumber(axis);
    }
  }

  // Warning, Warning, total hack ahead. This will have to do until a retained
  // mode shape layer which sits atop
  // the Canvas abstraction can provide hit detection
  public boolean click(XYPlot plot, int x, int y) {
    if (legendStringHeight == -1) {
      Layer layer = plot.getChart().getView().getCanvas().getRootLayer();
      computeMetrics(layer, true);
    }
    
    if (MathUtil.isBounded(y, bounds.y, bounds.y + legendStringHeight)) {
      double bx = bounds.x;
      double be = bounds.x + zoomStringWidth;

      if (MathUtil.isBounded(x, bx, be)) {
        // Move cursor to the 1st zoom link
        bx = bounds.x + zcolon + zspace;
        int i = 0;
        for (ZoomInterval zoom : zoomIntervals) {
          be = bx + this.zoomLinkWidths[i++];
          if (MathUtil.isBounded(x, bx, be)) {
            return zoom(plot, zoom.getInterval());
          }
          bx = be + zspace;
        }
        return false;
      }
    } else {
      return false;
    }
    return false;
  }

  public void drawLegend(XYPlot xyplot, Layer layer, Bounds axisBounds,
      boolean gridOnly) {

    DefaultXYPlot plot = (DefaultXYPlot) xyplot;
    View view = plot.getChart().getView();
    final int labelHeight = getLabelHeight(view, "X");

    bounds = new Bounds(axisBounds);
    clearAxis(layer, axisBounds);
    drawZoomLinks(plot, layer, axisBounds);
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
    begin(plot, axis);
  }

  private String asDate(double dataX) {
    long ldate = (long) dataX;
    Date d = new Date(ldate);
    return fmt(d.getMonth() + 1) + "/" + fmt(d.getDate()) + "/"
        + fmty(d.getYear());
  }

  private void box(String s, Layer layer, double bx, double be) {
    layer.setFillColor(s);
    // layer.beginPath();
    // layer.rect(bx, bounds.y, be-bx, legendStringHeight);
    layer.fillRect(bx, bounds.y, be - bx, legendStringHeight);
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

  private void computeMetrics(Layer layer, boolean force) {
    boolean doCompute = (force || legendStringHeight == -1);
    
    if (doCompute) {
      legendStringHeight = layer.stringHeight(fullZoomString,
          labelProperties.fontFamily, labelProperties.fontWeight,
          labelProperties.fontSize);
      
      zoomStringWidth = labelWidth(fullZoomString, layer);
      zcolon = labelWidth(ZOOM_COLON, layer);
      zspace = labelWidth(ZSPACE, layer) + 1;
  
      int i = 0;
      for (ZoomInterval zoom : this.zoomIntervals) {
        this.zoomLinkWidths[i++] = labelWidth(zoom.getName(), layer);
      }
    }
  }

  /*
   * private void drawHitDebugRegions(Layer layer, Bounds axisBounds) {
   * layer.save(); computeMetrics(layer);
   * 
   * double bx = bounds.x; double be = bounds.x + zoomStringWidth;
   * layer.setFillColor("#ff0000"); // layer.fillRect(bx,
   * bounds.y+legendStringHeight, be-bx, // legendStringHeight); //
   * layer.beginPath();
   *  // myrect(layer, bx, bounds.y+legendStringHeight, be-bx, //
   * legendStringHeight); // layer.closePath(); //
   * layer.setStrokeColor("#000000"); // layer.stroke(); box("#FFFF00", layer,
   * bx, bx + zcolon);
   * 
   * bx = bounds.x + zcolon + zspace; be = bx + z1d; box("#00ffff", layer, bx,
   * be); layer.setTransparency(0.2f);
   * 
   * bx += z1d + zspace; be = bx + z5d; box("#00ff00", layer, bx, be);
   * 
   * bx += z5d + zspace; be = bx + z1m;
   * 
   * box("#00ff00", layer, bx, be);
   * 
   * bx += z1m + zspace; be = bx + z3m;
   * 
   * box("#00ff00", layer, bx, be);
   * 
   * bx += z3m + zspace; be = bx + z6m; box("#00ff00", layer, bx, be);
   * 
   * bx += z6m + zspace; be = bx + z1y; box("#00ff00", layer, bx, be);
   * 
   * bx += z1y + zspace; be = bx + z5y; box("#00ff00", layer, bx, be);
   * 
   * bx += z5y + zspace; be = bx + z10y; box("#00ff00", layer, bx, be);
   * 
   * bx += z10y + zspace; be = bx + zmax; box("#00ff00", layer, bx, be);
   * layer.restore(); }
   */
  
  private double drawLegendLabel(double x, double y, DefaultXYPlot plot,
      Layer layer, int seriesNum, String layerName) {
    String seriesLabel = plot.getSeriesLabel(seriesNum);
    boolean isThisSeriesHovered = lastSerNum != -1 & lastSerPer != -1
        && seriesNum == lastSerNum;
    if (isThisSeriesHovered) {
      seriesLabel += " ("
          + plot.getRangeAxis(seriesNum).getFormattedLabel(
              plot.getDataY(lastSerNum, lastSerPer)) + ")";
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

  private void drawZoomLabel(Layer layer, double zx, double zy, String label,
      boolean clickable) {
    layer.drawText(zx, zy, label, labelProperties.fontFamily,
        labelProperties.fontWeight, labelProperties.fontSize, textLayerName,
        clickable ? Cursor.CLICKABLE : Cursor.DEFAULT);
  }

  private void drawZoomLinks(DefaultXYPlot plot, Layer layer, Bounds axisBounds) {

    layer.setStrokeColor(labelProperties.color);

    double zx = axisBounds.x;
    double zy = axisBounds.y;

    computeMetrics(layer, false);
    drawZoomLabel(layer, zx, zy, ZOOM_COLON, false);
    zx += zcolon + zspace;
    
    int i = 0;
    for (ZoomInterval zoom : zoomIntervals) {
      drawZoomLabel(layer, zx, zy, zoom.getName(), true);
      zx += zoomLinkWidths[i++] + zspace;
      
    }
    
    int serNum = plot.getHoverSeries();
    int serPer = plot.getHoverPoint();

    if (serPer == -1) {
      Focus focus = plot.getFocus();
      if (focus != null) {
        serNum = focus.getDatasetIndex();
        serPer = focus.getPointIndex();
      } else {
        serNum = -1;
        serPer = -1;
      }
    }

    lastSerNum = serNum;
    lastSerPer = serPer;

    if (false && lastSerNum != -1 && lastSerPer != -1) {
      String val = String.valueOf(plot.getDataY(lastSerNum, lastSerPer));
      String status = "X: " + asDate(plot.getDataX(lastSerNum, lastSerPer))
          + ", Y: " + val.substring(0, Math.min(4, val.length()));
      int width = layer.stringWidth(status, labelProperties.fontFamily,
          labelProperties.fontWeight, labelProperties.fontSize);

      layer.drawText(axisBounds.x + axisBounds.width - width, axisBounds.y,
          status, labelProperties.fontFamily, labelProperties.fontWeight,
          labelProperties.fontSize, textLayerName, Cursor.DEFAULT);
    } else {

      String status = asDate(plot.getDomainOrigin()) + " - "
          + asDate(plot.getDomainOrigin() + plot.getCurrentDomain());
      int width = layer.stringWidth(status, labelProperties.fontFamily,
          labelProperties.fontWeight, labelProperties.fontSize);

      layer.drawText(axisBounds.x + axisBounds.width - width - 5, axisBounds.y,
          status, labelProperties.fontFamily, labelProperties.fontWeight,
          labelProperties.fontSize, textLayerName, Cursor.DEFAULT);
    }
    // drawHitDebugRegions(layer, axisBounds);
  }

  private String fmt(int num) {
    return num < 10 ? "0" + num : "" + num;
  }

  private String fmty(int year) {
    return "" + (year + 1900);
  }
  
  /*
  private void myrect(Layer layer, double bx, double v, double v1,
      int legendStringHeight) {
    layer.moveTo(bx, v);
    layer.lineTo(bx + v1, v);
    layer.lineTo(bx + v1, v + legendStringHeight);
    layer.lineTo(bx, v + legendStringHeight);
    layer.lineTo(bx, v);
  }
  */
  
  private boolean zoom(XYPlot plot, double intervalInMillis) {
    if (intervalInMillis == Double.MAX_VALUE) {
      plot.maxZoomOut();
    }
    else {
      double cd = intervalInMillis;
      double dc = plot.getDomainOrigin() + plot.getCurrentDomain() / 2;
      plot.animateTo(dc - cd / 2, cd, XYPlotListener.ZOOMED, null);
    }
    return true;
  }

  private int labelWidth(String zs, Layer layer) {
    return layer.stringWidth(zs, labelProperties.fontFamily,
        labelProperties.fontWeight, labelProperties.fontSize);
  }
}
