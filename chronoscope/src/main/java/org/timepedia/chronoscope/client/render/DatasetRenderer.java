package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.exporter.client.Exportable;

/**
 * Responsible for visually rendering a {@link Dataset} onto a {@link Layer}.
 */
public abstract class DatasetRenderer<T extends Tuple2D> 
    implements GssElement, Exportable {
  
  private boolean isGssInitialized = false;
  
  protected GssProperties gssDisabledFillProps, gssDisabledLineProps, 
  gssDisabledPointProps, gssFillProps, gssFocusProps, gssHoverProps,
  gssLineProps, gssPointProps;
  
  protected int datasetIndex;
  
  protected GssElement parentGssElement;
  
  protected XYPlot<T> plot;
  
  /**
   * Called before first data is plotted, typically drawing state is setup, or a
   * path is begun.
   */
  public abstract void beginCurve(Layer layer, RenderState renderState);

  /**
   * Called before points are plotted, typically to setup drawing state (colors,
   * etc).
   */
  public abstract void beginPoints(Layer layer, RenderState renderState);

  
  /**
   * Calculates the pixel width of the legend icon.
   */
  public abstract double calcLegendIconWidth(View view);

  /**
   * Called for each visible data point, typically a segment is added to the
   * current drawing path, unless a more sophisticated shape like a bar chart is
   * being rendered.
   */
  public abstract void drawCurvePart(Layer layer, T tuplDataPoint, 
      int methodCallCount, RenderState renderState);

  /**
   * Draws the hover point for each dataset managed by the plot.
   */
  public abstract void drawHoverPoint(Layer layer, T point, int datasetIndex);

  /**
   * Render a small icon or sparkline representing this curve at the given x,y
   * screen coordinates, and return the the Bounds of the icon.
   */
  public abstract void drawLegendIcon(Layer layer, double x, double y);

  /**
   * Draw an individual point of the given tuple.
   */
  public abstract void drawPoint(Layer layer, T tupleDataPoint, RenderState renderState);

  /**
   * Called after last data is plotted (last call to drawCurvePart), typically
   * when stroke() or fill() is invoked.
   */
  public abstract void endCurve(Layer layer, RenderState renderState);

  /**
   * Called after all points are plotted, typically to cleanup state (restore()
   * after a save() ).
   */
  public abstract void endPoints(Layer layer, RenderState renderState);

  /**
   * The maximum number of datapoints that should be drawn in the view and
   * maintain interactive framerates for this renderer.
   */
  public int getMaxDrawableDatapoints() {
    return plot.getMaxDrawableDataPoints();
  }
  
  public final GssElement getParentGssElement() {
    return this.parentGssElement;
  }
  
  public final void setDatasetIndex(int datasetIndex) {
    this.datasetIndex = datasetIndex;
  }
  
  public final void setParentGssElement(GssElement parentGssElement) {
    this.parentGssElement = parentGssElement;
  }
  
  public final void setPlot(XYPlot<T> plot) {
    this.plot = plot;
  }
  
  public void initGss(View view) {
    if (isGssInitialized) {
      return;
    }

    GssElement fillElement = new GssElementImpl("fill", parentGssElement);
    GssElement pointElement = new GssElementImpl("point", parentGssElement);

    gssDisabledFillProps = view.getGssProperties(fillElement, "disabled");
    gssDisabledLineProps = view.getGssProperties(this, "disabled");
    gssDisabledPointProps = view.getGssProperties(pointElement, "disabled");
    gssFillProps = view.getGssProperties(fillElement, "");
    gssFocusProps = view.getGssProperties(pointElement, "focus");
    gssHoverProps = view.getGssProperties(pointElement, "hover");
    gssLineProps = view.getGssProperties(this, "");
    gssPointProps = view.getGssProperties(pointElement, "");

    isGssInitialized = true;
  }

}
