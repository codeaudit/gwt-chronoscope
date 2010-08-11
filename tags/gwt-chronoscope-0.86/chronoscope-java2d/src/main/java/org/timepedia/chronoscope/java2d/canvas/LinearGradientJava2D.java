package org.timepedia.chronoscope.java2d.canvas;

import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.render.LinearGradient;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: ray
 * Date: Jan 17, 2007
 * Time: 12:01:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class LinearGradientJava2D implements LinearGradient {
    private double startx;
    private double starty;
    private double endx;
    private double endy;

    private ArrayList<Stop> stops = new ArrayList<Stop>();

    public LinearGradientJava2D(double startx, double starty, double endx, double endy) {

        this.startx = startx;
        this.starty = starty;
        this.endx = endx;
        this.endy = endy;
    }

    public void addColorStop(double position, String color) {
        stops.add(new Stop(position, color));
    }

    public void translate(Layer layer, int lx, int ly) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    static class Stop {
        public double position;
        public Color color;

        public Stop(double position, String color) {
            this.position = position;
            this.color = Color.decode(color);
        }
    }

    GradientPaint getNative() {
        return new GradientPaint((float) startx, (float) starty, stops.get(0).color, (float) endx, (float) endy, stops.get(1).color);
    }
}
