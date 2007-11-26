package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssProperties;

/**
 * Draws a circle around a point with focus
 */
public class CircleFocusPainter implements FocusPainter {
    private String focusOutlineColor = "#B0B0B0";
    private final GssProperties gss;
    private final double focusRadius;


    public CircleFocusPainter(GssProperties gss) {
        this.gss = gss;
        this.focusRadius = gss.size;
    }


    public void drawFocus(XYPlot plot, Layer layer, double x, double y, int seriesNum) {
        layer.save();
        double ux = plot.domainToScreenX(x, seriesNum);
        double uy = plot.rangeToScreenY(y, seriesNum);

        layer.setStrokeColor(gss.color);
        double focusWidth = gss.lineThickness;
        layer.setLineWidth(focusWidth);
        layer.beginPath();

        layer.arc(ux, uy, focusRadius, 0, 2 * Math.PI, 1);
        layer.stroke();
        layer.restore();
    }


}
