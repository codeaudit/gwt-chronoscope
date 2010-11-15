package org.timepedia.chronoscopesamples.client;

import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.browser.json.JsonDatasetJSO;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author Manolo Carrasco <manolo@timepedia.org>
 */
public class ChartDemoLegend implements EntryPoint {

  public static native JsArray<JsonDatasetJSO> getJsons(String a) /*-{
     return $wnd[a];
  }-*/;
  
  Chronoscope chronoscope;
  Chronoscope chronoscope2;
  
  public void updateChart(String data)
  {
    chronoscope.createTimeseriesChart("chartdemo", getJsons(data), 223, 197, new ViewReadyCallback() {
      public void onViewReady(View view) {
      }
    });
    
    chronoscope2.createTimeseriesChart("chartdemobig", getJsons(data), 600, 500, new ViewReadyCallback() {
      public void onViewReady(View view) {
      }
    });
  }
  
  public void onModuleLoad() {
    Chronoscope.setErrorReporting(false);
    Chronoscope.setAnimationPreview(false);
    chronoscope = Chronoscope.getInstance();
    chronoscope2 = Chronoscope.getInstance();
    
    Button b1 = new Button("Chart Renders Empty", new ClickHandler() {
      public void onClick(ClickEvent event) {
        updateChart("blank_graph");
      }
    });
    Button b2 = new Button("Chart Renders With No Legend", new ClickHandler() {
      public void onClick(ClickEvent event) {
        updateChart("no_legend");
      }
    });
    Button b3 = new Button("Chart Renders With Overrun", new ClickHandler() {
      public void onClick(ClickEvent event) {
        updateChart("overrun");
      }
    });
    RootPanel.get("buttons").add(b1);
    RootPanel.get("buttons").add(b2);
    RootPanel.get("buttons").add(b3);
  }
}
