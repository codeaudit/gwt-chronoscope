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
     * Construct outer Bounds of two Bounds
     * eg new Bounds((0,0,10,100), (100,100,200,300)) = (0,0,200,300)
     */
    public Bounds(Bounds b1, Bounds b2) {
        x = Math.min(b1.x, b2.x);
        width = Math.max((b1.x + b1.width - x), (b2.x + b2.width - x));

        y = Math.min(b1.y, b2.y);
        height = Math.max((b1.y + b1.height - y), (b2.y + b2.height - y));
    }

    /**
     * Default constructor that initializes x, y, width, and height to 0.
     */
    public Bounds() {
    }

    /**
     * Copies the state of this bounds to the target bounds.
     */
    public void copyTo(Bounds target) {
      target.x = this.x;
      target.y = this.y;
      target.width = this.width;
      target.height = this.height;
    }
    
    /**
     * Returns the area of this bounds.  More specifically, 
     * <tt>{@link #width} * {@link #height}</tt>.
     */
    public double area() {
        return width * height;
    }
    
    /**
     * returns the midpoint of the horizontal length of this bounds.
     */
    public double midpointX() {
      return x + (width / 2.0);
    }
    
    /**
     * Returns the x-value of the right-hand edge of this bounds.
     */
    public double rightX() {
      return x + width;
    }
    
    /**
     * Returns the y-value of the bottom edge of this bounds.
     */
    public double bottomY() {
      return y + height;
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
    
    /**
     * Convenience method for setting the (x,y) position of this Bounds object.
     * Equivalent to this: <code>bounds.x = x; bounds.y = y;</code>.
     */
    public void setPosition(double x, double y) {
      this.x = x;
      this.y = y;
    }
    
    public String toString() {
        return "[x=" + x + ",y=" + y + ",w=" + width + ",h=" + height + "]";
    }
}
