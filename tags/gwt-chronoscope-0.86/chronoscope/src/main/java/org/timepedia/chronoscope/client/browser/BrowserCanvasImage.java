package org.timepedia.chronoscope.client.browser;

import com.google.gwt.user.client.ui.Image;

import org.timepedia.chronoscope.client.canvas.CanvasImage;

/**
 */
public class BrowserCanvasImage implements CanvasImage {

  private Image image;
  public BrowserCanvasImage(String url) {
    Image.prefetch(url);
    image = new Image(url);
  }

  public double getWidth() {
    return image.getWidth();
  }

  public double getHeight() {
   return image.getHeight();
  }

  public Image getNative() { return image; }
}
