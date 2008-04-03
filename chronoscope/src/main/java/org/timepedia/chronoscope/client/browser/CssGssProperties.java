package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.render.GssElementImpl;

/**
 * Implementation of GssProperties with values taken from DOM Level 2 CSS
 */
public class CssGssProperties extends GssProperties {

  private JavaScriptObject cssProperties;

  private Element styleElem;

  private String pseudoElt;

  public String get(String propName) {

    if (cssProperties == null) {
      cssProperties = getCssProperties(styleElem, pseudoElt);
    }

    try {
      return getCssPropertyValueString(cssProperties, propName);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

  public Color getColor(String propName) {
    try {
      return new Color(get(propName));
    } catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    return new Color("#000");
  }

  public double getFloat(String propName) {
    if (cssProperties == null) {
      cssProperties = getCssProperties(styleElem, pseudoElt);
    }
    try {
      return getCssPropertyValueFloat(cssProperties, propName);
    } catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    return 1;
  }

  public int getInt(String propName) {

    if (cssProperties == null) {
      cssProperties = getCssProperties(styleElem, pseudoElt);
    }
    try {
      return getCssPropertyValuePixels(cssProperties, propName);
    } catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    return 1;
  }

  /* String LINE_WIDTH = "width";
String POINT_COLOR = "color";
String FOCUS_COLOR = "color";
String LINE_COLOR = "color";
String SHADOW_BLUR = "width";
String TRANSPARENCY = "opacity";
String SHADOW_COLOR = "color";
String SHADOW_OFFSETX = "left";
String SHADOW_OFFSETY = "top";
String POINT_SIZE = "width";
String BGCOLOR = "background-color";
String FOCUS_WIDTH = "border-left-width";
String FOCUS_SIZE = "width";
String POINT_BORDER_WIDTH = "border-left-width";
String DISPLAY = "visibility";
String VALUE_VISIBLE = "visible";*/

  public void init(GssContext elem, GssElement gssElement, Element styleElem,
      String pseudoElt, View view) {
    this.styleElem = styleElem;
    this.pseudoElt = pseudoElt;
    color = getColor("color");
    bgColor = getColor("background-color");
    size = getInt("width");
    visible = !"hidden".equals(get("visibility"));
    transparency = getFloat("opacity");
    lineThickness = getInt("border-left-width");
    left = getInt("left");
    top = getInt("top");

    String bgImage = get("background-image");
    bgImage = bgImage.replaceAll("\\\\", "");

    if (bgImage.startsWith("url(")) {
      bgImage = bgImage.substring(4, bgImage
          .length() - 1);
    }
    if (!"none".equals(bgImage) && (bgImage.startsWith("http://timepedia.org/")
        || bgImage.indexOf("#") != -1)) {
      bgColor = createGradient(view.getCanvas().getRootLayer(), size, size,
          bgImage);
    } else if (!"none".equals(bgImage)) {
      // bgColor = view.getCanvas().createPattern(bgImage);
    }
    if (!"shadow".equals(gssElement.getType())) {
      GssProperties shadowProp = elem
          .getProperties(new GssElementImpl("shadow", gssElement), pseudoElt);
      shadowBlur = shadowProp.size;
      shadowColor = shadowProp.color;
      shadowOffsetX = shadowProp.left;
      shadowOffsetY = shadowProp.top;
    }

    fontFamily = get("font-family");
    fontWeight = get("font-weight");
    fontSize = getCssPropertyValueFloat(cssProperties, "font-size", 9) + "pt";

    ((CssGssContext) elem).dispose(styleElem);
    cssProperties = null;
    pseudoElt = null;
    styleElem = null;
  }

  protected native JavaScriptObject getCssProperties(Element styleElem,
      String pseudoElt) /*-{
        return $doc.defaultView.getComputedStyle(styleElem, pseudoElt);
    }-*/;

  protected native double getCssPropertyValueFloat(
      JavaScriptObject cssProperties, String propName) /*-{
        return cssProperties.getPropertyCSSValue(propName).getFloatValue(1);
    }-*/;

  protected native double getCssPropertyValueFloat(
      JavaScriptObject cssProperties, String propName, int type) /*-{
        return cssProperties.getPropertyCSSValue(propName).getFloatValue(type);
    }-*/;

  protected native int getCssPropertyValuePixels(JavaScriptObject cssProperties,
      String propName) /*-{
        var x=cssProperties.getPropertyCSSValue(propName).getFloatValue(5);
        if(x==NaN || x < 1) return 0;
        if(x > 1000) { return 1; }   
        return x;
    }-*/;

  protected native String getCssPropertyValueString(
      JavaScriptObject cssProperties, String propName) /*-{
        var prop=cssProperties.getPropertyCSSValue(propName);
        var type = prop.primitiveType;
        if(prop.primitiveType == 25)
        {
             return cssProperties.getPropertyValue(propName);
        }
    return cssProperties.getPropertyValue(propName);
    }-*/;
}
