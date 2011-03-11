package org.timepedia.chronoscope.client.plot;

import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.render.AxisPanel;
import org.timepedia.chronoscope.client.render.CompositeAxisPanel;
import org.timepedia.chronoscope.client.render.LegendAxisPanel;
import org.timepedia.chronoscope.client.render.Panel;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the auxiliary panel on the top of the center dataset plot.
 * 
 * @author chad takahashi
 */
final class TopPanel extends AuxiliaryPanel {
  private CompositeAxisPanel compositePanel;
  private LegendAxisPanel legendAxisPanel;
  private Bounds bounds;

  public static int DEFAULT_HEIGHT=32;

  public TopPanel() {
    bounds = new Bounds();
  }

  public void dispose() {
    super.dispose();
    if (null != compositePanel) { compositePanel.dispose(); }
    if (null != legendAxisPanel) { legendAxisPanel.dispose(); }
    bounds=null;
  }

  public void remove(Panel panel) {
    if (null != panel) {
      if (panel.equals(compositePanel)) { compositePanel = null; } else
      if (panel.equals(legendAxisPanel)) { legendAxisPanel = null; }
    }
  }
  
  public boolean click(int x, int y) {
    return legendAxisPanel.click(x, y);
  }
  
  public Bounds getBounds() {
    return bounds;
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
    return compositePanel.getLayer();
    // return legendAxisPanel.getLayer();
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

  private void initLayer() {
    log("initLayer");
    // compositePanel.setLayer(view.getCanvas().createLayer(Layer.TOP, bounds));
    setLayer(view.getCanvas().createLayer(Layer.TOP, bounds));
  }

  @Override
  public void layout() {
    compositePanel.layout();
    bounds.height = compositePanel.getBounds().height;
    bounds.width = compositePanel.getBounds().width;
    log("layout bounds"+ bounds +" composite bounds:"+compositePanel.getBounds());
    // if(null != compositePanel.getLayer()) {
      // log("compositePanel layer bounds:"+compositePanel.getLayer().getBounds());
      // if (compositePanel.getLayer().getBounds().width > bounds.width) {
       //  compositePanel.getLayer().setBounds(bounds);
      // }
    //}
  }

  public void setLayer(Layer layer) {
    if (layer == null) {
      log("setLayer null");
      return;
    }

    // log(" setLayer "+layer.getLayerId() + " layer.bounds: "+layer.getBounds() + " bounds: "+bounds);
    compositePanel.setLayer(layer);
  }

  public void setLayerOffset(double x, double y) {
    throw new UnsupportedOperationException();
  }

  public final void setPosition(double x, double y) {
    boolean positionChanged = !(x == bounds.x && y == bounds.y);
    
    if (positionChanged) {
      bounds.x = x;
      bounds.y = y;
      compositePanel.setPosition(x,y);
    }
    log("setPosition "+x+" "+y);
    if (((null != compositePanel) && (compositePanel.getLayer() == null)) || positionChanged) {
      initLayer();
    }
  }
  
  @Override
  protected void drawHook() {
    log("TopPanel drawHook initialized?"+isInitialized() + " child count:"+compositePanel.getChildCount());
    if (!isInitialized()) {
      return;
    }

    if (compositePanel.getChildCount() == 0) {
      return;
    }
    compositePanel.draw();
  }
  
  @Override
  protected void initHook() {
    if(null == bounds) { bounds = new Bounds(0, 0, view.getWidth(), 0); }

    initCompositePanel();
    initLayer();
    if (isEnabled()) {
      initLegendAxisPanel();
    }
    hookup();
  }

  private void initCompositePanel() {
    if (null == compositePanel) {
      compositePanel = new CompositeAxisPanel(Layer.TOP, CompositeAxisPanel.Position.TOP, plot, view);
    } else {
      compositePanel.reset(Layer.TOP, CompositeAxisPanel.Position.TOP, plot, view);
    }
    compositePanel.setBounds(bounds);
    compositePanel.setParent(this);
  }

  private void hookup(){
    if (isEnabled()) {
      if (legendAxisPanel != null && !compositePanel.getChildren().contains(legendAxisPanel)){
        compositePanel.add(legendAxisPanel);
      }
    }
    clearDrawCaches();
    if (isInitialized()) {
     //  plot.reloadStyles();
    }
  }

  @Override
  protected void setEnabledHook(boolean enabled) {
    if (compositePanel == null) {
      return;
    }
    
    if (enabled) {
      initHook();
    } else { // disable the legend
      if (legendAxisPanel != null) {
        legendAxisPanel.setBounds(new Bounds(0,0,1,1));
        compositePanel.remove(legendAxisPanel);
        initHook();
      }
    }
  }
  
  private void initLegendAxisPanel() {
    if (null == legendAxisPanel) {
      legendAxisPanel = new LegendAxisPanel();
    } else {
      legendAxisPanel.reset();
    }
    legendAxisPanel.setView(view);
    legendAxisPanel.setPlot(plot);
    legendAxisPanel.setZoomListener(plot);
    legendAxisPanel.init();
    legendAxisPanel.layout();
    // legendAxisPanel.setBounds(bounds);
  }

  public CompositeAxisPanel getCompositePanel() {
      return compositePanel;
  }

  public void setlegendLabelGssProperty(Boolean visible,Boolean valueVisible,Integer fontSize,Integer iconWidth,Integer iconHeight,Integer columnWidth,Integer columnCount, Boolean align){
      legendAxisPanel.setlegendLabelGssProperty(visible, valueVisible, fontSize, iconWidth, iconHeight, columnWidth, columnCount, align);
      if(isInitialized()){
          plot.reloadStyles();
      }
  }

  public void clearDrawCaches() {
    super.clearDrawCaches();
    // ...
  }

  private static void log (String msg) {
    System.out.println("TopPanel> "+msg);
  }

}
