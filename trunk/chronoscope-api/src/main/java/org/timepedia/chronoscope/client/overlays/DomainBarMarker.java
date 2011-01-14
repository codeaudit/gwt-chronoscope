package org.timepedia.chronoscope.client.overlays;

import com.google.gwt.event.shared.GwtEvent;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.InfoWindow;
import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.render.GssElementImpl;
import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import java.util.ArrayList;
import java.util.Date;

/**
 * An overlay which renders highlighted regions spanning the entire Y dimensions
 * of the plot over a given domain region
 *
 */
@ExportPackage("chronoscope")
public class DomainBarMarker implements Exportable, Overlay, GssElement {

  private final double domainX;

  private double rangeY;

  private final double domainWidth;

  private final String label;

  private String gssLabel;

  private ArrayList clickListener;

  private XYPlot plot = null;

  private GssProperties markerProperties = null;

  private GssProperties markerLabelProperties;

  public DomainBarMarker(double domainX, double domainWidth, String label) {
    this.domainX = domainX;
    this.domainWidth = domainWidth;
    this.label = label;
  }

  /**
   */
  @Export
  public DomainBarMarker(String startDate, String endDate, String label) {
    this.label = label;

    this.domainX = Date.parse(startDate);
    this.domainWidth = Date.parse(endDate) - this.domainX;
    this.gssLabel = label.replaceAll("[^a-zA-Z0-9._-]+", "");
  }

  /**
   */
  @Export
  public DomainBarMarker(String startDate, String endDate, String label,
      String gssLabel) {
    this.label = label;
    this.gssLabel = gssLabel;

    this.domainX = Date.parse(startDate);
    this.domainWidth = Date.parse(endDate) - this.domainX;
  }

  @Export
  /**
   * Change the gss class for this marker.
   */
  public void setGssLabel(String gssLabel) {
    this.gssLabel = gssLabel;
    this.markerProperties = null;
    this.markerLabelProperties = null;
  }

  /**
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
    final double plotDomainStart = plot.getDomain().getStart();
    final double plotDomainEnd = plot.getDomain().getEnd();
    final double myDomainEnd = domainX + domainWidth;
    if (domainX <= plotDomainStart && myDomainEnd < plotDomainStart
        || domainX > plotDomainEnd && myDomainEnd > plotDomainEnd) {
      return;
    }

    if (markerProperties == null) {
      View view = plot.getChart().getView();
      markerProperties = view.getGssProperties(this, "");
      markerLabelProperties = view
          .getGssProperties(new GssElementImpl("label", this), "");
    }

    if (!markerProperties.visible) {
      return;
    }

    double x = plot.domainToScreenX(domainX, 0);
    double x2 = plot.domainToScreenX(domainX + domainWidth, 0);

    backingCanvas.save();
    backingCanvas.setFillColor(markerProperties.bgColor);
    backingCanvas.setTransparency((float) markerProperties.transparency);
    backingCanvas.setComposite(Layer.LIGHTER);

    backingCanvas.fillRect(x, /*view.getPlotBounds().y*/0, x2 - x,
        /*view.getPlotBounds().y+*/
        plot.getInnerBounds().height);
    backingCanvas.drawText(x2 + 1,plot.getInnerBounds().y + 20  , label,
        markerLabelProperties.fontFamily, markerLabelProperties.fontWeight,
        markerLabelProperties.fontSize, layer, Cursor.CLICKABLE);
    backingCanvas.restore();
  }

  public void fire(GwtEvent event) {

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
    return "domainmarker";
  }

  public String getTypeClass() {
    return "horizontal "+gssLabel;
  }

  public boolean isHit(int x, int y) {

    double mx = plot.domainToScreenX(domainX, 0);
    double mx2 = plot.domainToScreenX(domainX + domainWidth, 0);

    return MathUtil.isBounded(x, mx, mx2) && y > plot.getInnerBounds().y
        && y < plot.getInnerBounds().y + plot.getInnerBounds().height;
  }

  public InfoWindow openInfoWindow(String html) {
    InfoWindow infoWindow = plot.getChart().getView()
        .createInfoWindow(html, domainX, rangeY);
    infoWindow.open();
    return infoWindow;
  }

  public void removeOverlayClickListener(OverlayClickListener ocl) {
    if (clickListener != null) {
      clickListener.remove(ocl);
    }
  }

  public void setPlot(XYPlot plot) {
    this.plot = plot;
  }
}
