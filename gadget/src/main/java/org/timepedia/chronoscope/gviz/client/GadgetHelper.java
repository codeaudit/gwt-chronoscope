package org.timepedia.chronoscope.gviz.client;

import com.google.gwt.gadgets.client.impl.PreferencesUtil;
import com.google.gwt.core.client.JavaScriptObject;

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
