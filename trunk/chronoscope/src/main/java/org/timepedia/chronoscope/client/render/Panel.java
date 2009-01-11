package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.canvas.Bounds;

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
   * Returns the location and dimension of this panel.
   */
  Bounds getBounds();

  /**
   * Sets the position of this panel's upper-left corner with respect
   * to its parent.
   */
  void setPosition(double x, double y);

}
