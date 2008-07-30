package org.timepedia.chronoscope.client.render;

import com.google.gwt.core.client.GWT;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.exporter.client.Exportable;

/**
 * Implementation of XYRenderer which can render scatter plot, lines,
 * points+lines, or filled areas depending on GSS styling used.
 */
public class XYLineRenderer extends XYRenderer
    implements GssElement, Exportable {

  boolean prevHover = false;

  boolean prevFocus = false;

  boolean gssInited = false;

  private double lx = -1, ly = -1;

  private FocusPainter focusPainter;

  private GssProperties gssLineProperties;

  private GssProperties focusGssProperties;

  private GssProperties gssPointProperties;

  private GssProperties disabledLineProperties;

  private GssProperties disabledPointProperties;

  private GssProperties gssHoverPointProperties;

  private GssProperties lineProp;

  private GssProperties pointProp;

  private boolean pointPathDefined = false;

  private final GssElementImpl fillElement;

  private GssProperties gssFillProperties;

  private GssProperties disabledFillProperties;

  private GssElement parentSeriesElement = null;

  private GssElement pointElement = null;

  private double fx = -1;

  public XYLineRenderer(int seriesNum) {
    parentSeriesElement = new GssElementImpl("series", null, "s" + seriesNum);
    pointElement = new GssElementImpl("point", parentSeriesElement);
    fillElement = new GssElementImpl("fill", parentSeriesElement);
  }

  public void beginCurve(XYPlot plot, Layer layer, RenderState renderState) {
    initGss(plot.getChart().getView());

    lineProp = renderState.isDisabled() ? disabledLineProperties : gssLineProperties;
    layer.save();
    layer.beginPath();

    lx = ly = -1;
    fx = -1;
  }

  public void beginPoints(XYPlot plot, Layer layer, RenderState renderState) {
    pointProp = renderState.isDisabled() ? disabledPointProperties : gssPointProperties;
    lx = ly = -1;
    layer.save();
  }

  public double calcLegendIconWidth(XYPlot plot) {
    initGss(plot.getChart().getView());
    GssProperties apointProp = 
      (plot.getFocus() != null) ? gssPointProperties 
                                : disabledPointProperties;
    return apointProp.size + 10;
  }

  public void drawCurvePart(XYPlot plot, Layer layer, double dataX,
      double dataY, int seriesNum, RenderState renderState) {
    double ux = plot.domainToScreenX(dataX, seriesNum);
    double uy = plot.rangeToScreenY(dataY, seriesNum);
    // guard webkit bug, coredump if draw two identical lineTo in a row
    if (!lineProp.visible) {
      return;
    }
    if (lx == -1) {
      layer.moveTo(ux, uy);
      fx = ux;
      lx = ux;
      return;
    }

    // previously, used to fix a bug in Safari canvas that would crash if two points in a path were
    // the same, commented out for now
    if (ux - lx >= 0) {
      layer.lineTo(ux, uy);
      lx = ux;
      ly = uy;
    }
  }
  
  public Bounds drawLegendIcon(XYPlot plot, Layer layer, double x, double y,
      int seriesNum) {
    layer.save();
    initGss(layer.getCanvas().getView());

    GssProperties alineProp, apointProp;
    if (plot.getFocus() != null) {
      alineProp = disabledLineProperties;
      apointProp = disabledPointProperties;
    } else {
      alineProp = gssLineProperties;
      apointProp = gssPointProperties;
    }

    layer.beginPath();
    layer.moveTo(x, y);
    layer.setTransparency((float) alineProp.transparency);
    layer.setLineWidth(alineProp.lineThickness);
    layer.setShadowBlur(alineProp.shadowBlur);
    layer.setShadowColor(alineProp.shadowColor);
    layer.setShadowOffsetX(alineProp.shadowOffsetX);
    layer.setShadowOffsetY(alineProp.shadowOffsetY);
    layer.setStrokeColor(alineProp.color);
    layer.lineTo(x + 5 + apointProp.size + 5, y);
    layer.stroke();

    if (apointProp.visible) {
      layer.translate(x, y - apointProp.size / 2 + 1);
      layer.beginPath();
      layer.setTransparency((float) apointProp.transparency);
      layer.setFillColor(apointProp.bgColor);
      layer.arc(6, 0, apointProp.size, 0, 2 * Math.PI, 1);
      layer.setShadowBlur(0);
      layer.fill();
      layer.beginPath();
      layer.setLineWidth(apointProp.lineThickness);
      if (apointProp.size < 1) {
        apointProp.size = 1;
      }
      layer.arc(6, 0, apointProp.size, 0, 2 * Math.PI, 1);
      layer.setStrokeColor(apointProp.color);
      layer.setShadowBlur(apointProp.shadowBlur);
      layer.setLineWidth(apointProp.lineThickness);
      layer.stroke();
    }
    layer.restore();
    return new Bounds(x, y, apointProp.size + 10, 10);
  }

  public void drawPoint(XYPlot plot, Layer layer, double dataX, double dataY,
      int seriesNum, RenderState renderState) {
    
    final boolean hovered = renderState.isHovered();
    final boolean isFocused = renderState.isFocused();
    
    GssProperties prop = hovered ? gssHoverPointProperties : pointProp;
    double ux = plot.domainToScreenX(dataX, seriesNum);
    double uy = plot.rangeToScreenY(dataY, seriesNum);

    if (prop.visible || isFocused) {
      if (isFocused) {
        focusPainter.drawFocus(plot, layer, dataX, dataY, seriesNum);
      }

      if (true || !pointPathDefined || hovered || isFocused) {
        if (prop.size < 1) {
          prop.size = 1;
        }
        layer.setFillColor(prop.bgColor);
        layer.setShadowBlur(0);
        layer.setLineWidth(prop.lineThickness);
        layer.setStrokeColor(prop.color);
        layer.setShadowBlur(prop.shadowBlur);
        layer.setLineWidth(prop.lineThickness);
        pointPathDefined = !(hovered || isFocused);
      }
      double dx = ux - lx;
      double dy = uy - ly;
      if (lx == -1 || isFocused || hovered || dx > prop.size * 2 + 4) {
        layer.beginPath();
//                layer.translate(ux, uy);
        layer.arc(ux, uy, prop.size, 0, 2 * Math.PI, 1);
        layer.fill();
        layer.stroke();
//                layer.translate(-ux, -uy);
        lx = ux;
        ly = uy;
      }
      if (hovered || isFocused) {
        pointPathDefined = false;
      }
    }
  }

  public void endCurve(XYPlot plot, Layer layer, int seriesNum, 
      RenderState renderState) {

    layer.setLineWidth(lineProp.lineThickness);
    layer.setTransparency((float) lineProp.transparency);
    layer.setShadowBlur(lineProp.shadowBlur);
    layer.setShadowColor(lineProp.shadowColor);
    layer.setShadowOffsetX(lineProp.shadowOffsetX);
    layer.setShadowOffsetY(lineProp.shadowOffsetY);
    layer.setStrokeColor(lineProp.color);
    layer.setFillColor(lineProp.bgColor);
    layer.setStrokeColor(lineProp.color);
    GssProperties fillProp = renderState.isDisabled() 
        ? disabledFillProperties
        : gssFillProperties;
    layer.stroke();
    layer.lineTo(lx, layer.getHeight());
    layer.lineTo(fx, layer.getHeight());
    layer.setFillColor(fillProp.bgColor);
    layer.setTransparency((float) fillProp.transparency);
    layer.fill();
    layer.restore();
  }

  public void endPoints(XYPlot plot, Layer layer, int seriesNum, 
      RenderState renderState) {
    layer.restore();
  }

  public GssElement getParentGssElement() {
    return parentSeriesElement;
  }

  public String getType() {
    return "line";
  }

  public String getTypeClass() {
    return null;
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
}