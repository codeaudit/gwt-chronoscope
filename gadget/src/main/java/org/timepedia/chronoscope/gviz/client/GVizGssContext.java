package org.timepedia.chronoscope.gviz.client;

import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.gss.MockGssContext;
import org.timepedia.chronoscope.client.gss.MockGssProperties;

/**
 *
 */
public class GVizGssContext extends MockGssContext {

  public GssProperties getProperties(GssElement gssElem, String pseudoElt) {

    if ("line".equals(gssElem.getType())) {
      return new ShawnLineGssProperties(gssElem.getParentGssElement());
    }
    if ("fill".equals(gssElem.getType())) {
      return new ShawnFillGssProperties();
    }
    if ("point".equals(gssElem.getType())) {
      return new ShawnPointGssProperties(pseudoElt);
    } else if ("plot".equals(gssElem.getType())) {
      return new ShawnPlotGssProperties();
    } else if ("axis".equals(gssElem.getType())) {
      return new ShawnRangeAxisGssProperties(pseudoElt);
    } else if ("grid".equals(gssElem.getType())) {
      return new ShawnGridGssProperties(pseudoElt);
    } else if ("label".equals(gssElem.getType())) {
      return new ShawnLabelGssProperties(gssElem.getParentGssElement());
    } else if ("tick".equals(gssElem.getType())) {
      return new ShawnTickGssProperties();
    }

    return super.getProperties(gssElem, pseudoElt);
  }

  static class ShawnLineGssProperties extends MockGssProperties {

    public ShawnLineGssProperties(GssElement parentGssElement) {
      // contains s0, s1, ... s#
      String seriesNum = parentGssElement.getTypeClass();
      if(seriesNum.indexOf("s0") == -1) {
        this.color = new Color("rgb(255,0,255)");
        
      }
      else {
        this.color = new Color("rgb(0,0,255)");
      }
      this.lineThickness = 1;
      this.shadowBlur = 0;
      this.shadowOffsetX = 0;
      this.shadowOffsetY = 0;
    }
  }

  static class ShawnPlotGssProperties extends MockGssProperties {

    public ShawnPlotGssProperties() {
      this.bgColor = new Color("transparent");
      this.fontFamily = "Helvetica";
      this.fontSize = "12pt";
    }
  }
  static class ShawnTickGssProperties extends MockGssProperties {

    public ShawnTickGssProperties() {
      this.bgColor = new Color("transparent");
      this.fontFamily = "Helvetica";
      this.fontSize = "12pt";
      this.color = new Color("#000000");
    }
  }

  static class ShawnLabelGssProperties extends MockGssProperties {

    public ShawnLabelGssProperties(GssElement parentGssElement) {
      this.tickAlign = "above";
      if ("axis".equals(parentGssElement.getType())) {
        this.visible=false;
      }
    }
  }

  private class ShawnFillGssProperties extends GssProperties {

    private ShawnFillGssProperties() {
      this.bgColor = new Color("rgba(0,0,0,0)");
    }
  }

  private class ShawnRangeAxisGssProperties extends GssProperties {

    private ShawnRangeAxisGssProperties(String pseudoElt) {
      this.tickPosition = "inside";
      this.bgColor = new Color("#FFFFFF");
      this.fontFamily = "Helvetica";
      this.fontSize = "12pt";
      this.color = new Color("#000000");
    }
  }

  private class ShawnPointGssProperties extends GssProperties {

    private ShawnPointGssProperties(String pseudoElt) {
      this.visible = "hover".equals(pseudoElt) ? true : false;
      this.size = "hover".equals(pseudoElt) ? 5 : 5;
      this.bgColor = "hover".equals(pseudoElt) ? new Color("rgb(50,0,255)")
          : new Color("rgb(0,0,255)");
      this.color = new Color("rgb(255,255,255)");
      this.lineThickness = 2;
    }
  }

  private class ShawnGridGssProperties extends MockGssProperties {

    private ShawnGridGssProperties(String psuedoElt) {
      this.color = new Color("rgba(200,200,200,255)");
      this.fontFamily = "Helvetica";
      this.fontSize = "8pt";
      this.transparency = 1.0f;
      this.lineThickness = 0;
    }
  }
}
