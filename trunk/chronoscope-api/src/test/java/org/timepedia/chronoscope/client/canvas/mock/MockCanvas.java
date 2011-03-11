package org.timepedia.chronoscope.client.canvas.mock;

import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.CanvasImage;

import java.util.HashMap;

/**
 *
 */
public class MockCanvas extends Canvas {

  private int width;

  private int height;

  private Layer rootLayer;

  public MockCanvas(MockView mockView, int width, int height) {
    super(mockView);

    this.width = width;
    this.height = height;
    rootLayer = createLayer("rootLayer", new Bounds(0, 0, width, height));
  }

  private HashMap<String, Layer> layers = new HashMap<String, Layer>();

  public Layer createLayer(String layerId, Bounds bounds) {
    MockLayer ml = new MockLayer(this, layerId, bounds);
    layers.put(layerId, ml);
    return ml;
  }

  public void disposeLayer(String layerId) {
    layers.remove(layerId);
  }

  public void dispose() {
    for (String layerId:layers.keySet()) {
      disposeLayer(layerId);
    }
  }
  public Layer getLayer(String layerId) {
    return layers.get(layerId);
  }

  public Layer getRootLayer() {
    return rootLayer;
  }

  public CanvasImage createImage(String url) {
    return new CanvasImage() {

      public double getWidth() {
        return 10;
      }

      public double getHeight() {
        return 10;  
      }
    };
  }
}
