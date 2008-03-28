package org.timepedia.chronoscope.client.browser;

import com.google.gwt.user.client.ui.RootPanel;

import org.timepedia.chronoscope.client.ChronoscopeTestCase;
import org.timepedia.chronoscope.client.Fixtures;
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.render.GssElementImpl;

/**
 * Test methods of ChartPanel
 */
public class ChartPanelTest extends ChronoscopeTestCase {

  public void testGssContextOverride() {
    ChartPanel cp = Chronoscope
        .createTimeseriesChart(Fixtures.getTestDataset(), 600, 400);
    final Color color = new Color("#ff0000");
    
    cp.setGssContext(new IEGssContext() {
      public GssProperties getProperties(GssElement gssElem, String pseudoElt) {
        if ("axes".equals(gssElem.getType())) {
          return new GssProperties() {
            {
              this.bgColor = color;
            }
          };
        }
        return super.getProperties(gssElem, pseudoElt);
      }
    });
    cp.setReadyListener(new ViewReadyCallback() {
      public void onViewReady(View view) {
        GssProperties props = view
            .getGssProperties(new GssElementImpl("axes", null), "");
        assertEquals(color.toString(), "#ff000");
        finishTest();
      }
    });
    delayTestFinish(15000);
    RootPanel.get().add(cp);
  }
}
