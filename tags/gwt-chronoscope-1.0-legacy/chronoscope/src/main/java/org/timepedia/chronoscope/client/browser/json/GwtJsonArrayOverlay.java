package org.timepedia.chronoscope.client.browser.json;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;

import org.timepedia.chronoscope.client.data.json.JsonArrayNumber;
import org.timepedia.chronoscope.client.data.json.JsonArray;
import org.timepedia.chronoscope.client.data.json.JsonObject;
import org.timepedia.chronoscope.client.data.json.JsonArrayString;
import org.timepedia.chronoscope.client.browser.json.GwtJsonArrayNumber;
import org.timepedia.chronoscope.client.browser.json.GwtJsonArrayString;

/**
 * GWT implementation of JsonArray
 */
public class GwtJsonArrayOverlay<T extends JsonObject> implements JsonArray<T> {

  public T get(int i) {
    T wrapper = null;
    if (parameterType == JsonArrayNumber.class) {
      wrapper = (T) new GwtJsonArrayNumber((JsArrayNumber) jso.get(i));
    } else if(parameterType == JsonArrayString.class) {
      wrapper = (T) new GwtJsonArrayString((JsArrayString) jso.get(i));
    }
    return wrapper;
  }

  public int length() {
    return jso.length();
  }

  private JsArray jso;

  private Class<T> parameterType;

  public GwtJsonArrayOverlay(JsArray jso, Class<T> parameterType) {
    this.jso = jso;
    this.parameterType = parameterType;
  }
}
