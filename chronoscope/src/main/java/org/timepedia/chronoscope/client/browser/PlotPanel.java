package org.timepedia.chronoscope.client.browser;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.browser.BrowserGssContext.OnGssInitializedCallback;
import org.timepedia.chronoscope.client.browser.event.CaptureFocusHandler;
import org.timepedia.chronoscope.client.browser.event.ChartDblClickHandler;
import org.timepedia.chronoscope.client.browser.event.ChartKeyDownHandler;
import org.timepedia.chronoscope.client.browser.event.ChartKeyPressHandler;
import org.timepedia.chronoscope.client.browser.event.ChartKeyUpHandler;
import org.timepedia.chronoscope.client.browser.event.ChartMouseDownHandler;
import org.timepedia.chronoscope.client.browser.event.ChartMouseMoveHandler;
import org.timepedia.chronoscope.client.browser.event.ChartMouseOutHandler;
import org.timepedia.chronoscope.client.browser.event.ChartMouseOverHandler;
import org.timepedia.chronoscope.client.browser.event.ChartMouseUpHandler;
import org.timepedia.chronoscope.client.browser.event.ChartMouseWheelHandler;
import org.timepedia.chronoscope.client.browser.event.ChartState;
import org.timepedia.chronoscope.client.browser.event.OverviewAxisMouseMoveHandler;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.util.ArgChecker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;

import static com.google.gwt.user.client.Event.KEYEVENTS;
import static com.google.gwt.user.client.Event.ONMOUSEOUT;
import static com.google.gwt.user.client.Event.ONMOUSEWHEEL;
import static com.google.gwt.user.client.Event.ONMOUSEUP;

public class PlotPanel extends FocusPanel implements ViewReadyCallback,
    SafariKeyboardConstants {

  MouseDownHandler mouseDownHandler = new ChartMouseDownHandler();
  MouseUpHandler mouseUpHandler = new ChartMouseUpHandler();
  MouseOutHandler mouseOutHandler = new ChartMouseOutHandler();
  MouseOverHandler mouseOverHandler = new ChartMouseOverHandler();
  MouseMoveHandler mouseMoveHandler = new ChartMouseMoveHandler();
  MouseMoveHandler mouseMoveHandlerOverview = new OverviewAxisMouseMoveHandler();
  MouseWheelHandler mouseWheelHandler = new ChartMouseWheelHandler();
  DoubleClickHandler dblClickHandler = new ChartDblClickHandler();
  KeyDownHandler keyDownHandler = new ChartKeyDownHandler();
  KeyUpHandler keyUpHandler = new ChartKeyUpHandler();
  KeyPressHandler keyPressHandler = new ChartKeyPressHandler();

  private static GssContext gssContext;
  private View view;
  private Chart chart;
  private XYPlot<?> plot;
  private String id;
  private int chartWidth;
  private int chartHeight;
  private ViewReadyCallback readyListener;
  private boolean viewReady;

  public PlotPanel() {
    CaptureFocusHandler setFocusHandler = new CaptureFocusHandler(this);
    addMouseDownHandler(setFocusHandler);
    addFocusHandler(setFocusHandler);
    addBlurHandler(setFocusHandler);
    
    addMouseDownHandler(mouseDownHandler);
    addMouseUpHandler(mouseUpHandler);
    addMouseOutHandler(mouseOutHandler);
    addMouseOverHandler(mouseOverHandler);
    addMouseMoveHandler(mouseMoveHandlerOverview);
    addMouseMoveHandler(mouseMoveHandler);
    addMouseWheelHandler(mouseWheelHandler);
    addDoubleClickHandler(dblClickHandler);
    addKeyDownHandler(keyDownHandler);
    addKeyUpHandler(keyUpHandler);
    addKeyPressHandler(keyPressHandler);
    addKeyDownHandler(keyDownHandler);
    
    view = (View) GWT.create(DOMView.class);
    chart = new Chart();
  }

  public void dispose() {
    gssContext = null;
    view = null;
    chart = null;
    plot = null;
    readyListener = null;
  }

  /**
   * This method is here because GWT 2.0.x does not have it and we don't want to 
   * depend on GWT 2.1.0 yet.
   */
  public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
    return addDomHandler(handler, DoubleClickEvent.getType());
  }
  
  private boolean initialized = false;

  /**
   * Instantiates a chart widget using the given DOM element as a container,
   * creating a DefaultXYPlot using the given datasets, with a
   * ViewReadyCallback. If the container is null it creates
   */
  public void init(XYPlot<?> plot, int chartWidth, int chartHeight) {

    ArgChecker.isNotNull(plot, "plot");

    disableContextMenu(getElement());

    this.plot = plot;
    this.chartWidth = chartWidth;
    this.chartHeight = chartHeight;

    chart.setPlot(plot);

    Element cssgss = DOM.createDiv();
    DOM.setStyleAttribute(cssgss, "width", "0px");
    DOM.setStyleAttribute(cssgss, "height", "0px");
    DOM.setElementAttribute(cssgss, "id",
        DOM.getElementAttribute(getElement(), "id") + "style");
    DOM.setElementAttribute(cssgss, "class", "chrono");
    appendBody(cssgss);

    if (gssContext == null) {
      gssContext = GWT.create(BrowserGssContext.class);
      ((BrowserGssContext) gssContext).initialize(cssgss,
          new OnGssInitializedCallback() {
            public void run() {
              initView();
            }
          });
    } else {
      initView();
    }
  }



  public void fireContextMenu(Event evt) {
    if (DOM.eventGetTypeString(evt) == "undefined") {
      return;
    }
    if (evt == null) {
      return;
    }

    int x = DOM.eventGetClientX(evt);
    int y = DOM.eventGetClientY(evt) + Window.getScrollTop();

    ((DefaultXYPlot<?>) getChart().getPlot()).fireContextMenuEvent(x, y);
    DOM.eventCancelBubble(evt, true);
    DOM.eventPreventDefault(evt);
  }

  public Chart getChart() {
    return chart;
  }

  public int getChartHeight() {
    return chartHeight;
  }

  public int getChartWidth() {
    return chartWidth;
  }

  public View getView() {
    return view;
  }

  /**
   * Handle main Chronoscope navigation features and forward them to the Chart
   * class.
   */
  @Override
  public void onBrowserEvent(Event evt) {
    if (!isAttached() || !viewReady || !initialized) {
      return;
    }

    // TODO: MCM move this to handlers
    // Only request (x,y) coordinates if they're available/relevant
    // (e.g. mouse move, mouse click). Otherwise, DOM.eventGetClientX()
    // will throw an exception.
    boolean screenCoordinatesRelevant = ((KEYEVENTS  & evt.getTypeInt()) == 0)
            || ((ONMOUSEOUT & evt.getTypeInt()) == 0)
            || ((ONMOUSEWHEEL & evt.getTypeInt()) == 0);

    int x, y;
    int originX = DOM.getAbsoluteLeft(getElement());
    int originY = DOM.getAbsoluteTop(getElement()) - Window.getScrollTop();

    if (screenCoordinatesRelevant) {
      x = evt.getClientX();
      y = evt.getClientY();
    } else {
      x = -1;
      y = -1;
    }

    ChartState chartInfo = ChartState.getInstance();
    chartInfo.chart = chart;
    chartInfo.setHandled(false);
    chartInfo.setClientX(x);
    chartInfo.setClientY(y);
    chartInfo.setOriginX(originX);
    chartInfo.setOriginY(originY);
    super.onBrowserEvent(evt);
  }

  /**
   * Invoked when a BrowserView is finished construction and ready for
   * rendering. Chart initialization is delayed until view creation, because
   * Chart initialization depends on measuring font metrics, CSS styles, and
   * other view-centric operations.
   */
  public void onViewReady(View view) {
    viewReady = true;
    plot.init(view);

    // possible leak source - FIXME
    // HistoryManager.putChart(id, chart);
    
    chart.redraw();
    if (readyListener != null) {
      readyListener.onViewReady(view);
      // run just once
      readyListener = null;
    }
  }

  public void setGssContext(GssContext context) {
    gssContext = context;
  }

  public void setReadyListener(ViewReadyCallback readyListener) {
    this.readyListener = readyListener;
  }

  /**
   * When this widget is attached, a BrowserView is constructed. Views are
   * responsible for providing rendering and CSS services. A BrowserView may be
   * created asynchronously (as is the case with Flash, Silverlight, and others)
   * and therefore super.onAttach() is not called until onViewReady is invoked.
   */
  protected void onAttach() {
    super.onAttach();
    if (initialized && view != null) {
      view.onAttach();
    }
  }

  private void initView() {

    ((DOMView) view).initialize(getElement(), chartWidth, chartHeight, true, gssContext, this);
    
    // configure chart
    chart.setView(view);
    chart.init();
    if (isAttached()) {
      view.onAttach();
    }
    
    initialized = true;
  }

  private native void appendBody(Element cssgss) /*-{
    $doc.body.appendChild(cssgss);
  }-*/;

  /**
   * Take over right-mouse button menu on browsers that support it.
   */
  private native void disableContextMenu(Element elem) /*-{
    var _this = this;
    elem.oncontextmenu=function(a,b) {
      _this.@org.timepedia.chronoscope.client.browser.PlotPanel::fireContextMenu(Lcom/google/gwt/user/client/Event;)(a);
      return false;
    };
  }-*/;

}
