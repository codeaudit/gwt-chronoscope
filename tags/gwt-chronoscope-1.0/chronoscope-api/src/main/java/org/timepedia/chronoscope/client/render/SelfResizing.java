/**
 * 
 */
package org.timepedia.chronoscope.client.render;

/**
 * Any UI component that is capable of resizing itself to either an ideal
 * or minimal size.
 * 
 * @author chad takahashi
 */
public interface SelfResizing {

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
