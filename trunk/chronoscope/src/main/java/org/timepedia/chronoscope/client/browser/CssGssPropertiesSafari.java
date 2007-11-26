package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Fixes a bug in Safari2
 */
public class CssGssPropertiesSafari extends CssGssProperties {


    protected native String getCssPropertyValueString(JavaScriptObject cssProperties, String propName) /*-{
        var prop=cssProperties.getPropertyCSSValue(propName);
        var type = prop.primitiveType;
        if(prop.primitiveType == 25)
        {
             return cssProperties.getPropertyValue(propName);
        }
        return prop.getStringValue(type);
    }-*/;
}
