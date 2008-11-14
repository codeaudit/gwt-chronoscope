package org.timepedia.chronoscopesamples.client;

import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.gss.*;

/**
 * 
 */
public class ShawnGssContext extends DefaultGssContext {

  public GssProperties getProperties(GssElement gssElem, String pseudoElt) {
    
    GssProperties gssProps = super.getProperties(gssElem, pseudoElt);

    if ("fill".equals(gssElem.getType())) {
      if ("disabled".equals(pseudoElt)) {
        gssProps.bgColor = new Color(0, 0, 0, 0);
      } else {
        gssProps.bgColor = this.datasetColorMap.get(gssElem.getParentGssElement());
        gssProps.transparency = 0.3;
      }
    }

    return gssProps;
  }

}
