package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.plot.RangePanel;
import org.timepedia.chronoscope.client.util.ArgChecker;

/**
 * Renders the overview axis.
 */
public class OverviewAxisPanel extends AxisPanel {

  public static final int OVERVIEW_HEIGHT = 42;

  private boolean initialized = false;

  public boolean visible = true;

  public GssProperties gssLensProperties;

  private OverviewHighlightPanel highlightPanel;

  public OverviewAxisPanel() {
  }

  public void reset() {
    initialized = false;
  }

  public void dispose() {
    super.dispose();
    gssLensProperties = null;
    highlightPanel.dispose();
  }

  public void remove(Panel panel) {
    if (null != panel && panel.equals(highlightPanel)) {
      highlightPanel = null;
    }
    return;
  }
  /**
   * Returns the bounds of the highlighted area of the overview axis, or
   * null if nothing is highlighted.
   */
  public Bounds getHighlightBounds() {
    if (null == highlightPanel) {
      initHighlightPanel();
    }
    return  highlightPanel.getHighlightBounds();
  }

  public void draw() {
    ArgChecker.isNotNull(layer, "layer");
    ArgChecker.isNotNull(parent, "parent");

    // ArgChecker.isNotNull(parent.getLayer(), "parent.layer");

    // FIXME - draw to overview layer and overview overlay layers directly, rather than copying to bottomlayer
    Layer player = layer;
    // Layer player = parent.getLayer();
    bounds.width=plot.getBounds().width;

    // player.drawImage(layer, 0, 0, layer.getWidth(), bounds.height,
    //     bounds.x, bounds.y, bounds.width, bounds.height);

    if (visible) {
      drawOverviewHighlight();
    }
  }

  public void drawOverviewHighlight() {
    highlightPanel.draw();
  }

   public GssProperties getGssProperties(){
      return gssProperties;
  }
  
  public String getType() {
    return "overview";
  }

  public void clearDrawCaches() {
    if (null != highlightPanel) {highlightPanel.clearDrawCaches();}
  }

  public String getTypeClass() {
    return null;
  }
  
  @Override
  public void layout() {
    log("layout bounds:"+bounds);
    bounds.x = plot.getBounds().x;
    if (visible) {
      // if (bounds.height < OVERVIEW_HEIGHT) { bounds.height = OVERVIEW_HEIGHT; }
      bounds.height = OVERVIEW_HEIGHT;
    } else {
      bounds.height = 1; // TEMP
    }

    if ((bounds.width <= 0) && (plot.getBounds().width > 10)) {
      bounds.width = plot.getBounds().width;
    }
    log("layout bounds:"+bounds);
    setBounds(bounds);
    // highlightPanel.layout();
  }

  @Override
  protected void initHook() {
    if (!initialized) { // guard visible from being reset back to initial gss value
      visible = gssProperties.visible;
      initialized = true;
    }
    if (null == layer) {
      setLayer(view.getCanvas().createLayer(Layer.OVERVIEW_SMALL, bounds));
    }
    gssLensProperties = view.getGssProperties(new GssElementImpl("lens", this), "");
    if (null == highlightPanel) {
      initHighlightPanel();
    }
  }

  private void initHighlightPanel() {
    highlightPanel = new OverviewHighlightPanel();
    highlightPanel.setGssProperties(gssLensProperties);
    highlightPanel.setParent(this);
    highlightPanel.setView(view);
    highlightPanel.setPlot(plot);
    highlightPanel.init();
  }


  private static void log(String msg){
    System.out.println("OverviewAxisPanel> "+msg);
  }
}
