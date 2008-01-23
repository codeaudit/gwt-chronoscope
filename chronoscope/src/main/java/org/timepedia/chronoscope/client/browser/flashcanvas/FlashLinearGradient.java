package org.timepedia.chronoscope.client.browser.flashcanvas;

import org.timepedia.chronoscope.client.render.LinearGradient;

import java.util.ArrayList;

/**
 *
 */
public class FlashLinearGradient implements LinearGradient {
    ArrayList stops=new ArrayList();
    private double x;
    private double y;
    private double x2;
    private double y2;

    public double getX2() {
        return x2;
    }

    public double getX() {
        return x;
    }

    public double getY2() {
        return y2;
    }

    public double getY() {
        return y;
    }

    public ArrayList getStops() {
        return stops;
    }

    public FlashLinearGradient(FlashLayer flashLayer, double x, double y, double w, double h) {
        this.x = x;
        this.y = y;
        this.x2 = w;
        this.y2 = h;
    }

    public void addColorStop(double position, String color) {
       stops.add(new FlashColorStop(position, color));
    }

    public static class FlashColorStop {
        public double position;
        public String color;

        public FlashColorStop(double position, String color) {

            this.position = position;
            this.color = color;
        }
    }
}
