package org.timepedia.chronoscope.client.plot;

import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.axis.DomainAxis;
import org.timepedia.chronoscope.client.axis.OverviewAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.render.AxisPanel;
import org.timepedia.chronoscope.client.render.CompositeAxisPanel;
import org.timepedia.chronoscope.client.render.DomainAxisPanel;
import org.timepedia.chronoscope.client.render.OverviewAxisPanel;
import org.timepedia.chronoscope.client.render.RangeAxisPanel;
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
  
  public void clearDrawCaches() {
    overviewDrawn = false;
  }
  
  /**
   * Draws the domain axis ticks and the highlighted region (if any) of the 
   * dataset overview.
   */
  @Override
  public void draw() {
    if (compositePanel.getAxisCount() == 0) {
      return;
    }

    layer.save();
    Bounds plotBounds = plot.getBounds();
    Bounds domainPanelBounds = new Bounds(plotBounds.x, 0, plotBounds.width,
        layer.getBounds().height);
    compositePanel.draw(layer, domainPanelBounds);
    layer.restore();
  }

  /**
   * Draws the overview (the  miniaturized fully-zoomed-out-view) of all the
   * datasets managed by this plot.
   */
  public void drawOverviewOfDatasets() {
    if (overviewDrawn || !overviewEnabled || !enabled) {
      return;
    }
    
    Interval plotDomain = plot.getDomain();
    
    // save original endpoints so they can be restored later
    double dO = plotDomain.getStart();
    double dE = plotDomain.getEnd();

    Datasets datasets = plot.getDatasets();
    
    plotDomain.setEndpoints(datasets.getMinDomain(), datasets.getMaxDomain());
    plot.drawPlot();
    overviewLayer.save();
    overviewLayer.setVisibility(false);
    overviewLayer.clear();
    overviewLayer.drawImage(plot.getPlotLayer(), 0, 0, overviewLayer.getWidth(),
        overviewLayer.getHeight());
    overviewLayer.restore();

    // restore original endpoints
    plotDomain.setEndpoints(dO, dE);

    overviewDrawn = true;
  }
  
  public CompositeAxisPanel getCompositeAxisPanel() {
    return this.compositePanel;
  }
  
  public AxisPanel getDomainAxisPanel() {
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
  
  public void setDomainAxisPanel(DomainAxisPanel axisPanel) {
    compositePanel.remove(domainAxisPanel);
    domainAxisPanel = axisPanel;
    axisPanel.setParentPanel(compositePanel);
    axisPanel.setValueAxis(domainAxis);
    domainAxis.setAxisRenderer((RangeAxisPanel) domainAxisPanel);
    compositePanel.insertBefore(overviewAxisPanel, domainAxisPanel);
  }
  
  public void setOverviewEnabled(boolean enabled) {
    if (this.overviewEnabled == enabled) {
      return;
    }
    
    this.overviewEnabled = enabled;
    if (enabled) {
      initOverviewAxisPanel();
    } else {
      compositePanel.remove(overviewAxisPanel);
      overviewAxisPanel = null;
    }
  }
  
  @Override
  protected void initHook() {
    compositePanel = new CompositeAxisPanel("domainAxisLayer" + plot.plotNumber,
        Position.BOTTOM, plot, view);
    
    if (this.isEnabled()) {
      initDomainAxisPanel();
      
      if (overviewEnabled) {
        initOverviewAxisPanel();
      }
    }
  }
  
  protected void setEnabledHook(boolean enabled) {
    if (compositePanel == null) {
      return;
    }
    
    setOverviewEnabled(enabled);
    
    if (enabled) {
      initDomainAxisPanel();
    }
    else { // disable
      if (domainAxisPanel != null) {
        // Remove the domainAxisPanel from the composite panel so that it
        // doesn't get rendered, but keep its reference around (as well
        // as its associated ValueAxis) because xyplot needs it for
        // userData<-->domainData conversions.
        compositePanel.remove(domainAxisPanel);
      }
    }
  }

  private void initOverviewAxisPanel() {
    compositePanel.remove(overviewAxisPanel);
    
    overviewAxisPanel = new OverviewAxisPanel();
    overviewAxisPanel.setValueAxis(new OverviewAxis(plot, "Overview"));
    compositePanel.add(overviewAxisPanel);
  }
  
  private void initDomainAxisPanel() {
    if (domainAxisPanel == null) {
      domainAxisPanel = new DomainAxisPanel();
    }
    else {
      compositePanel.remove(domainAxisPanel);
    }

    if (domainAxis == null) {
      //don't stomp on existing configured axis
      domainAxis = new DomainAxis(plot, view);
    }
    
    if (domainAxisPanel.getValueAxis() == null) {
      domainAxisPanel.setValueAxis(domainAxis);
      domainAxis.setAxisRenderer((RangeAxisPanel) domainAxisPanel);
    }

    compositePanel.add(domainAxisPanel);
  }
}
