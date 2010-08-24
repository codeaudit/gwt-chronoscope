/**
 * 
 */
package org.timepedia.chronoscope.client.gss.parser;

import java.util.List;

import com.google.gwt.junit.client.GWTTestCase;

public class GssParserTest extends GWTTestCase {

  public String getModuleName() {
    // Uncomment it to check with native javascript.
    // return "org.timepedia.chronoscope.ChronoscopeAPI";
    return null;
  }

  public void testParser() throws Exception {
    List<GssRule> l =  GssParser.parse("marker {\n" +
            "background-color: yellow;\n" + 
            "color: black;\n" + 
            "font-family:  Verdana ;\n" +
            "}\n" 
            );
    assertEquals("marker", l.get(0).getSelectors().get(0).getSimpleSelectors().get(0).getElementName());
    assertEquals("background-color", l.get(0).getProperties().get(0).getPropertyName());
    assertEquals("yellow", l.get(0).getProperties().get(0).getPropertyValue());
    assertEquals("color", l.get(0).getProperties().get(1).getPropertyName());
    assertEquals("black", l.get(0).getProperties().get(1).getPropertyValue());
    assertEquals("font-family", l.get(0).getProperties().get(2).getPropertyName());
    assertEquals("Verdana", l.get(0).getProperties().get(2).getPropertyValue());
  }

  public void testParserWithoutSemicolon() throws Exception {
    List<GssRule> l =  GssParser.parse("marker {\n" +
            "background-color: yellow\n" + 
            "color: black; \n" + 
            "font-family:  Verdana ;\n" +
            "}\n" 
            );
    assertEquals("marker", l.get(0).getSelectors().get(0).getSimpleSelectors().get(0).getElementName());
    assertEquals("background-color", l.get(0).getProperties().get(0).getPropertyName());
    assertEquals("yellow", l.get(0).getProperties().get(0).getPropertyValue());
    assertEquals("color", l.get(0).getProperties().get(1).getPropertyName());
    assertEquals("black", l.get(0).getProperties().get(1).getPropertyValue());
    assertEquals("font-family", l.get(0).getProperties().get(2).getPropertyName());
    assertEquals("Verdana", l.get(0).getProperties().get(2).getPropertyValue());
  }

  public void skip_testParserWithComments() throws Exception {
    List<GssRule> l =  GssParser.parse("/* multiline \n comment */ marker {\n" +
            "background-color: yellow\n" + 
            "color: black // a comment \n" + 
            "font-family:  Verdana ; // another comment\n" +
            "}\n" 
            );
    assertEquals("marker", l.get(0).getSelectors().get(0).getSimpleSelectors().get(0).getElementName());
    assertEquals("background-color", l.get(0).getProperties().get(0).getPropertyName());
    assertEquals("yellow", l.get(0).getProperties().get(0).getPropertyValue());
    assertEquals("color", l.get(0).getProperties().get(1).getPropertyName());
    assertEquals("black", l.get(0).getProperties().get(1).getPropertyValue());
    assertEquals("font-family", l.get(0).getProperties().get(2).getPropertyName());
    assertEquals("Verdana", l.get(0).getProperties().get(2).getPropertyValue());
  }
  
  public void testParserOneLine() throws Exception {
    List<GssRule> l =  GssParser.parse("marker {color: black} ; selector{attribute:value}"); 
    assertEquals("marker", l.get(0).getSelectors().get(0).getSimpleSelectors().get(0).getElementName());
    assertEquals("color", l.get(0).getProperties().get(0).getPropertyName());
    assertEquals("black", l.get(0).getProperties().get(0).getPropertyValue());
    assertEquals("selector", l.get(1).getSelectors().get(0).getSimpleSelectors().get(0).getElementName());
    assertEquals("attribute", l.get(1).getProperties().get(0).getPropertyName());
    assertEquals("value", l.get(1).getProperties().get(0).getPropertyValue());
  }


}
