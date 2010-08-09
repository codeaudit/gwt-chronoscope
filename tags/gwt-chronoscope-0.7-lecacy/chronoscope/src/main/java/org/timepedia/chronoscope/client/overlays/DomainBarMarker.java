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
import java.util.Date;

/**
 * An overlay which renders highlighted regions spanning the entire Y dimensions of the plot over a given domain region
 *
 * @gwt.exportPackage chronoscope
 */
public class DomainBarMarker implements Exportable, Overlay, GssElement {
    private final double domainX;
    private double rangeY;
    private final double domainWidth;
    private final String label;
    private int width = -1, height;

    private ArrayList clickListener;
    private XYPlot plot = null;
    private String date;
    private int seriesNum;
    private GssProperties markerProperties = null;
    private GssProperties markerLabelProperties;

    public DomainBarMarker(double domainX, double domainWidth, String label) {
        this.domainX = domainX;
        this.domainWidth = domainWidth;
        this.label = label;
    }

    /**
     * @param startDate
     * @param endDate
     * @param label
     * @gwt.export
     */
    public DomainBarMarker(String startDate, String endDate, String label) {
        this.label = label;

        this.domainX = Date.parse(startDate);
        this.domainWidth = Date.parse(endDate) - this.domainX;

    }

    public double getDomainX() {
        return domainX;
    }

    public double getRangeY() {
        return rangeY;
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
        if (domainX <= plot.getDomainOrigin() && domainX + domainWidth < plot.getDomainOrigin() || domainX >
                plot.getDomainOrigin() + plot.getCurrentDomain() &&
                domainX + domainWidth > plot.getDomainOrigin() + plot.getCurrentDomain()) {
            return;
        }

        if (markerProperties == null) {
            View view = plot.getChart().getView();
            markerProperties = view.getGssProperties(this, "");
            markerLabelProperties = view.getGssProperties(new GssElementImpl("label", this), "");

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
                               plot.getPlotBounds().height);
        backingCanvas.drawText(x2 + 1, /*view.getPlotBounds().y+*/10, label, markerLabelProperties.fontFamily,
                               markerLabelProperties.fontWeight, markerLabelProperties.fontSize, layer);
        backingCanvas.restore();
    }

    public boolean isHit(int x, int y) {

        double mx = plot.domainToScreenX(domainX, 0);
        double mx2 = plot.domainToScreenX(domainX + domainWidth, 0);

        return x >= mx && x <= mx2 && y > plot.getPlotBounds().y &&
                y < plot.getPlotBounds().y + plot.getPlotBounds().height;


    }

    public void click(int x, int y) {
        fireOverlayClickListener(x, y);
    }

    public void openInfoWindow(String html) {
        plot.getChart().getView().openInfoWindow(html, domainX, rangeY);
    }

    public GssElement getParentGssElement() {
        return null;
    }

    public String getType() {
        return "domainmarker";
    }

    public String getTypeClass() {
        return "horizontal";
    }
}
