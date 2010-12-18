package org.timepedia.chronoscope.client.canvas;

import java.util.HashMap;

/**
 * Represents a PaintStyle which is an RGBA color
 */
public class Color implements PaintStyle {
  
  /**
   * List of supported named colors in Chronoscope.
   * 
   * The list has been taken from: 
   * http://www.w3schools.com/html/html_colornames.asp
   */
  public static HashMap<String, String> colors = new HashMap<String, String>() {
    private static final long serialVersionUID = 1L;
    {
      put("aliceblue", "f0f8ff");
      put("antiquewhite", "faebd7");
      put("aqua", "00ffff");
      put("aquamarine", "7fffd4");
      put("azure", "f0ffff");
      put("beige", "f5f5dc");
      put("bisque", "ffe4c4");
      put("black", "000000");
      put("blanchedalmond", "ffebcd");
      put("blue", "0000ff");
      put("blueviolet", "8a2be2");
      put("brown", "a52a2a");
      put("burlywood", "deb887");
      put("cadetblue", "5f9ea0");
      put("chartreuse", "7fff00");
      put("chocolate", "d2691e");
      put("coral", "ff7f50");
      put("cornflowerblue", "6495ed");
      put("cornsilk", "fff8dc");
      put("crimson", "dc143c");
      put("cyan", "00ffff");
      put("darkblue", "00008b");
      put("darkcyan", "008b8b");
      put("darkgoldenrod", "b8860b");
      put("darkgray", "a9a9a9");
      put("darkgreen", "006400");
      put("darkkhaki", "bdb76b");
      put("darkmagenta", "8b008b");
      put("darkolivegreen", "556b2f");
      put("darkorange", "ff8c00");
      put("darkorchid", "9932cc");
      put("darkred", "8b0000");
      put("darksalmon", "e9967a");
      put("darkseagreen", "8fbc8f");
      put("darkslateblue", "483d8b");
      put("darkslategray", "2f4f4f");
      put("darkturquoise", "00ced1");
      put("darkviolet", "9400d3");
      put("deeppink", "ff1493");
      put("deepskyblue", "00bfff");
      put("dimgray", "696969");
      put("dodgerblue", "1e90ff");
      put("firebrick", "b22222");
      put("floralwhite", "fffaf0");
      put("forestgreen", "228b22");
      put("fuchsia", "ff00ff");
      put("gainsboro", "dcdcdc");
      put("ghostwhite", "f8f8ff");
      put("gold", "ffd700");
      put("goldenrod", "daa520");
      put("gray", "808080");
      put("green", "008000");
      put("greenyellow", "adff2f");
      put("honeydew", "f0fff0");
      put("hotpink", "ff69b4");
      put("indianred", "cd5c5c");
      put("indigo", "4b0082");
      put("ivory", "fffff0");
      put("khaki", "f0e68c");
      put("lavender", "e6e6fa");
      put("lavenderblush", "fff0f5");
      put("lawngreen", "7cfc00");
      put("lemonchiffon", "fffacd");
      put("lightblue", "add8e6");
      put("lightcoral", "f08080");
      put("lightcyan", "e0ffff");
      put("lightgoldenrodyellow", "fafad2");
      put("lightgray", "d3d3d3");
      put("lightgreen", "90ee90");
      put("lightpink", "ffb6c1");
      put("lightsalmon", "ffa07a");
      put("lightseagreen", "20b2aa");
      put("lightskyblue", "87cefa");
      put("lightslategray", "778899");
      put("lightsteelblue", "b0c4de");
      put("lightyellow", "ffffe0");
      put("lime", "00ff00");
      put("limegreen", "32cd32");
      put("linen", "faf0e6");
      put("magenta", "ff00ff");
      put("maroon", "800000");
      put("mediumaquamarine", "66cdaa");
      put("mediumblue", "0000cd");
      put("mediumorchid", "ba55d3");
      put("mediumpurple", "9370d8");
      put("mediumseagreen", "3cb371");
      put("mediumslateblue", "7b68ee");
      put("mediumspringgreen", "00fa9a");
      put("mediumturquoise", "48d1cc");
      put("mediumvioletred", "c71585");
      put("midnightblue", "191970");
      put("mintcream", "f5fffa");
      put("mistyrose", "ffe4e1");
      put("moccasin", "ffe4b5");
      put("navajowhite", "ffdead");
      put("navy", "000080");
      put("oldlace", "fdf5e6");
      put("olive", "808000");
      put("olivedrab", "6b8e23");
      put("orange", "ffa500");
      put("orangered", "ff4500");
      put("orchid", "da70d6");
      put("palegoldenrod", "eee8aa");
      put("palegreen", "98fb98");
      put("paleturquoise", "afeeee");
      put("palevioletred", "d87093");
      put("papayawhip", "ffefd5");
      put("peachpuff", "ffdab9");
      put("peru", "cd853f");
      put("pink", "ffc0cb");
      put("plum", "dda0dd");
      put("powderblue", "b0e0e6");
      put("purple", "800080");
      put("red", "ff0000");
      put("rosybrown", "bc8f8f");
      put("royalblue", "4169e1");
      put("saddlebrown", "8b4513");
      put("salmon", "fa8072");
      put("sandybrown", "f4a460");
      put("seagreen", "2e8b57");
      put("seashell", "fff5ee");
      put("sienna", "a0522d");
      put("silver", "c0c0c0");
      put("skyblue", "87ceeb");
      put("slateblue", "6a5acd");
      put("slategray", "708090");
      put("snow", "fffafa");
      put("springgreen", "00ff7f");
      put("steelblue", "4682b4");
      put("tan", "d2b48c");
      put("teal", "008080");
      put("thistle", "d8bfd8");
      put("tomato", "ff6347");
      put("turquoise", "40e0d0");
      put("violet", "ee82ee");
      put("wheat", "f5deb3");
      put("white", "ffffff");
      put("whitesmoke", "f5f5f5");
      put("yellow", "ffff00");
      put("yellowgreen", "9acd32");
    }
  };

  private final String color;

  private boolean rgbaSet = false;

  public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
  public static final Color WHITE = new Color("white");
  public static final Color BLACK = new Color("black");
  public static final Color DIMGRAY = new Color("dimgray");
  public static final Color GREEN = new Color("green");
  public static final Color LIGHTGRAY = new Color("lightgray");
  public static final Color GRAY = new Color("gray");

  private int rgba = 0;

  public Color(int r, int g, int b) {
    this(r, g, b, 255);
  }

  public Color(int r, int g, int b, int a) {
    setRgba(r, g, b, a);
    this.rgbaSet = true;
    this.color = "rgba(" + r + "," + g + "," + b + "," + a + ")";
  }

  public Color(String color) {
    if (!color.startsWith("#")) {
      String rgba = colors.get(color);
      if (rgba != null) {
        color =  "#" + rgba;
      }
    }
    this.color = color;
  }

  public String getCSSColor() {
    return color;
  }

  public int getRGBA() {
    return this.rgba;
  }

  public boolean isRgbaSet() {
    return rgbaSet;
  }

  public void setRgba(int rgba) {
    this.rgba = rgba;
    rgbaSet = true;
  }

  private void setRgba(int r, int g, int b, int a) {
    this.rgba = a << 24 | r << 16 | g << 8 | b;
  }

  public String toString() {
    return color;
  }
}
