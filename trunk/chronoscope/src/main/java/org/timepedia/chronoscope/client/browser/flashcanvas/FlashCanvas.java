package org.timepedia.chronoscope.client.browser.flashcanvas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.browser.BrowserCanvasImage;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.canvas.CanvasImage;
import org.timepedia.chronoscope.client.canvas.CanvasPattern;
import org.timepedia.chronoscope.client.canvas.CanvasReadyCallback;
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.DisplayList;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.PaintStyle;
import org.timepedia.chronoscope.client.canvas.RadialGradient;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.render.LinearGradient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;

public class FlashCanvas extends Canvas {

  public static String FLASH_ALTERNATIVES = "\n<!-- chart not visible in browser without canvas element -->\n";   // TODO - make this configurable.
          // "<p>Modern browsers such as Chrome, Safari, Firefox, or Internet Explorer 9 use javascript and HTML (rather than Flash) for a faster charting experience.</p>\n";

  public static String FLASH_ADVICE =
        "<p>If you're using Internet Explorer 6, 7, or 8 you need to enable or install Flash Player to experience these charts.</p>\n" +
        "<p><a href=\"http://www.adobe.com/go/getflashplayer\"><img src=\"http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif\" alt=\"Get Adobe Flash player\" /></a></p>\n" +
                FLASH_ALTERNATIVES;




  public static final String CMDSEP = "%";

  private static int canvasNumber = 0;

  private static native void createFlashLayer(String canvasId, String layerId,
      double x, double y, double width, double height) /*-{
        var flashCanvas = $wnd.navigator.appName.indexOf("Microsoft") != -1 ? $wnd[canvasId] : $doc[canvasId];
        flashCanvas && flashCanvas.createCanvas && flashCanvas.createCanvas(layerId, x, y, width, height);
    }-*/;

  private static native void drawFlashCanvas(String canvasId, String cmds) /*-{
        var flashCanvas = $wnd.navigator.appName.indexOf("Microsoft") != -1 ? $wnd[canvasId] : $doc[canvasId];
        flashCanvas && flashCanvas.drawframe && flashCanvas.drawframe(cmds);
    }-*/;

  private static native void flashDisposeLayer(String canvasId, String layerId) /*-{
        var flashCanvas = $wnd.navigator.appName.indexOf("Microsoft") != -1 ? $wnd[canvasId] : $doc[canvasId];
        flashCanvas && flashCanvas.disposeCanvas && flashCanvas.disposeCanvas(layerId);
    }-*/;

  ArrayList selectedLayers = new ArrayList();

  String selectedLayerId = "";

  private FlashLayer rootLayer;

  private final HashMap id2Layer = new HashMap();

  private final ArrayList layers = new ArrayList();

  private final int width;

  private final int height;

  private Element canvasElement;

  private String canvasId;

  private String readyFn = "";

  private JavaScriptObject ctx = null;

  public FlashCanvas(View view, int width, int height) {
    super(view);
    this.width = width;
    this.height = height;
    canvasId = "flashCanvas" + canvasNumber++;
    init(width, height);
  }

  public void arc(double x, double y, double radius, double startAngle,
      double endAngle, int clockwise) {
    rootLayer.arc(x, y, radius, startAngle, endAngle, clockwise);
  }

  public void attach(final View view, final CanvasReadyCallback canvasReadyCallback) {
    final FlashView bv = (FlashView) view;
    exportReadyFn(readyFn, view, new CanvasReadyCallback() {
      boolean initalized = false;

      public void onCanvasReady(Canvas canvas) {
        if (!initalized) {
          initalized = true;
          canvasReadyCallback.onCanvasReady(canvas);
        } else {
          resyncLayers();
        }
      }
    });
    DOM.appendChild(bv.getElement(), canvasElement);
    DOM.setElementAttribute(canvasElement, "width", "" + width);
    DOM.setElementAttribute(canvasElement, "height", "" + height);
    DOM.setStyleAttribute(canvasElement, "width", "" + width + "px");
    DOM.setStyleAttribute(canvasElement, "height", "" + height + "px");
    DOM.setStyleAttribute(canvasElement, "visibility", "visible");
    DOM.setStyleAttribute(canvasElement, "position", "absolute");
    DOM.setStyleAttribute(canvasElement, "top", "0px");
    DOM.setStyleAttribute(canvasElement, "left", "0px");

//    FlashResources flashResources = GWT.create(FlashResources.class);

    String swfUrl = Chronoscope
        .getURL(GWT.getModuleBaseURL()+"flcanvas.swf");//flashResources.flashCanvas().getUrl());
    String codeBasePref = GWT.getHostPageBaseURL().startsWith("https") ?
        "https" : "http";

    DOM.setInnerHTML(canvasElement,
        "<object style=\"position:absolute;top: 0px;left:0px; z-index: 0\" classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" \n"
            + "codebase=\""+ codeBasePref + "://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,40,0\" \n"
            + " width=\"100%\" height=\"100%\" \n" + "id=\""
            + canvasId + "\"> \n" + "<param name=\"movie\" value=\"" + swfUrl
            + "\"> \n" + "<param name=\"FlashVars\" value=\"readyFn=" + readyFn
            + "\">" + "<param name=\"quality\" value=\"high\"> \n"
            + "<param name=\"bgcolor\" value=\"#FFFFFF\"> \n"
            + "<param name=\"wmode\" value=\"opaque\">\n"
            + "<param name=\"MENU\" value=\"false\">\n"
            + "<param name=\"allowScriptAccess\" value=\"always\">\n"
            + "<embed style=\"position:absolute;top:0px;left:0px;z-index: 0\" src=\""
            + swfUrl + "\" quality=\"high\" bgcolor=\"#FFFFFF\"\n" + "width=\"100%\" height=\"100%\" \n" + "name=\"" + canvasId
            + "\" align=\"\" type=\"application/x-shockwave-flash\" \n"
            + "FlashVars=\"readyFn=" + readyFn + "\" "
            + "wmode=\"opaque\" " + "MENU=\"false\""
            + "allowScriptAccess=\"always\" "
            + "pluginspage=\"" + codeBasePref + "://www.macromedia.com/go/getflashplayer\"> \n"
            + "</embed> \n" + "</object>");
    
    com.google.gwt.dom.client.Element oElement = canvasElement
        .getElementsByTagName("object").getItem(0);
    fixSWFInIE(oElement.getId(), oElement);
  }

  private native void fixSWFInIE(String id,
      com.google.gwt.dom.client.Element element) /*-{
     try {
       $wnd[id]=element;
     } catch(e) {}
  }-*/;

  public void beginFrame() {
    super.beginFrame();
    selectedLayerId = getLayerId();
    selectedLayers.clear();
//        Iterator it = id2Layer.values().iterator();
//        while(it.hasNext()) {
//            FlashLayer fl=(FlashLayer)it.next();
//            fl.beginFrame();
//        }
    clearFlashDisplayList();
  }

  public void beginPath() {
    rootLayer.beginPath();
  }

  public void canvasSetupDone() {
    flashCanvasSetupDone(canvasId);
  }

  public void clearRect(double x, double y, double width, double height) {
    rootLayer.clearRect(x, y, width, height);
  }

//    public Element createTextDiv() {
//        return rootLayer.createTextDiv();
//    }

//    public DomTextLayer.TextLayer getTextLayer(String layerName) {
//        return rootLayer.getTextLayer(layerName);
//    }

  public void clearTextLayer(String layerName) {
    rootLayer.clearTextLayer(layerName);
  }

  public void clip(double x, double y, double width, double height) {
    rootLayer.clip(x, y, width, height);
  }

  public void closePath() {
    rootLayer.closePath();
  }

  public final native void cmd(String cmd, String arg) /*-{
             this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.push(cmd, 1, arg);
      }-*/;

  public final native void cmd(String cmd, float arg) /*-{
             this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.push(cmd, 1, arg);
      }-*/;

  public final native void cmd(String cmd, double arg) /*-{
             this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.push(cmd, 1, arg);
      }-*/;

  public final native void cmd(String cmd, double arg1, double arg2) /*-{
             this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.push(cmd, 2, arg1, arg2);
      }-*/;

  public final native void cmd(String cmd, double arg1, double arg2,
      double arg3, double arg4) /*-{
             this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.push(cmd, 4, arg1, arg2,
                     arg3, arg4);
         }-*/;

  public final native void cmd(String cmd, double arg1, double arg2,
      double arg3, double arg4, double arg5, double arg6) /*-{
                this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.push(cmd, 6,
                        arg1, arg2, arg3, arg4, arg5, arg6);
      }-*/;

  public final native void cmd(String cmd) /*-{
                this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.push(cmd, 0);
      }-*/;

  public final native void cmd(String cmd, String arg1, int arg2) /*-{
             this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.push(cmd, 2, arg1, arg2);
         }-*/;

  public final native void cmd(String cmd, double x, double y, String label,
      String fontFamily, String fontWeight, String fontSize, String layerName) /*-{
           this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.push(cmd, 7,
                   x, y, label, fontFamily, fontWeight, fontSize, layerName);
    }-*/;

  public final native void cmd(String cmd, String layerName, double x, double y, double width, double height) /*-{
           this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.push(cmd, 5,
                   layerName, x, y, width, height);
    }-*/;

  public final native void cmd(String cmd, double x, double y, double a,
      String label, String fontFamily, String fontWeight, String fontSize,
      String layerName) /*-{
           this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.push(cmd, 8,
                   x, y, a, label, fontFamily, fontWeight, fontSize, layerName);
    }-*/;

  public final native void cmd(String cmd, double x, double y,
      String label, String fontFamily, String fontWeight, String fontSize,
      String layerName, String cursorStyle) /*-{
           this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.push(cmd, 8,
                   x, y, label, fontFamily, fontWeight, fontSize, layerName, cursorStyle);
    }-*/;


  public DisplayList createDisplayList(String id) {
    return rootLayer.createDisplayList(id);
  }

  public Layer createLayer(String layerId, Bounds b) {
    Layer layer = getLayer(layerId);
    if (layer == null) {
      createFlashLayer(canvasId, layerId, b.x, b.y, b.width, b.height);
      layer = new FlashLayer(this, layerId, b);
      layers.add(layer);
      id2Layer.put(layer.getLayerId(), layer);
      //    DOM.appendChild(canvasElement, ( (FlashLayer) layer ).getLayerElement());

      layer.save();
      layer.setFillColor(Color.TRANSPARENT);
      layer.clearRect(0, 0, layer.getWidth(), layer.getHeight());
      layer.restore();
    }
    return layer;
  }

  public LinearGradient createLinearGradient(double x, double y, double w, double h) {
    return rootLayer.createLinearGradient(x, y, w, h);
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
      flashDisposeLayer(canvasId, layerId);
      // DOM.removeChild(canvasElement, ( (FlashLayer) layer ).getLayerElement());
    }
    id2Layer.remove(layerId);
  }

  public void drawImage(Layer layer, double x, double y, double width, double height) {
    rootLayer.drawImage(layer, x, y, width, height);
  }

  public void drawImage(Layer layer, double sx, double sy, double swidth,
      double sheight, double dx, double dy, double dwidth, double dheight) {
    rootLayer.drawImage(layer, sx, sy, swidth, sheight, dx, dy, dwidth, dheight);
  }

  public void drawRotatedText(double x, double y, double angle, String label,
      String fontFamily, String fontWeight, String fontSize, String layerName,
      Chart chart) {
    rootLayer.drawRotatedText(x, y, angle, label, fontFamily, fontWeight, fontSize,
            layerName, chart);
  }

  public void drawText(double x, double y, String label, String fontFamily,
      String fontWeight, String fontSize, String layerName) {
    rootLayer.drawText(x, y, label, fontFamily, fontWeight, fontSize, layerName, Cursor.DEFAULT);
  }

  public void endFrame() {
    super.endFrame();
    drawFlashCanvas(canvasId, getFlashDisplayList());
  }

  public void fill() {
    rootLayer.fill();
  }

  public void fillRect(double x, double y, double w, double h) {
    rootLayer.fillRect(Math.floor(x), Math.floor(y), Math.ceil(w), Math.floor(h));
  }

  public void fillRect() {
    rootLayer.fillRect();
  }

  public native void flashCanvasSetupDone(String canvasId) /*-{
      var flashCanvas = $wnd.navigator.appName.indexOf("Microsoft") != -1 ? $wnd[canvasId] : $doc[canvasId];
      flashCanvas && flashCanvas.viewInitialized && 
        flashCanvas.viewInitialized();
  }-*/;

  public Bounds getBounds() {
    return rootLayer.getBounds();
  }

  public Element getElement() {
    return canvasElement;
  }

  public native String getFlashDisplayList() /*-{
           return this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.join(@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::CMDSEP);
    }-*/;

  public double getHeight() {
    return rootLayer.getHeight();
  }

  public Layer getLayer(String layerId) {
    return (Layer) id2Layer.get(layerId);
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

  public final void popSelection() {
    selectedLayerId = (String) selectedLayers.remove(selectedLayers.size() - 1);
    cmd("L", selectedLayerId);
  }

  public final native void push(String cmd) /*-{
          this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.push(cmd);
      }-*/;

  public final native void push(float f) /*-{
          this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.push(f);

      }-*/;

  public final native void push(double f) /*-{
          this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.push(f);
      }-*/;

  public final void pushNCmd(String cmd, int n) {
    push(cmd);
    push(n);
  }

  public final void pushSelection(String id) {
    selectedLayers.add(selectedLayerId);
    selectedLayerId = id;
    cmd("L", id);
  }

  public void rect(double x, double y, double width, double height) {
    rootLayer.rect(x, y, width, height);
  }

  public void restore() {
    rootLayer.restore();
  }

    // TODO - switch the angles to type Radians or Degrees, too much ambiguity
  public int rotatedStringHeight(String str, double rotationAngle,
      String fontFamily, String fontWeight, String fontSize) {
    return rootLayer
        .rotatedStringHeight(str, rotationAngle, fontFamily, fontWeight, fontSize);
  }

  public int rotatedStringWidth(String str, double rotationAngle,
      String fontFamily, String fontWeight, String fontSize) {
    return rootLayer
        .rotatedStringWidth(str, rotationAngle, fontFamily, fontWeight, fontSize);
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

  public void setTransparency(float value) {
    rootLayer.setTransparency(value);
  }

  public void setVisibility(boolean visibility) {
    DOM.setStyleAttribute(canvasElement, "visibility",
        visibility ? "visible" : "hidden");
    rootLayer.setVisibility(visibility);
  }

  public native int stringHeight(String canvasId, String string, String font,
      String bold, String size, float angle) /*-{
        var flashCanvas = $wnd.navigator.appName.indexOf("Microsoft") != -1 ? $wnd[canvasId] : $doc[canvasId];
        if(flashCanvas && flashCanvas.stringHeight) {
          return flashCanvas.stringHeight(string, font, bold, size, angle);
        } else {
          return 10;
        }
    }-*/;

  public int stringHeight(String string, String font, String bold, String size,
      float angle) {
    return stringHeight(canvasId, string, font, bold, size, angle);
  }

  public native int stringWidth(String canvasId, String string, String font,
      String bold, String size, float angle) /*-{
        var flashCanvas = $wnd.navigator.appName.indexOf("Microsoft") != -1 ? $wnd[canvasId] : $doc[canvasId];
        if(flashCanvas && flashCanvas.stringWidth) {
          return flashCanvas.stringWidth(string, font, bold, size, angle);
        } else {
          return 8 * string.length;
        }
    }-*/;

  public int stringWidth(String string, String font, String bold, String size,
      float angle) {
    return stringWidth(canvasId, string, font, bold, size, angle);
  }

  public void stroke() {
    rootLayer.stroke();
  }

  public void translate(double x, double y) {
    rootLayer.translate(x, y);
  }

  void clearFlashDisplayList() {
    ctx = makectx();
  }

  void init(int width, int height) {
    canvasElement = DOM.createDiv();
    readyFn = "canvasReadyFn" + this.canvasId;
    clearFlashDisplayList();
  }

  private native void exportReadyFn(String readyFn, View view,
      CanvasReadyCallback canvasReadyCallback) /*-{
        var _this=this;
        $wnd[readyFn] = function() {
            _this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::flashCanvasReady(Lorg/timepedia/chronoscope/client/canvas/View;Lorg/timepedia/chronoscope/client/canvas/CanvasReadyCallback;)(view, canvasReadyCallback);
        }
    }-*/;

  private void flashCanvasReady(final View view, final CanvasReadyCallback canvasReadyCallback) {
    Timer t = new Timer() {
      public void run() {
        rootLayer = (FlashLayer) createLayer("rootLayer",
            new Bounds(0, 0, width, height));
        FlashCanvas.super.attach(view, canvasReadyCallback);
      }
    };
    t.schedule(1000);
  }

  private native JavaScriptObject makectx() /*-{
          return new Array();
      }-*/;

  private void resyncLayers() {
    for (Iterator iterator = layers.iterator(); iterator.hasNext();) {
      FlashLayer layer = (FlashLayer) iterator.next();
      Bounds bounds = layer.getBounds();
      createFlashLayer(canvasId, layer.getLayerId(), bounds.x, bounds.y,
          bounds.width, bounds.height);
    }
    getView().getChart().reloadStyles();
  }
}