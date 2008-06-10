package org.timepedia.chronoscope.gviz.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 */
public final class Query extends JavaScriptObject {

  protected Query() {
  }

  public native void send(QueryResponseHandler handler) /*-{
     this.send(function(resp) {
       handler.@org.timepedia.chronoscope.gviz.client.QueryResponseHandler::onQueryResponse(Lorg/timepedia/chronoscope/gviz/client/QueryResponse;)(resp);
     })
   }-*/;
}
