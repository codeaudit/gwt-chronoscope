package org.timepedia.chronoscope.client.browser;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.gen2.event.dom.client.DomEvent;
import com.google.gwt.gen2.event.dom.client.MouseOutEvent;
import com.google.gwt.gen2.event.dom.client.MouseOverEvent;
import com.google.gwt.gen2.event.dom.client.MouseWheelEvent;
import com.google.gwt.gen2.event.dom.client.MouseUpEvent;
import com.google.gwt.gen2.event.dom.client.MouseDownEvent;
import com.google.gwt.gen2.event.dom.client.KeyUpEvent;
import com.google.gwt.gen2.event.dom.client.DoubleClickEvent;
import com.google.gwt.gen2.event.dom.client.MouseMoveEvent;
import com.google.gwt.gen2.event.dom.client.KeyDownEvent;
import com.google.gwt.gen2.event.dom.client.ClickEvent;
import com.google.gwt.gen2.event.dom.client.KeyPressEvent;
import com.google.gwt.gen2.event.shared.HandlerManager;

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

  private static ChartState chartInfo;

  public static ChartState getChartState() {
    return chartInfo;
  }

  public ChartEventHandler() {
    // Stores information about the chart that's needed by the client event
    // handlers.
    chartInfo = new ChartState();
    chartInfo.setTabKeyEventCode(this.getTabKeyEventType()); // browser-specific

    // Register client event handlers
    handlerLookup = new HandlerManager(this);
    handlerLookup
        .addHandler(MouseDownEvent.TYPE, new ChartMouseDownHandler());
    handlerLookup.addHandler(MouseUpEvent.TYPE, new ChartMouseUpHandler());
    handlerLookup
        .addHandler(MouseOutEvent.TYPE, new ChartMouseOutHandler());
    handlerLookup
        .addHandler(MouseOverEvent.TYPE, new ChartMouseOverHandler());
    handlerLookup
        .addHandler(MouseMoveEvent.TYPE, new ChartMouseMoveHandler());
    handlerLookup
        .addHandler(MouseMoveEvent.TYPE, new OverviewAxisMouseMoveHandler());
    handlerLookup
        .addHandler(MouseWheelEvent.TYPE, new ChartMouseWheelHandler());
    handlerLookup.addHandler(ClickEvent.TYPE, new ChartMouseClickHandler());
    handlerLookup
        .addHandler(DoubleClickEvent.TYPE, new ChartDblClickHandler());
    handlerLookup.addHandler(KeyDownEvent.TYPE, new ChartKeyDownHandler());
    handlerLookup.addHandler(KeyUpEvent.TYPE, new ChartKeyUpHandler());
    handlerLookup
        .addHandler(KeyPressEvent.TYPE, new ChartKeyPressHandler());
  }

  public boolean handleChartEvent(Event event, Chart chart, int x, int y) {
    chartInfo.chart = chart;
    chartInfo.setHandled(false);
    chartInfo.setLocalX(x);
    chartInfo.setLocalY(y);
    
    DomEvent browserEvent = getBrowserEvent(event);
    if (browserEvent != null) {
      handlerLookup.fireEvent(browserEvent);
    } else {
      // shouldn't happen normally
      throw new RuntimeException("getBrowserEvent returned null");
    }
    return chartInfo.isHandled();
  }

  private DomEvent getBrowserEvent(Event event) {
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
        return new KeyPressEvent(event);
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
