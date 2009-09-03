package org.timepedia.chronoscopebench.client;

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
import com.google.gwt.user.client.ui.Widget;

import org.timepedia.chronoscope.client.About;
import org.timepedia.chronoscope.client.ChronoscopeOptions;
import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.io.DatasetReader;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.browser.json.JsonDatasetJSO;
import org.timepedia.chronoscope.client.browser.json.GwtJsonDataset;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.data.mock.MockDatasetFactory;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.client.overlays.OverlayClickListener;

import java.util.Arrays;

/**
 * @author Ray Cromwell <ray@timepedia.org>
 */
public class ChartBench implements EntryPoint {

  private static final String TIMEPEDIA_FONTBOOK_SERVICE
      = "http://api.timepedia.org/fr";

  private static volatile double GOLDEN__RATIO = 1.618;

  private FlexTable benchTable;
  
  private MockDatasetFactory mockDsFactory;
  
  private static native JsonDatasetJSO getJson(String varName) /*-{
       return $wnd[varName];   
    }-*/;

  public void onModuleLoad() {

    try {
      double GR = 1.618;
      int chartWidth = 450;
      int chartHeight = (int) (chartWidth / GOLDEN__RATIO);
      Chronoscope.setFontBookRendering(true);
      ChronoscopeOptions.setErrorReporting(true);
      Chronoscope.setMicroformatsEnabled(false);
      Chronoscope.initialize();
      mockDsFactory = new MockDatasetFactory(Chronoscope.get().getComponentFactory().getDatasetFactory());
      final Dataset[] ds = new Dataset[2];
      ds[0] = Chronoscope.get().getDatasetReader().createDatasetFromJson(new GwtJsonDataset(getJson("unratedata")));
      ds[1] = mockDsFactory.getBasicDataset();
      final ChartPanel chartPanel = Chronoscope
          .createTimeseriesChart(ds, chartWidth, chartHeight);
      chartPanel.setReadyListener(new ViewReadyCallback() {
        public void onViewReady(final View view) {
          final Marker m = new Marker(
              ds[0].getDomainExtrema().midpoint(), "A", 0);
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
      RootPanel.get("chartdemo").add(chartPanel);
    } catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  private void benchMark(final View view) {
    final XYPlot plot = view.getChart().getPlot();
    final Datasets datasets = plot.getDatasets();
    
    // Reset plot domain to 'max zoom'
    datasets.getDomainExtrema().copyTo(plot.getDomain());
    
    final double dO = plot.getDomain().getStart();
    final double cD = plot.getDomain().length();
    final double dC = dO + cD / 2;
    final int lim = GWT.isScript() ? 100 : 5;
    final int numTrials = 4;
    final double trialTimes[] = new double[numTrials];
    
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
      boolean doContinue = false;
      
      public boolean execute() {

        if (trialNum < numTrials) {
          if (frameNum < lim) {
            double ncd = cD - cD / 1.5 * ((double) frameNum / lim);
            double ndo = dC - ncd / 2;
            plot.getDomain().setEndpoints(ndo, ndo + ncd);
            double start = Duration.currentTimeMillis();
            view.getChart().redraw();
            double end = Duration.currentTimeMillis();
            curTime += end - start;
            frameNum++;
          }
          else {
            trialTimes[trialNum] = curTime;
            trialNum++;
            benchTable.setWidget(trialNum, 0, new HTML("Trial " + trialNum));
            benchTable.setWidget(trialNum, 1, new HTML(curTime + " ms"));
            frameNum = 0;
            curTime = 0;
          }
          
          doContinue = true;
        }
        else {
          Arrays.sort(trialTimes);
          double mean = 0;
          for (int i = 1; i < trialTimes.length - 1; i++) {
            mean += trialTimes[i];
          }
          mean /= trialTimes.length - 2;
          double stddev = 0;
          for (int i = 1; i < trialTimes.length - 1; i++) {
            double x = trialTimes[i] - mean;
            stddev += x * x;
          }
          stddev /= trialTimes.length - 2;
          stddev = Math.sqrt(stddev);
          benchTable.setWidget(trialNum + 1, 0, new HTML("Summary"));
          benchTable.setWidget(trialNum + 1, 1,
              new HTML("Mean: " + mean + " stddev: " + stddev));
          benchTable.setWidget(trialNum + 2, 0, new HTML("Estimated FPS"));

          benchTable.setWidget(trialNum + 2, 1,
              new HTML((double) lim / (mean / 1000) + " frames per second"));
          Image report = new Image(
              "http://api.timepedia.org/sense.gif?rev="
                  + About.getRevision() + "&mean=" + mean + "&stddev="
                  + stddev);
          RootPanel.get().add(report);
          
          doContinue = false;
        }
        
        return doContinue;
      }
    });
  }
}
