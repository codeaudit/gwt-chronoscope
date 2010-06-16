package org.timepedia.chronoscope.client.gss;

import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.PaintStyle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Single point of entry to register all GssElements types and Properties Useful
 * for auto-generating JavaDoc and auto-extending Batik as well as processing
 * syntactic sugar CSS into real CSS (e.g. 'line-thickness' -> 'border-width')
 */
public class GssPropertyManager {

  private static Map<String, GssPropertyType> propertyMap
      = new HashMap<String, GssPropertyType>();

  public static Map<String, GssElementType> elementMap
      = new HashMap<String, GssElementType>();

  public static void registerProperty(GssPropertyType type) {
    propertyMap.put(type.getName(), type);
  }

  public static GssPropertyType lookupGssPropertyType(String typeName) {
    return propertyMap.get(typeName);
  }


  public static void registerElement(GssElementType type) {
    elementMap.put(type.getName(), type);
  }

  public static GssElementType lookupGssElementType(String typeName) {
    return elementMap.get(typeName);
  }

  public static Iterator<GssElementType> getAllElements() {
    return elementMap.values().iterator();
  }

  public static Iterator<GssPropertyType> getAllProperties() {
    return propertyMap.values().iterator();
  }
  
  public static class GssElementType {

    final private String typeName;

    private String[] classes;

    private GssElementType[] childElementTypes;

    private String docString;

    private String exampleString;

    final HashSet properties = new HashSet();

    public String getDocString() {
      return docString;
    }

    public String getExampleString() {
      return exampleString;
    }

    public String[] getClasses() {
      return classes;
    }

    protected GssElementType(String typeName, String[] classes,
        GssElementType[] childElementTypes, GssPropertyType[] propertyTypes,
        String docString, String exampleString) {
      this.typeName = typeName;
      this.classes = classes;

      this.childElementTypes = childElementTypes;
      this.docString = docString;
      this.exampleString = exampleString;
      for (int i = 0; i < propertyTypes.length; i++) {
        GssPropertyType type = propertyTypes[i];
        properties.add(type);
      }
      registerElement(this);
    }

    protected GssElementType(String typeName,
        GssElementType[] childElementTypes, GssPropertyType[] propertyTypes,
        String docString, String example) {

      this(typeName, new String[0], childElementTypes, propertyTypes, docString,
          example);
    }


    public GssPropertyType[] getProperties() {
      return (GssPropertyType[]) properties.toArray(new GssPropertyType[0]);
    }

    public String getName() {
      return typeName;
    }

    public GssElementType[] getChildTypes() {
      return childElementTypes;
    }
  }

  public static class GssPropertyType {

    private String propertyName;

    private String altCssName;

    private TypeUnits valueType;

    private String docString;

    public static enum TypeUnits {
      INTEGER {
        public Object parse(String str) {
          return Integer.parseInt(str);
        }
        public String getDocString() {
          return "An integer value.";
        }},

      PX {
        public Object parse(String str) {
          str = str.replace("px","");
          return Integer.parseInt(str);
        }
        public String getDocString() {
          return "An integer value in pixel units, with optional px suffix";
        }},

      FLOAT {
        public Object parse(String pval) {
          return Double.parseDouble(pval);
        }
        public String getDocString() {
          return "A floating point value";
        }
      },

      PT {
        public Object parse(String str) {
          return str;
        }
        public String getDocString() {
          return "A font point size, a number followed by the suffix 'pt'";
        }},

      URI {
        public Object parse(String str) {
          return str;
        }
        public String getDocString() {
          return "A URL of the format url(http://host.domain/some/path)";
        }},

      COLOR {
        public Object parse(String str) {
          return new Color(str);
        }
        public String getDocString() {
          return "A CSS color name, rgb triple rgb(r,g,b), hexadecimal triple "
              + "(e.g. #f0a2c9), an rgba quadruple rgba(r,g,b,a)";
        }},

      BGIMAGE {
        public Object parse(String str) {
          return new Color(str);
        }
        public String getDocString() {
          return "A url to an image to be placed in the background of the form "
              + "url(http://host.domain/some/image.png) or a gradient function "
              + "gradient(startx, starty, endx, endy, colorstop, "
              + "color, colorstop, color, ...)";
        }},

      STRING {
        public Object parse(String str) {
          return str;
        }
        public String getDocString() {
          return "A string with or without surrounding quotes.";
        }};

      public abstract Object parse(String pval);

      public abstract String getDocString();
    }

    public String getDocString() {
      return docString;
    }

    public TypeUnits getValueType() {
      return valueType;
    }

    protected GssPropertyType(String propertyName, String altCssName,
        TypeUnits valueType, String docString) {

      this.propertyName = propertyName;
      this.altCssName = altCssName;
      this.valueType = valueType;

      this.docString = docString;
      registerProperty(this);
    }

    public String getName() {
      return propertyName;
    }

    public void setPropertyFromString(GssProperties props, String pval) {
      switch (valueType) {
        case PX:
        case INTEGER:
          setPropertyInt(props, (Integer) valueType.parse(pval));
          break;
        case COLOR:
        case BGIMAGE:
          setPropertyPaintStyle(props, (PaintStyle) valueType.parse(pval));
          break;
        case PT:
        case URI:
        case STRING:
          setPropertyString(props, pval);
          break;
        case FLOAT:
          setPropertyDouble(props, (Double) valueType.parse(pval));
      }
    }

    protected void setPropertyDouble(GssProperties props, double aDouble) {

    }


    public void setPropertyString(GssProperties props, String pval) {

    }

    protected void setPropertyPaintStyle(GssProperties props,
        PaintStyle paintStyle) {

    }

    protected void setPropertyInt(GssProperties props, int num) {

    }
  }

  public static final GssPropertyType GSS_GROUP_PROPERTY
      = new GssPropertyType("group", "", GssPropertyType.TypeUnits.STRING,
      "Asserts that a series is to be grouped with other series having the same" 
          + " group id into a new composite dataseries.") {

    @Override
    public void setPropertyString(GssProperties props, String pval) {
      props.group = pval;
    }
  };

  public static final GssPropertyType GSS_DATE_FORMAT_PROPERTY
      = new GssPropertyType("date-format", "", GssPropertyType.TypeUnits.STRING,
      "Specifies the date format for labels which contain dates") {

    @Override
    public void setPropertyString(GssProperties props, String pval) {
      props.dateFormat = pval;
    }
  };

  public static final GssPropertyType GSS_NUMBER_FORMAT_PROPERTY
      = new GssPropertyType("number-format", "", GssPropertyType.TypeUnits.STRING,
      "Specifies the number format for values displayed") {

    @Override
    public void setPropertyString(GssProperties props, String pval) {
      props.dateFormat = pval;
    }
  };
  
  // NOTE "em" not supported, eg line-thickness:.1em; is invalid
  public static final GssPropertyType GSS_LINE_THICKNESS_PROPERTY
      = new GssPropertyType("line-thickness", "", GssPropertyType.TypeUnits.PX,
      "Specifies the thickness of lines drawn on the chart") {
    @Override
    protected void setPropertyInt(GssProperties props, int num) {
      props.lineThickness = num;
    }
  };


  // NOTE "em" not supported, eg line-thickness:.1em; is invalid
   public static final GssPropertyType GSS_POINT_SELECTION_PROPERTY
       = new GssPropertyType("point-selection", "", GssPropertyType.TypeUnits.STRING,
       "Specifies 'nearest' or 'domain' for hover selection, nearest selects " 
           + "the closest data point based on the (x,y) of the mouse, 'domain' " 
           + "selects the data points to" 
           + "the X position of the mouse.") {
     @Override
     public void setPropertyString(GssProperties props, String prop) {
       props.pointSelection = prop;
     }
   };
  
  public static final GssPropertyType GSS_OPACITY_PROPERTY
      = new GssPropertyType("opacity", "", GssPropertyType.TypeUnits.FLOAT,
      "Specifies the opacity value from 0 (transparent) to 1 (opaque)") {
    @Override
    protected void setPropertyDouble(GssProperties props, double aDouble) {
      props.transparency = aDouble;
    }
  };


  public static final GssPropertyType GSS_COLOR_PROPERTY = new GssPropertyType(
      "color", "", GssPropertyType.TypeUnits.COLOR,
      "A standard CSS color property") {
    @Override
    protected void setPropertyPaintStyle(GssProperties props,
        PaintStyle paintStyle) {
      props.color = (Color) paintStyle;
    }
  };

  public static final GssPropertyType GSS_BGCOLOR_PROPERTY
      = new GssPropertyType("background-color", "",
      GssPropertyType.TypeUnits.COLOR,
      "Standard CSS background-color property") {
    @Override
    protected void setPropertyPaintStyle(GssProperties props,
        PaintStyle paintStyle) {
      props.bgColor = paintStyle;
    }
  };

  public static final GssPropertyType GSS_BGIMAGE_PROPERTY
      = new GssPropertyType("background-image", "",
      GssPropertyType.TypeUnits.BGIMAGE,
      "CSS Background image property with extensions for gradients") {
    @Override
    protected void setPropertyPaintStyle(GssProperties props,
        PaintStyle paintStyle) {
      props.bgColor = paintStyle;
    }
  };

   public static final GssPropertyType GSS_DISPLAY_PROPERTY
      = new GssPropertyType("display", "",
      GssPropertyType.TypeUnits.STRING,
      "override display type for series, e.g. line, bar, step") {
    @Override
    public void setPropertyString(GssProperties props,
       String str) {
      props.display = str;
    }
  };

  public static final GssPropertyType GSS_VISIBILITY_PROPERTY
      = new GssPropertyType("visibility", "", GssPropertyType.TypeUnits.STRING,
      "CSS visibility property controls whether a chart component is drawn or not") {
    @Override
    public void setPropertyString(GssProperties props, String pval) {
      props.visible = !"hidden".equals(pval);
    }
  };

  public static final GssPropertyType GSS_POINT_SIZE_PROPERTY
      = new GssPropertyType("radius", "", GssPropertyType.TypeUnits.PX,
      "For circular points, specifies the radius of circle") {
    @Override
    protected void setPropertyInt(GssProperties props, int num) {
      props.size = num;
    }
  };

  public static final GssPropertyType GSS_FONT_FAMILY_PROPERTY
      = new GssPropertyType("font-family", "", GssPropertyType.TypeUnits.STRING,
      "Standard CSS Font Family property") {
    @Override
    public void setPropertyString(GssProperties props, String pval) {
      props.fontFamily = pval;
    }
  };

  public static final GssPropertyType GSS_FONT_WEIGHT_PROPERTY
      = new GssPropertyType("font-weight", "", GssPropertyType.TypeUnits.STRING,
      "Standard CSS Font Weight property") {
    @Override
    public void setPropertyString(GssProperties props, String pval) {
      props.fontWeight = pval;
    }
  };

  // NOTE "em" and "px" not supported, eg font-size:1em; or font-size:12px; are invalid
  // TODO "px"
  public static final GssPropertyType GSS_FONT_SIZE_PROPERTY
      = new GssPropertyType("font-size", "", GssPropertyType.TypeUnits.PT,
      "Standard CSS Font Size property") {
    @Override
    public void setPropertyString(GssProperties props, String pval) {
      props.fontSize = pval;
    }
  };

  public static final GssPropertyType GSS_TICK_POSITION_PROPERTY
      = new GssPropertyType("tick-position", "", GssPropertyType.TypeUnits.STRING,
      "A value of 'inside' puts tick labels inside the plot, a value of " 
          + "'outside' renders them outside the plot") {
    @Override
    public void setPropertyString(GssProperties props, String pval) {
      props.tickPosition = pval;
    }
  };
  
  public static final GssPropertyType GSS_TICK_ALIGN_PROPERTY
      = new GssPropertyType("tick-align", "", GssPropertyType.TypeUnits.STRING,
      "A value of 'above' renders tick labels at a position numerically " 
          + "greater than the tick. A value of 'middle' centers the label on " 
          + "the tick. A valuer of 'below' renders tick labels at a position" 
          + " numerically lesser than the tick's value.") {
    @Override
    public void setPropertyString(GssProperties props, String pval) {
      props.tickAlign = pval;
    }
  };

  public static final GssElementType GSS_GRID_TYPE = new GssElementType("grid",
      new GssElementType[0],
      new GssPropertyType[]{GSS_BGCOLOR_PROPERTY, GSS_BGIMAGE_PROPERTY,
          GSS_OPACITY_PROPERTY},
      "In a line plot, allows control of fill region beneath line.",
      "line fill { opacity: 0.5; background-color: lightblue; } /* draws a filled region below plotted line 50% transparent and light blue */")
      ;
  
  public static final GssElementType GSS_FILL_TYPE = new GssElementType("fill",
      new GssElementType[0],
      new GssPropertyType[]{GSS_COLOR_PROPERTY, GSS_LINE_THICKNESS_PROPERTY,
          GSS_OPACITY_PROPERTY, GSS_VISIBILITY_PROPERTY, GSS_POINT_SELECTION_PROPERTY},
      "Allows styling of horizontal and vertical grid lines.",
      "axis.domain grid { opacity: 0.5; visiblity: visible; }")
      ;

  public static final GssElementType GSS_LINE_TYPE = new GssElementType("line",
      new GssElementType[]{GSS_FILL_TYPE},
      new GssPropertyType[]{GSS_LINE_THICKNESS_PROPERTY},
      "Controls styles used for line rendering",
      "line { line-thickness: 2px; color: red} /* draws a red line 2 pixels thick */")
      ;
  
  public static final GssElementType GSS_SERIES_TYPE = new GssElementType(
      "series", new String[]{"selected", "disabled", "s#"},
      new GssElementType[0],
      new GssPropertyType[]{GSS_BGCOLOR_PROPERTY, GSS_BGIMAGE_PROPERTY,
          GSS_COLOR_PROPERTY, GSS_VISIBILITY_PROPERTY, GSS_GROUP_PROPERTY},
      "for each time series, there is a series element with class s#, for example, the first time series dataset is represented by series.s0, and the second by series.s1",
      "series.s1 line { color: green } /* Make the second times series line green */")
      ;

   public static final GssElementType GSS_LABEL_TYPE = new GssElementType(
      "domainmarker", new String[0],
      new GssElementType[0],
      new GssPropertyType[]{GSS_BGCOLOR_PROPERTY, GSS_OPACITY_PROPERTY,
        GSS_VISIBILITY_PROPERTY, GSS_COLOR_PROPERTY, GSS_FONT_FAMILY_PROPERTY, GSS_FONT_WEIGHT_PROPERTY, GSS_FONT_SIZE_PROPERTY},
      "A label controls chart elements which have associated text like markers, tick labels, legend labels, etc.",
      "label { color: blue; font-size: 12pt } /* Make all labels blue and 12pt */")
      ;

  public static final GssElementType GSS_LABELS_TYPE = new GssElementType(
      "labels", new String[0],
      new GssElementType[0],
      new GssPropertyType[]{GSS_VISIBILITY_PROPERTY},
      "Controls visible of legend labelsc.",
      "axislegend labels { visibility: hidden }")
      ;
  
   public static final GssElementType GSS_MARKER_TYPE = new GssElementType(
        "marker", new String[0],
        new GssElementType[] { GSS_LABEL_TYPE},
        new GssPropertyType[]{GSS_BGCOLOR_PROPERTY, GSS_OPACITY_PROPERTY,
          GSS_VISIBILITY_PROPERTY},
        "A marker that represents a single point on the curve.",
        "marker { background-color: green; opacity: 0.3 } /* Make the marker green and 70% transparent */")
        ;
  
   public static final GssElementType GSS_DOMAINMARKER_TYPE = new GssElementType(
      "domainmarker", new String[0],
      new GssElementType[] { GSS_LABEL_TYPE},
      new GssPropertyType[]{GSS_BGCOLOR_PROPERTY, GSS_OPACITY_PROPERTY,
        GSS_VISIBILITY_PROPERTY},
      "A highlighting marker that stretches along the X axis.",
      "domainmarker { background-color: green; opacity: 0.3 } /* Make the highlight green and 70% transparent */")
      ;
  
   public static final GssElementType GSS_CROSSHAIR_TYPE = new GssElementType("crosshair",
      new GssElementType[0],
      new GssPropertyType[]{GSS_COLOR_PROPERTY, GSS_DATE_FORMAT_PROPERTY, GSS_LINE_THICKNESS_PROPERTY, GSS_NUMBER_FORMAT_PROPERTY, GSS_POINT_SELECTION_PROPERTY, GSS_VISIBILITY_PROPERTY},
      "Visibility of crosshair",
      "crosshair { visibility: visible; line-thickness: 2px; color: red} /* draws a red line 2 pixels thick */")
      ;

  public static final GssElementType GSS_GUIDELINE_TYPE = new GssElementType("guideline",
      new GssElementType[]{GSS_MARKER_TYPE},
      new GssPropertyType[]{GSS_COLOR_PROPERTY, GSS_LINE_THICKNESS_PROPERTY, GSS_POINT_SELECTION_PROPERTY, GSS_VISIBILITY_PROPERTY},
      "Visibility of guidelines on markers",
      "marker.foo guideline { visibility: visible; line-thickness: 2px; color: red} /* draws a red line 2 pixels thick */")
      ;
  
  public static final GssElementType GSS_AXISLEGEND_TYPE = new GssElementType("axislegend",
      new GssElementType[0],
      new GssPropertyType[]{GSS_COLOR_PROPERTY, GSS_BGCOLOR_PROPERTY, GSS_VISIBILITY_PROPERTY},
      "Visibility of axis legend",
      "axislegend { visibility: hidden }")
      ;
}
