package org.timepedia.chronoscope.client.browser.flashcanvas;

import org.timepedia.chronoscope.client.ChronoscopeMenu;
import org.timepedia.chronoscope.client.ChronoscopeOptions;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.InfoWindow;
import org.timepedia.chronoscope.client.browser.BrowserChronoscopeMenuFactory;
import org.timepedia.chronoscope.client.browser.BrowserGssContext;
import org.timepedia.chronoscope.client.browser.BrowserInfoWindow;
import org.timepedia.chronoscope.client.browser.CssGssViewSupport;
import org.timepedia.chronoscope.client.browser.DOMView;
import org.timepedia.chronoscope.client.browser.GwtView;
import org.timepedia.chronoscope.client.browser.nullcanvas.NullCanvas;
import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.gss.MockGssProperties;
import org.timepedia.chronoscope.client.util.PortableTimer;
import org.timepedia.chronoscope.client.util.PortableTimerTask;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.Exporter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.impl.FocusImpl;

/**
 * A realization of a View on the browser using a Flash implementation of the HTML Canvas API.
 *
 */
@ExportPackage("chronoscope")
public class FlashView extends GwtView
    implements Exportable, CssGssViewSupport, DOMView {
  
  public FlashView() {
    ChronoscopeOptions.setLowPerformance(true);
  }

  abstract static class BrowserTimer extends Timer implements PortableTimer {

  }

  static final FocusImpl focusImpl = FocusImpl.getFocusImplForPanel();


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

  protected Element rootElem, containerDiv;

  private Element element;

  private String id;

  /**
   * Create a menu and return it
   */
  public ChronoscopeMenu createChronoscopeMenu(int x, int y) {
    return menuFactory.createChronoscopeMenu(x, y);
  }

  /**
   * Creates a PortableTimer based on GWT's Timer class.
   */
  public PortableTimer createTimer(final PortableTimerTask run) {
    return new BrowserTimer() {
      public void cancelTimer() {
        cancel();
      }

      public double getTime() {
        return System.currentTimeMillis();
      }

      public void run() {
        run.run(this);
      }
    };
  }

  /**
   * If the DOM element containing the canvas is not visible, we first scroll it
   * into view
   */
  public void ensureViewVisible() {
    super.ensureViewVisible();
    DOM.scrollIntoView(containerDiv);
  }

  public void exportFunctions() {
    Exporter exporter = (Exporter) GWT.create(FlashView.class);
    exporter.export();
    Exporter exporter2 = (Exporter) GWT.create(BrowserInfoWindow.class);
    exporter2.export();
     Exporter exporter3 = (Exporter) GWT.create(MockGssProperties.class);
    exporter3.export();
  }

  /**
   * Overridden to disable double buffering
   */
  public void flipCanvas() {
  }

  /**
   * Use an internal focus listener to ensure keyboard focus events are picked
   * up
   */
  public void focus() {
    focusImpl.focus(containerDiv);
  }

  /**
   * The DIV containing the canvas and other misc elements
   */
  public Element getElement() {
    return containerDiv;
  }

  public Element getGssCssElement() {
    return ((BrowserGssContext) gssContext).getElement();
  }

  public void initialize(final Element element, final int width,
      final int height, final boolean interactive, GssContext gssContext,
      final ViewReadyCallback callback) {
    super.initialize(width, height, false, gssContext, callback);
    DOM.setStyleAttribute(element, "height", height + "px");
    DOM.setStyleAttribute(element, "width", width + "px");
    this.element = element;
    id = DOM.getElementAttribute(element, "id");
    menuFactory = new BrowserChronoscopeMenuFactory();
  }

  public void initialize(Element element, boolean interactive, GssContext ctx,
      ViewReadyCallback callback) {
    initialize(element, getClientWidthRecursive(element),
        getClientHeightRecursive(element), interactive, ctx, callback);
  }

  public String numberFormat(String labelFormat, double value) {
    return NumberFormat.getFormat(labelFormat).format(value);
  }

  public void onAttach() {
    initContainer(element, viewWidth, viewHeight);
    super.onAttach();
  }

  public native double remainder(double numerator, double modulus) /*-{
      return numerator % modulus;
  }-*/;

  public void setCursor(Cursor cursor) {
    switch (cursor) {
      case CLICKABLE:
        setCursorImpl("pointer");
        break;
      case SELECTING:
        setCursorImpl("text");
        break;
      case DRAGGABLE:
        setCursorImpl("move");
        break;
      case DRAGGING:
        setCursorImpl("move");
        break;
      default:
        setCursorImpl("default");
        break;
    }
  }

  /**
   * Return a Browser (CANVAS tag) canvas. This may be extended in the future to
   * support Flash, Silverlight, SVG, and Applet canvases for the Browser.
   */
  protected Canvas createCanvas(int width, int height) {
    if (ChronoscopeOptions.isFlashFallbackEnabled()) {
      return new FlashCanvas(this, width, height);
    } else {
      return new NullCanvas(this);
    }
  }

  protected Element getElement(Layer layer) {
    if (layer instanceof FlashCanvas) {
      return ((FlashCanvas) layer).getElement();
    }
    return null;
  }

  @Export
  @Override
  public void resize(int width, int height) {
    if (ChronoscopeOptions.isFlashFallbackEnabled()) {

    super.resize(width, height);
    initialize(element, width, height, true, gssContext,
        new ViewReadyCallback() {
          @Override
          public void onViewReady(View view) {
            view.getChart().reloadStyles();
          }
        });
    } else {
       initialize(element, width, height, true, gssContext,
        new ViewReadyCallback() {
           @Override
           public void onViewReady(View view) {
           }
        });
    }

    onAttach();
  }

  protected void initContainer(Element element, int width, int height) {
    this.rootElem = element;
    if (ChronoscopeOptions.isFlashFallbackEnabled()) {

    this.containerDiv = focusImpl.createFocusable();
    DOM.setInnerHTML(rootElem, "");
    DOM.setElementAttribute(containerDiv, "id",
        DOM.getElementAttribute(rootElem, "id") + "container");
    DOM.setIntStyleAttribute(containerDiv, "width", width);
    DOM.setIntStyleAttribute(containerDiv, "height", height);
    DOM.setStyleAttribute(containerDiv, "position", "relative");

    DOM.appendChild(rootElem, containerDiv);
    DOM.setStyleAttribute(containerDiv, "height", height + "px");
    DOM.setStyleAttribute(containerDiv, "width", width + "px");
  } else {
    DOM.setInnerHTML(rootElem, FlashCanvas.FLASH_ALTERNATIVES);
  }

  }



  private void setCursorImpl(String cssCursor) {
    getElement().getStyle().setProperty("cursor", cssCursor);
  }
}