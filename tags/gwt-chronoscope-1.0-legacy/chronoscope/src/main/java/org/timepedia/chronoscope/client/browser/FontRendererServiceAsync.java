package org.timepedia.chronoscope.client.browser;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Async interface for the FontRendererService
 */
public interface FontRendererServiceAsync {

  void getRenderedFontMetrics(String fontFamily, String fontWeight,
      String fontSize, String color, float angle, AsyncCallback async);
}
