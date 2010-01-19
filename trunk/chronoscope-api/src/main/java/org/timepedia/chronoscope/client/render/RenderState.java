package org.timepedia.chronoscope.client.render;

/**
 * Used by a {@link DatasetRenderer} to determine how to render a given
 * dataset curve and its constituent data points.
 * 
 * @author Chad Takahashi
 */
public class RenderState {
  private boolean isFocused;
  private boolean isDisabled;
  private int passNumber = 0;
  private Object userData;

  public <T> T getUserData() {
    return (T)userData;
  }

  public void setUserData(Object userData) {
    this.userData = userData;
  }

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
  
  public String toString() {
    return "disabled=" + isDisabled + ";focused=" + isFocused;
  }

  public int getPassNumber() {
    return passNumber;
  }

  public void setPassNumber(int passNumber) {
    this.passNumber = passNumber;
  }
}
