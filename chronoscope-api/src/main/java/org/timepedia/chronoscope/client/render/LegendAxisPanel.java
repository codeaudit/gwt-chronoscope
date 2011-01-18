package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.render.domain.DateTickFormatterFactory;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.chronoscope.client.util.TimeUnit;

/**
 * Renders the dataset legend axis.
 */
public class LegendAxisPanel extends AxisPanel {

  /**
   * Dictates the Y-padding between the top of the legend item bounds and the
   * bottom of the zoom and date range panels.
   */
  private static final int LEGEND_Y_TOP_PAD = 2;

  /**
   * Dictates the Y-padding between the bottom of the legend item bounds and the
   * top of the plot panel.
   */

  private static final int LEGEND_Y_BOTTOM_PAD = 2;

  private DateRangePanel dateRangePanel;

  private DatasetLegendPanel dsLegendPanel;

  private ZoomListener zoomListener;

  private ZoomPanel zoomPanel;

  private GssProperties legendLabelsProperties;

  public boolean click(int x, int y) {
    zoomPanel.setPosition(bounds.x, bounds.y);
    return zoomPanel.click(x, y);
  }

  public void draw() {
    final int zoomHeight = (int) this.zoomPanel.bounds.height;
    clearAxis(layer, bounds);

    // Position and size the panels
    zoomPanel.setPosition(bounds.x, bounds.y);
    layoutPanels(bounds);
    topRightJustify(dateRangePanel, bounds);
    dsLegendPanel.setPosition(bounds.x, bounds.y + zoomHeight + LEGEND_Y_TOP_PAD);

    // Draw the panels
    zoomPanel.draw();
    dateRangePanel.draw();
    if (legendLabelsProperties.visible) {
      dsLegendPanel.setLegendLabelsProperties(legendLabelsProperties);
      dsLegendPanel.draw();
    }
  }

  public String getType() {
    return "axislegend";
  }

  public String getTypeClass() {
    return null;
  }

  @Override
  public void setLayer(Layer layer) {
    super.setLayer(layer);
    
    dsLegendPanel.setLayer(layer);
    zoomPanel.setLayer(layer);
    dateRangePanel.setLayer(layer);
  }
  
  @Override
  public void layout() {
    bounds.height = calcHeight();
    bounds.width = view.getWidth();
  }
  
  @Override
  protected void initHook() {
    ArgChecker.isNotNull(plot, "plot");
    ArgChecker.isNotNull(zoomListener, "zoomListener");

    legendLabelsProperties = view.getGssPropertiesBySelector("axislegend labels");
    if (legendLabelsProperties == null) {
        legendLabelsProperties = view.getGssProperties(new GssElementImpl("labels", this),"");
    }
    ZoomIntervals zoomIntervals = createDefaultZoomIntervals(plot);
    final double minInterval = Math.max(0, plot.getDatasets().getMinInterval());
    Interval domainExtrema = plot.getDatasets().getDomainExtrema();
    zoomIntervals.applyFilter(domainExtrema, minInterval);

    Layer rootLayer = view.getCanvas().getRootLayer();

    dsLegendPanel = new DatasetLegendPanel();
    dsLegendPanel.setGssProperties(labelProperties);
    dsLegendPanel.setLegendLabelsProperties(legendLabelsProperties);
    dsLegendPanel.setPlot(plot);
    dsLegendPanel.setView(view);
    dsLegendPanel.setTextLayerName(textLayerName);
    dsLegendPanel.setStringSizer(stringSizer);
    dsLegendPanel.parent = this;
    dsLegendPanel.init();

    zoomPanel = new ZoomPanel();
    zoomPanel.setGssProperties(labelProperties);
    zoomPanel.setTextLayerName(textLayerName);
    zoomPanel.addListener(zoomListener);
    zoomPanel.setZoomIntervals(zoomIntervals);
    zoomPanel.setStringSizer(stringSizer);
    zoomPanel.parent = this;
    zoomPanel.init();

    dateRangePanel = new DateRangePanel();
    dateRangePanel.setTextLayerName(textLayerName);
    dateRangePanel.setStringSizer(stringSizer);
    dateRangePanel.parent = this;

    // TODO: MCM: Understand this, some times domainAxisPanel does not come with view or plot
    // Example: ShowHide subpanels -> HideAll/ShowAll 
    // dateRangePanel.setGssProperties(labelProperties);
    plot.getDomainAxisPanel().setView(view);
    plot.getDomainAxisPanel().setPlot(plot);
    
    dateRangePanel.init(rootLayer, plot.getDomainAxisPanel());
  }

  public void setZoomListener(ZoomListener l) {
    this.zoomListener = l;
  }

  /**
   * Returns the total height of the rendered legend axis
   */
  private double calcHeight() {
    double totalHeight = 0;
    totalHeight += zoomPanel.getBounds().height;
    if (legendLabelsProperties.visible) {
      totalHeight += dsLegendPanel.getBounds().height;
    }
    return totalHeight;
  }

  private void clearAxis(Layer layer, Bounds bounds) {
    layer.save();
    layer.setFillColor(gssProperties.bgColor);
    layer.translate(-1, bounds.y - 1);
    layer.scale(layer.getWidth() + 1, bounds.height + 1);
    layer.beginPath();
    layer.rect(0, 0, 1, 1);
    layer.fill();
    layer.restore();
    layer.clearTextLayer(textLayerName);
  }

  private static void copyState(Bounds source, Bounds target) {
    target.x = source.x;
    target.y = source.y;
    target.height = source.height;
    target.width = source.width;
  }

  private static ZoomIntervals createDefaultZoomIntervals(XYPlot plot) {
    ZoomIntervals zooms = new ZoomIntervals();

    boolean isDateDomain = plot.getDomainAxisPanel().getTickFormatterFactory() instanceof DateTickFormatterFactory;

    if (isDateDomain) {
      zooms.add(new ZoomInterval("1d", TimeUnit.DAY.ms()));
      zooms.add(new ZoomInterval("5d", TimeUnit.DAY.ms() * 5));
      zooms.add(new ZoomInterval("1m", TimeUnit.MONTH.ms()));
      zooms.add(new ZoomInterval("3m", TimeUnit.MONTH.ms() * 3));
      zooms.add(new ZoomInterval("6m", TimeUnit.MONTH.ms() * 6));
      zooms.add(new ZoomInterval("1y", TimeUnit.YEAR.ms()));
      zooms.add(new ZoomInterval("5y", TimeUnit.YEAR.ms() * 5));
      zooms.add(new ZoomInterval("10y", TimeUnit.DECADE.ms()));
      zooms.add(new ZoomInterval("100y", TimeUnit.CENTURY.ms()));
      zooms.add(new ZoomInterval("1000y", TimeUnit.MILLENIUM.ms()));
      zooms.add(new ZoomInterval("max", Double.MAX_VALUE).filterExempt(true));
    } else {
      Interval domainExtrema = plot.getDatasets().getDomainExtrema();
      double startPower = MathUtil.roundToNearestPowerOfTen(domainExtrema.getStart());
      double endPower = MathUtil.roundToNearestPowerOfTen(domainExtrema.getEnd());
      while (startPower <= endPower) {
        zooms.add(new ZoomInterval("" + startPower / 2, startPower / 2));
        zooms.add(new ZoomInterval("" + startPower, startPower));
        startPower *= 10;
      }
      zooms.add(new ZoomInterval("max", Double.MAX_VALUE).filterExempt(true));
    }
    return zooms;
  }

  /**
   * Currently, this method natively lays out the ZoomPanel and DateRangePanel on
   * the X-axis. Ultimately, layout rules and heuristics will be split out into
   * a separate LayoutStrategy interface of some sort.
   */ // TODO show/hide from GSS
  private void layoutPanels(Bounds parentBounds) {
    // The minimum distance allowed between the zoom panel and the dataset
    // legend panel.
    final int minCushion = 3;

    double parentWidth = parentBounds.width;

    dateRangePanel.resizeToIdealWidth();
    zoomPanel.resizeToIdealWidth();
    zoomPanel.show(true);
    double idealZoomPanelWidth = zoomPanel.getBounds().width;

    // First, see if the panels in their prettiest form will
    // fit within the container's bounds
    double cushion = parentWidth - zoomPanel.getBounds().width - dateRangePanel.getBounds().width;
    if (cushion >= minCushion) {
      return;
    }

    // Doesn't fit? Then compress only the date range panel
    dateRangePanel.resizeToMinimalWidth();
    topRightJustify(dateRangePanel, parentBounds);
    cushion = parentWidth - idealZoomPanelWidth
        - dateRangePanel.getBounds().width;
    if (cushion >= minCushion) {
      return;
    }

    // Still doesn't fit? Then compress only the zoom link panel
    zoomPanel.resizeToMinimalWidth();
    dateRangePanel.resizeToIdealWidth();
    topRightJustify(dateRangePanel, parentBounds);
    cushion = parentWidth - zoomPanel.getBounds().width
        - dateRangePanel.getBounds().width;
    if (cushion >= minCushion) {
      return;
    }

    // Still doesn't fit? Then compress both panels
    dateRangePanel.resizeToMinimalWidth();
    topRightJustify(dateRangePanel, parentBounds);
    cushion = parentWidth - zoomPanel.getBounds().width
        - dateRangePanel.getBounds().width;
    if (cushion >= minCushion) {
      return;
    }

    // Still doesn't fit? Then hide the zoom links completely.
    zoomPanel.show(false);
  }

  /**
   * Positions the specified panel in the top-right corner of the specified
   * bounds.
   */
  private void topRightJustify(Panel panel, Bounds parentBounds) {
    panel.setPosition(parentBounds.rightX() - panel.getBounds().width, parentBounds.y);
  }

  /**
   * For debugging purposes
   */ // TODO expose show/hide in GSS
  private static void hiliteBounds(Bounds b, Layer layer) {
    layer.save();

    layer.setLayerOrder(Layer.Z_LAYER_PLOTAREA);
    // layer.setTransparency(.35f);
    layer.setFillColor(new Color("#50D0FF"));
    layer.fillRect(b.x, b.y, b.width, b.height);

    layer.restore();
  }

   public void setlegendLabelGssProperty(Boolean visible,Boolean valueVisible,Integer fontSize,Integer iconWidth,Integer iconHeight,Integer columnWidth,Integer columnCount, Boolean align){
       if(visible!=null){
           legendLabelsProperties.visible=visible;
       }
       if(valueVisible!=null){
           legendLabelsProperties.valueVisible=valueVisible;
       }
       if(fontSize!=null && fontSize > 0){
           legendLabelsProperties.fontSize=fontSize+"pt";
       }
       if(iconWidth!=null && iconWidth > 0){
           legendLabelsProperties.iconWidth=iconWidth+"px";
       }
       if(iconHeight!=null && iconHeight > 0){
           legendLabelsProperties.iconHeight=iconHeight+"px";
       }
       if(columnWidth!=null && columnWidth > 0){
           legendLabelsProperties.columnWidth=columnWidth+"px";
       }
       if(columnCount!=null && columnCount > 0){
           legendLabelsProperties.columnCount=columnCount.toString();
       }
       if (align != null) {
         legendLabelsProperties.columnAligned = align;
       }
  }

}
