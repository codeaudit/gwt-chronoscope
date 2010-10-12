package org.timepedia.chronoscopesamples.client;

import org.timepedia.chronoscope.client.ChronoscopeOptions;
import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.browser.json.GwtJsonDataset;
import org.timepedia.chronoscope.client.browser.json.JsonDatasetJSO;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.client.overlays.OverlayClickListener;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

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
      ChronoscopeOptions.setErrorReporting(true);
      Chronoscope.setMicroformatsEnabled(false);
      Chronoscope.initialize();

      Chronoscope chronoscope = Chronoscope.getInstance();

      final Datasets<Tuple2D> datasets = new Datasets<Tuple2D>();
      datasets.add(chronoscope.getDatasetReader().createDatasetFromJson(new GwtJsonDataset(getJson("interestRates01"))));
      datasets.add(chronoscope.getDatasetReader().createDatasetFromJson(new GwtJsonDataset(getJson("interestRates02"))));

//      MockDatasetFactory datasetFactory = new MockDatasetFactory();
//      Dataset mockDataset = datasetFactory.getBasicDataset();
//      datasets.add(mockDataset);

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

      
      FocusPanel p = new FocusPanel();
      VerticalPanel v = new VerticalPanel();
      p.add(v);
      RootPanel.get().add(p);

      final HTML h = new HTML("adsfffffffffffffffffffffff"); 
      v.add(h);

      p.addFocusHandler(new FocusHandler() {
        public void onFocus(FocusEvent event) {
          h.setText("focus");
        }
      });
      
      p.addKeyPressHandler(new KeyPressHandler() {
        public void onKeyPress(KeyPressEvent event) {
          h.setText("" + (cursor++));
        }
      });
      
      v.add(chartPanel);
      
      } catch (Exception e) {
      e.printStackTrace();
    }
  }

  int cursor = 0;
  
}
