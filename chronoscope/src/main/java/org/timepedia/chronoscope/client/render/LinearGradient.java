package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.canvas.PaintStyle;

/**
 * Implement this interface to create LinearGradient fill styles for the Canvas
 */
public interface LinearGradient extends PaintStyle {

  void addColorStop(double position, String color);
}
