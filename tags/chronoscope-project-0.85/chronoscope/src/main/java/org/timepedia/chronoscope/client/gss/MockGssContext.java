package org.timepedia.chronoscope.client.gss;

import org.timepedia.chronoscope.client.browser.BrowserGssContext;
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.render.LinearGradient;

public class MockGssContext extends BrowserGssContext {

  public GssProperties getProperties(GssElement gssElem, String pseudoElt) {
    if ("line".equals(gssElem.getType())) {
      return pseudoElt.equals("disabled") ? new MockLineCssProperties(pseudoElt,
          true) : new MockLineCssProperties(pseudoElt, false);
    }
    if ("bar".equals(gssElem.getType())) {
      return pseudoElt.equals("disabled") ? new MockBarCssProperties(pseudoElt,
          true) : new MockBarCssProperties(pseudoElt, false);
    }
    if ("point".equals(gssElem.getType())) {
      return pseudoElt.equals("hover") ? new MockHoverPointCssProperties(
          pseudoElt, pseudoElt.equals("disabled"))
          : new MockPointCssProperties(pseudoElt, pseudoElt.equals("disabled"));
    }
    if ("plot".equals(gssElem.getType())) {
      return new MockPlotCssProperties();
    }
    if ("axes".equals(gssElem.getType())) {
      return new MockAxesCssProperties();
    }
    if ("axis".equals(gssElem.getType()) || "axislegend"
        .equals(gssElem.getType())) {
      return new MockAxisCssProperties();
    }
    if ("shadow".equals(gssElem.getType())) {
      return new MockShadowCssProperties();
    }
    if ("focus".equals(gssElem.getType())) {
      return new MockFocusCssProperties();
    }
    if ("fill".equals(gssElem.getType())) {
      return new MockFillCssProperties(pseudoElt);
    }
    if ("overview".equals(gssElem.getType())) {
      return new MockOverviewCssProperties();
    } else if ("grid".equals(gssElem.getType())) {
      return new MockGridCssProperties();
    }
    if ("marker".equals(gssElem.getType())) {
      return new MockMarkerCssProperties();
    }
    if ("domainmarker".equals(gssElem.getType())) {
      return new MockDomainMarkerCssProperties();
    }
    if ("rangemarker".equals(gssElem.getType())) {
      return new MockRangeMarkerCssProperties();
    }
    return new MockPlotCssProperties();
  }

  static class MockLineCssProperties extends MockGssProperties {

    public MockLineCssProperties(String pseudoElt, boolean disabled) {
      this.color = new Color(
          "s1".equals(pseudoElt) ? "rgb(245,86,95)" : "rgb(70,118,118)");
      if (disabled) {
        transparency = 0.2f;
      }
    }
  }

  static class MockBarCssProperties extends MockGssProperties {

    public MockBarCssProperties(String pseudoElt, boolean disabled) {
      this.color = new Color(
          "s1".equals(pseudoElt) ? "rgb(245,86,95)" : "rgb(70,118,118)");
      if (disabled) {
        transparency = 0.2f;
      }
      this.width = 5;
    }
  }

  static class MockFocusCssProperties extends MockGssProperties {

    public MockFocusCssProperties() {
      // point.focus { color: gray; width: 5px; border-style: solid;
      // border-width: 2px; }

      color = new Color("#D0D0D0");
      size = 5;
      lineThickness = 2;
    }
  }

  static class MockPointCssProperties extends MockGssProperties {

    public MockPointCssProperties(String pseudoElt, boolean disabled) {
      this.size = 4;
      this.lineThickness = 2;
      this.color = new Color("s1".equals(pseudoElt) ? "#FFFFFF" : "#008000");
      this.bgColor = new Color("#004000");
      if (true || disabled) {
        this.visible = false;
      }
    }
  }

  static class MockHoverPointCssProperties extends MockPointCssProperties {

    public MockHoverPointCssProperties(String pseudoElt, boolean disabled) {
      super(pseudoElt, disabled);
      this.size = 6;
      this.lineThickness = 2;

      this.bgColor = new Color("#FF0000");
    }
  }

  static class MockAxesCssProperties extends MockGssProperties {

    public MockAxesCssProperties() {
      bgColor = new Color("#FFFFFF");
    }
  }

  class MockPlotCssProperties extends MockGssProperties {

    public MockPlotCssProperties() {
      super();
// ServerLinearGradient gradient = new ServerLinearGradient(0, 0, 0, 1);
// gradient.addColorStop(0, "#00ABEB");
// gradient.addColorStop(1, "#FFFFFF");
// this.bgColor = gradient;
      LinearGradient ag = getView().getCanvas().getRootLayer()
          .createLinearGradient(0, 0, 600, 453);
//                   new FlashLinearGradient(null, 0,0, 600, 453);
      ag.addColorStop(0, "#00ABEB");
      ag.addColorStop(1, "#FFFFFF");
      this.bgColor = ag;
//           this.bgColor=new Color("#0000FF");
    }
  }

  static class MockAxisCssProperties extends MockGssProperties {

    public MockAxisCssProperties() {
      bgColor = new Color("#FFFFFF");
      fontFamily = "Verdana";
      fontSize = "12pt";
    }
  }

  static class MockFillCssProperties extends MockGssProperties {

    public MockFillCssProperties(String pseudoElt) {
      this.visible = !"s1".equals(pseudoElt);
//    bgColor = new Color("rgb(245,86,95)");
//    FlashLinearGradient ag=new FlashLinearGradient(null, 0,0, 1,1);
//           ag.addColorStop(0, "#00ABEB");
//           ag.addColorStop(1, "#FFFFFF");
//           this.bgColor=ag;
      bgColor = new Color("#00ABAB");
      fontFamily = "Verdana";
      fontSize = "8pt";
      transparency = 0.4;
    }
  }

  static class MockOverviewCssProperties extends MockGssProperties {

    public MockOverviewCssProperties() {
      bgColor = new Color("#99CCFF");
      color = new Color("#0099FF");
      fontFamily = "Verdana";
      fontSize = "12pt";
      transparency = 0.4;
      lineThickness = 2;
    }
  }

  static class MockGridCssProperties extends MockGssProperties {

    public MockGridCssProperties() {
      color = new Color("rgba(200,200,200,255)");
      fontFamily = "Verdana";
      fontSize = "8pt";
      transparency = 1.0f;
    }
  }

  static class MockShadowCssProperties extends MockGssProperties {
  }

  static class MockMarkerCssProperties extends MockGssProperties {

    public MockMarkerCssProperties() {
      bgColor = new Color("#D0D0D0");
      color = new Color("#000000");
      lineThickness = 1;
      fontFamily = "Verdana";
    }
  }

  static class MockDomainMarkerCssProperties extends MockGssProperties {

    public MockDomainMarkerCssProperties() {
      bgColor = new Color("#10f410");
      color = new Color("#000000");
      transparency = 0.3;
      lineThickness = 5;
    }
  }

  static class MockRangeMarkerCssProperties extends MockGssProperties {

    public MockRangeMarkerCssProperties() {
      bgColor = new Color("#f41010");
      color = new Color("#000000");
    }
  }
}
