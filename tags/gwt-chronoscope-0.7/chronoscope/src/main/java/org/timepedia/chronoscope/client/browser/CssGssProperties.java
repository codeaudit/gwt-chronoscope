package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;
import org.timepedia.chronoscope.client.canvas.*;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.render.GssElementImpl;
import org.timepedia.chronoscope.client.render.LinearGradient;

/**
 * Implementation of GssProperties with values taken from DOM Level 2 CSS
 */
public class CssGssProperties extends GssProperties {

    private static final String LINGRADFULLURI = "http://timepedia.org/lineargradient/";
    private static final String RADGRADURI = "http://timepedia.org/radialgradient/";
    private static final String LINEGRADHASHURI = "#lineargradient/";
    private static final String RADGRADHASHURI = "#radialgradient/";

    private JavaScriptObject cssProperties;
    private Element styleElem;
    private String pseudoElt;

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


    public void init(GssContext elem, GssElement gssElement, Element styleElem, String pseudoElt, View view) {
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
        if (!"none".equals(bgImage) && ( bgImage.startsWith("http://timepedia.org/") || bgImage.indexOf("#") != -1 )) {
            bgColor = createGradient(view.getCanvas(), size, size, bgImage);
        } else if (!"none".equals(bgImage)) {
            bgColor = view.getCanvas().createPattern(bgImage);
        }
        if (!"shadow".equals(gssElement.getType())) {
            GssProperties shadowProp = elem.getProperties(new GssElementImpl("shadow", gssElement), pseudoElt);
            shadowBlur = shadowProp.size;
            shadowColor = shadowProp.color;
            shadowOffsetX = shadowProp.left;
            shadowOffsetY = shadowProp.top;

        }

        fontFamily = get("font-family");
        fontWeight = get("font-weight");
        fontSize = getCssPropertyValueFloat(cssProperties, "font-size", 9) + "pt";

        ( (CssGssContext) elem ).dispose(styleElem);
        cssProperties = null;
        pseudoElt = null;
        styleElem = null;

    }


    public String get(String propName) {

        if (cssProperties == null) {
            cssProperties = getCssProperties(styleElem, pseudoElt);
        }


        return getCssPropertyValueString(cssProperties, propName);
    }

    public int getInt(String propName) {

        if (cssProperties == null) {
            cssProperties = getCssProperties(styleElem, pseudoElt);
        }
        return getCssPropertyValuePixels(cssProperties, propName);
    }

    public double getFloat(String propName) {
        if (cssProperties == null) {
            cssProperties = getCssProperties(styleElem, pseudoElt);
        }
        return getCssPropertyValueFloat(cssProperties, propName);
    }

    public Color getColor(String propName) {
        return new Color(get(propName));
    }

    public LinearGradient createLinearGradient(Layer layer, double x1, double y1, double x2, double y2,
                                               String pointBackgroundStr) {
        String afterUrl = null;
        if (pointBackgroundStr.startsWith(LINGRADFULLURI)) {
            afterUrl = pointBackgroundStr.substring(pointBackgroundStr.indexOf(LINGRADFULLURI) +
                    LINGRADFULLURI.length());
        } else {
            int ind = pointBackgroundStr.indexOf(LINEGRADHASHURI);
            afterUrl = pointBackgroundStr.substring(ind + LINEGRADHASHURI.length());
        }
        String[] pieces = afterUrl.split("/");
        String xy[] = pieces[0].split(",");
        String xy2[] = pieces[1].split(",");


        LinearGradient lingrad = layer.createLinearGradient(Double.parseDouble(xy[0]), Double.parseDouble(xy[1]),
                                                            Double.parseDouble(xy2[0]), Double.parseDouble(xy2[1]));
        lingrad.addColorStop(Double.parseDouble(pieces[2]), "#" + pieces[3]);
        lingrad.addColorStop(Double.parseDouble(pieces[4]), "#" + pieces[5]);
        return lingrad;

    }

    public PaintStyle createGradient(Layer layer, double w, double h, String backStr) {
        if (backStr.startsWith(LINGRADFULLURI) || backStr.indexOf(LINEGRADHASHURI) != -1) {
            return createLinearGradient(layer, 0.0, 0.0, 1.0, 1.0, backStr);
        } else {
            return createRadialGradient(layer, 0.0, 0.0, 1.0, 0.0, 0.0, w, backStr);
        }
    }

    public PaintStyle createRadialGradient(Layer layer, double x0, double y0, double r0, double x1, double y1,
                                           double r1, String backStr) {
        String afterUrl = null;
        int i = backStr.indexOf(RADGRADURI);
        if (i != -1) {
            afterUrl = backStr.substring(backStr.indexOf(RADGRADURI) + RADGRADURI.length());
        } else {
            int ind = backStr.indexOf(RADGRADHASHURI);
            afterUrl = backStr.substring(ind + RADGRADHASHURI.length());
        }
        String[] pieces = afterUrl.split("/");
        String xyr[] = pieces[0].split(",");
        String xyr2[] = pieces[1].split(",");


        RadialGradient radialGradient = layer.createRadialGradient(Double.parseDouble(xyr[0]), Double.parseDouble(
                xyr[1]), Double.parseDouble(xyr[2]), Double.parseDouble(xyr2[0]), Double.parseDouble(xyr2[1]),
                         Double.parseDouble(xyr2[2]));
        radialGradient.addColorStop(Double.parseDouble(pieces[2]), "#" + pieces[3]);
        radialGradient.addColorStop(Double.parseDouble(pieces[4]), "#" + pieces[5]);
        return radialGradient;


    }


    protected native String getCssPropertyValueString(JavaScriptObject cssProperties, String propName) /*-{
        var prop=cssProperties.getPropertyCSSValue(propName);
        var type = prop.primitiveType;
        if(prop.primitiveType == 25)
        {
             return cssProperties.getPropertyValue(propName);
        }
    return cssProperties.getPropertyValue(propName);
    }-*/;

    protected native int getCssPropertyValuePixels(JavaScriptObject cssProperties, String propName) /*-{
        var x=cssProperties.getPropertyCSSValue(propName).getFloatValue(5);
        if(x==NaN || x < 1) return 0;
        if(x > 1000) { return 1; }   
        return x;
    }-*/;


    protected native double getCssPropertyValueFloat(JavaScriptObject cssProperties, String propName) /*-{
        return cssProperties.getPropertyCSSValue(propName).getFloatValue(1);
    }-*/;


    protected native double getCssPropertyValueFloat(JavaScriptObject cssProperties, String propName, int type) /*-{
        return cssProperties.getPropertyCSSValue(propName).getFloatValue(type);
    }-*/;


    protected native JavaScriptObject getCssProperties(Element styleElem, String pseudoElt) /*-{
        return $doc.defaultView.getComputedStyle(styleElem, pseudoElt);
    }-*/;
}
