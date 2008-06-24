package org.timepedia.chronoscope.gviz.api.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 *
 */
@ExportPackage("chronoscope")
public class ChronoscopeVisualization implements Exportable {

  private Element element;

  @Export("Visualization")
  public ChronoscopeVisualization(Element element) {
    this.element = element;
  }

  @Export
  public void draw(DataTable table, JavaScriptObject options) {
    try {
      XYDataset ds[] = DataTableParser.parseDatasets(table);
      final Marker ms[] = DataTableParser.parseMarkers(table);

      ChartPanel cp = Chronoscope.createTimeseriesChart(ds,
          Window.getClientWidth(), Window.getClientHeight());
      cp.setGssContext(new GVizGssContext());
      cp.setReadyListener(new ViewReadyCallback() {
        public void onViewReady(View view) {
//                      view.getChart().getPlot().setOverviewEnabled(prefs.overviewEnabled().getValue());
//                      view.getChart().getPlot().setLegendEnabled(prefs.legendEnabled().getValue());
          for (Marker m : ms) {
            view.getChart().getPlot().addOverlay(m);
          }
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
