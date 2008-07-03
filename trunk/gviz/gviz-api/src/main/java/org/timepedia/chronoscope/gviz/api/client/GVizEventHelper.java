package org.timepedia.chronoscope.gviz.api.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 */
public class GVizEventHelper {

  public static final String SELECT_EVENT = "select";

  public static native void trigger(JavaScriptObject source, String eventName,
      JavaScriptObject eventData) /*-{
          $wnd.google.visualization.events.trigger(source, eventName, 
              eventData);
      }-*/;

  public static native JavaScriptObject selection(int col,
      int row) /*-{
      return [{col: col, row: row }];
  }-*/;
}
