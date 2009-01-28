package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.Color;
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

  private double layerOffsetX, layerOffsetY;
  private Panel parent;
  
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

  private GssProperties axesProperties;

  private Bounds bounds = new Bounds();
  
  private Layer layer;
  
  private boolean layerConfigured = false;

  private final String panelName;

  private XYPlot plot;
  
  private final Position position;
  
  private final List<AxisPanel> subPanels = new ArrayList<AxisPanel>();
  
  private StringSizer stringSizer;
  
  private View view;
  
  public CompositeAxisPanel(String panelName, Position position, XYPlot plot, View view) {
    this.panelName = panelName;
    this.position = position;
    this.plot = plot;
    this.view = view;
  }

  /** 
   * Adds the specified axis panel as a child of this panel.
   */
  public void add(AxisPanel subPanel) {
    ArgChecker.isNotNull(subPanel, "subPanel");

    subPanel.setParent(this);
    subPanels.add(subPanel);
    subPanel.setPlot(plot);
    subPanel.setView(view);
    subPanel.setStringSizer(stringSizer);
    subPanel.init();
    
    layout();
  }

  /**
   * Removes and unregisters all child panels from this container.
   */
  public void clear() {
    // Need to copy list to avoid ConcurrentModificationException
    List<AxisPanel> subPanelCopy = new ArrayList<AxisPanel>(this.subPanels);
    
    for (AxisPanel p : subPanelCopy) {
      remove(p);
    }
  }
  
  public void draw() {
    if (subPanels.size() == 0) {
      return;
    }

    layer.save();
    
    if (axesProperties == null) {
      axesProperties = view.getGssProperties(this, "");
    }

    if (!AxisPanel.GRID_ONLY) {
      clearPanel(this.layer, this.bounds);
    }
    
    for (int i = 0; i < subPanels.size(); i++) {
      AxisPanel subPanel = subPanels.get(i);
      if (!layerConfigured) {
        layer.setTextLayerBounds(subPanel.getTextLayerName(), 
            subPanel.getBounds());
      }
      
      subPanel.draw();
    }
    
    layer.restore();

    layerConfigured = true;
  }

  public Bounds getBounds() {
    return this.bounds;
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
    return this.layer;
  }
  
  public double getLayerOffsetX() {
    return this.layerOffsetX;
  }

  public double getLayerOffsetY() {
    return this.layerOffsetY;
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
    return this.parent;
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
    layerConfigured = false;
    axesProperties = null;
    
    for(AxisPanel panel : subPanels) {
      panel.init();
      panel.layout();
    }
    
    if (position.isHorizontal()) {
      double yPos = 0.0;
      double width = 0.0;
      for (Panel p : subPanels) {
        p.setPosition(0, yPos);
        Bounds b = p.getBounds();
        yPos += b.height;
        width = Math.max(width, b.width);
      }
      bounds.width = width;
      bounds.height = yPos;
      
      if (this.getPosition() == Position.TOP) {
        bounds.height += TOP_PANEL_PAD;
      }
    }
    else {
      double xPos = 0.0;
      double height = 0.0;
      for (Panel p : subPanels) {
        p.setPosition(xPos, 0);
        Bounds b = p.getBounds();
        xPos += b.width;
        height = Math.max(height, b.height);
      }
      bounds.width = xPos;
      bounds.height = height;
    }
  }

  /**
   * Removes and de-registers the specified child panel from this container.
   * @param childPanel
   */
  public void remove(AxisPanel childPanel) {
    subPanels.remove(childPanel);
    if (childPanel != null) {
      if (childPanel.layer != null) {
        childPanel.layer.clearTextLayer(childPanel.getTextLayerName());
      }
      childPanel.setParent(null);
      childPanel.setPlot(null);
      childPanel.setView(null);
      childPanel.setLayer(null);
      childPanel.setStringSizer(null);
    }
  }
  
  public void setLayer(Layer layer) {
    this.layer = layer;
    for (AxisPanel subPanel : subPanels) {
      subPanel.setLayer(layer);
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
    bounds.x = x;
    bounds.y = y;
    
    Panel parentPanel = getParent();
    layerOffsetX = x + parentPanel.getLayerOffsetX();
    layerOffsetY = y + parentPanel.getLayerOffsetY();
    for (AxisPanel subPanel : subPanels) {
      subPanel.setLayerOffset(layerOffsetX, layerOffsetY);
    }
  }

  public void setStringSizer(StringSizer stringSizer) {
    this.stringSizer = stringSizer;
  }
  
  public void setWidth(double width) {
    bounds.width = width;
    for (AxisPanel p : this.subPanels) {
      p.getBounds().width = width;
      p.layout();
    }
  }
  
  public String toString() {
    return "CompositeAxisPanel[" + this.panelName + "]";
  }
  
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

  private static void log(Object msg) {
    System.out.println("CompositeAxisPanel> " + msg);
  }

}
