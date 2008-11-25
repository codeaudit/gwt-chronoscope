package org.timepedia.chronoscope.client.browser;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;

import org.timepedia.chronoscope.client.HistoryManager;

/**
 * Deal with HistoryListener events.
 */
public class GwtHistoryManagerImpl implements HistoryListener {

  private static GwtHistoryManagerImpl instance;

  public GwtHistoryManagerImpl() {
    History.addHistoryListener(this);
  }

  public static void initHistory() {
    if (instance == null) {
      instance = new GwtHistoryManagerImpl();
    }
    String initToken = History.getToken();
    if (initToken.length() > 0) {
      instance.onHistoryChanged(initToken);
    }
  }

  public void onHistoryChanged(String historyToken) {
    HistoryManager.restoreHistory(historyToken);
  }
}
