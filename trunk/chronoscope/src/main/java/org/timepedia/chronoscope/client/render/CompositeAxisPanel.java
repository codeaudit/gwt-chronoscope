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
  
  private final ArrayList<AxisPanel> subPanels = new ArrayList<AxisPanel>();
  
  private View view;
  
  public CompositeAxisPanel(String panelName, Position position, XYPlot plot, View view) {
    this.panelName = panelName;
    this.position = position;
    this.plot = plot;
    this.view = view;
  }

  /** 
   * Adds the specified axis panel as a child of this panel.
   * This method automatically registers this object as the parent
   * of <tt>subPanel</tt> by calling the subpanel's 
   * {@link AxisPanel#setParentPanel(CompositeAxisPanel)} method. 
   */
  public void add(AxisPanel subPanel) {
    ArgChecker.isNotNull(subPanel, "subPanel");

    subPanel.setParentPanel(this);
    subPanels.add(subPanel);
    subPanel.setPlot(plot);
    subPanel.setView(view);
    subPanel.setLayer(layer);
    subPanel.init();
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
      clearPanel(layer);
    }
    
    Bounds subPanelBounds = new Bounds();
    subPanelBounds.setPosition(bounds.x, bounds.y);
    for (int i = 0; i < subPanels.size(); i++) {
      AxisPanel subPanel = subPanels.get(i);
      
      subPanelBounds.width = subPanel.getBounds().width;
      subPanelBounds.height = subPanel.getBounds().height;
      
      if (!layerConfigured) {
        layer.setTextLayerBounds(subPanel.getTextLayerName(), subPanelBounds);
      }
      
      //System.out.println("TESTING CompositeAxisPanel: " + axis.getTextLayerName() + "; i=" + i + ": axisBounds: " + axisBounds);
      subPanel.setBounds(subPanelBounds);
      subPanel.draw();
      
      if (position.isHorizontal()) {
        subPanelBounds.y += subPanelBounds.height;
      } else {
        subPanelBounds.x += subPanelBounds.width;
      }
    }
    
    layer.restore();

    layerConfigured = true;
  }

  public int getAxisCount() {
    return subPanels.size();
  }
  
  public Bounds getBounds() {
    return this.bounds;
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
    //return subPanels.indexOf(subPanel);
  }

  public String getName() {
    return panelName;
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
    
    bounds.width = calcWidth();
    bounds.height = calcHeight();
  }

  /**
   * Removes and deregisters the specified child panel from this container.
   * @param childPanel
   */
  public void remove(AxisPanel childPanel) {
    subPanels.remove(childPanel);
    if (childPanel != null) {
      if (childPanel.layer != null) {
        childPanel.layer.clearTextLayer(childPanel.getTextLayerName());
//        childPanel.layer.clear();
      }
      childPanel.setParentPanel(null);
      childPanel.setPlot(null);
      childPanel.setView(null);
      childPanel.setLayer(null);
    }
  }
  
  public void setLayer(Layer layer) {
    this.layer = layer;
    for (AxisPanel subPanel : subPanels) {
      subPanel.setLayer(layer);
    }
  }
  
  public final void setPosition(double x, double y) {
    bounds.x = x;
    bounds.y = y;
    for (AxisPanel subPanel : subPanels) {
      subPanel.setPosition(x, y);
    }
  }

  private double calcHeight() {
    double height = 0;
    for (int i = 0; i < subPanels.size(); i++) {
      AxisPanel a = subPanels.get(i);
      if (position.isHorizontal()) {
        height += a.getBounds().height;
      } else {
        height = a.getBounds().height;
      }
    }
    return height + (getPosition() == Position.TOP ? TOP_PANEL_PAD : 0);
  }

  private double calcWidth() {
    double width = 0;
    for (int i = 0; i < subPanels.size(); i++) {
      AxisPanel subPanel = subPanels.get(i);
      if (position.isHorizontal()) {
        width = Math.max(width, subPanel.getBounds().width);
      } else {
        width += subPanel.getBounds().width;
      }
    }
    return width;
  }

  private void clearPanel(Layer layer) {
    layer.save();
    layer.setFillColor(this.axesProperties.bgColor);
    layer.setStrokeColor(Color.WHITE);
    if (bounds.area() > 0) {
      // guard needed to store firefox bug
      // scaling by 0 in any dimension causes canvas to stop working
      layer.translate(bounds.x, bounds.y);
      layer.scale(bounds.width, bounds.height);
    }

//        layer.beginPath();
//        layer.setShadowBlur(0);
//        layer.setStrokeColor("rgba(0,0,0,0)");
//
//        layer.rect(0, 0, 1, 1);
//        layer.fill();
//        layer.stroke();
    layer.clearRect(0, 0, 1, 1);
    layer.restore();
  }
}
