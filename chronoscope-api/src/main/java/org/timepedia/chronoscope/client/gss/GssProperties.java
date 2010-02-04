package org.timepedia.chronoscope.client.gss;

import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.PaintStyle;

/**
 * A GSS analogue of CssProperties for a GssElement. Only a small subset of
 * properties are needed.
 */
public class GssProperties {

  public PaintStyle bgColor = new Color("#000000");

  public Color color = new Color("#FFFFFF");

  public String fontFamily = "Verdana";

  public String fontSize = "9pt";

  public String fontWeight = "normal";

  public int height;

  public int left = 0;

  public double lineThickness = 1;

  public double shadowBlur = 0;

  public Color shadowColor = new Color("#000000");

  public double shadowOffsetX = 0;

  public double shadowOffsetY = 0;

  public double size = 5;

  public String tickAlign = "middle";

  public String tickPosition = "outside";

  public int top = 0;

  public double transparency = 1.0;

  public boolean visible = true;

  public int width = 1;

  public String pointShape = "circle";

  public String display = "auto";

  public String dateFormat = null;

  public String group = null;
  
  public boolean gssSupplied = false;
  
  public GssProperties setColor(Color color) {
    this.color = color;
    return this;
  }

  public GssProperties setTransparency(double transparency) {
    this.transparency = transparency;
    return this;
  }

  public String toString() {
    return "visible: " + visible + "\ncolor: " + color + "\nbgColor:" + bgColor
        + "\nlineThickness:" + lineThickness + "\nshadowBlur: " + shadowBlur
        + "\nshadowOffsetX:" + shadowOffsetX + "\nshadowOffsetY:"
        + shadowOffsetY + "\nshadowColor:" + shadowColor + "\nwidth:" + width
        + "\ntransparency: " + transparency + "\nsize:" + size + "\nleft:"
        + left + "\ntop:" + top;
  }
}
