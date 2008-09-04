package org.timepedia.chronoscopesamples.client;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.IncrementalCommand;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.browser.JSONDataset;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.data.MockXYDataset;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.client.overlays.OverlayClickListener;

import java.util.Arrays;

/**
 * @author Ray Cromwell <ray@timepedia.org>
 */
public class ChartDemo implements EntryPoint {

  private static final String TIMEPEDIA_FONTBOOK_SERVICE
      = "http://api.timepedia.org/fr";

  private static volatile double GOLDEN__RATIO = 1.618;

  private FlexTable benchTable;

  private static native JSONDataset getJson(String varName) /*-{
       return $wnd[varName];   
    }-*/;

  public void onModuleLoad() {

    try {
// you must specify the chart dimensions for now, rather than have the chart grow to fill its container
      double GR = 1.618;
      int chartWidth = 450;
      int chartHeight = (int) (chartWidth / GOLDEN__RATIO);
      // Chronoscope.enableHistorySupport(true);
      Chronoscope.setFontBookRendering(true);
      Chronoscope.setErrorReporting(true);
      Chronoscope.setMicroformatsEnabled(true);
      Chronoscope.initialize();

      TabPanel vp = new TabPanel();
      final XYDataset[] ds = new XYDataset[2];
      ds[0] = Chronoscope.createXYDataset(getJson("unratedata"));
      ds[1] = new MockXYDataset();
      final ChartPanel chartPanel = Chronoscope
          .createTimeseriesChart(ds, chartWidth, chartHeight);
      chartPanel.setReadyListener(new ViewReadyCallback() {
        public void onViewReady(final View view) {
          final Marker m = new Marker(
              (ds[0].getDomainBegin() + ds[0].getDomainEnd()) / 2, 10, "A", 0);
          m.addOverlayClickListener(new OverlayClickListener() {
            public void onOverlayClick(Overlay overlay, int x, int y) {
              m.openInfoWindow("Hello");
            }
          });
          view.getChart().getPlot().addOverlay(m);
          view.getChart().getPlot().redraw();
         
        }
      });


      RootPanel.get("chartdemo").add(chartPanel);

      //currently, because of design issues in the initialization process,
     
    } catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  
}
