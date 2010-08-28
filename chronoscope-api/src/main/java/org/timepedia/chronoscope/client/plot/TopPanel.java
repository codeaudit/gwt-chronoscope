package org.timepedia.chronoscope.client.plot;

import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.render.CompositeAxisPanel;
import org.timepedia.chronoscope.client.render.LegendAxisPanel;
import org.timepedia.chronoscope.client.render.Panel;

import java.util.ArrayList;
import java.util.List;
import org.timepedia.chronoscope.client.render.DatasetLegendPanel;

/**
 * Represents the auxiliary panel on the top of the center dataset plot.
 * 
 * @author chad takahashi
 */
final class TopPanel extends AuxiliaryPanel {
  private CompositeAxisPanel compositePanel;
  private LegendAxisPanel legendAxisPanel;
  private Layer layer;
  private Bounds myBounds;
  
  public TopPanel() {
    myBounds = new Bounds(0, 0, 30, 10);
  }
  
  public boolean click(int x, int y) {
    return legendAxisPanel.click(x, y);
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
  
  public Layer getLayer() {
    return this.layer;
  }
  
  public double getLayerOffsetX() {
    return 0;
    //return this.layer.getBounds().x;
  }

  public double getLayerOffsetY() {
    return 0;
    //return this.layer.getBounds().y;
  }

  public Panel getParent() {
    return null;
  }

  public void initLayer() {
    Bounds layerBounds = getBounds();
    layer = plot.initLayer(layer, "topLayer", layerBounds);
    layer.setLayerOrder(Layer.Z_LAYER_AXIS);
    this.compositePanel.setLayer(layer);
  }
  
  @Override
  public void layout() {
    compositePanel.setPosition(0, 0);
    compositePanel.layout();
    myBounds.height = compositePanel.getBounds().height;
    myBounds.width = compositePanel.getBounds().width;
  }

  public void setLayerOffset(double x, double y) {
    throw new UnsupportedOperationException();
  }

  public final void setPosition(double x, double y) {
    boolean positionChanged = !(x == myBounds.x && y == myBounds.y);
    
    if (positionChanged) {
      myBounds.x = x;
      myBounds.y = y;
    }
    
    if (layer == null || positionChanged) {
      initLayer();
    }
  }
  
  @Override
  protected void drawHook() {
    if (compositePanel.getChildCount() == 0) {
      return;
    }
    compositePanel.draw();
  }
  
  @Override
  protected void initHook() {
    final String panelName = "topPanel" + plot.plotNumber; 
    this.compositePanel = new CompositeAxisPanel(panelName,
        CompositeAxisPanel.Position.TOP, plot, view);
    this.compositePanel.setStringSizer(stringSizer);
    this.compositePanel.setParent(this);
    
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

  public CompositeAxisPanel getCompositePanel() {
      return compositePanel;
  }

  public void setlegendLabelGssProperty(Boolean visible,Boolean valueVisible,Integer fontSize,Integer iconWidth,Integer iconHeight,Integer columnWidth,Integer columnCount){
      legendAxisPanel.setlegendLabelGssProperty(visible, valueVisible, fontSize, iconWidth, iconHeight, columnWidth, columnCount);
  }

}
