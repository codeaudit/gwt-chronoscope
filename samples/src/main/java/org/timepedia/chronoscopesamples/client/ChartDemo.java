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
//      Window.alert("ChartHeight = "+chartHeight);
      // Chronoscope.enableHistorySupport(true);
      Chronoscope.setFontBookRendering(true);
      Chronoscope.setErrorReporting(true);
      Chronoscope.setMicroformatsEnabled(false);
      Chronoscope.initialize();

      TabPanel vp = new TabPanel();
//    VerticalPanel vp = new VerticalPanel();
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
          Button bench = new Button("Bench");
          RootPanel.get().add(bench);
          bench.addClickListener(new ClickListener() {

            public void onClick(Widget sender) {
              benchMark(view);
            }
          });
        }
      });

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
//      final PlotPanel chartPanel3 = Chronoscope
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
////        PlotPanel chartPanel2 = Chronoscope.createTimeseriesChart(ds2, chartWidth, chartHeight);
////        vp.add(chartPanel2, "Two XYDataSources on separtate axes");
//
////        XYDataset[] ds4 = new XYDataset[1];
////        ds4[0] = Chronoscope.createXYDataset(getJson("unratedata"));
////        final PlotPanel chartPanel4 = Chronoscope.createTimeseriesChart(ds4, chartWidth, chartHeight);
////        Marker marker = new Marker("1975/10/10", 0, "A");
////        marker.addOverlayClickListener(new OverlayClickListener() {
////            public void onOverlayClick(Overlay overlay, int x, int y) {
////                ( (Marker) overlay ).createInfoWindow("You clicked on 'A'");
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
//              ((Marker) overlay).createInfoWindow("You clicked on 'A'");
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

  private void benchMark(final View view) {
    final double dO = view.getChart().getPlot().getDomainOrigin();
    final double cD = view.getChart().getPlot().getCurrentDomain();
    final double dC = dO + cD / 2;
    final int lim = GWT.isScript() ? 100 : 5;
    final int numTrials = 10;
    final double trials[] = new double[numTrials];
    if (benchTable == null) {
      benchTable = new FlexTable();
      RootPanel.get().add(benchTable);
    }
    benchTable.clear();
    benchTable.getFlexCellFormatter().setColSpan(0, 0, 2);
    benchTable
        .setWidget(0, 0, new HTML("Trial Results (Trials=" + numTrials + ")"));
    DeferredCommand.addCommand(new IncrementalCommand() {
      int trialNum = 0;

      int frameNum = 0;

      double curTime = 0;

      public boolean execute() {

        if (frameNum < lim) {
          double ncd = cD - cD / 1.5 * ((double) frameNum / lim);
          double ndo = dC - ncd / 2;
          view.getChart().getPlot().setDomainOrigin(ndo);
          view.getChart().getPlot().setCurrentDomain(ncd);
          double start = Duration.currentTimeMillis();
          view.getChart().redraw();
          double end = Duration.currentTimeMillis();
          curTime += end - start;
          frameNum++;
          return true;
        }
        frameNum = 0;
        if (trialNum < numTrials - 1) {

          trials[trialNum] = curTime;
          frameNum = 0;
          trialNum++;
          benchTable.setWidget(trialNum, 0, new HTML("Trial " + trialNum));
          benchTable.setWidget(trialNum, 1, new HTML(curTime + "ms"));
          curTime = 0;

          return true;
        } else {
          Arrays.sort(trials);
          double mean = 0;
          for (int i = 1; i < trials.length - 1; i++) {
            mean += trials[i];
          }
          mean /= trials.length - 2;
          double stddev = 0;
          for (int i = 1; i < trials.length - 1; i++) {
            double x = trials[i] - mean;
            stddev += x * x;
          }
          stddev /= trials.length - 2;
          stddev = Math.sqrt(stddev);
          benchTable.setWidget(trialNum + 1, 0, new HTML("Summary"));
          benchTable.setWidget(trialNum + 1, 1,
              new HTML("Mean: " + mean + " stddev: " + stddev));
          benchTable.setWidget(trialNum + 2, 0, new HTML("Estimated FPS"));

          benchTable.setWidget(trialNum + 2, 1,
              new HTML((double) lim / (mean / 1000) + " frames per second"));
          Image report = new Image(
              "http://api.timepedia.org/widget/clear.cache.gif?rev=r320&mean="
                  + mean + "&stddev=" + stddev);
          RootPanel.get().add(report);
          return false;
        }
      }
    });
  }
}
