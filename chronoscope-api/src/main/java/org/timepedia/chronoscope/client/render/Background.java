package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Layer;

/**
 * Classes that wish to render Plot backgrounds must implement this
 */
public interface Background {

  void paint(XYPlot plot, Layer layer, double domainOrigin, double currentDomain);
}
