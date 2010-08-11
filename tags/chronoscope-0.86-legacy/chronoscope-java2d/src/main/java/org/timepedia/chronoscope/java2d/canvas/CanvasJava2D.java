package org.timepedia.chronoscope.java2d.canvas;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.canvas.CanvasPattern;
import org.timepedia.chronoscope.client.canvas.CanvasReadyCallback;
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.DisplayList;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.PaintStyle;
import org.timepedia.chronoscope.client.canvas.RadialGradient;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.CanvasImage;
import org.timepedia.chronoscope.client.render.LinearGradient;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * A Java2D Canvas implementation to facillitate Server, Applet, and Application
 * rendering
 */
public class CanvasJava2D extends Canvas {

  private Layer rootLayer;

  private HashMap<String, Layer> id2Layer = new HashMap<String, Layer>();

  private LayerJava2D backingLayer;

  public CanvasJava2D(View view, int w, int h) {
    super(view);
    rootLayer = createLayer("rootLayer", new Bounds(0, 0, w, h));
    backingLayer = new LayerJava2D(this, "backing", new Bounds(0, 0, w, h));
    rootLayer.setFillColor(Color.TRANSPARENT);
    rootLayer.clearRect(0, 0, w, h);
  }

  public void arc(double x, double y, double radius, double startAngle,
      double endAngle, int clockwise) {
    rootLayer.arc(x, y, radius, startAngle, endAngle, clockwise);
  }

  public void attach(View view, CanvasReadyCallback canvasReadyCallback) {
    super.attach(view, canvasReadyCallback);
  }

  public void beginPath() {
    rootLayer.beginPath();
  }

  public void clearRect(double x, double y, double width, double height) {
    rootLayer.clearRect(x, y, width, height);
  }

  public void clearTextLayer(String textLayer) {
    rootLayer.clearTextLayer(textLayer);
  }

  public void clip(double x, double y, double width, double height) {
    rootLayer.clip(x, y, width, height);
  }

  public void closePath() {
    rootLayer.closePath();
  }

  public DisplayList createDisplayList(String id) {
    return rootLayer.createDisplayList(id);
  }

  public Layer createLayer(String layerId, Bounds b) {
    Layer layer = getLayer(layerId);
    if (layer == null) {
      layer = new LayerJava2D(this, layerId, b);
      id2Layer.put(layer.getLayerId(), layer);
      layer.setFillColor(Color.TRANSPARENT);
      layer.clearRect(0, 0, layer.getWidth(), layer.getHeight());
    }
    return layer;
  }

  public LinearGradient createLinearGradient(double startx, double starty,
      double endx, double endy) {
    return rootLayer.createLinearGradient(startx, starty, endx, endy);
  }

  public PaintStyle createPattern(String imageUri) {
    return rootLayer.createPattern(imageUri);
  }

  public RadialGradient createRadialGradient(double x0, double y0, double r0,
      double x1, double y1, double r1) {
    return rootLayer.createRadialGradient(x0, y0, r0, x1, y1, r1);
  }

  public void disposeLayer(String layerId) {
    Layer layer = getLayer(layerId);
    if (layer != null) {
      ((LayerJava2D) layer).dipose();
    }
    id2Layer.remove(layerId);
  }

  public void drawImage(Layer layer, double x, double y, double width,
      double height) {
    rootLayer.drawImage(layer, x, y, width, height);
  }

  public void drawImage(Layer layer, double sx, double sy, double swidth,
      double sheight, double dx, double dy, double dwidth, double dheight) {
    rootLayer
        .drawImage(layer, sx, sy, swidth, sheight, dx, dy, dwidth, dheight);
  }

  public void drawRotatedText(double x, double y, double v, String label,
      String fontFamily, String fontWeight, String fontSize, String layerName,
      Chart chart) {
    rootLayer.drawRotatedText(x, y, v, label, fontFamily, fontWeight, fontSize,
        layerName, chart);
  }

  public void drawText(double x, double y, String label, String fontFamily,
      String fontWeight, String fontSize, String textLayer, Cursor cursor) {
    rootLayer
        .drawText(x, y, label, fontFamily, fontWeight, fontSize, textLayer, cursor);
  }

  public void endFrame() {
    super.endFrame();
    ArrayList<Layer> layers = new ArrayList<Layer>(id2Layer.values());
    Collections.sort(layers, new Comparator<Layer>() {
      public int compare(Layer l1, Layer l2) {
        int diff = l1.getLayerOrder() - l2.getLayerOrder();
        if (diff != 0) {
          return diff;
        } else if (l1.getLayerOrder() == Layer.Z_LAYER_AXIS && l1.getLayerId()
            .startsWith("verticalAxis")) {
          return +1;
        } else {
          return 0;
        }
      }
    });
    int ord = 0;
    backingLayer.save();
    backingLayer.setFillColor("rgba(255,255,255,255)");
    backingLayer.setComposite(Layer.COPY);
    backingLayer
        .fillRect(0, 0, backingLayer.getWidth(), backingLayer.getHeight());
    backingLayer.restore();

    int i = 0;
    for (Layer l : layers) {
      Bounds b = l.getBounds();

      if (l.isVisible()  /* && !"rootLayer".equals(l.getLayerId()) && ("plotLayer".equals(l.getLayerId()) || "verticalAxis".equals(l.getLayerId()) ||
            "domainAxis".equals(l.getLayerId()) || "topLayer".equals(l.getLayerId())) */) {
        //    if(l.getLayerId().startsWith("highlight")) break;
//                System.out.println("layer " + l.getLayerId());
        backingLayer.save();
        backingLayer
            .drawImage(l, 0, 0, b.width, b.height, b.x, b.y, b.width, b.height);
        backingLayer.restore();
      }
    }
//        backingLayer.setFillColor("rgb(255, 0, 255)");
//        backingLayer.fillRect(0, 0, backingLayer.getWidth(), backingLayer.getHeight());
  }

  public void fill() {
    rootLayer.fill();
  }

  public void fillRect(double startx, double starty, double width,
      double height) {
    rootLayer.fillRect(startx, starty, width, height);
  }

  public void fillRect() {
    rootLayer.fillRect();
  }

  public Bounds getBounds() {
    return rootLayer.getBounds();
  }

  public Canvas getCanvas() {
    return rootLayer.getCanvas();
  }

  public double getHeight() {
    return rootLayer.getHeight();
  }

  public Image getImage() {
    return backingLayer.getImage();
  }

  public Layer getLayer(String layerId) {
    return id2Layer.get(layerId);
  }

  public float getLayerAlpha() {
    return rootLayer.getLayerAlpha();
  }

  public String getLayerId() {
    return rootLayer.getLayerId();
  }

  public int getLayerOrder() {
    return rootLayer.getLayerOrder();
  }

  public Layer getRootLayer() {
    return rootLayer;
  }

  public CanvasImage createImage(String url) {
    //TODO: implement
    return null;
  }

  public int getScrollLeft() {
    return rootLayer.getScrollLeft();
  }

  public String getStrokeColor() {
    return rootLayer.getStrokeColor();
  }

  public String getTransparency() {
    return rootLayer.getTransparency();
  }

  public double getWidth() {
    return rootLayer.getWidth();
  }

  public boolean isVisible() {
    return rootLayer.isVisible();
  }

  public void lineTo(double x, double y) {
    rootLayer.lineTo(x, y);
  }

  public void moveTo(double x, double y) {
    rootLayer.moveTo(x, y);
  }

  public void rect(double x, double y, double width, double height) {
    rootLayer.rect(x, y, width, height);
  }

  public void restore() {
    rootLayer.restore();
  }

  public int rotatedStringHeight(String str, double rotationAngle,
      String fontFamily, String fontWeight, String fontSize) {
    return rootLayer.rotatedStringHeight(str, rotationAngle, fontFamily,
        fontWeight, fontSize);
  }

  public int rotatedStringWidth(String str, double rotationAngle,
      String fontFamily, String fontWeight, String fontSize) {
    return rootLayer.rotatedStringWidth(str, rotationAngle, fontFamily,
        fontWeight, fontSize);
  }

  public void save() {
    rootLayer.save();
  }

  public void scale(double sx, double sy) {
    rootLayer.scale(sx, sy);
  }

  public void setCanvasPattern(CanvasPattern canvasPattern) {
    rootLayer.setCanvasPattern(canvasPattern);
  }

  public void setComposite(int mode) {
    rootLayer.setComposite(mode);
  }

  public void setFillColor(Color color) {
    rootLayer.setFillColor(color);
  }

  public void setFillColor(PaintStyle p) {
    rootLayer.setFillColor(p);
  }

  public void setLayerAlpha(float alpha) {
    rootLayer.setLayerAlpha(alpha);
  }

  public void setLayerOrder(int zorder) {
    rootLayer.setLayerOrder(zorder);
  }

  public void setLinearGradient(LinearGradient lingrad) {
    rootLayer.setLinearGradient(lingrad);
  }

  public void setLineWidth(double width) {
    rootLayer.setLineWidth(width);
  }

  public void setRadialGradient(RadialGradient radialGradient) {
    rootLayer.setRadialGradient(radialGradient);
  }

  public void setScrollLeft(int i) {
    rootLayer.setScrollLeft(i);
  }

  public void setShadowBlur(double width) {
    rootLayer.setShadowBlur(width);
  }

  public void setShadowColor(String color) {
    rootLayer.setShadowColor(color);
  }

  public void setShadowColor(Color shadowColor) {
    rootLayer.setShadowColor(shadowColor);
  }

  public void setShadowOffsetX(double x) {
    rootLayer.setShadowOffsetX(x);
  }

  public void setShadowOffsetY(double y) {
    rootLayer.setShadowOffsetY(y);
  }

  public void setStrokeColor(Color color) {
    rootLayer.setStrokeColor(color);
  }

  public void setStrokeColor(PaintStyle p) {
    rootLayer.setStrokeColor(p);
  }

  public void setTextLayerBounds(String textLayer, Bounds textLayerBounds) {
    rootLayer.setTextLayerBounds(textLayer, textLayerBounds);
  }

  public void setTransparency(float value) {
    rootLayer.setTransparency(value);
  }

  public void setVisibility(boolean visibility) {
    rootLayer.setVisibility(visibility);
  }

  public int stringHeight(String string, String font, String bold,
      String size) {
    return rootLayer.stringHeight(string, font, bold, size);
  }

  public int stringWidth(String string, String font, String bold, String size) {
    return rootLayer.stringWidth(string, font, bold, size);
  }

  public void stroke() {
    rootLayer.stroke();
  }

  public void translate(double x, double y) {
    rootLayer.translate(x, y);
  }
}
