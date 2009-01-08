package org.timepedia.chronoscope.client.render;

/**
 * Basic representation of a UI panel that has a size and screen location,
 * and can additionally resize itself to a minimal or ideal width.
 * 
 * @author Chad Takahashi
 */
public interface Panel {

  // TODO: Would it make sense to make Bounds an interface with getter methods
  // so that other UI constructs can extend it?

  /**
   * Returns this panel's height.
   */
  double getHeight();

  /**
   * Returns this panel's width.
   */
  double getWidth();

  /**
   * Sets this panel's screen location.
   */
  void setLocation(double x, double y);

}
