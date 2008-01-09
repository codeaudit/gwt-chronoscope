package org.timepedia.chronoscope.client.canvas;

/**
 * Represents a PaintStyle which is an RGBA color
 */
public class Color implements PaintStyle {
    private final String color;

    public Color(String color) {

        this.color = color;
    }

    public String toString() {
        return color;
    }

}
