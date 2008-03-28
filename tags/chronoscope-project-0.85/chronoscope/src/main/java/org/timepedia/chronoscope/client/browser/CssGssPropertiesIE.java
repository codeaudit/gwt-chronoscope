package org.timepedia.chronoscope.client.browser;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.core.client.JavaScriptObject;

import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.gss.GssElement;

/**
 * Placeholder for IE support. Element.currentStyle isn't exactly the same as
 * view.getComputedStyle(), but it may work good enough, or we may eventually
 * use a batik CSS engine on the server to preprocess CSS for IE
 */
public class CssGssPropertiesIE extends CssGssProperties {

  private JavaScriptObject currentStyle;

  public void init(GssContext elem, GssElement gssElement, Element styleElem,
      String pseudoElt, View view) {
    currentStyle = getCurrentStyle(styleElem);
    super.init(elem, gssElement, styleElem, pseudoElt,
        view);  
  }

  private static native JavaScriptObject getCurrentStyle(Element styleElem) /*-{
    return styleElem.currentStyle;
  }-*/;

  public String get(String propName) {
    return JavascriptHelper.jsPropGetString(currentStyle, propName);
  }

  public Color getColor(String propName) {
    String color = get(propName);
    return new Color(color);
  }

  public double getFloat(String propName) {
    return JavascriptHelper.jsPropGetD(currentStyle, propName);
  }

  public int getInt(String propName) {
    return (int)getFloat(propName);
  }
}
