package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Focus;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;

/**
 * Renders BarCharts on an XYPlot.
 */
public class BarChartXYRenderer<T extends Tuple2D> extends DatasetRenderer<T> 
    implements GssElement {

  private FocusPainter focusPainter;

  private double interval = -1;

  private double lx = -1;

  private double offset = -1;

  public void beginCurve(XYPlot<T> plot, Layer layer, RenderState renderState) {
    initGss(plot.getChart().getView());

    GssProperties prop = renderState.isDisabled() 
        ? gssDisabledLineProps
        : gssLineProps;
    layer.save();
    layer.setLineWidth(prop.lineThickness);
    layer.setTransparency((float) prop.transparency);
    layer.setShadowBlur(prop.shadowBlur);
    layer.setShadowColor(prop.shadowColor);
    layer.setShadowOffsetX(prop.shadowOffsetX);
    layer.setShadowOffsetY(prop.shadowOffsetY);

    lx = -1;
  }

  public void beginPoints(XYPlot<T> plot, Layer layer, RenderState renderState) {
    // do nothing
  }

  public double calcLegendIconWidth(XYPlot<T> plot, View view) {
    initGss(view);
    GssProperties apointProp = 
      (plot.getFocus() != null) ? gssPointProps 
                                : gssDisabledPointProps;
    return apointProp.size + 10;
  }

 public void drawCurvePart(XYPlot<T> plot, Layer layer, T point,
    int seriesNum, RenderState renderState) {
    
   final double dataX = point.getFirst();
   final double dataY = point.getSecond();
   
   double ux = plot.domainToScreenX(dataX, seriesNum);
    double uy = plot.rangeToScreenY(dataY, seriesNum);
    
    final boolean isDisabled = renderState.isDisabled();
    final boolean isFocused = renderState.isFocused();

    GssProperties barProp = isDisabled ? gssDisabledLineProps
        : gssLineProps;
    GssProperties pointProp = isDisabled ? gssDisabledPointProps
        : gssPointProps;
    // guard webkit bug, coredump if draw two identical lineTo in a row
    if (ux - lx >= 1) {
      double bw = barProp.size;
      double ow = bw / 2;
      if (barProp.visible) {
        layer.setShadowBlur(barProp.shadowBlur);
        layer.setShadowColor(barProp.shadowColor);
        layer.setShadowOffsetX(barProp.shadowOffsetX);
        layer.setShadowOffsetY(barProp.shadowOffsetY);
        layer.setFillColor(barProp.bgColor);
        double padding = 0;
        if (interval != -1) {

          bw = plot.domainToScreenX(dataX + interval, seriesNum) - ux;
          ow = plot.domainToScreenX(dataX + offset, seriesNum) - ux;
        }
        ow -= padding;
        bw -= padding * 2;

        double barHeight = plot.getInnerBounds().height + plot
            .getInnerBounds().y;
        layer.save();
        layer.translate(ux - ow, uy);

        layer.scale(bw, barHeight);
        layer.beginPath();

        // don't draw bars too close together
        if (true || ux - lx >= bw) {

          layer.moveTo(0, 1);
          layer.lineTo(0, 0);
          layer.lineTo(1, 0);
          layer.lineTo(1, 1);
          layer.closePath();
          layer.fill();
          lx = ux;
        }
        layer.restore();
      }
      
      if (pointProp.visible) {
        if (isFocused) {
          focusPainter.drawFocus(plot, layer, dataX, dataY, seriesNum);
        }

        layer.save();
        layer.translate(ux - ow, uy);

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
        lx = ux;
      }
    }
  }

 @Override
 public void drawHoverPoint(XYPlot<T> plot, Layer layer, T point,
     int datasetIndex) {
   
   // Do nothing for now...
 }

  public Bounds drawLegendIcon(XYPlot<T> plot, Layer layer, double x, double y,
      int seriesNum) {
    layer.save();
    initGss(layer.getCanvas().getView());
    
    GssProperties lineProp, pointProp;
    Focus focus = plot.getFocus();
    if (focus != null && focus.getDatasetIndex() != seriesNum) {
      lineProp = gssDisabledLineProps;
      pointProp = gssDisabledPointProps;
    } else {
      lineProp = gssLineProps;
      pointProp = gssPointProps;
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
      layer.translate(x, y - pointProp.size / 2 + 1);
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

  public void drawPoint(XYPlot<T> plot, Layer layer, T point,
      int seriesNum, RenderState renderState) {
    // do nothing
  }

  public void endCurve(XYPlot<T> plot, Layer layer, int seriesNum, RenderState renderState) {
    layer.restore();
  }

  public void endPoints(XYPlot<T> plot, Layer layer, int seriesNum, RenderState renderState) {
    // do nothing
  }

  public double getInterval() {
    return interval;
  }

  public double getOffset() {
    return offset;
  }

  public String getType() {
    return "bar";
  }

  public String getTypeClass() {
    return null;
  }

  public void setInterval(double interval) {
    this.interval = interval;
  }

  public void setOffset(double offset) {
    this.offset = offset;
  }

}