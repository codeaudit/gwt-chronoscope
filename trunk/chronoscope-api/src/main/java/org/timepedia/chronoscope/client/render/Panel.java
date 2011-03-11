package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;

import java.util.List;

/**
 * Basic representation of a UI panel that has a size and screen position.
 * 
 * @author Chad Takahashi
 */
public interface Panel {

  /**
   * Draws this panel onto its associated canvas layer.
   */
  void draw();
  
  /**
   * Returns the location and dimension of this panel.  The location
   * (i.e. the x and y values) is relative to the panel's parent.
   */
  Bounds getBounds();
  
  /**
   * Returns the layer onto which this panel draws itself.
   */
  Layer getLayer();
  
  /**
   * Returns the X-offset of this panel's top-left corner with respect to
   * the layer onto which it is rendered.
   */
  double getLayerOffsetX();
  
  /**
   * Returns the Y-offset of this panel's top-left corner with respect to
   * the layer onto which it is rendered.
   */
  double getLayerOffsetY();

  /**
   * Returns the panel that contains this panel, or null if this is a 
   * top-level panel.
   */
  Panel getParent();

  /**
   * Returns the number of child panels that this panel has.
   */
  int getChildCount();
  /**
   * Returns the panels contained by this panel.
   */
  List<Panel> getChildren();
  
  /**
   * Sets the (x,y) layer offset of this panel
   * 
   * @see #getLayerOffsetX()
   * @see #getLayerOffsetY()
   */
  void setLayerOffset(double x, double y);
  
  /**
   * Sets the position of this panel's upper-left corner with respect
   * to its parent.
   */
  void setPosition(double x, double y);


  void layout();

  /**
   * release resources
   */
  void dispose();

  /**
   * clear references to child panels
   * @param panel
   */
  void remove(Panel panel);
}
