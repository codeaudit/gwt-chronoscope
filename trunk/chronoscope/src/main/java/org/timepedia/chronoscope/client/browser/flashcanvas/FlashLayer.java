package org.timepedia.chronoscope.client.browser.flashcanvas;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.ChronoscopeOptions;
import org.timepedia.chronoscope.client.browser.BrowserCanvasImage;
import org.timepedia.chronoscope.client.browser.GwtLayer;
import org.timepedia.chronoscope.client.canvas.*;
import org.timepedia.chronoscope.client.render.LinearGradient;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * An implementation of the Layer interface using the Safari/WHATWG Javascript
 * CANVAS.
 *
 * @author Ray Cromwell
 */
public class FlashLayer extends GwtLayer {

  private boolean visibility = true;
  private String strokeColor;
  private String fillColor;
  private Bounds bounds;
  private boolean attached = false;
  private float layerAlpha;
  private int zIndex = 0;
  private int scrollLeft;
  private String layerId; // divElementId, embedElementId;
  private FlashCanvas flashCanvas;
  JsArrayMixed ctx;

  // DEBUG HACK
  public boolean SHOW_BOXES = false;

  public FlashLayer(FlashCanvas canvas, String layerId, Bounds bounds) {
    this.flashCanvas = canvas;
    this.layerId = layerId;
    this.bounds = new Bounds(bounds);
    save();
    setLayerOrder(Layer.Z_ORDER.indexOf(layerId) * 3);
    restore();
  }

  public void dispose() {
      bounds = null;

      // remove from the id2layer map in BrowserCanvas
      if (null != flashCanvas) { flashCanvas.remove(layerId);}
  }

  public void arc(double x, double y, double radius, double startAngle,
      double endAngle, int anticlockwise) {
    double sa = startAngle;// / Math.PI * 180;
    double ea = endAngle;// / Math.PI * 180;
    if (sa < 0) {
      sa += 360;
    }
    if (ea < 0) {
      sa += 360;
    }
    cmd("a", x, y, radius, sa, ea, anticlockwise);
  }

  public void beginPath() {
    cmd("b");
  }

  public void clear() {
    log("clear "+layerId);
    flashCanvas.cmd("CL", layerId);
  }

  public void clearRect(double x, double y, double width, double height) {
    if (width != 0 && height != 0) {
      cmd("CR", x, y, width, height);
    }
  }

  public void clearTextLayer(String layerName) {
    selectLayer();
    cmd("CT", layerName);
  }

  public void clip(double x, double y, double width, double height) {
    super.clip(x, y, width, height);
    beginPath();
    rect(x, y, Math.ceil(width), Math.ceil(height));
    cmd("C");
  }

  public void closePath() {
    cmd("c");
  }

  @Override
  public DisplayList createDisplayList(String id) {
    return new DefaultDisplayListImpl(id, this);
  }

  public LinearGradient createLinearGradient(double x, double y, double w,
      double h) {

    return new FlashLinearGradient(this, x, y, w, h);
  }

  public PaintStyle createPattern(String imageUri) {
    return new FlashCanvasPattern(this, imageUri);
  }

  public RadialGradient createRadialGradient(double x0, double y0, double r0,
      double x1, double y1, double r1) {
    return new FlashRadialGradient(this, x0, y0, r0, x1, y1, r1);
  }

  public void drawImage(Layer layer, double x, double y, double width, double height) {
    if (layer instanceof FlashCanvas) {
      // drawImage(((FlashCanvas) layer).getRootLayer(), x, y, width, height);
      // drawImage0(ctx, ( (BrowserCanvas) layer )
       //       .getRootLayer().getElement(), x, y, width, height);
    } else {
      pushNCmd("DI", 5);
      push(layer.getLayerId());
      push((int)x);
      push((int)y);
      push((int)width);
      push((int)height);
      // drawImage(((FlashCanvas) layer).getRootLayer(), x, y, width, height);
      //  drawImage0(ctx, ( (BrowserLayer) layer ).getElement(), x, y, width, height);
    }
  }

  public void drawImage(Layer layer, double sx, double sy, double swidth,
      double sheight, double dx, double dy, double dwidth, double dheight) {
    if (layer instanceof FlashLayer) {
      pushNCmd("DR", 9);
      push(layer.getLayerId());
      push((int)sx);
      push((int)sy);
      push((int)swidth);
      push((int)sheight);
      push((int)dx);
      push((int)dy);
      push((int)dwidth);
      push((int)dheight);
    }
  }

  public void drawImage(CanvasImage image, double dx, double dy, double dwidth, double dheight) {
      if (image instanceof BrowserCanvasImage) {
      pushNCmd("DE", 5);
      push(((BrowserCanvasImage)image).getNative().getElement().getId());
      push((int)dx);
      push((int)dy);
      push((int)dwidth);
      push((int)dheight);
    }
  }

  public void drawImage(CanvasImage image, double sx, double sy, double swidth,
      double sheight, double dx, double dy, double dwidth, double dheight) {
    if (image instanceof BrowserCanvasImage) {
      pushNCmd("DR", 9);
      push(((BrowserCanvasImage)image).getNative().getElement().getId());
      push((int)sx);
      push((int)sy);
      push((int)swidth);
      push((int)sheight);
      push((int)dx);
      push((int)dy);
      push((int)dwidth);
      push((int)dheight);
    }
  }

  public void drawRotatedText(double x, double y, double angle, String label,
      String fontFamily, String fontWeight, String fontSize, String layerName,
      Chart chart) {
    selectLayer();
    cmd("RT", x, y, angle / Math.PI * 180, label, fontFamily, fontWeight, fontSize, layerName);
  }

  public void drawText(double x, double y, String label, String fontFamily,
      String fontWeight, String fontSize, String layerName, Cursor cursorStyle) {
    selectLayer();
    cmd("DT", Math.floor(x), Math.floor(y), label, fontFamily, fontWeight, fontSize, layerName, cursorStyle);
  }

  public void fill() {
    cmd("f");
  }

  public void fillRect(double x, double y, double w, double h) {
    cmd("fr", x, y, w, h);
  }

  public Bounds getBounds() {
    return bounds;
  }

  public void setBounds(Bounds bounds) {
    log("setBounds "+layerId + " bounds: "+ bounds);
    if (null == bounds) { return; }
    flashCanvas.setLayerBounds(layerId, bounds);
    // cmd("SB", layerId, bounds.x, bounds.y, bounds.width, bounds.height);
  }

  public Element getElement() {
    return flashCanvas.getElement();
  }

  public double getHeight() {
    return bounds.height;
  }

  public float getLayerAlpha() {
    return layerAlpha;
  }

  public String getLayerId() {
    return layerId;
  }

  public int getLayerOrder() {
    return zIndex;
  }

  public int getScrollLeft() {
    return scrollLeft;
  }

  public String getStrokeColor() {
    return strokeColor;
  }

  public String getTransparency() {
    // TODO: implement via RPC call to flash? or local stack?
    return "1.0";
  }

  public double getWidth() {
    return bounds.width;
  }

  public boolean isVisible() {
    return visibility;
  }

  public void lineTo(double x, double y) {
    cmd("l", x, y);
  }

  public void moveTo(double x, double y) {
    cmd("m", x, y);
  }

  public void rect(double x, double y, double width, double height) {
    cmd("r", x, y, Math.ceil(width), Math.ceil(height));
  }

  public void restore() {
    cmd("Z");
    flashCanvas.popSelection();
  }

  public int rotatedStringHeight(String str, double rotationAngle, String fontFamily, String fontWeight, String fontSize) {
    return FlashCanvas.stringHeight(str, fontFamily, fontWeight, fontSize, (float) (rotationAngle / Math.PI * 180f));
  }

  public  int rotatedStringWidth(String str, double rotationAngle, String fontFamily, String fontWeight, String fontSize) {
    return FlashCanvas.stringWidth(str, fontFamily, fontWeight, fontSize, (float) (rotationAngle / Math.PI * 180f));
  }

  public void save() {
    flashCanvas.pushSelection(layerId);
    cmd("X");
  }

  public void scale(double sx, double sy) {
    cmd("y", sx, sy);
  }

  public void setCanvasPattern(CanvasPattern canvasPattern) {
    // TODO: no-op
  }

  public void setComposite(int mode) {
    cmd("CO", compositeModes[mode]);
  }

  public void setFillColor(Color c) {
    String color = c.getCSSColor();
    if ("transparent".equals(color)) {
      color = "rgba(0,0,0,0)";
    }

    try {
      fillColor = color;
      cmd("F", color);
    } catch (Throwable t) {
      if (ChronoscopeOptions.isErrorReportingEnabled()) {
        Window.alert("Error is " + t + " for color " + color);
      }
    }
  }

  public void setLayerAlpha(float alpha) {
     log("setLayerAlpha "+alpha);
    // cmd("SA", alpha);
    // layerAlpha = alpha;
  }

  public void setLayerOrder(int zIndex) {
    log(layerId + " setLayerOrder "+zIndex);
    // TODO - think of better default
    // TODO - test that flash is setting Z index
    this.zIndex = zIndex < 0 ? 3 : zIndex;
    cmd("ZO", layerId, zIndex);
  }

  public void setLinearGradient(LinearGradient lingrad) {
    try {
      FlashLinearGradient flg = (FlashLinearGradient) lingrad;
      ArrayList stops = flg.getStops();
      pushNCmd("LG", 4 + stops.size() * 2);
      push(flg.getX());
      push(flg.getY());
      push(flg.getX2());
      push(flg.getY2());
      Iterator it = stops.iterator();
      // TODO: should refer to gradient by unique ID, rather than retransmit
      while (it.hasNext()) {
        FlashLinearGradient.FlashColorStop stop
            = (FlashLinearGradient.FlashColorStop) it.next();
        push(stop.position);
        push(stop.color);
      }
    } catch (Throwable t) {
      if (ChronoscopeOptions.isErrorReportingEnabled()) {
        Window.alert("setLinearGradient: " + t);
      }
    }
  }

  public void setLineWidth(double width) {
    cmd("lw", width);
  }

  public void setRadialGradient(RadialGradient radialGradient) {
    // TODO: no-op , implement
  }

  public void setScrollLeft(int i) {
    scrollLeft = i;
    // TODO: set scroll
  }

  public void setShadowBlur(double width) {

    cmd("WB", width);
  }

  public void setShadowColor(String color) {

    cmd("WC", color);
  }

  public void setShadowOffsetX(double x) {
    cmd("WX", x);
  }

  public void setShadowOffsetY(double y) {
    cmd("WY", y);
  }

  public void setStrokeColor(Color c) {
    String color = c.getCSSColor();
    if ("transparent".equals(color)) {
      color = "rgba(0,0,0,0)";
    }

    try {
      strokeColor = color;
      cmd("S", color);
    } catch (Throwable t) {
      if (ChronoscopeOptions.isErrorReportingEnabled()) {
        Window.alert("Error is " + t + " for strokecolor " + color);
      }
    }
  }

  public void setTextLayerBounds(String layerName, Bounds bounds) {
    log ("DEPRECATED layerName setTextLayerBounds" + bounds);
    // selectLayer();
    // cmd("TB", layerName, bounds.x, bounds.y, bounds.width, bounds.height);
  }

  public void setTransparency(float value) {
    cmd("T", value);
  }

  public void setVisibility(boolean visibility) {
    this.visibility = visibility;
    flashCanvas.cmd("SV", visibility ? 1 : 0);
  }

  public int stringHeight(String string, String font, String bold, String size) {
    return FlashCanvas.stringHeight(string, font, bold, size, 0f);
  }

  public int stringWidth(String string, String font, String bold, String size) {
    return FlashCanvas.stringWidth(string, font, bold, size, 0f);
  }

  public void stroke() {
    cmd("s");
  }

  public void translate(double x, double y) {
    cmd("t", x, y);
  }

  /*
  void initLayerContainer(Bounds b) {
    this.bounds = b;
    layerDivElement = DOM.createElement("div");
    DOM.setElementAttribute(layerDivElement, "id", embedElementId);
    DOM.setStyleAttribute(layerDivElement, "width", "" + (int)b.width + "px");
    DOM.setStyleAttribute(layerDivElement, "height", "" + (int)b.height + "px");
    DOM.setStyleAttribute(layerDivElement, "visibility", "visible");
    DOM.setStyleAttribute(layerDivElement, "position", "absolute");
    DOM.setStyleAttribute(layerDivElement, "overflow", "hidden");
    DOM.setStyleAttribute(layerDivElement, "top", (int)b.y + "px");
    DOM.setStyleAttribute(layerDivElement, "left", (int)b.x + "px");
    DOM.setStyleAttribute(layerDivElement, "overflow", "visible");
  }*/

  private void cmd(String cmd) {
    // log(cmd);
    selectLayer();
    flashCanvas.cmd(cmd);
  }

  private void cmd(String cmd, float value) {
    // log(cmd + " "+value);
    selectLayer();
    flashCanvas.cmd(cmd, value);
  }

  private void cmd(String cmd, double value) {
    // log(cmd + " "+value);
    selectLayer();
    flashCanvas.cmd(cmd, value);
  }

  private void cmd(String cmd, double arg1, double arg2) {
    // log(cmd+" "+arg1+" "+arg2);
    selectLayer();
    flashCanvas.cmd(cmd, arg1, arg2);
  }

  private void cmd(String cmd, double arg1, double arg2, double arg3, double arg4) {
    // log(cmd + " " + arg1 + " "+arg3+" "+arg4);
    selectLayer();
    flashCanvas.cmd(cmd, arg1, arg2, arg3, arg4);
  }

  private void cmd(String cmd, double arg1, double arg2, double arg3,
      double arg4, double arg5, double arg6) {

    // log(cmd + " " + arg1 + " "+arg3+" "+arg4+ " "+arg5+" "+arg6);
    selectLayer();
    flashCanvas.cmd(cmd, arg1, arg2, arg3, arg4, arg5, arg6);
  }

  private void cmd(String cmd, String value) {
    // log(null == flashCanvas ? "null flashCanvas" : "flashCanvas != null");
    log(cmd + " "+value);
    selectLayer();
    flashCanvas.cmd(cmd, value);
  }

  private void cmd(String cmd, String layerId, int value) {
    // log(cmd + " layerId " + value);
    selectLayer();
    flashCanvas.cmd(cmd, layerId, value);
  }

  private void cmd(String s, double x, double y, String label,
      String fontFamily, String fontWeight, String fontSize, String layerName) {

    // log (s + " " + layerName + " x:"+x+" y:"+y);
    selectLayer();
    flashCanvas.cmd(s, x, y, label, fontFamily, fontWeight, fontSize, layerName);
  }

  private void cmd(String s, double x, double y, double a, String label,
      String fontFamily, String fontWeight, String fontSize, String layerName) {

    log (s + " " + layerName + " label: "+label);
    selectLayer();
    flashCanvas.cmd(s, x, y, a, label, fontFamily, fontWeight, fontSize, layerName);
  }

  private void cmd(String s, double x, double y, String label,
      String fontFamily, String fontWeight, String fontSize, String layerName, Cursor cursorStyle) {

    log (s + " " + layerName + " label: "+label);
    selectLayer();
    flashCanvas.cmd(s, x, y, label, fontFamily, fontWeight, fontSize, layerName, cursorStyle.name());
  }

  private void cmd(String s, String layerName, double x, double y, double width, double height) {

    log (s + " " + layerName + " "+ x + " "+ y +" " + width + " " + height);
    selectLayer();
    flashCanvas.cmd(s, layerName, x, y, width, height);
  }

  private String getFillColor() {
    return fillColor;
  }

  private FlashCanvas getFlashCanvas() {
    return flashCanvas;
  }

  private void push(String s) {
    flashCanvas.push(s);
  }

  private void push(double s) {   
    flashCanvas.push(s);
  }

  private void pushNCmd(String cmd, int i) {
    selectLayer();
    flashCanvas.pushNCmd(cmd, i);
  }

  private void selectLayer() {
  }

  public boolean isAttached() {
    return attached;
  }

 private static void log(String msg) {
   System.out.println("FlashLayer> "+msg);
 }

}