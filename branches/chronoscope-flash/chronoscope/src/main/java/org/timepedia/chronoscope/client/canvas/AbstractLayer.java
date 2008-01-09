package org.timepedia.chronoscope.client.canvas;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.render.LinearGradient;


/**
 * @author Ray Cromwell <ray@timepedia.org>
 */
public abstract class AbstractLayer implements Layer {
    private Canvas canvas;

    public AbstractLayer() {
    }

    public AbstractLayer(Canvas canvas) {
        this.canvas = canvas;
    }


    public Canvas getCanvas() {
        return canvas;
    }

    public void fillRect() {
        fillRect(0, 0, getWidth(), getHeight());
    }

    public void save() {

    }

    public void restore() {

    }

    public void clip(double x, double y, double width, double height) {
    }


    public void setFillColor(PaintStyle p) {
        if (p instanceof Color) {
            setFillColor(p.toString());
        } else if (p instanceof LinearGradient) {
            setLinearGradient((LinearGradient) p);
        } else if (p instanceof RadialGradient) {
            setRadialGradient((RadialGradient) p);
        } else if (p instanceof CanvasPattern) {
            setCanvasPattern((CanvasPattern) p);
        }
    }

    public void setShadowColor(Color shadowColor) {
        setShadowColor(shadowColor.toString());
    }

    public void setStrokeColor(PaintStyle p) {

        if (p instanceof Color) {
            setStrokeColor(p.toString());
        }

    }


    public void drawRotatedText(double x, double y, double v, String label, String fontFamily, String fontWeight,
                                String fontSize, String layerName, Chart chart) {

    }

    public int rotatedStringWidth(String str, double rotationAngle, String fontFamily, String fontWeight,
                                  String fontSize) {
        return stringWidth(str, fontFamily, fontWeight, fontSize);

    }

    public int rotatedStringHeight(String str, double rotationAngle, String fontFamily, String fontWeight,
                                   String fontSize) {
        return stringHeight(str, fontFamily, fontWeight, fontSize);
    }


    public DisplayList createDisplayList(String id) {
        return new DefaultDisplayListImpl(id, this);
    }


}
