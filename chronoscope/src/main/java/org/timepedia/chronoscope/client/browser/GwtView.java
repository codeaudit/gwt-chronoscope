package org.timepedia.chronoscope.client.browser;

import org.timepedia.chronoscope.client.HistoryManager;
import org.timepedia.chronoscope.client.InfoWindow;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.util.DateFormatter;
import org.timepedia.chronoscope.client.util.date.DateFormatterFactory;
import org.timepedia.chronoscope.client.util.date.GWTDateFormatter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class GwtView extends View implements DOMView {

  public GwtView() {
    if (GWT.isClient()) {
      DateFormatterFactory.setDateFormatterFactory(new DateFormatterFactory() {
            private DateFormatter blankFormatter = new GWTDateFormatter("");
            private DateFormatter previousFormatter;
            private String previous = ""; // look back at last one

            public DateFormatter getDateFormatter(String format) {
              if (null == format || "".equals(format) || "undefined".equals(format) || "null".equals(format)) {
                return blankFormatter;
              }
              if (!previous.equals(format)) {
                previous = format;
                previousFormatter = new GWTDateFormatter(format);
              }
              return previousFormatter;
            }
          });
      HistoryManager.setHistoryManagerImpl(new HistoryManager.HistoryManagerImpl() {
        public void push(String historyToken) {
          History.newItem(historyToken);
        }
      });
    }
  }
  
  
  /**
   * Opens an HTML popup info window at the given screen coordinates (within the
   * plot bounds)
   * 
   * It sets the same font family, size, color and bgcolor defined for markers, if
   * you wanted override them use the css selector div.chrono-infoWindow-content.
   * 
   * FIXME: (MCM) this should be a unique instance of popup: ask Shawn
   */
  public InfoWindow createInfoWindow(String html, double x, double y) {
    final PopupPanel pp = new DecoratedPopupPanel(true);
    pp.addStyleName("chrono-infoWindow");
    Widget content = new HTML(html);
    content.setStyleName("chrono-infoWindow-content");
    pp.setWidget(content);
    pp.setPopupPosition(getElement().getAbsoluteLeft() + (int)x, getElement().getAbsoluteTop() + (int)y);
//    pp.setPopupPosition(DOM.getAbsoluteLeft(getElement()) + (int) x, DOM.getAbsoluteTop(getElement()) + (int) y);

    GssProperties markerProperties = gssContext.getPropertiesBySelector("marker");
    if (markerProperties != null) {
      pp.getElement().getStyle().setBackgroundColor(markerProperties.bgColor.toString());
      pp.getElement().getStyle().setColor(markerProperties.color.toString());
      pp.getElement().getStyle().setProperty("fontFamily", markerProperties.fontFamily.toString());
      pp.getElement().getStyle().setProperty("fontSize", markerProperties.fontSize.toString());
      pp.getElement().getStyle().setPadding(5, Unit.PX);
    }
    pp.getElement().getStyle().setZIndex(9999);
    pp.show();
    
    return new BrowserInfoWindow(this, pp);
  }

}
