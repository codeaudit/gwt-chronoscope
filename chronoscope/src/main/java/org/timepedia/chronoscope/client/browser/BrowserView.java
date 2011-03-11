package org.timepedia.chronoscope.client.browser;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.gss.MockGssProperties;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.Exporter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.impl.FocusImpl;

/**
 * A realization of a View on the browser using Safari JavaScript CANVAS and DOM
 * Level 2 CSS
 *
 */
@ExportPackage("chronoscope")
public class BrowserView extends GwtView
    implements Exportable, CssGssViewSupport, DOMView {

  static final FocusImpl focusImpl = FocusImpl.getFocusImplForPanel();

  public void exportFunctions() {
    Exporter exporter = (Exporter) GWT.create(BrowserView.class);
    Exporter exporter2 = (Exporter) GWT.create(BrowserInfoWindow.class);
    Exporter exporter3 = (Exporter) GWT.create(MockGssProperties.class);
  }


  /**
   * Use an internal focus listener to ensure keyboard focus events are picked
   * up
   */
  public void focus() {
//    focusImpl.focus(containerDiv);
  }

  public void init() {
     ((BrowserCanvas)getCanvas()).onAttach();
  }

  public void initialize(final Element element, final int width,
      final int height, final boolean interactive, GssContext gssContext,
      final ViewReadyCallback callback) {

    this.element = element;
    DOM.setStyleAttribute(element, "position", "relative");
    initTargetElementId(element);
    super.initialize(width, height, false, gssContext, callback);
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

//  public void onAttach() {
//    super.onAttach();
//  }

  public native double remainder(double numerator, double modulus) /*-{
    return numerator % modulus;
  }-*/;


  /**
   * Return a Browser (CANVAS tag) canvas.
   */
  protected Canvas createCanvas(int width, int height) {
    return new BrowserCanvas(this, width, height);
  }

  protected Element getElement(Layer layer) {
    if (layer instanceof BrowserCanvas) {
      return ((BrowserCanvas) layer).getElement();
    }
    return null;
  }

  @Export
  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
    initialize(element, width, height, true, gssContext,
        new ViewReadyCallback() {
          @Override
          public void onViewReady(View view) {
            view.getChart().reloadStyles();
          }
        });
    onAttach();
  }

}
