package org.timepedia.chronoscope.client.canvas;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Used by FontRendererService to transfer Java2D font metrics information
 */
public class RenderedFontMetrics implements IsSerializable {

  public int maxAscent;

  public int maxDescent;

  public int maxAdvance;

  public int leading;

  public int advances[];

  public String url;

  public int maxBoundsWidth;

  public int maxBoundsHeight;

  public int getAdvance(char c) {
    if (c > 255) {
      GWT.log("Warning: Character code above 255, no metrics for " + c, null);
      return maxAdvance;
    }
    return advances[c];
  }

  public void getBounds(Bounds b, char c) {
    double line = Math.floor(c / 16);
    double column = c % 16;
    b.x = column * maxBoundsWidth;
    b.y = line * maxBoundsHeight;
    b.width = maxBoundsWidth;
    b.height = maxBoundsHeight;
  }

  public String getCSSClip(char c) {
    int line = c / 16;
    int column = c % 16;
    int right = (16 - (column + 1)) * maxAdvance;
    int top = line * getHeight(c);
    int bottom = (16 - (line + 1)) * getHeight(c);
    int left = column * maxAdvance;
    return "rect(" + top + " " + right + " " + bottom + " " + left + ")";
  }

  public int maxCharWidth(char[] chrs) {
    int width = 0;
    for (int i = 0; i < chrs.length; i++) {
      width = Math.max(width, getAdvance(chrs[i]));
    }
    return width;
  }

  public int stringHeight(char[] chrs) {
    int height = 0;
    for (int i = 0; i < chrs.length; i++) {
      height = Math.max(height, getHeight(chrs[i]));
    }
    return height;
  }

  public int stringWidth(char[] chrs) {
    int width = 0;
    for (int i = 0; i < chrs.length; i++) {
      width += getAdvance(chrs[i]);
    }
    return width;
  }

  private int getHeight(char chr) {
    return maxAscent + maxDescent + leading;
  }
}
