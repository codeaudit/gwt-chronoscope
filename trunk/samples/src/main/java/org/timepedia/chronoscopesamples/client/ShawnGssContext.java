package org.timepedia.chronoscopesamples.client;

import org.timepedia.chronoscope.client.browser.IEGssContext;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.MockGssContext;
import org.timepedia.chronoscope.client.gss.MockGssProperties;
import org.timepedia.chronoscope.client.canvas.Color;

/**
 *
 */
public class ShawnGssContext extends MockGssContext {

  public GssProperties getProperties(GssElement gssElem, String pseudoElt) {
    
    if ("line".equals(gssElem.getType())) {
      return new ShawnLineGssProperties();
    }
    if ("fill".equals(gssElem.getType())) {
      return new ShawnFillGssProperties();
    }
     if ("point".equals(gssElem.getType())) {
      return new ShawnPointGssProperties(pseudoElt);
    }
    else if("plot".equals(gssElem.getType()))
       return new ShawnPlotGssProperties();
    return super.getProperties(gssElem,
        pseudoElt);   
  }
  
   static class ShawnLineGssProperties extends MockGssProperties {

    public ShawnLineGssProperties() {
      this.color = new Color("rgb(0,0,255)");
      this.lineThickness = 2;
      this.shadowBlur=0;
      this.shadowOffsetX=0;
      this.shadowOffsetY=0;
    }
  }
  static class ShawnPlotGssProperties extends MockGssProperties {

    public ShawnPlotGssProperties() {
      this.bgColor = new Color("transparent");
    }
  }

  private class ShawnFillGssProperties extends GssProperties {

    private ShawnFillGssProperties() {
      this.bgColor = new Color("rgb(100, 100, 255)");
    }
  }

  private class ShawnPointGssProperties extends GssProperties {

    private ShawnPointGssProperties(String pseudoElt) {
      this.visible = "hover".equals(pseudoElt) ? true : false;
      this.size = "hover".equals(pseudoElt) ? 5 : 5;
      this.bgColor = "hover".equals(pseudoElt) ? new Color("rgb(255,0,0)") : new Color("rgb(0,0,255)");
      this.color = new Color("rgb(255,255,255)");
      this.lineThickness = 2;
    }
  }
}
