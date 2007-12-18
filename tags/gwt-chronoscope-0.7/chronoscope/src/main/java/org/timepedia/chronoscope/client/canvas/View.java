package org.timepedia.chronoscope.client.canvas;


import org.timepedia.chronoscope.client.*;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.util.PortableTimer;
import org.timepedia.chronoscope.client.util.PortableTimerTask;
import org.timepedia.exporter.client.Exportable;

import java.util.Vector;


/**
 * View encapsulate platform specific behaviors, such as graphics rendering, timing, and CSS property retrieval. Views
 * support asynchronous creation, therefore, the proper use of a view is to postpone operations until ViewReadyCallback
 * is invoked.
 *
 * @gwt.exportPackage chronoscope
 */
public abstract class View implements Exportable {
    protected final int viewWidth;
    protected final int viewHeight;
    protected Canvas frontCanvas, backingCanvas;
    protected ChronoscopeMenuFactory menuFactory = null;
    protected final GssContext gssContext;
    protected final ViewReadyCallback callback;
    private final Vector viewListeners = new Vector();
    protected Chart chart;
    private boolean doubleBuffered = false;


    /**
     * Create a view with the given imensions, GssContext, calling the ViewReadyCallback when all
     * Canvases are created and the view layer is ready.
     *
     * @param width
     * @param height
     * @param doubleBuffered
     * @param gssContext
     * @param callback
     */
    public View(final int width, final int height, boolean doubleBuffered, final GssContext gssContext,
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

    /**
     * Invoked when the parent element containing this view is added to the visible UI hierarchy (e.g. DOM)
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
     * Invoked after all canvases (front, back, etc) are created
     */
    protected void allCanvasReady() {

        init();
        if (callback != null) {
            callback.onViewReady(this);
        }
    }

    /**
     * Override to provide View-specific initialization
     * (see {@link org.timepedia.chronoscope.client.browser.BrowserView} for more details)
     */
    protected void init() {
    }


    /**
     * Implement this method to create a Canvas with the given dimensions.
     *
     * @param width
     * @param height
     * @return
     */
    protected abstract Canvas createCanvas(int width, int height);

    /**
     * A menu factory is used to delegate the creation of Menu UI widgets
     *
     * @param menuFactory
     */
    public void setMenuFactory(ChronoscopeMenuFactory menuFactory) {
        this.menuFactory = menuFactory;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public int getViewWidth() {
        return viewWidth;
    }


    /**
     * @return
     * @gwt.export
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * Returns the backingCanvas (offscreen canvas being drawn to)
     *
     * @return
     */
    public Canvas getCanvas() {
        return backingCanvas;
    }


    /**
     * If double buffered, the frontCanvas is made invisible, the offscreen canvas is flipped to front,
     * and the references to the front and back canvases are swapped.
     */
    public void flipCanvas() {
        if (doubleBuffered) {
            frontCanvas.setVisibility(false);
            backingCanvas.setVisibility(true);
            Canvas tmp = frontCanvas;
            frontCanvas = backingCanvas;
            backingCanvas = tmp;
        }
    }


    /**
     * Given a GssElement and pseudo class, we utilize the GssContext to retrieve a GssProperties object for this
     * GssElement
     *
     * @param gssElem
     * @param pseudoElt
     * @return
     */
    public GssProperties getGssProperties(GssElement gssElem, String pseudoElt) {
        return gssContext.getProperties(gssElem, pseudoElt);

    }

    /**
     * Resizing the chart once displayed currently unsupported
     *
     * @param width
     * @param height
     */
    public void resize(int width, int height) {
    }


    public void fireFocusEvent(XYPlot plot, int focusSeries, int focusPoint) {
        for (int i = 0; i < viewListeners.size(); i++) {
            ( (XYPlotListener) viewListeners.get(i) ).onFocusPointChanged(plot, focusSeries, focusPoint);
        }
    }


    public void fireContextMenuEvent(int x, int y) {

        for (int i = 0; i < viewListeners.size(); i++) {
            ( (XYPlotListener) viewListeners.get(i) ).onContextMenu(x, y);
        }
    }


    /**
     * Make sure the canvas is currently visible in the UI.
     */
    public void ensureViewVisible() {
    }

    private ChronoscopeMenu contextMenu = null;

    /**
     * Attach a context menu to this View
     *
     * @param cm
     * @gwt.export
     */
    public void setContextMenu(ChronoscopeMenu cm) {
        if (contextMenu == null) {
            contextMenu = cm;
            addViewListener(new XYPlotListener() {

                public void onPlotMoved(XYPlot plot, double amt, int seriesNum, int type, boolean animated) {
                }

                public void onFocusPointChanged(XYPlot plot, int focusSeries, int focusPoint) {
                }

                public void onContextMenu(int x, int y) {
                    ChronoscopeMenu menu = getContextMenu();

                    menu.show(x, y);
                }
            });
        } else {
            contextMenu = cm;
        }

    }

    private ChronoscopeMenu getContextMenu() {
        return contextMenu;
    }


    /**
     * Popup a window containing the given HTML at the coordinates specified (relative to plot insets)
     *
     * @param html
     * @param x
     * @param y
     */
    public abstract void openInfoWindow(String html, double x, double y);

    /**
     * Create a timer capable of scheduling delayed execution of the given PortableTimerTask.
     * PortableTimerTask is an abstract to ensure that View/Plot related code is not tightly bound to the
     * browser's environment of GWT and can run in an Applet or Servlet environment as well.
     *
     * @param run
     * @return
     */
    public abstract PortableTimer createTimer(PortableTimerTask run);

    public void addViewListener(XYPlotListener vl) {
        viewListeners.add(vl);
    }


    public void fireScrollEvent(XYPlot plot, double amt, int seriesNum, int type, boolean anim) {
        for (int i = 0; i < viewListeners.size(); i++) {
            ( (XYPlotListener) viewListeners.get(i) ).onPlotMoved(plot, amt, seriesNum, type, anim);
        }
    }

    /**
     * Create a ChronoscopeMenu implementation.
     *
     * @param x
     * @param y
     * @return
     */
    public abstract ChronoscopeMenu createChronoscopeMenu(int x, int y);

    /**
     * @return
     * @gwt.export createMenu
     */
    public ChronoscopeMenu createChronoscopeMenu() {
        return createChronoscopeMenu(0, 0);
    }


    public void setChart(Chart chart) {
        this.chart = chart;
    }
}
