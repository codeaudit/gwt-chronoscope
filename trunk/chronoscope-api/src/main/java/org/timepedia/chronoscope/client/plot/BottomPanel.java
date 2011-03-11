package org.timepedia.chronoscope.client.plot;

import org.timepedia.chronoscope.client.axis.DomainAxis;
import org.timepedia.chronoscope.client.axis.OverviewAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.render.*;
import org.timepedia.chronoscope.client.util.ArgChecker;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the auxiliary panel directly below the main plot, which is 
 * composed of the dataset overview (and associated highlight region),
 * and the domain tick axis.
 * 
 * @author chad takahashi
 */
final class BottomPanel extends AuxiliaryPanel {
  // Contains all sub-panels
  // private CompositeAxisPanel compositePanel;

  // The model for the ticks and tick labels
  private DomainAxis domainAxis;
  // Renders the horizontal domain ticks and tick labels
  private DomainAxisPanel domainAxisPanel;
  // The miniaturized fully-zoomed-out representation of the datasets
  private OverviewAxisPanel overviewAxisPanel;

  private Bounds bounds; // TODO - should be able to use compositePanel.bounds instead

  private boolean boundsSet;
  private boolean overviewDrawn = false;

  // not really used ...
  // private Layer layer;

  public BottomPanel() {
    // bounds = new Bounds();
  }

  public void dispose() {
    if (null != domainAxisPanel) { domainAxisPanel.dispose(); }
    if (null != overviewAxisPanel) { overviewAxisPanel.dispose(); }
    // if (null != compositePanel) { compositePanel.dispose(); }
  }

  public void remove(Panel panel) {
    if (null==panel) { return; }
    // if (panel.equals(compositePanel)) {
    //   compositePanel = null;
    // } else
    if (panel.equals(domainAxisPanel)) {
      domainAxisPanel = null;
      domainAxis = null;
    } else if (panel.equals(overviewAxisPanel)) {
      overviewAxisPanel = null;
    }
  }

  @Override
  public void clearDrawCaches() {
    overviewDrawn = false;
  }

  public Bounds getBounds() {
    return isOverviewVisible() ? new Bounds(overviewAxisPanel.getBounds(), domainAxisPanel.getBounds())
            : domainAxisPanel.getBounds();
  }

  public double getHeight() {
    double height =  domainAxisPanel.getBounds().height;
    height += isOverviewVisible() ? overviewAxisPanel.getBounds().height : 0;
    return height;
  }

  public void setBounds(Bounds bounds) {
    log("setBounds "+bounds);
    this.bounds = new Bounds(bounds);
    // compositePanel.setBounds(bounds);

    Bounds axisBounds = domainAxisPanel.getBounds();
    Bounds overBounds = overviewAxisPanel.getBounds();

    domainAxisPanel.setBounds(new Bounds(
            bounds.x, bounds.y, bounds.width, axisBounds.height));

    overviewAxisPanel.setBounds(new Bounds(
            bounds.x, bounds.y + axisBounds.height, bounds.width, overBounds.height));

  }

  public int getChildCount() {
    return getChildren().size();
  }

  public List<Panel> getChildren() {
    List<Panel> l = new ArrayList<Panel>();
    l.add(domainAxisPanel);
    l.add(overviewAxisPanel);
    return l;
  }

  public DomainAxisPanel getDomainAxisPanel() {
    return domainAxisPanel;
  }

  public Layer getLayer() {
    log(">>> ERROR getLayer BOTTOMPANEL shouldn't be called");
    return (Layer) null; // return layer;
  }

  public double getLayerOffsetX() {
    return 0;
    //return bounds.x;
  }

  public double getLayerOffsetY() {
    return 0;
    // return bounds.y;
  }

  public OverviewAxisPanel getOverviewAxisPanel() {
    return overviewAxisPanel;
  }

  public Panel getParent() {
    return null;
  }

//  public void initLayer() {
//    // compositePanel.setLayer(view.getCanvas().createLayer(Layer.BOTTOM, compositePanel.getBounds()));
//  }

  @Override
  public void layout() {
    // The DomainAxisPanel and OverviewAxisPanel have fixed heights and,
    // therefore, dictate the height of this panel.
    // FIXME: need to automate method of determining which dimensions are
    // determined by sub-panels and which dimensions are dictated by
    // the parent panel.
    log("layout bounds "+bounds);
    if (null != domainAxisPanel) {
      domainAxisPanel.layout();
        // log("layout domainAxisPanel "+domainAxisPanel.getBounds()+ " "
        //        + domainAxisPanel.getLayer().getLayerId() +domainAxisPanel.getLayer().getBounds());
    }
    if (null != overviewAxisPanel) {
      overviewAxisPanel.layout();
      log("layout overviewAxisPanel bounds:"+overviewAxisPanel.getBounds().height);
      if (isOverviewVisible()) {
        log(" overview visible");
        // log("layout overviewAxisPanel "+overviewAxisPanel.getBounds()+ " "
        //        + overviewAxisPanel.getLayer().getLayerId() +overviewAxisPanel.getLayer().getBounds());
      } else {
        log("overview not visible");
      }
    }

  }

  public void setDomainAxisPanel(DomainAxisPanel domainAxisPanel) {
    if ((null != domainAxisPanel) && (!domainAxisPanel.equals(this.domainAxisPanel))) {
        this.domainAxisPanel.dispose();
    }
    domainAxisPanel.setValueAxis(domainAxis);
    this.domainAxisPanel = domainAxisPanel;
    initHook();
  }

  public void setLayerOffset(double x, double y) {
    throw new UnsupportedOperationException();
  }

  public void setWidth(double width) {
    ArgChecker.isNonNegative(width, "width");
    log("set width " + width);
    bounds.width = width;
    if (null != domainAxisPanel) { domainAxisPanel.setWidth(width); }
    if (null != overviewAxisPanel) { overviewAxisPanel.setWidth(width); }
    // compositePanel.setWidth(width);
    layout();
  }


  public final void setPosition(double x, double y) {
   log("setPosition "+x+", "+y);
   // boolean positionChanged = !(x == bounds.x && y == bounds.y);

   // if (positionChanged) {
      // bounds.setPosition(x,y);
      // compositePanel.setPosition(x, y);
      domainAxisPanel.setPosition(x, y);
      if(isOverviewVisible()){
        overviewAxisPanel.setPosition(x, y+domainAxisPanel.getBounds().height);
      }
   // }
//    if ((getLayer() == null) || (positionChanged)) {
//      domainAxisPanel.setPosition(x,y);
//      overviewAxisPanel.setPosition(x,y);
//    }
  }

  public void setY(double y) {
    ArgChecker.isNonNegative(y, "Y");
    log("setY "+y);
    if (null != domainAxisPanel) {
      domainAxisPanel.setPosition(domainAxisPanel.getBounds().x, y);
    }
    if (null != overviewAxisPanel && isOverviewVisible()) {
      overviewAxisPanel.setPosition(overviewAxisPanel.getBounds().x, y+domainAxisPanel.getBounds().height);
    }
    layout();
  }
  public void setX(double x) {
    ArgChecker.isNonNegative(x, "X");
    log("setX "+x);
    if (null != domainAxisPanel) {
      domainAxisPanel.setPosition(x, domainAxisPanel.getBounds().y);
    }
    if (null != overviewAxisPanel && isOverviewVisible()) {
      overviewAxisPanel.setPosition(x, overviewAxisPanel.getBounds().y);
    }
    layout();
  }



  @Deprecated
  public boolean isOverviewEnabled() {
    return isOverviewVisible();
  }

  @Deprecated
  public void setOverviewEnabled(boolean overviewEnabled) {
    setOverviewVisible(overviewEnabled);
  }

   public void setOverviewVisible(boolean overviewVisible){
     if (overviewAxisPanel.visible == overviewVisible) {
       return;
     }
     overviewAxisPanel.visible = overviewVisible;
     if (overviewVisible && !this.enabled) {
        this.enabled = true;
     }
     clearDrawCaches();
     if (!this.initialized) {
       return;
     }

     initHook();
     // drawHook();
  }

  public boolean isOverviewVisible(){
    if (null == overviewAxisPanel) {
      return false;
    } else {
      return overviewAxisPanel.visible;
    }
  }

  @Override
  protected void drawHook() {
    if (!isInitialized()) {
      return;
    }

    domainAxisPanel.draw();

    if (overviewAxisPanel.visible && !overviewDrawn) {
      drawDatasetOverview();
    }
    if (overviewAxisPanel.visible) {
       overviewAxisPanel.drawOverviewHighlight();
    }
    // compositePanel.draw();
  }

  @Override
  protected void initHook() {
    int height = isEnabled()? DomainAxisPanel.MIN_HEIGHT : 0;
    height += (isEnabled() && isOverviewVisible())? OverviewAxisPanel.OVERVIEW_HEIGHT : 0;
    // if plot initialized, then use plotBounds

    log("initHook bounds:"+bounds);
    // initCompositePanel();

    // Both DomainAxisPanel and OverviewAxisPanel must be initialized even
    // if BottomPanel is not currently enabled, because other auxiliary panels
    // still refer to them.   FIXME

    initOverviewAxisPanel();
    initDomainAxisPanel();

    clearDrawCaches();

    // initLayer();

    // hookup();
  }

//  private void hookup(){
//    log("hookup");
//    if (isEnabled()) {
//      // log("adding "+domainAxisPanel);
//      // compositePanel.add(domainAxisPanel);
//
//      if (overviewAxisPanel.visible) {
//        // log("adding "+overviewAxisPanel);
//        // compositePanel.add(overviewAxisPanel);
//      }
//    }
//    clearDrawCaches();
//    if (isInitialized()) {
//      plot.reloadStyles();
//    }
//  }

  protected void setEnabledHook(boolean enabled) {
    if (enabled) {
      initHook();
    } else { // disabling is disabled, see FIXME in initHook()
      // overviewAxisPanel .dispose();
      // domainAxisPanel.dispose();
    }
  }


  /**
   * Draws the overview (the  miniaturized fully-zoomed-out-view) of all the
   * datasets managed by this plot.
   */
  private void drawDatasetOverview() {
    log("drawDatasetOverview "+ overviewAxisPanel.getType() + " "+overviewAxisPanel.getBounds());
    log("drawDatasetOverview " + overviewAxisPanel.getLayer().getLayerId() + " "+overviewAxisPanel.getLayer().getBounds());

    plot.drawOverviewPlot(overviewAxisPanel.getLayer());

    log("drawDatasetOverview "+ overviewAxisPanel.getType() + " "+overviewAxisPanel.getBounds());
    log("drawDatasetOverview " + overviewAxisPanel.getLayer().getLayerId() + " "+overviewAxisPanel.getLayer().getBounds());
    overviewDrawn = true;
  }

  private void initCompositePanel() {
    // if (null == compositePanel) {
      // compositePanel = new CompositeAxisPanel(Layer.DOMAIN_AXIS, Position.BOTTOM, plot, view);
    // } else {
      // compositePanel.reset(Layer.DOMAIN_AXIS, Position.BOTTOM, plot, view);
    // }
    // compositePanel.setBounds(bounds);
    // compositePanel.setParent(this);
  }

  private void initDomainAxisPanel() {
    if (null == domainAxisPanel) {
      domainAxisPanel = new DomainAxisPanel();
    } else {
      domainAxisPanel.reset();
    }
    if (null == domainAxisPanel.getBounds()) {
      double axisWidth = plot.getBounds().width;
      double overviewHeight = isEnabled() && isOverviewVisible() ? OverviewAxisPanel.OVERVIEW_HEIGHT : 0.0;
      double axisHeight = DomainAxisPanel.MIN_HEIGHT; // TODO - get real height
      double axisY = (double)(view.getHeight()) - overviewHeight - axisHeight;
      double axisX = plot.getBounds().x;

      Bounds domainAxisPanelBounds = new Bounds(axisX, axisY, axisWidth, axisHeight);
      log ("initDomainAxisPanel bounds:"+domainAxisPanelBounds);
      domainAxisPanel.setBounds(domainAxisPanelBounds);
    }

    domainAxisPanel.setValueAxis(new DomainAxis(plot));
    domainAxisPanel.setParent(this);
    domainAxisPanel.setPlot(plot);
    domainAxisPanel.setView(view);
    domainAxisPanel.init();
  }

  private void initOverviewAxisPanel() {
    if (null == overviewAxisPanel) {
        overviewAxisPanel = new OverviewAxisPanel();
    } else {
        overviewAxisPanel.reset();
    }
    if (null == overviewAxisPanel.getBounds()) {
      double overviewHeight = isEnabled() && isOverviewVisible() ? OverviewAxisPanel.OVERVIEW_HEIGHT : 0.0;
      double overviewY = (double)(view.getHeight()) - overviewHeight;

      // NOTE - plot.x and plot.width should be set before this is called.
      double overviewX = plot.getBounds().x;
      double overviewWidth = plot.getBounds().width;

      Bounds overviewAxisPanelBounds = new Bounds(overviewX, overviewY, overviewWidth, overviewHeight);
      log ("initOverviewAxisPanel bounds:"+overviewAxisPanelBounds);
      overviewAxisPanel.setBounds(overviewAxisPanelBounds);
    }

    overviewAxisPanel.setValueAxis(new OverviewAxis(plot, "overview"));
    overviewAxisPanel.setParent(this);
    overviewAxisPanel.setPlot(plot);
    overviewAxisPanel.setView(view);
    overviewAxisPanel.init();

  }

  @SuppressWarnings("unused")
  private static void log(Object msg) {
    System.out.println("BottomPanel> " + msg);
  }

  public boolean click(int x, int y) {
    return domainAxisPanel.click(x,y);
  }
}
