package org.timepedia.chronoscope.client;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.DOM;

/**
 * Auto-inject Chronoscope.css for testing
 */
public class ChronoscopeTestCase extends GWTTestCase {

  public String getModuleName() {
    return "org.timepedia.chronoscope.ChronoscopeTestSuite";
  }
  
  public void setUp() {
    Element head=getHead();
    Element link = DOM.createElement("link");
    DOM.setElementAttribute(link, "href", "Chronoscope.css");
    DOM.setElementAttribute(link, "rel", "stylesheet");
    DOM.setElementAttribute(link, "type", "text/css");
    DOM.setElementAttribute(link, "title", "Chronoscope Default");
    DOM.appendChild(head, link);
  }

  private static native Element getHead() /*-{
    return $doc.getElementsByTagName("head")[0];
  }-*/;
}
