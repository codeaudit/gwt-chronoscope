package org.timepedia.chronoscopesamples.client;

import org.timepedia.chronoscope.client.ChronoscopeOptions;
import org.timepedia.chronoscope.client.browser.Chronoscope;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class ChartDemoNumberformats implements EntryPoint {

  public void onModuleLoad() {
    GWT.setUncaughtExceptionHandler(new ClientExceptionHandler());
    Chronoscope.setMicroformatsEnabled(true);
    ChronoscopeOptions.setErrorReporting(true);
    Chronoscope.getInstance();
  }

  private class ClientExceptionHandler implements GWT.UncaughtExceptionHandler {
    public void onUncaughtException(Throwable cause) {
      System.out.println(cause.toString());
      GWT.log(cause.toString(), cause);
    }
  }
}

