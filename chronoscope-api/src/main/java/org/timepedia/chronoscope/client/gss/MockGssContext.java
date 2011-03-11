package org.timepedia.chronoscope.client.gss;

import org.timepedia.chronoscope.client.canvas.Color;

public class MockGssContext extends GssContext {
  
  // Determines the dataset color for a given GSS class.
  protected DatasetColorMap datasetColorMap = new DatasetColorMap(); 
  
  public GssProperties getProperties(GssElement gssElem, String pseudoElt) {
    final String elementType = gssElem.getType();
    
    GssProperties gssProps = new MockGssProperties();
    
    if ("line".equals(elementType) || "step".equals(elementType) || "candlestick".equals(elementType)) {
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
    else if ("axis".equals(elementType)) {
      configAxisProps(gssProps);
    }
    else if ("axislegend".equals(elementType)) {
        configLegendProps(gssProps);
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
    else if ("lens".equals(elementType)) {
      configOverviewLensProps(gssProps);
    }
    else if ("tick".equals(elementType)) {
      configTickProps(gssProps);
    }
    else if ("shadow".equals(elementType)) {
      // do nothing for now...
    }
    else if ("label".equals(elementType)) {
      configLabelProps(gssProps);
    }
    else if ("labels".equals(elementType)) {
      configLabelsProps(gssProps, gssElem.getParentGssElement());
    }
    else if("crosshair".equals(elementType) || "guideline".equals(elementType)) {
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
    p.color = Color.GRAY;
    p.bgColor = Color.GRAY;
    p.visible = false;
    p.valueVisible = true;
    p.labelVisible = false;
    p.transparency = 0.5f;
    p.dateFormat = "auto";
  }

  private void configLabelsProps(GssProperties p, GssElement elt) {
      String parentType = elt.getType();

      p.labelVisible = "axislegend".equals(parentType) ? true : false;
      p.valueVisible = "crosshair".equals(parentType) ? true : false;
      p.transparency = "crosshair".equals(parentType) ? 0.75f : 1.0f;
      p.fontSize = "crosshair".equals(parentType) ? "8pt" : "9pt";
      p.lineThickness = 1;

      p.color = Color.BLACK; // datasetColorMap.get(elt);
      p.bgColor = Color.BLACK; // ? p.color = datasetColorMap.get(elt) : Color.TRANSPARENT;

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
    p.color = Color.GRAY;
    p.transparency = 0.1f;
    p.lineThickness = 2;
    p.visible = true;
    p.fontSize = "8pt";
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

  // TODO - might make sense to rename color and bgColor to strokeColor and fillColor

  private void configOverviewProps(GssProperties p) {
    p.bgColor = Color.WHITE;
    p.color = Color.TRANSPARENT;
    p.transparency = 0.2;
    p.lineThickness = 0;
  }

  private void configOverviewLensProps(GssProperties p) {
    p.color = Color.LIGHTGRAY;
    p.transparency = 0.5;
    p.lineThickness = 1;
    p.borderBottom=0;
    p.borderTop=0;
    p.borderLeft=1;
    p.borderRight=1;
  }

  
  private void configPointProps(GssProperties p, GssElement elt, String pseudoElt) {
    boolean isHover = isHover(pseudoElt);
    boolean isFocus = isFocus(pseudoElt);
    boolean isDisabled = isDisabled(pseudoElt);

    p.visible = (isHover || isFocus);

    // p.size = isFocus ? 2 : 2;
    p.size = 2;

    // p.lineThickness = isFocus ? 2 : 1;
    p.lineThickness = 2;

    p.color = datasetColorMap.get(elt);
    // p.bgColor = isFocus ? p.color = datasetColorMap.get(elt) : Color.TRANSPARENT;
    p.bgColor = datasetColorMap.get(elt);

    p.transparency = isFocus ? 0.75 : 0.5;
    p.transparency = isDisabled ? 0.5 : p.transparency;

    // Determines the color of the point's outer ring
    // p.color = isFocus ? new Color("khaki") : new Color("olive");
    // Determines the color of the point's center area
    // p.bgColor = Color.TRANSPARENT; // let the underlying line show through.
    // .bgColor = isHover ? new Color("lightgreen") : new Color("lightblue");

    p.shadowBlur = 0;
    p.shadowOffsetX = 0;
    p.shadowOffsetY = 0;
  }

  private void configPlotProps(GssProperties p) {
    p.bgColor = Color.TRANSPARENT;
  }

  private void configRangeAxesProps(GssProperties p) {
    p.bgColor = Color.TRANSPARENT;
  }

  private void configLegendProps(GssProperties p) {
    p.visible = true;
    p.valueVisible = false;
    p.labelVisible = true;
    p.bgColor = Color.TRANSPARENT;
  }

  private void configAxisProps(GssProperties p) {
    p.color = Color.GRAY;
    p.tickPosition = "inside";
    p.bgColor = Color.TRANSPARENT;
    p.fontSize = "8pt";
    p.display = "framed"; // use actual min, max to denote range axis frame
  }

  private void configRangeMarkerProps(GssProperties p) {
    p.bgColor = new Color("powderblue");
    p.color = Color.GRAY;
    p.transparency = 0.1f;
    p.lineThickness = 5;
    p.visible = true;
  }
  
  private void configTickProps(GssProperties p) {
    p.lineThickness = 1;
    p.color = Color.GRAY;
    p.bgColor = Color.TRANSPARENT;
  }
  
  private void configLabelProps(GssProperties p) {
    p.tickAlign = "above";
    p.fontSize="9pt";
    p.color = Color.GRAY;
    p.bgColor = Color.TRANSPARENT;
  }

  /**
   * Maps a GSS class (presumably referring to some dataset) to a color.
   */
  protected static final class DatasetColorMap {
    private static Color[] defaultSeriesColors = {
        new Color("blue"),
        new Color("green"),
        new Color("red"),
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
      return defaultSeriesColors[n % defaultSeriesColors.length];
    }
  }
}
