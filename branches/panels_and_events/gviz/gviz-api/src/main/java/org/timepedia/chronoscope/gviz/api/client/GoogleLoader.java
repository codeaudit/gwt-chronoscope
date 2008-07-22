package org.timepedia.chronoscope.gviz.api.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.user.client.Command;

import org.timepedia.chronoscope.client.browser.Chronoscope;

/**
 *
 */
public class GoogleLoader {

  private static boolean injectedLoader = false;

  private static final String JSAPI_CALLBACK = "__injectedJSAPI";

  public static void load(Chronoscope.URLResolver resolver, final String libName,
      final String version, final Command callback) {
    if (!injectedLoader) {
      final ScriptElement se = Document.get().createScriptElement();
//      se.setSrc(intrinsics.getCachedUrl("http://www.google.com/jsapi?callback=" + JSAPI_CALLBACK));
      se.setSrc("http://www.google.com/jsapi?callback=" + JSAPI_CALLBACK);
      
      final HeadElement he = HeadElement.as(Document.get().getElementsByTagName("head").getItem(0));
      exportHandler(new Command() {

        public void execute() {
          injectedLoader=true;
          he.removeChild(se);
          nativeLoad(libName, version, callback);
        }
      });
     
      he.appendChild(se);
    } else {
      nativeLoad(libName, version, callback);
    }
  }

  private static native void exportHandler(Command command) /*-{
      $wnd[@org.timepedia.chronoscope.gviz.api.client.GoogleLoader::JSAPI_CALLBACK] = 
        function() {
            command.@com.google.gwt.user.client.Command::execute()();
          
        }
  }-*/;

  private static native void nativeLoad(String libName, String version,
      Command callback) /*-{
      google.load(libName, version, { callback: function() {
        callback.@com.google.gwt.user.client.Command::execute()();
      } });
  
  }-*/;
}
