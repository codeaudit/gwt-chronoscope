package org.timepedia.chronoscope.client.browser.flashcanvas;
import com.google.gwt.user.client.Element;

public abstract class SwfObject {

    public abstract Element create(String objectElementId, String swfUrl, String readyFn);

    public abstract Element create(String objectElementId, String swfUrl, String readyFn, Element altContent);

}
