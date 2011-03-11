package org.timepedia.chronoscope.client.browser.flashcanvas;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import static com.google.gwt.core.client.GWT.getModuleBaseURL;

/**
 * Swf Object creation for IE
 */
public class SwfObjectIE extends SwfObjectImpl {
    @Override
    public Element create(String objectElementId, String swfUrl, String readyFn, Element altContent) {

      Element obj = DOM.createElement("object");
      DOM.setStyleAttribute(obj, "position", "absolute");
      DOM.setStyleAttribute(obj, "top", "0px");
      DOM.setStyleAttribute(obj, "left", "0px");
      DOM.setStyleAttribute(obj, "zIndex", "0");
      DOM.setElementAttribute(obj, "classid", "clsid:D27CDB6E-AE6D-11CF-96B8-444553540000");
      DOM.setElementAttribute(obj, "id", objectElementId);
      DOM.setElementAttribute(obj, "width", "100%");
      DOM.setElementAttribute(obj, "height","100%");

      add(obj, "movie", swfUrl);
      add(obj, "FlashVars", "readyFn=" + readyFn);
      add(obj, "quality", "autolow");
      add(obj, "bgcolor", "#FFFFFF");
      add(obj, "wmode", "opaque");
      add(obj, "MENU", "false");
      // add(obj, "base", getModuleBaseURL());
      add(obj, "allowScriptAccess", "always");
      add(obj, "allowNetworking", "all");

      return obj;
    }

}
