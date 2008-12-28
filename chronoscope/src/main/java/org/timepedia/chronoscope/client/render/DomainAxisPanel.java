package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.ChronoscopeOptions;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.render.domain.TickFormatter;
import org.timepedia.chronoscope.client.render.domain.TickFormatterFactory;
import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.chronoscope.client.util.date.ChronoDate;

/**
 * Renders zoomable dates on x-axis (domain axis).
 */
public class DomainAxisPanel extends RangeAxisPanel {

  private static TickFormatterFactory tickFormatFactory = TickFormatterFactory
      .get();

  private static final String CREDITS = "Powered by Timefire";

  private static final String AXIS_LABEL = ""; // (Time)

  private static final int SUB_TICK_HEIGHT = 3;
  
  private static final int TICK_HEIGHT = 6;
  
  private boolean boundsSet = false;

  private Label creditsLabel;
  
  private Label domainAxisLabel;
  
  private GssElement gridGssElement, tickGssElement;

  private GssProperties gridProperties, tickProperties;

  private double minTickSize = -1;
  
  private double myHeight;
  
  public DomainAxisPanel() {
    gridGssElement = new GssElementImpl("grid", this);
    tickGssElement = new GssElementImpl("tick", this);
  }

  public void draw(Layer layer, Bounds bounds) {

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
    final int maxTicksForScreen = calcMaxTicksForScreen(layer, bounds,
        domainWidth, tlf);
    final int idealTickStep = tlf
        .calcIdealTickStep(domainWidth, maxTicksForScreen);
    ChronoDate tickDate = tlf
        .quantizeDate(plot.getDomain().getStart(), idealTickStep);

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
          drawTick(layer, plot, bounds, tickScreenPos, TICK_HEIGHT);
          drawTickLabel(layer, bounds, tickScreenPos, tickLabel, labelWidth);
        }
      }

      // Draw auxiliary sub-ticks
      if (!isFirstTick) {
        int subTickStep = tlf.getSubTickStep(actualTickStep);
        if (subTickStep > 1) {
          double auxTickWidth = (tickScreenPos - prevTickScreenPos)
              / subTickStep;
          double auxTickPos = prevTickScreenPos + auxTickWidth;
          for (int i = 0; i < subTickStep - 1; i++) {
            if (MathUtil.isBounded(auxTickPos, bounds.x, boundsRightX)) {
              drawTick(layer, plot, bounds, auxTickPos, SUB_TICK_HEIGHT);
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
      this.domainAxisLabel.draw(layer);
      drawAxisLabels(layer, bounds);
    }
  }

  @Override
  public double getHeight() {
    return myHeight;
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
    return plot.getInnerBounds().width;
  }

  @Override
  protected void initHook() {
    if (!parentPanel.getPosition().isHorizontal()) {
      throw new RuntimeException("DomainAxisPanel only works in a horizontal panel");
    }
    
    tickProperties = view.getGssProperties(tickGssElement, "");
    gridProperties = view.getGssProperties(gridGssElement, "");
    
    Layer rootLayer = view.getCanvas().getRootLayer();
    
    domainAxisLabel = new Label(AXIS_LABEL, this.textLayerName, 
        rootLayer, this.labelProperties);
    
    creditsLabel = new Label(CREDITS, this.textLayerName,
        rootLayer, "Veranda", "normal", "9pt");
    
    myHeight = getLabelHeight(rootLayer, "X") + TICK_HEIGHT + 
        creditsLabel.getBounds().height + 1;
  }

  private void clearAxis(Layer layer, Bounds bounds) {
    layer.save();
    layer.setFillColor(gssProperties.bgColor);
    layer.fillRect(bounds.x - 1, bounds.y - 1, bounds.width + 2,
        bounds.height + 2);
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
   * Converts the specified domain width (e.g. '2 years') into a screen pixel
   * width.
   */
  private double domainToScreenWidth(double domainWidth, Bounds bounds) {
    return bounds.width * (valueAxis.dataToUser(domainWidth) - valueAxis
        .dataToUser(0.0));
  }

  private double domainToScreenX(double dataX, Bounds bounds) {
    return bounds.x + valueAxis.dataToUser(dataX) * bounds.width;
  }

  private void drawAxisLabels(Layer layer, Bounds bounds) {
    layer.setFillColor(labelProperties.bgColor);
    layer.setStrokeColor(labelProperties.color);
    
    final double textRowY = bounds.bottomY() - creditsLabel.getBounds().height;
    final double halfLabelWidth = domainAxisLabel.getBounds().width / 2;
    
    domainAxisLabel.setLocation(bounds.midpointX() - halfLabelWidth, textRowY);
    domainAxisLabel.draw(layer);

    // only show if enabled and a collision with the axis label is avoided
    creditsLabel.setLocation(bounds.rightX() - creditsLabel.getBounds().width, textRowY);
    final boolean collision = domainAxisLabel.getBounds().rightX() >= creditsLabel.getBounds().x;
    if (ChronoscopeOptions.isShowCreditsEnabled() && !collision) {
      layer.save();
      layer.setTransparency(0.2f);
      creditsLabel.draw(layer);
      layer.restore();
    }
  }

  private void drawHorizontalLine(Layer layer, Bounds bounds) {
    layer.setStrokeColor(tickProperties.color);
    layer.setLineWidth(tickProperties.lineThickness);
    layer.moveTo(bounds.x, bounds.y);
    layer.lineTo(bounds.rightX(), bounds.y);
    layer.stroke();
  }

  private void drawTickLabel(Layer layer, Bounds bounds, double ux,
      String tickLabel, double tickLabelWidth) {
    layer.setStrokeColor(labelProperties.color);
    layer.setFillColor(labelProperties.bgColor);
    layer.drawText(ux - tickLabelWidth / 2, bounds.y + 5, tickLabel,
        gssProperties.fontFamily, gssProperties.fontWeight,
        gssProperties.fontSize, textLayerName, Cursor.DEFAULT);
  }
  
  private void drawTick(Layer layer, XYPlot plot, Bounds bounds, double ux,
      int tickLength) {
    layer.save();
    layer.setFillColor(tickProperties.color);
    layer.fillRect(ux, bounds.y, tickProperties.lineThickness, tickLength);

    if (gridProperties.visible) {
      Layer plotLayer = plot.getPlotLayer();
      plotLayer.save();
      plotLayer.setFillColor(gridProperties.color);
      plotLayer.setTransparency((float) gridProperties.transparency);
      plotLayer.fillRect(ux - bounds.x, 0, gridProperties.lineThickness,
          plot.getInnerBounds().height);
      plotLayer.restore();
    }
    
    layer.restore();
  }

  /**
   * Calculates the maximum number of ticks that can visually fit on the domain
   * axis given the visible screen width and the max width of a tick label for
   * the specified {@link TickFormatter}.
   */
  private int calcMaxTicksForScreen(Layer layer, Bounds bounds,
      double domainWidth, TickFormatter tlf) {

    // Needed to round screen width due to tiny variances that were causing the 
    // result of this method to fluctuate by +/- 1.
    double screenWidth = Math.round(domainToScreenWidth(domainWidth, bounds));

    double maxLabelWidth = 15 + tlf.getMaxTickLabelWidth(layer, gssProperties);

    return (int) (screenWidth / maxLabelWidth);
  }

  private int getLabelHeight(Layer layer, String str) {
    return layer.stringHeight(str, gssProperties.fontFamily, 
        gssProperties.fontWeight, gssProperties.fontSize);
  }

}
