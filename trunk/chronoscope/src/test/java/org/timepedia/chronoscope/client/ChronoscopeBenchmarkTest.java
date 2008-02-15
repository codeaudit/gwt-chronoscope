package org.timepedia.chronoscope.client;

import com.google.gwt.junit.client.Benchmark;
import com.google.gwt.user.client.ui.RootPanel;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.data.MockXYDataset;

/**
 * Test Redraw Speed
 */
public class ChronoscopeBenchmarkTest extends Benchmark {
    public String getModuleName() {
        return "org.timepedia.chronoscope.Chronoscope";
    }

    ChartPanel cp;
    public void beginChartRedraw() {
        cp = Chronoscope.createTimeseriesChart(new XYDataset[] { new MockXYDataset() },
        600, 453);
        RootPanel.get().add(cp);

    }
    public void testChartRedraw() {
        XYPlot p = cp.getChart().getPlot();
        for(int i=0; i<10000; i++) {
           p.redraw();
        }
    }
    
}
