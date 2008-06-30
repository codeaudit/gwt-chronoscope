package org.timepedia.chronoscope.client.canvas;

/**
 * Helper class representing a rectangular bounds.
 */
public class Bounds {

  public static final Bounds NO_DAMAGE = new Bounds(0, 0, 0, 0);

  public double x, y, width, height;

  public Bounds(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public Bounds(double x, double y, double width, double height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  /**
   * Copy constructor.
   */
  public Bounds(Bounds b) {
    x = b.x;
    y = b.y;
    width = b.width;
    height = b.height;
  }

  /**
   * Default constructor that initializes x, y, width, and height to 0.
   */
  public Bounds() {
  }

  /**
   * Returns true only if the specified point (px,py) is inside this rectangular
   * bounding box. Note that points on the perimeter are considered "inside"
   * (e.g. <tt>new Bounds(0, 0, 100, 50).inside(100, 50)</tt> returns
   * <tt>true</tt>).
   */
  public boolean inside(int px, int py) {
    return px >= x && px <= x + width && py >= y && py <= y + height;
  }

  public String toString() {
    return "Bounds[x=" + x + ",y=" + y + ",w=" + width + ",h=" + height + "]";
  }
}
