package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;

import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.util.DateFormatter;
import org.timepedia.chronoscope.client.util.date.DateFormatterFactory;
import org.timepedia.chronoscope.client.util.date.GWTDateFormatter;
import org.timepedia.chronoscope.client.HistoryManager;

public abstract class GwtView extends View {

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

}
