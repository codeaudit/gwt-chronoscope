package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.overlays.OverlayClickListener;
import org.timepedia.exporter.client.Exportable;

/**
 * Interface implemented by Markers and other clases which overlay the plot
 *
 * @gwt.exportPackage chronoscope
 */
public interface Overlay extends Exportable {
    public double getDomainX();

    public double getRangeY();

    /**
     * Draw the overlay on the given layer, with text rendered on the given textLayer
     *
     * @param layer
     * @param textLayer
     */
    void draw(Layer layer, String textLayer);

    /**
     * True if the screen coordinates (x,y) relative to the Plot bounds are inside the Overlay
     *
     * @param x
     * @param y
     * @return
     */
    boolean isHit(int x, int y);

    /**
     * Fire a click event for this overlay
     *
     * @param x
     * @param y
     */
    void click(int x, int y);

    /**
     * Allows a caller to register for click events on this Overlay
     *
     * @param cl
     * @gwt.export addOverlayListener
     */
    void addOverlayClickListener(OverlayClickListener cl);

    /**
     * Removes an OverlayClickListener from this overlay
     *
     * @param cl
     */
    void removeOverlayClickListener(OverlayClickListener cl);

    /**
     * Sets the plot on which this Overlay is bound
     *
     * @param view
     */
    void setPlot(XYPlot view);
}
