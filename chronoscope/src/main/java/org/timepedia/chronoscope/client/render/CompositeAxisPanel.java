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

/**
 * A container that manages 0 or more {@link AxisPanel} objects. 
 * A {@link CompositeAxisPanel} can be assigned a {@link Position} relative to its 
 * container.  This panel is responsible for partitioning space for itself
 * and  allocating that space to each contained axis, as well as using its GSS 
 * {@link #axesProperties} to fill the background of the panel when cleared.
 */
public final class CompositeAxisPanel implements GssElement {

  private static final double TOP_PANEL_PAD = 23;

  public void insertBefore(OverviewAxisPanel oldPanel,
      AxisPanel subPanel) {
    subPanel.setParentPanel(this);
    subPanels.add(subPanels.indexOf(oldPanel), subPanel);
    subPanel.setPlot(plot);
    subPanel.setView(view);
    subPanel.init();
  }

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

  private final ArrayList<AxisPanel> subPanels = new ArrayList<AxisPanel>();

  private boolean layerConfigured = false;

  private final String panelName;

  private final Position position;
  
  private XYPlot plot;
  
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
    subPanel.init();
  }

  public void draw(Layer layer, Bounds panelBounds) {
    if (subPanels.size() == 0) {
      return;
    }

    if (axesProperties == null) {
      axesProperties = view.getGssProperties(this, "");
    }

    if (!AxisPanel.GRID_ONLY) {
      clearPanel(layer, panelBounds);
    }
    
    Bounds lPBounds = new Bounds(panelBounds);
    for (int i = 0; i < subPanels.size(); i++) {
      if (!layerConfigured) {
        layer.setTextLayerBounds(panelName + i, lPBounds);
      }
      AxisPanel axis = subPanels.get(i);
      lPBounds.width = axis.getWidth();
      lPBounds.height = axis.getHeight();

      axis.draw(layer, lPBounds);
      
      if (position.isHorizontal()) {
        lPBounds.y += lPBounds.height;
      } else {
        lPBounds.x += lPBounds.width;
      }
    }
    layerConfigured = true;
  }

  public int getAxisCount() {
    return subPanels.size();
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

  public double getHeight() {
    double height = 0;
    for (int i = 0; i < subPanels.size(); i++) {
      AxisPanel a = subPanels.get(i);
      if (position.isHorizontal()) {
        height += a.getHeight();
      } else {
        height = a.getHeight();
      }
    }
    return height + (getPosition() == Position.TOP ? TOP_PANEL_PAD : 0);
  }

  public String getPanelName() {
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

  public double getWidth() {
    double width = 0;
    for (int i = 0; i < subPanels.size(); i++) {
      AxisPanel a = subPanels.get(i);
      if (position.isHorizontal()) {
        width = Math.max(width, a.getWidth());
      } else {
        width += a.getWidth();
      }
    }
    return width;
  }

  public void layout() {
    layerConfigured = false;
    axesProperties = null;
    for(AxisPanel panel : subPanels) {
      panel.layout();
    }
  }

  public void remove(AxisPanel childPanel) {
    subPanels.remove(childPanel);
  }

  private void clearPanel(Layer layer, Bounds panelBounds) {
    layer.save();
    layer.setFillColor(this.axesProperties.bgColor);
    layer.setStrokeColor(Color.WHITE);
    if (position.isHorizontal()) {
      layer.scale(layer.getWidth(), layer.getHeight());
    } else if (panelBounds.area() > 0) {
      layer.scale(panelBounds.width, panelBounds.height);
      layer.translate(panelBounds.x, panelBounds.y);
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
