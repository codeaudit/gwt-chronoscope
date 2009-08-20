package org.timepedia.chronoscope.client.browser.json;

import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JavaScriptObject;

import org.timepedia.chronoscope.client.data.json.JsonArrayNumber;

/**
 * GWT implementation of JsonArrayNumber.
 */
public class GwtJsonArrayNumber implements JsonArrayNumber {

  private JsArrayNumber jso;

  public double get(int i) {
    return jso.get(i);
  }

  public int length() {
    return jso.length();
  }

  public GwtJsonArrayNumber(JsArrayNumber jso) {
    this.jso = jso;
  }
}
