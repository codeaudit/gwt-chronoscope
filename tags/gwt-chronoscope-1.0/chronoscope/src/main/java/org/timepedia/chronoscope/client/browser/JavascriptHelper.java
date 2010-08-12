package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * The class that everyone reimplements (until GWT 1.5 :) )
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class JavascriptHelper {

  public static native JavaScriptObject jsArrGet(JavaScriptObject datasets,
      int i) /*-{
        return datasets[i];
    }-*/;

  public static native double jsArrGetD(JavaScriptObject datasets, int i) /*-{
           return datasets[i];
       }-*/;

  public static native String jsArrGetS(JavaScriptObject datasets, int i) /*-{
           return datasets[i];
       }-*/;

  public static native int jsArrLength(JavaScriptObject datasets) /*-{
           return datasets.length;
       }-*/;

  public static native JavaScriptObject jsPropGet(JavaScriptObject json,
      String propName) /*-{
        return json[propName] || null;
    }-*/;

  public static native double jsPropGetD(JavaScriptObject json, String propName) /*-{
        return json[propName];
    }-*/;

  public static native String jsPropGetString(JavaScriptObject json,
      String propName) /*-{
        var s=json[propName];
        if(!s) return null;
        return ""+s;
    }-*/;

  public static native double jsPropGetDor(JavaScriptObject json, String s, double def) /*-{
    return json[s] || def;
  }-*/;
}
