package org.timepedia.chronoscope.client.browser;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.dom.client.NativeEvent;

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
public class ChartEventHandler implements HasHandlers {

  protected HandlerManager handlerLookup;

  private static ChartState chartInfo;

  public static ChartState getChartState() {
    return chartInfo;
  }

  public ChartEventHandler() {
    // Stores information about the chart that's needed by the client event
    // handlers.
    chartInfo = new ChartState();
    chartInfo.setTabKeyEventCode(this.getTabKeyEventType()); // browser-specific
    chartInfo.setHandlerManager(handlerLookup);
    
    // Register client event handlers
    handlerLookup = new HandlerManager(this);
    handlerLookup
        .addHandler(MouseDownEvent.getType(), new ChartMouseDownHandler());
    handlerLookup.addHandler(MouseUpEvent.getType(), new ChartMouseUpHandler());
    handlerLookup
        .addHandler(MouseOutEvent.getType(), new ChartMouseOutHandler());
    handlerLookup
        .addHandler(MouseOverEvent.getType(), new ChartMouseOverHandler());
    handlerLookup
        .addHandler(MouseMoveEvent.getType(), new ChartMouseMoveHandler());
    handlerLookup
        .addHandler(MouseMoveEvent.getType(), new OverviewAxisMouseMoveHandler());
    handlerLookup
        .addHandler(MouseWheelEvent.getType(), new ChartMouseWheelHandler());
    handlerLookup.addHandler(ClickEvent.getType(), new ChartMouseClickHandler());
    handlerLookup
        .addHandler(DoubleClickEvent.getType(), new ChartDblClickHandler());
    handlerLookup.addHandler(KeyDownEvent.getType(), new ChartKeyDownHandler());
    handlerLookup.addHandler(KeyUpEvent.getType(), new ChartKeyUpHandler());
    handlerLookup
        .addHandler(KeyPressEvent.getType(), new ChartKeyPressHandler());
  }

  public boolean handleChartEvent(Event event, Chart chart, int clientX,
      int clientY, int originX, int originY) {
    chartInfo.chart = chart;
    chartInfo.setHandled(false);
    chartInfo.setClientX(clientX);
    chartInfo.setClientY(clientY);
    chartInfo.setOriginX(originX);
    chartInfo.setOriginY(originY);

    DomEvent.fireNativeEvent(event, this);
    return chartInfo.isHandled();
  }


  /**
   * Safari and IE use KEYDOWN for TAB, FF uses KEYPRESS
   */
  public int getTabKeyEventType() {
    return Event.ONKEYDOWN;
  }

  public void sinkEvents(UIObject uiObject) {
    uiObject.sinkEvents(Event.MOUSEEVENTS);
    uiObject.sinkEvents(Event.KEYEVENTS);
    uiObject.sinkEvents(Event.ONCLICK);
    uiObject.sinkEvents(Event.ONDBLCLICK);
    uiObject.sinkEvents(Event.ONMOUSEWHEEL);
  }

  public void fireEvent(GwtEvent<?> gwtEvent) {
    handlerLookup.fireEvent(gwtEvent);
  }
}
                