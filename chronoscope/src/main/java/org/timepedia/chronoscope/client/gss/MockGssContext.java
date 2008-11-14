package org.timepedia.chronoscope.client.gss;

import org.timepedia.chronoscope.client.browser.BrowserGssContext;
import org.timepedia.chronoscope.client.canvas.Color;

import java.util.HashMap;
import java.util.Map;

public class MockGssContext extends BrowserGssContext {
  
  // Determines the dataset color for a given GSS class.
  protected DatasetColorMap datasetColorMap = new DatasetColorMap(); 
  
  public GssProperties getProperties(GssElement gssElem, String pseudoElt) {
    final String elementType = gssElem.getType();
    
    GssProperties gssProps = new MockGssProperties();
    
    if ("line".equals(elementType)) {
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
      configFillProps(gssProps);
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
      p.transparency = 0.3;
    }
  }

  private void configDomainMarkerProps(GssProperties p) {
    p.bgColor = new Color("#10f410");
    p.color = Color.BLACK;
    p.transparency = 0.3;
    p.lineThickness = 5;
  }
  
  private void configFillProps(GssProperties p) {
    p.visible = true;
    p.bgColor = Color.BLACK;
    p.transparency = 0.0f;
  }
  
  private void configGridProps(GssProperties p) {
    p.color = new Color("rgba(200,200,200,255)");
    p.fontFamily = "Helvetica";
    p.fontSize = "9pt";
    p.transparency = 1.0f;
    p.lineThickness = 0;
  }
  
  private void configMarkerProps(GssProperties p) {
    p.bgColor = new Color("#D0D0D0");
    p.color = Color.BLACK;
    p.lineThickness = 1;
    p.fontFamily = "Verdana";
  }
  
  private void configOverviewProps(GssProperties p) {
    p.bgColor = new Color("#99CCFF");
    p.color = new Color("#0099FF");
    p.fontFamily = "Verdana";
    p.fontSize = "12pt";
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
    p.color = isFocus ? Color.BLACK : Color.WHITE;
    // Determines the color of the point's center area
    p.bgColor = isHover ? new Color(50,0,255) : new Color(0,0,255);
  }

  private void configPlotProps(GssProperties p) {
    p.bgColor = new Color("transparent");
    p.fontFamily = "Helvetica";
    p.fontSize = "9pt";
  }
  
  private void configRangeAxesProps(GssProperties p) {
    p.bgColor = Color.WHITE;
  }

  private void configRangeAxisProps(GssProperties p) {
    p.tickPosition = "inside";
    p.bgColor = Color.WHITE;
    p.fontFamily = "Helvetica";
    p.fontWeight = "normal";
    p.fontSize = "9pt";
    p.color = Color.BLACK;
  }

  private void configRangeMarkerProps(GssProperties p) {
    p.bgColor = new Color("#f41010");
    p.color = Color.BLACK;
  }
  
  private void configTickProps(GssProperties p) {
    p.bgColor = new Color("transparent");
    p.fontFamily = "Helvetica";
    p.fontSize = "9pt";
    p.color = Color.BLACK;
  }

  /**
   * Maps a GSS class (presumably referring to some dataset) to a color.
   */
  protected static final class DatasetColorMap {
    // Use this color if the dataset color cannot be determined from the
    // provided GSS class string.
    private static final Color DEFAULT_COLOR = new Color("#FF00FF");
    
    private Map<String,Color> gssClass2color = new HashMap<String,Color>();
    
    public DatasetColorMap() {
      gssClass2color.put("s0", new Color("#2E43DF"));
      gssClass2color.put("s1", new Color("#2CAA1B"));
      gssClass2color.put("s2", new Color("#C21C1C"));
      gssClass2color.put("s3", new Color("#E98419"));
      gssClass2color.put("s4", new Color("#F8DD0D"));
      gssClass2color.put("s5", new Color("#A72AA2"));
    }
    
    public Color get(GssElement gssElement) {
      if (gssElement == null) {
        return DEFAULT_COLOR;
      }
      
      Color c = gssClass2color.get(gssElement.getTypeClass());
      return (c != null) ? c : DEFAULT_COLOR;
    }
  }
}
