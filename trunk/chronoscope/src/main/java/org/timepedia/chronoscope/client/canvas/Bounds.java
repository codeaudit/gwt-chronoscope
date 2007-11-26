package org.timepedia.chronoscope.client.canvas;

/**
 * Helper class representing a rectangular bounds
 */
public class Bounds {
    public double x, y, width, height;
    public static final Bounds NO_DAMAGE = new Bounds(0, 0, 0, 0);

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

    public Bounds(Bounds b) {
        x = b.x;
        y = b.y;
        width = b.width;
        height = b.height;
    }

    public Bounds() {
    }

    public boolean inside(int px, int py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }

    public String toString() {
        return "Bounds[x=" + x + ",y=" + y + ",w=" + width + ",h=" + height + "]";
    }
}
