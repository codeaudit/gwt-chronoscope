package org.timepedia.chronoscope.gviz.api.client;

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
      return new GVizLineGssProperties(gssElem.getParentGssElement());
    }
    if ("fill".equals(gssElem.getType())) {
      return new GVizFillGssProperties();
    }
    if ("point".equals(gssElem.getType())) {
      return new GVizPointGssProperties(gssElem.getParentGssElement(), pseudoElt);
    } else if ("plot".equals(gssElem.getType())) {
      return new GVizPlotGssProperties();
    } else if ("axis".equals(gssElem.getType())) {
      return new GVizRangeAxisGssProperties(pseudoElt);
    } else if ("grid".equals(gssElem.getType())) {
      return new GVizGridGssProperties(pseudoElt);
    } else if ("label".equals(gssElem.getType())) {
      return new GVizLabelGssProperties(gssElem.getParentGssElement());
    } else if ("tick".equals(gssElem.getType())) {
      return new GVizTickGssProperties();
    }

    return super.getProperties(gssElem, pseudoElt);
  }

  static class GVizLineGssProperties extends MockGssProperties {

    public GVizLineGssProperties(GssElement parentGssElement) {
      // contains s0, s1, ... s#
      String seriesNum = parentGssElement.getTypeClass();
      if(seriesNum.matches(".*\\bs\\d+\\b")) {
        int ind = seriesNum.indexOf("s");
        if(ind != -1) {
          int snum = 0;
          try {
            snum = Integer.parseInt(seriesNum.substring(ind+1).trim());
          } catch (NumberFormatException e) {
          }
          this.color = new Color(colors[Math.min(snum, colors.length-1)]);
          
        }
      }
      else {
        this.color = new Color("rgb(255,0,255)");
      }
      this.lineThickness = 1;
      this.shadowBlur = 0;
      this.shadowOffsetX = 0;
      this.shadowOffsetY = 0;
    }
  }

  static class GVizPlotGssProperties extends MockGssProperties {

    public GVizPlotGssProperties() {
      this.bgColor = new Color("transparent");
      this.fontFamily = "Helvetica";
      this.fontSize = "12pt";
    }
  }
  static class GVizTickGssProperties extends MockGssProperties {

    public GVizTickGssProperties() {
      this.bgColor = new Color("transparent");
      this.fontFamily = "Helvetica";
      this.fontSize = "12pt";
      this.color = new Color("#000000");
    }
  }

  static class GVizLabelGssProperties extends MockGssProperties {

    public GVizLabelGssProperties(GssElement parentGssElement) {
      this.tickAlign = "above";
      if ("axis".equals(parentGssElement.getType())) {
        this.visible=false;
      }
    }
  }

  private class GVizFillGssProperties extends GssProperties {

    private GVizFillGssProperties() {
      this.bgColor = new Color("rgba(0,0,0,0)");
    }
  }

  private class GVizRangeAxisGssProperties extends GssProperties {

    private GVizRangeAxisGssProperties(String pseudoElt) {
      this.tickPosition = "inside";
      this.bgColor = new Color("#FFFFFF");
      this.fontFamily = "Helvetica";
      this.fontSize = "12pt";
      this.color = new Color("#000000");
    }
  }

  private class GVizPointGssProperties extends GssProperties {

    private GVizPointGssProperties(GssElement parentGssElement, String pseudoElt) {
      String seriesNum = parentGssElement.getTypeClass();
      if(seriesNum.matches(".*\\bs\\d+\\b")) {
        int ind = seriesNum.indexOf("s");
        if(ind != -1) {
          int snum = 0;
          try {
            snum = Integer.parseInt(seriesNum.substring(ind+1).trim());
          } catch (NumberFormatException e) {
          }
          this.color = new Color(colors[Math.min(snum, colors.length-1)]);
          
        }
      }
      else {
        this.color = new Color("rgb(255,0,255)");
      }
      this.visible = "hover".equals(pseudoElt) ? true : false;
      this.size = "hover".equals(pseudoElt) ? 5 : 5;
      this.bgColor = "hover".equals(pseudoElt) ? new Color("rgb(50,0,255)")
          : new Color("rgb(0,0,255)");
      this.color = new Color("rgb(255,255,255)");
      this.lineThickness = 2;
    }
  }

  private class GVizGridGssProperties extends MockGssProperties {

    private GVizGridGssProperties(String psuedoElt) {
      this.color = new Color("rgba(200,200,200,255)");
      this.fontFamily = "Helvetica";
      this.fontSize = "8pt";
      this.transparency = 1.0f;
      this.lineThickness = 0;
    }
  }
  
  static String colors[] = {  "#2E43DF", "#2CAA1B", "#C21C1C",
     "#E98419", "#F8DD0D", "#A72AA2" 
      
  };
}
