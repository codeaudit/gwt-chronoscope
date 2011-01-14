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
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import java.util.ArrayList;

/**
 * An overlay which renders highlighted regions spanning the entire X dimensions
 * of the plot over a given range region.
 *
 */
@ExportPackage("chronoscope")
public class RangeBarMarker implements Exportable, Overlay, GssElement {

  private final double rangeLow;

  private final double rangeHigh;

  private final String label;

  private ArrayList clickListener;

  private XYPlot plot = null;

  private GssProperties markerProperties = null;

  private GssProperties markerLabelProperties;

  private int labelHeight;

  /**
   */
  @Export
  public RangeBarMarker(double rangeLow, double rangeHigh, String label) {
    this.rangeLow = rangeLow;
    this.rangeHigh = rangeHigh;

    this.label = label;
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
    if (markerProperties == null) {
      View view = plot.getChart().getView();
      markerProperties = view.getGssProperties(this, "");
      markerLabelProperties = view
          .getGssProperties(new GssElementImpl("label", this), "");
      labelHeight = backingCanvas.stringHeight(label,
          markerLabelProperties.fontFamily, markerLabelProperties.fontWeight,
          markerLabelProperties.fontSize);
    }
    if (!markerProperties.visible) {
      return;
    }

    double x = 0;
    double y1 = plot.rangeToScreenY(rangeHigh, 0);
    double y2 = plot.rangeToScreenY(rangeLow, 0);

    backingCanvas.save();
    backingCanvas.setFillColor(markerProperties.bgColor);
    backingCanvas.setComposite(Layer.LIGHTER);
    backingCanvas.setTransparency((float) markerProperties.transparency);
    backingCanvas.fillRect(x, y1, plot.getInnerBounds().width, y2 - y1);
    backingCanvas.setComposite(Layer.SRC_OVER);
    if (markerProperties.lineThickness > 0) {
      backingCanvas.beginPath();
      backingCanvas.moveTo(x, y1);
      backingCanvas.lineTo(x + plot.getInnerBounds().width, y1);
      backingCanvas.lineTo(x + plot.getInnerBounds().width, y2);
      backingCanvas.lineTo(x, y2);
      backingCanvas.closePath();
      backingCanvas.setStrokeColor(markerProperties.color);
      backingCanvas.stroke();
    }
    
    // Add a little left-padding to marker text so that it doesn't overlap
    // the range values on the left range axis.  TODO:  Determine the actual
    // distance (if any) that the text needs to be shifted to the right.
    final double textStartX = x + 25;
    
    if (labelHeight + 4 < Math.abs(y1 - y2)) {
      backingCanvas.drawText(textStartX, y1 + labelHeight + 2, label, markerLabelProperties.fontFamily,
          markerLabelProperties.fontWeight, markerLabelProperties.fontSize,
          layer, Cursor.CLICKABLE);
    } else if (y1 - labelHeight - 4 < plot.getInnerBounds().y) {
      backingCanvas.drawText(textStartX, y2 + 2, label, markerLabelProperties.fontFamily,
          markerLabelProperties.fontWeight, markerLabelProperties.fontSize,
          layer, Cursor.CONTRASTED);
    } else {
      backingCanvas.drawText(textStartX, y1 + labelHeight + 2, label,
          markerLabelProperties.fontFamily, markerLabelProperties.fontWeight,
          markerLabelProperties.fontSize, layer, Cursor.CONTRASTED);
    }
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
    return plot.getDomain().getStart();
  }

  public GssElement getParentGssElement() {
    return null;
  }

  public double getRangeY() {
    return 0;
  }

  public String getType() {
    return "rangemarker";
  }

  public String getTypeClass() {
    return "vertical";
  }

  public boolean isHit(int x, int y) {

    return false;
  }

  public InfoWindow openInfoWindow(String html) {
    InfoWindow infoWindow = plot.getChart().getView().createInfoWindow(html,
        plot.getDomain().midpoint(), (rangeHigh + rangeLow) / 2);
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
