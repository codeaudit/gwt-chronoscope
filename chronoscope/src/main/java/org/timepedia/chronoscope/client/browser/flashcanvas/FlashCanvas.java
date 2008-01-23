package org.timepedia.chronoscope.client.browser.flashcanvas;

import com.google.gwt.core.client.JavaScriptObject;
import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.canvas.*;
import org.timepedia.chronoscope.client.render.LinearGradient;

import java.util.*;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Timer;


/**
 * An implementation of Canvas that creates a CANVAS tag per Layer, as well as using DIVs and images to render
 * text on the CANVAS
 */
public class FlashCanvas extends Canvas {


    private FlashLayer rootLayer;
    private final HashMap id2Layer = new HashMap();
    private final int width;
    private final int height;
    private Element canvasElement;
    private String canvasId;
    private static int canvasNumber = 0;
    private String readyFn = "";

    public FlashCanvas(View view, int width, int height) {
        super(view);
        this.width = width;
        this.height = height;
        canvasId = "flashCanvas" + canvasNumber++;
        init(width, height);
    }

    public Layer createLayer(String layerId, Bounds b) {
        Layer layer = getLayer(layerId);
        if (layer == null) {
            createFlashLayer(canvasId, layerId, b.x, b.y, b.width, b.height);
            layer = new FlashLayer(this, layerId, b);
            id2Layer.put(layer.getLayerId(), layer);
        //    DOM.appendChild(canvasElement, ( (FlashLayer) layer ).getLayerElement());

            layer.setFillColor("transparent");
            layer.clearRect(0, 0, layer.getWidth(), layer.getHeight());
        }
        return layer;
    }

    private static native void createFlashLayer(String canvasId, String layerId, double x, double y, double width, double height) /*-{
        var flashCanvas = $wnd.navigator.appName.indexOf("Microsoft") != -1 ? $wnd[canvasId] : $doc[canvasId];
        flashCanvas.createCanvas(layerId, x, y, width, height);
    }-*/;

    private static native void drawFlashCanvas(String canvasId, String cmds) /*-{
        var flashCanvas = $wnd.navigator.appName.indexOf("Microsoft") != -1 ? $wnd[canvasId] : $doc[canvasId];
        flashCanvas.drawframe(cmds);
    }-*/;

    public Layer getLayer(String layerId) {
        return (Layer) id2Layer.get(layerId);
    }

    ArrayList selectedLayers=new ArrayList();
    String selectedLayerId = "";
    public final void pushSelection(String id) {
       selectedLayers.add(selectedLayerId);
       selectedLayerId = id;
        cmd("L", id);
    }

    public final void popSelection() {
        selectedLayerId = (String) selectedLayers.remove(selectedLayers.size()-1);
        cmd("L", selectedLayerId);
    }

    public void attach(View view, CanvasReadyCallback canvasReadyCallback) {
        FlashView bv = (FlashView) view;
        exportReadyFn(readyFn, view, canvasReadyCallback);
        DOM.appendChild(bv.getElement(), canvasElement);
          DOM.setElementAttribute(canvasElement, "width", "" + width);
        DOM.setElementAttribute(canvasElement, "height", "" + height);
        DOM.setStyleAttribute(canvasElement, "width", "" + width + "px");
        DOM.setStyleAttribute(canvasElement, "height", "" + height + "px");
        DOM.setStyleAttribute(canvasElement, "visibility", "visible");
        DOM.setStyleAttribute(canvasElement, "position", "absolute");
        DOM.setStyleAttribute(canvasElement, "top", "0px");
        DOM.setStyleAttribute(canvasElement, "left", "0px");
        DOM.setInnerHTML(canvasElement, "<object style=\"position:absolute;top: 0px;left:0px\" classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" \n" +
                "codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,40,0\" \n" +
                " width=\"" + width + "\" height=\"" + height + "\" \n" +
                "id=\"" + canvasId + "\"> \n" +
                "<param name=\"movie\" value=\"Main.swf\"> \n" +
                "<param name=\"FlashVars\" value=\"readyFn=" + readyFn + "\">" +
                "<param name=\"quality\" value=\"high\"> \n" +
                "<param name=\"bgcolor\" value=\"#FFFFFF\"> \n" +
                "<param name=\"wmode\" value=\"transparent\">\n" +
                "<param name=\"allowScriptAccess\" value=\"sameDomain\" />"+
                "<embed style=\"position:absolute;top:0px;left:0px\" src=\"Main.swf\" quality=\"high\" bgcolor=\"#FFFFFF\"\n" +
                "width=\"" + width + "\" height=\"" + height + "\" \n" +
                "name=\"" + canvasId + "\" align=\"\" type=\"application/x-shockwave-flash\" \n" +
                "FlashVars=\"readyFn=" + readyFn + "\" " +
                "wmode=\"transparent\" "+
                "allowScriptAccess=\"sameDomain\" "+
                "pluginspage=\"http://www.macromedia.com/go/getflashplayer\"> \n" +
                "</embed> \n" +
                "</object>");
        
    }

    private JavaScriptObject ctx=null;
    void clearFlashDisplayList() {
          ctx = makectx();
    }

      private native JavaScriptObject makectx() /*-{
          return new Array();
      }-*/;

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

      public final native void cmd(String cmd, double arg1, double arg2, double arg3, double arg4) /*-{
             this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.push(cmd, 4, arg1, arg2,
                     arg3, arg4);
         }-*/;
      public final native void cmd(String cmd, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6) /*-{
                this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.push(cmd, 6, arg1, arg2,
                     arg3, arg4, arg5, arg6);
      }-*/;

      public final native void cmd(String cmd) /*-{
                this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.push(cmd, 0);
      }-*/;


    public void endFrame() {
        super.endFrame();
        drawFlashCanvas(canvasId, getFlashDisplayList());

    }

    public static final String CMDSEP = "%";
    
    public native String getFlashDisplayList() /*-{
           return this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.join(@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::CMDSEP);
    }-*/;

    private void flashCanvasReady(final View view, final CanvasReadyCallback canvasReadyCallback) {
        Timer t=new Timer() {
            public void run() {
                rootLayer = (FlashLayer) createLayer("rootLayer", new Bounds(0, 0, width, height));
                FlashCanvas.super.attach(view, canvasReadyCallback);
            }
        };
        t.schedule(100);

    }

    public void disposeLayer(String layerId) {
        Layer layer = getLayer(layerId);
        if (layer != null) {
          if (layer != null) {
            flashDisposeLayer(canvasId, layerId);
   //         DOM.removeChild(canvasElement, ( (FlashLayer) layer ).getLayerElement());
          }
        }
        id2Layer.remove(layerId);

    }

   private static native void flashDisposeLayer(String canvasId, String layerId) /*-{
        var flashCanvas = $wnd.navigator.appName.indexOf("Microsoft") != -1 ? $wnd[canvasId] : $doc[canvasId];
        flashCanvas.disposeCanvas(layerId);
    }-*/;

    public native int stringWidth(String canvasId, String string, String font, String bold, String size,
                                  float angle) /*-{
        var flashCanvas = $wnd.navigator.appName.indexOf("Microsoft") != -1 ? $wnd[canvasId] : $doc[canvasId];
        return flashCanvas.stringWidth(string, font, bold, size, angle);
    }-*/;

    public native int stringHeight(String canvasId, String string, String font, String bold, String size,
                                   float angle) /*-{
        var flashCanvas = $wnd.navigator.appName.indexOf("Microsoft") != -1 ? $wnd[canvasId] : $doc[canvasId];
        return flashCanvas.stringHeight(string, font, bold, size, angle);
    }-*/;

    void init(int width, int height) {
        canvasElement = DOM.createDiv();
        readyFn = "canvasReadyFn" + this.canvasId;
        clearFlashDisplayList();

    }

    private native void exportReadyFn(String readyFn, View view, CanvasReadyCallback canvasReadyCallback) /*-{
        var _this=this;
        $wnd[readyFn] = function() {
            _this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::flashCanvasReady(Lorg/timepedia/chronoscope/client/canvas/View;Lorg/timepedia/chronoscope/client/canvas/CanvasReadyCallback;)(view, canvasReadyCallback);
        }
    }-*/;

    public void setVisibility(boolean visibility) {
        DOM.setStyleAttribute(canvasElement, "visibility", visibility ? "visible" : "hidden");
        rootLayer.setVisibility(visibility);
    }

    public void closePath() {
        rootLayer.closePath();
    }

    public void restore() {
        rootLayer.restore();
    }

    public void save() {
        rootLayer.save();
    }

    public void fill() {
        rootLayer.fill();
    }

    public void setTransparency(float value) {
        rootLayer.setTransparency(value);
    }

    public void setComposite(int mode) {
        rootLayer.setComposite(mode);
    }

    public void translate(double x, double y) {
        rootLayer.translate(x, y);
    }

    public void arc(double x, double y, double radius, double startAngle, double endAngle, int clockwise) {
        rootLayer.arc(x, y, radius, startAngle, endAngle, clockwise);
    }

    public void clearRect(double x, double y, double width, double height) {
        rootLayer.clearRect(x, y, width, height);
    }

    public void rect(double x, double y, double width, double height) {
        rootLayer.rect(x, y, width, height);
    }

    public void setShadowBlur(double width) {
        rootLayer.setShadowBlur(width);
    }

    public void setShadowColor(String color) {
        rootLayer.setShadowColor(color);
    }

    public void setShadowOffsetX(double x) {
        rootLayer.setShadowOffsetX(x);
    }

    public void setShadowOffsetY(double y) {
        rootLayer.setShadowOffsetY(y);
    }

    public LinearGradient createLinearGradient(double x, double y, double w, double h) {
        return rootLayer.createLinearGradient(x, y, w, h);
    }

    public double getWidth() {
        return rootLayer.getWidth();
    }

    public double getHeight() {
        return rootLayer.getHeight();
    }

    public void setFillColor(String color) {
        rootLayer.setFillColor(color);
    }

    public void setLinearGradient(LinearGradient lingrad) {
        rootLayer.setLinearGradient(lingrad);
    }

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

    public void setStrokeColor(String color) {
        rootLayer.setStrokeColor(color);
    }

    public void setLineWidth(double width) {
        rootLayer.setLineWidth(width);
    }

    public void moveTo(double x, double y) {
        rootLayer.moveTo(x, y);
    }

    public void lineTo(double x, double y) {
        rootLayer.lineTo(x, y);
    }

    public void stroke() {
        rootLayer.stroke();
    }

    public void fillRect(double x, double y, double w, double h) {
        rootLayer.fillRect(x, y, w, h);
    }


    public Element getElement() {
        return canvasElement;
    }

    public void clip(double x, double y, double width, double height) {
        rootLayer.clip(x, y, width, height);
    }

    public void drawImage(Layer layer, double x, double y, double width, double height) {
        rootLayer.drawImage(layer, x, y, width, height);
    }

    public void scale(double sx, double sy) {
        rootLayer.scale(sx, sy);
    }

    public String getTransparency() {
        return rootLayer.getTransparency();
    }

    public void drawImage(Layer layer, double sx, double sy, double swidth, double sheight, double dx, double dy,
                          double dwidth, double dheight) {
        rootLayer.drawImage(layer, sx, sy, swidth, sheight, dx, dy, dwidth, dheight);
    }

    public String getLayerId() {
        return rootLayer.getLayerId();
    }

    public Bounds getBounds() {
        return rootLayer.getBounds();
    }

    public void setLayerOrder(int zorder) {
        rootLayer.setLayerOrder(zorder);
    }

    public int getScrollLeft() {
        return rootLayer.getScrollLeft();
    }

    public void setScrollLeft(int i) {
        rootLayer.setScrollLeft(i);
    }

    public void setLayerAlpha(float alpha) {
        rootLayer.setLayerAlpha(alpha);
    }

    public int getLayerOrder() {
        return rootLayer.getLayerOrder();
    }

    public boolean isVisible() {
        return rootLayer.isVisible();
    }

    public float getLayerAlpha() {
        return rootLayer.getLayerAlpha();
    }

    public DisplayList createDisplayList(String id) {
        return rootLayer.createDisplayList(id);
    }

    public String getStrokeColor() {
        return rootLayer.getStrokeColor();
    }

    public PaintStyle createPattern(String imageUri) {
        return rootLayer.createPattern(imageUri);
    }

    public void setCanvasPattern(CanvasPattern canvasPattern) {
        rootLayer.setCanvasPattern(canvasPattern);
    }

    public void setRadialGradient(RadialGradient radialGradient) {
        rootLayer.setRadialGradient(radialGradient);
    }

    public RadialGradient createRadialGradient(double x0, double y0, double r0, double x1, double y1, double r1) {
        return rootLayer.createRadialGradient(x0, y0, r0, x1, y1, r1);
    }

    public int stringWidth(String string, String font, String bold, String size, float angle) {
        return stringWidth(canvasId, string, font, bold, size, angle);
    }

    public int stringHeight(String string, String font, String bold, String size, float angle) {
        return stringHeight(canvasId, string, font, bold, size, angle);
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

    public void drawText(double x, double y, String label, String fontFamily, String fontWeight, String fontSize,
                         String layerName) {
        rootLayer.drawText(x, y, label, fontFamily, fontWeight, fontSize, layerName);
    }

    public void setTextLayerBounds(String layerName, Bounds bounds) {
        rootLayer.setTextLayerBounds(layerName, bounds);
    }

    public void drawRotatedText(double x, double y, double angle, String label, String fontFamily, String fontWeight,
                                String fontSize, String layerName, Chart chart) {
        rootLayer.drawRotatedText(x, y, angle, label, fontFamily, fontWeight, fontSize, layerName, chart);
    }

    public void fillRect() {
        rootLayer.fillRect();
    }


    public void setFillColor(PaintStyle p) {
        rootLayer.setFillColor(p);
    }

    public void setShadowColor(Color shadowColor) {
        rootLayer.setShadowColor(shadowColor);
    }

    public void setStrokeColor(PaintStyle p) {
        rootLayer.setStrokeColor(p);
    }


    public int rotatedStringWidth(String str, double rotationAngle, String fontFamily, String fontWeight,
                                  String fontSize) {
        return rootLayer.rotatedStringWidth(str, rotationAngle, fontFamily, fontWeight, fontSize);
    }

    public int rotatedStringHeight(String str, double rotationAngle, String fontFamily, String fontWeight,
                                   String fontSize) {
        return rootLayer.rotatedStringHeight(str, rotationAngle, fontFamily, fontWeight, fontSize);
    }


    public Layer getRootLayer() {
        return rootLayer;
    }

    public final native void cmd(String cmd, double x, double y, String label, String fontFamily, String fontWeight, String fontSize, String layerName) /*-{
           this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.push(cmd, 7, x, y, label, fontFamily,
                   fontWeight, fontSize, layerName);
    }-*/;

     public native void cmd(String cmd, String layerName, double x, double y, double width, double height) /*-{
           this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.push(cmd, 5, layerName, x, y,
                   width, height);
    }-*/;

      public final native void cmd(String cmd, double x, double y, double a, String label, String fontFamily, String fontWeight, String fontSize, String layerName) /*-{
           this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashCanvas::ctx.push(cmd, 8, x, y, a, label, fontFamily,
                   fontWeight, fontSize, layerName);
    }-*/;
}