package org.timepedia.chronoscopesamples.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;

import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.browser.JSONDataset;
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
      
      final Datasets<Tuple2D,XYDataset<Tuple2D>> ds = new Datasets<Tuple2D,XYDataset<Tuple2D>>();
      ds.add(Chronoscope.createXYDataset(getJson("unratedata")));
      
      MockDatasetFactory datasetFactory = new MockDatasetFactory();
      XYDataset mockDataset = datasetFactory.getBasicDataset(); 
      ds.add(mockDataset);
      
      XYDataset[] dsArray = new XYDataset[ds.size()];
      for (int i = 0; i < ds.size(); i++) {
        dsArray[i] = (XYDataset)ds.get(i);
      }
      
      final ChartPanel chartPanel = Chronoscope
          .createTimeseriesChart(dsArray, chartWidth, chartHeight);
      chartPanel.setReadyListener(new ViewReadyCallback() {
        public void onViewReady(final View view) {
          XYDataset xyds = ds.get(0);
          final Marker m = new Marker(
              (xyds.getDomainBegin() + xyds.getDomainEnd()) / 2, "A", 0);
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
