package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.AxisPanel;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;

/**
 * Renders vertical range axis
 */
public class RangeAxisRenderer implements AxisRenderer, GssElement {
    private GssProperties axisProperties;
    private RangeAxis axis;
    private GssProperties labelProperties;
    private GssProperties tickProperties;
    private GssProperties gridProperties;
    private String textLayerName;

    public RangeAxisRenderer(RangeAxis rangeAxis) {
        this.axis = rangeAxis;
    }


    public void drawAxis(XYPlot plot, Layer layer, Bounds axisBounds, boolean gridOnly) {

        double rangeLow = axis.getRangeLow();
        double rangeHigh = axis.getRangeHigh();
        double range = rangeLow;

        if (!gridOnly) {
            clearAxis(layer, axisBounds);

            drawVerticalLine(layer, axisBounds);
        }

        layer.setTransparency(1.0f);
        layer.setFillColor("rgba(255,255,255,255)");
        layer.setStrokeColor("rgb(0,255,0)");
        double valueHeight = axis.getMaxLabelHeight();
        double tickDist = valueHeight + 20;
        double dDist = tickDist / axis.getHeight() * ( rangeHigh - rangeLow );
        double rangeSize = rangeHigh - rangeLow;

        do {
            drawTick(plot, layer, range, rangeLow, rangeSize, axisBounds, gridOnly);
            range += dDist;
        } while (range < rangeHigh);

        if (!gridOnly) {
            drawAxisLabel(layer, axisBounds);
        }


    }


    private void drawAxisLabel(Layer layer, Bounds bounds) {
        double dir = ( axis.getAxisPanel().getPosition() == AxisPanel.LEFT ?
                bounds.width - axis.getMaxLabelWidth() - 10 - axis.getAxisLabelWidth() : axis.getMaxLabelWidth() );
        double x = bounds.x + dir;
        double y = bounds.y + bounds.height / 2 - axis.getAxisLabelHeight() / 2;
        layer.setStrokeColor(axisProperties.color);
        String label = axis.getLabel();


        layer.drawRotatedText(x, y, getRotationAngle(), label, axisProperties.fontFamily, axisProperties.fontWeight,
                              axisProperties.fontSize, textLayerName, axis.getChart());


    }

    private double getRotationAngle() {
        return ( axis.getAxisPanel().getPosition() == AxisPanel.RIGHT ? 1.0 : -1.0 ) * Math.PI / 2;
    }


    private void drawTick(XYPlot plot, Layer layer, double range, double rangeLow, double rangeSize, Bounds bounds,
                          boolean gridOnly) {
        double uy = ( bounds.height - ( ( range - rangeLow ) / rangeSize * bounds.height ) ) + bounds
                .y;
        double dir = ( axis.getAxisPanel().getPosition() == AxisPanel.LEFT ? -5 + bounds.width : 0 );
        layer.save();
        layer.setFillColor(tickProperties.color);

        layer.fillRect(bounds.x + dir, uy, 5, tickProperties.lineThickness);
        if (gridProperties.visible && uy != bounds.y + bounds.height) {
            layer.setFillColor(gridProperties.color);
            layer.setTransparency((float) gridProperties.transparency);
            layer.fillRect(bounds.x + bounds.width, uy, plot.getPlotBounds().width, gridProperties.lineThickness);
        }
        layer.restore();
        if (!gridOnly) {
            drawLabel(layer, uy, bounds, range);
        }
    }

    private void drawLabel(Layer layer, double y, Bounds bounds, double value) {

        String label = String.valueOf(value);
        label = label.substring(0, Math.min(4, label.length()));

        double labelWidth = layer.stringWidth(label, axisProperties.fontFamily, axisProperties.fontWeight,
                                              axisProperties.fontSize);
        double labelHeight = layer.stringHeight(label, axisProperties.fontFamily, axisProperties.fontWeight,
                                                axisProperties.fontSize);
        double dir = ( axis.getAxisPanel().getPosition() == AxisPanel.LEFT ? -5 - labelWidth : 5 - bounds.width );
        layer.setStrokeColor(labelProperties.color);
        layer.setFillColor(labelProperties.bgColor);

        if (y >= bounds.y && y <= bounds.y + bounds.height) {
            layer.drawText(bounds.x + bounds.width + dir, y - labelHeight / 2, label, axisProperties.fontFamily,
                           axisProperties.fontWeight, axisProperties.fontSize, textLayerName);
        }
    }

    private void clearAxis(Layer layer, Bounds bounds) {

        layer.save();
        layer.setFillColor(axisProperties.bgColor);

        layer.setShadowBlur(0);
        layer.setShadowOffsetX(0);
        layer.setShadowOffsetY(0);
        layer.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        layer.translate(bounds.x, bounds.y);
        layer.scale(bounds.width, bounds.height);
        layer.beginPath();
        layer.rect(0, 0, 1, 1);
        layer.setLineWidth(1.0f / bounds.width);
        layer.stroke();
        layer.fill();

        layer.restore();


        layer.clearTextLayer(textLayerName);
    }

    private void drawVerticalLine(Layer layer, Bounds bounds) {
        layer.setFillColor(tickProperties.color);
        double dir = ( axis.getAxisPanel().getPosition() == AxisPanel.LEFT ? bounds.width : 0 );

        layer.fillRect(bounds.x + dir, bounds.y, tickProperties.lineThickness, bounds.y + bounds.height);
    }

    public void init(View view) {
        if (axisProperties == null) {
            axisProperties = view.getGssProperties(this, "");
            labelProperties = view.getGssProperties(new GssElementImpl("label", this), "");
            tickProperties = view.getGssProperties(new GssElementImpl("tick", this), "");
            gridProperties = view.getGssProperties(new GssElementImpl("grid", this), "");
            textLayerName = axis.getAxisPanel().getPanelName() + axis.getAxisPanel().getAxisNumber(axis);

        }
        view.getCanvas().setStrokeColor(axisProperties.color);


    }

    public GssElement getParentGssElement() {
        return axis.getAxisPanel();
    }

    public String getType() {
        return "axis";
    }

    public String getTypeClass() {
        return "a" + axis.getAxisNumber();
    }


    public int getLabelWidth(View view, String str) {
        return view.getCanvas().rotatedStringWidth(str, getRotationAngle(), axisProperties.fontFamily,
                                                   axisProperties.fontWeight, axisProperties.fontSize);
    }

    public int getLabelHeight(View view, String str) {
        return view.getCanvas().rotatedStringHeight(str, getRotationAngle(), axisProperties.fontFamily,
                                                    axisProperties.fontWeight, axisProperties.fontSize);
    }


}
