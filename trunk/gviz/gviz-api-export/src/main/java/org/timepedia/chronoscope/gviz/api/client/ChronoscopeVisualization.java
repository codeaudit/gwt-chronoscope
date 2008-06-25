package org.timepedia.chronoscope.gviz.api.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.XYPlotListener;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.browser.JavascriptHelper;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import java.util.Map;
import java.util.HashMap;

import gwtquery.client.Properties;

/**
 *
 */
@ExportPackage("chronoscope")
public class ChronoscopeVisualization implements Exportable {

  private Element element;

  private ChartPanel cp;

  private boolean dontfire;

  @Export("Visualization")
  public ChronoscopeVisualization(Element element) {
    this.element = element;
  }

  @Export
  public JavaScriptObject getSelection() {
    return GVizEventHelper.selection(dataset2Column.get(cp.getChart().getPlot().getFocusSeries()),
        cp.getChart().getPlot().getFocusPoint());
  }

  @Export
  void setSelection(JavaScriptObject selection) {
    Properties sel = JavascriptHelper.jsArrGet(selection, 0).cast();
    dontfire = true;
    for(Map.Entry<Integer,Integer> e : dataset2Column.entrySet()) {
      if(e.getValue() == sel.getInt("col")) {
        cp.getChart().getPlot().setFocus(e.getKey(), sel.getInt("row"));
        
      }
    }
  }

  Map<Integer, Integer> dataset2Column = new HashMap<Integer, Integer>();
  
  @Export
  public void draw(final DataTable table, JavaScriptObject options) {
    try {
      final Properties opts = options.cast();

      XYDataset ds[] = DataTableParser.parseDatasets(table, dataset2Column);
      final Marker ms[] = DataTableParser.parseMarkers(table, dataset2Column);

      cp = Chronoscope.createTimeseriesChart(ds, Window.getClientWidth(),
          Window.getClientHeight());
      cp.setGssContext(new GVizGssContext());
      cp.setReadyListener(new ViewReadyCallback() {
        public void onViewReady(View view) {
          view.getChart().getPlot()
              .setOverviewEnabled(!"false".equals(opts.get("overview")));
          view.getChart().getPlot()
              .setLegendEnabled(!"false".equals(opts.get("legend")));
          for (Marker m : ms) {
            view.getChart().getPlot().addOverlay(m);
          }
          view.addViewListener(new XYPlotListener() {

            public void onContextMenu(int x, int y) {
              //To change body of implemented methods use File | Settings | File Templates.
            }

            public void onFocusPointChanged(XYPlot plot, int focusSeries,
                int focusPoint) {
              if (!dontfire) {
                GVizEventHelper
                    .trigger(table, GVizEventHelper.SELECT_EVENT, null);
              }
              dontfire = false;
            }

            public void onPlotMoved(XYPlot plot, double amt, int seriesNum,
                int type, boolean animated) {
              //To change body of implemented methods use File | Settings | File Templates.
            }
          });
          view.getChart().reloadStyles();
          view.getChart().redraw();
        }
      });
      RootPanel.get().add(cp);
    } catch (Throwable e) {
      RootPanel.get()
          .add(new Label("There was an error parsing the spreadsheet data."));
    }
  }
}
