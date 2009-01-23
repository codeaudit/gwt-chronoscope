package org.timepedia.chronoscope.client.browser.flashcanvas;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.ChronoscopeOptions;
import org.timepedia.chronoscope.client.canvas.AbstractLayer;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.canvas.CanvasPattern;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.PaintStyle;
import org.timepedia.chronoscope.client.canvas.RadialGradient;
import org.timepedia.chronoscope.client.canvas.CanvasImage;
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.render.LinearGradient;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * An implementation of the Layer interface using the Safari/WHATWG Javascript
 * CANVAS.
 *
 * @author Ray Cromwell
 */
public class FlashLayer extends AbstractLayer {

  private static final String[] compositeModes = {"copy", "source-atop",
      "source-in", "source-out", "source-over", "destination-atop",
      "destination-in", "destination-out", "destination-over", "darker",
      "lighter", "xor"};

  private static int layerCount = 0;

  // private String ctx;
  JavaScriptObject ctx;

  private String strokeColor;

  private String fillColor;

  private Bounds bounds;

  private final String layerId;

  private int zIndex = 0;

  private int zorder;

  private int scrollLeft;

  private boolean visibllity = true;

  private Element layerContainer;

  private float layerAlpha;

  private FlashCanvas fc;

  public FlashLayer(Canvas canvas, String layerId, Bounds b) {
    super(canvas);
    this.fc = (FlashCanvas) canvas;
    this.layerId = layerId;
    init(b);
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
    fc.cmd("CL", layerId);
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
    rect(x, y, width, height);
    cmd("C");
  }

  public void closePath() {
    cmd("c");
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

  public void drawImage(Layer layer, double x, double y, double width,
      double height) {
    if (layer instanceof FlashCanvas) {
      //   drawImage0(ctx, ( (BrowserCanvas) layer )
      //          .getRootLayer().getElement(), x, y, width, height);
    } else {
      pushNCmd("DI", 5);
      push(layer.getLayerId());
      push(x);
      push(y);
      push(width);
      push(height);
      //  drawImage0(ctx, ( (BrowserLayer) layer ).getElement(), x, y, width, height);
    }
  }

  public void drawImage(Layer layer, double sx, double sy, double swidth,
      double sheight, double dx, double dy, double dwidth, double dheight) {
    if (layer instanceof FlashLayer) {
      pushNCmd("DR", 9);
      push(layer.getLayerId());
      push(sx);
      push(sy);
      push(swidth);
      push(sheight);
      push(dx);
      push(dy);
      push(dwidth);
      push(dheight);
    }
  }

  public void drawImage(CanvasImage image, double dx, double dy, double dwidth,
      double dheight) {
    //TODO: not implemented
  }

  public void drawRotatedText(double x, double y, double angle, String label,
      String fontFamily, String fontWeight, String fontSize, String layerName,
      Chart chart) {
    selectLayer();
    cmd("RT", x, y, angle / Math.PI * 180, label, fontFamily, fontWeight,
        fontSize, layerName);
  }

  public void drawText(double x, double y, String label, String fontFamily,
      String fontWeight, String fontSize, String layerName, Cursor cursorStyle) {
    selectLayer();
    cmd("DT", x, y, label, fontFamily, fontWeight, fontSize, layerName);
  }

  public void fill() {
    cmd("f");
  }

  public void fillRect(double x, double y, double w, double h) {
    beginPath();
    moveTo(x,y);
    rect(x,y,w,h);
    fill();
//    cmd("fr", x, y, w, h);
  }

  public Bounds getBounds() {
    return bounds;
  }

  public Element getElement() {
    return ((FlashCanvas) getCanvas()).getElement();
  }

  public double getHeight() {
    return bounds.height;
  }

  public float getLayerAlpha() {
    return layerAlpha;
  }

//     public native boolean hasDrawCommands() /*-{
//        return this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashLayer::ctx.split(@org.timepedia.chronoscope.client.browser.flashcanvas.FlashLayer::CMDSEP).length > 3;
//    }-*/

  public Element getLayerElement() {
    return layerContainer;
  }

  public String getLayerId() {
    return layerId;
  }

  public int getLayerOrder() {
    return zorder;
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

//    public native String getFlashDisplayList() /*-{
//        return this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashLayer::ctx;
//    }-*/;

  public native boolean hasDrawCommands() /*-{
        return this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashLayer::ctx.length > 3;
    }-*/;

  public boolean isVisible() {
    return visibllity;
  }

  public void lineTo(double x, double y) {
    cmd("l", x, y);
  }

  public void moveTo(double x, double y) {
    cmd("m", x, y);
  }

  public void rect(double x, double y, double width, double height) {
    cmd("r", x, y, width, height);
  }

  public void restore() {
    cmd("Z");
    fc.popSelection();
  }

  public int rotatedStringHeight(String str, double rotationAngle,
      String fontFamily, String fontWeight, String fontSize) {
    return ((FlashCanvas) getCanvas()).stringHeight(str, fontFamily, fontWeight,
        fontSize, (float) (rotationAngle / Math.PI * 180f));
  }

  public int rotatedStringWidth(String str, double rotationAngle,
      String fontFamily, String fontWeight, String fontSize) {
    return ((FlashCanvas) getCanvas()).stringWidth(str, fontFamily, fontWeight,
        fontSize, (float) (rotationAngle / Math.PI * 180f));
  }

  public void save() {
    fc.pushSelection(layerId);
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
    cmd("SA", alpha);
    layerAlpha = alpha;
  }

  public void setLayerOrder(int zorder) {

    // TODO: set movieclip Z value
    this.zorder = zorder;
    cmd("ZO", getLayerId(), zorder);
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
    selectLayer();
    cmd("TB", layerName, bounds.x, bounds.y, bounds.width, bounds.height);
  }

  public void setTransparency(float value) {
    cmd("T", value);
  }

  public void setVisibility(boolean visibility) {
    this.visibllity = visibility;
    fc.cmd("SV", visibility ? 1 : 0);
  }

  public int stringHeight(String string, String font, String bold,
      String size) {
    return ((FlashCanvas) getCanvas())
        .stringHeight(string, font, bold, size, 0f);
  }

  public int stringWidth(String string, String font, String bold, String size) {
    return ((FlashCanvas) getCanvas())
        .stringWidth(string, font, bold, size, 0f);
  }

  public void stroke() {
    cmd("s");
  }

  public void translate(double x, double y) {
    cmd("t", x, y);
  }

  void init(Bounds b) {
    this.bounds = b;
//        layerContainer = DOM.createElement("div");
    this.bounds = new Bounds(b);
    String lc = String.valueOf(layerCount++);
//        DOM.setElementAttribute(layerContainer, "id", "_lc_" + layerId + lc);
//        DOM.setStyleAttribute(layerContainer, "width", "" + b.width + "px");
//        DOM.setStyleAttribute(layerContainer, "height", "" + b.height + "px");
//        DOM.setStyleAttribute(layerContainer, "visibility", "visible");
//        DOM.setStyleAttribute(layerContainer, "position", "absolute");
//        DOM.setStyleAttribute(layerContainer, "overflow", "hidden");
//        DOM.setStyleAttribute(layerContainer, "top", b.y + "px");
//        DOM.setStyleAttribute(layerContainer, "left", b.x + "px");
//        DOM.setStyleAttribute(layerContainer, "overflow", "visible");
  }

  private void cmd(String cmd) {
    selectLayer();
    fc.cmd(cmd);
  }

  private void cmd(String cmd, float value) {
    selectLayer();
    fc.cmd(cmd, value);
  }

  private void cmd(String cmd, double value) {
    selectLayer();
    fc.cmd(cmd, value);
  }

  private void cmd(String cmd, double arg1, double arg2) {
    selectLayer();
    fc.cmd(cmd, arg1, arg2);
  }

  private void cmd(String cmd, double arg1, double arg2, double arg3,
      double arg4) {
    selectLayer();
    fc.cmd(cmd, arg1, arg2, arg3, arg4);
  }

  private void cmd(String cmd, double arg1, double arg2, double arg3,
      double arg4, double arg5, double arg6) {
    selectLayer();
    fc.cmd(cmd, arg1, arg2, arg3, arg4, arg5, arg6);
  }

  private void cmd(String cmd, String value) {
    selectLayer();
    fc.cmd(cmd, value);
  }

  private void cmd(String cmd, String layerId, int value) {
    selectLayer();
    fc.cmd(cmd, layerId, value);
  }

  private void cmd(String s, double x, double y, String label,
      String fontFamily, String fontWeight, String fontSize, String layerName) {
    selectLayer();
    fc.cmd(s, x, y, label, fontFamily, fontWeight, fontSize, layerName);
  }

  private void cmd(String s, double x, double y, double a, String label,
      String fontFamily, String fontWeight, String fontSize, String layerName) {
    selectLayer();
    fc.cmd(s, x, y, a, label, fontFamily, fontWeight, fontSize, layerName);
  }

  private void cmd(String s, String layerName, double x, double y, double width,
      double height) {
    selectLayer();
    fc.cmd(s, layerName, x, y, width, height);
  }

  private String getFillColor() {
    return fillColor;
  }

  private FlashCanvas getFlashCanvas() {
    return fc;
  }

  private void push(String s) {
    fc.push(s);
  }

  private void push(double s) {
    fc.push(s);
  }

  private void pushNCmd(String cmd, int i) {
    selectLayer();
    fc.pushNCmd(cmd, i);
  }

  private void selectLayer() {
  }
}