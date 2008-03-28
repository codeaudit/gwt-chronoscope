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

  private GssElementImpl tickGssElem;

  private GssProperties tickLabelProperties;

  private String labelFormat;

  private boolean scientificNotationOn;

  public RangeAxisRenderer(RangeAxis rangeAxis) {
    this.axis = rangeAxis;
  }

  public void drawAxis(XYPlot plot, Layer layer, Bounds axisBounds,
      boolean gridOnly) {
    double tickPositions[] = axis.computeTickPositions(axis.getRangeLow(),
        axis.getRangeHigh(), axis.getHeight(), axis.getMaxLabelHeight());

    double rangeHigh = axis.getRangeHigh();
    for(int i=0; i<tickPositions.length; i++) {
      if(tickPositions[i] >= rangeHigh) {
       rangeHigh=tickPositions[i];
        break;
      }
    }
    
    if (!gridOnly) {
      clearAxis(layer, axisBounds);
      drawVerticalLine(layer, axisBounds);
    }

    layer.setTransparency(1.0f);
    layer.setFillColor("rgba(255,255,255,255)");
    layer.setStrokeColor("rgb(0,255,0)");

    double axisRange = rangeHigh - tickPositions[0];
    labelFormat = null;

    for (int i = 0; i < tickPositions.length; i++) {
      drawTick(plot, layer, tickPositions[i], tickPositions[0], axisRange,
          axisBounds, gridOnly);
    }

    if (!gridOnly) {
      drawAxisLabel(layer, axisBounds);
    }
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

  public GssElement getParentGssElement() {
    return axis.getAxisPanel();
  }

  public String getType() {
    return "axis";
  }

  public String getTypeClass() {
    return "a" + axis.getAxisNumber();
  }

  public void init(View view) {
    if (axisProperties == null) {
      axisProperties = view.getGssProperties(this, "");
      labelProperties = view
          .getGssProperties(new GssElementImpl("label", this), "");
      tickGssElem = new GssElementImpl("tick", this);
      tickProperties = view
          .getGssProperties(tickGssElem, "");
      tickLabelProperties = view
          .getGssProperties(new GssElementImpl("label", tickGssElem), "");
      gridProperties = view
          .getGssProperties(new GssElementImpl("grid", this), "");
      textLayerName = axis.getAxisPanel().getPanelName() + axis.getAxisPanel()
          .getAxisNumber(axis);
    }
//        view.getCanvas().setStrokeColor(axisProperties.color);
  }

  public boolean isScientificNotationOn() {
    return scientificNotationOn;
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

  private void drawAxisLabel(Layer layer, Bounds bounds) {
    if (labelProperties.visible) {
      double dir = (axis.getAxisPanel().getPosition() == AxisPanel.LEFT ? bounds
          .width - axis.getMaxLabelWidth() - 10 - axis.getAxisLabelWidth()
          : axis.getMaxLabelWidth());
      double x = bounds.x + dir;
      double y = bounds.y + bounds.height / 2 - axis.getAxisLabelHeight() / 2;
      layer.setStrokeColor(axisProperties.color);
      String label = axis.getLabel();

      layer.drawRotatedText(x, y, axis.getRotationAngle(), label,
          axisProperties.fontFamily, axisProperties.fontWeight,
          axisProperties.fontSize, textLayerName, axis.getChart());
    }
  }

  private void drawLabel(Layer layer, double y, Bounds bounds, double value) {

    computeLabelFormat(axis.getRange());
    String label = getFormattedLabel(value, layer.getCanvas().getView());

    double labelWidth = layer.stringWidth(label, axisProperties.fontFamily,
        axisProperties.fontWeight, axisProperties.fontSize);
    double labelHeight = layer.stringHeight(label, axisProperties.fontFamily,
        axisProperties.fontWeight, axisProperties.fontSize);
    double dir = (axis.getAxisPanel().getPosition() == AxisPanel.LEFT ? -5
        - labelWidth : 5 - bounds.width);
    layer.setStrokeColor(labelProperties.color);
    layer.setFillColor(labelProperties.bgColor);

    if (y >= bounds.y && y <= bounds.y + bounds.height) {
      layer.drawText(bounds.x + bounds.width + dir, y - labelHeight / 2, label,
          axisProperties.fontFamily, axisProperties.fontWeight,
          axisProperties.fontSize, textLayerName);
    }
  }

  private void drawTick(XYPlot plot, Layer layer, double range, double rangeLow,
      double rangeSize, Bounds bounds, boolean gridOnly) {
    double uy =
        (bounds.height - ((range - rangeLow) / rangeSize * bounds.height))
            + bounds
            .y;
    double dir = (axis.getAxisPanel().getPosition() == AxisPanel.LEFT ? -5
        + bounds.width : 0);
    layer.save();
    layer.setFillColor(tickProperties.color);

    layer.fillRect(bounds.x + dir, uy, 5, tickProperties.lineThickness);
    if (gridProperties.visible && uy != bounds.y + bounds.height) {
      layer.setFillColor(gridProperties.color);
      layer.setTransparency((float) gridProperties.transparency);
      layer.fillRect(bounds.x + bounds.width, uy, plot.getPlotBounds().width,
          gridProperties.lineThickness);
    }
    layer.restore();
    if (!gridOnly) {
      drawLabel(layer, uy, bounds, range);
    }
  }

  private void drawVerticalLine(Layer layer, Bounds bounds) {
    layer.setFillColor(tickProperties.color);
    double dir = (axis.getAxisPanel().getPosition() == AxisPanel.LEFT
        ? bounds.width : 0);

    layer.fillRect(bounds.x + dir, bounds.y, tickProperties.lineThickness,
        bounds.y + bounds.height);
  }

  private String getFormattedLabel(double label, View view) {
    if(!Double.isNaN(axis.getScale()))
      label /= axis.getScale();

    String lab=view.numberFormat(labelFormat, label);
    return lab;
  }

  private void computeLabelFormat(double label) {
    if (labelFormat == null) {
      int intDigits = (int) Math.floor(Math.log(label) / Math.log(10));
      if (axis.isForceScientificNotation() || (axis.isAllowScientificNotation()
          && (intDigits + 1 > axis.getMaxDigits() || Math.abs(intDigits) > axis
          .getMaxDigits()))) {
        labelFormat = "0." + "0#########".substring(axis.getMaxDigits()) + "E0";
        scientificNotationOn = true;
      } else if (intDigits > 0) {
        String digStr = "#########0";
        labelFormat = digStr.substring(digStr.length() - intDigits);
        int leftOver = Math.max(axis.getMaxDigits() - intDigits, 0);
        if (leftOver > 0) {
          labelFormat += "." + "0#########".substring(leftOver);
        }
        scientificNotationOn = false;
      } else {
        labelFormat = "0." + "0#########".substring(axis.getMaxDigits());
        scientificNotationOn = false;
      }
    }
  }
}
