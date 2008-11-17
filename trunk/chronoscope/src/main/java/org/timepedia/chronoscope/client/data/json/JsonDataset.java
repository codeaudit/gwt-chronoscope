package org.timepedia.chronoscope.client.data.json;

import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JsArray;

/**
 * Interface for JSON datasets to be implemented for GWT and non-GWT.
 */
public interface JsonDataset extends JsonObject {

  String getDateTimeFormat();

  double getDomainScale();

  boolean isMipped();

  double getRangeTop();

  double getRangeBottom();

  boolean hasRangeInformation();

  String getId();

  String getLabel();

  String getAxisId();

  JsonArrayNumber getDomain();

  JsonArrayString getDomainString();

  JsonArrayNumber getRange();

  JsonArray<JsonArrayNumber> getTupleRange();

  JsonArray<JsonArrayNumber> getMultiDomain();

  JsonArray<JsonArrayNumber> getMultiRange();

  double getMinInterval();

  String getPartitionStrategy();

  String getPreferredRenderer();
}
