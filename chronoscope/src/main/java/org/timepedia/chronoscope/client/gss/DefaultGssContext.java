package org.timepedia.chronoscope.client.gss;

import org.timepedia.chronoscope.client.canvas.Color;

/**
 * Hardcoded default stylesheet
 */
public class DefaultGssContext extends MockGssContext {
     public GssProperties getProperties(GssElement gssElem, String pseudoElt) {

    if ("line".equals(gssElem.getType())) {
      return new DefaultLineGssProperties(gssElem.getParentGssElement(), pseudoElt);
    }
    if ("fill".equals(gssElem.getType())) {
      return new DefaultFillGssProperties();
    }
    if ("point".equals(gssElem.getType())) {
      return new DefaultPointGssProperties(gssElem.getParentGssElement(), pseudoElt);
    } else if ("plot".equals(gssElem.getType())) {
      return new DefaultPlotGssProperties();
    } else if ("axis".equals(gssElem.getType())) {
      return new DefaultRangeAxisGssProperties(pseudoElt);
    } else if ("grid".equals(gssElem.getType())) {
      return new DefaultGridGssProperties(pseudoElt);
    } else if ("label".equals(gssElem.getType())) {
      return new DefaultLabelGssProperties(gssElem.getParentGssElement());
    } else if ("tick".equals(gssElem.getType())) {
      return new DefaultTickGssProperties();
    }

    return super.getProperties(gssElem, pseudoElt);
  }

  static class DefaultLineGssProperties extends MockGssProperties {

    public DefaultLineGssProperties(GssElement parentGssElement, String pseudoElt) {
      if("disabled".equals(pseudoElt)) this.transparency=0.3;
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

  static class DefaultPlotGssProperties extends MockGssProperties {

    public DefaultPlotGssProperties() {
      this.bgColor = new Color("transparent");
      this.fontFamily = "Helvetica";
      this.fontSize = "12pt";
    }
  }
  static class DefaultTickGssProperties extends MockGssProperties {

    public DefaultTickGssProperties() {
      this.bgColor = new Color("transparent");
      this.fontFamily = "Helvetica";
      this.fontSize = "12pt";
      this.color = new Color("#000000");
    }
  }

  static class DefaultLabelGssProperties extends MockGssProperties {

    public DefaultLabelGssProperties(GssElement parentGssElement) {
      this.tickAlign = "above";
      if ("axis".equals(parentGssElement.getType())) {
        this.visible=false;
      }
    }
  }

  private class DefaultFillGssProperties extends GssProperties {

    private DefaultFillGssProperties() {
      this.bgColor = new Color("rgba(0,0,0,0)");
    }
  }

  private class DefaultRangeAxisGssProperties extends GssProperties {

    private DefaultRangeAxisGssProperties(String pseudoElt) {
      this.tickPosition = "inside";
      this.bgColor = new Color("#FFFFFF");
      this.fontFamily = "Helvetica";
      this.fontSize = "12pt";
      this.color = new Color("#000000");
    }
  }

  private class DefaultPointGssProperties extends GssProperties {

    private DefaultPointGssProperties(GssElement parentGssElement, String pseudoElt) {
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
      this.visible = ("hover".equals(pseudoElt) || "focus".equals(pseudoElt)) ? true : false;
      this.size = "hover".equals(pseudoElt) ? 5 : 5;
      this.bgColor = "hover".equals(pseudoElt) ? new Color("rgb(50,0,255)")
          : new Color("rgb(0,0,255)");
      this.color = "focus".equals(pseudoElt) ? new Color("rgb(0,0,0)") : new Color("rgb(255,255,255)");
      this.lineThickness = 2;
    }
  }

  private class DefaultGridGssProperties extends MockGssProperties {

    private DefaultGridGssProperties(String psuedoElt) {
      this.color = new Color("rgba(200,200,200,255)");
      this.fontFamily = "Helvetica";
      this.fontSize = "8pt";
      this.transparency = 1.0f;
      this.lineThickness = 0;
    }
  }

  protected static String colors[] = {  "#2E43DF", "#2CAA1B", "#C21C1C",
     "#E98419", "#F8DD0D", "#A72AA2"
      
  };
}
