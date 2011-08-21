package org.timepedia.chronoscope.gviz.api.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Focus;
import org.timepedia.chronoscope.client.InfoWindow;
import org.timepedia.chronoscope.client.InfoWindowClosedHandler;
import org.timepedia.chronoscope.client.InfoWindowEvent;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.browser.JavascriptHelper;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.event.PlotFocusEvent;
import org.timepedia.chronoscope.client.event.PlotFocusHandler;
import org.timepedia.chronoscope.client.event.PlotHoverEvent;
import org.timepedia.chronoscope.client.event.PlotHoverHandler;
import org.timepedia.chronoscope.client.event.PlotMovedEvent;
import org.timepedia.chronoscope.client.event.PlotMovedHandler;
import org.timepedia.chronoscope.client.gss.DefaultGssContext;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExporterUtil;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@ExportPackage("chronoscope")
public class ChronoscopeVisualization implements Exportable {

  private static int vizCount = 0;

  /**
   * This static method parses an HTML table found in the current document and returns a GViz DataTable. 
   * If the table does not correctly adhere to the Chronoscope Microformat syntax, an exception will be 
   * thrown with details as to where parsing fails.
   * 
   * <i>tableId</i> is the ID attribute of an HTML table formatted according to the Chronoscope Microformat syntax.
   * 
   */
  @Export
  public static DataTable microformatToDataTable(String tableId) {
    DataTable table = DataTableParser.parseMicroformatIntoDataTable(tableId);
    return table;
  }

  Map<Integer, Integer> dataset2Column = new HashMap<Integer, Integer>();

  private Element element;

  private ChartPanel cp;

  private boolean dontfire;

  @Export("Visualization")
  public ChronoscopeVisualization(Element element) {
    this.element = element;
  }

  @Export
  public void zoomIn() {
    dontfire = true;
    cp.getChart().nextZoom();
  }

  @Export
  public void zoomOut() {
    dontfire = true;
    cp.getChart().prevZoom();
  }

  @Export
  public void pageRight() {
    dontfire = true;
    cp.getChart().pageRight(1.0);
  }


  @Export
  public void pageRight(double amt) {
    dontfire = true;
    cp.getChart().pageRight(MathUtil.bound(amt, 0.0, 1.0));
  }

  @Export
  public void pageLeft() {
    dontfire = true;
    cp.getChart().pageRight(1.0);
  }

  @Export
  public void pageLeft(double amt) {
    dontfire = true;
    cp.getChart().pageLeft(MathUtil.bound(amt, 0.0, 1.0));
  }

  @Export
  public void highlight(double from, double to) {
//    dontfire = true;
    cp.getChart().getPlot().setHighlight(from, to);
    cp.getChart().redraw();
  }

  @Export
  public void draw(final DataTable table, JavaScriptObject options) {
    String id = element.getId();
    if (id == null || "".equals(id)) {
      id = "__viz" + vizCount++;
      element.setId(id);
    }

    try {
      final Properties opts = options.cast();
      String style = opts.get("style");
      final double domainOrigin = getDate(opts, "zoomStartTime");
      final double endDomain = getDate(opts, "zoomEndTime");
      final String scaleType = opts.get("scaleType");

      GssContext gssContext = new DefaultGssContext();

      if (style != null && !"".equals(style)) {
        GVizAPIStyle gstyle = null;
        try {
          gstyle = GVizAPIStyle.valueOf(style.toUpperCase());
        } catch (IllegalArgumentException e) {
          throw new RuntimeException("Unknown Style " + style);
        }
        gssContext = gstyle.getGssContext();
      }

      final Dataset ds[] = DataTableParser.parseDatasets(table, dataset2Column);
      final Marker ms[] = DataTableParser
          .parseMarkers(ExporterUtil.wrap(this), table, dataset2Column);

      cp = Chronoscope
          .createTimeseriesChart(ds, element.getPropertyInt("clientWidth"),
              element.getPropertyInt("clientHeight"));
      ((DefaultGssContext) gssContext)
          .setShowAxisLabels(!"false".equals(opts.get("axisLabels")));
      cp.setGssContext(gssContext);

      cp.setReadyListener(new ViewReadyCallback() {
        public void onViewReady(View view) {
          
          cp.getChart().getPlot().setOverviewEnabled(
              !"false".equals(opts.get("overview")));
          cp.getChart().getPlot().setLegendEnabled(
              !"false".equals(opts.get("legend")));

          for (Marker m : ms) {
            view.getChart().getPlot().addOverlay(m);
          }

          if (!Double.isNaN(domainOrigin) && !Double.isNaN(endDomain)) {
            Interval plotDomain = view.getChart().getPlot().getDomain();
            plotDomain.setEndpoints(domainOrigin, endDomain);
          }

          XYPlot plot = view.getChart().getPlot();
          plot.addPlotFocusHandler(new PlotFocusHandler() {
            public void onFocus(PlotFocusEvent event) {
              if (!dontfire) {
                GVizEventHelper
                    .trigger(ExporterUtil.wrap(ChronoscopeVisualization.this),
                        GVizEventHelper.SELECT_EVENT, null);
              }
              dontfire = false;
            }
          });
          plot.addPlotHoverHandler(new PlotHoverHandler() {
            public void onHover(PlotHoverEvent event) {
              try {
                GVizEventHelper
                .trigger(ExporterUtil.wrap(ChronoscopeVisualization.this),
                    GVizEventHelper.HOVER_EVENT,
                    wrapJSArray(event.getHoverPoints()));
              } catch (Exception e) {
                System.err.println("Exception when triggering Hover event: " + e.getMessage());
              }
            }

            private JavaScriptObject wrapJSArray(int[] hoverPoints) {
              JsArray arr = JsArray.createArray().cast();
              for (int i = 0; i < hoverPoints.length; i++) {
                arr.set(i, GVizEventHelper.point(dataset2Column.get(i),
                    hoverPoints[i]));
              }
              return arr;
            }
          });
          plot.addPlotMovedHandler(new PlotMovedHandler() {
            public void onMoved(PlotMovedEvent event) {
              GVizEventHelper
                  .trigger(ExporterUtil.wrap(ChronoscopeVisualization.this),
                      GVizEventHelper.RANGECHANGE_EVENT,
                      rangeProps(event.getDomain().getStart(),
                          event.getDomain().length(),
                          eventTypeToString(event.getMoveType())));
            }
          });

          view.getChart().reloadStyles();
          if ("maximum".equals(scaleType)) {
            for (int i = 0; i < ds.length; i++) {
              RangeAxis ax = view.getChart().getPlot().getRangeAxis(i);
              ax.setAutoZoomVisibleRange(true);
            }
          }

          view.getChart().redraw();
        }
      });

      RootPanel.get(id).add(cp);
    } catch (Exception e) {
      e.printStackTrace();
      RootPanel.get(id).add(new Label(
          "There was an error setting up the chart: " + e.getMessage()));
    }
  }

  private native JavaScriptObject rangeProps(double domainOrigin,
      double currentDomain, String eventType) /*-{
    return { start: new $wnd.Date(domainOrigin), end: new $wnd.Date(currentDomain-domainOrigin), event: eventType };
  }-*/;

  private String eventTypeToString(PlotMovedEvent.MoveType type) {
    switch (type) {
      case DRAGGED:
        return "dragged";
      case ZOOMED:
        return "zoomed";
      case PAGED:
        return "paged";
      case CENTERED:
        return "centered";
    }
    return "zoomed";
  }

  @Export
  public JavaScriptObject getSelection() {
    Focus focus = cp.getChart().getPlot().getFocus();
    if (focus == null) {
      return JavaScriptObject.createArray();
    }

    return GVizEventHelper
        .selection(dataset2Column.get(focus.getDatasetIndex()),
            focus.getPointIndex());
  }

  private InfoWindow currentInfoWindow = null;

  @Export
  public InfoWindow openInfoWindow(JavaScriptObject selection, String html) {
    if (currentInfoWindow != null) {
      currentInfoWindow.close();
    }
    Properties sel = selection.cast();
    int datasetIndex = -1;
    int pointIndex = sel.getInt("row");

    for (Map.Entry<Integer, Integer> e : dataset2Column.entrySet()) {
      if (e.getValue() == sel.getInt("col")) {
        datasetIndex = e.getKey();
      }
    }
    if (datasetIndex != -1) {
      XYPlot plot = cp.getChart().getPlot();
      double x = plot.getDatasets().get(datasetIndex).getX(pointIndex);
      Tuple2D tuple = plot.getDatasets().get(datasetIndex)
          .getFlyweightTuple(pointIndex);
      double y = tuple.getRange0();
      currentInfoWindow = plot.openInfoWindow(html, x, y, datasetIndex);
      currentInfoWindow
          .addInfoWindowClosedHandler(new InfoWindowClosedHandler() {
            public void onInfoWindowClosed(InfoWindowEvent event) {
              currentInfoWindow = null;
            }
          });
    }
    return currentInfoWindow;
  }

  @Export
  public void setSelection(JavaScriptObject selection) {
    Properties sel = JavascriptHelper.jsArrGet(selection, 0).cast();
    dontfire = true;

    for (Map.Entry<Integer, Integer> e : dataset2Column.entrySet()) {
      if (e.getValue() == sel.getInt("col")) {
        Focus focus = new Focus();
        focus.setDatasetIndex(e.getKey());
        focus.setPointIndex(sel.getInt("row"));
        cp.getChart().getPlot().setFocus(focus);
      }
    }
    ((DefaultXYPlot) cp.getChart().getPlot()).redraw(true);
  }

  final static class Properties extends JavaScriptObject {

    protected Properties() {
    }

    public native String get(String key) /*-{
       return this[key];
    }-*/;

    public native int getInt(String key) /*-{
       return this[key];
    }-*/;
  }

  private native double getDate(Properties opts, String field) /*-{
       var f = opts[field];
       if(!f) return 0/0;
       return f.getTime();
  }-*/;
}
