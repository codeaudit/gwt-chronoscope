package org.timepedia.chronoscope.java2d.gss;

import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.render.GssElementImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.views.DocumentView;

/**
 * Retrieves GSS properties from Batik's CSS Engine
 */
public class BatikGssProperties extends GssProperties {

  private Document doc;

  private Element styleElem;

  private String pseudoElt;

  private CSSStyleDeclaration cssProperties;

  public String get(String propName) {

    if (cssProperties == null) {
      cssProperties = getCssProperties(styleElem, pseudoElt);
    }

    String value = getCssPropertyValueString(cssProperties, propName);

    return value;
  }

  public Color getColor(String propName) {
    return new Color(get(propName));
  }

  public double getFloat(String propName) {
    if (cssProperties == null) {
      cssProperties = getCssProperties(styleElem, pseudoElt);
    }
    double value = getCssPropertyValueFloat(cssProperties, propName);

    return value;
  }

  public int getInt(String propName) {

    if (cssProperties == null) {
      cssProperties = getCssProperties(styleElem, pseudoElt);
    }
    int value = getCssPropertyValuePixels(cssProperties, propName);

    return value;
  }

  public void init(BatikGssContext gssContext, Document doc, GssElement elem,
      Element styleElem, String pseudoElt, View view) {
    this.doc = doc;
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
    if (bgImage.startsWith("url(")) {
      bgImage = bgImage.substring(4, bgImage.length() - 1);
    }
    if (!"none".equals(bgImage) && bgImage
        .startsWith("http://timepedia.org/")) {
      bgColor = createGradient(view.getCanvas().getRootLayer(), size, size,
          bgImage);
    }
    if (!"shadow".equals(elem.getType())) {
      GssProperties shadowProp = gssContext
          .getProperties(new GssElementImpl("shadow", elem), pseudoElt);
      shadowBlur = shadowProp.size;
      shadowColor = shadowProp.color;
      shadowOffsetX = shadowProp.left;
      shadowOffsetY = shadowProp.top;
    }

    fontFamily = get("font-family");
    fontWeight = get("font-weight");
    try {
      fontSize =
          (int) (getCssPropertyValueFloat(cssProperties, "font-size", 5) / .75)
              + "pt";
    } catch (Exception e) {
      //        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      fontSize = "9pt";
    }

    gssContext.dispose(styleElem);
    cssProperties = null;
    pseudoElt = null;
    styleElem = null;
  }

  protected CSSStyleDeclaration getCssProperties(Element styleElem,
      String pseudoElt) {
    return ((ViewCSS) (((DocumentView) doc).getDefaultView()))
        .getComputedStyle(styleElem, pseudoElt);
  }

  protected double getCssPropertyValueFloat(CSSStyleDeclaration cssProperties,
      String propName) {
    return ((CSSPrimitiveValue) cssProperties.getPropertyCSSValue(propName))
        .getFloatValue((short) 1);
  }

  protected double getCssPropertyValueFloat(CSSStyleDeclaration cssProperties,
      String propName, int type) {
    return ((CSSPrimitiveValue) cssProperties.getPropertyCSSValue(propName))
        .getFloatValue((short) type);
  }

  protected int getCssPropertyValuePixels(CSSStyleDeclaration cssProperties,
      String propName) {
    CSSPrimitiveValue cssValue = (CSSPrimitiveValue) cssProperties
        .getPropertyCSSValue(propName);
    return (int) ((CSSPrimitiveValue) cssValue)
        .getFloatValue(cssValue.getPrimitiveType());
  }

  protected String getCssPropertyValueString(CSSStyleDeclaration cssProperties,
      String propName) {

    return cssProperties.getPropertyValue(propName);
  }
}
