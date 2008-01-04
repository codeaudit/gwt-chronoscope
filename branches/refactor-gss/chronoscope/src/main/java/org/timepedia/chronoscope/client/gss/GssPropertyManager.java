package org.timepedia.chronoscope.client.gss;

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
        private String[] classes;   // TODO - pin down the classes
        private String[] examples;

        // TODO - unit test to run examples
        // TODO - safen up and checken up       
        // TODO - approach for multiplot placement (eg price stacked above vol, etc)

        final HashSet properties = new HashSet();

        public String getDocString() {
            return docString;
        }

        protected GssElementType(String typeName, String[] classes, GssElementType[] childElementTypes, GssPropertyType[] propertyTypes, String docString, String[] examples) {
            this.typeName = typeName;
            this.classes = classes;
            this.childElementTypes = childElementTypes;
            this.docString = docString;
            this.examples = examples;
            for (int i = 0; i < propertyTypes.length; i++) {
                GssPropertyType type = propertyTypes[i];
                properties.add(type);
            }

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

        public String[] getClasses() {
            return classes;
        }

        public String[] getExamples() {
            return examples;
        }
    }

    public static class GssPropertyType {
        private String propertyName;
        private String altCssName;
        private int valueType;
        private String docString;

        public static final int INTEGER = 0, PX = 1, PT = 2, URI = 3, COLOR = 4, BGIMAGE = 5, STRING = 6, BORDER = 7;


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

    // TODO - support { border-width: 1px; border-color: black} as well as { border: 1px black; }
    public static final GssPropertyType GSS_BORDER_PROPERTY = new GssPropertyType(
            "border", "border", GssPropertyType.BORDER, "A standard CSS color property");

    public static final GssPropertyType GSS_BGCOLOR_PROPERTY = new GssPropertyType(
            "background-color", "background-color", GssPropertyType.COLOR, "Standard CSS background-color property");

    public static final GssPropertyType GSS_BGIMAGE_PROPERTY = new GssPropertyType(
            "background-image", "background-image", GssPropertyType.BGIMAGE, "CSS Background image property with extensions for gradients");

    public static final GssPropertyType GSS_COLOR_PROPERTY = new GssPropertyType(
            "color", "color", GssPropertyType.COLOR, "A standard CSS color property");

    public static final GssPropertyType GSS_FONT_FAMILY_PROPERTY = new GssPropertyType(
            "font-family", "font-family", GssPropertyType.STRING, "Standard CSS Font Family property");

    public static final GssPropertyType GSS_FONT_SIZE_PROPERTY = new GssPropertyType(
            "font-size", "font-size", GssPropertyType.PT, "Standard CSS Font Size property");

    public static final GssPropertyType GSS_FONT_WEIGHT_PROPERTY = new GssPropertyType(
            "font-weight", "font-weight", GssPropertyType.STRING, "Standard CSS Font Weight property");

    public static final GssPropertyType GSS_HEIGHT_PROPERTY = new GssPropertyType(
            "height", "height", GssPropertyType.PX, "A standard CSS height property");

    public static final GssPropertyType GSS_LEFT_PROPERTY = new GssPropertyType(
            "left", "left", GssPropertyType.PX, "A standard CSS left property used for offsets");

    public static final GssPropertyType GSS_LINE_THICKNESS_PROPERTY = new GssPropertyType(
            "-cs-line-thickness", "border-width-left", GssPropertyType.PX, "Specifies the thickness of lines drawn in a line plot");

    // TODO - warn if visibility: visible and opacity 0?
    public static final GssPropertyType GSS_OPACITY_PROPERTY = new GssPropertyType("opacity",
            "opacity", GssPropertyType.PX, "Specifies the opacity value from 0 (transparent) to 1 (opaque)");

    public static final GssPropertyType GSS_POINT_SIZE_PROPERTY = new GssPropertyType(
            "-cs-point-size", "width", GssPropertyType.PX, "For circular points, specifies the radius of circle");

    // TODO tick label {text-align: left} for labels left of axis, {text-align: right} for tick labels to the right of axis
    public static final GssPropertyType GSS_TEXT_ALIGN_PROPERTY = new GssPropertyType(
            "text-align", "text-align", GssPropertyType.STRING, "CSS text-align property controls whether tick labels are left or right of axis (which may be inside the plot region)");

    public static final GssPropertyType GSS_TOP_PROPERTY = new GssPropertyType(
            "top", "top", GssPropertyType.PX, "A standard CSS top property used for offsets");

    // TODO - sub, super, middle, top?, bottom? for markers
    public static final GssPropertyType GSS_VERTICAL_ALIGN_PROPERTY = new GssPropertyType(
            "vertical-align", "vertical-align", GssPropertyType.STRING, "CSS vertical-align property controls the vertical align of label, eg sub for labels below ticks, super for labels above ticks, middle for labels lined up with middle of ticks, etc.");

    public static final GssPropertyType GSS_VISIBILITY_PROPERTY = new GssPropertyType(
            "visibility", "visibility", GssPropertyType.STRING, "CSS visibility property controls whether a chart component is drawn or not");

    public static final GssPropertyType GSS_WIDTH_PROPERTY = new GssPropertyType(
            "width", "width", GssPropertyType.PX, "A standard CSS width property");

    public static final GssElementType GSS_FILL_TYPE = new GssElementType(
            // element name
            "fill",
            // gss classes
            new String[]{
                    ,
            },
            // child elements
            new GssElementType[]{
                    ,
            },
            // properties
            new GssPropertyType[]{
                    GSS_BGCOLOR_PROPERTY,
                    GSS_BGIMAGE_PROPERTY,
                    GSS_OPACITY_PROPERTY,
                    GSS_VISIBILITY_PROPERTY,
            },
            // docstring
            "In a line plot, allows control of fill region beneath line.",
            // gss examples
            new String[]{
                    "line fill { opacity: 0.5; background-color: lightblue; } /* draws a filled region below plotted line 50% transparent and light blue */",
                    ".s1 fill { background-color: rgb( 245, 86, 95 ); opacity: 0.4;",
                    "fill { background-color: transparent; opacity: 0.4; }",
                    "fill.disabled { background-color: rgba( 0, 0, 0, 0 ); opacity: 0; }",
            }
    );

    public static final GssElementType GSS_LABEL_TYPE = new GssElementType(
            // element name
            "label",
            // gss classes
            new String[]{
                    ,
            },
            // child elements
            new GssElementType[]{
                    ,
            },
            // properties
            new GssPropertyType[]{
                    GSS_BORDER_PROPERTY,
                    GSS_COLOR_PROPERTY,
                    GSS_FONT_FAMILY_PROPERTY,
                    GSS_FONT_SIZE_PROPERTY,
                    GSS_FONT_WEIGHT_PROPERTY,
                    GSS_OPACITY_PROPERTY,
                    GSS_TEXT_ALIGN_PROPERTY,
                    GSS_VERTICAL_ALIGN_PROPERTY,
                    GSS_VISIBILITY_PROPERTY,
            },
            // docstring
            "",  // TODO
            // gss examples
            new String[]{
                    "label {color: black; border-width: 0px; visibility: visible; opacity: .8; }",
                    "label {color: #ffffff; border-width: 1px; visibility: visible; }",
            }
    );

    public static final GssElementType GSS_SHADOW_TYPE = new GssElementType(
        // element name
        "shadow",
        // gss classes
        new String[] {
                "disabled",
                "focus",
                "hover",
        },
        // child elements
        new GssElementType[] {
                ,
        },
        // properties
        new GssPropertyType[] {
                GSS_LEFT_PROPERTY,
                GSS_OPACITY_PROPERTY,
                GSS_TOP_PROPERTY,
                GSS_VISIBILITY_PROPERTY,
                GSS_WIDTH_PROPERTY,  // TODO - choose a more logical property name than width
        },
        // docstring
        "",  // TODO
        // gss examples
        new String[] {
                "shadow { width: 2px; top: 2px; left: 2px; color: black; visibility: visible; }",
                "point.focus shadow { width: 4px; top: 10px; left: 10px; color: black; visibility: visible; opacity: 0.8; }",
        }
    );

    public static final GssElementType GSS_LINE_TYPE = new GssElementType(
            // element name
            "line",
            // gss classes
            new String[]{
                    "disabled",
                    "selected",
                    "focus"
            },
            // child elements
            new GssElementType[]{
                    GSS_SHADOW_TYPE,
            },
            // properties
            new GssPropertyType[]{
                    GSS_LINE_THICKNESS_PROPERTY,
                    GSS_COLOR_PROPERTY,
                    GSS_OPACITY_PROPERTY,
                    GSS_VISIBILITY_PROPERTY,
            },
            // docstring
            "Controls styles used for line rendering",
            // gss examples
            new String[]{
                    "line { -cs-line-thickness: 2px; color: red} /* draws a red line 2 pixels thick */",
                    "line { visibility: visible; color: blue; -cs-line-thickness: 2px; opacity: 1.0; }",
                    "line.focus { color: red; opacity: 1; }"
            }
    );
    
    public static final GssElementType GSS_MARKER_TYPE = new GssElementType(
        // element name
        "marker",
        // gss classes
        new String[] { 
                ,
        }, 
        // child elements
        new GssElementType[] { 
                , 
        }, 
        // properties
        new GssPropertyType[] {
                    GSS_BGCOLOR_PROPERTY,
                    GSS_BGIMAGE_PROPERTY,
                    GSS_BORDER_PROPERTY,
                    GSS_COLOR_PROPERTY,
                    GSS_FONT_FAMILY_PROPERTY,
                    GSS_FONT_SIZE_PROPERTY,
                    GSS_FONT_WEIGHT_PROPERTY,
                    GSS_OPACITY_PROPERTY,
                    GSS_VISIBILITY_PROPERTY,
        },
        // docstring
        "",
        // gss examples
        new String[] { 
                "marker { background-color: lightgray; color: black; border-width: 1px; font-family: elegant; font-size: 9pt; }",
        }
    );

    public static final GssElementType GSS_OVERVIEW_TYPE = new GssElementType(
        // element name
        "overview",
        // gss classes
        new String[] {
                "infoWindow", // TODO - switch to chrono.overview  
        },
        // child elements
        new GssElementType[] {
                ,
        },
        // properties
        new GssPropertyType[] {
                    GSS_BORDER_PROPERTY,
                    GSS_BGCOLOR_PROPERTY,
                    GSS_BGIMAGE_PROPERTY,
                    GSS_COLOR_PROPERTY,
                    GSS_OPACITY_PROPERTY,
                    GSS_VISIBILITY_PROPERTY,
        },
        // docstring
        "sliding overview window for context",
        // gss examples
        new String[] {
                "overview { background-color: #99CCFF; color: #0099FF; opacity: 0.4; border: 2px; }",
        }
    );

    public static final GssElementType GSS_PLOT_TYPE = new GssElementType(
        // element name
        "plot",
        // gss classes
        new String[] { 
                ,
        },
        // child elements
        new GssElementType[] {
                    , // TODO - GSS_SERIES_TYPE     [plot should contain series]
        },
        // properties
        new GssPropertyType[] {
                    GSS_BORDER_PROPERTY,
                    GSS_BGCOLOR_PROPERTY,
                    GSS_BGIMAGE_PROPERTY,
                    GSS_COLOR_PROPERTY,
                    GSS_OPACITY_PROPERTY,
                    GSS_VISIBILITY_PROPERTY,
        },
        // docstring
        "plot is the region where lines or bars are drawn",
        // gss examples
        new String[] {
                "plot { visibility: visible; color: blue; border-width: 2px; opacity: 1.0; }",
                "plot { background-image: url( http://timepedia.org/lineargradient/0,0/0,1/0/00ABEb/1/FFFFFF ); }",
        }
    );

    public static final GssElementType GSS_POINT_TYPE = new GssElementType(
        // element name
        "point",
        // gss classes
        new String[] {
                "disabled",
                "focus",
                "hover",
        }, 
        // child elements
        new GssElementType[] {
                GSS_SHADOW_TYPE
        },
        // properties
        new GssPropertyType[] {
                    GSS_BGCOLOR_PROPERTY,
                    GSS_BGIMAGE_PROPERTY,
                    GSS_BORDER_PROPERTY,
                    GSS_COLOR_PROPERTY,   // TODO - use border color for color of point border instead of color, since interior is bgcolor
                    GSS_OPACITY_PROPERTY,
                    GSS_VISIBILITY_PROPERTY,
        },
        // docstring
        "point presentation",  // TODO
        // gss examples
        new String[] {
                "point { border-width: 1px; color: lightgreen; background-color: lightgreen; width: 2px; }",
                "point.hover { color: red; width: 3px; background-color: white; visibility: visible; }",
                "point.focus { color: gray; width: 5px; border-style: solid; border-width: 2px; visibility: visible; }",
                "point { border-width: 0px; color: green; background-color: lightgreen; width: 2px; visibility: visible; }",
                "series.disabled point { visibility: hidden; }",
                "series.disabled point { visibility: visible; opacity: 0.1; }",
        }
    );

        public static final GssElementType GSS_RANGEMARKER_TYPE = new GssElementType(
            // element name
            "rangemarker",
            // gss classes
            new String[]{
                    ,
            },
            // child elements
            new GssElementType[]{
                    GSS_LABEL_TYPE,
            },
            // properties
            new GssPropertyType[] {
                    GSS_BGCOLOR_PROPERTY,
                    GSS_BGIMAGE_PROPERTY,
                    GSS_BORDER_PROPERTY,
                    GSS_COLOR_PROPERTY,
                    GSS_FONT_FAMILY_PROPERTY,
                    GSS_FONT_SIZE_PROPERTY,
                    GSS_FONT_WEIGHT_PROPERTY,
                    GSS_OPACITY_PROPERTY,
                    GSS_VISIBILITY_PROPERTY,
            },
            // docstring
            "",  // TODO
            // gss examples
            new String[]{
                    "domainmarker { color: white; background-color: white; width: 1px; height: 1px; font-size: 9pt; border-width: 1px; }",
                    "domainmarker { color: white; background-color: grey; visibility: visible, border-width:2px; opacity: .5; }",
            }
    );

    public static final GssElementType GSS_BAR_TYPE = new GssElementType(
            "bar",                  // element name
            new String[]{          // classes
                    ,
            },
            new GssElementType[]{  // child elements
                    GSS_POINT_TYPE,
            },
            new GssPropertyType[]{ // properties
                    GSS_BORDER_PROPERTY,
                    GSS_BGCOLOR_PROPERTY,
                    GSS_BGIMAGE_PROPERTY,
                    GSS_OPACITY_PROPERTY,
                    GSS_VISIBILITY_PROPERTY,
            },
            "",                 // docstring
            new String[]{          // examples
                    "bar  { color: white; background-color: grey; visibility: visible, border-width:2px;}",
            }
    );

    public static final GssElementType GSS_SERIES_TYPE = new GssElementType(
        // element name
        "series",
        // gss classes
        new String[] {
            "selected", "disabled", "focus", "s#"
            },
        // child elements
        new GssElementType[] {
                    GSS_BAR_TYPE,
                    GSS_FILL_TYPE,
                    GSS_LINE_TYPE,
                    GSS_POINT_TYPE,
            },
        // properties
        new GssPropertyType[] {
                    GSS_BGCOLOR_PROPERTY,
                    GSS_BGIMAGE_PROPERTY,
                    GSS_COLOR_PROPERTY,
                    GSS_HEIGHT_PROPERTY,
                    GSS_OPACITY_PROPERTY,
                    GSS_VISIBILITY_PROPERTY,
            },
        // docstring
            "for each time series, there is a series element with class s#, for example, the first time series dataset is represented by series.s0, and the second by series.s1",
        // gss examples
        new String[] {
                    "series.disabled bar { opacity: 0.2 }",
                    "series.disabled line { opacity: 0.2 }",
                    "series.disabled point { visibility: hidden; }",
                    "series.s1 line { color: green } /* Make the second times series line green */",
                    "series, series.s0, series.s1, series.s2, series.s3 {color: blue; visibility: visible; }",
            }
    );


    public static final GssElementType GSS_TICK_TYPE = new GssElementType(
        // element name
        "tick",
        // gss classes
        new String[] {
                ,
        },
        // child elements
        new GssElementType[] {
                ,
        },
        // properties
        new GssPropertyType[] {
                GSS_LEFT_PROPERTY,
                GSS_OPACITY_PROPERTY,
                GSS_TOP_PROPERTY,
                GSS_VISIBILITY_PROPERTY,
                GSS_WIDTH_PROPERTY,  // TODO - choose a more logical property name than width ?
        },
        // docstring
        "",
        // gss examples
        new String[] {
                "tick { visibility: hidden; }",
                ".s0 bar, line, axis.a0 tick { color: rgb( 70, 118, 118 ); }",
        }
    );

     public static final GssElementType GSS_AXIS_TYPE = new GssElementType(
            "axis",
            new String[]{ // classes
            },
            new GssElementType[]{
                    GSS_SERIES_TYPE
            },
            new GssPropertyType[]{
                    GSS_BGCOLOR_PROPERTY,
                    GSS_BGIMAGE_PROPERTY,
                    GSS_BORDER_PROPERTY,
                    GSS_COLOR_PROPERTY,
                    GSS_OPACITY_PROPERTY,
                    GSS_VISIBILITY_PROPERTY},
            "axis.a0 .. axis.aN ", // TODO - tests to ensure classes numbered in order added  ...  Require axis.aN for each series.seriesN  ?
            new String[]
                    {
                            "axis { background-color: white; background-image: none }",
                            "axis.a0 label, axis.a0 { color: rgb( 70, 118, 118 ); }",
                    });

    public static final GssElementType GSS_AXES_TYPE = new GssElementType(
            "axes",
            new String[]{ // classes
            },
            new GssElementType[]{GSS_AXIS_TYPE},
            new GssPropertyType[]{GSS_BGCOLOR_PROPERTY, GSS_BGIMAGE_PROPERTY, GSS_VISIBILITY_PROPERTY},
            "there are four default axes: top, bottom, left, right; available as axes.top axes.bottom, axes.left, axes.right",
            new String[]{ // xmp
                    "axes { background-color: white; background-image: none }",
            });
    // TODO - defaults for multiplots, eg finance chart domain axes below price but above volume

    public static final GssElementType GSS_AXISLEGEND_TYPE = new GssElementType(
            // element name
            "axislegend",
            new String[]{ // classes
                    ,
            },
            new GssElementType[]{ // child elements
                    GSS_LABEL_TYPE,
            },
            new GssPropertyType[]{ // properties
                    GSS_BGCOLOR_PROPERTY,
                    GSS_BGIMAGE_PROPERTY,
                    GSS_COLOR_PROPERTY,
                    GSS_FONT_FAMILY_PROPERTY,
                    GSS_FONT_SIZE_PROPERTY,
                    GSS_FONT_WEIGHT_PROPERTY,
                    GSS_VISIBILITY_PROPERTY,
            },
            "", // docstring TODO
            new String[]{ // xmp
                    "axislegend { font-family: Helvetica; font-size: 8pt; color: black; background-color: white; background-image: url( http://timepedia.org/lineargradient/0,0/0,1/0/FFFFFF/1/00ABEb); }",
                    "axislegend { font-family: elegant; font-size: 12px; color: black; background-color: rgba(255,255,255,127); }",
                    "axislegend { font-family: 'lucida console', monospaced, sans-serif; font-size: 6px; color: black; background-color: transparent; }",
            });


    public static final GssElementType GSS_BARMARKER_TYPE = new GssElementType(
            // element name
            "barmarker",
            // gss classes
            new String[]{
                    ,
            },
            // child elements
            new GssElementType[]{
                    GSS_LABEL_TYPE,
            }, // TODO - images as children?
            // properties
            new GssPropertyType[] {
                    GSS_BGCOLOR_PROPERTY,
                    GSS_BGIMAGE_PROPERTY,
                    GSS_BORDER_PROPERTY,
                    GSS_COLOR_PROPERTY,
                    GSS_FONT_FAMILY_PROPERTY,
                    GSS_FONT_SIZE_PROPERTY,
                    GSS_FONT_WEIGHT_PROPERTY,
                    GSS_OPACITY_PROPERTY,
                    GSS_VISIBILITY_PROPERTY,
            },
            // docstring
            "",  // TODO
            // gss examples
            new String[]{
                    "barmarker { color: white; background-color: white; width: 1px; height: 1px; font-size: 9pt; border-width: 1px; }",
                    "barmarker { color: white; background-color: grey; visibility: visible, border-width:2px; opacity: .5; }",
            }
    );
    
    public static final GssElementType GSS_DOMAINMARKER_TYPE = new GssElementType(
            // element name
            "domainmarker",
            // gss classes
            new String[]{
                    ,
            },
            // child elements
            new GssElementType[]{
                    GSS_LABEL_TYPE,
            },
            // properties
            new GssPropertyType[] {
                    GSS_BGCOLOR_PROPERTY,
                    GSS_BGIMAGE_PROPERTY,
                    GSS_BORDER_PROPERTY,
                    GSS_COLOR_PROPERTY,
                    GSS_FONT_FAMILY_PROPERTY,
                    GSS_FONT_SIZE_PROPERTY,
                    GSS_FONT_WEIGHT_PROPERTY,
                    GSS_OPACITY_PROPERTY,
                    GSS_VISIBILITY_PROPERTY,
            },
            // docstring
            "",  // TODO
            // gss examples
            new String[]{
                    "domainmarker { color: white; background-color: white; width: 1px; height: 1px; font-size: 9pt; border-width: 1px; }",
                    "domainmarker { color: white; background-color: grey; visibility: visible, border-width:2px; opacity: .5; }",
            }
    );

         public static final GssElementType GSS_GRID_TYPE = new GssElementType(
            // element name
            "grid",
            // gss classes
            new String[]{
                    ,
            },
            // child elements
            new GssElementType[]{
                    GSS_POINT_TYPE,  // TODO - really?
            },
            // properties
            new GssPropertyType[]{
                    GSS_LINE_THICKNESS_PROPERTY,
                    GSS_COLOR_PROPERTY,
                    GSS_BGIMAGE_PROPERTY,
                    GSS_OPACITY_PROPERTY,
                    GSS_VISIBILITY_PROPERTY,
            },
            // docstring
            "",
            // gss examples
            new String[]{
                    "grid { color: green; opacity: 0.5; visibility: visible; -cs-line-thickness: 1px; }",
                    "grid { color: rgba( 0, 0, 0, 0 ); opacity: 0.5; visibility: visible; -cs-line-thickness: 2px; }",
            }
    );
    
}
