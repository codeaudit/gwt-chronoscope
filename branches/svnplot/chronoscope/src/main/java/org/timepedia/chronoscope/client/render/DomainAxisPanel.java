package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.render.domain.TickFormatter;
import org.timepedia.chronoscope.client.render.domain.TickFormatterFactory;
import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.chronoscope.client.util.date.ChronoDate;

/**
 * Renders zoomable dates on x-axis (domain axis).
 */
public class DomainAxisPanel extends AxisPanel {
  
  private static TickFormatterFactory tickFormatFactory = TickFormatterFactory.get();
  
  private static final String CREDITS = "Powered by Timepedia Chronoscope";

  private static final String CREDITS_FONT = "Verdana";

  private static final String CREDITS_WEIGHT = "normal";

  private static final String CREDITS_SIZE = "9pt";

  private static final String TIME_LABEL = ""; // (Time)

  private int axisLabelWidth, axisLabelHeight;

  private boolean boundsSet = false;

  private int creditsWidth, creditsHeight;

  private GssElement gridGssElement, tickGssElement;
  
  private GssProperties gridProperties, tickProperties;

  private int maxLabelWidth, maxLabelHeight;

  private double minTickSize = -1;

  public DomainAxisPanel() {
    gridGssElement = new GssElementImpl("grid", this);
    tickGssElement = new GssElementImpl("tick", this);
  }
  
  public void draw(Layer layer, Bounds bounds) {
    //init();

    if (!GRID_ONLY) {
      clearAxis(layer, bounds);
      drawHorizontalLine(layer, bounds);
    }

    // TODO: cache this based on domainWidth(?)  
    // This stuff shouldn't change in the case where the user is just scrolling 
    // left/right.
    final double domainWidth = plot.getDomain().length();
    TickFormatter tlf = tickFormatFactory.findBestFormatter(domainWidth);
    final double boundsRightX = bounds.rightX();
    final double labelWidth = tlf.getMaxTickLabelWidth(layer, gssProperties);
    final double labelWidthDiv2 = labelWidth / 2.0;
    final int maxTicksForScreen = calcMaxTicksForScreen(layer, bounds, domainWidth, tlf);
    final int idealTickStep = tlf.calcIdealTickStep(domainWidth, maxTicksForScreen);
    ChronoDate tickDate = tlf.quantizeDate(plot.getDomain().getStart(), idealTickStep);

    boolean stillEnoughSpace = true; // enough space to draw another tick+label?
    boolean isFirstTick = true;
    double prevTickScreenPos = 0.0;
    int actualTickStep = 0;
    while (stillEnoughSpace) {
      double tickScreenPos = this.domainToScreenX(tickDate.getTime(), bounds);
      stillEnoughSpace = (tickScreenPos + labelWidthDiv2 < boundsRightX);
      if (stillEnoughSpace) {
        // Quantized tick date may have gone off the left edge; need to guard
        // against this case.
        if (tickScreenPos > bounds.x) {
          String tickLabel = tlf.formatRelativeTick(tickDate);
          drawTick(layer, plot, bounds, tickScreenPos, 6);
          drawLabel(layer, bounds, tickScreenPos, tickLabel, labelWidth);
        }
      }

      // Draw auxiliary sub-ticks
      if (!isFirstTick) {
        int subTickStep = tlf.getSubTickStep(actualTickStep);
        if (subTickStep > 1) {
          double auxTickWidth = (tickScreenPos - prevTickScreenPos) / subTickStep;
          double auxTickPos = prevTickScreenPos + auxTickWidth;
          for (int i = 0; i < subTickStep - 1; i++) {
            if (MathUtil.isBounded(auxTickPos, bounds.x, boundsRightX)) {
              drawTick(layer, plot, bounds, auxTickPos, 3);
            }
            auxTickPos += auxTickWidth;
          }
        }
      }
      
      actualTickStep = tlf.incrementDate(tickDate, idealTickStep);
      prevTickScreenPos = tickScreenPos;
      isFirstTick = false;
    }
    
    if (labelProperties.visible) {
      drawAxisLabel(layer, bounds);
    }
  }

  @Override
  public double getHeight() {
    if (parentPanel.getPosition().isHorizontal()) {
      return maxLabelHeight + 5 + axisLabelHeight + 2;
    } else {
      return plot.getInnerBounds().height;
    }
  }
 
  public int getLabelHeight(View view, String str) {
    return view.getCanvas().getRootLayer().stringHeight(str,
        gssProperties.fontFamily, gssProperties.fontWeight,
        gssProperties.fontSize);
  }

  public int getLabelWidth(View view, String str) {
    return view.getCanvas().getRootLayer().stringWidth(str,
        gssProperties.fontFamily, gssProperties.fontWeight,
        gssProperties.fontSize);
  }

  public double getMinimumTickSize() {
    if (minTickSize == -1) {
      TickFormatter leafFormatter = tickFormatFactory.getLeafFormatter();
      minTickSize = leafFormatter.getTickInterval().ms();
    }
    return minTickSize;
  }

  public String getType() {
    return "axis";
  }
  
  public String getTypeClass() {
    return "domain";
  }
  
  @Override
  public double getWidth() {
    if (parentPanel.getPosition().isHorizontal()) {
      return plot.getInnerBounds().width;
    } else {
      return maxLabelWidth + 5 + axisLabelWidth + 10;
    }
  }

  @Override
  protected void initHook() {
    tickProperties = view.getGssProperties(tickGssElement, "");
    gridProperties = view.getGssProperties(gridGssElement, "");
    creditsWidth = view.getCanvas().getRootLayer()
        .stringWidth(CREDITS, CREDITS_FONT, CREDITS_WEIGHT, CREDITS_SIZE);
    creditsHeight = view.getCanvas().getRootLayer()
        .stringHeight(CREDITS, CREDITS_FONT, CREDITS_WEIGHT, CREDITS_SIZE);

    boolean isHorizontal = parentPanel.getPosition().isHorizontal();
    final String axisLabel = "(Time)";

    maxLabelWidth = getLabelWidth(view, "XXX'00");
    maxLabelHeight = getLabelHeight(view, "XXXX");
    axisLabelWidth = getLabelWidth(view, isHorizontal ? axisLabel : "X");

    axisLabelHeight = 0;
    if (isAxisLabelVisible()) {
      if (isHorizontal) {
        axisLabelHeight = getLabelHeight(view, axisLabel);
      } else {
        axisLabelHeight = getLabelHeight(view, "X") * axisLabel.length();
      }
    }

  }

  public boolean isAxisLabelVisible() {
    return labelProperties.visible;
  }

  private void clearAxis(Layer layer, Bounds bounds) {
    layer.save();
    layer.setFillColor(gssProperties.bgColor);
    layer.fillRect(bounds.x-1, bounds.y-1, bounds.width+2, bounds.height+2);
//    layer.setStrokeColor("rgba(0,0,0,0)");
//    layer.setLineWidth(0.0);
//    layer.setShadowColor("rgba(0,0,0,0)");
//    layer.setShadowBlur(0);
//    layer.setShadowOffsetX(0);
//    layer.setShadowOffsetY(0);
//    layer.translate(bounds.x-1, bounds.y-1);
//    layer.scale(bounds.width+2, bounds.height+2);
//    layer.beginPath();
//    layer.rect(0, 0, 1, 1);
//    layer.closePath();
  //  layer.stroke();
//    layer.fill();
    if (!boundsSet) {
      layer.setTextLayerBounds(textLayerName, bounds);
      boundsSet = true;
    }
    layer.clearTextLayer(textLayerName);
    layer.restore();
  }

  /**
   * Converts the specified domain width (e.g. '2 years') into a screen pixel width.
   */
  private double domainToScreenWidth(double domainWidth, Bounds bounds) {
    return bounds.width * (valueAxis.dataToUser(domainWidth) - valueAxis.dataToUser(0.0));
  }
  
  private double domainToScreenX(double dataX, Bounds bounds) {
    return bounds.x + valueAxis.dataToUser(dataX) * bounds.width;
  }

  private void drawAxisLabel(Layer layer, Bounds bounds) {
    layer.setFillColor(labelProperties.bgColor);
    layer.setStrokeColor(labelProperties.color);
    double center = bounds.x + (bounds.width / 2);
    
    double halfLabelWidth = axisLabelWidth / 2;
    layer.drawText(center - halfLabelWidth,
        bounds.y + maxLabelHeight + 5, TIME_LABEL,
        labelProperties.fontFamily, labelProperties.fontWeight,
        labelProperties.fontSize, textLayerName, Cursor.DEFAULT);
    // only show if enabled and a collision with the axis label is avoided
    if (Chronoscope.isShowCreditsEnabled()
        && center + halfLabelWidth < bounds.x + bounds.width - creditsWidth) {
      layer.save();
      layer.setTransparency(0.2f);
      layer.drawText(bounds.x + bounds.width - creditsWidth,
          bounds.y + bounds.height - creditsHeight, CREDITS, CREDITS_FONT,
          CREDITS_WEIGHT, CREDITS_SIZE, textLayerName, Cursor.DEFAULT);
      layer.restore();
    }
  }

  private void drawHorizontalLine(Layer layer, Bounds bounds) {
    layer.setStrokeColor(tickProperties.color);
    layer.setLineWidth(tickProperties.lineThickness);
    layer.moveTo(bounds.x, bounds.y);
    layer.lineTo(bounds.x + bounds.width, bounds.y);
    layer.stroke();
  }
  
  private void drawLabel(Layer layer, Bounds bounds, double ux, String tickLabel,  
      double tickLabelWidth) {
    layer.setStrokeColor(labelProperties.color);
    layer.setFillColor(labelProperties.bgColor);
    layer.drawText(ux - tickLabelWidth / 2, bounds.y + 5, tickLabel,
        gssProperties.fontFamily, gssProperties.fontWeight,
        gssProperties.fontSize, textLayerName, Cursor.DEFAULT);
  }

  private void drawTick(Layer layer, XYPlot plot, Bounds bounds, double ux, int tickLength) {
    layer.save();
    layer.setFillColor(tickProperties.color);
    layer.fillRect(ux, bounds.y, tickProperties.lineThickness, tickLength);

    if (gridProperties.visible) {
      Layer player = plot.getPlotLayer();
      player.save();
      player.setFillColor(gridProperties.color);
      player.setTransparency((float) gridProperties.transparency);
      player.fillRect(ux - bounds.x, 0, gridProperties.lineThickness,
          plot.getInnerBounds().height);
      player.restore();
    }
    layer.restore();
  }
  
  /**
   * Calculates the maximum number of ticks that can visually fit on the 
   * domain axis given the visible screen width and the max width of a tick 
   * label for the specified {@link TickFormatter}. 
   */
  private int calcMaxTicksForScreen(Layer layer, Bounds bounds, double domainWidth, 
      TickFormatter tlf) {
    
    // Needed to round screen width due to tiny variances that were causing the 
    // result of this method to fluctuate by +/- 1.
    double screenWidth = Math.round(domainToScreenWidth(domainWidth, bounds));
    
    double maxLabelWidth = 15 + tlf.getMaxTickLabelWidth(layer, gssProperties);
    
    //log("domainWidth=" + (long)domainWidth + "; screenWidth=" + screenWidth + "; maxLabelWidth=" + maxLabelWidth + "; maxTicks=" + (int)(screenWidth / maxLabelWidth));
    return (int)(screenWidth / maxLabelWidth);
  }

}
