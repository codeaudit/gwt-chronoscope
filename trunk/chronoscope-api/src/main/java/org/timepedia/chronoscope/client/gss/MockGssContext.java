package org.timepedia.chronoscope.client.gss;

import org.timepedia.chronoscope.client.canvas.Color;

public class MockGssContext extends GssContext {
  
  // Determines the dataset color for a given GSS class.
  protected DatasetColorMap datasetColorMap = new DatasetColorMap(); 
  
  public GssProperties getProperties(GssElement gssElem, String pseudoElt) {
    final String elementType = gssElem.getType();
    
    GssProperties gssProps = new MockGssProperties();
    
    if ("line".equals(elementType) || "step".equals(elementType)
        || "candlestick".equals(elementType)) {
      configLineProps(gssProps, gssElem.getParentGssElement(), pseudoElt);
    }
    else if ("bar".equals(elementType)) {
      configBarProps(gssProps, gssElem.getParentGssElement(), pseudoElt);
    }
    else if ("point".equals(elementType)) {
      configPointProps(gssProps, gssElem.getParentGssElement(), pseudoElt);
    }
    else if ("plot".equals(elementType)) {
      configPlotProps(gssProps);
    }
    else if ("fill".equals(elementType)) {
      configFillProps(gssProps, gssElem.getParentGssElement());
    }
    else if ("marker".equals(elementType)) {
      configMarkerProps(gssProps);
    }
    else if ("domainmarker".equals(elementType)) {
      configDomainMarkerProps(gssProps);
    }
    else if ("rangemarker".equals(elementType)) {
      configRangeMarkerProps(gssProps);
    }
    else if ("axis".equals(elementType) || "axislegend"
        .equals(elementType)) {
      configRangeAxisProps(gssProps);
    }
    else if ("axes".equals(elementType)) {
      configRangeAxesProps(gssProps);
    }
    else if ("grid".equals(elementType)) {
      configGridProps(gssProps);
    }
    else if ("overview".equals(elementType)) {
      configOverviewProps(gssProps);
    }
    else if ("tick".equals(elementType)) {
      configTickProps(gssProps);
    }
    else if ("shadow".equals(elementType)) {
      // do nothing for now...
    }
    else if ("label".equals(gssElem.getType())) {
      configLabelProps(gssProps);
    }
    else if("crosshair".equals(gssElem.getType()) || "guideline".equals(gssElem.getType())) {
      configCrosshairProps(gssProps);
    }
    return gssProps;
  }

  protected boolean isDisabled(String pseudoElt) {
    return "disabled".equals(pseudoElt);
  }
  
  protected boolean isHover(String pseudoElt) {
    return "hover".equals(pseudoElt);
  }
  
  protected boolean isFocus(String pseudoElt) {
    return "focus".equals(pseudoElt);
  }
  
  private void configCrosshairProps(GssProperties p) {
    p.visible = false;
  }
  
  private void configBarProps(GssProperties p, GssElement elt, String pseudoElt) {
    configLineProps(p, elt, pseudoElt);
    // width represents the percentage of maximum bar width.  E.g. a value of 
    // 100 means each bar will be drawn at maximum width (i.e. all bars will 
    // be touching each other).
    p.width = 75;
  }
  
  private void configLineProps(GssProperties p, GssElement elt, String pseudoElt) {
    p.lineThickness = 1;
    p.shadowBlur = 0;
    p.shadowOffsetX = 0;
    p.shadowOffsetY = 0;
    
    p.color = datasetColorMap.get(elt);
    
    if (isDisabled(pseudoElt)) {
      p.transparency = 0.3f;
    }
  }

  private void configDomainMarkerProps(GssProperties p) {
    p.bgColor = new Color("#10f410");
    p.color = Color.BLACK;
    p.transparency = 0.1f;
    p.lineThickness = 5;
    p.visible  =true;
  }
  
  private void configFillProps(GssProperties p, GssElement elt) {
    // TODO: p.visible=false does not work, it seems the way to hide the fill is with transparency
    p.bgColor = datasetColorMap.get(elt);
    p.transparency = 0.0f;
  }
  
  private void configGridProps(GssProperties p) {
    p.color = new Color("gainsboro");
    p.visible = false;
  }
  
  private void configMarkerProps(GssProperties p) {
    p.bgColor = new Color("peachpuff");
    p.color = new Color("navy");
    p.lineThickness = 0.5;
    p.fontSize = "8pt";
  }
  
  private void configOverviewProps(GssProperties p) {
    p.bgColor = new Color("#99CCFF");
    p.color = new Color("#0099FF");
    p.transparency = 0.4;
    p.lineThickness = 2;
  }
  
  private void configPointProps(GssProperties p, GssElement elt, String pseudoElt) {
    boolean isHover = isHover(pseudoElt);
    boolean isFocus = isFocus(pseudoElt);
    
    p.size = isHover ? 5 : 4;
    p.visible = (isHover || isFocus);
    p.lineThickness = 2;
    
    // Determines the color of the point's outer ring
    p.color = isFocus ? new Color("khaki") : new Color("olive");
    // Determines the color of the point's center area
    p.bgColor = isHover ? new Color("lightgreen") : new Color("lightblue");
  }

  private void configPlotProps(GssProperties p) {
    p.bgColor = Color.TRANSPARENT;
  }

  private void configRangeAxesProps(GssProperties p) {
    p.bgColor = Color.WHITE;
  }

  private void configRangeAxisProps(GssProperties p) {
    p.tickPosition = "inside";
    p.bgColor = Color.TRANSPARENT;
    p.fontSize = "9pt";
  }

  private void configRangeMarkerProps(GssProperties p) {
    p.bgColor = new Color("powderblue");
  }
  
  private void configTickProps(GssProperties p) {
  }
  
  private void configLabelProps(GssProperties p) {
    p.tickAlign = "above";
    p.fontSize="9pt";
  }

  /**
   * Maps a GSS class (presumably referring to some dataset) to a color.
   */
  protected static final class DatasetColorMap {
    private static Color[] defaultSerieColors = {
        new Color("red"),
        new Color("blue"),
        new Color("brown"),
        new Color("gold"),
        new Color("burlywood"),
        new Color("cadetblue"),
        new Color("chartreuse"),
        new Color("chocolate"),
        new Color("aqua"),
        new Color("darkgoldenrod"),
        new Color("darkgreen"),
        new Color("darkmagenta"),
        new Color("darkred"),
        new Color("forestgreen"),
        new Color("lightgreen"),
        new Color("blueviolet"),
        new Color("crimson"),
        new Color("black")
    };

    public Color get(GssElement gssElement) {
      int n = gssElement == null ? 0 : Integer.parseInt(gssElement.getTypeClass().replaceFirst("^[^\\d]*(\\d*).*$", "0$1"));
      return defaultSerieColors[n % defaultSerieColors.length];
    }
  }
}
