package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Opera9's DOM level 2 CSS has many implementation differences compared to
 * Firefox/WebKit, this class is just for testing right now
 */
public class CssGssPropertiesOpera extends CssGssProperties {

  protected native double getCssPropertyValueFloat(
      JavaScriptObject cssProperties, String propName) /*-{
          return parseFloat(cssProperties.getPropertyValue(propName));
      }-*/;

  protected native double getCssPropertyValueFloat(
      JavaScriptObject cssProperties, String propName, int type) /*-{
          return parseFloat(cssProperties.getPropertyValue(propName));
      }-*/;

  protected native int getCssPropertyValuePixels(JavaScriptObject cssProperties,
      String propName) /*-{
          return parseInt(cssProperties.getPropertyValue(propName));
      }-*/;

  protected native String getCssPropertyValueString(
      JavaScriptObject cssProperties, String propName) /*-{

         return cssProperties.getPropertyValue(propName);
      }-*/;
}
