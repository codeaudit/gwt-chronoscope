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
  private Layer layer;
  
  public boolean click(int x, int y) {
    return legendAxisPanel.click(x, y);
  }
  
  public Bounds getBounds() {
    return compositePanel.getBounds();
  }
  
  @Override
  public void layout() {
    compositePanel.layout();
  }

  public final void setPosition(double x, double y) {
    compositePanel.setPosition(x, y);
  }
  
  @Override
  protected void drawHook() {
    if (compositePanel.getAxisCount() == 0) {
      return;
    }
    compositePanel.draw();
  }
  
  @Override
  protected void initHook() {
    Bounds layerBounds = new Bounds(0, 0, view.getWidth(), view.getHeight());
    layer = plot.initLayer(layer, "topLayer", layerBounds);
    layer.setLayerOrder(Layer.Z_LAYER_AXIS);
    layer.setVisibility(false);
    
    final String panelName = "topPanel" + plot.plotNumber; 
    this.compositePanel = new CompositeAxisPanel(panelName,
        CompositeAxisPanel.Position.TOP, plot, view);
    this.compositePanel.setLayer(layer);
    
    if (this.isEnabled()) {
      initLegendAxisPanel();
    }
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
      }
    }
  }
  
  private void initLegendAxisPanel() {
    if (legendAxisPanel == null) {
      legendAxisPanel = new LegendAxisPanel();
    }
    else {
      compositePanel.remove(legendAxisPanel);
    }
    legendAxisPanel.setZoomListener(plot);
    compositePanel.add(legendAxisPanel);
  }

}
