package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;

import java.util.ArrayList;

/**
 * An AxisPanel is a container which holds multiple Axis objects.  In
 * addition to holding Axis objects, AxisPanels are positioned
 * (LEFT/RIGHT/TOP/BOTTOM) and have orientation. They are responsible for
 * partitioning their space and allocating that space to each Axis, as well as
 * using GSS "axes" properties to fill the background of the panel when
 * cleared.
 */
public class AxisPanel implements GssElement {

  private static final double TOP_PANEL_PAD = 23;

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

  private final ArrayList<ValueAxis> axes = new ArrayList<ValueAxis>();

  private boolean layerConfigured = false;

  private final String panelName;

  private final Position position;

  private GssProperties axesProperties;

  public AxisPanel(String panelName, Position position) {
    this.panelName = panelName;
    this.position = position;
  }

  public void add(ValueAxis axis) {
    axes.add(axis);
    axis.init();
  }

  public boolean contains(ValueAxis theAxis) {
    return axes.contains(theAxis);
  }

  public void drawAxisPanel(XYPlot plot, Layer layer,
      Bounds panelPosition, boolean gridOnly) {

    if (axes.size() == 0) {
      return;
    }
    View view = plot.getChart().getView();

    if (axesProperties == null) {
      axesProperties = view.getGssProperties(this, "");
    }

    if (!gridOnly) {
      clearPanel(layer, panelPosition);
    }
    Bounds lPBounds = new Bounds(panelPosition);

    for (int i = 0; i < axes.size(); i++) {
      if (!layerConfigured) {
        layer.setTextLayerBounds(panelName + i, lPBounds);
      }
      ValueAxis axis = (ValueAxis) axes.get(i);
      lPBounds.width = axis.getWidth();
      lPBounds.height = axis.getHeight();

      axis.drawAxis(plot, layer, lPBounds, gridOnly);
      if (position.isHorizontal()) {
        lPBounds.y += lPBounds.height;
      } else {
        lPBounds.x += lPBounds.width;
      }
    }
    layerConfigured = true;
  }

  public int getAxisCount() {
    return axes.size();
  }

  public int getAxisNumber(ValueAxis axis) {
    return axes.indexOf(axis);
  }

  public double getHeight() {
    double height = 0;
    for (int i = 0; i < axes.size(); i++) {
      ValueAxis a = (ValueAxis) axes.get(i);
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
    for (int i = 0; i < axes.size(); i++) {
      ValueAxis a = (ValueAxis) axes.get(i);
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
    for(ValueAxis axis : axes) {
      axis.layout();
    }
  }

  public void remove(ValueAxis axis) {
    axes.remove(axis);
  }

  private void clearPanel(Layer layer, Bounds panelPosition) {
    layer.save();
    layer.setFillColor(this.axesProperties.bgColor);
    layer.setStrokeColor("#ffffff");
    if (position.isHorizontal()) {
      layer.scale(layer.getWidth(), layer.getHeight());
    } else if (panelPosition.area() > 0) {
      layer.scale(panelPosition.width, panelPosition.height);
      layer.translate(panelPosition.x, panelPosition.y);
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
