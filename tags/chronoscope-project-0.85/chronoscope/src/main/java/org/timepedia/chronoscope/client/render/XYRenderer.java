package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.exporter.client.Exportable;

/**
 * Abstract class used classes which render a curve using data
 */
public abstract class XYRenderer implements Exportable {

  /**
   * Called before first data is plotted, typically drawing state is setup, or a
   * path is begun
   */
  public abstract void beginCurve(XYPlot plot, Layer layer, boolean inSelection,
      boolean disabled);

  /**
   * Called before points are plotted, typically to setup drawing state (colors,
   * etc)
   */
  public abstract void beginPoints(XYPlot plot, Layer layer,
      boolean inSelection, boolean disabled);

  /**
   * Called for each visible data point, typically a segment is added to the
   * current drawing path, unless a more sophisticated shape like a bar chart is
   * being rendered
   */
  public abstract void drawCurvePart(XYPlot plot, Layer layer, double dataX,
      double dataY, int seriesNum, boolean isFocused, boolean isHovered,
      boolean inSelection, boolean isDisabled);

  /**
   * Render a small icon or sparkline representing this curve at the given x,y
   * screen coordinates, and return the the Bounds of the icon
   */
  public abstract Bounds drawLegendIcon(XYPlot plot, Layer layer, double x,
      double y, int seriesNum);

  /**
   * Draw an individual point of the given domain and range values
   */
  public abstract void drawPoint(XYPlot plot, Layer layer, double domainx,
      double rangey, int seriesNum, boolean focused, boolean hovered,
      boolean inSelection, boolean disabled);

  /**
   * Called after last data is plotted (last call to drawCurvePart), typically
   * when stroke() or fill() is invoked
   */
  public abstract void endCurve(XYPlot plot, Layer layer, boolean inSelection,
      boolean disabled, int seriesNum);

  /**
   * Called after all points are plotted, typically to cleanup state (restore()
   * after a save() )
   */
  public abstract void endPoints(XYPlot plot, Layer layer, boolean inSelection,
      boolean disabled, int seriesNum);

  /**
   * The maximum number of datapoints that should be drawn in the view and
   * maintain interactive framerates for this renderer
   */
  public int getMaxDrawableDatapoints(XYPlot plot) {
    return plot.getMaxDrawableDataPoints();
  }
}
