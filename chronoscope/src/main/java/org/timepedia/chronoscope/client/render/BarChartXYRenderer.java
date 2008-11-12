package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.util.MathUtil;

public class BarChartXYRenderer<T extends Tuple2D> extends LineXYRenderer<T> {

  private double currInterval, prevInterval;
  private double barGapFactor = 0.75;
  private GssProperties gssBarProps;
  
  @Override
  public void beginCurve(Layer layer, RenderState renderState) {
    layer.save();
    
    lx = -1;
    currInterval = prevInterval = -1;

    final boolean isDisabled = renderState.isDisabled();
    gssBarProps = isDisabled ? gssDisabledLineProps : gssLineProps;
    assignGssPropsToLayer(gssBarProps, layer);
  }

  @Override
  public void drawCurvePart(Layer layer, T point, int methodCallCount, 
      RenderState renderState) {
    
    final double dataX = point.getFirst();
    final double dataY = point.getSecond();
    final double ux = plot.domainToScreenX(dataX, datasetIndex);
    final double uy = plot.rangeToScreenY(dataY, datasetIndex);
      
    if (methodCallCount == 0) {
      // nothing to do
    }
    else {
      double x, y, width, height;

      currInterval = ux - lx;
      height = plot.getInnerBounds().bottomY() - ly;
      y = ly;

      if (methodCallCount == 1) {
        // Calculate the screen-x and width of first bar
        width = (currInterval / 2.0) * barGapFactor;
        x = lx;
      }
      else {
        width = MathUtil.min(prevInterval, currInterval) * barGapFactor;
        x = lx - (width / 2.0);
      }
      
      drawVerticalBar(layer, x, y ,width, height);

      prevInterval = currInterval;
    }
    
    lx = ux;
    ly = uy;
  }

  @Override
  public int getMaxDrawableDatapoints() {
    return 70;
  }

  @Override
  public void endCurve(Layer layer, RenderState renderState) {
    // Render the final bar (i.e. te furthest one to the right)
    double width = (currInterval / 2.0) * barGapFactor;
    double height = plot.getInnerBounds().bottomY() - ly;
    double x = lx - width;
    double y = ly;

    drawVerticalBar(layer, x, y, width, height);
    
    layer.restore();
  }

  private void drawVerticalBar(Layer layer, double x, double y, double w, double h) {
    layer.beginPath();
    layer.moveTo(x, y);
    layer.lineTo(x + w, y);
    layer.lineTo(x + w, y + h);
    layer.lineTo(x, y + h);
    layer.closePath();
    layer.fill();
  }
  
  private void assignGssPropsToLayer(GssProperties gss, Layer layer) {
    layer.setFillColor(gss.color);
    //layer.setLineWidth(gss.lineThickness);
    layer.setShadowBlur(gss.shadowBlur);
    layer.setShadowColor(gss.shadowColor);
    layer.setShadowOffsetX(gss.shadowOffsetX);
    layer.setShadowOffsetY(gss.shadowOffsetY);
    //layer.setStrokeColor(gss.color);
    //layer.setTransparency((float) gssBarProps.transparency);
 }
}
