package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;

import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.util.DateFormatter;
import org.timepedia.chronoscope.client.util.date.DateFormatterFactory;
import org.timepedia.chronoscope.client.util.date.GWTDateFormatter;
import org.timepedia.chronoscope.client.HistoryManager;

import java.util.HashMap;

public abstract class GwtView extends View {

  public GwtView() {
    if (GWT.isClient()) {
      DateFormatterFactory
          .setDateFormatterFactory(new DateFormatterFactory() {
            private HashMap<String,DateFormatter> memo = new HashMap<String,DateFormatter>();
            public DateFormatter getDateFormatter(String format) {
              if (null == format) { return new GWTDateFormatter(null); }
              else {
                if (!memo.containsKey(format)) {
                  memo.put(format, new GWTDateFormatter(format));
                }
                return memo.get(format);
              }
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
