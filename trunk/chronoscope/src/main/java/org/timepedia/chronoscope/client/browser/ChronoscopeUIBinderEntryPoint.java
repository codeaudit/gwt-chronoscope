package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.core.client.EntryPoint;
import org.timepedia.chronoscope.client.browser.ChronoscopeUIBinder;

public class ChronoscopeUIBinderEntryPoint implements EntryPoint {

  public static String elementId=null;

  public void onModuleLoad() {
    GWT.setUncaughtExceptionHandler(new ClientExceptionHandler());
    ChronoscopeUIBinder w = new ChronoscopeUIBinder();
    RootPanel.get(elementId).add(w);
  }

  private class ClientExceptionHandler implements GWT.UncaughtExceptionHandler {
    public void onUncaughtException(Throwable cause) {
      GWT.log(cause.getMessage(), cause);
    }
  }
}


