package org.timepedia.chronoscope.client.browser;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.canvas.CanvasReadyCallback;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.CanvasImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of Canvas that creates a CANVAS tag per Layer,
 */
public class BrowserCanvas extends Canvas {

  // private BrowserLayer rootLayer; // TODO - eliminate

  // is2Layer keys are logical IDs such as "domainAxisLayer" which
  //   might be associated with DOM div id = view17055_bl_domainAxisLayer
  //   and DOM canvas id = view17055_cv_domainAxisLayer
  //
  // There should be one BrowserCanvas per chart so a web page with
  // multiple charts would have separate BrowserCanvas contexts
  private final Map<String,BrowserLayer> id2Layer = new HashMap<String,BrowserLayer>();
  // private final Map<String,String> id2LayerCanvasElementId = new HashMap<String,String>();

  private final int width;
  private final int height;

  private boolean attached = false;
  private boolean visible = true;

  private String canvasDivElementId = "bc" + (int)(Math.random()*999.9);
  private Element canvasDivElement;

  public BrowserCanvas(BrowserView view, int width, int height) {
    super(view);
    this.width = width;
    this.height = height;

    canvasDivElementId = getView().getViewId() + "bc";
    if (null == canvasDivElement) {
      canvasDivElement = DOM.createElement("div");
    }
    DOM.setElementAttribute(canvasDivElement, "id", canvasDivElementId);

    Bounds initialBounds = new Bounds(0,0,view.getWidth(), view.getHeight());
    GwtView.initDivElement(canvasDivElement, initialBounds);
    GwtView.positionDivElement(canvasDivElement, initialBounds);

    Element viewElement = view.getElement();
    if ((viewElement != null) && (canvasDivElement !=null)) {
      DOM.appendChild(viewElement, canvasDivElement);
    }

    // create layer and append <div layer><canvas layer></div> to canvasDivElement
    // rootLayer = createLayer(Layer.BACKGROUND, new Bounds(0, 0, width, height));

  }

//  public void arc(double x, double y, double radius, double startAngle,
//      double endAngle, int clockwise) {
//    rootLayer.arc(x, y, radius, startAngle, endAngle, clockwise);
//  }

  public void attach(View view, CanvasReadyCallback canvasReadyCallback) {
    super.attach(view, canvasReadyCallback);
  }

//  public void beginPath() {
//    rootLayer.beginPath();
//  }

//  public void clearRect(double x, double y, double width, double height) {
//    rootLayer.clearRect(x, y, width, height);
//  }

//  public void clearTextLayer(String layerName) {
//    rootLayer.clearTextLayer(layerName);
//  }

//  public void clip(double x, double y, double width, double height) {
//    rootLayer.clip(x, y, width, height);
//  }

//  public void closePath() {
//    rootLayer.closePath();
//  }

//  public DisplayList createDisplayList(String id) {
//    return rootLayer.createDisplayList(id);
//  }

  public BrowserLayer createLayer(String layerId, Bounds b) {
    log("createLayer "+layerId+" bounds: "+b);
    BrowserLayer layer = getLayer(layerId);
    if (layer == null) {
      layer = new BrowserLayer(this, layerId, b);
      id2Layer.put(layerId, layer);
      // layer.clear();
    } else {
      layer.setBounds(b);
      if (attached && canvasDivElement !=null) {
        layer.onAttach();
      }
      log(layer.getLayerId()+" clear");
      layer.clear();
    }
    return layer;
  }

//  public LinearGradient createLinearGradient(double x, double y, double w,
//      double h) {
//    return rootLayer.createLinearGradient(x, y, w, h);
//  }
//
//  public PaintStyle createPattern(String imageUri) {
//    return rootLayer.createPattern(imageUri);
//  }
//
//  public RadialGradient createRadialGradient(double x0, double y0, double r0,
//      double x1, double y1, double r1) {
//    return rootLayer.createRadialGradient(x0, y0, r0, x1, y1, r1);
//  }

  public void disposeLayer(String layerId) {
    BrowserLayer layer = getLayer(layerId);
    if (layer != null) {
      layer.dispose();
    }
  }

  public void remove(String layerId) {
    id2Layer.remove(layerId);
  }

  public void dispose() {
    ArrayList<String> layerIDs = new ArrayList<String>(id2Layer.keySet());

    for (String layerId: layerIDs) {
      disposeLayer(layerId);
    }
  }

//  public void drawImage(Layer layer, double x, double y, double width,
//      double height) {
//    rootLayer.drawImage(layer, x, y, width, height);
//  }
//
//  public void drawImage(Layer layer, double sx, double sy, double swidth,
//      double sheight, double dx, double dy, double dwidth, double dheight) {
//    rootLayer.drawImage(layer, sx, sy, swidth, sheight, dx, dy, dwidth, dheight);
//  }
//
//  public void drawRotatedText(double x, double y, double angle, String label,
//      String fontFamily, String fontWeight, String fontSize, String layerName,
//      Chart chart) {
//    rootLayer.save();
//    rootLayer.drawRotatedText(x, y, angle, label, fontFamily, fontWeight,
//        fontSize, layerName, chart);
//    rootLayer.restore();
//  }
//
//  public void drawText(double x, double y, String label, String fontFamily,
//      String fontWeight, String fontSize, String layerName, Cursor cursor) {
//    rootLayer
//        .drawText(x, y, label, fontFamily, fontWeight, fontSize, layerName,
//            cursor);
//  }
//
//  public void fill() {
//    rootLayer.fill();
//  }

//  public void fillRect(double x, double y, double w, double h) {
//    // pixel alignment
//    rootLayer.fillRect(Math.floor(x), Math.floor(y), Math.ceil(w), Math.floor(h));
//  }
//
//  public void fillRect() {
//    rootLayer.fillRect();
//  }
//
  public Bounds getBounds() {
    return new Bounds(0,0,width,height);
  }

  public Element getElement() {
    return canvasDivElement;
  }

  public double getHeight() {
    return height; // TODO - make sure it tracks the aggregate height
  }

  public BrowserLayer getLayer(String layerId) {
    return id2Layer.get(layerId);
  }

//  public float getLayerAlpha() {
//    return rootLayer.getLayerAlpha();
//  }
//
//  public String getLayerId() {
//    return rootLayer.getLayerId();
//  }
//
//  public int getLayerOrder() {
//    return rootLayer.getLayerOrder();
//  }
//
  public Layer getRootLayer() {
    // return rootLayer;
    return createLayer(Layer.BACKGROUND, new Bounds(0, 0, width, height));
  }

  public CanvasImage createImage(String url) {
    return new BrowserCanvasImage(url);
  }

//  public int getScrollLeft() {
//    // TODO aggregate scroll left
//  }

//  public String getStrokeColor() {
//    return rootLayer.getStrokeColor();
//  }

//  public DomTextLayer.TextLayer getTextLayer(String layerName) {
//    return rootLayer.getTextLayer(layerName);
//  }

//  public String getTransparency() {
//    // TODO - overall transparecy?
//  }

  public double getWidth() {
    return width; // TODO - make sure it tracks the aggregate width
  }

  public boolean isVisible() {
    return visible;
  }

//  public void lineTo(double x, double y) {
//    rootLayer.lineTo(x, y);
//  }

//  public void moveTo(double x, double y) {
//    rootLayer.moveTo(x, y);
//  }

//  public void rect(double x, double y, double width, double height) {
//    rootLayer.rect(x, y, width, height);
//  }

//  public void restore() {
//    rootLayer.restore();
//  }

//  public int rotatedStringHeight(String str, double rotationAngle,
//      String fontFamily, String fontWeight, String fontSize) {
//    return rootLayer.rotatedStringHeight(str, rotationAngle, fontFamily,
//        fontWeight, fontSize);
//  }

//  public int rotatedStringWidth(String str, double rotationAngle,
//      String fontFamily, String fontWeight, String fontSize) {
//    return rootLayer.rotatedStringWidth(str, rotationAngle, fontFamily,
//        fontWeight, fontSize);
//  }

  public void save() {
    // TODO - save stack state?
    // rootLayer.save();
  }

//  public void scale(double sx, double sy) {
//    rootLayer.scale(sx, sy);
//  }

//  public void setCanvasPattern(CanvasPattern canvasPattern) {
//    rootLayer.setCanvasPattern(canvasPattern);
//  }

  public void setComposite(int mode) {
    // TODO - overall composite mode
    // rootLayer.setComposite(mode);
  }

//  public void setFillColor(Color color) {
//    rootLayer.setFillColor(color);
//  }

//  public void setFillColor(PaintStyle p) {
//    rootLayer.setFillColor(p);
//  }

//  public void setLayerAlpha(float alpha) { // TODO - aggregate alpha?
//    rootLayer.setLayerAlpha(alpha);
//  }

//  public void setLayerOrder(int zorder) {
//    rootLayer.setLayerOrder(zorder);
//  }

//  public void setLinearGradient(LinearGradient lingrad) {
//    rootLayer.setLinearGradient(lingrad);
//  }

//  public void setLineWidth(double width) {  // TODO - default line width for child layers?
//    rootLayer.setLineWidth(width);
//  }

//  public void setRadialGradient(RadialGradient radialGradient) { // TODO - default for children?
//    rootLayer.setRadialGradient(radialGradient);
//  }

//  public void setScrollLeft(int i) { // TODO - scroll all children?
//    // rootLayer.setScrollLeft(i);
//  }

//  public void setShadowBlur(double width) { // TODO - default?
//    rootLayer.setShadowBlur(width);
//  }

//  public void setShadowColor(String color) {
//    rootLayer.setShadowColor(color);
//  }

//  public void setShadowColor(Color shadowColor) {
//    rootLayer.setShadowColor(shadowColor);
//  }

//  public void setShadowOffsetX(double x) {
//    rootLayer.setShadowOffsetX(x);
//  }

//  public void setShadowOffsetY(double y) {
//    rootLayer.setShadowOffsetY(y);
//  }

//  public void setStrokeColor(Color color) {
//    rootLayer.setStrokeColor(color);
//  }

//  public void setStrokeColor(PaintStyle p) {
//    rootLayer.setStrokeColor(p);
//  }

  // public void setTextLayerBounds(String layerName, Bounds bounds) {
  //   rootLayer.setTextLayerBounds(layerName, bounds);
  // }

//  public void setTransparency(float value) {
//    rootLayer.setTransparency(value);
//  }

  public void setVisibility(boolean visibility) {
    Element canvasElement = DOM.getElementById(canvasDivElementId);
    if (null != canvasElement) {
      DOM.setStyleAttribute(canvasElement, "visibility",
        visibility ? "visible" : "hidden");
    }
//    if (rootLayer != null) {
//      rootLayer.setVisibility(visibility);
//    }
  }

//  public int stringHeight(String string, String font, String bold,
//      String size) {
//    return rootLayer.stringHeight(string, font, bold, size);
//  }

//  public int stringWidth(String string, String font, String bold, String size) {
//    return rootLayer.stringWidth(string, font, bold, size);
//  }

//  public void stroke() {
//    rootLayer.stroke();
//  }

//  public void translate(double x, double y) {
//    rootLayer.translate(x, y);
//  }

//  void onAttach(int width, int height) {
    // canvasDivElement = DOM.createDiv();
//    DOM.setElementAttribute(canvasDivElement, "width", "" + width);
//    DOM.setElementAttribute(canvasDivElement, "height", "" + height);
//    DOM.setStyleAttribute(canvasDivElement, "width", "" + width + "px");
//    DOM.setStyleAttribute(canvasDivElement, "height", "" + height + "px");
//    DOM.setStyleAttribute(canvasDivElement, "visibility", "hidden");
//    DOM.setStyleAttribute(canvasDivElement, "position", "absolute");
//    DOM.setStyleAttribute(canvasDivElement, "top", "0px");
//    DOM.setStyleAttribute(canvasDivElement, "left", "0px");
//  }

  // this is run when attached to DOM
  void onAttach() {
    // ctx might not have been available earlier but should be now
    attached = true;
    for (BrowserLayer bl: id2Layer.values()) {
      bl.onAttach();
    }
  }

  public boolean isAttached() {
    return attached;
  }

  private static void log (String msg) {
    System.out.println("BrowserCanvas> "+msg);
  }
}

