package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.impl.FocusImpl;
import org.timepedia.chronoscope.client.ChronoscopeMenu;
import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.util.PortableTimer;
import org.timepedia.chronoscope.client.util.PortableTimerTask;
import org.timepedia.exporter.client.Exportable;


/**
 * A realization of a View on the browser using Safari JavaScript CANVAS and DOM Level 2 CSS
 *
 * @gwt.exportPackage chronoscope
 */
public class BrowserView extends View implements Exportable {
    static final FocusImpl focusImpl = (FocusImpl) GWT.create(FocusImpl.class);

    protected Element rootElem, containerDiv;
    private Element element;
    private String id;


    public BrowserView(final Element element, final int width, final int height, final boolean interactive,
                       GssContext gssContext, final ViewReadyCallback callback) {
        super(width, height, false, gssContext, callback);
        this.element = element;
        id = DOM.getElementAttribute(element, "id");
        menuFactory = new BrowserChronoscopeMenuFactory();
    }

    public void onAttach() {
        initContainer(element, viewWidth, viewHeight);
        super.onAttach();
    }


    protected void initContainer(Element element, int width, int height) {
        this.rootElem = element;
        this.containerDiv = focusImpl.createFocusable();
        DOM.setInnerHTML(rootElem, "");
        DOM.setElementAttribute(containerDiv, "id", DOM.getElementAttribute(rootElem, "id") + "container");
        DOM.setIntStyleAttribute(containerDiv, "width", width);
        DOM.setIntStyleAttribute(containerDiv, "height", height);
        DOM.setStyleAttribute(containerDiv, "position", "relative");


        DOM.appendChild(rootElem, containerDiv);
        DOM.setStyleAttribute(containerDiv, "height", "100%");
        DOM.setStyleAttribute(containerDiv, "width", "100%");
    }


    protected Element getElement(Layer layer) {
        if (layer instanceof BrowserCanvas) {
            return ( (BrowserCanvas) layer ).getElement();
        }
        return null;
    }


    /**
     * Overridden to disable double buffering
     */
    public void flipCanvas() {
    }

    /**
     * If the DOM element containing the canvas is not visible, we first scroll it into view
     */
    public void ensureViewVisible() {
        super.ensureViewVisible();
        DOM.scrollIntoView(containerDiv);
    }

    /**
     * Opens an HTML popup info window at the given screen coordinates (within the plot bounds)
     *
     * @param html
     * @param x
     * @param y
     */
    public void openInfoWindow(String html, double x, double y) {
        PopupPanel pp = new PopupPanel(true);
        pp.setStyleName("chrono-infoWindow");
        pp.setWidget(new HTML(html));
        pp.setPopupPosition(ChartPanel.getAbsoluteLeft(getElement()) + (int) x, ChartPanel.getAbsoluteTop(
                getElement()) + (int) y);
        DOM.setStyleAttribute(pp.getElement(), "zIndex", "99999");
        pp.show();

    }

    /**
     * Return a Browser (CANVAS tag) canvas. This may be extended in the future to support Flash, Silverlight, SVG,
     * and Applet canvases for the Browser.
     *
     * @param width
     * @param height
     * @return
     */
    protected Canvas createCanvas(int width, int height) {
        return new BrowserCanvas(this, width, height);
    }


    public BrowserView(Element element, boolean interactive, GssContext ctx, ViewReadyCallback callback) {
        this(element, getClientWidthRecursive(element), getClientHeightRecursive(element), interactive, ctx, callback);
    }

    private static int getClientHeightRecursive(Element element) {
        int height = DOM.getElementPropertyInt(element, "clientHeight");
        if (height != 0) {
            return height;
        }
        Element parent = DOM.getParent(element);
        if (parent != null) {
            return getClientHeightRecursive(parent);
        }
        return 600;
    }

    private static int getClientWidthRecursive(Element element) {
        int width = DOM.getElementPropertyInt(element, "clientWidth");
        if (width != 0) {
            return width;
        }
        Element parent = DOM.getParent(element);
        if (parent != null) {
            return getClientWidthRecursive(parent);
        }
        return 800;
    }


    /**
     * Is the current history token from the HistoryListener the same as the state we just left?
     *
     * @param history
     * @return
     */
    public boolean isPreviousHistory(String history) {

        return history.equals(previousHistory);
    }

    public void clearPreviousHIstory() {
        previousHistory = "";
    }

    /**
     * Push the current state of the Plot into browser history
     */
    public void pushHistory() {
        Chronoscope.pushHistory();
    }


    /**
     * Get a history token representing the current state of the plot
     *
     * @return
     */
    public String getHistoryToken() {

        return getId() + chart.getHistoryToken();
    }

    public String previousHistory;


    /**
     * The DIV containing the canvas and other misc elements
     *
     * @return
     */
    public Element getElement() {
        return containerDiv;
    }


    /**
     * Use an internal focus listener to ensure keyboard focus events are picked up
     */
    public void focus() {
        focusImpl.focus(containerDiv);
    }

    /**
     * Create a menu and return it
     *
     * @param x
     * @param y
     * @return
     */
    public ChronoscopeMenu createChronoscopeMenu(int x, int y) {
        return menuFactory.createChronoscopeMenu(x, y);
    }

    /**
     * Return the representing this view
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Go back to the previous chart state
     */
    public void popHistory() {
        History.back();

    }

    public Element getGssCssElement() {
        return ( (CssGssContext) gssContext ).getElement();
    }


    static abstract class BrowserTimer extends Timer implements PortableTimer {
    }


    /**
     * Creates a PortableTimer based on GWT's Timer class.
     *
     * @param run
     * @return
     */
    public PortableTimer createTimer(final PortableTimerTask run) {
        return new BrowserTimer() {

            public void run() {
                run.run(this);
            }

            public void cancelTimer() {
                cancel();
            }
        };

    }


}
