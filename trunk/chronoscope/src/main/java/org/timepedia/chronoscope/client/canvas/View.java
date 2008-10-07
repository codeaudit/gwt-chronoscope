package org.timepedia.chronoscope.client.canvas;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.ChronoscopeMenu;
import org.timepedia.chronoscope.client.ChronoscopeMenuFactory;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.InfoWindow;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.XYPlotListener;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.PortableTimer;
import org.timepedia.chronoscope.client.util.PortableTimerTask;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import java.util.ArrayList;
import java.util.List;

/**
 * View encapsulate platform specific behaviors, such as graphics rendering,
 * timing, and CSS property retrieval. Views support asynchronous creation,
 * therefore, the proper use of a view is to postpone operations until
 * ViewReadyCallback is invoked.
 *
 * @gwt.exportPackage chronoscope
 */
@ExportPackage("chronoscope")
public abstract class View implements Exportable {

  protected int viewHeight, viewWidth;

  protected Canvas frontCanvas, backingCanvas;

  protected ChronoscopeMenuFactory menuFactory = null;

  protected GssContext gssContext;

  protected ViewReadyCallback callback;

  protected Chart chart;

  private final List<XYPlotListener> plotListeners = new ArrayList<XYPlotListener>();

  private boolean doubleBuffered = false;

  private ChronoscopeMenu contextMenu = null;

  public View() {
  }

  public void addViewListener(XYPlotListener listener) {
    ArgChecker.isNotNull(listener, "listener");
    plotListeners.add(listener);
  }

  public void canvasSetupDone() {
    getCanvas().canvasSetupDone();
  }

  /**
   * Create a ChronoscopeMenu implementation.
   */
  public abstract ChronoscopeMenu createChronoscopeMenu(int x, int y);

  /**
   * @gwt.export createMenu
   */
  @Export("createMenu")
  public ChronoscopeMenu createChronoscopeMenu() {
    return createChronoscopeMenu(0, 0);
  }

  /**
   * Create a timer capable of scheduling delayed execution of the given
   * PortableTimerTask. PortableTimerTask is an abstract to ensure that
   * View/Plot related code is not tightly bound to the browser's environment of
   * GWT and can run in an Applet or Servlet environment as well.
   */
  public abstract PortableTimer createTimer(PortableTimerTask run);

  /**
   * Make sure the canvas is currently visible in the UI.
   */
  public void ensureViewVisible() {
  }

  public void fireContextMenuEvent(int x, int y) {
    for (XYPlotListener l : plotListeners) {
      l.onContextMenu(x, y);
    }
  }

  public void fireFocusEvent(XYPlot plot, int focusSeries, int focusPoint) {
    for (XYPlotListener l : plotListeners) {
      l.onFocusPointChanged(plot, focusSeries, focusPoint);
    }
  }

  public void fireScrollEvent(XYPlot plot, double domainAmt, int type, boolean anim) {
    for (XYPlotListener l : plotListeners) {
      
      // FIXME: pass domainAmt to onPlotMoved
      l.onPlotMoved(plot, domainAmt, type, anim);
    }
  }

  /**
   * If double buffered, the frontCanvas is made invisible, the offscreen canvas
   * is flipped to front, and the references to the front and back canvases are
   * swapped.
   */
  public void flipCanvas() {
    if (doubleBuffered) {
      // frontCanvas.setVisibility(false);
      // backingCanvas.setVisibility(true);
      Canvas tmp = frontCanvas;
      frontCanvas = backingCanvas;
      backingCanvas = tmp;
    }
  }

  /**
   * Returns the backingCanvas (offscreen canvas being drawn to)
   */
  public Canvas getCanvas() {
    return backingCanvas;
  }

  /**
   * @gwt.export
   */
  @Export
  public Chart getChart() {
    return chart;
  }

  /**
   * Given a GssElement and pseudo class, we utilize the GssContext to retrieve
   * a GssProperties object for this GssElement
   */
  public GssProperties getGssProperties(GssElement gssElem, String pseudoElt) {
    return gssContext.getProperties(gssElem, pseudoElt);
  }

  public int getViewHeight() {
    return viewHeight;
  }

  public int getViewWidth() {
    return viewWidth;
  }

  /**
   * Create a view with the given dimensions, GssContext, calling the
   * ViewReadyCallback when all Canvases are created and the view layer is
   * ready.
   */
  public void initialize(final int width, final int height,
      boolean doubleBuffered, final GssContext gssContext,
      final ViewReadyCallback callback) {

    this.viewWidth = width == 0 ? 400 : width;
    this.viewHeight = height == 0 ? 300 : height;
    this.gssContext = gssContext;
    this.doubleBuffered = doubleBuffered;
    gssContext.setView(this);
    this.callback = callback;
    backingCanvas = createCanvas(viewWidth, viewHeight);

    if (doubleBuffered) {
      frontCanvas = createCanvas(viewWidth, viewHeight);
    }
  }

  public  String numberFormat(String labelFormat, double value) {
    return String.valueOf(value);
  }

  /**
   * Invoked when the parent element containing this view is added to the
   * visible UI hierarchy (e.g. DOM)
   */
  public void onAttach() {

    backingCanvas.attach(this, new CanvasReadyCallback() {

      public void onCanvasReady(Canvas canvas) {
        if (doubleBuffered) {
          frontCanvas.attach(View.this, new CanvasReadyCallback() {
            public void onCanvasReady(Canvas canvas) {
              allCanvasReady();
            }
          });
        } else {
          allCanvasReady();
        }
      }
    });
  }

  /**
   * Popup a window containing the given HTML at the coordinates specified
   * (relative to plot insets)
   */
  public abstract InfoWindow createInfoWindow(String html, double x, double y);

  /**
   * Hack, to add IEEERemainer function for GWT until fixed in JRE emul
   */
  public double remainder(double numerator, double modulus) {
    return 0;
  }

  /**
   * Resizing the chart once displayed currently unsupported
   */
  public void resize(int width, int height) {
  }

  public void setChart(Chart chart) {
    this.chart = chart;
  }

  /**
   * Attach a context menu to this View
   *
   * @gwt.export
   */
  @Export
  public void setContextMenu(ChronoscopeMenu cm) {
    if (contextMenu == null) {
      contextMenu = cm;
      addViewListener(new XYPlotListener() {
        public void onContextMenu(int x, int y) {
          ChronoscopeMenu menu = getContextMenu();
          menu.show(x, y);
        }

        public void onFocusPointChanged(XYPlot plot, int focusSeries,
            int focusPoint) {
          // do nothing
        }

        public void onPlotMoved(XYPlot plot, double domainAmt, int type, boolean animated) {
          // do nothing
        }
      });
    } else {
      contextMenu = cm;
    }
  }

  /**
   * Hint to set the mouse cursor to a particular mode.
   * @param cursor
   */
  public void setCursor(Cursor cursor) {
    // default impl, ignore
  }

  /**
   * A menu factory is used to delegate the creation of Menu UI widgets
   */
  public void setMenuFactory(ChronoscopeMenuFactory menuFactory) {
    this.menuFactory = menuFactory;
  }

  /**
   * Invoked after all canvases (front, back, etc) are created
   */
  protected void allCanvasReady() {

    init();
    if (callback != null) {
      callback.onViewReady(this);
    }
  }

  /**
   * Implement this method to create a Canvas with the given dimensions.
   */
  protected abstract Canvas createCanvas(int width, int height);

  /**
   * Override to provide View-specific initialization (see {@link
   * org.timepedia.chronoscope.client.browser.BrowserView} for more details)
   */
  protected void init() {
  }

  private ChronoscopeMenu getContextMenu() {
    return contextMenu;
  }
}
