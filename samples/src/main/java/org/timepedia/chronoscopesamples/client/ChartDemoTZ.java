package org.timepedia.chronoscopesamples.client;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.browser.json.JsonDatasetJSO;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.data.IncrementalDataResponse;
import org.timepedia.chronoscope.client.data.IncrementalHandler;
import org.timepedia.chronoscope.client.util.Interval;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author Manolo Carrasco <manolo@timepedia.org>
 */
public class ChartDemoTZ implements EntryPoint {

  public static native JsArray<JsonDatasetJSO> getJsons(String a) /*-{
     return $wnd[a];
  }-*/;
  
  
  XYPlot theplot;
  
  public void onModuleLoad() {
    
    Chronoscope chronoscope = Chronoscope.getInstance();
    Dataset[] datasets = chronoscope.createDatasets(getJsons("odd_display_jagged"));
    
    ChartPanel chartPanel = Chronoscope.createTimeseriesChart(datasets, 480, 320);
    chartPanel.setReadyListener(new ViewReadyCallback() {
      public void onViewReady(View view) {
        theplot = view.getChart().getPlot(); 
        theplot.getRangeAxis(0).setAutoZoomVisibleRange(true);
        
        Dataset d = theplot.getDatasets().get(0);
        d.setIncrementalHandler(new IncrementalHandler() {
          
          public void onDataNeeded(Interval region, Dataset dataset, IncrementalDataResponse response) {
            
            if (region.length() < 86400000) {
              
            }
            
          }
        });
        
      }
    });
    
    Button b1 = new Button("offset tz back", new ClickHandler() {
      public void onClick(ClickEvent event) {
        System.out.println(theplot);
        theplot.setTimeZoneOffsetUTC(-5);
      }
    });
    Button b2 = new Button("offset tz forward", new ClickHandler() {
      public void onClick(ClickEvent event) {
        theplot.setTimeZoneOffsetUTC(3);
      }
    });
    Button b3 = new Button("offset tz same", new ClickHandler() {
      public void onClick(ClickEvent event) {
        theplot.setTimeZoneOffsetUTC(0);
      }
    });
    Button b4 = new Button("local offset 0 (UTC)", new ClickHandler() {
      public void onClick(ClickEvent event) {
        theplot.setTimeZoneOffsetBrowserLocal(0);
      }
    });
    Button b5 = new Button("local offset -1", new ClickHandler() {
      public void onClick(ClickEvent event) {
        theplot.setTimeZoneOffsetBrowserLocal(-1);
      }
    });
    RootPanel.get().add(b1);
    RootPanel.get().add(b2);
    RootPanel.get().add(b3);
    RootPanel.get().add(b4);
    RootPanel.get().add(b5);
    RootPanel.get().add(chartPanel);
  }
}
