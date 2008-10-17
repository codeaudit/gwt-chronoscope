package org.timepedia.chronoscope.client.render;

/**
 * Used by a {@link DatasetRenderer} to determine how to render a given
 * dataset curve and its constituent data points.
 * 
 * @author Chad Takahashi
 */
public class RenderState {
  private boolean isHovered;
  private boolean isFocused;
  private boolean isDisabled;
  
  public boolean isDisabled() {
    return isDisabled;
  }
  
  public void setDisabled(boolean b) {
    this.isDisabled = b ;
  }
  
  public boolean isFocused() {
    return isFocused;
  }
  
  public void setFocused(boolean b) {
    this.isFocused = b;
  }
  
  public boolean isHovered() {
    return isHovered;
  }
  
  public void setHovered(boolean b) {
    this.isHovered = b;
  }
  
  public String toString() {
    return "disabled=" + isDisabled +
           ";focused=" + isFocused +
           ";hovered=" + isHovered;
  }
}
