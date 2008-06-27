package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.XYPlotListener;
import org.timepedia.chronoscope.client.axis.LegendAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;

import java.util.Date;

/**
 * Renderer used to draw Legend
 */
public class LegendAxisRenderer implements AxisRenderer, GssElement {

  private static final String ZOOM_COLON = "Zoom:";

  private static final String ZOOM_1D = "1d";

  private static final String ZOOM_5D = "5d";

  private static final String ZOOM_1M = "1m";

  private static final String ZOOM_3M = "3m";

  private static final String ZOOM_6M = "6m";

  private static final String ZOOM_1Y = "1y";

  private static final String ZOOM_5Y = "5y";

  private static final String ZOOM_10Y = "10y";

  private static final String ZOOM_MAX = "max";

  private static final String ZSPACE = "\u00A0";

  private static final String ZOOM_STRING = ZOOM_COLON + ZSPACE + ZOOM_1D
      + ZSPACE + ZOOM_5D + ZSPACE + ZOOM_1M + ZSPACE + ZOOM_3M + ZSPACE
      + ZOOM_6M + ZSPACE + ZOOM_1Y + ZSPACE + ZOOM_5Y + ZSPACE + ZOOM_10Y
      + ZSPACE + ZOOM_MAX;

  private LegendAxis axis;

  private GssProperties axisProperties;

  private GssProperties labelProperties;

  private int lastSerNum;

  private int lastSerPer;

  private int legendStringHeight = -1;

  private int zoomStringWidth = -1;

  private int zcolon;

  private int z1d;

  private int z5d;

  private int z1m;

  private int z3m;

  private int z6m;

  private int z1y;

  private int z5y;

  private int z10y;

  private int zmax;

  private Bounds bounds;

  private int zspace;

  private String textLayerName;

  private Layer l;

  public LegendAxisRenderer(LegendAxis axis) {
    this.axis = axis;
  }

  public void begin(XYPlot plot, LegendAxis legendAxis) {
    if (axisProperties == null) {
      axis = legendAxis;

      axisProperties = plot.getChart().getView().getGssProperties(this, "");
      labelProperties = plot.getChart().getView()
          .getGssProperties(new GssElementImpl("label", this), "");
      textLayerName = axis.getAxisPanel().getPanelName() + axis.getAxisPanel()
          .getAxisNumber(axis);
    }
  }

  // Warning, Warning, total hack ahead. This will have to do until a retained mode shape layer which sits atop
  // the Canvas abstraction can provide hit detection
  public boolean click(XYPlot plot, int x, int y) {
    if (legendStringHeight == -1) {
      Layer layer = plot.getChart().getView().getCanvas().getRootLayer();
      legendStringHeight = layer.stringHeight(ZOOM_STRING,
          labelProperties.fontFamily, labelProperties.fontWeight,
          labelProperties.fontSize);
      zoomStringWidth = zw(ZOOM_STRING, layer);
      zcolon = zw(ZOOM_COLON, layer);
      z1d = zw(ZOOM_1D, layer);
      z5d = zw(ZOOM_5D, layer);
      z1m = zw(ZOOM_1M, layer);
      z3m = zw(ZOOM_3M, layer);
      z6m = zw(ZOOM_6M, layer);
      z1y = zw(ZOOM_1Y, layer);
      z5y = zw(ZOOM_5Y, layer);
      z10y = zw(ZOOM_10Y, layer);
      zmax = zw(ZOOM_MAX, layer);
      zspace = zw(ZSPACE, layer);
    }
    if (y >= bounds.y && y <= bounds.y + legendStringHeight) {

      double bx = bounds.x;
      double be = bounds.x + zoomStringWidth;

      if (x >= bx && x <= be) {
        bx = bounds.x + zcolon + zspace;
        be = bx + z1d;
        if (x >= bx && x <= be) {
          return zoom(plot, 86400);
        }
        bx += z1d + zspace;
        be = bx + z5d;
        if (x >= bx && x <= be) {
          return zoom(plot, 86400 * 5);
        }
        bx += z5d + zspace;
        be = bx + z1m;
        if (x >= bx && x <= be) {
          return zoom(plot, 86400 * 30);
        }
        bx += z1m + zspace;
        be = bx + z3m;
        if (x >= bx && x <= be) {
          return zoom(plot, 86400 * 30 * 3);
        }
        bx += z3m + zspace;
        be = bx + z6m;
        if (x >= bx && x <= be) {
          return zoom(plot, 86400 * 30 * 6);
        }
        bx += z6m + zspace;
        be = bx + z1y;
        if (x >= bx && x <= be) {
          return zoom(plot, 86400 * 365);
        }
        bx += z1y + zspace;
        be = bx + z5y;
        if (x >= bx && x <= be) {
          return zoom(plot, 86400 * 365 * 5);
        }
        bx += z5y + zspace;
        be = bx + z10y;
        if (x >= bx && x <= be) {
          return zoom(plot, 86400 * 365 * 10);
        }
        bx += z10y + zspace;
        be = bx + zmax;
        if (x >= bx && x <= be) {
          plot.maxZoomOut();
          return true;
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
    bounds = new Bounds(axisBounds);
    clearAxis(layer, axisBounds);
    drawZoomLinks(plot, layer, axisBounds);
    double x = axisBounds.x;
    double y = axisBounds.y + getLabelHeight(plot.getChart().getView(), "X")
        + 5;

    for (int i = 0; i < plot.getSeriesCount(); i++) {

      double width = drawLegendLabel(x, y, plot, layer, i, textLayerName);
      if (width < 0) {
        x = axisBounds.x;
        y += getLabelHeight(plot.getChart().getView(), "X");
        x += drawLegendLabel(x, y, plot, layer, i, textLayerName);
      } else {
        x += width;
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
        axisProperties.fontSize) + 12;
  }

  public Bounds getLegendLabelBounds(DefaultXYPlot plot, Layer layer,
      Bounds axisBounds) {
    Bounds b = new Bounds();
    int x = 0;
    View view = plot.getChart().getView();

    for (int i = 0; i < plot.getSeriesCount(); i++) {
      String seriesLabel = plot.getSeriesLabel(i);
      int x2 = getLabelWidth(view, seriesLabel);
      if (x + x2 < axisBounds.width) {
        x += x2;
        b.width = Math.max(b.width, x);
      } else {
        b.height += getLabelHeight(view, "X");
        x = 0;
      }
    }
    b.x = 0;
    b.y = 0;
    b.height += getLabelHeight(view, "X");
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
    return fmt(d.getMonth() + 1) + "/" + fmt(d.getDate()) + "/" + fmty(
        d.getYear());
  }

  private void box(String s, Layer layer, double bx, double be) {
    layer.setFillColor(s);
//            layer.beginPath();
//            layer.rect(bx, bounds.y, be-bx, legendStringHeight);
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

  private void drawHitDebugRegions(Layer layer, Bounds axisBounds) {
    layer.save();
    computeMetrics(layer);

    double bx = bounds.x;
    double be = bounds.x + zoomStringWidth;
    layer.setFillColor("#ff0000");
//           layer.fillRect(bx, bounds.y+legendStringHeight, be-bx, legendStringHeight);
//          layer.beginPath();

    //    myrect(layer, bx, bounds.y+legendStringHeight, be-bx, legendStringHeight);
//          layer.closePath();
//          layer.setStrokeColor("#000000");
//          layer.stroke();
    box("#FFFF00", layer, bx, bx + zcolon);

    bx = bounds.x + zcolon + zspace;
    be = bx + z1d;
    box("#00ffff", layer, bx, be);
    layer.setTransparency(0.2f);

    bx += z1d + zspace;
    be = bx + z5d;
    box("#00ff00", layer, bx, be);

    bx += z5d + zspace;
    be = bx + z1m;

    box("#00ff00", layer, bx, be);

    bx += z1m + zspace;
    be = bx + z3m;

    box("#00ff00", layer, bx, be);

    bx += z3m + zspace;
    be = bx + z6m;
    box("#00ff00", layer, bx, be);

    bx += z6m + zspace;
    be = bx + z1y;
    box("#00ff00", layer, bx, be);

    bx += z1y + zspace;
    be = bx + z5y;
    box("#00ff00", layer, bx, be);

    bx += z5y + zspace;
    be = bx + z10y;
    box("#00ff00", layer, bx, be);

    bx += z10y + zspace;
    be = bx + zmax;
    box("#00ff00", layer, bx, be);
    layer.restore();
  }

  private void computeMetrics(Layer layer) {
    if (legendStringHeight == -1) {

      legendStringHeight = layer.stringHeight(ZOOM_STRING,
          labelProperties.fontFamily, labelProperties.fontWeight,
          labelProperties.fontSize);
      zoomStringWidth = zw(ZOOM_STRING, layer);
      zcolon = zw(ZOOM_COLON, layer);
      z1d = zw(ZOOM_1D, layer);
      z5d = zw(ZOOM_5D, layer);
      z1m = zw(ZOOM_1M, layer);
      z3m = zw(ZOOM_3M, layer);
      z6m = zw(ZOOM_6M, layer);
      z1y = zw(ZOOM_1Y, layer);
      z5y = zw(ZOOM_5Y, layer);
      z10y = zw(ZOOM_10Y, layer);
      zmax = zw(ZOOM_MAX, layer);
      zspace = zw(ZSPACE, layer) + 1;
    }
  }

  private double drawLegendLabel(double x, double y, DefaultXYPlot plot,
      Layer layer, int seriesNum, String layerName) {
    String seriesLabel = plot.getSeriesLabel(seriesNum);
    if (lastSerNum != -1 & lastSerPer != -1 && seriesNum == lastSerNum) {
      seriesLabel += " (" + plot.getRangeAxis(seriesNum)
          .getFormattedLabel(plot.getDataY(lastSerNum, lastSerPer)) + ")";
    }
    XYRenderer renderer = plot.getRenderer(seriesNum);

    double height = getLabelHeight(plot.getChart().getView(), "X");
    double lWidth = this.getLabelWidth(plot.getChart().getView(), seriesLabel);

    if (x + lWidth >= bounds.x + bounds.width) {
      return -1;
    }

    Bounds b = renderer
        .drawLegendIcon(plot, layer, x, y + height / 2, seriesNum);

    layer.setStrokeColor(labelProperties.color);
    layer.drawText(x + b.width + 2, y, seriesLabel, labelProperties.fontFamily,
        labelProperties.fontWeight, labelProperties.fontSize, layerName,
        Cursor.DEFAULT);
    return b.width + lWidth + 20;
  }

  private void drawZoomLinks(DefaultXYPlot plot, Layer layer,
      Bounds axisBounds) {

    layer.setStrokeColor(labelProperties.color);

    double zx = axisBounds.x;
    double zy = axisBounds.y;

    computeMetrics(layer);
    drawZoomLabel(layer, zx, zy, ZOOM_COLON, false);

    zx += zcolon + zspace;
    drawZoomLabel(layer, zx, zy, ZOOM_1D, true);
    zx += z1d + zspace;
    
    drawZoomLabel(layer, zx, zy, ZOOM_5D, true);
    zx += z5d + zspace;
    
    drawZoomLabel(layer, zx, zy, ZOOM_1M, true);
    zx += z1m + zspace;
    
    drawZoomLabel(layer, zx, zy, ZOOM_3M, true);
    zx += z3m + zspace;
    
    drawZoomLabel(layer, zx, zy, ZOOM_6M, true);
    zx += z6m + zspace;
    
    drawZoomLabel(layer, zx, zy, ZOOM_1Y, true);
    zx += z1y + zspace;

    drawZoomLabel(layer, zx, zy, ZOOM_5Y, true);
    zx += z5y + zspace;

    drawZoomLabel(layer, zx, zy, ZOOM_10Y, true);
    zx += z10y + zspace;
    
    drawZoomLabel(layer, zx, zy, ZOOM_MAX, true);

    int serNum = plot.getHoverSeries();
    int serPer = plot.getHoverPoint();

    if (serPer == -1) {
      serNum = plot.getFocusSeries();
      serPer = plot.getFocusPoint();
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

      String status = asDate(plot.getDomainOrigin()) + " - " + asDate(
          plot.getDomainOrigin() + plot.getCurrentDomain());
      int width = layer.stringWidth(status, labelProperties.fontFamily,
          labelProperties.fontWeight, labelProperties.fontSize);

      layer.drawText(axisBounds.x + axisBounds.width - width - 5, axisBounds.y,
          status, labelProperties.fontFamily, labelProperties.fontWeight,
          labelProperties.fontSize, textLayerName, Cursor.DEFAULT);
    }
    //  drawHitDebugRegions(layer, axisBounds);
  }

  private void drawZoomLabel(Layer layer, double zx, double zy, String label,
      boolean clickable) {
    layer.drawText(zx, zy, label, labelProperties.fontFamily,
        labelProperties.fontWeight, labelProperties.fontSize, textLayerName,
        clickable ? Cursor.CLICKABLE : Cursor.DEFAULT);
  }

  private String fmt(int num) {
    return num < 10 ? "0" + num : "" + num;
  }

  private String fmty(int year) {
    return "" + (year + 1900);
  }

  private void myrect(Layer layer, double bx, double v, double v1,
      int legendStringHeight) {
    layer.moveTo(bx, v);
    layer.lineTo(bx + v1, v);
    layer.lineTo(bx + v1, v + legendStringHeight);
    layer.lineTo(bx, v + legendStringHeight);
    layer.lineTo(bx, v);
  }

  private boolean zoom(XYPlot plot, int secs) {
    double cd = (double) secs * 1000;
    double dc = plot.getDomainOrigin() + plot.getCurrentDomain() / 2;
    plot.animateTo(dc - cd / 2, cd, XYPlotListener.ZOOMED, null);
    return true;
  }

  private int zw(String zs, Layer layer) {
    return layer.stringWidth(zs, labelProperties.fontFamily,
        labelProperties.fontWeight, labelProperties.fontSize);
  }
}
