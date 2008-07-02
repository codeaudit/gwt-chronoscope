package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;

import java.util.ArrayList;

/**
 * An AxisPanel is a container which holds multiple Axis objects <p/> In
 * addition to holding Axis objects, AxisPanels are positioned
 * (LEFT/RIGHT/TOP/BOTTOM) and have orientation. <p/> They are responsible for
 * partitioning their space and allocating that space to each Axis, as well as
 * using GSS "axes" properties to fill the background of the panel when
 * cleared.
 */
public class AxisPanel implements GssElement {

  public static final int LEFT = 0, RIGHT = 1, TOP = 2, BOTTOM = 3;

  public static final int VERTICAL_AXIS = 0;

  public static final int HORIZONTAL_AXIS = 1;

  private final ArrayList axes = new ArrayList();

  private boolean layerConfigured = false;

  private final String panelName;

  private final int position;

  private GssProperties axesProperties;

  private int orientation;

  public AxisPanel(String panelName, int position) {
    this.panelName = panelName;
    this.position = position;
    this.orientation = position == LEFT || position == RIGHT ? VERTICAL_AXIS
        : HORIZONTAL_AXIS;
  }

  public void add(ValueAxis axis) {
    axes.add(axis);
    axis.init();
  }

  public boolean contains(ValueAxis theAxis) {
    return axes.contains(theAxis);
  }

  public void drawAxisPanel(DefaultXYPlot plot, Layer layer,
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
      if (axis.getOrientation() == HORIZONTAL_AXIS) {
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
      if (a.getOrientation() == HORIZONTAL_AXIS) {
        height += a.getHeight();
      } else {
        height = a.getHeight();
      }
    }
    return height;
  }

  public int getOrientation() {
    return orientation;
  }

  public String getPanelName() {
    return panelName;
  }

  public GssElement getParentGssElement() {
    return null;
  }

  public int getPosition() {
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
      if (a.getOrientation() == VERTICAL_AXIS) {
        width += a.getWidth();
      } else {
        width = Math.max(width, a.getWidth());
      }
    }
    return width;
  }

  public void remove(ValueAxis axis) {
    axes.remove(axis);
  }

  private void clearPanel(Layer layer, Bounds panelPosition) {
    layer.save();
    layer.setFillColor(this.axesProperties.bgColor);
    layer.setStrokeColor("#ffffff");
    if (position == BOTTOM || position == TOP) {
      layer.scale(layer.getWidth(), layer.getHeight());
    } else if (panelPosition.area() > 0){
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
