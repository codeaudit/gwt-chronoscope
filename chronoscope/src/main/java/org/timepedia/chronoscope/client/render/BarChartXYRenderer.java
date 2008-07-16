package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Focus;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;

/**
 * Renders BarCharts on an XYPlot.
 */
public class BarChartXYRenderer extends XYRenderer implements GssElement {

  private GssProperties disabledLineProperties;

  private GssProperties disabledPointProperties;

  private GssProperties focusGssProperties;

  private FocusPainter focusPainter;

  private GssProperties gssHoverPointProperties;

  private boolean gssInited = false;

  private GssProperties gssLineProperties;

  private GssProperties gssPointProperties;

  private double interval = -1;

  private double lx = -1;

  private double offset = -1;

  private GssElement parentSeriesElement = null;

  private GssElement pointElement = null;

  private boolean prevFocus = false;

  private boolean prevHover = false;

  private final int seriesNum;

  public BarChartXYRenderer(int seriesNum) {

    this.seriesNum = seriesNum;
    parentSeriesElement = new GssElementImpl("series", null, "s" + seriesNum);
    pointElement = new GssElementImpl("point", parentSeriesElement);
  }

  public void beginCurve(XYPlot plot, Layer layer, boolean inSelection,
      boolean isDisabled) {
    initGss(plot.getChart().getView());

    GssProperties prop = isDisabled ? disabledLineProperties
        : gssLineProperties;
    layer.save();
    layer.setLineWidth(prop.lineThickness);
    layer.setTransparency((float) prop.transparency);
    layer.setShadowBlur(prop.shadowBlur);
    layer.setShadowColor(prop.shadowColor);
    layer.setShadowOffsetX(prop.shadowOffsetX);
    layer.setShadowOffsetY(prop.shadowOffsetY);

    lx = -1;
    prevHover = false;
  }

  public void beginPoints(XYPlot plot, Layer layer, boolean inSelection,
      boolean disabled) {
  }

  public double calcLegendIconWidth(XYPlot plot) {
    GssProperties apointProp = 
      (plot.getFocus() != null) ? gssPointProperties 
                                : disabledPointProperties;
    return apointProp.size + 10;
  }

 public void drawCurvePart(XYPlot plot, Layer layer, double dataX,
      double dataY, int seriesNum, boolean isFocused, boolean isHovered,
      boolean inSelection, boolean isDisabled) {
    double ux = plot.domainToScreenX(dataX, seriesNum);
    double uy = plot.rangeToScreenY(dataY, seriesNum);
    GssProperties barProp = isDisabled ? disabledLineProperties
        : gssLineProperties;
    GssProperties pointProp = isDisabled ? disabledPointProperties
        : gssPointProperties;
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

        double barHeight = plot.getInnerPlotBounds().height + plot
            .getInnerPlotBounds().y;
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

        if (prevHover) {
          pointProp = gssHoverPointProperties;
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

    prevHover = isHovered;
    prevFocus = isFocused;
  }

  public Bounds drawLegendIcon(XYPlot plot, Layer layer, double x, double y,
      int seriesNum) {
    layer.save();
    initGss(layer.getCanvas().getView());
    
    GssProperties lineProp, pointProp;
    Focus focus = plot.getFocus();
    if (focus != null && focus.getDatasetIndex() != seriesNum) {
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

  public void drawPoint(XYPlot plot, Layer layer, double x, double y,
      int seriesNum, boolean focused, boolean hovered, boolean inSelection,
      boolean disabled) {
  }

  public void endCurve(XYPlot plot, Layer layer, boolean inSelection,
      boolean isDisabled, int seriesNum) {
    layer.restore();
  }

  public void endPoints(XYPlot plot, Layer layer, boolean inSelection,
      boolean disabled, int seriesNum) {
  }

  public double getInterval() {
    return interval;
  }

  public double getOffset() {
    return offset;
  }

  public GssElement getParentGssElement() {
    return parentSeriesElement;
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
}