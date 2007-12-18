package org.timepedia.chronoscope.client.gss;

import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.PaintStyle;

/**
 * A GSS analogue of CssProperties for a GssElement. Only a small subset of properties are needed
 */
public abstract class GssProperties {


    public boolean visible = true;
    public Color color = new Color("#FFFFFF");
    public PaintStyle bgColor = new Color("#000000");
    public double lineThickness = 1;

    public double shadowBlur = 0;
    public double shadowOffsetX = 0;
    public double shadowOffsetY = 0;
    public Color shadowColor = new Color("#000000");


    public int width = 1;

    public double transparency = 1.0;
    public double size = 5;


    public int left = 0;
    public int top = 0;
    public String fontFamily;
    public String fontWeight;
    public String fontSize;
    public int height;

    public String toString() {
        return "visible: " + visible + "\ncolor: " + color + "\nbgColor:" + bgColor + "\nlineThickness:" +
                lineThickness + "\nshadowBlur: " + shadowBlur + "\nshadowOffsetX:" + shadowOffsetX +
                "\nshadowOffsetY:" + shadowOffsetY + "\nshadowColor:" + shadowColor + "\nwidth:" + width +
                "\ntransparency: " + transparency + "\nsize:" + size + "\nleft:" + left + "\ntop:" + top;
    }

}
