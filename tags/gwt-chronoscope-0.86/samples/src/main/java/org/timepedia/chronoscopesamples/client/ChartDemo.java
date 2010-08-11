package org.timepedia.chronoscopesamples.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.RootPanel;

import org.timepedia.chronoscope.client.ChronoscopeOptions;
import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.io.DatasetReader;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.browser.json.GwtJsonDataset;
import org.timepedia.chronoscope.client.browser.json.JsonDatasetJSO;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.data.mock.MockDatasetFactory;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.client.overlays.OverlayClickListener;

/**
 * @author Ray Cromwell <ray@timepedia.org>
 */
public class ChartDemo implements EntryPoint {

  private static final String TIMEPEDIA_FONTBOOK_SERVICE
      = "http://api.timepedia.org/fr";

  private static volatile double GOLDEN__RATIO = 1.618;

  private FlexTable benchTable;

  private static native JsonDatasetJSO getJson(String varName) /*-{
       return $wnd[varName];
    }-*/;

  public void onModuleLoad() {
    try {
      // You must specify the chart dimensions for now, rather than have the chart
      // grow to fill its container
      int chartWidth = 450;
      int chartHeight = (int) (chartWidth / GOLDEN__RATIO);

      // Chronoscope.enableHistorySupport(true);
      Chronoscope.setFontBookRendering(true);
      Chronoscope.setFontBookServiceEndpoint("fr");
      ChronoscopeOptions.setErrorReporting(true);
      Chronoscope.setMicroformatsEnabled(true);
      Chronoscope.initialize();

      Chronoscope chronoscope = Chronoscope.getInstance();

      final Datasets<Tuple2D> datasets = new Datasets<Tuple2D>();
      datasets.add(DatasetReader.createDatasetFromJson(new GwtJsonDataset(getJson("interestRates01"))));
      datasets.add(DatasetReader.createDatasetFromJson(new GwtJsonDataset(getJson("interestRates02"))));

      MockDatasetFactory datasetFactory = new MockDatasetFactory();
      Dataset mockDataset = datasetFactory.getBasicDataset();
      datasets.add(mockDataset);

      Dataset[] dsArray = datasets.toArray();

      final ChartPanel chartPanel = Chronoscope
          .createTimeseriesChart(dsArray, chartWidth, chartHeight);

      chartPanel.setReadyListener(new ViewReadyCallback() {
        public void onViewReady(final View view) {
          Dataset dataset = datasets.get(0);
          final Marker m = new Marker(
              dataset.getDomainExtrema().midpoint(), "A", 0);
          m.addOverlayClickListener(new OverlayClickListener() {
            public void onOverlayClick(Overlay overlay, int x, int y) {
              m.openInfoWindow("Hello");
            }
          });

          XYPlot plot = view.getChart().getPlot();
          //plot.setDatasetRenderer(1, new BarChartXYRenderer());
          plot.addOverlay(m);
          plot.redraw();
        }
      });


      RootPanel.get("chartdemo").add(chartPanel);

      //currently, because of design issues in the initialization process,
     
    } catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  
}
