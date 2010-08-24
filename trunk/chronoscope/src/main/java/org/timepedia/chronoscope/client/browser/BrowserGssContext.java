package org.timepedia.chronoscope.client.browser;

import com.google.gwt.user.client.Element;

import org.timepedia.chronoscope.client.gss.GssContext;

/**
 * Subtype for GssContexts that operate in the browser
 */
public class BrowserGssContext extends GssContext {

  private Element cssgss;

  public Element getElement() {
    return cssgss;
  }

  public void initialize(Element cssgss, OnGssInitializedCallback callback) {
    this.cssgss = cssgss;
  }
  
  public interface OnGssInitializedCallback {
    void run(); 
  }
  
}
