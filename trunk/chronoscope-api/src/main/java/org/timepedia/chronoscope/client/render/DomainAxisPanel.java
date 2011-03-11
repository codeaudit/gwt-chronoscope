package org.timepedia.chronoscope.client.render;

import com.google.gwt.user.client.rpc.core.java.lang.Double_CustomFieldSerializer;
import org.timepedia.chronoscope.client.ChronoscopeOptions;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.overlays.OverlayClickListener;
import org.timepedia.chronoscope.client.plot.RangePanel;
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

  private static final String CREDITS = " ";
  private static final String AXIS_LABEL = ""; // (Time)
  private static final int SUB_TICK_HEIGHT = 3;
  private static final int TICK_HEIGHT = 6;
  public static final int MIN_HEIGHT = 18;

  private OverlayClickListener clickHandler;
  private GssProperties axesProperties;

  private boolean boundsSet = false;

  private Label creditsLabel;
  private Label domainAxisLabel;

  private GssElement gridGssElement, tickGssElement;
  private GssProperties gridProperties, tickProperties;

  private double firstTickX,lastTickX;
  private double labelHeight;
  private double minTickSize = -1;

  private static TickFormatterFactory tickFormatterFactory = new DateTickFormatterFactory();

  private Layer background;


  public DomainAxisPanel() {
    gridGssElement = new GssElementImpl("grid", this);
    tickGssElement = new GssElementImpl("tick", this);
  }

  public void reset() {
    gridGssElement = new GssElementImpl("grid", this);
    tickGssElement = new GssElementImpl("tick", this);
    clickHandler = null;
  }

  public void dispose() {
    super.dispose();
    clickHandler = null;
    creditsLabel = null;
    domainAxisLabel = null;
    gridGssElement = null;
    tickGssElement = null;
    gridProperties = null;
    tickProperties = null;
    if (null != background) {
      background.dispose();
      background = null;
    }
  }

  public void remove(Panel panel) {
    return; // no sub panels here: BottomPanel has the references to overviewPanel etc
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
    layer.save();

    log("draw bounds "+bounds);
    if (!GRID_ONLY) {
      clearAxis(layer, bounds);
    }
    if (axesProperties == null) {
      axesProperties = view.getGssProperties(this, "");
    }
    // drawHorizontalLine(layer, bounds);

    // TODO: cache this based on domainWidth(?)
    // shouldn't change in the case where the user is just scrolling left/right.
    final double domainWidth = plot.getDomain().length();
    TickFormatter tickFormatter = getBestFormatter(domainWidth);

    //log("best formatter for domain " + (long)domainWidth + ": " + tickFormatter);

    final double labelWidth = tickFormatter.getMaxTickLabelWidth(layer, gssProperties);
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
    firstTickX = bounds.width;
    lastTickX = -1;
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
      stillEnoughSpace = tickScreenPos + labelWidthDiv2 < bounds.width;
      
//      log("tickScreenPos=" + tickScreenPos + 
//          "; tickDomainValue=" + (long)tickFormatter.getTickDomainValue() +
//          "; boundsRightX=" + boundsRightX);

      if ((tickScreenPos > 0) && stillEnoughSpace) {
        // Quantized tick date may have gone off the left edge; need to guard
        // against this case.
        // if (tickScreenPos >= bounds.x) {
          String tickLabel = tickFormatter.format();
          boolean bold = tickFormatter.isBoundary(idealTickStep);
          drawTick(layer, plot, bounds, tickScreenPos, TICK_HEIGHT, bold);
          drawTickLabel(layer, bounds, tickScreenPos, tickLabel, bold, labelWidth);
        // }
      }

      // Draw auxiliary sub-ticks
      if (!isFirstTick) {
        int subTickStep = tickFormatter.getSubTickStep(actualTickStep);
        if (subTickStep > 1) {
          double auxTickWidth = (tickScreenPos - prevTickScreenPos)
              / subTickStep;
          double auxTickPos = prevTickScreenPos + auxTickWidth;
          for (int i = 0; i < subTickStep - 1; i++) {
            // if (MathUtil.isBounded(auxTickPos, bounds.x, boundsRightX)) {
            if (MathUtil.isBounded(auxTickPos, 0, bounds.width)) {
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
      drawAxisLabels(layer, bounds);
    }

    layer.restore();

    // the horizontal axis line is on the bottom of the plot background layer now
    if (null == background) {
      background = view.getCanvas().getLayer(Layer.BACKGROUND);
    }
    background.save();
    drawHorizontalLine(background, bounds, plot.getBounds().height);
    background.restore();
  }

  public static TickFormatter getBestFormatter(double domainWidth) {
    return tickFormatterFactory.findBestFormatter(domainWidth);
  }

  public double getMinimumTickSize() {
    if (minTickSize == -1) {
      TickFormatter leafFormatter = tickFormatterFactory.getLeafFormatter();
      minTickSize = leafFormatter.getTickInterval();
    }
    return minTickSize;
  }

  public static TickFormatterFactory getTickFormatterFactory() {
    return tickFormatterFactory;
  }

  public String getType() {
    return "axis";
  }

  public String getTypeClass() {
    return "domain";
  }

  @Override
  public void layout() {
    log("layout bounds:"+bounds);
    bounds.x = plot.getBounds().x;
    bounds.height = labelHeight + TICK_HEIGHT;
    if ((null != creditsLabel) && !creditsLabel.isEmpty()
       ||(null != domainAxisLabel) && !domainAxisLabel.isEmpty()) {
       bounds.height += labelHeight + 1;
    }

    if ((bounds.width <= 0) && (plot.getBounds().width > 10)) {
      bounds.width = plot.getBounds().width;
    }

    log("layout bounds:"+bounds);
    setBounds(bounds);
  }

  @Export
  public void setCreditsClickHandler(OverlayClickListener handler) {
    clickHandler = handler;
  }

  @Export
  public void setCreditsLabel(String label) {
    // Layer domainAxisLayer = view.getCanvas().getLayer(Layer.DOMAIN_AXIS);
    if (null == layer) {
      layer = view.getCanvas().createLayer(Layer.BOTTOM, bounds);
    }

    creditsLabel = new Label(label, this.textLayerName, layer, "Helvetica", "normal", "8pt");
  }

  @Export
  public void setTickFormatterFactory(
      TickFormatterFactory tickFormatterFactory) {
    ArgChecker.isNotNull(tickFormatterFactory, "tickFormatterFactory");
    this.tickFormatterFactory = tickFormatterFactory;
  }

  private void initLabels() {
    layer.save();

    labelHeight =  layer.stringHeight("X", gssProperties.fontFamily, gssProperties.fontWeight, gssProperties.fontSize);

    if (creditsLabel == null) {
      creditsLabel = new Label(CREDITS, layer.getLayerId(), layer, "Helvetica", "normal", "8pt");
    }
    if (domainAxisLabel == null) {
      domainAxisLabel = new Label(AXIS_LABEL, layer.getLayerId(), layer, this.labelProperties);
    }
    // TODO - optional mip/timespan level label
    // layer.setBounds(bounds);

    layer.restore();
  }

  @Override
  protected void initHook() {
//    if (!((CompositeAxisPanel) parent).getPosition().isHorizontal()) {
//      throw new RuntimeException("DomainAxisPanel only works in a horizontal panel");
//    }

    tickProperties = view.getGssProperties(tickGssElement, "");
    gridProperties = view.getGssProperties(gridGssElement, "");

    if (null == layer) {
      setLayer(view.getCanvas().createLayer(Layer.DOMAIN_AXIS, bounds));
    }
    initLabels();
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
    layer.setFillColor(gssProperties.bgColor);
    layer.setStrokeColor(gssProperties.color);
    log("clearAxis boundsSet "+boundsSet + " this.bounds:"+this.bounds + " bounds:"+bounds);
    if (!boundsSet) {
      layer.setBounds(bounds);
      boundsSet = true;
    }
    layer.clear(); // clearTextLayer(textLayerName);
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

    final double textRowY = bounds.height - creditsLabel.getBounds().height;
    // final double halfLabelWidth = domainAxisLabel.getBounds().width / 2;

    //domainAxisLabel.setLocation(bounds.midpointX() - halfLabelWidth, textRowY);
    //domainAxisLabel.draw(layer);

    // only show if enabled and a collision with the axis label is avoided
    // creditsLabel.setLocation(bounds.rightX() - creditsLabel.getBounds().width , textRowY);
    creditsLabel.setLocation(bounds.width - creditsLabel.getBounds().width - 12, textRowY);
    // final boolean collision = domainAxisLabel.getBounds().rightX() >= creditsLabel.getBounds().x;
    // if (ChronoscopeOptions.isShowCreditsEnabled() && !collision) {
    if (ChronoscopeOptions.isShowCreditsEnabled()) {
      // layer.save();
      layer.setStrokeColor(Color.GRAY);
      layer.setTransparency(0.75f);
      creditsLabel.draw(layer);
      // layer.restore();
    }
  }

  private void drawHorizontalLine(Layer layer, Bounds bounds, double lineY) {
    layer.setFillColor(tickProperties.color);
    Math.round(lineY);
    // log("drawHorizontal "+layer.getLayerId() + " fillRect firstTickX:"+firstTickX+" lastTickX:"+lastTickX);
    // ticks above line:
    //   layer.fillRect(bounds.x, bounds.y+tickProperties.lineThickness,bounds.rightX(),bounds.y+tickProperties.lineThickness);
    // ticks below line:
    //   layer.fillRect(firstTickX, TICK_HEIGHT, lastTickX - firstTickX +tickProperties.lineThickness, tickProperties.lineThickness);
    // ticks inclusive edges:
    //   layer.fillRect(firstTickX, lineY-tickProperties.lineThickness, lastTickX - firstTickX +tickProperties.lineThickness, tickProperties.lineThickness);
    layer.fillRect(TICK_HEIGHT-1, lineY-tickProperties.lineThickness, bounds.width - 2*TICK_HEIGHT +2, tickProperties.lineThickness);
  }

  private void drawTick(Layer layer, XYPlot plot, Bounds bounds, double ux, int tickLength, boolean bold) {
    double tickWidth = bold ? tickProperties.lineThickness + 1 : tickProperties.lineThickness;
    ux = Math.round(ux);

    layer.setFillColor(tickProperties.color);
    // layer.fillRect(ux, bounds.y, tickWidth, tickLength);
    layer.fillRect(ux, 0, tickWidth, tickLength);
    firstTickX = Math.min(ux, firstTickX);
    lastTickX = Math.max(ux, lastTickX);

    if (gridProperties.visible) {
      if (null == background) {
        background = view.getCanvas().getLayer(Layer.BACKGROUND);
      }

      background.save();
      background.setFillColor(gridProperties.color);
      background.setTransparency((float) gridProperties.transparency);
      // background.fillRect(ux - bounds.x, 0, tickWidth, plot.getInnerBounds().height);
      background.fillRect(ux, 0, tickWidth, plot.getBounds().height);
      background.restore();
    }

  }

  private void drawTickLabel(Layer layer, Bounds bounds, double ux,
      String tickLabel, boolean bold, double tickLabelWidth) {
    layer.setStrokeColor(labelProperties.color);
    layer.setFillColor(labelProperties.color);
    double x = Math.max(2.5 + ux - tickLabelWidth / 2.0, 0);
    x = Math.min(x, (layer.getBounds().width - tickLabelWidth));
    log("drawText("+x+", "+TICK_HEIGHT + labelHeight + ", "+tickLabel);
    // layer.drawText(x, bounds.y + TICK_HEIGHT + labelHeight, tickLabel,

    layer.drawText(x, TICK_HEIGHT + labelHeight, tickLabel,
        gssProperties.fontFamily, bold ? "bold" : gssProperties.fontWeight,
        gssProperties.fontSize, textLayerName, Cursor.DEFAULT);
  }

  public String toString() {
    return "domainAxisPanel";
  }

  private static void log (String msg) {
    System.out.println("DomainAxisPanel> "+msg);
  }
}
  
