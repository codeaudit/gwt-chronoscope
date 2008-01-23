package org.timepedia.chronoscope.client.browser.flashcanvas;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import org.timepedia.chronoscope.client.canvas.*;
import org.timepedia.chronoscope.client.render.LinearGradient;
import org.timepedia.chronoscope.client.browser.*;
import org.timepedia.chronoscope.client.Chart;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * An implementation of the Layer interface using the Safari/WHATWG Javascript CANVAS
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class FlashLayer extends AbstractLayer {

    private static final String[] compositeModes = {"copy", "source-atop", "source-in", "source-out", "source-over",
            "destination-atop", "destination-in", "destination-out", "destination-over", "darker", "lighter", "xor"};
    private String strokeColor;
    private String fillColor;
    private Bounds bounds;
    private final String layerId;
    private int zIndex = 0;
    private int zorder;
    private int scrollLeft;
    private static int layerCount = 0;
    private boolean visibllity = true;
   // private String ctx;
    JavaScriptObject ctx;
    private Element layerContainer;
    private float layerAlpha;
    private FlashCanvas fc;

    public FlashLayer(Canvas canvas, String layerId, Bounds b) {
        super(canvas);
        this.fc=(FlashCanvas)canvas;
        this.layerId = layerId;
        init(b);

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



    public void setVisibility(boolean visibility) {
        this.visibllity = visibility;
        fc.cmd("SV", visibility ? 1 : 0);
    }

    public void closePath() {
        cmd("c");
    }

    private FlashCanvas getFlashCanvas() {
        return fc;
    }

    private void selectLayer() {

    }
    
    private void cmd(String cmd) {
        selectLayer();
        fc.cmd(cmd);
    }

    private void cmd(String cmd, float value) {
        selectLayer();
        fc.cmd(cmd, value);
    }
     private void cmd(String cmd, double  value) {
        selectLayer();
        fc.cmd(cmd, value);
    }
     private void cmd(String cmd, double arg1, double arg2) {
        selectLayer();
        fc.cmd(cmd, arg1, arg2);
    }

    private void cmd(String cmd, double arg1, double arg2,double arg3, double arg4) {
        selectLayer();
        fc.cmd(cmd, arg1, arg2, arg3, arg4);
    }

      private void cmd(String cmd, double arg1, double arg2,double arg3, double arg4, double arg5, double arg6) {
        selectLayer();
        fc.cmd(cmd, arg1, arg2, arg3, arg4, arg5, arg6);
    }

     private void cmd(String cmd, String value) {
        selectLayer();
        fc.cmd(cmd, value);
    }

     private void pushNCmd(String cmd, int i) {
        selectLayer();
        fc.pushNCmd(cmd, i);
    }

    private void push(String s) {
        fc.push(s);
    }

     private void push(double s) {
        fc.push(s);
    }

    public void clear() {
        fc.cmd("CL", layerId);
    }
    
    public void restore() {
        super.restore();
        cmd("Z");
        fc.popSelection();

    }


    public void save() {
        super.save();
        fc.pushSelection(layerId);
        cmd("X");
    }


    public void fill() {
        cmd("f");
    }

    public void setTransparency(float value) {
        cmd("T", value);
    }

    public void setComposite(int mode) {
        cmd("CO", compositeModes[mode]);
    }

    public void translate(double x, double y) {
        cmd("t", x, y);
    }


    public void clearRect(double x, double y, double width, double height) {
        if (width != 0 && height != 0) {
             cmd("CR", x, y, width, height);
        }
    }

    public void rect(double x, double y, double width, double height) {
        cmd("r", x, y, width, height);
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


    public void arc(double x, double y, double radius, double startAngle, double endAngle, int clockwise) {
        cmd("a", x, y, radius, startAngle, endAngle, clockwise);
    }


    public LinearGradient createLinearGradient(double x, double y, double w, double h) {

        return new FlashLinearGradient(this, x, y, w, h);
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
            cmd("F", color);
        } catch (Throwable t) {
            if (Chronoscope.isErrorReportingEnabled()) {
                Window.alert("Error is " + t + " for color " + color);
            }
        }
    }

    public void setLinearGradient(LinearGradient lingrad) {
        try {
            FlashLinearGradient flg = (FlashLinearGradient) lingrad;
            ArrayList stops = flg.getStops();
            pushNCmd("LG", 4+stops.size()*2);
            push(flg.getX());
            push(flg.getY());
            push(flg.getX2());
            push(flg.getY2());
            Iterator it = stops.iterator();
            while (it.hasNext()) {
                FlashLinearGradient.FlashColorStop stop = (FlashLinearGradient.FlashColorStop) it.next();
                push(stop.position);
                push(stop.color);
            }
            //setGradient0(ctx, ( (BrowserLinearGradient) lingrad ).getNative());
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
            cmd("S", color);
        } catch (Throwable t) {
            if (Chronoscope.isErrorReportingEnabled()) {
                Window.alert("Error is " + t + " for strokecolor " + color);
            }
        }

    }


    public void stroke() {
        cmd("s");
    }

    public void lineTo(double x, double y) {
        cmd("l", x, y);
    }

    public void moveTo(double x, double y) {
        cmd("m", x, y);
    }

    public void setLineWidth(double width) {
        cmd("lw", width);
    }


    public void beginPath() {
        cmd("b");
    }


    public void scale(double sx, double sy) {
        cmd("y", sx, sy);
    }


    public void fillRect(double x, double y, double w, double h) {
        cmd("fr", x, y, w, h);
    }


    public void clip(double x, double y, double width, double height) {
        super.clip(x, y, width, height);
        beginPath();
        rect(x, y, width, height);
        cmd("C");
    }

    public void drawImage(Layer layer, double x, double y, double width, double height) {
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




    public String getTransparency() {
        //return getTransparency0(ctx);
        return "1.0";
    }

    public void drawImage(Layer layer, double sx, double sy, double swidth, double sheight, double dx, double dy,
                          double dwidth, double dheight) {


        if (layer instanceof FlashCanvas) {
            //drawImageSrcDest0(ctx, ( (BrowserCanvas) layer )
            //        .getRootLayer().getElement(), sx, sy, swidth, sheight, dx, dy, dwidth, dheight);
        } else {
            // drawImageSrcDest0(ctx, ( (BrowserLayer) layer ).getElement(), sx, sy, swidth, sheight, dx, dy, dwidth,
            //                   dheight);
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


    public String getLayerId() {
        return layerId;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setLayerOrder(int zorder) {

        // TODO: set movieclip Z value
        this.zorder = zorder;
    }

    public int getScrollLeft() {
        return scrollLeft;
    }

    public void setScrollLeft(int i) {
        scrollLeft = i;
        // TODO: set scroll
    }

    public void setLayerAlpha(float alpha) {
       cmd("SA", alpha);
        layerAlpha = alpha;
    }

    public int getLayerOrder() {
        return zorder;
    }

    public boolean isVisible() {
        return visibllity;
    }

    public float getLayerAlpha() {
        return layerAlpha;
    }


    private String getFillColor() {
        return fillColor;
    }

    public String getStrokeColor() {
        return strokeColor;
    }

    public PaintStyle createPattern(String imageUri) {
        return new FlashCanvasPattern(this, imageUri);
    }

    public void setCanvasPattern(CanvasPattern canvasPattern) {
        //TODO: no-op
    }


    public void setRadialGradient(RadialGradient radialGradient) {
        //TODO: no-op
    }

    public RadialGradient createRadialGradient(double x0, double y0, double r0, double x1, double y1, double r1) {
        return new FlashRadialGradient(this, x0, y0, r0, x1, y1, r1);
    }


    public Element getElement() {
        return ((FlashCanvas) getCanvas()).getElement();
    }

//    public native String getFlashDisplayList() /*-{
//        return this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashLayer::ctx;
//    }-*/;






     public native boolean hasDrawCommands() /*-{
        return this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashLayer::ctx.length > 3;
    }-*/;

//     public native boolean hasDrawCommands() /*-{
//        return this.@org.timepedia.chronoscope.client.browser.flashcanvas.FlashLayer::ctx.split(@org.timepedia.chronoscope.client.browser.flashcanvas.FlashLayer::CMDSEP).length > 3;
//    }-*/

    public Element getLayerElement() {
        return layerContainer;
    }

//    public TextLayer getTextLayer(String layerName) {
//        TextLayer layer = (TextLayer) layers.get(layerName);
//        if (layer == null) {
//            Element layerElem;
//            layerElem = DOM.createElement("div");
//            DOM.setStyleAttribute(layerElem, "position", "absolute");
//            DOM.setIntStyleAttribute(layerElem, "left", 0);
//            DOM.setIntStyleAttribute(layerElem, "top", 0);
//            DOM.setIntStyleAttribute(layerElem, "width", (int) getWidth());
//            DOM.setIntStyleAttribute(layerElem, "height", (int) getHeight());
//            DOM.setStyleAttribute(layerElem, "backgroundColor", "transparent");
//            DOM.setStyleAttribute(layerElem, "zIndex", "3");
//            DOM.setStyleAttribute(layerElem, "overflow", "visible");
//            DOM.appendChild(getLayerElement(), layerElem);
//            layer = new TextLayer();
//            layer.layerElem = layerElem;
//            layer.bounds = new Bounds(0, 0, getWidth(), getHeight());
//            layers.put(layerName, layer);
//
//        }
//        return layer;
//    }

   public void drawText(double x, double y, String label, String fontFamily, String fontWeight, String fontSize, String layerName) {
       selectLayer();
       cmd("DT", x, y, label, fontFamily, fontWeight, fontSize, layerName);
    }

    private void cmd(String s, double x, double y, String label, String fontFamily, String fontWeight, String fontSize, String layerName) {
        selectLayer();
        fc.cmd(s,x,y,label,fontFamily, fontWeight, fontSize, layerName);
    }

     private void cmd(String s, double x, double y, double a, String label, String fontFamily, String fontWeight, String fontSize, String layerName) {
        selectLayer();
        fc.cmd(s,x,y,a,label,fontFamily, fontWeight, fontSize, layerName);
    }
    public void clearTextLayer(String layerName) {
        selectLayer();
        cmd("CT", layerName);
    }

    public int stringWidth(String string, String font, String bold, String size) {
        return ((FlashCanvas)getCanvas()).stringWidth(string, font, bold, size, 0f);
    }

    public int stringHeight(String string, String font, String bold, String size) {
        return ((FlashCanvas)getCanvas()).stringHeight(string, font, bold, size, 0f);
    }

    public void setTextLayerBounds(String layerName, Bounds bounds) {
        selectLayer();
        cmd("TB", layerName, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    private void cmd(String s, String layerName, double x, double y, double width, double height) {
        selectLayer();
        fc.cmd(s,layerName, x, y, width, height);
    }

    public int rotatedStringWidth(String str, double rotationAngle, String fontFamily, String fontWeight, String fontSize) {
        return ((FlashCanvas)getCanvas()).stringWidth(str, fontFamily,
                fontWeight, fontSize, (float)(rotationAngle/Math.PI * 180f));

    }

    public int rotatedStringHeight(String str, double rotationAngle, String fontFamily, String fontWeight, String fontSize) {
         return ((FlashCanvas)getCanvas()).stringHeight(str, fontFamily,
                fontWeight, fontSize, (float)(rotationAngle/Math.PI * 180f));
    }

    public void drawRotatedText(double x, double y, double angle, String label, String fontFamily, String fontWeight, String fontSize, String layerName, Chart chart) {
        selectLayer();
        cmd("RT", x, y, angle/Math.PI*180, label, fontFamily, fontWeight, fontSize, layerName );
////        drawText(x, y, label, fontFamily, fontWeight, fontSize, layerName);
    }

}