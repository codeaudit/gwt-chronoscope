package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
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

  private DateRangePanel dateRangePanel;
  private DatasetLegendPanel dsLegendPanel;
  private ZoomPanel zoomPanel;

  private ZoomListener zoomListener;
  private GssProperties legendLabelsProperties;

  public void dispose() {
    super.dispose();
    if (null != dateRangePanel) { dateRangePanel.dispose(); }
    if (null != dsLegendPanel) { dsLegendPanel.dispose(); }
    if (null != zoomPanel) { zoomPanel.dispose(); }
    legendLabelsProperties = null;
  }

  public void reset() {
    super.reset();
    if (null != dateRangePanel) { dateRangePanel.reset(); }
    if (null != dsLegendPanel) { dsLegendPanel.reset(); }
    if (null != zoomPanel) { zoomPanel.reset(); }
    if (null != zoomListener) { zoomListener = null; }

  }

  public void remove(Panel panel) {
    if (null != panel) {
      if (panel.equals(dateRangePanel)) {
        dateRangePanel = null;
      } else if (panel.equals(dsLegendPanel)) {
        dsLegendPanel = null;
      } else if (panel.equals(zoomPanel)) {
        zoomPanel = null;
        zoomListener = null;
      }
    }
  }
  public boolean click(int x, int y) {
    zoomPanel.setPosition(bounds.x, bounds.y);
    return zoomPanel.click(x, y);
  }

  public void draw() {
    final int zoomHeight = (int) this.zoomPanel.bounds.height;
    // clearAxis(layer, bounds);

    // Position and size the panels
    // zoomPanel.setPosition(bounds.x, bounds.y);
    // topRightJustify(dateRangePanel, bounds);
    // dsLegendPanel.setPosition(bounds.x, bounds.y + zoomHeight + LEGEND_Y_TOP_PAD);

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
    if (null == layer) { return; } else
    if (layer.equals(this.layer)) { return; } else
    if (this.layer != null) {
      this.layer.dispose();
    }
    this.layer = layer;
    if (null != dsLegendPanel) {  dsLegendPanel.setLayer(layer); }
  }
  
  @Override
  public void layout() {
    bounds.height = calcHeight();
    bounds.width = view.getWidth();
    log("layout "+bounds);
//    if (null != layer) {
//      layer.save();
//
//      if(!bounds.equals(layer.getBounds())){
//        layer.setBounds(bounds);
//      }
//
//      layer.restore();
//    }
    layoutPanels(bounds);

    Layer zlayer = zoomPanel.getLayer();
    if (null != zlayer) {
      zlayer.save();

      if(!zoomPanel.getBounds().equals(zlayer.getBounds())){
        zlayer.setBounds(zoomPanel.getBounds());
        // log("layout zoomPanel bounds:"+zoomPanel.getBounds() + " "+zoomPanel.getLayer().getLayerId()+zoomPanel.getLayer().getBounds());
      }

      zlayer.restore();
    }

    Layer drlayer = dateRangePanel.getLayer();
    if (null != drlayer) {
      drlayer.save();

      if(!dateRangePanel.getBounds().equals(drlayer.getBounds())){
        drlayer.setBounds(dateRangePanel.getBounds());
        // log("layout zoomPanel bounds:"+zoomPanel.getBounds() + " "+zoomPanel.getLayer().getLayerId()+zoomPanel.getLayer().getBounds());
      }

      drlayer.restore();
    }
  }

  private void initZoomPanel() {
    ZoomIntervals zoomIntervals = createDefaultZoomIntervals(plot);
    final double minInterval = Math.max(0, plot.getDatasets().getMinInterval());
    Interval domainExtrema = plot.getDatasets().getDomainExtrema();
    zoomIntervals.applyFilter(domainExtrema, minInterval);
    if (null == zoomPanel) {
      zoomPanel = new ZoomPanel();
    }
    if (null == zoomPanel.getLayer()) {
      zoomPanel.setLayer(view.getCanvas().createLayer(Layer.ZOOMLEVEL, bounds));
    }
    zoomPanel.parent = this;
    zoomPanel.setGssProperties(labelProperties);
    zoomPanel.setTextLayerName(Layer.ZOOMLEVEL);
    zoomPanel.addListener(zoomListener);
    zoomPanel.setZoomIntervals(zoomIntervals);
    zoomPanel.init();

  }

  private void initDSLegendPanel() {
    legendLabelsProperties = view.getGssPropertiesBySelector("axislegend labels");
    if (legendLabelsProperties == null) {
      legendLabelsProperties = view.getGssProperties(new GssElementImpl("labels", this),"");
    }
    if (null == dsLegendPanel) {
      dsLegendPanel = new DatasetLegendPanel();
    }
    if (null == dsLegendPanel.getLayer()) {
      // dsLegendPanel.setLayer(view.getCanvas().createLayer(Layer.LEGEND, bounds));
      dsLegendPanel.setLayer(layer);
    }

    dsLegendPanel.setPlot(plot);
    dsLegendPanel.setView(view);
    dsLegendPanel.setGssProperties(labelProperties);
    dsLegendPanel.setLegendLabelsProperties(legendLabelsProperties);
    // dsLegendPanel.setTextLayerName(textLayerName);
    // push down by zoom height for zoom visibility
    // dsLegendPanel.setLayerOffset(0, zoomPanel.getBounds().height);
    dsLegendPanel.setPosition(0,zoomPanel.getBounds().height);
    dsLegendPanel.parent = this;
    dsLegendPanel.init();
  }

  private void initDateRangePanel() {
    if (null == dateRangePanel) {
      dateRangePanel = new DateRangePanel();
    }
    if (null == dateRangePanel.getLayer()) {
      dateRangePanel.setLayer(view.getCanvas().createLayer(Layer.DATERANGE, bounds));
    }
    dateRangePanel.setGssProperties(labelProperties);
    dateRangePanel.setTextLayerName(Layer.DATERANGE);
    dateRangePanel.parent = this;
    dateRangePanel.setPlot(plot);
    dateRangePanel.setView(view);
    dateRangePanel.init();
  }
  @Override
  protected void initHook() {
    ArgChecker.isNotNull(plot, "plot");
    ArgChecker.isNotNull(view, "view");
    ArgChecker.isNotNull(zoomListener, "zoomListener");

    // TODO: MCM: Understand this, some times domainAxisPanel does not come with view or plot
    // Example: ShowHide subpanels -> HideAll/ShowAll
    // plot.getDomainAxisPanel().setView(view);
    // plot.getDomainAxisPanel().setPlot(plot);

    if (null == layer) {
      layer = view.getCanvas().createLayer(Layer.LEGEND, bounds);
    } else
    if (bounds != null) {
      layer.save();

      if (!bounds.equals(layer.getBounds())) {
        layer.setBounds(bounds);
        layer.clear();
      }

      layer.restore();
    }
    log("legendAxisPanel layer bounds"+layer.getLayerId() + " bounds: "+getBounds());
    initZoomPanel();
    initDateRangePanel();
    initDSLegendPanel();
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

/*
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
*/

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
   * Currently, this method naively lays out the ZoomPanel and DateRangePanel on
   * the X-axis. Ultimately, layout rules and heuristics will be split out into
   * a separate LayoutStrategy interface of some sort.
   */
  private void layoutPanels(Bounds parentBounds) {
    final int minCushion = 3;
    zoomPanel.show(true);
    double idealZoomPanelWidth = zoomPanel.getBounds().width;

    // First, see if the panels in their prettiest form will
    // fit within the container's bounds
    double cushion = view.getWidth() - zoomPanel.getBounds().width - dateRangePanel.getBounds().width;
    if (cushion >= minCushion) {
      return;
    }

    // Doesn't fit? Then compress only the date range panel
    dateRangePanel.resizeToMinimalWidth();
    // topRightJustify(dateRangePanel, parentBounds);
    cushion = view.getWidth() - idealZoomPanelWidth - dateRangePanel.getBounds().width;
    if (cushion >= minCushion) {
      return;
    }

    // Still doesn't fit? Then compress only the zoom link panel
    zoomPanel.resizeToMinimalWidth();
    dateRangePanel.resizeToIdealWidth();
    // topRightJustify(dateRangePanel, parentBounds);
    cushion = view.getWidth() - zoomPanel.getBounds().width - dateRangePanel.getBounds().width;
    if (cushion >= minCushion) {
      return;
    }

    // Still doesn't fit? Then compress both panels
    dateRangePanel.resizeToMinimalWidth();
    // topRightJustify(dateRangePanel, parentBounds);
    cushion = view.getWidth() - zoomPanel.getBounds().width - dateRangePanel.getBounds().width;
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
    private static void log (String msg) {
      System.out.println(msg);
    }

}
