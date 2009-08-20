package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.JavaScriptObject;

import org.timepedia.chronoscope.client.render.LinearGradient;

/**
 * An implementation of the LinearGradient interface on the JavaScript CANVAS
 */
public class BrowserLinearGradient implements LinearGradient {

  private final JavaScriptObject nativeGradient;

  public BrowserLinearGradient(BrowserLayer layer, double x, double y,
      double width, double height) {
    nativeGradient = createNativeGradient(layer.getContext(), x, y, width,
        height);
  }

  public void addColorStop(double position, String color) {
    addColorStop0(nativeGradient, position, color);
  }

  public JavaScriptObject getNative() {
    return nativeGradient;
  }

  private native void addColorStop0(JavaScriptObject nativeGradient,
      double position, String color) /*-{
       nativeGradient.addColorStop(position, color);
    }-*/;

  private native JavaScriptObject createNativeGradient(JavaScriptObject ctx,
      double x, double y, double width, double height) /*-{
         return ctx.createLinearGradient(x, y, width, height);
    }-*/;
}
