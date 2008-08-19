/**
 * 
 */
package org.timepedia.chronoscope.client.browser.event;

/**
 * Represents a logical UI action whose completion requires 2 or more atomic UI
 * actions, as well has having distinct start and end states. Examples are:
 * <ul>
 * <li><b>mouse drag:</b> Dragging a UI object from one place to another.
 * Requires holding down the left mouse button while simultaneously moving the
 * mouse pointer.
 * <li><b>selection:</b> Selecting (or highlighting) some sub-region of the
 * canvas. Requires holding down the shift key and then initiating a mouse drag
 * operation (which is itself a compound action).
 * </ul>
 * 
 * @author chad takahashi
 */
public class CompoundUIAction {
  private Object source;
  private boolean isSelectAction;
  private int startX = -1;
  
  /**
   * Cancels this action.  Sets <tt>source</tt> to <tt>null</tt>, <tt>isSelecting</tt>
   * to <tt>false</tt>, and <tt>startX</tt> to <tt>-1</tt>.
   */
  public void cancel() {
    source = null;
    isSelectAction = false;
    startX = -1;
  }
  
  /**
   * Returns true if something is currently being draged within <i>any</i>
   * drag-capable component.
   */
  public boolean isDragging() {
    return this.source != null;
  }
  
  /**
   * Returns true only if something is being dragged within the specified UI
   * component.
   */
  public boolean isDragging(Object component) {
    return component == this.source;
  }

  /**
   * Returns true if something is currently being selected within <i>any</i>
   * selection-capable component.
   */
  public boolean isSelecting() {
    return isSelectAction;
  }

  /**
   * Returns true only if something is currently being selected within the
   * specified component.
   */
  public boolean isSelecting(Object component) {
    return isDragging(component) && isSelectAction;
  }

  /**
   * The UI component where this action was initiated.
   */
  public Object getSource() {
    return source;
  }

  public void setSource(Object source) {
    this.source = source;
  }

  /**
   * Set to <tt>true</tt> if a "select" action; <tt>false</tt> is assumed to 
   * mean this is a drag action.
   */
  public void setSelectAction(boolean isSelectAction) {
    this.isSelectAction = isSelectAction;
  }

  /**
   * Records the x-value the mouse pointer was at when this action was
   * initiated.
   */
  public int getStartX() {
    return startX;
  }

  public void setStartX(int startX) {
    this.startX = startX;
  }

  public String toString() {
    return "source=" + source + "; startX=" + startX + "; selectAction=" + isSelectAction;
  }
}
