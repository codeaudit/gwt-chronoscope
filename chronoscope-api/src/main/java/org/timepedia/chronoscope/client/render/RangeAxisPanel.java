package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.render.CompositeAxisPanel.Position;
import org.timepedia.chronoscope.client.util.MathUtil;

/**
 * Renders a vertical range axis.
 */
public class RangeAxisPanel extends AxisPanel {

  public RangeAxisPanel() {
    new RuntimeException().printStackTrace();
  }

  public enum TickPosition {
    INSIDE, OUTSIDE;

    public static TickPosition is(String s) {
      try {
        return valueOf(s.trim().toUpperCase());
      } catch (Exception e) {
        return INSIDE;
      }
    }
    public String toString(){
        return name().toLowerCase();
    }
  }
  public enum TickAlignment {
    ABOVE, MIDDLE, BELOW;

    public static TickAlignment is(String s) {
      try {
        return valueOf(s.trim().toUpperCase());
      } catch (Exception e) {
        return ABOVE;
      }
    }
    public String toString(){
        return name().toLowerCase();
    }
  }

  private boolean boundsSet;
  private double axisLabelWidth, maxLabelWidth, maxLabelHeight;
  private double rotationAngle;
  private Bounds drawBounds = new Bounds();

  private GssProperties gridProperties, tickProperties;
  private RangeAxis rangeAxis;

  public void dispose() {
    super.dispose();
    gridProperties = null;
    tickProperties = null;
    rangeAxis = null;
  }

  public void remove(Panel panel) {
    return; // no sub panels
  }

  public void computeLabelWidths(View view) {
    log ("computeLabelWidths bounds "+bounds);
    if (null == layer) {
      layer = parent.getLayer();
    }

    final String valueAxisLabel = valueAxis.getLabel();
    // TODO - really walk the ticklabels and compute the max width
    layer.save();

    maxLabelWidth = StringSizer.getWidth(layer, getDummyLabel(), gssProperties)+1;
    maxLabelHeight = StringSizer.getHeight(layer, getDummyLabel(), gssProperties)+2;
    axisLabelWidth = StringSizer.getRotatedWidth(layer, valueAxisLabel, gssProperties, rotationAngle);

    layer.restore();
  }

  public void draw() {
    bounds.copyTo(drawBounds);
    log("draw bounds:"+bounds + " layerOffsetX:"+getLayerOffsetX() + " layerOffsetY:"+getLayerOffsetY());
    // drawBounds.x += getLayerOffsetX();
    // drawBounds.y += getLayerOffsetY();

    double tickPositions[] = rangeAxis.calcTickPositions();

    layer.save();

    if (!GRID_ONLY) {
      // layer.clear(); // (layer, drawBounds);
      drawLine(layer, drawBounds);
    }
    drawAxisLabel(layer, drawBounds, plot.getChart());

    layer.setTransparency((float)gssProperties.transparency);
    layer.setFillColor(gssProperties.bgColor);
    layer.setStrokeColor(gssProperties.color);

    final double axisInterval = valueAxis.getExtrema().length();
    for (int i = 0; i < tickPositions.length; i++) {
      drawTick(layer, tickPositions[i], tickPositions[0], axisInterval, drawBounds, GRID_ONLY);
    }

    layer.restore();
  }

//  public String formatLegendLabel(double value) {
//    return rangeAxis.getFormattedLabel(value);
//  }

  public double getMaxLabelHeight() {
    return maxLabelHeight;
  }

  public double getMaxLabelWidth() {
    return maxLabelWidth;
  }

  public String getType() {
    return "axis";
  }

  public String getTypeClass() {
    return "range a" + ((RangeAxis) valueAxis).getAxisIndex();
  }

  @Override
  public void layout() {
    computeLabelWidths(view);
    bounds.width = calcWidth();
    setBounds(bounds);
    // height not calculated -- it must be dictated by some external entity
  }

  @Override
  protected void initHook() {
    rangeAxis = (RangeAxis) this.valueAxis;
    GssElement tickGssElem = new GssElementImpl("tick", this);
    GssElement gridGssElem = new GssElementImpl("grid", this);
    tickProperties = view.getGssProperties(tickGssElem, "");
    gridProperties = view.getGssProperties(gridGssElem, "");

    if (getParentPosition() == Position.RIGHT) {
      rotationAngle = Math.PI / 2;
    } else {
      rotationAngle = -(Math.PI / 2);
    }
  }

  // TODO - not as variable as height, but axisLabelWidth is per axis, unlike maxLabelWidth
  private double calcWidth() {
    double w = 0.0;
    final double widthBuffer = 4;
    final double computedAxisLabelWidth = labelProperties.visible ?
        axisLabelWidth + widthBuffer : 0;

    boolean isLeft = getParentPosition() == Position.LEFT;
    if (isInnerMost(isLeft)) {
      if (getTickPosition() == RangeAxisPanel.TickPosition.INSIDE) {
        w = computedAxisLabelWidth;
      } else {
        w = maxLabelWidth + widthBuffer + computedAxisLabelWidth;
      }
    } else {
      w = maxLabelWidth + widthBuffer + computedAxisLabelWidth;
    }

    return w;
  }

/*
  private void clearAxis(Layer layer, Bounds bounds) {
    layer.save();
    if (!boundsSet) {
      layer.setTextLayerBounds(textLayerName, bounds);
      boundsSet = true;
    }
    layer.clearTextLayer(textLayerName);
    layer.setFillColor(gssProperties.bgColor);
    //layer.setShadowBlur(0);
    //layer.setShadowOffsetX(0);
    //layer.setShadowOffsetY(0);
    //layer.translate(bounds.x - 1, bounds.y - 1);
    // layer.scale(bounds.width + 1, bounds.height + 1);
    //layer.fillRect(0, 0, 1, 1);
    layer.restore();
  }
*/

  private void drawAxisLabel(Layer layer, Bounds bounds, Chart chart) {
    if (labelProperties.visible) {
      boolean isLeft = getParentPosition() == Position.LEFT;
      boolean isInnerMost = isInnerMost(isLeft);

      double x = isLeft ?
           (isInnerMost ? layer.getWidth() - axisLabelWidth : axisLabelWidth) + 5:
           (isInnerMost ? layer.getWidth() - axisLabelWidth : maxLabelWidth + 2) - 1;
      String label = valueAxis.getLabel();

      layer.save();
      int labelHeight = StringSizer.getRotatedHeight(layer, label, gssProperties, rotationAngle);
      // double y = bounds.y + ((bounds.height - maxLabelHeight) / 2) + (labelHeight / 2);
      double y = ((bounds.height - maxLabelHeight) / 2) + (labelHeight / 2);
      layer.setStrokeColor(labelProperties.color);

      layer.drawRotatedText(x, y, rotationAngle, label, gssProperties.fontFamily,
              gssProperties.fontWeight, gssProperties.fontSize, textLayerName, chart);
      layer.restore();
    }
  }

  private void drawLabel(Layer layer, double y, Bounds bounds, double value) {
    log(layer.getLayerId()+" drawLabel "+value +" "+bounds);
    String label = rangeAxis.getFormattedLabel(value);
//    double labelWidth = layer.stringWidth(label,
//            gssProperties.fontFamily, gssProperties.fontWeight, gssProperties.fontSize);
//    double labelHeight = layer.stringHeight(label,
//            gssProperties.fontFamily, gssProperties.fontWeight, gssProperties.fontSize);
    boolean isLeft = getParentPosition() == Position.LEFT;
    double dir = (isLeft ? -5 - maxLabelWidth : 5 - bounds.width);
    if (TickPosition.INSIDE == TickPosition.is(gssProperties.tickPosition)) {
      dir = isLeft ? 5 : -maxLabelWidth - 5;
    }
    double alignAdjust = Math.floor(-maxLabelHeight / 2.0);
    if (TickAlignment.ABOVE == TickAlignment.is(labelProperties.tickAlign)) {
      // alignAdjust = -maxLabelHeight;
      alignAdjust = -2;
      dir = 1;

      if (isInnerMost(isLeft)) {
        layer = plot.getPlotRangeLayer();
        layer.setFillColor(getTickProps(tickProperties).color);
        layer.setTransparency(1);
        dir = isLeft ? -bounds.width + 1 :  - maxLabelWidth + 1;
        drawLabelText(layer, bounds.width + dir, y + alignAdjust, label);
      } else {
        dir = isLeft ? (-maxLabelWidth + 2)
            : (-maxLabelWidth - axisLabelWidth - 10);
        drawLabelText(layer, bounds.width + dir, y + alignAdjust, label);
      }
    }
/*
    log(layer.getLayerId() + layer.getBounds()+"drawText isBounded("+y+", "+0+", "+bounds.height);
    if (MathUtil.isBounded(y, 0, bounds.height)) {
      // layer.drawText(bounds.rightX() + dir, y + alignAdjust, label,
      drawLabelText(layer, bounds.width + dir, y + alignAdjust, label);

    } else {
      log (layer.getLayerId() + layer.getBounds()+"drawText not bounded("+y+", "+0+", "+bounds.height);
    }
*/


  }


  private void drawLabelText(Layer layer, double x, double y, String text) {
    log(layer.getLayerId() + " drawLabelText "+x+", "+ y + " " + text);
    layer.save();
    // layer.setTransparency(1);
    layer.setStrokeColor(getTickProps(labelProperties).color);
    layer.setFillColor(labelProperties.bgColor);
    // layer.fillRect(bounds.rightX()+dir, y + alignAdjust,
    // bounds.rightX()+dir+labelWidth, y+alignAdjust+labelHeight);
    layer.drawText(x, y, text,
        gssProperties.fontFamily, gssProperties.fontWeight,
        gssProperties.fontSize, textLayerName, Cursor.CONTRASTED);

    layer.restore();
  }

  private void drawLine(Layer layer, Bounds bounds) {
    log(layer.getLayerId() + " drawLine "+ bounds);
    GssProperties tprop = getTickProps(tickProperties);
    log("drawLine setFill "+tprop.color);
    layer.setFillColor(tprop.color);
    boolean isLeft = getParentPosition() == Position.LEFT;
    double dir = (isLeft ? bounds.width : 0);
    if (TickPosition.INSIDE == TickPosition.is(gssProperties.tickPosition)) {
      if (isInnerMost(isLeft)) {
        dir = isLeft ? layer.getBounds().width - tickProperties.lineThickness : 0;
      } else {
        dir = isLeft ? bounds.width - maxLabelWidth-1  : layer.getBounds().width - maxLabelWidth - 21;
      }
    }
    // layer.fillRect(bounds.x + dir, maxLabelHeight, tickProperties.lineThickness, bounds.height-maxLabelHeight);
    layer.fillRect(dir, maxLabelHeight, tickProperties.lineThickness, bounds.height);
    }

  private GssProperties getTickProps(GssProperties defprop) {
    GssProperties tprop = defprop;
    if (false && !plot.isMultiaxis()) {
      int dIdx = plot.getFocus() != null ? plot.getFocus().getDatasetIndex()
          : -1;

      if (dIdx != -1) {
        GssProperties cprop = plot.getDatasetRenderer(dIdx).getCurveProperties()
            ;
        if (cprop != null) {
          tprop = cprop;
        }
      }
    }
    return tprop;
  }

  private void drawTick(Layer layer, double range, double rangeLow,
      double rangeInterval, Bounds bounds, boolean gridOnly) {

    // Determines the horizontal length (in pixels) of each range axis tick
    final int tickWidth = 5;
    double tickPixelHeight = ((range - rangeLow) / rangeInterval) * (bounds.height);
    // double uy = Math.round(bounds.y + maxLabelHeight + (bounds.height - tickPixelHeight));
    double uy = Math.min( bounds.height -1, Math.round(maxLabelHeight + (bounds.height - tickPixelHeight)));

    boolean isLeft = getParentPosition() == Position.LEFT;
    double dir = (isLeft ? (bounds.width - tickWidth) : 0);
    if (TickPosition.INSIDE == TickPosition.is(gssProperties.tickPosition)) {
      if (isInnerMost(isLeft)) {
        // if INSIDE the plot then use plotAxesLayer instead of the axis layer (outside plot area)
        layer = plot.getPlotRangeLayer();
        bounds = layer.getBounds();
        // dir = isLeft ? (bounds.width + 1) : -tickWidth;
        dir = isLeft ? 0 : bounds.width - tickWidth;
      } else {
        dir = isLeft ? (bounds.width - maxLabelWidth - 1)
            : (maxLabelWidth + tickWidth + 1);
      }
    }
    layer.setFillColor(getTickProps(tickProperties).color);
    layer.setTransparency(1);

    layer.save();
    layer.setFillColor(getTickProps(tickProperties).color);
    layer.setTransparency(1);
    if (!gridOnly) {
      drawLabel(layer, uy, bounds, range);
    }
    layer.restore();

    // double ux = Math.round(bounds.x + dir);
    double ux = dir;
    log(layer.getLayerId() + " fillRect "+ux + ", "+uy+", "+tickWidth+", "+tickProperties.lineThickness);
    layer.fillRect(ux, uy, tickWidth, tickProperties.lineThickness);
    if (gridProperties.visible && uy != bounds.height) {
      Layer gridlayer = plot.getOverlayLayer(); // TODO - should be background
      gridlayer.save();
      gridlayer.setFillColor(gridProperties.color);
      gridlayer.setTransparency((float) gridProperties.transparency);
      gridlayer.fillRect(0, uy, gridlayer.getBounds().width, gridProperties.lineThickness);
      gridlayer.restore();
    }
  }

  private String getDummyLabel() {
    int maxDig = RangeAxis.MAX_DIGITS;
    return "0" + ((maxDig == 1) ? "" : "." + "000000000".substring(0, maxDig - 1));
  }

  private TickPosition getTickPosition() {
      return TickPosition.is(gssProperties.tickPosition);
  }

  private TickAlignment getTickAlignment() {
      return TickAlignment.is(gssProperties.tickAlign);
  }

  private boolean isInnerMost(boolean isLeftPanel) {
    CompositeAxisPanel parentContainer = (CompositeAxisPanel) this.parent;

    return parentContainer.indexOf(this) == (isLeftPanel ?
        parentContainer.getChildCount() - 1 : 0);
  }

  private Position getParentPosition() {
    return ((CompositeAxisPanel) parent).getPosition();
  }


  private static void log(Object msg) {
    System.out.println("RangeAxisPanel> " + msg);
  }
}
