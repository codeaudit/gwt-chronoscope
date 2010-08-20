package org.timepedia.chronoscope.client.browser;

import com.google.gwt.user.client.Element;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NodeList;

import org.timepedia.chronoscope.client.gss.parser.GssStylesheetGssContext;
import org.timepedia.chronoscope.client.gss.parser.GssParseException;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.gss.GssElement;

/**
 * Retrieves styles from <STYLE type="text/gss"> elements
 */
public class GssStyleElementGssContext extends BrowserGssContext implements
 CssGssViewSupport {

  GssStylesheetGssContext gssContext = new GssStylesheetGssContext();

  public GssStyleElementGssContext() {
  }

  @Override
  public void initialize(Element cssgss) {
    super.initialize(cssgss);
    String gss="";
    NodeList nl=Document.get().getElementsByTagName("style");
    for(int i=0; i<nl.getLength(); i++) {
      Element e = (Element)nl.getItem(i);
      if("text/gss".equals(e.getAttribute("type"))) {
        gss += e.getInnerText();
      }
    }
    if(!gss.trim().equals("")) {
      try {
        gssContext.parseStylesheet(gss);
      } catch (GssParseException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
  }

  @Override
  public GssProperties getProperties(GssElement gssElem, String pseudoElt) {
    return gssContext.getProperties(gssElem, pseudoElt);
  }

  public Element getGssCssElement() {
    return super.getElement();
  }
}
