package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.data.DatasetListener;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.util.ArgChecker;

import java.util.ArrayList;
import java.util.List;

/**
 * A container that manages 0 or more {@link AxisPanel} objects. 
 * A {@link CompositeAxisPanel} can be assigned a {@link Position} relative to its 
 * container.  This panel is responsible for partitioning space for itself
 * and  allocating that space to each contained axis, as well as using its GSS 
 * {@link #axesProperties} to fill the background of the panel when cleared.
 */
public final class CompositeAxisPanel implements Panel, GssElement {

  private static final double TOP_PANEL_PAD = 23;

  /**
   * Enumerator of the possible positions that this panel can occupy relative
   * to its containing panel.
   */
  public enum Position {
    LEFT {
      public boolean isHorizontal() {
        return false;
      };
    },
    RIGHT {
      public boolean isHorizontal() {
        return false;
      };
    },
    TOP {
      public boolean isHorizontal() {
        return true;
      };
    },
    BOTTOM {
      public boolean isHorizontal() {
        return true;
      };
    };

    /**
     * True only if this panel is oriented horizontally, otherwise
     * assumed to be vertically oriented.
     */
    public abstract boolean isHorizontal();
  }

  private boolean layerConfigured = false;
  private double layerOffsetX, layerOffsetY;
  private Bounds bounds = new Bounds();
  private Position position;

  private String panelName;
  private GssProperties axesProperties;

  private Layer layer;

  private XYPlot<?> plot;
  private View view;

  private Panel parent;
  private List<AxisPanel> subPanels = new ArrayList<AxisPanel>();

  public CompositeAxisPanel(String panelName, Position position, XYPlot<?> plot, View view) {
    this.panelName = panelName;
    this.position = position;
    this.plot = plot;
    this.view = view;
  }

  public void reset(String panelName, Position position, XYPlot<?> plot, View view) {
    log("reset "+panelName + " "+ position +" "+view.getViewId());
    this.panelName = panelName;
    this.position = position;
    this.plot = plot;
    this.view = view;
    layerConfigured = false;
  }

  /** 
   * Adds the specified axis panel as a child of this panel.
   */
  public void add(AxisPanel subPanel) {
    ArgChecker.isNotNull(subPanel, "subPanel");
    if(subPanel.getLayer() != null) {
      log(getType() + " add " + subPanel.getType() + " to panel on " + layer.getLayerId());
    } else {
        log(getType() + " add " + subPanel.getType() + " to panel with  null layer");
    }

    if (!subPanels.contains(subPanel)) {
      subPanels.add(subPanel);
    }
    if (subPanels.size()>5) {
      log(layer.getLayerId() + " subPanels.size " +subPanels.size());
    }

    subPanel.setParent(this);
    subPanel.setPlot(plot);
    subPanel.setView(view);

    if (null == subPanel.getLayer() && null != layer) {
      // Layer subLayer = view.getCanvas().createLayer(layer.getLayerId()+subPanels.indexOf(subPanel), bounds);
      // log("add created "+subLayer.getLayerId());
      // layer.save();
      // subLayer.setLayerOrder(layer.getLayerOrder()); // sublayers not in the main Layer.Z mapping
      // layer.restore();
      // subPanel.setLayer(subLayer);
      subPanel.setLayer(layer);
      // subPanel.setBounds(bounds);

    }
    subPanel.init();
  }

  /**
   * Removes and unregisters all child panels from this container.
   */
  public void dispose() {
    // Need to copy list to avoid ConcurrentModificationException
    List<AxisPanel> subPanelCopy = new ArrayList<AxisPanel>(this.subPanels);

    for (AxisPanel p : subPanelCopy) {
      dispose(p);
    }
    subPanels.clear();

    parent.remove(this);
    parent=null;

    if (null != layer) {
      layer.dispose();
      layer = null;
    }

    plot = null;
    view = null;
  }

  /**
   * Removes and de-registers the specified child panel from this container.
   * @param childPanel
   */
  public void remove(Panel childPanel) {
    if (childPanel != null) {
      subPanels.remove(childPanel);
    }
  }

  public void dispose(Panel childPanel) {
    childPanel.dispose(); // will call parent.remove(this) = this.remove(childPanel)
  }

  public void draw() {
    log("draw");
    if (subPanels.size() == 0) {
      log ("draw " + layer.getLayerId() + " no subPanels");
      return;
    }

    layer.save();
    
    if (axesProperties == null) {
      axesProperties = view.getGssProperties(this, "");
    }

    if (!AxisPanel.GRID_ONLY) {
      layer.clear(); //clearPanel(this.layer, this.bounds);
    }
    
    for (int i = 0; i < subPanels.size(); i++) {
      AxisPanel subPanel = subPanels.get(i);
      if (subPanel.getLayer()!=null){
        log("draw subPanel " + subPanel.getBounds() + " "
        +subPanel.getLayer().getLayerId()+" "+subPanel.getLayer().getBounds());
      } else {
        log("draw null subPanel layer" + subPanel.getBounds());
      }
      if (!layerConfigured) {
        log(layer.getLayerId() + " not configured");
        // layer.setTextLayerBounds(subPanel.getTextLayerName(), subPanel.getBounds());
      }
      subPanel.draw();
    }

    layer.restore();
    layerConfigured = true;
  }

  public Bounds getBounds() {
    return bounds;
  }

  public int getChildCount() {
    return this.subPanels.size();
  }

  public List<Panel> getChildren() {
    List<Panel> l = new ArrayList<Panel>(this.subPanels.size());
    for (Panel p : this.subPanels) {
      l.add(p);
    }
    return l;
  }
  
  public Layer getLayer() {
    return layer;
  }
  
  public double getLayerOffsetX() {
    return layerOffsetX;
  }

  public double getLayerOffsetY() {
    return layerOffsetY;
  }

  /**
   * Returns the ordinal position of the specified sub-panel.
   */
  public int indexOf(Panel subPanel) {
    for (int i = 0; i < subPanels.size(); i++) {
      if (subPanel == subPanels.get(i)) {
        return i;
      }
    }
    throw new RuntimeException("subPanel not in container: " + subPanel);
  }

  public String getName() {
    return panelName;
  }

  public Panel getParent() {
    return parent;
  }

  public GssElement getParentGssElement() {
    return null;
  }

  public Position getPosition() {
    return position;
  }

  public String getType() {
    return "axes";
  }

  public String getTypeClass() {
    switch (position) {
      case LEFT:
        return "left";
      case RIGHT:
        return "right";
      case TOP:
        return "top";
      case BOTTOM:
        return "bottom";
      default:
        return null;
    }
  }
  
  public void layout() {
    if (null == layer) { return; }

    layer.save();

    log("layout " + bounds + " " +layer.getLayerId()+ " "+layer.getBounds());
    layerConfigured = false;
    axesProperties = null;

    switch (position) {
      case LEFT:
          log(layer.getLayerId() + " " + position);
          bounds = calcLeftBounds();
          setWidth(bounds.width);
          break;
      case RIGHT:
          log(layer.getLayerId() + " " + position);
          bounds = calcRightBounds();
          setWidth(bounds.width);
          break;
      case TOP:
          log(layer.getLayerId() + " " + position);
          bounds = calcTopBounds();
          setHeight(bounds.height);
          break;
      case BOTTOM:
          bounds = calcBottomBounds();
          log(layer.getLayerId() + " " + position);
          setBounds(bounds);
          break;
    }

    layer.restore();
  }

  private Bounds calcLeftBounds() {
    log("calcLeftBounds "+ getBounds() + " " +getLayer().getLayerId() + getLayer().getBounds());

    double totalWidth = 0.0;
    double maxHeight = 0.0;

    for (AxisPanel p : subPanels) {
      log("calcLeftBounds subPanel "+p.getLayer().getLayerId() + p.getBounds() + " layer bounds:"+p.getLayer().getBounds());
      log("calcLeftBounds subPanel "+p.getType() + " "+p.getTextLayerName() + p.getBounds() + " " + p.getLayer().getLayerId() + p.getLayer().getBounds());

      p.init();
      p.layout();

      // p.setPosition(layer.getBounds().x + totalWidth, layer.getBounds().y);
      Bounds b = p.getBounds();
      p.setLayerOffset(totalWidth, 0);
      totalWidth += b.width;
      maxHeight = Math.max(maxHeight, b.height);
    }

    Bounds leftBounds = new Bounds(bounds.x, bounds.y, totalWidth, maxHeight);
    log("leftBounds:"+leftBounds);
    return leftBounds;
  }

  private Bounds calcRightBounds() {
    log("calcRightBounds "+ getBounds() + " " +getLayer().getLayerId() + getLayer().getBounds());

    double totalWidth = 0.0;
    double maxHeight = 0.0;
    for (AxisPanel p : subPanels) {
      log("calcRightBounds subPanel "+p.getLayer().getLayerId() + p.getBounds() + " layer bounds:"+p.getLayer().getBounds());
      log("calcRightBounds subPanel "+p.getType() + " "+p.getTextLayerName() + p.getBounds() + " " + p.getLayer().getLayerId() + p.getLayer().getBounds());

      p.init();
      p.layout();

      // p.setPosition(view.getWidth() - totalWidth, layer.getBounds().y);
      Bounds b = p.getBounds();
      p.setLayerOffset(totalWidth, 0);
      totalWidth += b.width;
      maxHeight = Math.max(maxHeight, b.height);
    }

    Bounds rightBounds = new Bounds(view.getWidth() - totalWidth, bounds.y, totalWidth, maxHeight);
    log("rightBounds:"+rightBounds);
    return rightBounds;
  }

  private Bounds calcTopBounds() {
    log("calcTopBounds "+ getBounds() + " " +getLayer().getLayerId() + getLayer().getBounds());

    double totalHeight = 0.0;
    double maxWidth = 0.0;
    for (AxisPanel p : subPanels) {
      log("calcTopBounds panel "+p.getLayer().getLayerId() + p.getBounds() + " layer bounds:"+p.getLayer().getBounds());
      p.init();
      p.layout();

      Bounds b = p.getBounds();
      // p.setPosition(bounds.x + totalHeight, bounds.y);
      totalHeight += b.height;
      maxWidth = Math.max(maxWidth, b.width);
    }
    Bounds topBounds = new Bounds(
      0, 0, maxWidth, totalHeight);// + TOP_PANEL_PAD);
    log("topBounds:"+topBounds);
    return topBounds;
  }

  private Bounds calcBottomBounds() {
    log("calcBottomBounds "+ getBounds() + " " +getLayer().getLayerId() + getLayer().getBounds());

    double totalHeight = 0.0;
    double maxWidth = 0.0;
    for (AxisPanel p : subPanels) {
      log("calcBottomBounds panel "+p.getLayer().getLayerId() + p.getBounds() + " layer bounds:"+p.getLayer().getBounds());
      p.init();
      p.layout();

      Bounds b = p.getBounds();
      totalHeight += b.height;
      p.setPosition(bounds.x, view.getHeight()-totalHeight);
      maxWidth = Math.max(maxWidth, b.width);
    }
    Bounds bottomBounds = new Bounds(
      bounds.x, bounds.y, maxWidth, totalHeight + TOP_PANEL_PAD);
    log("bottomBounds:"+bottomBounds);
    return bottomBounds;
  }

  public void setBounds(Bounds bounds) {
    log ("setBounds "+bounds);
    this.bounds = new Bounds(bounds);
    if (null != layer) {
      if (null != parent && !layer.equals(parent.getLayer())) {
        layer.save();

        layer.setBounds(bounds);
        log(layer.getLayerId() + " " + layer.getBounds());

        layer.restore();
      }
    } else {
      log ("setBounds "+bounds+ " null layer");
    }
  }

  public void setLayer(Layer layer) {
    if (null == layer) { return; } else
    if (layer.equals(this.layer)) { return; } else
    if (this.layer != null) {
      log("setLayer "+this.layer.getLayerId()+".dispose()");
      this.layer.dispose();
    }
    log("setLayer "+layer.getLayerId() + " "+layer.getBounds());

    this.layer = layer;
    for (AxisPanel subPanel : subPanels) {
      if (null == subPanel.getLayer()) {
        // Layer subLayer = view.getCanvas().createLayer(layer.getLayerId()+subPanels.indexOf(subPanel), bounds);
        subPanel.setLayer(layer);
      }
    }
  }
  
  public void setLayerOffset(double x, double y) {
    this.layerOffsetX = x;
    this.layerOffsetY = y;
  }

  public void setParent(Panel parent) {
    this.parent = parent;
  }
  
  public final void setPosition(double x, double y) {
    log(getType() + " setPosition "+x+", "+y);
    bounds.x = x;
    bounds.y = y;
    if (null == layer) { return; }

    layer.save();

    Bounds lb = layer.getBounds();
    setBounds(new Bounds(x, y, lb.width, lb.height));

    Panel parentPanel = getParent();
    /* if ((parentPanel != null) && parentPanel.getLayer().equals(layer)) {
    log("parentPanel = "+parentPanel.getLayer().getLayerId() + " "
    +parentPanel.getLayer().getBounds());

    log("layerOffsetX = "+x +" + "+ parentPanel.getLayerOffsetX());
    layerOffsetX = x + parentPanel.getLayerOffsetX();
    layerOffsetY = y + parentPanel.getLayerOffsetY();

    }

    layerOffsetX = x;
    layerOffsetY = y;  */
    for (AxisPanel subPanel : subPanels) {
      // subPanel.setLayerOffset(layerOffsetX, layerOffsetY);
      Bounds subBounds = subPanel.getBounds();
      subPanel.setBounds(new Bounds(x, y, subBounds.width, subBounds.height));
    }

    layer.restore();
  }

  public void setWidth(double width) {
    log("setWidth "+width + " "+bounds);
    bounds.width = width;
    if (null == layer) {
        return;
    }

    layer.save();

    Bounds lb = layer.getBounds();
    layer.setBounds(new Bounds(lb.x, lb.y, width, lb.height));

    switch (position) {
      case LEFT:
        break;
      case RIGHT:
        break;
      case TOP:
        for (AxisPanel p : this.subPanels) {
          Bounds b = p.getBounds();
          if (width != p.getBounds().width) {
            p.setBounds(new Bounds(b.x, b.y, width, b.height));
          }
        }
        break;
      case BOTTOM:
        for (AxisPanel p : this.subPanels) {
          Bounds b = p.getBounds();
          if (width != p.getBounds().width) {
            p.setBounds(new Bounds(b.x, b.y, width, b.height));
          }
        }
        break;
    }

    layer.restore();
  }

  public void setHeight(double height) {
    log("setHeight "+height + " "+bounds);
    bounds.height = height;
    if (null == layer) { return; }

    layer.save();

    Bounds lb = layer.getBounds();
    layer.setBounds(new Bounds(lb.x, lb.y, lb.width, height));

    switch (position) {
      case LEFT:
        for (AxisPanel p : this.subPanels) {
          Bounds b = p.getBounds();
          if (height != p.getBounds().height) {
            p.setBounds(new Bounds(b.x, b.y, b.width, height));
          }
        }
        break;
      case RIGHT:
        for (AxisPanel p : this.subPanels) {
          Bounds b = p.getBounds();
          if (height != p.getBounds().height) {
            p.setBounds(new Bounds(b.x, b.y, b.width, height));
          }
        }
        break;
      case TOP:
        break;
      case BOTTOM:
        break;
    }

    layer.restore();
  }
  
  public String toString() {
    return "CompositeAxisPanel[" + this.panelName + "]";
  }
  
/*
  private void clearPanel(Layer layer, Bounds bounds) {
    layer.save();
    layer.setFillColor(this.axesProperties.bgColor);
    layer.setStrokeColor(Color.WHITE);
    if (bounds.area() > 0) {
      // guard needed to store firefox bug
      // scaling by 0 in any dimension causes canvas to stop working
      layer.translate(bounds.x, bounds.y);
      layer.scale(bounds.width, bounds.height);
    }
    layer.clearRect(0, 0, 1, 1);
    layer.restore();
  }
*/

  private static void log(Object msg) {
    System.out.println("CompositeAxisPanel> " + msg);
  }

}
