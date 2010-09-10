/**
 * 
 */
package org.timepedia.chronoscope.client.gss.parser;

import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.render.GssElementImpl;

import com.google.gwt.junit.client.GWTTestCase;

public class GssContextTest extends GWTTestCase {

  public String getModuleName() {
    // Uncomment it to check with native javascript.
    // return "org.timepedia.chronoscope.ChronoscopeAPI";
    return null;
  }

  public void testChildrenWithId() {
    GssElement gssElem = new GssElementImpl("series", null, "s1 mid");
    GssStylesheetGssContext gss=new GssStylesheetGssContext();
    GssProperties p = gss.getProperties(gssElem, "");
    assertEquals("#000000", p.color.toString());
    assertEquals(true, p.visible);
    
    gss=new GssStylesheetGssContext("series.mid {visibility: hidden; color: red}");
    p = gss.getProperties(gssElem, "");
    assertEquals("#ff0000", p.color.toString());
    assertEquals(false, p.visible);
  }

  public void testChildrenSeriesWithId() {
    GssStylesheetGssContext gss=new GssStylesheetGssContext("series.s1 fill {color: black}; series.#mid fill {visibility: hidden; color: red}; series.s1 fill {color: green}; ");
    GssElement gssElem = new GssElementImpl("series", null, "s1 #mid");
    GssElement gssFill = new GssElementImpl("fill", gssElem);
    GssProperties p = gss.getProperties(gssFill, "");

    assertEquals("#ff0000", p.color.toString());
    assertEquals(false, p.visible);
  }
    
}
