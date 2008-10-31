package org.timepedia.chronoscope.client.canvas;

/**
 * Represents a PaintStyle which is an RGBA color
 */
public class Color implements PaintStyle {

  private final String color;

  private boolean rgbaSet = false;

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
}
