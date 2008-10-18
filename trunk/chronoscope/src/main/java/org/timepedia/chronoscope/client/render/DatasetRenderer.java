package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.exporter.client.Exportable;

/**
 * Responsible for visually rendering a {@link Dataset} onto a {@link Layer}.
 */
public abstract class DatasetRenderer<S extends Tuple2D, T extends Dataset<S>> 
    implements Exportable {

  /**
   * Called before first data is plotted, typically drawing state is setup, or a
   * path is begun.
   */
  public abstract void beginCurve(XYPlot<S,T> plot, Layer layer, RenderState renderState);

  /**
   * Called before points are plotted, typically to setup drawing state (colors,
   * etc).
   */
  public abstract void beginPoints(XYPlot<S,T> plot, Layer layer, RenderState renderState);

  
  /**
   * Calculates the pixel width of the legend icon.
   * 
   * @see #drawLegendIcon(XYPlot, Layer, double, double, int)
   */
  public abstract double calcLegendIconWidth(XYPlot<S,T> plot, View view);

  /**
   * Called for each visible data point, typically a segment is added to the
   * current drawing path, unless a more sophisticated shape like a bar chart is
   * being rendered.
   */
  public abstract void drawCurvePart(XYPlot<S,T> plot, Layer layer, S data,
      int seriesNum, RenderState renderState);

  /**
   * Render a small icon or sparkline representing this curve at the given x,y
   * screen coordinates, and return the the Bounds of the icon.
   */
  public abstract Bounds drawLegendIcon(XYPlot<S,T> plot, Layer layer, double x,
      double y, int seriesNum);

  /**
   * Draw an individual point of the given tuple.
   */
  public abstract void drawPoint(XYPlot<S,T> plot, Layer layer, S data,
      int seriesNum, RenderState renderState);

  /**
   * Called after last data is plotted (last call to drawCurvePart), typically
   * when stroke() or fill() is invoked.
   */
  public abstract void endCurve(XYPlot<S,T> plot, Layer layer, int seriesNum, 
      RenderState renderState);

  /**
   * Called after all points are plotted, typically to cleanup state (restore()
   * after a save() ).
   */
  public abstract void endPoints(XYPlot<S,T> plot, Layer layer, int seriesNum, 
      RenderState renderState);

  /**
   * The maximum number of datapoints that should be drawn in the view and
   * maintain interactive framerates for this renderer.
   */
  public int getMaxDrawableDatapoints(XYPlot<S,T> plot) {
    return plot.getMaxDrawableDataPoints();
  }
}
