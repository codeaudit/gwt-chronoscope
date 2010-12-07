package org.timepedia.chronoscopesamples.client;

import java.util.ArrayList;

import org.timepedia.chronoscope.client.ChronoscopeOptions;
import org.timepedia.chronoscope.client.browser.Chronoscope;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Element;

/**
 * @author Manolo Carrasco <manolo@timepedia.org>
 */
public class ChartDemoJS implements EntryPoint {

  public void onModuleLoad() {
//    GWT.setUncaughtExceptionHandler(new ClientExceptionHandler());

    NodeList nl = Document.get().getElementsByTagName("style");
    final ArrayList<String> toLoad = new ArrayList<String>();
    for (int i = 0; i < nl.getLength(); i++) {
      Element e = (Element) nl.getItem(i);
      if ("text/gss".equals(e.getAttribute("type"))) {
        GWT.log("Style = " + e.getInnerText(), null);
      }
    }

    Chronoscope.setMicroformatsEnabled(true);
//    ChronoscopeOptions.setErrorReporting(true);
    Chronoscope.getInstance();
  }

  private class ClientExceptionHandler implements GWT.UncaughtExceptionHandler {
    public void onUncaughtException(Throwable cause) {
      GWT.log(cause.toString(), cause);
    }
  }

}
