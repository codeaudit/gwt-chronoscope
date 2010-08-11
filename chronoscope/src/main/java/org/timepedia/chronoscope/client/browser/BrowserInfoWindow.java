package org.timepedia.chronoscope.client.browser;

import com.google.gwt.gen2.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.DOM;

import org.timepedia.chronoscope.client.InfoWindow;
import org.timepedia.chronoscope.client.InfoWindowEvent;
import org.timepedia.chronoscope.client.InfoWindowClosedHandler;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.Export;

/**
 *
 */
public class BrowserInfoWindow implements InfoWindow, Exportable {

  HandlerManager manager;

  private final PopupPanel pp;

  private DOMView browserView;

  public BrowserInfoWindow(DOMView browserView, PopupPanel pp) {
    this.browserView = browserView;
    this.pp = pp;
    manager = new HandlerManager(this);
    pp.addPopupListener(new PopupListener() {

      public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
        manager.fireEvent(new InfoWindowEvent());
      }
    });
  }

  @Export
  public void close() {
    pp.hide();
  }

  @Export
  public void setPosition(double x, double y) {
    pp.setPopupPosition(DOM.getAbsoluteLeft(browserView.getElement()) + (int) x,
        DOM.getAbsoluteTop(browserView.getElement()) + (int) y);
  }

  @Export("addCloseHandler")
  public void addInfoWindowClosedHandler(InfoWindowClosedHandler handler) {
    manager.addHandler(InfoWindowEvent.TYPE, handler);
  }

  @Export
  public void open() {
    pp.show();
  }
}
