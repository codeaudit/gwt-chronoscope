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
   * 
   * @see #getX()
   * @see #getY()
   */
  void setLocation(double x, double y);

  /**
   * Causes the panel to lay itself out in the most visually desirable way,
   * ignoring any external width constraints, but while constraining height.
   */
  void resizeToIdealWidth();

  /**
   * Causes this panel to shrink its width as much as possible, while still
   * maintaining enough information for the user to make sense of its usage.
   * For example, a panel could reduce its width by decreasing the font 
   * size, shrinking whitespace, or using abbreviated text.
   */
  void resizeToMinimalWidth();
}
