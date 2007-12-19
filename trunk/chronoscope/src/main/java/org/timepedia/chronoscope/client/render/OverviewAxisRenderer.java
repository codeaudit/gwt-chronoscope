package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.OverviewAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;

/**
 * Rendering code used to render OverviewAxis
 */
public class OverviewAxisRenderer implements AxisRenderer, GssElement {
    private int overviewHeight;
    private GssProperties axisProperties;
    private OverviewAxis axis;
    private static final int MIN_OVERVIEW_HEIGHT = 65;

    public void drawOverview(XYPlot plot, Layer layer, Bounds axisBounds, boolean gridOnly) {
        Layer overviewLayer = plot.getOverviewLayer();
        clearAxis(layer, axis, axisBounds);

        layer.drawImage(overviewLayer, 0, 0, overviewLayer.getWidth(), overviewLayer.getHeight(), axisBounds.x,
                        axisBounds.y, axisBounds.width, axisBounds.height);
        double origin = plot.getDomainMin();
        double currentOrigin = plot.getDomainOrigin();
        double maxDomain = plot.getDomainMax() - origin;
        double currentDomain = plot.getCurrentDomain();
        if (maxDomain > currentDomain) {
            double beginHighlight = ( ( currentOrigin - origin ) / maxDomain * axisBounds.width) + axisBounds.x;
            double endHighlight =
                    ( ( currentOrigin + currentDomain - origin ) / maxDomain * axisBounds.width) + axisBounds.x;

            if (beginHighlight < axisBounds.x) {
                beginHighlight = axisBounds.x;
            }
            if (endHighlight > axisBounds.width+axisBounds.x) {
                endHighlight = axisBounds.width+axisBounds.x;
            }
            layer.save();
            layer.setFillColor(axisProperties.bgColor);
            layer.setTransparency((float) Math.max(0.5f, axisProperties.transparency));
            layer.fillRect(beginHighlight, axisBounds.y, endHighlight - beginHighlight, axisBounds.height);
            layer.setStrokeColor(axisProperties.color);
            layer.setTransparency(1.0f);
            layer.setLineWidth(axisProperties.lineThickness);
            layer.beginPath();
            layer.moveTo(beginHighlight, axisBounds.y+1);  // fix for Opera, on Firefox/Safari, rect() has implicit moveTo
            layer.rect(beginHighlight, axisBounds.y + 1, endHighlight - beginHighlight, axisBounds.height);
            layer.stroke();
            layer.setLineWidth(1);
            layer.restore();
        }
    }

    public void init(XYPlot plot, OverviewAxis overviewAxis) {
        if (axisProperties == null) {
            axis = overviewAxis;

            axisProperties = plot.getChart().getView().getGssProperties(this, "");
            overviewHeight = axisProperties.height;
            if (overviewHeight < MIN_OVERVIEW_HEIGHT) {
                overviewHeight = MIN_OVERVIEW_HEIGHT;
            }

        }
    }


    private void clearAxis(Layer layer, OverviewAxis axis, Bounds bounds) {
    }

    public GssElement getParentGssElement() {
        return axis.getAxisPanel();
    }

    public String getType() {
        return "overview";
    }

    public String getTypeClass() {
        return null;
    }

    public int getOverviewHeight() {
        return overviewHeight;
    }


}
