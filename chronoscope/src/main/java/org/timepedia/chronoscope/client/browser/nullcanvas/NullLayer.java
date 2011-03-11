package org.timepedia.chronoscope.client.browser.nullcanvas;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.canvas.*;
import org.timepedia.chronoscope.client.render.LinearGradient;

public class NullLayer extends AbstractLayer{

    @Override
    public void arc(double x, double y, double radius, double startAngle, double endAngle, int clockwise) {
        // ...
    }

    @Override
    public void beginPath() {
        // ...
    }

    @Override
    public void clearRect(double x, double y, double width, double height) {
        // ...
    }

    @Override
    public void clearTextLayer(String textLayer) {
        // ...
    }

    @Override
    public void closePath() {
        // ...
    }

    public void dispose() {
        // ...
    }

    @Override
    public LinearGradient createLinearGradient(double startx, double starty, double endx, double endy) {
        return null;  // ...
    }

    @Override
    public PaintStyle createPattern(String imageUri) {
        return Color.WHITE;  // ...
    }

    @Override
    public RadialGradient createRadialGradient(double x0, double y0, double r0, double x1, double y1, double r1) {
        return null;  // ...
    }

    @Override
    public void drawImage(Layer layer, double x, double y, double width, double height) {
        // ...
    }

    @Override
    public void drawImage(Layer layer, double sx, double sy, double swidth, double sheight, double dx, double dy, double dwidth, double dheight) {
        // ...
    }

    @Override
    public void drawImage(CanvasImage image, double dx, double dy, double dwidth, double dheight) {
        // ...
    }

    @Override
    public void drawText(double x, double y, String label, String fontFamily, String fontWeight, String fontSize, String textLayer, Cursor cursorStyle) {
        // ...
    }

    @Override
    public void fill() {
        // ...
    }

    @Override
    public void fillRect(double startx, double starty, double width, double height) {
        // ...
    }

    @Override
    public Bounds getBounds() {
        return new Bounds(0,0,1,1);  // ...
    }

    @Override
    public double getHeight() {
        return 1;  // ...
    }

    @Override
    public float getLayerAlpha() {
        return 0;  // ...
    }

    @Override
    public String getLayerId() {
        return null;  // ...
    }

    @Override
    public int getLayerOrder() {
        return 0;  // ...
    }

    @Override
    public int getScrollLeft() {
        return 0;  // ...
    }

    @Override
    public String getStrokeColor() {
        return null;  // ...
    }

    @Override
    public String getTransparency() {
        return "1.0";  // ...
    }

    @Override
    public double getWidth() {
        return 1.0;  // ...
    }

    @Override
    public boolean isVisible() {
        return false;  // ...
    }

    @Override
    public void lineTo(double x, double y) {
        // ...
    }

    @Override
    public void moveTo(double x, double y) {
        // ...
    }

    @Override
    public void rect(double x, double y, double width, double height) {
        // ...
    }

    @Override
    public void restore() {
        // ...
    }

    @Override
    public void save() {
        // ...
    }

    @Override
    public void scale(double sx, double sy) {
        // ...
    }

    @Override
    public void setBounds(Bounds bounds) {
        // ...
    }

    @Override
    public void setCanvasPattern(CanvasPattern canvasPattern) {
        // ...
    }

    @Override
    public void setComposite(int mode) {
        // ...
    }

    @Override
    public void setLayerAlpha(float alpha) {
        // ...
    }

    public void setLayerOrder(int zorder) {
    // ...
}

    @Override
    public void setLinearGradient(LinearGradient lingrad) {
        // ...
    }

    @Override
    public void setLineWidth(double width) {
        // ...
    }

    @Override
    public void setRadialGradient(RadialGradient radialGradient) {
        // ...
    }

    @Override
    public void setScrollLeft(int i) {
        // ...
    }

    @Override
    public void setShadowBlur(double width) {
        // ...
    }

    @Override
    public void setShadowColor(String color) {
        // ...
    }

    @Override
    public void setShadowOffsetX(double x) {
        // ...
    }

    @Override
    public void setShadowOffsetY(double y) {
        // ...
    }

    @Override
    public void setTextLayerBounds(String textLayer, Bounds textLayerBounds) {
        // ...
    }

    @Override
    public void setTransparency(float value) {
        // ...
    }

    @Override
    public void setVisibility(boolean visibility) {
        // ...
    }

    @Override
    public int stringHeight(String string, String font, String bold, String size) {
        return 0;  // ...
    }

    @Override
    public int stringWidth(String string, String font, String bold, String size) {
        return 0;  // ...
    }

    @Override
    public void stroke() {
        // ...
    }

    @Override
    public void translate(double x, double y) {
        // ...
    }

    @Override
    public void setFillColor(Color color) {
        // ...
    }

    @Override
    public void setStrokeColor(Color color) {
        // ...
    }
}
