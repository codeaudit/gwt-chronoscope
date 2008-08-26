package org.timepedia.chronoscope.client.canvas;

import com.google.gwt.core.client.GWT;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.render.LinearGradient;

/**
 * @author Ray Cromwell <ray@timepedia.org>
 */
public abstract class AbstractLayer implements Layer {

  public int saveLevel = 0;

  private Canvas canvas;

  public AbstractLayer() {
  }

  public AbstractLayer(Canvas canvas) {
    this.canvas = canvas;
  }

  public void clear() {
    clearRect(0, 0, getWidth(), getHeight());
  }

  public void clip(double x, double y, double width, double height) {
  }

  public DisplayList createDisplayList(String id) {
    return new DefaultDisplayListImpl(id, this);
  }

  public void drawRotatedText(double x, double y, double v, String label,
      String fontFamily, String fontWeight, String fontSize, String layerName,
      Chart chart) {
  }

  public void fillRect() {
    fillRect(0, 0, getWidth(), getHeight());
  }

  public Canvas getCanvas() {
    return canvas;
  }

  public void restore() {
    if (!GWT.isScript()) {
      saveLevel--;
    }
  }

  public int rotatedStringHeight(String str, double rotationAngle,
      String fontFamily, String fontWeight, String fontSize) {
    rotationAngle = Math.abs(rotationAngle);

    return (int) (Math.sin(rotationAngle) * stringWidth(str, fontFamily,
        fontWeight, fontSize) + Math.cos(rotationAngle) * stringHeight(str,
        fontFamily, fontWeight, fontSize));
  }

  public int rotatedStringWidth(String str, double rotationAngle,
      String fontFamily, String fontWeight, String fontSize) {
    rotationAngle = Math.abs(rotationAngle);
    return (int) (Math.cos(rotationAngle) * stringWidth(str, fontFamily,
        fontWeight, fontSize) + Math.sin(rotationAngle) * stringHeight(str,
        fontFamily, fontWeight, fontSize));
  }

  public void save() {
    if (!GWT.isScript()) {
      saveLevel++;
    }
  }

  public void setFillColor(PaintStyle p) {
    if (p instanceof Color) {
      setFillColor(p.toString());
    } else if (p instanceof LinearGradient) {
      setLinearGradient((LinearGradient) p);
    } else if (p instanceof RadialGradient) {
      setRadialGradient((RadialGradient) p);
    } else if (p instanceof CanvasPattern) {
      setCanvasPattern((CanvasPattern) p);
    }
  }

  public void setShadowColor(Color shadowColor) {
    setShadowColor(shadowColor.toString());
  }

  public void setStrokeColor(PaintStyle p) {

    if (p instanceof Color) {
      setStrokeColor(p.toString());
    }
  }
}
