package org.timepedia.chronoscope.client.browser.vmlcanvas;

import com.google.gwt.user.client.Element;

import org.timepedia.chronoscope.client.browser.BrowserView;
import org.timepedia.chronoscope.client.browser.BrowserCanvas;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.Canvas;

public class VmlView extends BrowserView {

  protected Element getElement(Layer layer) {
    if (layer instanceof VmlCanvas) {
      return ((VmlCanvas) layer).getElement();
    }
    return null;
  }

  protected Canvas createCanvas(int width, int height) {
    return new VmlCanvas(this, width, height);
  }
}
