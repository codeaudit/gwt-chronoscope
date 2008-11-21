package org.timepedia.chronoscope.client.plot;

import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.render.CompositeAxisPanel;
import org.timepedia.chronoscope.client.render.LegendAxisPanel;

/**
 * Represents the auxiliary panel on the top of the center dataset plot.
 * 
 * @author chad takahashi
 */
final class TopPanel extends AuxiliaryPanel {
  private CompositeAxisPanel compositePanel;
  private LegendAxisPanel legendAxisPanel;
  
  public boolean click(int x, int y) {
    return legendAxisPanel.click(x, y);
  }
  
  @Override
  public void draw() {
    if (compositePanel.getAxisCount() == 0) {
      return;
    }

    layer.save();
    Bounds panelBounds = new Bounds(0, 0, layer.getBounds().width,
        layer.getBounds().height);
    compositePanel.draw(layer, panelBounds);
    layer.restore();
  }

  @Override
  protected void initHook() {
    final String panelName = "topPanel" + plot.plotNumber; 
    this.compositePanel = new CompositeAxisPanel(panelName,
        CompositeAxisPanel.Position.TOP, plot, view);
    
    if (this.isEnabled()) {
      initLegendAxisPanel();
    }
  }
  
  @Override
  public void initLayer() {
    Bounds layerBounds = new Bounds(0, 0, view.getWidth(), this.getHeight());
    layer = plot.initLayer(layer, "topLayer", layerBounds);
    layer.setLayerOrder(Layer.Z_LAYER_AXIS);
  }

  public double getHeight() {
    return compositePanel.getHeight();
  }
  
  @Override
  public void layout() {
    compositePanel.layout();
  }

  @Override
  protected void setEnabledHook(boolean enabled) {
    if (compositePanel == null) {
      return;
    }
    
    if (enabled) {
      initLegendAxisPanel();
    }
    else { // disable the legend
      if (legendAxisPanel != null) {
        compositePanel.remove(legendAxisPanel);
        legendAxisPanel = null;
      }
    }
  }
  
  private void initLegendAxisPanel() {
    if (legendAxisPanel == null) {
      legendAxisPanel = new LegendAxisPanel();
      legendAxisPanel.setZoomListener(plot);
      compositePanel.add(legendAxisPanel);
    }
  }

}
