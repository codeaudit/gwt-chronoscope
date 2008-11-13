package org.timepedia.chronoscope.gviz.api.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 */
public class GVizEventHelper {

  public static final String SELECT_EVENT = "select";

  public static final String RANGECHANGE_EVENT = "rangechange";

  public static final String HOVER_EVENT = "hover";

  public static native void trigger(JavaScriptObject source, String eventName,
      JavaScriptObject eventData) /*-{
          $wnd.google.visualization.events.trigger(source, eventName, 
              eventData);
      }-*/;

  public static native JavaScriptObject selection(int col, int row) /*-{
      return [{col: col, row: row }];
  }-*/;

  public static native JavaScriptObject point(int col, int row) /*-{
      return { col: col, row: row };
  }-*/;
}
