package org.timepedia.chronoscope.client.browser;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.browser.event.ChartState;
import org.timepedia.chronoscope.client.browser.event.ClientEventHandler;
import org.timepedia.chronoscope.client.browser.event.HandlerLookup;
import org.timepedia.chronoscope.client.browser.event.KeyDownHandler;
import org.timepedia.chronoscope.client.browser.event.KeyPressHandler;
import org.timepedia.chronoscope.client.browser.event.KeyUpHandler;
import org.timepedia.chronoscope.client.browser.event.MouseClickHandler;
import org.timepedia.chronoscope.client.browser.event.MouseDblClickHandler;
import org.timepedia.chronoscope.client.browser.event.MouseDownHandler;
import org.timepedia.chronoscope.client.browser.event.MouseMoveHandler;
import org.timepedia.chronoscope.client.browser.event.MouseOutHandler;
import org.timepedia.chronoscope.client.browser.event.MouseOverHandler;
import org.timepedia.chronoscope.client.browser.event.MouseUpHandler;
import org.timepedia.chronoscope.client.browser.event.MouseWheelHandler;

/**
 * 
 */
public class ChartEventHandler {

  private HandlerLookup handlerLookup;

  private ChartState chartInfo;

  public ChartEventHandler() {
    // Stores information about the chart that's needed by the client event
    // handlers.
    chartInfo = new ChartState();
    chartInfo.tabKeyEventCode = this.getTabKeyEventType(); // browser-specific

    // Register client event handlers
    handlerLookup = new HandlerLookup();
    handlerLookup.addHandler(Event.ONMOUSEDOWN, new MouseDownHandler());
    handlerLookup.addHandler(Event.ONMOUSEUP, new MouseUpHandler());
    handlerLookup.addHandler(Event.ONMOUSEOUT, new MouseOutHandler());
    handlerLookup.addHandler(Event.ONMOUSEOVER, new MouseOverHandler());
    handlerLookup.addHandler(Event.ONMOUSEMOVE, new MouseMoveHandler());
    handlerLookup.addHandler(Event.ONMOUSEWHEEL, new MouseWheelHandler());
    handlerLookup.addHandler(Event.ONCLICK, new MouseClickHandler());
    handlerLookup.addHandler(Event.ONDBLCLICK, new MouseDblClickHandler());
    handlerLookup.addHandler(Event.ONKEYDOWN, new KeyDownHandler());
    handlerLookup.addHandler(Event.ONKEYUP, new KeyUpHandler());
    handlerLookup.addHandler(Event.ONKEYPRESS, new KeyPressHandler());
  }

  public boolean handleChartEvent(Event event, Chart chart, int x, int y) {
    int eventKey = DOM.eventGetType(event);
    ClientEventHandler handler = this.handlerLookup.getHandler(eventKey);
    chartInfo.chart = chart;

    boolean wasHandled = false;
    if (handler != null) {
      wasHandled = handler.handle(event, x, y, chartInfo);
    }
    return wasHandled;
  }

  /**
   * Safari and IE use KEYDOWN for TAB, FF uses KEYPRESS
   */
  public int getTabKeyEventType() {
    return Event.ONKEYDOWN;
  }
}
