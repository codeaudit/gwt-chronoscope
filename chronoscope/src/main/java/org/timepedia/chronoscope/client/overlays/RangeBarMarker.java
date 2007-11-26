package org.timepedia.chronoscope.client.overlays;

import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.render.GssElementImpl;
import org.timepedia.exporter.client.Exportable;

import java.util.ArrayList;

/**
 * An overlay which renders highlighted regions spanning the entire X dimensions of the plot over a given range region
 *
 * @gwt.exportPackage chronoscope
 */
public class RangeBarMarker implements Exportable, Overlay, GssElement {
    private double domainWidth;
    private final double rangeLow;
    private final double rangeHigh;
    private final String label;
    private int width = -1, height;

    private ArrayList clickListener;
    private XYPlot plot = null;
    private String date;
    private int seriesNum;
    private GssProperties markerProperties = null;
    private GssProperties markerLabelProperties;
    private int labelHeight;

    /**
     * @param rangeLow
     * @param rangeHigh
     * @param label
     * @gwt.export
     */
    public RangeBarMarker(double rangeLow, double rangeHigh, String label) {
        this.rangeLow = rangeLow;
        this.rangeHigh = rangeHigh;

        this.label = label;
    }


    public double getDomainX() {
        return plot.getDomainOrigin();
    }

    public double getRangeY() {
        return 0;
    }


    public void fireOverlayClickListener(int x, int y) {
        if (clickListener != null) {
            for (int i = 0; i < clickListener.size(); i++) {
                ( (OverlayClickListener) clickListener.get(i) ).onOverlayClick(this, x, y);
            }
        }
    }

    /**
     * @param ocl
     * @gwt.export addOverlayListener
     */
    public void addOverlayClickListener(OverlayClickListener ocl) {
        if (clickListener == null) {
            clickListener = new ArrayList();
        }
        clickListener.add(ocl);
    }

    public void removeOverlayClickListener(OverlayClickListener ocl) {
        if (clickListener != null) {
            clickListener.remove(ocl);
        }
    }

    public void setPlot(XYPlot plot) {
        this.plot = plot;
    }

    public void draw(Layer backingCanvas, String layer) {
        if (markerProperties == null) {
            View view = plot.getChart().getView();
            markerProperties = view.getGssProperties(this, "");
            markerLabelProperties = view.getGssProperties(new GssElementImpl("label", this), "");
            labelHeight = backingCanvas.stringHeight(label, markerLabelProperties.fontFamily,
                                                     markerLabelProperties.fontWeight, markerLabelProperties.fontSize);
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
        backingCanvas.fillRect(x, y1, plot.getPlotBounds().width, y2 - y1);
        backingCanvas.setComposite(Layer.SRC_OVER);
        if (markerProperties.lineThickness > 0) {
            backingCanvas.beginPath();
            backingCanvas.moveTo(x, y1);
            backingCanvas.lineTo(x + plot.getPlotBounds().width, y1);
            backingCanvas.lineTo(x + plot.getPlotBounds().width, y2);
            backingCanvas.lineTo(x, y2);
            backingCanvas.closePath();
            backingCanvas.setStrokeColor(markerProperties.color);
            backingCanvas.stroke();

        }
        if (labelHeight + 4 < Math.abs(y1 - y2)) {
            backingCanvas.drawText(x, y1 + 2, label, markerLabelProperties.fontFamily, markerLabelProperties.fontWeight,
                                   markerLabelProperties.fontSize, layer);
        } else if (y1 - labelHeight - 4 < plot.getPlotBounds().y) {
            backingCanvas.drawText(x, y2 + 2, label, markerLabelProperties.fontFamily, markerLabelProperties.fontWeight,
                                   markerLabelProperties.fontSize, layer);
        } else {
            backingCanvas.drawText(x, y1 - labelHeight - 2, label, markerLabelProperties.fontFamily,
                                   markerLabelProperties.fontWeight, markerLabelProperties.fontSize, layer);
        }
        backingCanvas.restore();
    }

    public boolean isHit(int x, int y) {

        return false;


    }

    public void click(int x, int y) {
        fireOverlayClickListener(x, y);
    }

    public void openInfoWindow(String html) {
        plot.getChart().getView().openInfoWindow(html, plot.getDomainOrigin() + plot.getCurrentDomain() / 2,
                                                 ( rangeHigh + rangeLow ) / 2);
    }

    public GssElement getParentGssElement() {
        return null;
    }

    public String getType() {
        return "rangemarker";
    }

    public String getTypeClass() {
        return "vertical";
    }
}
