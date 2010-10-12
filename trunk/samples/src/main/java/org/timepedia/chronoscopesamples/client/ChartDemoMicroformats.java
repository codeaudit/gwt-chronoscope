package org.timepedia.chronoscopesamples.client;

import org.timepedia.chronoscope.client.browser.Chronoscope;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

public class ChartDemoMicroformats implements EntryPoint {

  public void onModuleLoad() {
    Chronoscope.setMicroformatsEnabled(true);
    Chronoscope.initialize();
    
    Button b = new Button("click");
    b.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        Chronoscope.initialize();
      }
    });
    
    RootPanel.get().add(b);
    
  }

}
