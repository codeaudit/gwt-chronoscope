package org.timepedia.chronoscope.client.browser.flashcanvas;

import org.timepedia.chronoscope.client.ChronoscopeOptions;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.browser.BrowserChronoscopeMenuFactory;
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
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import org.timepedia.exporter.client.Exporter;

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

  // static final FocusImpl focusImpl = FocusImpl.getFocusImplForPanel();

  public void exportFunctions() {
    Exporter exporter = (Exporter) GWT.create(FlashView.class);
    Exporter exporter2 = (Exporter) GWT.create(BrowserInfoWindow.class);
    Exporter exporter3 = (Exporter) GWT.create(MockGssProperties.class);
  }

  public void initialize(final Element element, final int width,
      final int height, final boolean interactive, GssContext gssContext,
      final ViewReadyCallback callback) {

    this.element = element;
    initTargetElementId(element);
    super.initialize(width, height, false, gssContext, callback);
    menuFactory = new BrowserChronoscopeMenuFactory();
  }

  public void initialize(Element element, boolean interactive, GssContext ctx, ViewReadyCallback callback) {
    initialize(element, getClientWidthRecursive(element),
        getClientHeightRecursive(element), interactive, ctx, callback);
  }

  public String numberFormat(String labelFormat, double value) {
    return NumberFormat.getFormat(labelFormat).format(value);
  }

  public void onAttach() {
    if (!ChronoscopeOptions.isFlashFallbackEnabled()) {
      DOM.setInnerText(element, FlashCanvas.FLASH_ALTERNATIVES);
    } else {
       super.onAttach();
    }
  }

  /**
   * Return a Flash object (if flash enabled) that supports the canvas interface.
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
      initialize(element, width, height, true, gssContext, null);
    }
    onAttach();
  }

  private void log(String msg){
    System.out.println("FlashView> "+msg);
  }
}