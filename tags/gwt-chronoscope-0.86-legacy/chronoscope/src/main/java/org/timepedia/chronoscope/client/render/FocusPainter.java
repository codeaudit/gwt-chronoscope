package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Layer;

/**
 * Implemented by classes which wish to paint focus indicators for points
 */
public interface FocusPainter {

  public void drawFocus(XYPlot plot, Layer layer, double x, double y,
      int seriesNum);
}
