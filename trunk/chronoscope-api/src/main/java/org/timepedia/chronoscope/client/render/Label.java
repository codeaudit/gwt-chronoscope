package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.util.ArgChecker;

/**
 * @author chad takahashi
 */
public class Label {
  private Bounds bounds = new Bounds();
  private String textLayerName;
  private String text = "label";
  private String fontFamily;
  private String fontWeight;
  private String fontSize;
  
  public Label(String text, String textLayerName, Layer layer,
      String fontFamily, String fontWeight, String fontSize) {
    init(text, textLayerName, layer, fontFamily, fontWeight, fontSize);
  }
  
  public Label(String text, String textLayerName, Layer layer, GssProperties gssProps) {
    ArgChecker.isNotNull(gssProps, "gssProps");
    init(text, textLayerName, layer, gssProps.fontFamily, gssProps.fontWeight, gssProps.fontSize);
  }
  
  private void init(String text, String textLayerName, Layer layer,
      String fontFamily, String fontWeight, String fontSize) {

    this.text = text;
    this.textLayerName = textLayerName;
    this.fontFamily = fontFamily;
    this.fontWeight = fontWeight;
    this.fontSize = fontSize;

    bounds.x = 0;
    bounds.y = 0;
    layer.save();
    bounds.height = layer.stringHeight(text, this.fontFamily, this.fontWeight, this.fontSize); 
    bounds.width = layer.stringWidth(text, this.fontFamily, this.fontWeight, this.fontSize);
    layer.restore();
  }

  public boolean isEmpty() {
    if (null == text) { return true; } else
    if (text.trim().isEmpty()) { return true; }
    return false;
  }

  public void draw(Layer layer) {
    layer.save();

    layer.drawText(bounds.x, bounds.bottomY(), text,
        fontFamily, fontWeight, fontSize,
        textLayerName, Cursor.DEFAULT);

    layer.restore();
  }
  
  public Bounds getBounds() {
    return bounds;
  }

  public String getText() {
    return text;
  }
  
  public String toString() {
    return bounds + "; text='" + text + "'";
  }

  public String getTextLayerName() {
    return textLayerName;
  }

  public void setLocation(double x, double y) {
    bounds.x = x;
    bounds.y = y;
  }

  private static void log(String msg) {
    System.out.print("Label> "+msg);
  }
}
