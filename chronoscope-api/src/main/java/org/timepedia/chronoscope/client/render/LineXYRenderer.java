package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.exporter.client.Exportable;

/**
 * Renders scatter plot, lines, points+lines, or filled areas depending on GSS
 * styling used.
 */
public class LineXYRenderer<T extends Tuple2D> extends DatasetRenderer<T>
    implements GssElement, Exportable {

  protected double lx = -1, ly = -1;

  protected double fx = -1;

  @Override
  public void beginCurve(Layer layer, RenderState renderState) {
    gssActiveLineProps = renderState.isDisabled() ? gssDisabledLineProps : gssLineProps;
    layer.save();
    layer.beginPath();

    lx = ly = -1;
    fx = -1;
  }

  @Override
  public void beginPoints(Layer layer, RenderState renderState) {
    lx = ly = -1;
    layer.save();
  }


  @Override
  public void drawCurvePart(int datasetIndex, int domainIndex, Layer layer, T point, int methodCallCount, RenderState renderState) {
    gssActiveLineProps = renderState.isFocused() ? gssFocusLineProps : gssLineProps;
    gssActiveLineProps = renderState.isDisabled() ? gssDisabledLineProps : gssLineProps;
    double dataX = point.getDomain();
    double dataY = point.getRange0();
    double ux = plot.domainToScreenX(dataX, datasetIndex);
    double uy = plot.rangeToScreenY(dataY, datasetIndex);

    addClickable(datasetIndex, domainIndex, renderState.getPassNumber(), dataX, dataY, ux, uy);

    // guard webkit bug, coredump if draw two identical lineTo in a row
    if (gssActiveLineProps.visible) {
      if (methodCallCount == 0) {
        // This is the first point to be rendered, so just store the coordinates.
        fx = lx = ux;
        ly = uy;
      } else {
        if (methodCallCount == 1) {
          // This is the 2nd point to be rendered, and also the 1st line segment
          // to be rendered, so need to position the cursor at point 0 (the
          // previous point)
          layer.moveTo(lx, ly);
        }

        lineTo(layer, ux, uy);

        lx = ux;
        ly = uy;
      }
    }
  }

  @Override
  public void drawHoverPoint(Layer layer, T point, int datasetIndex) {
    if (!gssHoverProps.visible) {
      return;
    }
    
    if (gssHoverProps.size < 1) {
      gssHoverProps.size = 1;
    }

    final double dataX = point.getDomain();
    final double dataY = point.getRange0();
    final double ux = plot.domainToScreenX(dataX, datasetIndex);
    final double uy = plot.rangeToScreenY(dataY, datasetIndex);
    drawPoint(ux, uy, layer, gssHoverProps);
  }


  @Override
  public void drawPoint(int datasetIndex, int domainIndex, Layer layer, T point, RenderState renderState) {
    final boolean isFocused = renderState.isFocused();
    final double dataX = point.getDomain();
    final double dataY = point.getRange0();

    GssProperties gssProps;
    if (isFocused) {
      gssProps = this.gssFocusPointProps;
    } else if (renderState.isDisabled()) {
      gssProps = this.gssDisabledPointProps;
    } else {
      gssProps = this.gssPointProps;
    }

    if (gssProps.visible || isFocused) {

      if (gssProps.size < 1) {
        gssProps.size = 1;
      }

      double ux = plot.domainToScreenX(dataX, datasetIndex);
      double uy = plot.rangeToScreenY(dataY, datasetIndex);
      double dx = ux - lx;
      if (isFocused && gssFocusGuidelineProps.visible) {

      drawFocusPointGuideLine(layer, (int) ux);
      }

      if (lx == -1 || isFocused || dx > (gssProps.size * 2 + 4)) {
        drawPoint(ux, uy, layer, gssProps);
        lx = ux;
        ly = uy;
      }
    }
    gssProps = null;
  }

  @Override
  public void endCurve(Layer layer, RenderState renderState) {
    gssActiveLineProps = renderState.isDisabled() ? gssDisabledLineProps : gssLineProps;
    gssActiveFillProps = renderState.isDisabled() ? gssDisabledFillProps : gssFillProps;

    layer.setFillColor(gssActiveLineProps.bgColor);
    layer.setLineWidth(gssActiveLineProps.lineThickness);
    layer.setShadowBlur(gssActiveLineProps.shadowBlur);
    layer.setShadowColor(gssActiveLineProps.shadowColor);
    layer.setShadowOffsetX(gssActiveLineProps.shadowOffsetX);
    layer.setShadowOffsetY(gssActiveLineProps.shadowOffsetY);
    layer.setStrokeColor(gssActiveLineProps.color);
    layer.setTransparency((float) gssActiveLineProps.transparency);

    layer.stroke();
    layer.lineTo(lx, layer.getHeight());
    layer.lineTo(fx, layer.getHeight());

    layer.setFillColor(gssActiveFillProps.bgColor);
    layer.setTransparency((float) gssActiveFillProps.transparency);

    layer.fill();
    layer.restore();
  }

  @Override
  public void endPoints(Layer layer, RenderState renderState) {
    layer.restore();
  }

  @Override
  public void initGss(View view) {
    super.initGss(view);
  }

  public String getType() {
    return "line";
  }

  public String getTypeClass() {
    return null;
  }

  protected void lineTo(Layer layer, double nextX, double nextY) {
    // Draw a line from the end of the previous line segment (or the 1st point
    // of the visible curve if this is the first line segment to be drawn) 
    // to (ux, uy).
    layer.lineTo(nextX, nextY);
  }

  /**
   * Draws a point at the specified screen coordinates
   */
  private void drawPoint(double ux, double uy, Layer layer, GssProperties gssProps) {
    layer.setFillColor(gssProps.bgColor);
    layer.setLineWidth(gssProps.lineThickness);
    layer.setShadowBlur(gssProps.shadowBlur);
    layer.setStrokeColor(gssProps.color);

    layer.beginPath();
//  layer.translate(ux, uy);
    layer.arc(ux, uy, gssProps.size, 0, 2 * Math.PI, 1);
    layer.fill();
    layer.stroke();
//  layer.translate(-ux, -uy);
  }

}