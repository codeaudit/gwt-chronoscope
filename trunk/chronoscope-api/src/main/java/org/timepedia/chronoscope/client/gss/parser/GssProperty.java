package org.timepedia.chronoscope.client.gss.parser;

/**
 * Represents a GSS property declaration
 */
public class GssProperty {

  private String propertyName;

  private String propertyValue;

  public GssProperty(String propertyName, String propertyValue) {

    this.propertyName = propertyName;
    this.propertyValue = propertyValue;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public String getPropertyValue() {
    return propertyValue;
  }
  
  public String toString() {
    return  propertyName + ":" + propertyValue + ";"; 
  }
}
