package org.timepedia.chronoscope.client.browser.json;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JavaScriptObject;

import org.timepedia.chronoscope.client.data.json.JsonArrayString;

/**
 * GWT implementation of JsonArrayString.
 */
public class GwtJsonArrayString implements JsonArrayString {

  private JsArrayString jso;

  public String get(int i) {
    return jso.get(i);
  }

  public int length() {
    return jso.length();
  }

  public GwtJsonArrayString(JsArrayString jso) {
    this.jso = jso;
  }
}
