package org.timepedia.chronoscope.gviz.api.client;

import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.gss.MockGssContext;

/**
 *
 */
public class GVizGssContext extends MockGssContext {

  public GssProperties getProperties(GssElement gssElem, String pseudoElt) {
  
    GssProperties gssProps = super.getProperties(gssElem, pseudoElt);
    
    if ("fill".equals(gssElem.getType())) {
      gssProps.setColor(Color.TRANSPARENT);
    } else if ("point".equals(gssElem.getType())) {
      gssProps.size = 5;
      gssProps.lineThickness = isFocus(pseudoElt) ? 4 : 2;
    } else if ("plot".equals(gssElem.getType())) {
      gssProps.fontSize = "12pt";
    } else if ("axis".equals(gssElem.getType())) {
      gssProps.fontSize = "12pt";
    } else if ("grid".equals(gssElem.getType())) {
      gssProps.fontSize = "8pt";
    } else if ("label".equals(gssElem.getType())) {
      gssProps.tickAlign = "above";
    } else if ("tick".equals(gssElem.getType())) {
      gssProps.fontSize = "12pt";
    }

    return gssProps;
  }

}
