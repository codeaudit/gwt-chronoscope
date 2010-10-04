package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.data.MipMap;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.util.Array1D;
import org.timepedia.chronoscope.client.util.DateFormatter;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.date.DateFormatterFactory;
import org.timepedia.exporter.client.Exportable;

/**
 * Responsible for visually rendering a {@link Dataset} onto a {@link Layer}.
 */
public abstract class DatasetRenderer<T extends Tuple2D>
    implements GssElement, Exportable {

  private boolean isGssInitialized = false;

  private boolean customInstalled = false;

  private DateFormatter guideLineDateFmt;

  public int DEFAULT_ICON_SIZE = 7;  // 7x7px

  public boolean isCustomInstalled() {
    return customInstalled;
  }

  public void setCustomInstalled(boolean customInstalled) {
    this.customInstalled = customInstalled;
  }

  protected GssElement parentGssElement;

  protected GssProperties gssDisabledFillProps, gssDisabledLineProps, gssDisabledPointProps,
                          gssFillProps, gssLineProps, gssPointProps,
                          gssFocusFillProps, gssFocusLineProps, gssFocusPointProps,
                          gssActiveFillProps, gssActiveLineProps, gssActivePointProps,
                          gssFocusGuidelineProps,
                          gssHoverProps,
                          gssLegendProps;

  protected int datasetIndex;

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
  // public abstract double calcLegendIconWidth(View view);
  public double calcLegendIconWidth(View view) {
    gssActiveLineProps = (plot.getFocus() != null) ? gssDisabledLineProps : gssLineProps;
    String width= gssLegendProps.iconWidth;
    return "auto".equals(width) ? 8d : Double.valueOf(width.substring(0, width.length()-2));
  }

  /**
   * Called for each visible data point, typically a segment is added to the
   * current drawing path, unless a more sophisticated shape like a bar chart is
   * being rendered.
   */
  public abstract void drawCurvePart(Layer layer, T tuplDataPoint, int methodCallCount, RenderState renderState);


  public void drawGuideLine(Layer layer, int x) {
      layer.save();
      String textLayer = "plotTextLayer";
      layer.setFillColor(gssFocusGuidelineProps.color);
      double lt = Math.max(gssFocusGuidelineProps.lineThickness, 1);
      int coffset = (int) Math.floor(lt / 2.0);

      layer.fillRect(x - coffset, 0, lt, layer.getBounds().height);
      if (gssFocusGuidelineProps.dateFormat != null) {
        layer.setStrokeColor(Color.BLACK);
        int hx = x;
        double dx = ((DefaultXYPlot) plot).windowXtoDomain(hx + ((DefaultXYPlot) plot).getBounds().x);
        String label = guideLineDateFmt.format(dx);
        hx += dx < plot.getDomain().midpoint() ? 1.0 : -1 - layer.stringWidth(label, "Verdana", "", "9pt");

        layer.drawText(hx, 5.0, label, "Verdana", "", "9pt", textLayer, Cursor.DEFAULT);
      }
      layer.restore();
  }

  /**
   * Draws the hover point for each dataset managed by the plot.
   */
  public abstract void drawHoverPoint(Layer layer, T point, int datasetIndex);

  /**
   * Render a small icon or sparkline representing this curve at the given x,y
   * screen coordinates, and return the the Bounds of the icon.
   */
  // public abstract void drawLegendIcon(Layer layer, double x, double y, int dim);
  public void drawLegendIcon(Layer layer, double x, double y, int dim) {
      layer.save();

      GssProperties alineProp, apointProp;

      if (plot.getFocus() != null
          && plot.getFocus().getDatasetIndex() != this.datasetIndex) {
        alineProp = gssDisabledLineProps;
        apointProp = gssDisabledPointProps;
      } else {
        alineProp = gssLineProps;
        apointProp = gssPointProps;
      }

      layer.beginPath();
      layer.moveTo(x, y);
      // layer.setLineWidth(alineProp.lineThickness);
      String height = gssLegendProps.iconHeight;
      if(height.equals("auto")){
          layer.setLineWidth(DEFAULT_ICON_SIZE);
      }else{
         layer.setLineWidth(Double.valueOf(height.substring(0, height.length()-2)));
      }

      layer.setShadowBlur(alineProp.shadowBlur);
      layer.setShadowColor(alineProp.shadowColor);
      layer.setShadowOffsetX(alineProp.shadowOffsetX);
      layer.setShadowOffsetY(alineProp.shadowOffsetY);
      layer.setStrokeColor(alineProp.color);
      layer.setTransparency((float) alineProp.transparency);

      String width = gssLegendProps.iconWidth;
      if(width.equals("auto")){
          layer.lineTo(x + DEFAULT_ICON_SIZE, y);
      }else{
          double widthValue=Double.valueOf(width.substring(0, width.length()-2));
          layer.lineTo(x + widthValue, y);
      }

      layer.stroke();

      /** eliminating point in legend icon for now.
      if (apointProp.visible) {
        layer.translate(x, y - apointProp.size / 2 + 1);
        layer.beginPath();
        layer.setFillColor(apointProp.bgColor);
        layer.setTransparency((float) apointProp.transparency);
        layer.arc(6, 0, apointProp.size, 0, 2 * Math.PI, 1);
        layer.setShadowBlur(0);
        layer.fill();
        layer.beginPath();
        layer.setLineWidth(apointProp.lineThickness);
        if (apointProp.size < 1) {
          apointProp.size = 1;
        }
        layer.arc(6, 0, apointProp.size, 0, 2 * Math.PI, 1);
        layer.setLineWidth(apointProp.lineThickness);
        layer.setShadowBlur(apointProp.shadowBlur);
        layer.setStrokeColor(apointProp.color);
        layer.stroke();
      }
      */

      layer.restore();
    }


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

  /**
   * Subclasses can override this method to return a domain span that's larger
   * than the maximum according to current mipmapped domain associated with the
   * {@link DrawableDataset}.  This is sometimes necessary depending on how each
   * datapoint is rendered (e.g. barchart requires domain padding to avoid
   * cropping of the end point bars).
   */
  protected Interval getDrawableDomain(Array1D mipmappedDomain) {
    return new Interval(mipmappedDomain.get(0), mipmappedDomain.getLast());
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
    gssFocusPointProps = view.getGssProperties(pointElement, "focus");
    gssFocusGuidelineProps = view.getGssProperties(new GssElementImpl("guideline", pointElement), "focus");
    if (gssFocusGuidelineProps.dateFormat != null) {
      this.guideLineDateFmt = DateFormatterFactory.getInstance()
          .getDateFormatter(gssFocusGuidelineProps.dateFormat);
    }

    gssHoverProps = view.getGssProperties(pointElement, "hover");
    gssLineProps = view.getGssProperties(this, "");
    gssPointProps = view.getGssProperties(pointElement, "");

    gssLegendProps = view.getGssProperties(new GssElementImpl("axislegend" , parentGssElement), "");

    isGssInitialized = true;
  }

  public GssProperties getLegendProperties(int dim, RenderState rs) {
    if (plot.getFocus() != null
        && plot.getFocus().getDatasetIndex() != this.datasetIndex) {
      return gssDisabledLineProps;
    } else {
      return gssLineProps;
    }
  }

  public GssProperties getCurveProperties() {
    return gssLineProps;
  }

  public int getNumPasses(Dataset dataset) {
    return 1;
  }

  public double getRange(Tuple2D tuple2D) {
    return tuple2D.getRange0();
  }

  public Interval getRangeExtrema(Dataset ds) {
    return ds.getRangeExtrema(0);
  }

  public Interval getRangeExtrema(MipMap ds) {
    return ds.getRangeExtrema(0);
  }

  public int getFocusDimension(int pass) {
    return 0;
  }

  public int[] getLegendEntries(Dataset dataset) {
    return getPassOrder(dataset);
  }

  public void drawLegendIcon(Layer layer, double lblX, double v) {
    drawLegendIcon(layer, lblX, v, 0);
  }

  public double getRangeValue(Tuple2D tuple, int dimension) {
    return tuple.getRange0();
  }

  public int[] getPassOrder(Dataset<T> dataSet) {
    return new int[]{0};
  }
}
