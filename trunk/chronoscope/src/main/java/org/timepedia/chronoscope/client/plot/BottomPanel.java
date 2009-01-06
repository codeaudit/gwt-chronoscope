package org.timepedia.chronoscope.client.plot;

import org.timepedia.chronoscope.client.axis.DomainAxis;
import org.timepedia.chronoscope.client.axis.OverviewAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.render.CompositeAxisPanel;
import org.timepedia.chronoscope.client.render.DomainAxisPanel;
import org.timepedia.chronoscope.client.render.OverviewAxisPanel;
import org.timepedia.chronoscope.client.render.CompositeAxisPanel.Position;
import org.timepedia.chronoscope.client.util.Interval;

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
  
  // Renders the horizontal domain ticks and tick labels
  private DomainAxisPanel domainAxisPanel;
  
  // The miniaturized fully-zoomed-out representation of the datasets
  private OverviewAxisPanel overviewAxisPanel;
  
  private boolean overviewDrawn = false;
  
  private boolean overviewEnabled = true;
  
  private Layer overviewLayer;
  
  @Override
  public void clearDrawCaches() {
    overviewDrawn = false;
  }
  
  public CompositeAxisPanel getCompositeAxisPanel() {
    return this.compositePanel;
  }

  public DomainAxisPanel getDomainAxisPanel() {
    return this.domainAxisPanel;
  }
  
  public double getHeight() {
    return compositePanel.getHeight();
  }
  
  public Layer getOveriewLayer() {
    return this.overviewLayer;
  }
  
  public OverviewAxisPanel getOverviewAxisPanel() {
    return this.overviewAxisPanel;
  }
  
  @Override
  public void initLayer() {
    Bounds bottomPanelBounds = new Bounds(0, plot.getBounds().bottomY(),
        view.getWidth(), compositePanel.getHeight());
    layer = plot.initLayer(layer, "domainAxis", bottomPanelBounds);
    layer.setLayerOrder(Layer.Z_LAYER_AXIS);
    
    if (overviewEnabled) {
      overviewLayer = plot.initLayer(overviewLayer, "overviewLayer", plot.getBounds());
      overviewLayer.setVisibility(false);
    }
  }
  
  public boolean isOverviewEnabled() {
    return this.overviewEnabled;
  }

  @Override
  public void layout() {
    compositePanel.layout();
  }
  
  public void setDomainAxisPanel(DomainAxisPanel domainAxisPanel) {
    this.compositePanel.remove(this.domainAxisPanel);
    this.domainAxisPanel = domainAxisPanel;
    domainAxisPanel.setParentPanel(this.compositePanel);
    domainAxisPanel.setValueAxis(domainAxis);
    this.compositePanel.insertBefore(this.overviewAxisPanel, this.domainAxisPanel);
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

    if (compositePanel.getAxisCount() > 0) {
      layer.save();
      Bounds plotBounds = plot.getBounds();
      Bounds domainPanelBounds = new Bounds(plotBounds.x, 0, plotBounds.width,
          layer.getBounds().height);
      compositePanel.draw(layer, domainPanelBounds);
      layer.restore();
    }
  }
  
  @Override
  protected void initHook() {
    compositePanel = new CompositeAxisPanel("domainAxisLayer" + plot.plotNumber,
        Position.BOTTOM, plot, view);
    
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
    //plot.getDomain().setEndpoints(plot.getDatasets().getMinDomain(), plot.getDatasets().getMaxDomain());

    plot.drawPlot();
    overviewLayer.save();
    overviewLayer.setVisibility(false);
    overviewLayer.clear();
    overviewLayer.drawImage(plot.getPlotLayer(), 0, 0, overviewLayer.getWidth(),
        overviewLayer.getHeight());
    overviewLayer.restore();

    // restore original endpoints
    origVisPlotDomain.copyTo(plot.getDomain());

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
}
