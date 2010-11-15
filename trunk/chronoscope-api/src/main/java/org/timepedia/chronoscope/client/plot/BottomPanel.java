package org.timepedia.chronoscope.client.plot;

import org.timepedia.chronoscope.client.axis.DomainAxis;
import org.timepedia.chronoscope.client.axis.OverviewAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.render.*;
import org.timepedia.chronoscope.client.render.CompositeAxisPanel.Position;
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

  public BottomPanel() {
    myBounds = new Bounds(0, 0, 480, 64);
  }
  
  @Override
  public void clearDrawCaches() {
    overviewDrawn = false;
  }
  
  public Bounds getBounds() {
    return myBounds;
  }
  
  public int getChildCount() {
    return getChildren().size();
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
    domainAxisPanel.getBounds().width = width;
    overviewAxisPanel.getBounds().width = width;
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

     compositePanel.clear();
     initDomainAxisPanel();
     compositePanel.add(domainAxisPanel);
     initOverviewAxisPanel();
     if (overviewVisible) {
        compositePanel.add(overviewAxisPanel);
     } else {
        // don't add it
     }

     plot.reloadStyles();

     // drawHook();
  }

  public boolean isOverviewVisible(){
      return overviewAxisPanel.visible;
  }
  
  @Override
  protected void drawHook() {
    if (!isInitialized()) {
      return;
    }

    if (overviewAxisPanel.visible && !overviewDrawn) {
      drawDatasetOverview();
    }

    compositePanel.draw();
  }
  
  @Override
  protected void initHook() {
    compositePanel = new CompositeAxisPanel("domainAxisLayer" + plot.plotNumber, Position.BOTTOM, plot, view);
    compositePanel.setParent(this);
    compositePanel.setStringSizer(stringSizer);

    // Both DomainAxisPanel and OverviewAxisPanel must be initialized even 
    // if BottomPanel is not currently enabled, because other auxiliary panels 
    // still refer to them.  
    initDomainAxisPanel();
    initOverviewAxisPanel();

    if (this.isEnabled()) {
      compositePanel.add(domainAxisPanel);
      if (overviewAxisPanel.visible) {
        compositePanel.add(overviewAxisPanel);
      }
    }
  }
  
  protected void setEnabledHook(boolean enabled) {
    clearDrawCaches();
    
    if (!isInitialized()) {
      return;
    }
    
    initDomainAxisPanel();
    initOverviewAxisPanel();
    compositePanel.clear();

    if (enabled) {
        compositePanel.add(domainAxisPanel);
        if (overviewAxisPanel.visible) {
          compositePanel.add(overviewAxisPanel);
        } else {
          // no need, just cleared it
          // compositePanel.remove(overviewAxisPanel);
        }
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
    plot.drawOverviewPlot(overviewLayer);
    overviewDrawn = true;
  }

  private void initDomainAxisPanel() {
    if (domainAxisPanel == null) {
      domainAxisPanel = new DomainAxisPanel();
    } else {
      compositePanel.remove(domainAxisPanel);
    }
    domainAxisPanel.setValueAxis(new DomainAxis(plot));
  }
  
  private void initOverviewAxisPanel() {
    if (overviewAxisPanel == null) {
      overviewAxisPanel = new OverviewAxisPanel();
    } else {
        // overviewAxisPanel.init();
        compositePanel.remove(overviewAxisPanel);
    }
    // else {
    //  compositePanel.remove(overviewAxisPanel);
    // }
    overviewAxisPanel.setValueAxis(new OverviewAxis(plot, "overview"));              

  }

  @SuppressWarnings("unused")
  private static void log(Object msg) {
    System.out.println("BottomPanel> " + msg);
  }

  public boolean click(int x, int y) {
    return domainAxisPanel.click(x,y);
  }
}
