package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssProperties;

/**
 * Provides string pixel dimensions to a client.  The source of the calculations
 * is encapsulated so that client code is not affected if this source changes
 * (e.g. currently, string dimension calcs are defined in the {@link Layer}
 * interface, but these calcs may get moved to the {@link Canvas} interface
 * at some point).
 * 
 * @author chad takahashi
 */
public class StringSizer {
  
  // Layer used only for determinine the width or height of a string
  // (typically this is the root layer of a canvas)
  private Layer dummyLayer;
  
  public int getRotatedHeight(String s, GssProperties gss, double rotationAngle) {
    return dummyLayer.rotatedStringHeight(s, rotationAngle, 
        gss.fontFamily, gss.fontWeight, gss.fontSize);
  }
  
  public int getHeight(String s, GssProperties gss) {
    return dummyLayer.stringHeight(s, gss.fontFamily, gss.fontWeight, gss.fontSize);
  }
  
  public int getRotatedWidth(String s, GssProperties gss, double rotationAngle) {
    return dummyLayer.rotatedStringWidth(s, rotationAngle, 
        gss.fontFamily, gss.fontWeight, gss.fontSize);
  }
  
  public int getWidth(String s, GssProperties gss) {
    return dummyLayer.stringWidth(s, gss.fontFamily, gss.fontWeight, gss.fontSize);
  }
  
  public void setCanvas(Canvas canvas) {
    this.dummyLayer = canvas.getRootLayer();
  }
}
