package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import org.timepedia.chronoscope.client.canvas.*;
import org.timepedia.chronoscope.client.render.LinearGradient;

/**
 * An implementation of the Layer interface using the Safari/WHATWG Javascript CANVAS
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class BrowserLayer extends DomTextLayer {

    private Element canvas;
    private JavaScriptObject ctx;
    private static final String[] compositeModes = { "copy", "source-atop", "source-in", "source-out", "source-over",
            "destination-atop", "destination-in", "destination-out", "destination-over", "darker", "lighter", "xor" };
    private String strokeColor;
    private String fillColor;
    private Bounds bounds;
    private final String layerId;
    private Element layerContainer;
    private int zIndex = 0;
    private int zorder;
    private int scrollLeft;
    private static int layerCount = 0;

    public BrowserLayer(Canvas canvas, String layerId, Bounds b) {
        super(canvas);
        this.layerId = layerId;
        init(b);

    }


    void init(Bounds b) {
        layerContainer = DOM.createElement("div");
        this.bounds = new Bounds(b);
        canvas = DOM.createElement("canvas");
        String lc = String.valueOf(layerCount++);
        DOM.setElementAttribute(layerContainer, "id", "_lc_" + layerId + lc);
        DOM.setElementAttribute(canvas, "id", "_cv_" + layerId + lc);


        DOM.setElementAttribute(canvas, "width", "" + b.width);
        DOM.setElementAttribute(canvas, "height", "" + b.height);

        DOM.setStyleAttribute(layerContainer, "width", "" + b.width + "px");
        DOM.setStyleAttribute(layerContainer, "height", "" + b.height + "px");
        DOM.setStyleAttribute(canvas, "width", "" + b.width + "px");
        DOM.setStyleAttribute(canvas, "height", "" + b.height + "px");
        DOM.setStyleAttribute(canvas, "position", "absolute");
        DOM.setStyleAttribute(layerContainer, "visibility", "visible");
        DOM.setStyleAttribute(layerContainer, "position", "absolute");

        DOM.setStyleAttribute(layerContainer, "overflow", "hidden");
        DOM.setStyleAttribute(layerContainer, "top", b.y + "px");
        DOM.setStyleAttribute(layerContainer, "left", b.x + "px");
        DOM.setStyleAttribute(layerContainer, "overflow", "visible");

        ctx = getCanvasContext(canvas);
        DOM.appendChild(layerContainer, canvas);
    }


    public void setVisibility(boolean visibility) {
        DOM.setStyleAttribute(layerContainer, "visibility", visibility ? "visible" : "hidden");
    }

    public void closePath() {
        closePath0(ctx);
    }

    public void restore() {
        super.restore();

        restore0(ctx);
    }

    private native void restore0(JavaScriptObject ctx)/*-{
         ctx.restore();
    }-*/;

    public void save() {
        super.save();

        save0(ctx);
    }

    private native void save0(JavaScriptObject ctx) /*-{
        ctx.save();
    }-*/;

    public void fill() {
        fill0(ctx);
    }

    public void setTransparency(float value) {
        setTransparency0(ctx, value);
    }

    public void setComposite(int mode) {
        setComposite0(ctx, compositeModes[mode]);
    }

    public void translate(double x, double y) {
        translate0(ctx, x, y);
    }

    /*public void arc(double x, double y, double radius, double startAngle, double endAngle, int clockwise) {

        arc0(ctx, x, y, radius, startAngle, endAngle, clockwise);
    }*/

    public void clearRect(double x, double y, double width, double height) {
        if (width != 0 && height != 0) {
            clearRect0(ctx, x, y, width, height);
        }
    }

    public void rect(double x, double y, double width, double height) {
        rect0(ctx, x, y, width, height);
    }

    public void setShadowBlur(double width) {

        setShadowBlur0(ctx, width);
    }

    private native void setShadowBlur0(JavaScriptObject ctx, double width) /*-{
        ctx.shadowBlur=width;
    }-*/;

    public void setShadowColor(String color) {

        setShadowColor0(ctx, color);
    }

    private native void setShadowColor0(JavaScriptObject ctx, String color) /*-{

        ctx.shadowColor=color;
    }-*/;

    public void setShadowOffsetX(double x) {
        setShadowOffsetX0(ctx, x);
    }

    private native void setShadowOffsetX0(JavaScriptObject ctx, double x) /*-{
        ctx.shadowOffsetX=x;
    }-*/;

    public void setShadowOffsetY(double y) {
        setShadowOffsetY0(ctx, y);
    }

    private native void setShadowOffsetY0(JavaScriptObject ctx, double y) /*-{
        ctx.shadowOffsetY=y;
    }-*/;

    private native void clearRect0(JavaScriptObject ctx, double x, double y, double width, double height) /*-{
        ctx.clearRect(x,y,width,height);
    }-*/;


    public native void arc(double x, double y, double radius, double startAngle, double endAngle, int clockwise) /*-{
        this.@org.timepedia.chronoscope.client.browser.BrowserLayer::ctx.arc(x,y,radius,startAngle, endAngle, clockwise);
    }-*/;

    private native void translate0(JavaScriptObject ctx, double x, double y) /*-{
        ctx.translate(x,y);
    }-*/;

    private native void setComposite0(JavaScriptObject ctx, String compositeMode) /*-{
        ctx.globalCompositeOperation=compositeMode;
    }-*/;

    private native void setTransparency0(JavaScriptObject ctx, float value) /*-{
        ctx.globalAlpha=value;
    }-*/;


    private native void fill0(JavaScriptObject ctx) /*-{
        ctx.fill();
    }-*/;

    private native void closePath0(JavaScriptObject ctx) /*-{
        ctx.closePath();
    }-*/;

    public LinearGradient createLinearGradient(double x, double y, double w, double h) {

        return new BrowserLinearGradient(this, x, y, w, h);
    }

    public double getWidth() {
        return bounds.width;
    }

    public double getHeight() {
        return bounds.height;
    }

    public void setFillColor(String color) {
        if ("transparent".equals(color)) {
            color = "rgba(0,0,0,0)";
        }

        try {
            fillColor = color;
            setFillColor0(ctx, color);
        } catch (Throwable t) {
            if (Chronoscope.isErrorReportingEnabled()) {
                Window.alert("Error is " + t + " for color " + color);
            }
        }
    }

    public void setLinearGradient(LinearGradient lingrad) {
        try {
            setGradient0(ctx, ( (BrowserLinearGradient) lingrad ).getNative());
        } catch (Throwable t) {
            if (Chronoscope.isErrorReportingEnabled()) {
                Window.alert("setLinearGradient: " + t);
            }
        }
    }


    public void setStrokeColor(String color) {
        if ("transparent".equals(color)) {
            color = "rgba(0,0,0,0)";
        }

        try {
            strokeColor = color;
            setStrokeColor0(ctx, color);
        } catch (Throwable t) {
            if (Chronoscope.isErrorReportingEnabled()) {
                Window.alert("Error is " + t + " for strokecolor " + color);
            }
        }

    }


    public native void stroke() /*-{
        this.@org.timepedia.chronoscope.client.browser.BrowserLayer::ctx.stroke();
    }-*/;

    public native void lineTo(double x, double y) /*-{
        this.@org.timepedia.chronoscope.client.browser.BrowserLayer::ctx.lineTo(x, y);
    }-*/;

    public native void moveTo(double x, double y) /*-{
        this.@org.timepedia.chronoscope.client.browser.BrowserLayer::ctx.moveTo(x, y);
    }-*/;

    public native void setLineWidth(double width) /*-{
        this.@org.timepedia.chronoscope.client.browser.BrowserLayer::ctx.lineWidth=width;
    }-*/;

    private native void setStrokeColor0(JavaScriptObject ctx, String color) /*-{
       ctx.strokeStyle=color;
    }-*/;

    public native void beginPath() /*-{
        this.@org.timepedia.chronoscope.client.browser.BrowserLayer::ctx.beginPath();
    }-*/;


    private native void setGradient0(JavaScriptObject ctx, JavaScriptObject lingrad) /*-{
        ctx.fillStyle = lingrad;
    }-*/;

    private native void setFillColor0(JavaScriptObject ctx, String color) /*-{
        ctx.fillStyle = color;
    }-*/;

    public native void scale(double sx, double sy) /*-{
        this.@org.timepedia.chronoscope.client.browser.BrowserLayer::ctx.scale(sx, sy);
    }-*/;

    private native JavaScriptObject getCanvasContext(Element elem) /*-{
        return elem.getContext("2d");
    }-*/;


    public native void fillRect(double x, double y, double w, double h) /*-{
       this.@org.timepedia.chronoscope.client.browser.BrowserLayer::ctx.fillRect(x, y, w, h);
    }-*/;

    JavaScriptObject getContext() {

        return ctx;
    }

    public Element getLayerElement() {
        return layerContainer;
    }

    public void clip(double x, double y, double width, double height) {
        super.clip(x, y, width, height);
        beginPath();
        rect(x, y, width, height);
        clip0(ctx);

    }

    public void drawImage(Layer layer, double x, double y, double width, double height) {
        if (layer instanceof BrowserCanvas) {
            drawImage0(ctx, ( (BrowserCanvas) layer )
                    .getRootLayer().getElement(), x, y, width, height);
        } else {
            drawImage0(ctx, ( (BrowserLayer) layer ).getElement(), x, y, width, height);
        }
    }


    public String getTransparency() {
        return getTransparency0(ctx);
    }

    public void drawImage(Layer layer, double sx, double sy, double swidth, double sheight, double dx, double dy,
                          double dwidth, double dheight) {


        if (layer instanceof BrowserCanvas) {
            drawImageSrcDest0(ctx, ( (BrowserCanvas) layer )
                    .getRootLayer().getElement(), sx, sy, swidth, sheight, dx, dy, dwidth, dheight);
        } else {
            drawImageSrcDest0(ctx, ( (BrowserLayer) layer ).getElement(), sx, sy, swidth, sheight, dx, dy, dwidth,
                              dheight);
        }
    }


    public String getLayerId() {
        return layerId;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setLayerOrder(int zorder) {

        DOM.setIntStyleAttribute(canvas, "zIndex", zorder);
        DOM.setIntStyleAttribute(layerContainer, "zIndex", zorder);
        this.zorder = zorder;
    }

    public int getScrollLeft() {
        return scrollLeft;
    }

    public void setScrollLeft(int i) {
        scrollLeft = i;
        DOM.setStyleAttribute(canvas, "left", i + "px");
    }

    public void setLayerAlpha(float alpha) {
        DOM.setStyleAttribute(canvas, "opacity", "" + alpha);
    }

    public int getLayerOrder() {
        return zorder;
    }

    public boolean isVisible() {
        return DOM.getStyleAttribute(layerContainer, "visibility").equals("visible");
    }

    public float getLayerAlpha() {
        return DOM.getIntStyleAttribute(canvas, "opacity");
    }

    private native void drawImageSrcDest0(JavaScriptObject ctx, Element canvas, double sx, double sy, double swidth,
                                          double sheight, double dx, double dy, double dwidth, double dheight) /*-{
         ctx.drawImage(canvas, sx, sy, swidth, sheight, dx, dy, dwidth, dheight);

    }-*/;

    private native String getTransparency0(JavaScriptObject ctx) /*-{
        return ""+ctx.globalAlpha;
    }-*/;

    private String getFillColor() {
        return fillColor;
    }

    public String getStrokeColor() {
        return strokeColor;
    }

    public PaintStyle createPattern(String imageUri) {
        return new BrowserCanvasPattern(this, imageUri);
    }

    public void setCanvasPattern(CanvasPattern canvasPattern) {
        setCanvasPattern0(ctx, ( (BrowserCanvasPattern) canvasPattern ).getNative());
    }

    private native void setCanvasPattern0(JavaScriptObject ctx, JavaScriptObject pattern) /*-{
            if(pattern != null) {
               ctx.fillStyle = pattern;
                }
    }-*/;

    public void setRadialGradient(RadialGradient radialGradient) {
        setGradient0(ctx, ( (BrowserRadialGradient) radialGradient ).getNative());
    }

    public RadialGradient createRadialGradient(double x0, double y0, double r0, double x1, double y1, double r1) {
        return new BrowserRadialGradient(this, x0, y0, r0, x1, y1, r1);
    }


    private native void drawImage0(JavaScriptObject ctx, JavaScriptObject image, double x, double y, double w, double h) /*-{
          ctx.drawImage(image, x, y, w, h);
    }-*/;

    private native void rect0(JavaScriptObject ctx, double x, double y, double width, double height) /*-{
        ctx.rect(x,y,width,height);
    }-*/;

    private native void clip0(JavaScriptObject ctx) /*-{
        ctx.clip();
    }-*/;

    public Element getElement() {
        return canvas;
    }
}
