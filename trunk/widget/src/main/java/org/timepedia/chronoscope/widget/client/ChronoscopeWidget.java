package org.timepedia.chronoscope.widget.client;

import com.google.gwt.core.client.EntryPoint;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Window;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.ChronoscopeOptions;


/**
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class ChronoscopeWidget implements EntryPoint {

  public void onModuleLoad() {
    GWT.setUncaughtExceptionHandler(new ClientExceptionHandler());
    Chronoscope.setMicroformatsEnabled(true);
    ChronoscopeOptions.setErrorReporting(true);
    Chronoscope.getInstance();
  }

    private class ClientExceptionHandler implements GWT.UncaughtExceptionHandler
    {
        public void onUncaughtException(Throwable cause) {
            GWT.log(cause.getMessage(), cause);
        }
    }

}


