package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.libideas.event.client.BrowserEvent;
import com.google.gwt.libideas.event.client.ClickEvent;
import com.google.gwt.libideas.event.client.DoubleClickEvent;
import com.google.gwt.libideas.event.client.KeyDownEvent;
import com.google.gwt.libideas.event.client.KeyPressedEvent;
import com.google.gwt.libideas.event.client.KeyUpEvent;
import com.google.gwt.libideas.event.client.MouseDownEvent;
import com.google.gwt.libideas.event.client.MouseMoveEvent;
import com.google.gwt.libideas.event.client.MouseOutEvent;
import com.google.gwt.libideas.event.client.MouseOverEvent;
import com.google.gwt.libideas.event.client.MouseUpEvent;
import com.google.gwt.libideas.event.client.MouseWheelEvent;
import com.google.gwt.libideas.event.shared.HandlerManager;
import com.google.gwt.user.client.Event;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.browser.event.ChartDblClickHandler;
import org.timepedia.chronoscope.client.browser.event.ChartKeyDownHandler;
import org.timepedia.chronoscope.client.browser.event.ChartKeyPressHandler;
import org.timepedia.chronoscope.client.browser.event.ChartKeyUpHandler;
import org.timepedia.chronoscope.client.browser.event.ChartMouseClickHandler;
import org.timepedia.chronoscope.client.browser.event.ChartMouseDownHandler;
import org.timepedia.chronoscope.client.browser.event.ChartMouseMoveHandler;
import org.timepedia.chronoscope.client.browser.event.ChartMouseOutHandler;
import org.timepedia.chronoscope.client.browser.event.ChartMouseOverHandler;
import org.timepedia.chronoscope.client.browser.event.ChartMouseUpHandler;
import org.timepedia.chronoscope.client.browser.event.ChartMouseWheelHandler;
import org.timepedia.chronoscope.client.browser.event.ChartState;
import org.timepedia.chronoscope.client.browser.event.OverviewAxisMouseMoveHandler;

/**
 *
 */
public class ChartEventHandler {

  private HandlerManager handlerLookup;

  private ChartState chartInfo;

  public ChartEventHandler() {
    // Stores information about the chart that's needed by the client event
    // handlers.
    chartInfo = new ChartState();
    chartInfo.setTabKeyEventCode(this.getTabKeyEventType()); // browser-specific

    // Register client event handlers
    handlerLookup = new HandlerManager(this);
    handlerLookup
        .addEventHandler(MouseDownEvent.KEY, new ChartMouseDownHandler());
    handlerLookup.addEventHandler(MouseUpEvent.KEY, new ChartMouseUpHandler());
    handlerLookup
        .addEventHandler(MouseOutEvent.KEY, new ChartMouseOutHandler());
    handlerLookup
        .addEventHandler(MouseOverEvent.KEY, new ChartMouseOverHandler());
    handlerLookup
        .addEventHandler(MouseMoveEvent.KEY, new ChartMouseMoveHandler());
    handlerLookup
        .addEventHandler(MouseMoveEvent.KEY, new OverviewAxisMouseMoveHandler());
    handlerLookup
        .addEventHandler(MouseWheelEvent.KEY, new ChartMouseWheelHandler());
    handlerLookup.addEventHandler(ClickEvent.KEY, new ChartMouseClickHandler());
    handlerLookup
        .addEventHandler(DoubleClickEvent.KEY, new ChartDblClickHandler());
    handlerLookup.addEventHandler(KeyDownEvent.KEY, new ChartKeyDownHandler());
    handlerLookup.addEventHandler(KeyUpEvent.KEY, new ChartKeyUpHandler());
    handlerLookup
        .addEventHandler(KeyPressedEvent.KEY, new ChartKeyPressHandler());
  }

  public boolean handleChartEvent(Event event, Chart chart, int x, int y) {
    chartInfo.chart = chart;
    chartInfo.setHandled(false);
    chartInfo.setLocalX(x);
    chartInfo.setLocalY(y);
    
    BrowserEvent browserEvent = getBrowserEvent(event);
    if (browserEvent != null) {
      browserEvent.setUserData(chartInfo);
      handlerLookup.fireEvent(browserEvent);
    } else {
      // shouldn't happen normally
      throw new RuntimeException("getBrowserEvent returned null");
    }
    return chartInfo.isHandled();
  }

  private BrowserEvent getBrowserEvent(Event event) {
    switch (event.getTypeInt()) {
      case Event.ONCLICK:
        return new ClickEvent(event);
      case Event.ONDBLCLICK:
        return new DoubleClickEvent(event);
      case Event.ONKEYDOWN:
        return new KeyDownEvent(event);
      case Event.ONKEYUP:
        return new KeyUpEvent(event);
      case Event.ONKEYPRESS:
        return new KeyPressedEvent(event);
      case Event.ONMOUSEDOWN:
        return new MouseDownEvent(event);
      case Event.ONMOUSEMOVE:
        return new MouseMoveEvent(event);
      case Event.ONMOUSEOUT:
        return new MouseOutEvent(event);
      case Event.ONMOUSEUP:
        return new MouseUpEvent(event);
      case Event.ONMOUSEWHEEL:
        return new MouseWheelEvent(event);
      case Event.ONMOUSEOVER:
        return new MouseOverEvent(event);
    }
    return null;
  }

  /**
   * Safari and IE use KEYDOWN for TAB, FF uses KEYPRESS
   */
  public int getTabKeyEventType() {
    return Event.ONKEYDOWN;
  }
}
