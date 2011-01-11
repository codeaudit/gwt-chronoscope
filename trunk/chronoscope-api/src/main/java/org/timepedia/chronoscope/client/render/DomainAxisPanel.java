package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.ChronoscopeOptions;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.overlays.OverlayClickListener;
import org.timepedia.chronoscope.client.render.domain.DateTickFormatter;
import org.timepedia.chronoscope.client.render.domain.DateTickFormatterFactory;
import org.timepedia.chronoscope.client.render.domain.TickFormatter;
import org.timepedia.chronoscope.client.render.domain.TickFormatterFactory;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 * Renders zoomable dates on x-axis (domain axis).
 */
@ExportPackage("chronoscope")
public class DomainAxisPanel extends AxisPanel implements Exportable {

  private static final String CREDITS = "Powered by Timefire";

  private static final String AXIS_LABEL = ""; // (Time)

  private static final int SUB_TICK_HEIGHT = 3;

  private static final int TICK_HEIGHT = 6;

  private OverlayClickListener clickHandler;

// private static final void log(Object msg) {
//    System.out.println("TESTING DomainAxisPanel: " + msg);
//  }

  private boolean boundsSet = false;

  private Label creditsLabel;

  private Label domainAxisLabel;

  private GssElement gridGssElement, tickGssElement;

  private GssProperties gridProperties, tickProperties;

  private double minTickSize = -1;

  private static TickFormatterFactory tickFormatterFactory = new DateTickFormatterFactory();

  public DomainAxisPanel() {
    gridGssElement = new GssElementImpl("grid", this);
    tickGssElement = new GssElementImpl("tick", this);
  }

  public boolean click(int x, int y) {
    Bounds b = creditsLabel.getBounds();
    double nx = x - getBounds().x - getLayer().getBounds().x;
    double ny = y - getBounds().y - getLayer().getBounds().y;
    if (b.inside((int) nx, (int) ny)) {
      if (clickHandler != null) {
        clickHandler.onOverlayClick(null, x, y);
      }
    }
    return false;
  }

  public void draw() {
    if (!GRID_ONLY) {
      // clearAxis(layer, bounds);
      drawHorizontalLine(layer, bounds);
    }

      drawHorizontalLine(layer, bounds);


    // TODO: cache this based on domainWidth(?)  
    // This stuff shouldn't change in the case where the user is just scrolling 
    // left/right.
    final double domainWidth = plot.getDomain().length();
    TickFormatter tickFormatter = getBestFormatter(domainWidth);

    //log("best formatter for domain " + (long)domainWidth + ": " + tickFormatter);

    final double boundsRightX = bounds.rightX();
    final double labelWidth = tickFormatter
        .getMaxTickLabelWidth(layer, gssProperties);
    final double labelWidthDiv2 = labelWidth / 2.0;
    final int maxTicksForScreen = calcMaxTicksForScreen(layer, bounds,
        domainWidth, tickFormatter);
    final int idealTickStep = tickFormatter
        .calcIdealTickStep(domainWidth, maxTicksForScreen);
    //log("dw=" + (long)domainWidth + "; maxTicks=" + maxTicksForScreen + 
    //    "; idealStep=" + idealTickStep);
    
    tickFormatter
        .resetToQuantizedTick(plot.getDomain().getStart(), idealTickStep);

    boolean stillEnoughSpace = true; // enough space to draw another tick+label?
    boolean isFirstTick = true;
    double prevTickScreenPos = 0.0;
    int actualTickStep = 0;

//    log("idealTickStep=" + idealTickStep +
//        "; maxTicks=" + maxTicksForScreen +
//        "; domainStart=" + (long)plot.getDomain().getStart() +
//        "; domainLen=" + (long)plot.getDomain().length() +
//        "; quantizedDomainValue=" + (long)tickFormatter.getTickDomainValue() + 
//        "; idealTickStep=" + idealTickStep
//        );

    while (stillEnoughSpace) {
      double tickScreenPos = this.domainToScreenX(tickFormatter.getTickDomainValue(), bounds);
      stillEnoughSpace = tickScreenPos + labelWidthDiv2 < boundsRightX;
      
//      log("tickScreenPos=" + tickScreenPos + 
//          "; tickDomainValue=" + (long)tickFormatter.getTickDomainValue() +
//          "; boundsRightX=" + boundsRightX);

      if ((tickScreenPos - labelWidthDiv2) > 0 && stillEnoughSpace) {
        // Quantized tick date may have gone off the left edge; need to guard
        // against this case.
        if (tickScreenPos >= bounds.x) {
          String tickLabel = tickFormatter.format();
          boolean bold = tickFormatter.isBoundary(idealTickStep);
          drawTick(layer, plot, bounds, tickScreenPos, TICK_HEIGHT, bold);
          drawTickLabel(layer, bounds, tickScreenPos, tickLabel, bold, labelWidth);
        }
      }

      // Draw auxiliary sub-ticks
      if (!isFirstTick) {
        int subTickStep = tickFormatter.getSubTickStep(actualTickStep);
        if (subTickStep > 1) {
          double auxTickWidth = (tickScreenPos - prevTickScreenPos)
              / subTickStep;
          double auxTickPos = prevTickScreenPos + auxTickWidth;
          for (int i = 0; i < subTickStep - 1; i++) {
            if (MathUtil.isBounded(auxTickPos, bounds.x, boundsRightX)) {
              drawTick(layer, plot, bounds, auxTickPos, SUB_TICK_HEIGHT, false);
            }
            auxTickPos += auxTickWidth;
          }
        }
      }

      actualTickStep = tickFormatter.incrementTick(idealTickStep);
      prevTickScreenPos = tickScreenPos;
      isFirstTick = false;
    }

    if (labelProperties.visible) {
      this.domainAxisLabel.draw(layer);
      drawAxisLabels(layer, bounds);
    }
  }

  public TickFormatter getBestFormatter(double domainWidth) {
    return tickFormatterFactory.findBestFormatter(domainWidth);
  }

  public double getMinimumTickSize() {
    if (minTickSize == -1) {
      TickFormatter leafFormatter = tickFormatterFactory.getLeafFormatter();
      minTickSize = leafFormatter.getTickInterval();
    }
    return minTickSize;
  }

  public TickFormatterFactory getTickFormatterFactory() {
    return this.tickFormatterFactory;
  }

  public String getType() {
    return "axis";
  }

  public String getTypeClass() {
    return "domain";
  }

  @Override
  public void layout() {
    Layer rootLayer = view.getCanvas().getRootLayer();

    bounds.height = getLabelHeight(rootLayer, "X") + TICK_HEIGHT +
        + creditsLabel.getBounds().height + 1;

    // default width for now
    if (bounds.width <= 0) {
      bounds.width = view.getWidth(); 
    }
  }

  @Export
  public void setCreditsClickHandler(OverlayClickListener handler) {
    clickHandler = handler;
  }

  @Export
  public void setCreditsLabel(String label) {
    creditsLabel = new Label(label, this.textLayerName,
        view.getCanvas().getRootLayer(), "Helvetica", "normal", "9pt");
  }

  @Export
  public void setTickFormatterFactory(
      TickFormatterFactory tickFormatterFactory) {
    ArgChecker.isNotNull(tickFormatterFactory, "tickFormatterFactory");
    this.tickFormatterFactory = tickFormatterFactory;
  }

  @Override
  protected void initHook() {
    if (!((CompositeAxisPanel) parent).getPosition().isHorizontal()) {
      throw new RuntimeException(
          "DomainAxisPanel only works in a horizontal panel");
    }

    tickProperties = view.getGssProperties(tickGssElement, "");
    gridProperties = view.getGssProperties(gridGssElement, "");

    Layer rootLayer = view.getCanvas().getRootLayer();

    domainAxisLabel = new Label(AXIS_LABEL, this.textLayerName, rootLayer,
        this.labelProperties);

    if (creditsLabel == null) {
      creditsLabel = new Label(CREDITS, this.textLayerName, rootLayer,
          "Helvetica", "normal", "9pt");
    }
  }

  /**
   * Calculates the maximum number of ticks that can visually fit on the domain
   * axis given the visible screen width and the max width of a tick label for
   * the specified {@link DateTickFormatter}.
   */
  private int calcMaxTicksForScreen(Layer layer, Bounds bounds,
      double domainWidth, TickFormatter tickFormatter) {

    // Needed to round screen width due to tiny variances that were causing the 
    // result of this method to fluctuate by +/- 1.
    double screenWidth = domainToScreenWidth(domainWidth, bounds);

    double maxLabelWidth = 15 + tickFormatter
        .getMaxTickLabelWidth(layer, gssProperties);

    return (int) (screenWidth / maxLabelWidth);
  }

  private void clearAxis(Layer layer, Bounds bounds) {
    layer.save();
    layer.setFillColor(gssProperties.bgColor);
    layer.setStrokeColor(gssProperties.color);
  //  layer.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
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
    return bounds.width * (valueAxis.dataToUser(domainWidth) - valueAxis.dataToUser(0.0));
  }

  private double domainToScreenX(double dataX, Bounds bounds) {
    return bounds.width * valueAxis.dataToUser(dataX);
  }

  private void drawAxisLabels(Layer layer, Bounds bounds) {
    layer.setFillColor(labelProperties.bgColor);
    layer.setStrokeColor(labelProperties.color);
    layer.setTransparency((float)gssProperties.transparency);

    final double textRowY = bounds.bottomY() - 2;
    final double halfLabelWidth = domainAxisLabel.getBounds().width / 2;

    domainAxisLabel.setLocation(bounds.midpointX() - halfLabelWidth, textRowY);
    domainAxisLabel.draw(layer);

    // only show if enabled and a collision with the axis label is avoided
    // TODO - better PAD
    creditsLabel.setLocation(bounds.rightX() - creditsLabel.getBounds().width - 20, textRowY);
    final boolean collision = domainAxisLabel.getBounds().rightX()
        >= creditsLabel.getBounds().x;
    if (ChronoscopeOptions.isShowCreditsEnabled() && !collision) {
      layer.save();
      layer.setStrokeColor(Color.GRAY);
      layer.setTransparency(0.75f);
      creditsLabel.draw(layer);
      layer.restore();
    }
  }

  private void drawHorizontalLine(Layer layer, Bounds bounds) {
    layer.setFillColor(tickProperties.color);
    layer.fillRect(bounds.x, bounds.y,bounds.rightX(),bounds.y+tickProperties.lineThickness);
  }

  private void drawTick(Layer layer, XYPlot plot, Bounds bounds, double ux,
      int tickLength, boolean bold) {
    
    double tickWidth = bold ? tickProperties.lineThickness + 1 : tickProperties.lineThickness;
    layer.save();
    layer.setFillColor(tickProperties.color);
    layer.fillRect(ux, bounds.y, tickWidth, tickLength);

    if (gridProperties.visible) {
      Layer plotLayer = plot.getPlotLayer();
      plotLayer.save();
      plotLayer.setFillColor(gridProperties.color);
      plotLayer.setTransparency((float) gridProperties.transparency);
      plotLayer.fillRect(ux - bounds.x, 0, tickWidth, plot.getInnerBounds().height);
      plotLayer.restore();
    }

    layer.restore();
  }

  private void drawTickLabel(Layer layer, Bounds bounds, double ux,
      String tickLabel, boolean bold, double tickLabelWidth) {
    layer.setStrokeColor(labelProperties.color);
    layer.setFillColor(labelProperties.color);
    // TODO - rather than y+14 this is the use case for passing alignment top
    layer.drawText(2.5 + ux - tickLabelWidth / 2, bounds.y + 16, tickLabel,
        gssProperties.fontFamily, bold ? "bold" : gssProperties.fontWeight,
        gssProperties.fontSize, textLayerName, Cursor.DEFAULT);
  }

  private int getLabelHeight(Layer layer, String str) {
    return layer
        .stringHeight(str, gssProperties.fontFamily, gssProperties.fontWeight,
            gssProperties.fontSize);
  }

  public String toString() {
    return "domainAxisPanel";
  }
}
  
