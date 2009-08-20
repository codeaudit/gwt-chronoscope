package org.timepedia.chronoscope.client.browser.event;

import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.Event;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;

import org.timepedia.chronoscope.client.Chart;

/**
 * Handles the event where a character is generated from a key press (either
 * directory or through auto-repeat).
 *
 * @author Chad Takahashi
 */
public class ChartKeyPressHandler
    extends AbstractEventHandler<KeyPressHandler>
    implements KeyPressHandler {

  static final int KEY_S = 83 + 32;

  static final int KEY_X = 88 + 32;

  static final int KEY_Z = 90 + 32;

  public void onKeyPress(KeyPressEvent event) {
    ChartState chartInfo = getChartState(event);

    Chart chart = chartInfo.chart;

    int keyCode = event.getCharCode();
    boolean handled = true;
    if (keyCode == KEY_TAB) {
      handled = handleTabKey((Event)event.getNativeEvent(), chartInfo, keyCode, event.isShiftKeyDown());
    } else if (keyCode == KEY_Z) {
      chart.nextZoom();
    } else if (keyCode == KEY_X) {
      chart.prevZoom();
    } else if (keyCode == KeyboardListener.KEY_ENTER) {
      chart.maxZoomToFocus();
    } else {
      handled = false;
    }

    chartInfo.setHandled(handled);
  }

}
