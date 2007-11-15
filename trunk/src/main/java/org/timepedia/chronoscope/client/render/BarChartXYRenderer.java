package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;

/**
 * Renders BarCharts on an XYPlot
 */
public class BarChartXYRenderer extends XYRenderer implements GssElement {

    private double lx = -1, ly = -1;
    private double ldx, ldy;
    private boolean prevHover = false;
    private boolean prevFocus = false;
    private FocusPainter focusPainter;

    private boolean gssInited = false;
    private GssProperties gssLineProperties;
    private GssProperties focusGssProperties;
    private GssProperties gssPointProperties;
    private GssProperties disabledLineProperties;
    private GssProperties disabledPointProperties;
    private GssProperties gssHoverPointProperties;
    private final int seriesNum;


    public BarChartXYRenderer(int seriesNum) {

        this.seriesNum = seriesNum;
        parentSeriesElement = new GssElementImpl("series", null, "s" + seriesNum);
        pointElement = new GssElementImpl("point", parentSeriesElement);

    }


    public void beginCurve(XYPlot plot, Layer layer, boolean inSelection, boolean isDisabled) {
        initGss(plot.getChart().getView());

        GssProperties prop = isDisabled ? disabledLineProperties : gssLineProperties;
        layer.save();
        layer.setLineWidth(prop.lineThickness);
        layer.beginPath();
        layer.moveTo(0, 0);
        layer.setTransparency((float) prop.transparency);
        layer.setShadowBlur(prop.shadowBlur);
        layer.setShadowColor(prop.shadowColor);
        layer.setShadowOffsetX(prop.shadowOffsetX);
        layer.setShadowOffsetY(prop.shadowOffsetY);

        lx = ly = -1;
        prevHover = false;

    }

    private void initGss(View view) {
        if (gssInited) {
            return;
        }

        gssLineProperties = view.getGssProperties(this, "");
        focusGssProperties = view.getGssProperties(pointElement, "focus");
        gssPointProperties = view.getGssProperties(pointElement, "");
        gssHoverPointProperties = view.getGssProperties(pointElement, "hover");
        disabledLineProperties = view.getGssProperties(this, "disabled");
        disabledPointProperties = view.getGssProperties(pointElement, "disabled");
        focusPainter = new CircleFocusPainter(focusGssProperties);

        gssInited = true;
    }


    public void endCurve(XYPlot plot, Layer layer, boolean inSelection, boolean isDisabled, int seriesNum) {
        GssProperties lineProp = isDisabled ? disabledLineProperties : gssLineProperties;
        GssProperties pointProp = isDisabled ? disabledPointProperties : gssPointProperties;

        layer.setStrokeColor(lineProp.color);
        layer.setFillColor(lineProp.bgColor);
        if (lx > -1 && pointProp.visible) {

            if (prevFocus) {
                focusPainter.drawFocus(plot, layer, ldx, ldy, seriesNum);
            }
            layer.save();
            layer.translate(lx, ly);
            layer.beginPath();

            if (prevHover) {
                pointProp = gssHoverPointProperties;
            }

            layer.setFillColor(pointProp.bgColor);
            layer.arc(0, 0, pointProp.size, 0, 2 * Math.PI, 1);
            layer.setShadowBlur(0);
            layer.fill();
            layer.beginPath();
            layer.setLineWidth(pointProp.lineThickness);
            layer.arc(0, 0, pointProp.size, 0, 2 * Math.PI, 1);
            layer.setStrokeColor(pointProp.color);
            layer.setShadowBlur(pointProp.shadowBlur);

            layer.stroke();
            layer.restore();
        }

        layer.restore();

    }

    public void drawCurvePart(XYPlot plot, Layer layer, double dataX, double dataY, int seriesNum, boolean isFocused,
                              boolean isHovered, boolean inSelection, boolean isDisabled) {
        double ux = plot.domainToScreenX(dataX, seriesNum);
        double uy = plot.rangeToScreenY(dataY, seriesNum);
        GssProperties barProp = isDisabled ? disabledLineProperties : gssLineProperties;
        GssProperties pointProp = isDisabled ? disabledPointProperties : gssPointProperties;
        // guard webkit bug, coredump if draw two identical lineTo in a row
        if (ux - lx >= 1) {
            if (lx != -1 && barProp.visible) {
                layer.setShadowBlur(barProp.shadowBlur);
                layer.setShadowColor(barProp.shadowColor);
                layer.setShadowOffsetX(barProp.shadowOffsetX);
                layer.setShadowOffsetY(barProp.shadowOffsetY);
                layer.setFillColor(barProp.bgColor);
                double bw = barProp.size / 2;

                double barHeight = plot.getPlotBounds().height + plot.getPlotBounds().y;
                layer.save();
                layer.translate(lx - bw, ly);

                layer.scale(bw * 2, barHeight);
                layer.beginPath();

                // don't draw bars too close together
                if (ux - lx > bw * 2) {

                    layer.moveTo(0, 1);
                    layer.lineTo(0, 0);
                    layer.lineTo(1, 0);
                    layer.lineTo(1, 1);
                    layer.closePath();
                    layer.fill();
                }
                layer.restore();
            }
        }
        if (lx > -1 && pointProp.visible) {
            if (isFocused) {
                focusPainter.drawFocus(plot, layer, dataX, dataY, seriesNum);
            }

            if (prevHover) {
                pointProp = gssHoverPointProperties;
            }

            layer.save();
            layer.translate(lx, ly);

            layer.beginPath();

            layer.setFillColor(pointProp.bgColor);
            layer.arc(0, 0, pointProp.size, 0, 2 * Math.PI, 1);
            layer.setShadowBlur(0);
            layer.fill();
            layer.beginPath();
            layer.setLineWidth(pointProp.lineThickness);
            if (pointProp.size < 1) {
                pointProp.size = 1;
            }
            layer.arc(0, 0, pointProp.size, 0, 2 * Math.PI, 1);
            layer.setStrokeColor(pointProp.color);
            layer.setShadowBlur(pointProp.shadowBlur);
            layer.setLineWidth(pointProp.lineThickness);
            layer.stroke();
            layer.restore();
        }
        lx = ux;
        ly = uy;
        ldx = dataX;
        ldy = dataY;
        prevHover = isHovered;
        prevFocus = isFocused;
        if (barProp.visible) {
            layer.beginPath();
            layer.setStrokeColor(barProp.color);
            layer.moveTo(ux, uy);
        }
    }


    public Bounds drawLegendIcon(XYPlot plot, Layer layer, double x, double y, int seriesNum) {
        layer.save();
        GssProperties lineProp, pointProp;
        if (seriesNum != plot.getFocusSeries() && plot.getFocusSeries() != -1) {
            lineProp = disabledLineProperties;
            pointProp = disabledPointProperties;
        } else {
            lineProp = gssLineProperties;
            pointProp = gssPointProperties;
        }

        layer.beginPath();
        layer.moveTo(x, y);
        layer.setTransparency((float) lineProp.transparency);
        layer.setLineWidth(lineProp.lineThickness);
        layer.setShadowBlur(lineProp.shadowBlur);
        layer.setShadowColor(lineProp.shadowColor);
        layer.setShadowOffsetX(lineProp.shadowOffsetX);
        layer.setShadowOffsetY(lineProp.shadowOffsetY);
        layer.setStrokeColor(lineProp.color);
        layer.lineTo(x + 5 + pointProp.size + 5, y);
        layer.stroke();

        if (pointProp.visible) {
            layer.translate(x, y - pointProp.size / 2);
            layer.beginPath();
            layer.setTransparency((float) pointProp.transparency);
            layer.setFillColor(pointProp.bgColor);
            layer.arc(6, 0, pointProp.size, 0, 2 * Math.PI, 1);
            layer.setShadowBlur(0);
            layer.fill();
            layer.beginPath();
            layer.setLineWidth(pointProp.lineThickness);
            if (pointProp.size < 1) {
                pointProp.size = 1;
            }
            layer.arc(6, 0, pointProp.size, 0, 2 * Math.PI, 1);
            layer.setStrokeColor(pointProp.color);
            layer.setShadowBlur(pointProp.shadowBlur);
            layer.setLineWidth(pointProp.lineThickness);
            layer.stroke();
        }
        layer.restore();
        return new Bounds(x, y, pointProp.size + 10, 10);

    }

    public void beginPoints(XYPlot plot, Layer layer, boolean inSelection, boolean disabled) {
    }

    public void endPoints(XYPlot plot, Layer layer, boolean inSelection, boolean disabled, int seriesNum) {
    }

    public void drawPoint(XYPlot plot, Layer layer, double x, double y, int seriesNum, boolean focused, boolean hovered,
                          boolean inSelection, boolean disabled) {
    }

    private GssElement parentSeriesElement = null;
    private GssElement pointElement = null;


    public GssElement getParentGssElement() {
        return parentSeriesElement;
    }

    public String getType() {
        return "bar";
    }

    public String getTypeClass() {
        return null;
    }
}