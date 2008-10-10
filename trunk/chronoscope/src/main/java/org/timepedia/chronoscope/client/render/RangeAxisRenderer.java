package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.axis.AxisPanel.Position;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.util.MathUtil;

/**
 * Renders vertical range axis
 */
public class RangeAxisRenderer extends AxisRenderer {

  private boolean boundsSet;

  private GssProperties gridProperties, tickProperties;
  
  private RangeAxis rangeAxis;
  
  public void drawAxis(XYPlot plot, Layer layer, Bounds axisBounds,
      boolean gridOnly) {
    double tickPositions[] = rangeAxis.computeTickPositions();

    layer.save();
    if (!gridOnly) {
      clearAxis(layer, axisBounds);
      drawVerticalLine(layer, axisBounds);
    }

    layer.setTransparency(1.0f);
    layer.setFillColor("rgba(255,255,255,255)");
    layer.setStrokeColor("rgb(0,255,0)");

    double axisRange = valueAxis.getRange();

    for (int i = 0; i < tickPositions.length; i++) {
      drawTick(plot, layer, tickPositions[i], tickPositions[0], axisRange,
          axisBounds, gridOnly);
    }

    if (!gridOnly) {
      drawAxisLabel(layer, axisBounds, plot.getChart());
    }
    layer.restore();
  }

  public int getLabelHeight(View view, String str, double rotationAngle) {
    return view.getCanvas().getRootLayer().rotatedStringHeight(str,
        rotationAngle, axisProperties.fontFamily, axisProperties.fontWeight,
        axisProperties.fontSize);
  }

  public int getLabelWidth(View view, String str, double rotationAngle) {
    return view.getCanvas().getRootLayer().rotatedStringWidth(str,
        rotationAngle, axisProperties.fontFamily, axisProperties.fontWeight,
        axisProperties.fontSize);
  }

  public String getType() {
    return "axis";
  }

  public String getTypeClass() {
    return "a" + rangeAxis.getAxisNumber();
  }

  @Override
  protected void initHook() {
    GssElement tickGssElem = new GssElementImpl("tick", this);
    GssElement gridGssElem = new GssElementImpl("grid", this);
    tickProperties = view.getGssProperties(tickGssElem, "");
    gridProperties = view.getGssProperties(gridGssElem, "");
    rangeAxis = (RangeAxis)this.valueAxis;
  }

  private void clearAxis(Layer layer, Bounds bounds) {
    layer.save();
    if (!boundsSet) {
      layer.setTextLayerBounds(textLayerName, bounds);
      boundsSet = true;
    }

    layer.clearTextLayer(textLayerName);

    layer.setFillColor(axisProperties.bgColor);
    layer.setShadowBlur(0);
    layer.setShadowOffsetX(0);
    layer.setShadowOffsetY(0);
    layer.translate(bounds.x - 1, bounds.y - 1);
    layer.scale(bounds.width + 1, bounds.height + 1);
    layer.fillRect(0, 0, 1, 1);
//    
//    layer.beginPath();
//    layer.rect(0, 0, 1, 1);
//    layer.setLineWidth(1.0f / bounds.width);
//    layer.stroke();
    // layer.fill();
    layer.restore();
  }

  private void drawAxisLabel(Layer layer, Bounds bounds, Chart chart) {
    if (labelProperties.visible) {
      boolean isLeft = valueAxis.getAxisPanel().getPosition() == Position.LEFT;
      boolean isInnerMost = valueAxis.getAxisPanel().getAxisNumber(valueAxis) == (
          isLeft ?
              valueAxis.getAxisPanel().getAxisCount() - 1 : 0);

//      double dir = (isLeft ? bounds
//          .width - (isInnerMost ? 0 : axis.getMaxLabelWidth() - 10) - axis
//          .getAxisLabelWidth() : (isInnerMost ? 0 : axis.getMaxLabelWidth()));
      double dir = isLeft ? 0 : (isInnerMost ? 0 : rangeAxis.getMaxLabelWidth() + 1);
      double x = bounds.x + dir;
      double y = bounds.y + bounds.height / 2 - rangeAxis.getAxisLabelHeight() / 2;
      layer.setStrokeColor(labelProperties.color);
      String label = valueAxis.getLabel();

      layer.drawRotatedText(x, y, rangeAxis.getRotationAngle(), label,
          axisProperties.fontFamily, axisProperties.fontWeight,
          axisProperties.fontSize, textLayerName, chart);
    }
  }

  private void drawLabel(Layer layer, double y, Bounds bounds, double value) {

    String label = rangeAxis.getFormattedLabel(value);

    double labelWidth = layer.stringWidth(label, axisProperties.fontFamily,
        axisProperties.fontWeight, axisProperties.fontSize);
    double labelHeight = layer.stringHeight(label, axisProperties.fontFamily,
        axisProperties.fontWeight, axisProperties.fontSize);
    boolean isLeft = valueAxis.getAxisPanel().getPosition() == Position.LEFT;
    double dir = (isLeft ? -5 - labelWidth : 5 - bounds.width);
    if ("inside".equals(axisProperties.tickPosition)) {
      dir = isLeft ? 5 + 1 : -labelWidth - 5;
    }

    layer.save();
    layer.setStrokeColor(labelProperties.color);
    layer.setFillColor(labelProperties.bgColor);
    double alignAdjust = -labelHeight / 2;
    boolean isInnerMost = valueAxis.getAxisPanel().getAxisNumber(valueAxis) == (isLeft ?
        valueAxis.getAxisPanel().getAxisCount() - 1 : 0);
    if ("above".equals(labelProperties.tickAlign)) {
      alignAdjust = -labelHeight;
      dir = 1;

      if (isInnerMost) {
        dir = isLeft ? 1 : -bounds.width - labelWidth - 3;
      } else {
        double maxLabelWidth = rangeAxis.getMaxLabelWidth();
        double axisLabelWidth = rangeAxis.getAxisLabelWidth();
        dir = isLeft ? -maxLabelWidth + 1
            : -labelWidth-axisLabelWidth-10;
      }
    }
    if (MathUtil.isBounded(y, bounds.y, bounds.y + bounds.height)) {
      layer.drawText(bounds.x + bounds.width + dir, y + alignAdjust, label,
          axisProperties.fontFamily, axisProperties.fontWeight,
          axisProperties.fontSize, textLayerName, Cursor.DEFAULT);
    }
    layer.restore();
  }

  private void drawTick(XYPlot plot, Layer layer, double range, double rangeLow,
      double rangeSize, Bounds bounds, boolean gridOnly) {
    double uy =
        (bounds.height - ((range - rangeLow) / rangeSize * bounds.height))
            + bounds
            .y;
    boolean isLeft = valueAxis.getAxisPanel().getPosition() == Position.LEFT;
    double dir = (isLeft ? -5 + bounds.width : 0);
    if ("inside".equals(axisProperties.tickPosition)) {
      boolean isInnerMost = valueAxis.getAxisPanel().getAxisNumber(valueAxis) == (isLeft ?
          valueAxis.getAxisPanel().getAxisCount() - 1 : 0);
      if (isInnerMost) {
        dir = isLeft ? bounds.width + 1 : -5;
      } else {
        double maxLabelWidth = rangeAxis.getMaxLabelWidth();
        dir = isLeft ? bounds.width - maxLabelWidth - 1
            : maxLabelWidth - 5 + 1;
      }
    }
    layer.save();
    layer.setFillColor(tickProperties.color);
    layer.setTransparency(1);

    layer.fillRect(bounds.x + dir, uy, 5, tickProperties.lineThickness);
    if (gridProperties.visible && uy != bounds.y + bounds.height) {
      layer.setFillColor(gridProperties.color);
      layer.setTransparency((float) gridProperties.transparency);
      layer.fillRect(bounds.x + bounds.width, uy,
          plot.getInnerBounds().width, gridProperties.lineThickness);
    }
    layer.restore();
    if (!gridOnly) {
      drawLabel(layer, uy, bounds, range);
    }
  }

  private void drawVerticalLine(Layer layer, Bounds bounds) {
    layer.setFillColor(tickProperties.color);
    boolean isLeft = valueAxis.getAxisPanel().getPosition() == Position.LEFT;
    double dir = (isLeft ? bounds.width : 0);
    if ("inside".equals(axisProperties.tickPosition)) {
      boolean isInnerMost = valueAxis.getAxisPanel().getAxisNumber(valueAxis) == (isLeft ?
          valueAxis.getAxisPanel().getAxisCount() - 1 : 0);
      if (isInnerMost) {
        dir = isLeft ? bounds.width : -1;
      } else {
        double maxLabelWidth = rangeAxis.getMaxLabelWidth();
        dir = isLeft ? bounds.width - maxLabelWidth - 1
            : maxLabelWidth + 1;
      }
    }
    layer.fillRect(bounds.x + dir, bounds.y, tickProperties.lineThickness,
        bounds.y + bounds.height);
  }

  public TickPosition getTickPosition() {
    return "inside".equals(axisProperties.tickPosition) ? TickPosition.INSIDE
        : TickPosition.OUTSIDE;
  }

  public boolean isAxisLabelVisible() {
    return labelProperties.visible;
  }

  public enum TickPosition {
    INSIDE, OUTSIDE
  }
  
}
