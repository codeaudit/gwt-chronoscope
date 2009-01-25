package org.timepedia.chronoscope.client.plot;

import org.timepedia.chronoscope.client.axis.DomainAxis;
import org.timepedia.chronoscope.client.axis.OverviewAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.render.CompositeAxisPanel;
import org.timepedia.chronoscope.client.render.DomainAxisPanel;
import org.timepedia.chronoscope.client.render.OverviewAxisPanel;
import org.timepedia.chronoscope.client.render.Panel;
import org.timepedia.chronoscope.client.render.CompositeAxisPanel.Position;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Interval;

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
  private CompositeAxisPanel compositePanel;
  
  // The model for the ticks and tick labels
  private DomainAxis domainAxis;
  
  private Layer domainAxisLayer, overviewLayer;
  
  // Renders the horizontal domain ticks and tick labels
  private DomainAxisPanel domainAxisPanel;
  
  private Bounds myBounds;
  
  // The miniaturized fully-zoomed-out representation of the datasets
  private OverviewAxisPanel overviewAxisPanel;
  
  private boolean overviewDrawn = false;
  
  private boolean overviewEnabled = true;
  
  public BottomPanel() {
    myBounds = new Bounds(0, 0, 100, 20);
  }
  
  @Override
  public void clearDrawCaches() {
    overviewDrawn = false;
  }
  
  public Bounds getBounds() {
    return myBounds;
  }
  
  public List<Panel> getChildren() {
    List<Panel> l = new ArrayList<Panel>();
    l.add(compositePanel);
    return l;
  }
  
  public DomainAxisPanel getDomainAxisPanel() {
    return this.domainAxisPanel;
  }
  
  public Layer getLayer() {
    return this.domainAxisLayer;
  }
  
  public double getLayerOffsetX() {
    return 0;
    //return myBounds.x;
  }

  public double getLayerOffsetY() {
    return 0;
    //return myBounds.y;
  }

  public OverviewAxisPanel getOverviewAxisPanel() {
    return this.overviewAxisPanel;
  }

  public Panel getParent() {
    return null;
  }

  public void initLayer() {
    Bounds layerBounds = new Bounds(myBounds);
    domainAxisLayer = plot.initLayer(domainAxisLayer, "domainAxis", layerBounds);
    domainAxisLayer.setLayerOrder(Layer.Z_LAYER_AXIS);
    
    overviewLayer = plot.initLayer(overviewLayer, "overviewLayer", layerBounds);
    overviewAxisPanel.setOverviewLayer(overviewLayer);

    compositePanel.setLayer(domainAxisLayer);
  }
  
  public boolean isOverviewEnabled() {
    return this.overviewEnabled;
  }

  @Override
  public void layout() {
    if (domainAxisLayer == null) {
      initLayer();
    }

    compositePanel.setPosition(0, 0);
    compositePanel.layout();
    
    // The DomainAxisPanel and OverviewAxisPanel have fixed heights and,
    // therefore, dictate the height of this panel.
    // FIXME: need to automate method of determining which dimensions are
    // determined by sub-panels and which dimensions are dictated by
    // the parent panel.
    myBounds.height = compositePanel.getBounds().height;
  }
  
  public void setDomainAxisPanel(DomainAxisPanel domainAxisPanel) {
    this.compositePanel.remove(this.domainAxisPanel);
    this.domainAxisPanel = domainAxisPanel;
    domainAxisPanel.setParent(this.compositePanel);
    domainAxisPanel.setValueAxis(domainAxis);
    this.compositePanel.add(this.domainAxisPanel);
  }
  
  public void setLayerOffset(double x, double y) {
    throw new UnsupportedOperationException();
  }
  
  public void setWidth(double width) {
    ArgChecker.isNonNegative(width, "width");
    myBounds.width = width;
    compositePanel.setWidth(width);
    layout();
  }
  
  public final void setPosition(double x, double y) {
   boolean positionChanged = !(x == myBounds.x && y == myBounds.y);
    
   if (positionChanged) {
      myBounds.x = x;
      myBounds.y = y;
   }
    
    if (domainAxisLayer == null || positionChanged) {
      initLayer();
    }
  }
  
  public void setOverviewEnabled(boolean overviewEnabled) {
    if (this.overviewEnabled == overviewEnabled) {
      return;
    }
    
    clearDrawCaches();

    this.overviewEnabled = overviewEnabled;
    if (overviewEnabled) {
      this.enabled = true;
    }
    
    if (!this.initialized) {
      return;
    }
    
    if (overviewEnabled) {
      compositePanel.clear();
      initDomainAxisPanel();
      compositePanel.add(domainAxisPanel);
      initOverviewAxisPanel();
      compositePanel.add(overviewAxisPanel);
    } else {
      compositePanel.remove(overviewAxisPanel);
    }
    
    if (isInitialized()) {
      plot.reloadStyles();
    }
  }
  
  @Override
  protected void drawHook() {
    if (overviewEnabled && !overviewDrawn) {
      drawDatasetOverview();
    }

    compositePanel.draw();
  }
  
  @Override
  protected void initHook() {
    compositePanel = new CompositeAxisPanel("domainAxisLayer" + plot.plotNumber,
        Position.BOTTOM, plot, view);
    compositePanel.setParent(this);
    
    // Both DomainAxisPanel and OverviewAxisPanel must be initialized even 
    // if BottomPanel is not currently enabled, because other auxiliary panels 
    // still refer to them.  
    initDomainAxisPanel();
    initOverviewAxisPanel();

    if (this.isEnabled()) {
      compositePanel.add(domainAxisPanel);
      if (overviewEnabled) {
        compositePanel.add(overviewAxisPanel);
      }
    }
  }
  
  protected void setEnabledHook(boolean enabled) {
    overviewEnabled = enabled;
    clearDrawCaches();
    
    if (!isInitialized()) {
      return;
    }
    
    initDomainAxisPanel();
    initOverviewAxisPanel();
    compositePanel.clear();

    if (enabled) {
      compositePanel.add(domainAxisPanel);
      compositePanel.add(overviewAxisPanel);
    }
    
    if (isInitialized()) {
      plot.reloadStyles();
    }
  }
  
  /**
   * Draws the overview (the  miniaturized fully-zoomed-out-view) of all the
   * datasets managed by this plot.
   */
  private void drawDatasetOverview() {
    // save original endpoints so they can be restored later
    Interval origVisPlotDomain = plot.getDomain().copy();

    plot.getWidestDomain().copyTo(plot.getDomain());

    plot.drawPlot();
    overviewLayer.save();
    overviewLayer.setVisibility(false);
    overviewLayer.clear();
    overviewLayer.drawImage(plot.getPlotLayer(), 0, 0, overviewLayer.getWidth(),
        overviewLayer.getHeight());
    overviewLayer.restore();

    // restore original endpoints
    origVisPlotDomain.copyTo(plot.getDomain());

    // TODO: hack, to prevent double-drawn blended filled line areas
    // replace with a drawPlot routine that can draw with a custom layer
    // and plotBounds
    plot.getPlotLayer().clear();
    plot.drawBackground();
    overviewDrawn = true;
  }

  private void initDomainAxisPanel() {
    if (domainAxisPanel == null) {
      domainAxisPanel = new DomainAxisPanel();
    }
    else {
      compositePanel.remove(domainAxisPanel);
    }
    
    domainAxisPanel.setValueAxis(new DomainAxis(plot));
  }
  
  private void initOverviewAxisPanel() {
    if (overviewAxisPanel == null) {
      overviewAxisPanel = new OverviewAxisPanel();
      overviewAxisPanel.setValueAxis(new OverviewAxis(plot, "Overview"));
    }
    else {
      compositePanel.remove(overviewAxisPanel);
    }
  }
  
  
  private static void log(Object msg) {
    System.out.println("BottomPanel> " + msg);
  }

}
