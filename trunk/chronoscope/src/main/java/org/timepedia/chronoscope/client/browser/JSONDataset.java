package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

/**
 * Javascript Object Overlay of our JSON format
 */
public class JSONDataset extends JavaScriptObject {

  protected JSONDataset() {
  }

  public final native String getDateTimeFormat() /*-{
    return this.dtformat;
  }-*/;

  public final native double getDomainScale() /*-{
    return this.domainscale || 1;
  }-*/;

  public final native boolean isMipped() /*-{
    return this.mipped || false;
  }-*/;

  public final native double getRangeTop() /*-{
    return this.rangeTop;
  }-*/;

  public final native double getRangeBottom() /*-{
    return this.rangeBottom;
  }-*/;

  public final native boolean hasRangeInformation() /*-{
    return this.rangeTop != undefined && this.rangeBottom != undefined;
  }-*/;

  public final native String getId() /*-{
     return this.id;
  }-*/;

  public final native String getLabel() /*-{
     return this.label;
  }-*/;

  public final native String getAxisId() /*-{
     return this.axis;
  }-*/;

  public final native JsArrayNumber getDomain() /*-{
    return this.domain;
  }-*/;

  public final native JsArrayString getDomainString() /*-{
    return this.domain;
  }-*/;

  public final native JsArrayNumber getRange() /*-{
    return this.range;
  }-*/;

  public final native JsArray<JsArrayNumber> getTupleRange() /*-{
    return this.tupleRange;
  }-*/;
  
  public final native JsArray<JsArrayNumber> getMultiDomain() /*-{
     return this.domain;
   }-*/;

  public final native JsArray<JsArrayNumber>  getMultiRange() /*-{
     return this.range;
   }-*/;

  public final native double getMinInterval() /*-{
    return this.minInterval || -1;
  }-*/;
  
  public final native String getPartitionStrategy() /*-{
    return this.partitionStrategy || "binary";
  }-*/;
}
