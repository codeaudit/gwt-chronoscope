package org.timepedia.chronoscope.client.browser;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.Command;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.Fixtures;

import java.util.Date;

/**
 * Test microformat import facility
 */
public class MicroformatsTest extends GWTTestCase {

  public String getModuleName() {
    return "org.timepedia.chronoscope.ChronoscopeTestSuite";
  }
  
  public void testCustomDateTimeFormat() {
     HTML data = new HTML(Fixtures.microformatData);
     RootPanel.get().add(data);
     delayTestFinish(15000);
     Chronoscope.setMicroformatsEnabled(true);
     Microformats.setMicroformatsReadyListener(new Command() {
       public void execute() {
         Chart c=Chronoscope.getChartById("microformatdemochrono");
         XYDataset xy = c.getPlot().getDataset(0);
         Date start = new Date(53, 0, 1, 0, 0, 0);
         Date end = new Date(64, 0, 1, 0, 0 ,0);
         assertEquals(start.getTime(), (long)xy.getX(0));
         assertEquals(end.getTime(), (long)xy.getX(xy.getNumSamples()-1));
         finishTest();
       }
     });
     Chronoscope.initialize();
  }
}
