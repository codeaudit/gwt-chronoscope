package org.timepedia.chronoscopesamples.client;

import java.util.Date;

import org.timepedia.chronoscope.client.ChronoscopeOptions;
import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.browser.json.JsonDatasetJSO;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author Manolo Carrasco <manolo@timepedia.org>
 */
public class ChartDemoSlowIE implements EntryPoint {

  Chronoscope chronoscope;
  ChartPanel chartPanel;
  String [] datasetNames = {"dash","dffdata", "odd_display_jagged",  "jagged", "dffdatamip"};
  Date d = new Date();
  int cont = 0;
  
  HorizontalPanel p = new HorizontalPanel();
  HTML[] h = {new HTML(), new HTML()};
  

  public static native JsArray<JsonDatasetJSO> getJsons(String a) /*-{
     return $wnd[a];
  }-*/;
  
  
  void l(int n, int t, String msg) {
    h[n].setHTML(h[n].getHTML() + "<br/>c" + t + " - " + msg);
  }
  
  void createChart(){
    final Date start = new Date();

    if (chartPanel != null) {
      chartPanel.removeFromParent();
    }

    ChronoscopeOptions.setErrorReporting(true);
    chronoscope = Chronoscope.getInstance();
    Chronoscope.initialize();

    final int n = cont ++ % datasetNames.length;
    String data_name = datasetNames[0];
    Dataset[] datasets = chronoscope.createDatasets(getJsons(data_name));
    chartPanel = Chronoscope.createTimeseriesChart(datasets, 450, 300);
    chartPanel.setReadyListener(new ViewReadyCallback() {
      public void onViewReady(View view) {
        Date end = new Date();
        l(1, n, "" + (end.getTime() - start.getTime()));
        view.getChart().getPlot().getRangeAxis(0).setAutoZoomVisibleRange(true);
      }
    });
    p.add(chartPanel);
  }
  
  void reuseChart(){
    Date start = new Date();
    if (chartPanel == null) {
      return;
    }
    
    final int n = cont ++ % datasetNames.length;
    String data_name = datasetNames[1];
    chronoscope.replaceDatasets(chartPanel, getJsons(data_name));
    Date end = new Date();
    l(0, n, "" + (end.getTime() - start.getTime()));
  }

  Button b1 = new Button("Create Chart", new ClickHandler() {
    public void onClick(ClickEvent arg0) {
      createChart();
    }
  });
  
  Button b2 = new Button("Reuse Chart", new ClickHandler() {
    public void onClick(ClickEvent arg0) {
      reuseChart();
    }
  });

  public void onModuleLoad() {
    RootPanel.get().add(b1);
    RootPanel.get().add(b2);
    p.add(h[1]);
    p.add(h[0]);
    RootPanel.get().add(p);
    p.setSpacing(6);
    createChart();
  }
}
