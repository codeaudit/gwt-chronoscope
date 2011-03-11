package org.timepedia.chronoscope.client.browser.flashcanvas;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import org.timepedia.chronoscope.client.browser.Chronoscope;

import static com.google.gwt.core.client.GWT.getModuleBaseURL;

/**
 * This is the non-IE implementation
 */
public class SwfObjectImpl extends SwfObject {

    @Override
    public Element create(String objectElementId, String swfUrl, String readyFn) {

      Element obj = DOM.createElement("object");
      DOM.setStyleAttribute(obj, "position", "absolute");
      DOM.setStyleAttribute(obj, "top", "0px");
      DOM.setStyleAttribute(obj, "left", "0px");
      DOM.setStyleAttribute(obj, "zIndex", "0");
      DOM.setElementAttribute(obj, "id", objectElementId);
      DOM.setElementAttribute(obj, "width", "100%");
      DOM.setElementAttribute(obj, "height","100%");
      DOM.setElementAttribute(obj, "type", "application/x-shockwave-flash");
      DOM.setElementAttribute(obj, "data", swfUrl);

      add(obj, "FlashVars", "readyFn=" + readyFn);
      add(obj, "quality", "autolow");
      add(obj, "bgcolor", "#FFFFFF");
      add(obj, "wmode", "opaque");
      add(obj, "MENU", "false");
      add(obj, "base", getModuleBaseURL());
      add(obj, "allowScriptAccess", "always");
      add(obj, "allowNetworking", "all");

      return obj;
    }

    @Override
    public Element create(String objectElementId, String swfUrl, String readyFn, Element altContent) {
      Element obj = create(objectElementId, swfUrl,readyFn);
      DOM.appendChild(obj, altContent);
      return obj;
    }

    protected static void add(Element obj, String name, String value) {
      DOM.appendChild(obj, param(name, value));
    }

    protected static Element param(String name, String value) {
      Element p = DOM.createElement("param");
      DOM.setElementAttribute(p, "name", name);
      DOM.setElementAttribute(p, "value", value);
      return p;
    }
}
