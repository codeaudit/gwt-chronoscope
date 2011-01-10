package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Focusable;

/**
 * Class used to capture down events in flash view and set the focus to the focusable 
 * container so as keyboard events work.
 * 
 * This is only needed in IE + Flash.
 */
public class CaptureFocusHandler implements FocusHandler, BlurHandler, MouseDownHandler {
  
  boolean focused = false;
  Focusable focusable;
  
  public CaptureFocusHandler(Focusable f) {
    focusable = f;
  }

  @Override
  public void onMouseDown(MouseDownEvent event) {
    if (!focused) {
      new Timer() {
        public void run() {
          focusable.setFocus(true);
      }}.schedule(5);
    }
  }

  @Override
  public void onBlur(BlurEvent event) {
    focused = false;
  }

  @Override
  public void onFocus(FocusEvent event) {
    focused = true;
  }

}
