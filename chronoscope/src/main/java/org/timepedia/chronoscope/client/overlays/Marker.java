package org.timepedia.chronoscope.client.overlays;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.InfoWindow;
import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by IntelliJ IDEA. User: ray Date: Apr 20, 2007 Time: 4:03:02 PM To
 * change this template use File | Settings | File Templates.
 *
 * @gwt.exportPackage chronoscope
 */
@ExportPackage("chronoscope")
public class Marker implements Overlay, GssElement, Exportable {
  
  private static enum MarkerShape { BALLOON, TEARDROP };
  
  // Determines how high (stretched-out) the marker is.
  private static final int MARKER_HEIGHT = 15;
  
  protected XYPlot plot = null;

  private ArrayList<OverlayClickListener> clickListeners;

  private int datasetIdx = -1;

  private final double domainX;
  
  private boolean isScreenPropsSet = false;
  
  private String label;

  private GssProperties markerProps;
  
  private MarkerShape markerShape;
  
  private double rangeY;
  
  private int labelWidth, labelHeight;

  public Marker(double domainX, double rangeY, String label, int datasetIdx) {
    this.domainX = domainX;
    this.rangeY = rangeY;
    this.label = label;
    
    // Silently fix an invalid dataset index
    this.datasetIdx = Math.max(0, datasetIdx);
  }

  /**
   * @gwt.export
   */
  @Export
  public Marker(String date, int datasetIdx, String label) {
    this(Date.parse(date), 0, label, datasetIdx);
  }

  /**
   * @gwt.export addOverlayListener
   */
  @Export("addOverlayListener")
  public void addOverlayClickListener(OverlayClickListener ocl) {
    if (clickListeners == null) {
      clickListeners = new ArrayList<OverlayClickListener>();
    }
    clickListeners.add(ocl);
  }

  public void click(int x, int y) {
    if (clickListeners != null) {
      for (OverlayClickListener l : clickListeners) {
        l.onOverlayClick(this, x, y);
      }
    }
  }

  public void draw(Layer backingCanvas, String layer) {
    if (plot == null) {
      throw new IllegalStateException("plot not set");
    }
    
    if (!plot.getDomain().containsOpen(domainX)) {
      return;
    }
    
    lazyInitScreenProps(backingCanvas);
    
    double x = plot.domainToScreenX(domainX, datasetIdx);
    double rangeY = interpolateRangeY(domainX, datasetIdx);
    double yp = plot.rangeToScreenY(rangeY, datasetIdx);
    double y = yp;
    
    double proposedMarkerTop = y - MARKER_HEIGHT - labelHeight; 
    if (proposedMarkerTop > plot.getInnerBounds().y) {
      y = proposedMarkerTop;
      markerShape = MarkerShape.BALLOON;
    } else {
      y += MARKER_HEIGHT;
      markerShape = MarkerShape.TEARDROP;
    }
    
    backingCanvas.save();
    int arcDirection = (y < yp) ? 1 : 0;
    x = drawOval(labelWidth, labelHeight, markerProps, backingCanvas, x, y, yp, arcDirection);

    backingCanvas.drawText(x, y, label, markerProps.fontFamily,
        markerProps.fontWeight, markerProps.fontSize, layer,
        Cursor.CLICKABLE);
    backingCanvas.restore();
  }

  public double getDomainX() {
    return domainX;
  }

  public GssElement getParentGssElement() {
    return null;
  }

  public double getRangeY() {
    return rangeY;
  }

  public String getType() {
    return "marker";
  }

  
  public String getLabel() {
    return label;
  }
  
  public void setLabel(String label) {
    this.label = label;
  }
  
  public String getTypeClass() {
    return label;
  }

  public boolean isHit(int x, int y) {
    if (plot == null) {
      throw new IllegalStateException("plot not set");
    }

    final double mx = plot.domainToWindowX(domainX, datasetIdx);
    final double xPad = labelWidth / 2 + 3;
    final boolean isHitX = MathUtil.isBounded(x, mx - xPad, mx + xPad);
    
    final double my = plot.rangeToWindowY(rangeY, datasetIdx);
    boolean isHitY;
    if (markerShape == MarkerShape.BALLOON) {
      double topOfBalloon = my - MARKER_HEIGHT - labelHeight;
      isHitY = MathUtil.isBounded(y, topOfBalloon, my);
    } else { // assumed to be TEARDROP
      double bottomOfTeardrop = my + MARKER_HEIGHT + labelHeight;
      isHitY = MathUtil.isBounded(y, my, bottomOfTeardrop);
    }
    
    return isHitX && isHitY; 
  }

  /**
   * @gwt.export
   */
  @Export
  public InfoWindow openInfoWindow(String html) {
    if (plot == null) {
      throw new IllegalStateException("plot not set");
    }
    return plot.getChart().getPlot().openInfoWindow(html, domainX, rangeY, datasetIdx);
  }

  public void removeOverlayClickListener(OverlayClickListener listener) {
    if (clickListeners != null) {
      clickListeners.remove(listener);
    }
  }

  public void setPlot(XYPlot plot) {
    ArgChecker.isNotNull(plot, "plot");
    this.plot = plot;
    rangeY = interpolateRangeY(domainX, datasetIdx);
  }
  
  public static double drawOval(int width, int height, GssProperties markerProperties, Layer backingCanvas, double x,
      double y, double yp, int dir) {
    backingCanvas.setStrokeColor(markerProperties.color);
    backingCanvas.setTransparency(1.0f);
    backingCanvas.beginPath();

    x -= width / 2;
    backingCanvas.setShadowOffsetX(0);
    backingCanvas.setShadowOffsetY(0);
    backingCanvas.setShadowBlur(0);
    
    /*
    double startAngle = Math.PI * 2.0 - Math.PI / 2 + Math.PI / 8;
    double endAngle = Math.PI * 2.0 - Math.PI / 2 - Math.PI / 8;
    if (dir == 0) {
      startAngle = Math.PI * 2.0 - Math.PI / 2 - Math.PI / 4 - Math.PI / 8;
      endAngle = Math.PI * 2.0 - Math.PI / 2 + Math.PI / 4 + Math.PI / 8;
    }
    */
    
    backingCanvas.arc(x + width / 2, y + height / 2, width + 1, 0, Math.PI, dir);
    backingCanvas.lineTo(x + (width + 1) / 2, yp);
    backingCanvas.closePath();

    backingCanvas.setFillColor(markerProperties.bgColor);
    backingCanvas.fill();
    backingCanvas.setLineWidth(markerProperties.lineThickness);
    backingCanvas.setShadowOffsetX(3);
    backingCanvas.setShadowOffsetY(3);
    backingCanvas.setShadowBlur(3);
    backingCanvas.stroke();
    return x;
  }

  private int drawBox(Layer backingCanvas, int x, int y) {
    backingCanvas.setStrokeColor("rgb(0,0,0)");
    backingCanvas.setTransparency(1.0f);
    backingCanvas.beginPath();

    x -= labelWidth / 2;
    backingCanvas.setShadowOffsetX(0);
    backingCanvas.setShadowOffsetY(0);
    backingCanvas.setShadowBlur(0);
    backingCanvas.moveTo(x - 1, y);
    backingCanvas.lineTo(x + labelWidth + 3, y);
    backingCanvas.lineTo(x + labelWidth + 3, y + labelHeight);
    backingCanvas.lineTo(x - 1, y + labelHeight);
    backingCanvas.closePath();
    backingCanvas.setFillColor("rgb(200,200,200)");
    backingCanvas.fill();
    backingCanvas.setLineWidth(1);
    backingCanvas.stroke();
    return x;
  }

  private double interpolateRangeY(double domainX, int datasetIdx) {
    int p = plot.getNearestVisiblePoint(domainX, datasetIdx) - 1;
    p = Math.max(p, 0);
    
    // linearly interpolate rangeY from domainX and its surrounding 2 data points
    double d0 = plot.getDataX(datasetIdx, p);
    double d1 = plot.getDataX(datasetIdx, p + 1);
    double r0 = plot.getDataY(datasetIdx, p);
    double r1 = plot.getDataY(datasetIdx, p + 1);

    double interplatedRangeY = r0 + (domainX - d0) / (d1 - d0) * (r1 - r0);
    return interplatedRangeY;
  }

  private void lazyInitScreenProps(Layer layer) {
    if (!isScreenPropsSet) {
      View view = plot.getChart().getView();
      markerProps = view.getGssProperties(this, "");
      labelWidth = layer.stringWidth(label, "Verdana", "normal", "9pt");
      labelHeight = layer.stringHeight(label, "Verdana", "normal", "9pt") + 2;
      isScreenPropsSet = true;
    }
  }
  
}
