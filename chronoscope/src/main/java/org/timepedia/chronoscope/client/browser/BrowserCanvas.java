package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

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

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of Canvas that creates a CANVAS tag per Layer,
 */
public class BrowserCanvas extends Canvas {

  private BrowserLayer rootLayer;

  private final Map<String,Layer> id2Layer = new HashMap<String,Layer>();

  private final int width;

  private final int height;

  private Element canvasElement;

  public BrowserCanvas(View view, int width, int height) {
    super(view);
    this.width = width;
    this.height = height;
    init(width, height);
    BrowserView bv = (BrowserView) view;
    DOM.appendChild(bv.getElement(), canvasElement);
    rootLayer = (BrowserLayer) createLayer("rootLayer",
        new Bounds(0, 0, width, height));
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

  public void clearTextLayer(String layerName) {
    rootLayer.clearTextLayer(layerName);
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
      layer = new BrowserLayer(this, layerId, b);
      id2Layer.put(layer.getLayerId(), layer);
      DOM.appendChild(canvasElement, ((BrowserLayer) layer).getLayerElement());
      layer.clear();
    } else {
      layer.clear();
      // layer.clearTextLayer(layer.getLayerId());
      if (null != b) { // TODO - more sensible setBounds
        layer.getBounds().x = b.x;
        layer.getBounds().y = b.y;
        layer.getBounds().width = b.width;
        layer.getBounds().height = b.height;
      }
    }
    return layer;
  }
  /*
  public Layer createLayer(String layerId, Bounds b) {
    Layer layer = getLayer(layerId);
    if (layer == null) {
      layer = new BrowserLayer(this, layerId, b);
      id2Layer.put(layer.getLayerId(), layer);
      DOM.appendChild(canvasElement, ((BrowserLayer) layer).getLayerElement());
      layer.setFillColor(Color.TRANSPARENT);
      layer.clearRect(0, 0, layer.getWidth(), layer.getHeight());
    } else {
      layer.clear();
      layer.clearTextLayer(layer.getLayerId());
      if (null != b) {
        layer.getBounds().x = b.x;
        layer.getBounds().y = b.y;
        layer.getBounds().width = b.width;
        layer.getBounds().height = b.height;
      }
    }

    return layer;
  } */

  public LinearGradient createLinearGradient(double x, double y, double w,
      double h) {
    return rootLayer.createLinearGradient(x, y, w, h);
  }

  public PaintStyle createPattern(String imageUri) {
    return rootLayer.createPattern(imageUri);
  }

  public RadialGradient createRadialGradient(double x0, double y0, double r0,
      double x1, double y1, double r1) {
    return rootLayer.createRadialGradient(x0, y0, r0, x1, y1, r1);
  }

//  public Element createTextDiv() {
//    return rootLayer.createTextDiv();
//  }
  /*
  public void disposeLayer(String layerId) {
    Layer layer = getLayer(layerId);
    if (null == layer) {
      return;
    }

    id2Layer.remove(layerId);
    DOM.removeChild(canvasElement, ((BrowserLayer) layer).getLayerElement());
  } */
  public void disposeLayer(String layerId) {
    Layer layer = getLayer(layerId);
    if (layer != null) {
      DOM.removeChild(canvasElement, ((BrowserLayer) layer).getLayerElement());
    }
    id2Layer.remove(layerId);
  }


  public void drawImage(Layer layer, double x, double y, double width,
      double height) {
    rootLayer.drawImage(layer, x, y, width, height);
  }

  public void drawImage(Layer layer, double sx, double sy, double swidth,
      double sheight, double dx, double dy, double dwidth, double dheight) {
    rootLayer.drawImage(layer, sx, sy, swidth, sheight, dx, dy, dwidth, dheight);
  }

  public void drawRotatedText(double x, double y, double angle, String label,
      String fontFamily, String fontWeight, String fontSize, String layerName,
      Chart chart) {
    rootLayer.save();
    rootLayer.drawRotatedText(x, y, angle, label, fontFamily, fontWeight,
        fontSize, layerName, chart);
    rootLayer.restore();
  }

  public void drawText(double x, double y, String label, String fontFamily,
      String fontWeight, String fontSize, String layerName, Cursor cursor) {
    rootLayer
        .drawText(x, y, label, fontFamily, fontWeight, fontSize, layerName,
            cursor);
  }

  public void fill() {
    rootLayer.fill();
  }

  public void fillRect(double x, double y, double w, double h) {
    // pixel alignment
    rootLayer.fillRect(Math.floor(x), Math.floor(y), Math.ceil(w), Math.floor(h));
  }

  public void fillRect() {
    rootLayer.fillRect();
  }

  public Bounds getBounds() {
    return rootLayer.getBounds();
  }

  public JavaScriptObject getContext() {
    return rootLayer.getContext();
  }

  public Element getElement() {
    return canvasElement;
  }

  public double getHeight() {
    return rootLayer.getHeight();
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
    return new BrowserCanvasImage(url);
  }

  public int getScrollLeft() {
    return rootLayer.getScrollLeft();
  }

  public String getStrokeColor() {
    return rootLayer.getStrokeColor();
  }

//  public DomTextLayer.TextLayer getTextLayer(String layerName) {
//    return rootLayer.getTextLayer(layerName);
//  }

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

  // public void setTextLayerBounds(String layerName, Bounds bounds) {
  //   rootLayer.setTextLayerBounds(layerName, bounds);
  // }

  public void setTransparency(float value) {
    rootLayer.setTransparency(value);
  }

  public void setVisibility(boolean visibility) {
    DOM.setStyleAttribute(canvasElement, "visibility",
        visibility ? "visible" : "hidden");
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

  // TODO - init with id to prevent making too many, or make id idempotent
  void init(int width, int height) {
    canvasElement = DOM.createDiv();
    DOM.setElementAttribute(canvasElement, "width", "" + width);
    DOM.setElementAttribute(canvasElement, "height", "" + height);
    DOM.setStyleAttribute(canvasElement, "width", "" + width + "px");
    DOM.setStyleAttribute(canvasElement, "height", "" + height + "px");
    DOM.setStyleAttribute(canvasElement, "visibility", "hidden");
    DOM.setStyleAttribute(canvasElement, "position", "absolute");
    DOM.setStyleAttribute(canvasElement, "top", "0px");
    DOM.setStyleAttribute(canvasElement, "left", "0px");
  }
}
