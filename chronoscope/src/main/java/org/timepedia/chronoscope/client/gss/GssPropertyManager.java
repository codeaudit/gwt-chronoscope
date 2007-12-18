package org.timepedia.chronoscope.client.gss;

import java.lang.reflect.Field;
import java.util.HashSet;

/**
 * Single point of entry to register all GssElements types and Properties
 * Useful for auto-generating JavaDoc and auto-extending Batik as well as processing syntactic sugar CSS into real
 * CSS (e.g. 'line-thickness' -> 'border-width')
 */
public class GssPropertyManager {
    public static class GssElementType {
        final private String typeName;
        private GssElementType[] childElementTypes;
        private String docString;
        private String exampleString;
        final HashSet properties=new HashSet();

        public String getDocString() {
            return docString;
        }

        public String getExampleString() {
            return exampleString;
        }

        protected GssElementType(String typeName, String[] classes, GssElementType[] childElementTypes, GssPropertyType[] propertyTypes, String docString, String exampleString) {
            this.typeName = typeName;
            this.childElementTypes = childElementTypes;
            this.docString = docString;
            this.exampleString = exampleString;
            for (int i = 0; i < propertyTypes.length; i++) {
                GssPropertyType type = propertyTypes[i];
                properties.add(type);
            }

        }

        protected GssElementType(String typeName, GssElementType[] childElementTypes, GssPropertyType[] propertyTypes, String docString, String example) {

            this(typeName, new String[0], childElementTypes, propertyTypes, docString, example);
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
        private int valueType;
        private String docString;

        public static final int INTEGER = 0, PX = 1, PT = 2, URI = 3, COLOR=4, BGIMAGE=5, STRING=5;


        public String getDocString() {
            return docString;
        }

        protected GssPropertyType(String propertyName, String altCssName, int valueType, String docString) {

            this.propertyName = propertyName;
            this.altCssName = altCssName;
            this.valueType = valueType;
            this.docString = docString;
        }

        public String getName() {
            return propertyName;
        }
    }

    public static final GssPropertyType GSS_LINE_THICKNESS_PROPERTY = new GssPropertyType("-cs-line-thickness",
            "border-width-left", GssPropertyType.PX, "Specifies the thickness of lines drawn in a line plot");


    public static final GssPropertyType GSS_OPACITY_PROPERTY = new GssPropertyType("opacity",
            "opacity", GssPropertyType.PX, "Specifies the opacity value from 0 (transparent) to 1 (opaque)");


    public static final GssPropertyType GSS_COLOR_PROPERTY = new GssPropertyType("color",
            "color", GssPropertyType.COLOR, "A standard CSS color property");

    public static final GssPropertyType GSS_BGCOLOR_PROPERTY = new GssPropertyType("background-color",
            "background-color", GssPropertyType.COLOR, "Standard CSS background-color property");

    public static final GssPropertyType GSS_BGIMAGE_PROPERTY = new GssPropertyType("background-image",
            "background-image", GssPropertyType.BGIMAGE, "CSS Background image property with extensions for gradients");


    public static final GssPropertyType GSS_VISBILITY_PROPERTY = new GssPropertyType("visibility",
            "visibility", GssPropertyType.STRING, "CSS visibility property controls whether a chart component is drawn or not");

    public static final GssPropertyType GSS_POINT_SIZE_PROPERTY = new GssPropertyType("-cs-point-size",
            "width", GssPropertyType.PX, "For circular points, specifies the radius of circle");

    public static final GssPropertyType GSS_FONT_FAMILY_PROPERTY = new GssPropertyType("font-family",
            "font-family", GssPropertyType.STRING, "Standard CSS Font Family property");

    public static final GssPropertyType GSS_FONT_WEIGHT_PROPERTY = new GssPropertyType("font-weight",
            "font-weight", GssPropertyType.STRING, "Standard CSS Font Weight property");

    public static final GssPropertyType GSS_FONT_SIZE_PROPERTY = new GssPropertyType("font-size",
            "font-size", GssPropertyType.PT, "Standard CSS Font Size property");


    public static final GssElementType GSS_FILL_TYPE = new GssElementType("fill",  new GssElementType[0], new GssPropertyType[]{GSS_BGCOLOR_PROPERTY, GSS_BGIMAGE_PROPERTY, GSS_OPACITY_PROPERTY}, "In a line plot, allows control of fill region beneath line.",
            "line fill { opacity: 0.5; background-color: lightblue; } /* draws a filled region below plotted line 50% transparent and light blue */");
    public static final GssElementType GSS_LINE_TYPE = new GssElementType("line",  new GssElementType[0],
            new GssPropertyType[] { GSS_LINE_THICKNESS_PROPERTY }, "Controls styles used for line rendering", "line { -cs-line-thickness: 2px; color: red} /* draws a red line 2 pixels thick */");


    public static final GssElementType GSS_SERIES_TYPE = new GssElementType("series",
            new String[] { "selected", "disabled", "s#"},
            new GssElementType[] {GSS_FILL_TYPE, GSS_LINE_TYPE},
            new GssPropertyType[] {
                GSS_BGCOLOR_PROPERTY, GSS_BGIMAGE_PROPERTY, GSS_COLOR_PROPERTY, GSS_VISBILITY_PROPERTY    
            }, "for each time series, there is a series element with class s#, for example, the first time series dataset is represented by series.s0, and the second by series.s1",
            "series.s1 line { color: green } /* Make the second times series line green */");
   
    
}
