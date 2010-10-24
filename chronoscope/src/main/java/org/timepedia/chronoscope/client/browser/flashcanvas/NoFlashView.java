package org.timepedia.chronoscope.client.browser.flashcanvas;

import org.timepedia.chronoscope.client.ChronoscopeMenu;
import org.timepedia.chronoscope.client.InfoWindow;
import org.timepedia.chronoscope.client.browser.BrowserChronoscopeMenuFactory;
import org.timepedia.chronoscope.client.browser.BrowserGssContext;
import org.timepedia.chronoscope.client.browser.BrowserInfoWindow;
import org.timepedia.chronoscope.client.browser.CssGssViewSupport;
import org.timepedia.chronoscope.client.browser.DOMView;
import org.timepedia.chronoscope.client.browser.GwtView;
import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.gss.MockGssProperties;
import org.timepedia.chronoscope.client.util.PortableTimer;
import org.timepedia.chronoscope.client.util.PortableTimerTask;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.impl.FocusImpl;

/**
 * A realization of a View for a browser which does not have Flash installed.
 *
 */
@ExportPackage("chronoscope")
public class NoFlashView extends GwtView
    implements Exportable, CssGssViewSupport, DOMView {

  abstract static class BrowserTimer extends Timer implements PortableTimer {
  }

  static final FocusImpl focusImpl = FocusImpl.getFocusImplForPanel();

  private Element containerDiv;

  private Element element;

  @Override
  public ChronoscopeMenu createChronoscopeMenu(int x, int y) {
    return menuFactory.createChronoscopeMenu(x, y);
  }

  @Override
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

  public void ensureViewVisible() {
    DOM.scrollIntoView(containerDiv);
  }

  public void exportFunctions() {
    GWT.create(NoFlashView.class);
    GWT.create(BrowserInfoWindow.class);
    GWT.create(MockGssProperties.class);
  }

  public void flipCanvas() {
  }

  public void focus() {
    focusImpl.focus(containerDiv);
  }

  public Element getElement() {
    return containerDiv;
  }

  public Element getGssCssElement() {
    return ((BrowserGssContext) gssContext).getElement();
  }

  @Override
  public void initialize(final Element element, final int width,
      final int height, final boolean interactive, GssContext gssContext,
      final ViewReadyCallback callback) {
    this.element = element;
    resize(width, height);
    menuFactory = new BrowserChronoscopeMenuFactory();
    element.setInnerHTML("Unsupported Browser. Install Flash to view the chart.");
  }


  @Override
  public String numberFormat(String labelFormat, double value) {
    return NumberFormat.getFormat(labelFormat).format(value);
  }

  @Override
  public void onAttach() {
  }

  @Override
  public InfoWindow createInfoWindow(String html, double x, double y) {
    return null;
  }


  @Override
  protected Canvas createCanvas(int width, int height) {
    return null;
  }

  @Export
  @Override
  public void resize(int width, int height) {
    DOM.setStyleAttribute(element, "height", height + "px");
    DOM.setStyleAttribute(element, "width", width + "px");
  }


}