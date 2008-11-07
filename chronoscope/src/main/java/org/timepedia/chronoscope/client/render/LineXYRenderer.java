package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.exporter.client.Exportable;

/**
 * Renders scatter plot, lines, points+lines, or filled areas depending on 
 * GSS styling used.
 */
public class LineXYRenderer<T extends Tuple2D> extends DatasetRenderer<T> 
    implements GssElement, Exportable {
  
  boolean prevFocus = false, prevHover = false;

  boolean isGssInitialized = false;
  
  private double lx = -1, ly = -1;

  private GssProperties activeGssLineProps, activeGssPointProps;

  private GssProperties gssDisabledFillProps, gssDisabledLineProps, gssDisabledPointProps,
      gssFillProps, gssFocusProps, gssHoverProps, gssLineProps, gssPointProps;

  private FocusPainter focusPainter;

  private double fx = -1;
  
  // Keeps track of the index of the currently processed datapoint
  private int pointIndex;
  
  public void beginCurve(XYPlot<T> plot, Layer layer, RenderState renderState) {
    initGss(plot.getChart().getView());

    activeGssLineProps = renderState.isDisabled() ? gssDisabledLineProps : gssLineProps;
    layer.save();
    layer.beginPath();

    lx = ly = -1;
    fx = -1;
    pointIndex = 0;
  }

  public void beginPoints(XYPlot<T> plot, Layer layer, RenderState renderState) {
    activeGssPointProps = renderState.isDisabled() ? gssDisabledPointProps : gssPointProps;
    lx = ly = -1;
    layer.save();
  }

  public double calcLegendIconWidth(XYPlot<T> plot, View view) {
    initGss(view);
    GssProperties apointProp = 
      (plot.getFocus() != null) ? gssPointProps 
                                : gssDisabledPointProps;
    return apointProp.size + 10;
  }

  public void drawCurvePart(XYPlot<T> plot, Layer layer,
      T point, int seriesNum, RenderState renderState) {
      double ux = plot.domainToScreenX(point.getFirst(), seriesNum);
      double uy = plot.rangeToScreenY(point.getSecond(), seriesNum);
      
      // guard webkit bug, coredump if draw two identical lineTo in a row
      if (activeGssLineProps.visible) {
        if (pointIndex == 0) {
          // This is the first point of the dataset, so just store the coordinates.
          fx = ux;
          lx = ux;
          ly = uy;
        }
        else {
          if (pointIndex == 1) {
            // This is the 2nd point of the dataset, and also the 1st line segment
            // of the dataset, so need to position the cursor at point 0 (the 
            // previous point)
            layer.moveTo(lx, ly);
          }
          // Draw a line from the end of the previous line segment (or the 1st point
          // of the curve if this is the first line segment to be drawn) to (ux, uy).
          layer.lineTo(ux, uy);
          lx = ux;
          ly = uy;
        }
      }
      
      ++pointIndex;
  }
  
  public Bounds drawLegendIcon(XYPlot<T> plot, Layer layer, double x, double y,
      int seriesNum) {
    layer.save();
    initGss(layer.getCanvas().getView());

    GssProperties alineProp, apointProp;
    if (plot.getFocus() != null) {
      alineProp = gssDisabledLineProps;
      apointProp = gssDisabledPointProps;
    } else {
      alineProp = gssLineProps;
      apointProp = gssPointProps;
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

  public void drawPoint(XYPlot<T> plot, Layer layer, T point,
      int seriesNum, RenderState renderState) {
    
    final boolean isHovered = renderState.isHovered();
    final boolean isFocused = renderState.isFocused();
    final double dataX = point.getFirst();
    final double dataY = point.getSecond();
    
    GssProperties prop = isHovered ? gssHoverProps : activeGssPointProps;
    double ux = plot.domainToScreenX(dataX, seriesNum);
    double uy = plot.rangeToScreenY(dataY, seriesNum);
    
    if (prop.visible || isFocused) {
      if (isFocused) {
        focusPainter.drawFocus(plot, layer, dataX, dataY, seriesNum);
      }

      if (prop.size < 1) {
        prop.size = 1;
      }
      
      layer.setFillColor(prop.bgColor);
      layer.setShadowBlur(0);
      layer.setLineWidth(prop.lineThickness);
      layer.setStrokeColor(prop.color);
      layer.setShadowBlur(prop.shadowBlur);
      layer.setLineWidth(prop.lineThickness);
      
      double dx = ux - lx;
      if (lx == -1 || isFocused || isHovered || dx > (prop.size * 2 + 4)) {
        layer.beginPath();
//      layer.translate(ux, uy);
        layer.arc(ux, uy, prop.size, 0, 2 * Math.PI, 1);
        layer.fill();
        layer.stroke();
//      layer.translate(-ux, -uy);
        lx = ux;
        ly = uy;
      }
    }
  }

  public void endCurve(XYPlot<T> plot, Layer layer, int seriesNum, 
      RenderState renderState) {

    layer.setLineWidth(activeGssLineProps.lineThickness);
    layer.setTransparency((float) activeGssLineProps.transparency);
    layer.setShadowBlur(activeGssLineProps.shadowBlur);
    layer.setShadowColor(activeGssLineProps.shadowColor);
    layer.setShadowOffsetX(activeGssLineProps.shadowOffsetX);
    layer.setShadowOffsetY(activeGssLineProps.shadowOffsetY);
    layer.setStrokeColor(activeGssLineProps.color);
    layer.setFillColor(activeGssLineProps.bgColor);
    layer.setStrokeColor(activeGssLineProps.color);
    GssProperties fillProp = renderState.isDisabled() 
        ? gssDisabledFillProps
        : gssFillProps;
    layer.stroke();
    layer.lineTo(lx, layer.getHeight());
    layer.lineTo(fx, layer.getHeight());
    layer.setFillColor(fillProp.bgColor);
    layer.setTransparency((float) fillProp.transparency);
    layer.fill();
    layer.restore();
  }

  public void endPoints(XYPlot<T> plot, Layer layer, int seriesNum, 
      RenderState renderState) {
    layer.restore();
  }

  public String getType() {
    return "line";
  }

  public String getTypeClass() {
    return null;
  }

  private void initGss(View view) {
    if (isGssInitialized) {
      return;
    }

    GssElement pointElement = new GssElementImpl("point", parentGssElement);
    GssElement fillElement = new GssElementImpl("fill", parentGssElement);

    gssLineProps = view.getGssProperties(this, "");
    gssFocusProps = view.getGssProperties(pointElement, "focus");
    gssPointProps = view.getGssProperties(pointElement, "");
    gssHoverProps = view.getGssProperties(pointElement, "hover");
    gssDisabledLineProps = view.getGssProperties(this, "disabled");
    gssDisabledPointProps = view.getGssProperties(pointElement, "disabled");
    gssFillProps = view.getGssProperties(fillElement, "");
    gssDisabledFillProps = view.getGssProperties(fillElement, "disabled");

    focusPainter = new CircleFocusPainter(gssFocusProps);

    isGssInitialized = true;
  }

}