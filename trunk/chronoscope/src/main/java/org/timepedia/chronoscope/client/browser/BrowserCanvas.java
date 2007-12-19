package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.canvas.*;
import org.timepedia.chronoscope.client.render.LinearGradient;

import java.util.HashMap;


/**
 * An implementation of Canvas that creates a CANVAS tag per Layer, as well as using DIVs and images to render
 * text on the CANVAS
 */
public class BrowserCanvas extends Canvas {


    private BrowserLayer rootLayer;
    private final HashMap id2Layer = new HashMap();
    private final int width;
    private final int height;
    private Element canvasElement;

    public BrowserCanvas(View view, int width, int height) {
        super(view);
        this.width = width;
        this.height = height;
        init(width, height);
    }

    public Layer createLayer(String layerId, Bounds b) {
        Layer layer = getLayer(layerId);
        if (layer == null) {
            layer = new BrowserLayer(this, layerId, b);
            id2Layer.put(layer.getLayerId(), layer);
            DOM.appendChild(canvasElement, ( (BrowserLayer) layer ).getLayerElement());
            layer.setFillColor("transparent");
            layer.clearRect(0, 0, layer.getWidth(), layer.getHeight());
        }
        return layer;
    }

    public Layer getLayer(String layerId) {
        return (Layer) id2Layer.get(layerId);
    }

    public void attach(View view, CanvasReadyCallback canvasReadyCallback) {
        BrowserView bv = (BrowserView) view;
        DOM.appendChild(bv.getElement(), canvasElement);
        rootLayer = (BrowserLayer) createLayer("rootLayer", new Bounds(0, 0, width, height));

        super.attach(view, canvasReadyCallback);
    }

    public void disposeLayer(String layerId) {
        Layer layer = getLayer(layerId);
        if (layer != null) {
            DOM.removeChild(canvasElement, ( (BrowserLayer) layer ).getLayerElement());
        }
        id2Layer.remove(layerId);

    }

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

    public JavaScriptObject getContext() {
        return rootLayer.getContext();
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

    public int stringWidth(String string, String font, String bold, String size) {
        return rootLayer.stringWidth(string, font, bold, size);
    }

    public int stringHeight(String string, String font, String bold, String size) {
        return rootLayer.stringHeight(string, font, bold, size);
    }

    public Element createTextDiv() {
        return rootLayer.createTextDiv();
    }

    public DomTextLayer.TextLayer getTextLayer(String layerName) {
        return rootLayer.getTextLayer(layerName);
    }

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


    public BrowserLayer getRootLayer() {
        return rootLayer;
    }
}
