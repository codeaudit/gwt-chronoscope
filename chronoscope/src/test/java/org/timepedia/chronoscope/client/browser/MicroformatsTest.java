package org.timepedia.chronoscope.client.browser;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.Command;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.XYDataset;

import java.util.Date;

/**
 * Test microformat import facility
 */
public class MicroformatsTest extends GWTTestCase {

  String microformatData = "<table id=\"microformatdemo\" class=\"cmf-chart\">\n"
      + "    <colgroup>\n"
      + "      <col class=\"cmf-dateformat\" title=\"yyyy\">\n"
      + "    </colgroup>\n" + "    <thead>\n" + "        <tr>\n"
      + "            <th>Time</th>\n" + "            <th>GDP</th>\n"
      + "        </tr>\n" + "    </thead>\n" + "    <tbody>\n"
      + "        <tr>\n" + "            <td>1953</td>\n"
      + "            <td>1000</td>\n" + "        </tr>\n" + "        <tr>\n"
      + "            <td>1954</td>\n" + "            <td>3000</td>\n"
      + "        </tr>\n" + "        <tr>\n" + "            <td>1955</td>\n"
      + "            <td>3100</td>\n" + "        </tr>\n" + "        <tr>\n"
      + "            <td>1956</td>\n" + "            <td>3200</td>\n"
      + "        </tr>\n" + "        <tr>\n" + "            <td>1957</td>\n"
      + "            <td>3250</td>\n" + "        </tr>\n" + "        <tr>\n"
      + "            <td>1958</td>\n" + "            <td>3300</td>\n"
      + "        </tr>\n" + "        <tr>\n" + "            <td>1959</td>\n"
      + "            <td>3325</td>\n" + "        </tr>\n" + "        <tr>\n"
      + "            <td>1960</td>\n" + "            <td>1900</td>\n"
      + "        </tr>\n" + "        <tr>\n" + "            <td>1961</td>\n"
      + "            <td>1800</td>\n" + "        </tr>\n" + "        <tr>\n"
      + "            <td>1962</td>\n" + "            <td>2000</td>\n"
      + "        </tr>\n" + "        <tr>\n" + "            <td>1963</td>\n"
      + "            <td>2100</td>\n" + "        </tr>\n" + "        <tr>\n"
      + "            <td>1964</td>\n" + "            <td>2200</td>\n"
      + "        </tr>\n" + "    </tbody>\n" + "</table>";
  
  public String getModuleName() {
    return "org.timepedia.chronoscope.ChronoscopeTestSuite";
  }
  
  public void testCustomDateTimeFormat() {
     HTML data = new HTML(microformatData);
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
