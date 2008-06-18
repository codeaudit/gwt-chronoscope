package org.timepedia.chronoscope.client.overlays;

import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Export;

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

  private final double domainX;

  private double rangeY;

  private final String label;

  private int width = -1, height;

  private ArrayList clickListener;

  private XYPlot plot = null;

  private int seriesNum = -1;

  private GssProperties markerProperties = null;

  public Marker(double domainX, double rangeY, String label, int seriesNum) {
    this.domainX = domainX;
    this.rangeY = rangeY;
    this.label = label;
    this.seriesNum = seriesNum;
  }

  /**
   * @gwt.export
   */
  @Export
  public Marker(String date, int seriesNum, String label) {
    this.label = label;
    this.domainX = Date.parse(date);
    this.seriesNum = seriesNum;
  }

  /**
   * @gwt.export addOverlayListener
   */
  @Export("addOverlayListener")
  public void addOverlayClickListener(OverlayClickListener ocl) {
    if (clickListener == null) {
      clickListener = new ArrayList();
    }
    clickListener.add(ocl);
  }

  public void click(int x, int y) {
    fireOverlayClickListener(x, y);
  }

  public void draw(Layer backingCanvas, String layer) {
    if (domainX <= plot.getDomainOrigin()
        || domainX >= plot.getDomainOrigin() + plot.getCurrentDomain()) {
      return;
    }

    int point =
        plot.getNearestVisiblePoint(domainX, seriesNum == -1 ? 0 : seriesNum)
            - 1;
    point = Math.max(point, 0);

    View view = plot.getChart().getView();
    if (markerProperties == null) {
      markerProperties = view.getGssProperties(this, "");
    }

    if (width == -1) {
      width = backingCanvas.stringWidth(label, "Verdana", "normal", "9pt");
      height = backingCanvas.stringHeight(label, "Verdana", "normal", "9pt")
          + 2;
    }
    double x, y, yp;
    x = plot.domainToScreenX(domainX, 0);

    double x0 = plot.domainToScreenX(plot.getDataX(seriesNum, point), seriesNum);
    double x1 = plot.domainToScreenX(plot.getDataX(seriesNum, point + 1), seriesNum);

    double y1 = plot.rangeToScreenY(plot.getDataY(seriesNum, point), seriesNum);
    double y2 = plot.rangeToScreenY(plot.getDataY(seriesNum, point + 1), seriesNum);
    yp = y1 + (y2 - y1) * (x - x0) / (x1 - x0);

    y = yp;
    if (y - 15 - height <= plot.getInnerPlotBounds().y) {
      y = y + 5;
    } else {
      y = y - 15 - height;
    }
    backingCanvas.save();
    x = drawOval(backingCanvas, x, y, yp, y < yp ? 1 : 0);

    backingCanvas.drawText(x, y, label, markerProperties.fontFamily,
        markerProperties.fontWeight, markerProperties.fontSize, layer);
    backingCanvas.restore();
  }

  public void fireOverlayClickListener(int x, int y) {
    if (clickListener != null) {
      for (int i = 0; i < clickListener.size(); i++) {
        ((OverlayClickListener) clickListener.get(i))
            .onOverlayClick(this, x, y);
      }
    }
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

  public String getTypeClass() {
    return label;
  }

  public boolean isHit(int x, int y) {

    View view = plot.getChart().getView();
    double mx = plot.getChart().domainToWindowX(plot, domainX, seriesNum);

    double my = plot.getChart().rangeToWindowY(plot, rangeY, seriesNum) + 5;
    if (my - 15 - height <= plot.getInnerPlotBounds().y) {
      my = my + 5;
    } else {
      my = my - 15 - height;
    }
    if (width == -1) {
      width = view.getCanvas().getRootLayer()
          .stringWidth(label, "Verdana", "normal", "9pt");
      height = view.getCanvas().getRootLayer()
          .stringHeight(label, "Verdana", "normal", "9pt") + 2;
    }
    mx -= width / 2 + 2;
    my -= height / 2 + 2;
    return x >= mx && x <= mx + width + 3 && y >= my && my <= y + height;
  }

  /**
   * @gwt.export
   */
  @Export
  public void openInfoWindow(String html) {
    plot.getChart().getPlot().openInfoWindow(html, domainX, rangeY, seriesNum);
  }

  public void removeOverlayClickListener(OverlayClickListener ocl) {
    if (clickListener != null) {
      clickListener.remove(ocl);
    }
  }

  public void setPlot(XYPlot plot) {
    this.plot = plot;

    if (seriesNum != -1) {
      int p = plot.getNearestVisiblePoint(domainX, seriesNum) - 1;
      p = Math.max(p, 0);

      double r1 = plot.getDataY(seriesNum, p);
      double r2 = plot.getDataY(seriesNum, p + 1);
      double d2 = plot.getDataX(seriesNum, p + 1);
      double d1 = plot.getDataX(seriesNum, p);

      rangeY = r1 + (domainX - d1) / (d2 - d1) * (r2 - r1);
    }
  }

  private int drawBox(Layer backingCanvas, int x, int y) {
    backingCanvas.setStrokeColor("rgb(0,0,0)");
    backingCanvas.setTransparency(1.0f);
    backingCanvas.beginPath();

    x -= width / 2;
    backingCanvas.setShadowOffsetX(0);
    backingCanvas.setShadowOffsetY(0);
    backingCanvas.setShadowBlur(0);
    backingCanvas.moveTo(x - 1, y);
    backingCanvas.lineTo(x + width + 3, y);
    backingCanvas.lineTo(x + width + 3, y + height);
    backingCanvas.lineTo(x - 1, y + height);
    backingCanvas.closePath();
    backingCanvas.setFillColor("rgb(200,200,200)");
    backingCanvas.fill();
    backingCanvas.setLineWidth(1);
    backingCanvas.stroke();
    return x;
  }

  private double drawOval(Layer backingCanvas, double x, double y, double yp,
      int dir) {
    backingCanvas.setStrokeColor(markerProperties.color);
    backingCanvas.setTransparency(1.0f);
    backingCanvas.beginPath();

    x -= width / 2;
    backingCanvas.setShadowOffsetX(0);
    backingCanvas.setShadowOffsetY(0);
    backingCanvas.setShadowBlur(0);

    double startAngle = Math.PI * 2.0 - Math.PI / 2 + Math.PI / 8;
    double endAngle = Math.PI * 2.0 - Math.PI / 2 - Math.PI / 8;
    if (dir == 0) {
      startAngle = Math.PI * 2.0 - Math.PI / 2 - Math.PI / 4 - Math.PI / 8;
      endAngle = Math.PI * 2.0 - Math.PI / 2 + Math.PI / 4 + Math.PI / 8;
    }

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
}
