package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;

/**
 * Implementation of XYRenderer which can render scatter plot, lines, points+lines, or filled areas depending
 * on GSS styling used.
 */
public class XYLineRenderer extends XYRenderer implements GssElement {

    private double lx = -1, ly = -1;
    boolean prevHover = false;
    boolean prevFocus = false;
    private FocusPainter focusPainter;

    boolean gssInited = false;
    private GssProperties gssLineProperties;
    private GssProperties focusGssProperties;
    private GssProperties gssPointProperties;
    private GssProperties disabledLineProperties;
    private GssProperties disabledPointProperties;
    private GssProperties gssHoverPointProperties;
    private final int seriesNum;
    private GssProperties lineProp;
    private GssProperties pointProp;
    private boolean pointPathDefined = false;
    private final GssElementImpl fillElement;
    private GssProperties gssFillProperties;
    private GssProperties disabledFillProperties;


    public XYLineRenderer(int seriesNum) {

        this.seriesNum = seriesNum;
        parentSeriesElement = new GssElementImpl("series", null, "s" + seriesNum);
        pointElement = new GssElementImpl("point", parentSeriesElement);
        fillElement = new GssElementImpl("fill", parentSeriesElement);

    }


    public void beginCurve(XYPlot plot, Layer layer, boolean inSelection, boolean isDisabled) {
        initGss(plot.getChart().getView());

        lineProp = isDisabled ? disabledLineProperties : gssLineProperties;
        layer.save();
        layer.beginPath();

        lx = ly = -1;

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
        gssFillProperties = view.getGssProperties(fillElement, "");
        disabledFillProperties = view.getGssProperties(fillElement, "disabled");

        focusPainter = new CircleFocusPainter(focusGssProperties);

        gssInited = true;
    }


    public void endCurve(XYPlot plot, Layer layer, boolean inSelection, boolean isDisabled, int seriesNum) {

        layer.lineTo(lx, layer.getHeight());
        layer.setLineWidth(lineProp.lineThickness);
        layer.setTransparency((float) lineProp.transparency);
        layer.setShadowBlur(lineProp.shadowBlur);
        layer.setShadowColor(lineProp.shadowColor);
        layer.setShadowOffsetX(lineProp.shadowOffsetX);
        layer.setShadowOffsetY(lineProp.shadowOffsetY);
        layer.setStrokeColor(lineProp.color);
        layer.setFillColor(lineProp.bgColor);
        layer.setStrokeColor(lineProp.color);
        GssProperties fillProp = isDisabled ? disabledFillProperties : gssFillProperties;
        if(lineProp.visible) {
            layer.stroke();
            layer.setFillColor(fillProp.bgColor);
            layer.setTransparency((float) fillProp.transparency);
            layer.fill();
        }
        layer.restore();

    }

    public void drawCurvePart(XYPlot plot, Layer layer, double dataX, double dataY, int seriesNum, boolean isFocused,
                              boolean isHovered, boolean inSelection, boolean isDisabled) {
        double ux = Math.max(0, plot.domainToScreenX(dataX, seriesNum));
        double uy = plot.rangeToScreenY(dataY, seriesNum);
        // guard webkit bug, coredump if draw two identical lineTo in a row
        if (!lineProp.visible) {
            return;
        }
        if (lx == -1) {
            layer.moveTo(ux, layer.getHeight());
        }

        // previously, used to fix a bug in Safari canvas that would crash if two points in a path were
        // the same, commented out for now
        if (ux - lx >= 0) {

            layer.lineTo(ux, uy);
            lx = ux;
            ly = uy;

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
            layer.translate(x, y - pointProp.size / 2 +1);
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

    public void beginPoints(XYPlot plot, Layer layer, boolean inSelection, boolean isDisabled) {
        pointProp = isDisabled ? disabledPointProperties : gssPointProperties;
        lx = ly = -1;
    }

    public void endPoints(XYPlot plot, Layer layer, boolean inSelection, boolean disabled, int seriesNum) {

    }

    public void drawPoint(XYPlot plot, Layer layer, double dataX, double dataY, int seriesNum, boolean isFocused,
                          boolean hovered, boolean inSelection, boolean disabled) {

        GssProperties prop = hovered ? gssHoverPointProperties : pointProp;
        double ux = plot.domainToScreenX(dataX, seriesNum);
        double uy = plot.rangeToScreenY(dataY, seriesNum);

        if (lx > -1 && pointProp.visible) {
            if (isFocused) {
                focusPainter.drawFocus(plot, layer, dataX, dataY, seriesNum);
            }


            if (!pointPathDefined || hovered || isFocused) {
                if (prop.size < 1) {
                    prop.size = 1;
                }
                layer.setFillColor(prop.bgColor);
                layer.setShadowBlur(0);
                layer.setLineWidth(prop.lineThickness);
                layer.setStrokeColor(prop.color);
                layer.setShadowBlur(prop.shadowBlur);
                layer.setLineWidth(prop.lineThickness);
                pointPathDefined = !( hovered || isFocused );
            }
            layer.beginPath();
            layer.translate(ux, uy);
            layer.arc(0, 0, prop.size, 0, 2 * Math.PI, 1);
            layer.fill();
            layer.stroke();
            layer.translate(-ux, -uy);
            if (hovered || isFocused) {
                pointPathDefined = false;
            }
        }
        lx = ux;
        ly = uy;
    }

    private GssElement parentSeriesElement = null;
    private GssElement pointElement = null;


    public GssElement getParentGssElement() {
        return parentSeriesElement;
    }

    public String getType() {
        return "line";
    }

    public String getTypeClass() {
        return null;
    }
}