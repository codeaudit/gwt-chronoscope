package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.Widget;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

/**
 * ChartPanel is a GWT Widget that intercepts events and translates them to the
 * Chart interface as well as creating a View instance for the chart to render
 * with. All client-side browser-specific classes reside in the browser
 * subpackage to ensure the portability of the rest of Chronoscope to other
 * environments (Applet, Servlet, etc) <p/> ChartPanel at the moment only
 * handles XYPlots, but in the future will be extended to handle other chart
 * types. See the project wiki for more details. <p/> <p/> AÊsimple way to
 * construct a chart looks like this: <p/>
 * <pre>
 * ChartPanel chartPanel = new ChartPanel(myDatasets);
 * RootPanel.get("someid").add(chartPanel);
 * </pre>
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class ChartPanel extends Widget implements ViewReadyCallback,
    WindowResizeListener, SafariKeyboardConstants, Exportable {

  private ChartEventHandler chartEventHandler;

  /**
   * Gets the window's scroll left.
   *
   * @return window's scroll left
   */
  public static native int getScrollLeft() /*-{
          // Standard mode || Quirks mode.
          return $doc.documentElement.scrollLeft || $doc.body.scrollLeft
        }-*/;

  /**
   * Get the window's scroll top.
   *
   * @return the window's scroll top
   */
  public static native int getScrollTop() /*-{
          // Standard mode || Quirks mode.
          return $doc.documentElement.scrollTop || $doc.body.scrollTop;
        }-*/;

  private GssContext gssContext;

  private View view;

  private Chart chart;

  private XYPlot plot;

  private String id;

  private int chartWidth;

  private int chartHeight;

  private ViewReadyCallback readyListener;

  private boolean viewReady;

  /**
   * Instantiates a chart widget using the given DOM element as a container
   */
  public ChartPanel(Element container, XYPlot plot, int chartWidth,
      int chartHeight) {
    this(container, plot, chartWidth, chartHeight, null);
  }

  /**
   * Instantiates a chart widget using the given DOM element as a container,
   * with a ViewReadyCallback
   */
  public ChartPanel(Element container, XYPlot plot, int chartWidth,
      int chartHeight, ViewReadyCallback readyListener) {
    this.chartWidth = chartWidth;
    this.chartHeight = chartHeight;
    this.readyListener = readyListener;
    initElement(container);
    this.plot = plot;
    chart = new Chart();
    chart.setPlot(plot);
  }

  /**
   * Instantiates a chart widget using the given DOM element as a container,
   * creating a DefaultXYPlot using the given datasets, with a
   * ViewReadyCallback.
   */
  public ChartPanel(Element container, XYDataset[] datasets, int chartWidth,
      int chartHeight, ViewReadyCallback readyListener) {
    this.chartWidth = chartWidth;
    this.chartHeight = chartHeight;
    this.readyListener = readyListener;
    initElement(container);
    chart = new Chart();
    plot = new DefaultXYPlot(chart, datasets, true);
    chart.setPlot(plot);
  }

  /**
   * Instantiates a chart widget using the given DOM element as a container,
   * creating a DefaultXYPlot using the given datasets.
   */
  public ChartPanel(Element container, XYDataset[] datasets, int chartWidth,
      int chartHeight) {
    this(container, datasets, chartWidth, chartHeight, null);
  }

  /**
   * Create a chart using the given datasets and an automatically generated
   * container element
   */
  public ChartPanel(XYDataset[] datasets, int chartWidth, int chartHeight) {
    this(DOM.createDiv(), datasets, chartWidth, chartHeight);
  }

  /**
   * Create a chart using the given XYPlot, and an automatically generated
   * container element
   */
  public ChartPanel(XYPlot plot, int chartWidth, int chartHeight) {
    this(DOM.createDiv(), plot, chartWidth, chartHeight);
  }

  public void fireContextMenu(Event evt) {

    int x = DOM.eventGetClientX(evt);
    int y = DOM.eventGetClientY(evt) + getScrollTop();

    view.fireContextMenuEvent(x, y);
    DOM.eventCancelBubble(evt, true);
    DOM.eventPreventDefault(evt);
  }

  /**
   * @gwt.export
   */
  @Export
  public Chart getChart() {
    return chart;
  }

  /**
   * Handle main Chronoscope navigation features and forward them to the Chart
   * class.
   */
  public void onBrowserEvent(Event evt) {
    if (!isAttached() || !viewReady) {
      return;
    }
    
    // Only request (x,y) coordinates if they're available/relevant
    // (e.g. mouse move, mouse click).  Otherwise, DOM.eventGetClientX()
    // will throw an exception.
    boolean screenCoordinatesRelevant = 
      (Event.KEYEVENTS & evt.getTypeInt()) == 0;

    int x, y;
    if (screenCoordinatesRelevant) {
      x = DOM.eventGetClientX(evt) - DOM.getAbsoluteLeft(getElement());
      int absTop = DOM.getAbsoluteTop(getElement());
      y = DOM.eventGetClientY(evt) - absTop + Window.getScrollTop();
    }
    else {
      x = -1;
      y = -1;
    }
    
    if (!chartEventHandler.handleChartEvent(evt, chart, x, y)) {
      super.onBrowserEvent(evt);
    } else {
      DOM.eventCancelBubble(evt, true);
      DOM.eventPreventDefault(evt);
    }
  }

  /**
   * Invoked when a BrowserView is finished construction and ready for
   * rendering. Chart initialization is delayed until view creation, because
   * Chart initialization depends on measuring font metrics, CSS styles, and
   * other view centric operations.
   */
  public void onViewReady(View view) {

    viewReady = true;
    chart.init(view, plot);
    Chronoscope.putChart(id, chart);
    if (readyListener != null) {
      readyListener.onViewReady(view);
    } else {
      chart.redraw();
    }
  }

  /**
   * Note: Window/Chart/View resizing doesn't work right now.
   */
  public void onWindowResized(int width, int height) {

    Element elem = ((DOMView) view).getElement();
    view.resize(DOM.getElementPropertyInt(elem, "clientWidth"),
        DOM.getElementPropertyInt(elem, "clientHeight"));
  }

  public void resetDrag(int amt) {
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

    chartEventHandler = GWT.create(ChartEventHandler.class);

    Element cssgss = null;
    cssgss = DOM.createDiv();
    DOM.setStyleAttribute(cssgss, "width", "0px");
    DOM.setStyleAttribute(cssgss, "height", "0px");
    DOM.setElementAttribute(cssgss, "id",
        DOM.getElementAttribute(getElement(), "id") + "style");
    DOM.setElementAttribute(cssgss, "class", "chrono");
    appendBody(cssgss);
    super.onAttach();
    view = (View) GWT.create(DOMView.class);
    if (gssContext == null) {
      gssContext = (BrowserGssContext) GWT
          .create(BrowserGssContext.class);
    }
    ((BrowserGssContext) gssContext).initialize(cssgss);

    ((DOMView) view)
        .initialize(getElement(), chartWidth, chartHeight, true, gssContext,
            this);
    view.onAttach();
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
             _this.@org.timepedia.chronoscope.client.browser.ChartPanel::fireContextMenu(Lcom/google/gwt/user/client/Event;)(a);
               return false;
           };

   }-*/;

  private void initElement(Element container) {
    setElement(container);
    DOM.setStyleAttribute(container, "overflow", "hidden");
//    addStyleName("chrono");
    sinkEvents(Event.MOUSEEVENTS);
    sinkEvents(Event.KEYEVENTS);
    sinkEvents(Event.ONCLICK);
    sinkEvents(Event.ONDBLCLICK);
    sinkEvents(Event.ONMOUSEWHEEL);

    Window.addWindowResizeListener(this);
    disableContextMenu(getElement());

    id = DOM.getElementAttribute(container, "id");
    if (id == null || "".equals(id)) {
      id = Chronoscope.generateId();
      DOM.setElementAttribute(container, "id", id);
    }
  }
}
