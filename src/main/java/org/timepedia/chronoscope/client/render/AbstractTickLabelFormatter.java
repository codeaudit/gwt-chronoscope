package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssProperties;

/**
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public abstract class AbstractTickLabelFormatter implements TickLabelFormatter {
    private final String dummyTick;
    private double maxLabelWidth = -1;

    public AbstractTickLabelFormatter(String s) {
        dummyTick = s;
    }

    public double getMaxDimensionDummyTick(Layer layer, GssProperties axisProperties) {
        if (maxLabelWidth == -1) {
            maxLabelWidth = tickLabelLength(dummyTick, layer, axisProperties);

        }
        return maxLabelWidth;
    }

    private double tickLabelLength(String l, Layer layer, GssProperties axisProperties) {
        return layer.stringWidth(l, axisProperties.fontFamily, axisProperties.fontWeight, axisProperties.fontSize);
    }
}
