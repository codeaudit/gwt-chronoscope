package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.CanvasPattern;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.util.ArgChecker;

/**
 * Responsible for rendering the background of a plot
 */
public class GssBackground implements Background, GssElement {

  private final GssProperties gssPlotProperties;

  public GssBackground(View view) {
    ArgChecker.isNotNull(view, "view");
    gssPlotProperties = view.getGssProperties(this, "");
  }

  public GssElement getParentGssElement() {
    return null;
  }

  public String getType() {
    return "plot";
  }

  public String getTypeClass() {
    return null;
  }

  public void paint(XYPlot plot, Layer layer, double domainOrigin, double currentDomain) {
    layer.save();
    Bounds b = layer.getBounds();
    if (gssPlotProperties.bgColor instanceof CanvasPattern) {
      layer.setFillColor(Color.TRANSPARENT);
      layer.clearRect(b.x, b.y, b.width, b.height);
      layer.setComposite(Layer.COPY);
      layer.setFillColor(gssPlotProperties.bgColor);
      layer.beginPath();
      layer.rect(0, 0, b.width, b.height);
      layer.fill();
    } else {
      layer.translate(0, 0);
      layer.setComposite(Layer.COPY);
      layer.setFillColor(gssPlotProperties.bgColor);
      layer.fillRect(0, 0, b.width, b.height);
    }
    layer.restore();
  }
}
