package org.timepedia.chronoscope.client.browser.json;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArray;

import org.timepedia.chronoscope.client.data.json.JsonDataset;
import org.timepedia.chronoscope.client.data.json.JsonArrayNumber;
import org.timepedia.chronoscope.client.data.json.JsonArray;
import org.timepedia.chronoscope.client.data.json.JsonArrayString;
import org.timepedia.chronoscope.client.browser.json.GwtJsonArrayNumber;
import org.timepedia.chronoscope.client.browser.json.GwtJsonArrayOverlay;
import org.timepedia.chronoscope.client.browser.json.GwtJsonArrayString;
import org.timepedia.chronoscope.client.browser.json.JsonDatasetJSO;

/**
 * Javascript Object Overlay of our JSON format
 */
public class GwtJsonDataset implements JsonDataset {

  private JsonDatasetJSO jso;

  public GwtJsonDataset(JavaScriptObject jso) {
    this.jso = jso.cast();
  }

  public String getDateTimeFormat() {
    return jso.getDateTimeFormat();
  }

  public double getDomainScale() {
    return jso.getDomainScale();
  }

  public boolean isMipped() {
    return jso.isMipped();
  }

  public double getRangeTop() {
    return jso.getRangeTop();
  }

  public double getRangeBottom() {
    return jso.getRangeBottom();
  }

  public boolean hasRangeInformation() {
    return jso.hasRangeInformation();
  }

  public String getId() {
    return jso.getId();
  }

  public String getLabel() {
    return jso.getLabel();
  }

  public String getAxisId() {
    return jso.getAxisId();
  }

  public JsonArrayNumber getDomain() {
    return new GwtJsonArrayNumber(jso.getDomain());
  }

  public JsonArrayString getDomainString() {
    return new GwtJsonArrayString(jso.getDomainString());
  }

  public JsonArrayNumber getRange() {
    return new GwtJsonArrayNumber(jso.getRange());
  }

  public JsonArray<JsonArrayNumber> getTupleRange() {
    JsArray<JsArrayNumber> tupleRange = jso.getTupleRange();
    if (tupleRange != null) {
      return new GwtJsonArrayOverlay<JsonArrayNumber>(tupleRange,
          JsonArrayNumber.class);
    }
    return null;
  }

  public JsonArray<JsonArrayNumber> getMultiDomain() {
    return new GwtJsonArrayOverlay<JsonArrayNumber>(jso.getMultiDomain(),
        JsonArrayNumber.class);
  }

  public JsonArray<JsonArrayNumber> getMultiRange() {
    return new GwtJsonArrayOverlay<JsonArrayNumber>(jso.getMultiRange(),
        JsonArrayNumber.class);
  }

  public double getMinInterval() {
    return jso.getMinInterval();
  }

  public String getPartitionStrategy() {
    return jso.getPartitionStrategy();
  }

  public String getPreferredRenderer() {
    return jso.getPreferredRenderer();
  }

}
