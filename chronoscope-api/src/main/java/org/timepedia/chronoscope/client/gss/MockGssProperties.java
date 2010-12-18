package org.timepedia.chronoscope.client.gss;

import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.exporter.client.Exportable;

/**
 * Canned property object used for testing
 */
public class MockGssProperties extends GssProperties implements Exportable {

  public MockGssProperties() {
    this.bgColor = Color.TRANSPARENT;
    this.color = Color.BLACK;
    this.width = 1;
    this.fontFamily = "Helvetica";
    this.fontSize = "10pt";
    this.fontWeight = "normal";
    this.lineThickness = 1;
    this.left = 0;
    this.top = 0;
    this.shadowBlur = 0;
    this.shadowColor = Color.LIGHTGRAY;
    this.shadowOffsetX = 0;
    this.shadowOffsetY = 0;
    this.size = 1;
    this.transparency = 1.0;
    this.visible = true;
    
  }
}
