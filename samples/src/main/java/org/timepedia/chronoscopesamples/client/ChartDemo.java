package org.timepedia.chronoscopesamples.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.axis.TickLabelNumberFormatter;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.data.AppendableXYDataset;
import org.timepedia.chronoscope.client.data.RangeMutableXYDataset;
import org.timepedia.chronoscope.client.data.MockXYDataset;
import org.timepedia.chronoscope.client.overlays.DomainBarMarker;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.client.overlays.OverlayClickListener;
import org.timepedia.chronoscope.client.overlays.RangeBarMarker;

/**
 * @author Ray Cromwell <ray@timepedia.org>
 */
public class ChartDemo implements EntryPoint {

  private static final String TIMEPEDIA_FONTBOOK_SERVICE
      = "http://api.timepedia.org/fr";

  private static volatile double GOLDEN__RATIO = 1.618;

  private static native JavaScriptObject getJson(String varName) /*-{
       return $wnd[varName];   
    }-*/;

  public void onModuleLoad() {

    try {
// you must specify the chart dimensions for now, rather than have the chart grow to fill its container
      double GR = 1.618;
      int chartWidth = 1024;
      int chartHeight = (int) (chartWidth / GOLDEN__RATIO);
      Window.alert("ChartHeight = "+chartHeight);
      // Chronoscope.enableHistorySupport(true);
      Chronoscope.setFontBookRendering(true);
      Chronoscope.setErrorReporting(false);
      Chronoscope.setMicroformatsEnabled(true);
      Chronoscope.initialize();

      TabPanel vp = new TabPanel();
//    VerticalPanel vp = new VerticalPanel();
      final XYDataset[] ds = new XYDataset[1];
      ds[0] = Chronoscope.createXYDataset(getJson("ibm"));
//        ds[1]=new MockXYDataset();
      final ChartPanel chartPanel = Chronoscope
          .createTimeseriesChart(ds, chartWidth, chartHeight);

//      VerticalPanel vert = new VerticalPanel();
//      vp.add(new HTML("Hello World"), "Hello World");
//      HorizontalPanel hp = new HorizontalPanel();
//
//      final TextBox tb = new TextBox();
//      final Label l = new Label("Num Points: " + ds[0].getNumSamples());
//      hp.add(l);
//      hp.add(tb);
//      Button b = new Button("Mutate Range to Random Value");
//      b.addClickListener(new ClickListener() {
//  
//        public void onClick(Widget sender) {
//          RangeMutableXYDataset rxy = (RangeMutableXYDataset) ds[0];
//          rxy.beginUpdate();
//          rxy.setY(Integer.parseInt(tb.getText()),
//              Math.random() * (ds[0].getRangeTop() - ds[0].getRangeBottom()));
//          rxy.endUpdate();
//        }
//      });
//      hp.add(b);
//      vert.add(chartPanel);
//      vert.add(hp);
//      vp.add(vert, "Mutable XYDatasSource");
//
//      vp.selectTab(0);
//      XYDataset ds2[] = new XYDataset[1];
//      ds2[0] = Chronoscope.createXYDataset(getJson("unratedata"));
//      final ChartPanel chartPanel3 = Chronoscope
//          .createTimeseriesChart(ds2, 400, 300);
//      chartPanel3.setReadyListener(new ViewReadyCallback() {
//        public void onViewReady(View view) {
//          RangeAxis ra = view.getChart().getPlot().getRangeAxis(0);
//          ra.setTickLabelNumberFormatter(new TickLabelNumberFormatter() {
//            public String format(double value) {
//              return "FOO:"+value;
//            }
//          });
//        }
//      });
//      vp.add(chartPanel3, "Chart 3");
//
////     vp.add(chartPanel);
//
////        XYDataset[] ds2 = new XYDataset[2];
////        ds2[0] = Chronoscope.createXYDataset(getJson("unratedata"));
////        ds2[1] = Chronoscope.createXYDataset(getJson("dffdata"));
////        ChartPanel chartPanel2 = Chronoscope.createTimeseriesChart(ds2, chartWidth, chartHeight);
////        vp.add(chartPanel2, "Two XYDataSources on separtate axes");
//
////        XYDataset[] ds4 = new XYDataset[1];
////        ds4[0] = Chronoscope.createXYDataset(getJson("unratedata"));
////        final ChartPanel chartPanel4 = Chronoscope.createTimeseriesChart(ds4, chartWidth, chartHeight);
////        Marker marker = new Marker("1975/10/10", 0, "A");
////        marker.addOverlayClickListener(new OverlayClickListener() {
////            public void onOverlayClick(Overlay overlay, int x, int y) {
////                ( (Marker) overlay ).openInfoWindow("You clicked on 'A'");
////            }
//
////        });
////        vp.add(chartPanel4, "Markers");
//      chartPanel.setReadyListener(new ViewReadyCallback() {
//        public void onViewReady(final View view) {
//  //                view.getChart().getPlot().setRenderer(0, new BarChartXYRenderer(0));
//          Marker marker = new Marker("1977/10/10", 0, "A");
//          marker.addOverlayClickListener(new OverlayClickListener() {
//            public void onOverlayClick(Overlay overlay, int x, int y) {
//              ((Marker) overlay).openInfoWindow("You clicked on 'A'");
//            }
//          });
//          view.getChart().getPlot().addOverlay(marker);
//          DomainBarMarker dm = new DomainBarMarker("1979/01/01", "1980/01/01",
//              "1979");
//          view.getChart().getPlot().addOverlay(dm);
//          RangeBarMarker rm = new RangeBarMarker(2.0, 4.0, "Foobar");
//          view.getChart().getPlot().addOverlay(rm);
//  //                Date start = new Date();
//  //                if(GWT.isScript()) {
//  //                for(int i=0; i<25; i++) {
//  //                    view.getChart().redraw();
//  //                }
//  //                Date end = new Date();
//  //                Window.alert("Time taken for 25 redraws() = "+(end.getTime()-start.getTime()));
//  //                }
//          view.getChart().redraw();
//          Timer t = new Timer() {
//            int count = 0;
//  
//            public void run() {
//              AppendableXYDataset mxy = (AppendableXYDataset) ds[0];
//              mxy.beginUpdate();
//              mxy.insertXY(mxy.getX(mxy.getNumSamples() - 1) + 86400 * 1000
//                  + Math.random() * 5 * 86400 * 1000,
//                  //        count++
//                  Math.random() * (mxy.getRangeTop() - mxy.getRangeBottom()));
//              mxy.endUpdate();
//              l.setText("Num Points: "+ds[0].getNumSamples());
//            }
//          };
//        //  t.scheduleRepeating(500);
//        }
//      });
//
//      RootPanel.get("chartdemo").add(vp);
      RootPanel.get("chartdemo").add(chartPanel);

      //currently, because of design issues in the initialization process,
      // these must come after attachment

//        chartPanel2.getChart().getPlot().setOverviewEnabled(false);
//
//        chartPanel4.getChart().getPlot().addOverlay(marker);
//        chartPanel4.getChart().getPlot().addOverlay(new RangeBarMarker(4.0, 6.0, "Desired Range"));
//        chartPanel4.getChart().getPlot().addOverlay(new DomainBarMarker("1970/1/1", "1979/12/31", "The 70s"));
//        chartPanel4.getChart().redraw();
//        vp.selectTab(0);

      // hack to fix current bug when chart is hidden with display:none in a tab panel
//        vp.addTabListener(new TabListener() {
//            public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
//                chartPanel4.getChart().redraw();
//                return true;
//            }

//            public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
//
//            }
//        });
    } catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }
}
