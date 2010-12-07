package org.timepedia.chronoscopesamples.client;

import java.util.Date;

import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.MutableDataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.browser.json.JsonDatasetJSO;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author Manolo Carrasco <manolo@timepedia.org>
 */
public class ChartDemoMutate implements EntryPoint {

  public static native JsArray<JsonDatasetJSO> getJsons(String a) /*-{
     return $wnd[a];
  }-*/;

  
  public static native JavaScriptObject getDomainVal(double d) /*-{
     return [d];
  }-*/;

  public static native JavaScriptObject getRangeVal(double a, double b, double c) /*-{
    return [[a],[b],[c]];
  }-*/;
  
  Chronoscope chrono;
  View theview;
  XYPlot<?> theplot;

  int maxyear = 1996;
  double r1 = 100;
  double r2 = 200;
  double r3 = 400;
  
  public double d(double value) { 
    return Math.floor(1.4 * value - 0.3 * value * Math.random());
  }
  
  public void  mutate() {
    Datasets<?> datasets= theplot.getDatasets();
    datasets.beginMutation();
    
    MutableDataset<?> dataset = (MutableDataset<?>)datasets.getById("jenga");
    
    int year = maxyear - (int)Math.floor(6 * Math.random());
    r1 = d(r1); 
    r2 = d(r2);
    r3 = d(r3);
    
    @SuppressWarnings("deprecation")
    JavaScriptObject domain_val =  getDomainVal(new Date(year, 0, 1).getTime() / 1000);
    JavaScriptObject range_val =  getRangeVal(r1, r2, r3);
    
    dataset.mutateArray(domain_val, range_val);
    datasets.endMutation();
    
    theview.getChart().redraw();
  }

  public void onModuleLoad() {
    Chronoscope.setErrorReporting(true);
    Chronoscope.setAnimationPreview(false);
    chrono = Chronoscope.getInstance();
    
    chrono.createTimeseriesChart("chartdemo", getJsons("__datasets"), 480, 320,
        new ViewReadyCallback() {
          public void onViewReady(View view) {
            ChartDemoMutate.this.theview = view;
            theplot = view.getChart().getPlot();
          }
        });
    
    Button b1 = new Button("Mutate", new ClickHandler() {
      public void onClick(ClickEvent event) {
        mutate();
      }
    });
    RootPanel.get("buttons").add(b1);
  }
}
