package org.timepedia.chronoscope.gviz.api.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import org.timepedia.chronoscope.client.Focus;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.XYPlotListener;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.gss.DefaultGssContext;
import org.timepedia.chronoscope.client.browser.PlotPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.browser.JavascriptHelper;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExporterUtil;

import java.util.HashMap;
import java.util.Map;

import gwtquery.client.Properties;

/**
 *
 */
@ExportPackage("chronoscope")
public class ChronoscopeVisualization implements Exportable {

  private static int vizCount = 0;

  @Export
  public static DataTable microformatToDataTable(String id) {
    DataTable table = DataTableParser.parseMicroformatIntoDataTable(id);
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
  public void pageRight(int amt) {
    dontfire = true;
    cp.getChart().pageRight(Math.max(0, Math.min(amt, 1)));
  }

  @Export
  public void pageLeft() {
    dontfire = true;
    cp.getChart().pageRight(1.0);
  }

  @Export
  public void pageLeft(int amt) {
    dontfire = true;
    cp.getChart().pageLeft(Math.max(0, Math.min(amt, 1)));
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

      final XYDataset ds[] = DataTableParser
          .parseDatasets(table, dataset2Column);
      final Marker ms[] = DataTableParser
          .parseMarkers(ExporterUtil.wrap(this), table, dataset2Column);

      cp = Chronoscope.createTimeseriesChart(ds,
          element.getPropertyInt("clientWidth"),
          element.getPropertyInt("clientHeight"));
      ((DefaultGssContext)gssContext).setShowAxisLabels(!"false".equals(opts.get("axisLabels")));
      cp.setGssContext(gssContext);
      cp.getChart().getPlot()
          .setOverviewEnabled(!"false".equals(opts.get("overview")));
      cp.getChart().getPlot()
          .setLegendEnabled(!"false".equals(opts.get("legend")));
      cp.setReadyListener(new ViewReadyCallback() {
        public void onViewReady(View view) {

          for (Marker m : ms) {
            view.getChart().getPlot().addOverlay(m);
          }
          if (!Double.isNaN(domainOrigin)) {
            view.getChart().getPlot().setDomainOrigin(domainOrigin);
          }
          if (!Double.isNaN(endDomain)) {
            view.getChart().getPlot().setCurrentDomain(
                endDomain - view.getChart().getPlot().getDomainOrigin());
          }

          view.addViewListener(new XYPlotListener() {

            public void onContextMenu(int x, int y) {
              //To change body of implemented methods use File | Settings | File Templates.
            }

            public void onFocusPointChanged(XYPlot plot, int focusSeries,
                int focusPoint) {
              if (!dontfire) {
                GVizEventHelper
                    .trigger(ExporterUtil.wrap(ChronoscopeVisualization.this),
                        GVizEventHelper.SELECT_EVENT, null);
              }
              dontfire = false;
            }

            public void onPlotMoved(XYPlot plot, double domainAmt, int type, boolean animated) {
              GVizEventHelper
                  .trigger(ExporterUtil.wrap(ChronoscopeVisualization.this),
                      GVizEventHelper.RANGECHANGE_EVENT, rangeProps(
                      plot.getDomainOrigin(), plot.getCurrentDomain(),
                      eventTypeToString(type)));
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
      RootPanel.get(id)
          .add(new Label(
              "There was an error setting up the chart: " + e.getMessage()));
    }
  }

  private native JavaScriptObject rangeProps(double domainOrigin,
      double currentDomain, String eventType) /*-{
    return { start: new $wnd.Date(domainOrigin), end: new $wnd.Date(currentDomain-domainorigin), event: eventType };
  }-*/;

  private String eventTypeToString(int type) {
    switch (type) {
      case XYPlotListener.DRAGGED:
        return "dragged";
      case XYPlotListener.ZOOMED:
        return "zoomed";
      case XYPlotListener.PAGED:
        return "paged";
      case XYPlotListener.CENTERED:
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

    return GVizEventHelper.selection(
        dataset2Column.get(focus.getDatasetIndex()), focus.getPointIndex());
  }

  @Export
  void setSelection(JavaScriptObject selection) {
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
    cp.getChart().redraw();
  }

  private native double getDate(Properties opts, String field) /*-{
       var f = opts[field];
       if(!f) return 0/0;
       return f.getTime();
  }-*/;
}
