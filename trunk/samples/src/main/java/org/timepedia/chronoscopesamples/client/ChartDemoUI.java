package org.timepedia.chronoscopesamples.client;

import org.timepedia.chronoscope.client.ChronoscopeOptions;
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
import org.timepedia.chronoscopesamples.client.ui.ChronoUIExample;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author Manolo Carrasco <manolo@timepedia.org>
 */
public class ChartDemoUI implements EntryPoint {

  public void onModuleLoad() {
    ChronoUIExample c = new ChronoUIExample();
    RootPanel.get().add(c);

  }
}
