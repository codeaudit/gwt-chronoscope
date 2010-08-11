package org.timepedia.chronoscope.client.canvas;

/**
 * Represents a PaintStyle which is an RGBA color
 */
public class Color implements PaintStyle {

  private final String color;

  private boolean rgbaSet = false;

  public static final Color TRANSPARENT = new Color(0,0,0,0);
  public static final Color WHITE = new Color(255,255,255);
  public static final Color BLACK = new Color(0,0,0);
  public static final Color GREEN = new Color(0,255,0);

  public Color(int r, int g, int b) {
    this(r,g,b,255);
  }

  public Color(int r, int g, int b, int a) {
    setRgba(r, g, b, a);
    this.rgbaSet = true;
    this.color = "rgba("+r+","+g+","+b+","+a+")";
  }

  private void setRgba(int r, int g, int b, int a) {
    this.rgba = a << 24 | r << 16 | g << 8 | b;
  }

  public boolean isRgbaSet() {
    return rgbaSet;
  }

  public void setRgba(int rgba) {
    this.rgba = rgba;
    rgbaSet = true;
  }

  private int rgba;

  public Color(String color) {

    this.color = color;
  }

  public int getRGBA() {
    return this.rgba;
  }

  public String toString() {
    return color;
  }

  public String getCSSColor() {
    return color;
  }
}
