package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.data.AbstractDataset;
import org.timepedia.chronoscope.client.data.FlyweightTuple;
import org.timepedia.chronoscope.client.data.MipMap;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.data.RenderedPoint;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.util.Array1D;
import org.timepedia.chronoscope.client.util.DateFormatter;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.JavaArray2D;
import org.timepedia.chronoscope.client.util.date.DateFormatterFactory;
import org.timepedia.exporter.client.Exportable;

import java.util.HashMap;
import java.util.HashSet;

import static org.timepedia.chronoscope.client.render.DatasetLegendPanel.LEGEND_ICON_PAD;
import static org.timepedia.chronoscope.client.render.DatasetLegendPanel.LEGEND_ICON_SIZE;


/**
 * Responsible for visually rendering a {@link Dataset} onto a {@link Layer}.
 */
public abstract class DatasetRenderer<T extends Tuple2D>
    implements GssElement, Exportable {

  protected boolean isGssInitialized = false;

  private boolean customInstalled = false;

  private DateFormatter guideLineDateFmt;

  // protected HashMap<String, FlyweightTuple> regions = new HashMap<String, FlyweightTuple>();  // for hit detection of points, features, etc

  protected HashMap<String, HashSet<RenderedPoint>> regions = new HashMap<String, HashSet<RenderedPoint>>();  // for hit detection of points, features, etc

  protected double DOMAIN_REGIONS = 32d;
  protected double RANGE_REGIONS = 8d;

  // hit detection range =~  org.timepedia.chronoscope.client.plot.DefaultXYPlot.MAX_FOCUS_DIST;
  protected double HIT_DISTANCE = 8d;

  private String[] scratchRegions;

  private HashSet<String> scratchX = new HashSet<String>(3);
  private HashSet<String> scratchY = new HashSet<String>(3);

  protected int focusDimension = 0;
  // private int focus = 0;

  public void clearRegions() {
    regions.clear();
  }


  protected String[] getRegions(double plotX, double plotY) {
    Bounds b = plot.getBounds();
    if (plotX < 0) { plotX = 0; }
    if (plotX > b.width) { plotX = b.width; }
    if (plotY < 0) { plotY = 0; }
    if (plotY > b.height) { plotY = b.height; }
    scratchX.clear();
    scratchY.clear();


    scratchX.add(naturalize((plotX / b.width) * DOMAIN_REGIONS));

    double px = plotX - HIT_DISTANCE;
    px = Math.max(0, px);
    scratchX.add(naturalize((px / b.width) * DOMAIN_REGIONS));

    px = plotX + HIT_DISTANCE;
    px = Math.min(b.width, px);
    scratchX.add(naturalize((px / b.width) * DOMAIN_REGIONS));


    scratchY.add(naturalize((plotY / b.height) * RANGE_REGIONS));

    double py = plotY - HIT_DISTANCE;
    py  = Math.max(0, py);
    scratchY.add(naturalize((py / b.height) * RANGE_REGIONS));

    py = plotY + HIT_DISTANCE;
    py = Math.min(b.height, py);
    scratchY.add(naturalize((py / b.height) * RANGE_REGIONS));

    String[] X = scratchX.toArray(new String[scratchX.size()]);
    String[] Y = scratchY.toArray(new String[scratchY.size()]);
    scratchRegions = new String[X.length * Y.length];
    int l = 0;
    for (int i=0; i < X.length; i++) {
      for (int j=0; j < Y.length; j++) {
        scratchRegions[l] = X[i] + "," + Y[j];
        l++;
      }
    }

    return scratchRegions;
  }

  protected void regionalize(int datasetIndex, int domainIndex, int dim, double domain, double range, double plotX, double plotY) {
    String[] re = getRegions(plotX, plotY);
    for (int i=0; i<re.length; i++) {
      if (null == regions.get(re[i])) {
        regions.put(re[i], new HashSet<RenderedPoint>());
      }
      regions.get(re[i]).add(new RenderedPoint(datasetIndex, domainIndex, dim, domain, range, plotX, plotY));
    }
  }

  private String naturalize(double d) {
    String s = String.valueOf(Math.floor(d));
    int i = s.indexOf('.');
    if ( i > -1) {
      s = s.substring(0,i);
    }
    return s;
  }

  public void addClickable(int datasetIndex, int domainIndex, int dim, double domain, double range, double plotX, double plotY) {
    regionalize(datasetIndex, domainIndex, dim, domain, range, plotX, plotY);
  }

  public HashSet<RenderedPoint> getClickable(int plotX, int plotY) {
    Bounds b = plot.getBounds();
    String d = naturalize((plotX / b.width) * DOMAIN_REGIONS);
    String r = naturalize((plotY / b.height) * RANGE_REGIONS);

    return regions.get(d+","+r);
  }

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

  public void clear() {
    parentGssElement = null;

    gssDisabledFillProps = null;
    gssDisabledLineProps = null;
    gssDisabledPointProps = null;

    gssFillProps = null;
    gssLineProps = null;
    gssPointProps = null;

    gssFocusFillProps = null;
    gssFocusLineProps = null;
    gssFocusPointProps = null;

    gssActiveFillProps = null;
    gssActiveLineProps = null;
    gssActivePointProps = null;

    gssFocusGuidelineProps = null;
    gssHoverProps = null;
    gssLegendProps = null;

    plot = null;
  }
  /**
   * Called for each visible data point, typically a segment is added to the
   * current drawing path, unless a more sophisticated shape like a bar chart is
   * being rendered.
   */
  public abstract void drawCurvePart(int datasetIndex, int domainIndex, Layer layer, T tuplDataPoint, int methodCallCount, RenderState renderState);


  public void drawFocusPointGuideLine(Layer layer, int x) {
      layer.save();
      String textLayer = "overlays";
      layer.clearTextLayer(textLayer);

      // layer.setFillColor(gssFocusGuidelineProps.color);

      // layer.setFillColor(gssFocusPointProps.color);

      double lt = Math.max(gssFocusGuidelineProps.lineThickness, 1);
      int coffset = (int) Math.floor(lt / 2.0);

      layer.fillRect(x - coffset, 0, lt, layer.getBounds().height);

      // for now, don't bother drawing label on point guidelines
      /*
      if (gssFocusGuidelineProps.dateFormat != null) {
        layer.setStrokeColor(Color.BLACK);
        int hx = x;
        double dx = ((DefaultXYPlot) plot).windowXtoDomain(hx + ((DefaultXYPlot) plot).getBounds().x);
        String label = guideLineDateFmt.format(dx);
        hx += dx < plot.getDomain().midpoint() ? 1.0 : -1 - layer.stringWidth(label, "Helvetica", "", "8pt");

        layer.drawText(hx, plot.getInnerBounds().y + 10, label, "Helvetica", "", "8pt", textLayer, Cursor.CONTRASTED);
      }
      */
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
  public void drawLegendIcon(Layer layer, double x, double y, double w, double h, int dim) {
      layer.save();

      GssProperties alineProp;
      if (plot.getFocus() != null
          && plot.getFocus().getDatasetIndex() != this.datasetIndex) {
        alineProp = gssDisabledLineProps;
      } else {
        alineProp = gssLineProps;
      }

      // layer.setShadowBlur(alineProp.shadowBlur);
      // layer.setShadowColor(alineProp.shadowColor);
      // layer.setShadowOffsetX(alineProp.shadowOffsetX);
      // layer.setShadowOffsetY(alineProp.shadowOffsetY);

      layer.setTransparency((float) alineProp.transparency);
      layer.setFillColor(alineProp.color);
      layer.fillRect(x, y, w, h);
      layer.restore();
    }


  /**
   * Draw an individual point of the given tuple.
   */
  public abstract void drawPoint(int datasetIndex, int domainIndex, Layer layer, T tupleDataPoint, RenderState renderState);

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
    isGssInitialized = false;
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

    gssFillProps = view.getGssProperties(fillElement, "");
    gssLineProps = view.getGssProperties(this, "");
    gssPointProps = view.getGssProperties(pointElement, "");

    gssDisabledFillProps = view.getGssProperties(fillElement, "disabled");
    gssDisabledLineProps = view.getGssProperties(this, "disabled");
    gssDisabledPointProps = view.getGssProperties(pointElement, "disabled");

    gssFocusFillProps = view.getGssProperties(fillElement, "focus");
    gssFocusLineProps = view.getGssProperties(this, "focus");
    gssFocusPointProps = view.getGssProperties(pointElement, "focus");

    gssFocusGuidelineProps = view.getGssProperties(new GssElementImpl("guideline", pointElement), "focus");
    if (gssFocusGuidelineProps.dateFormat != null) {
      this.guideLineDateFmt = DateFormatterFactory.getInstance()
          .getDateFormatter(gssFocusGuidelineProps.dateFormat);
    }

    gssHoverProps = view.getGssProperties(pointElement, "hover");

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

  public int getFocusDimension() {
    return focusDimension;
  }

  public int setFocusDimension(int focusDimension) {
    return this.focusDimension = focusDimension;
  }

  public int[] getLegendEntries(Dataset dataset) {
    return getPassOrder(dataset);
  }

  public double getRangeValue(Tuple2D tuple, int dimension) {
    return tuple.getRange0();
  }

  public int[] getPassOrder(Dataset<T> dataSet) {
    return new int[]{0};
  }
}
