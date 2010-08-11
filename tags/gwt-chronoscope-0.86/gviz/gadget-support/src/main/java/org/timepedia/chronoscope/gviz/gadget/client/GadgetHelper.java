package org.timepedia.chronoscope.gviz.gadget.client;

import com.google.gwt.core.client.JavaScriptObject;

import org.timepedia.chronoscope.gviz.api.client.Query;

/**
 *
 */
public final class GadgetHelper extends JavaScriptObject {

  protected GadgetHelper() {
  }

  public static native GadgetHelper create() /*-{
    return new $wnd.google.visualization.GadgetHelper();
  }-*/;
  
  public native Query createQueryFromPrefs() /*-{
    return this.createQueryFromPrefs(new $wnd._IG_Prefs());
  }-*/;

}
