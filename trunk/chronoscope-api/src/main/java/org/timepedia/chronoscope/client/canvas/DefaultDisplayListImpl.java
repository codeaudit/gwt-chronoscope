package org.timepedia.chronoscope.client.canvas;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.render.LinearGradient;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Helper class for implementors getting started with Custom Canvas/Layer
 * implementations <p/> not really intended for real use, just a placeholder
 * with no performance benefit (indeed, a deficit). DisplayLists will be most
 * useful when dealing with Flash/Silverlight/etc Canvases, where the
 * DisplayList will be compactly encoded in a wire format
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class DefaultDisplayListImpl implements DisplayList {

  interface Cmd {
    void exec();
  }

  private String id;
  // TODO - consider using the id here to eliminate all the layer.save() calls
  //  used to push the layer selection onto the display list

  private Layer layer;

  private ArrayList<Cmd> cmdBuffer = new ArrayList<Cmd>();

  public DefaultDisplayListImpl(String id, Layer layer) {
    this.id = id;
    this.layer = layer;
  }

  public void arc(final double x, final double y, final double radius,
      final double startAngle, final double endAngle, final int clockwise) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.arc(x, y, radius, startAngle, endAngle, clockwise);
      }
    });
  }

  public void beginPath() {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.beginPath();
      }
    });
  }

  public void clear() {
    log(getLayerId()+" clear()");
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.clear();
      }
    });
  }

  public void clearRect(final double x, final double y, final double width,
      final double height) {
    log(getLayerId()+" clearRect "+x+", "+y+" "+width + " "+height);
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.clearRect(x, y, width, height);
      }
    });
  }

  public void clearTextLayer(final String textLayer) {
    log(getLayerId() + "clearTextLayer "+textLayer);
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.clearTextLayer(textLayer);
      }
    });
  }

  public void clip(final double x, final double y, final double width,
      final double height) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.clip(x, y, width, height);
      }
    });
  }

  public void closePath() {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.closePath();
      }
    });
  }

  public DisplayList createDisplayList(String id) {
    log("createDisplayList "+id);
    return layer.createDisplayList(id);
  }

  public void dispose() {
    layer=null;
    cmdBuffer.clear();
  }

  public LinearGradient createLinearGradient(double startx, double starty,
      double endx, double endy) {
    return layer.createLinearGradient(startx, starty, endx, endy);
  }

  public PaintStyle createPattern(String imageUri) {
    return layer.createPattern(imageUri);
  }

  public RadialGradient createRadialGradient(double x0, double y0, double r0,
      double x1, double y1, double r1) {
    return layer.createRadialGradient(x0, y0, r0, x1, y1, r1);
  }

  public void drawImage(final Layer layer2, final double sx, final double sy,
      final double swidth, final double sheight, final double dx,
      final double dy, final double dwidth, final double dheight) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.drawImage(layer2, sx, sy, swidth, sheight, dx, dy, dwidth,
            dheight);
      }
    });
  }

  public void drawImage(final CanvasImage image, final double dx, final double dy, final double dwidth,
      final double dheight) {
    cmdBuffer.add(new Cmd() {

      public void exec() {
        layer.drawImage(image, dx, dy, dwidth, dheight);
      }
    });
  }

  public void drawImage(final Layer layer2, final double x, final double y,
      final double width, final double height) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.drawImage(layer2, x, y, width, height);
      }
    });
  }

  public void drawRotatedText(final double x, final double y, final double v,
      final String label, final String fontFamily, final String fontWeight,
      final String fontSize, final String layerName, final Chart chart) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.drawRotatedText(x, y, v, label, fontFamily, fontWeight, fontSize,
            layerName, chart);
      }
    });
  }

  public void drawText(final double x, final double y, final String label,
      final String fontFamily, final String fontWeight, final String fontSize,
      final String textLayer, Cursor cursorStyle) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer
            .drawText(x, y, label, fontFamily, fontWeight, fontSize, textLayer,
                Cursor.DEFAULT);
      }
    });
  }

  public void execute() {
    Iterator i = cmdBuffer.iterator();
    while (i.hasNext()) {
      ((Cmd) i.next()).exec();
    }
  }

  public void fill() {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.fill();
      }
    });
  }

  public void fillRect(double startx, double starty, double width, double height) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.fillRect();
      }
    });
  }

  public void fillRect() {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.fillRect();
      }
    });
  }

  public Bounds getBounds() {
    return layer.getBounds();
  }

  public void setBounds(Bounds b) {
    layer.setBounds(b);
  }

  public Canvas getCanvas() {
    return layer.getCanvas();
  }

  public double getHeight() {
    return layer.getHeight();
  }

  public float getLayerAlpha() {
    return layer.getLayerAlpha();
  }

  public String getLayerId() {
    return layer.getLayerId();
  }

  public int getLayerOrder() {
    return layer.getLayerOrder();
  }

  public int getScrollLeft() {
    return layer.getScrollLeft();
  }

  public String getStrokeColor() {
    return layer.getStrokeColor();
  }

  public String getTransparency() {
    return layer.getTransparency();
  }

  public double getWidth() {
    return layer.getWidth();
  }

  public boolean isVisible() {
    return layer.isVisible();
  }

  public void lineTo(final double x, final double y) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.lineTo(x, y);
      }
    });
  }

  public void moveTo(final double x, final double y) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.moveTo(x, y);
      }
    });
  }

  public void rect(final double x, final double y, final double width,
      final double height) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.rect(x, y, width, height);
      }
    });
  }

  @Override
  public void rotate(final double angle) {
    cmdBuffer.add(new Cmd() {
      @Override
      public void exec() {
        layer.rotate(angle);
      }
    });
  }

  public void restore() {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.restore();
      }
    });
  }

  public int rotatedStringHeight(String str, double rotationAngle,
      String fontFamily, String fontWeight, String fontSize) {
    return layer.rotatedStringHeight(str, rotationAngle, fontFamily, fontWeight,
        fontSize);
  }

  public int rotatedStringWidth(String str, double rotationAngle,
      String fontFamily, String fontWeight, String fontSize) {
    return layer.rotatedStringWidth(str, rotationAngle, fontFamily, fontWeight,
        fontSize);
  }

  public void save() {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.save();
      }
    });
  }

  public void scale(final double sx, final double sy) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.scale(sx, sy);
      }
    });
  }

  public void setCanvasPattern(final CanvasPattern canvasPattern) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.setCanvasPattern(canvasPattern);
      }
    });
  }

  public void setComposite(final int mode) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.setComposite(mode);
      }
    });
  }

  public void setFillColor(final PaintStyle p) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.setFillColor(p);
      }
    });
  }



  public void setLayerAlpha(final float alpha) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.setLayerAlpha(alpha);
      }
    });
  }

  public void setLayerOrder(final int zorder) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.setLayerOrder(zorder);
      }
    });
  }

  public void setLinearGradient(final LinearGradient lingrad) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.setLinearGradient(lingrad);
      }
    });
  }

  public void setLineWidth(final double width) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.setLineWidth(width);
      }
    });
  }

  public void setRadialGradient(final RadialGradient radialGradient) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.setRadialGradient(radialGradient);
      }
    });
  }

  public void setScrollLeft(final int i) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.setScrollLeft(i);
      }
    });
  }

  public void setShadowBlur(final double width) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.setShadowBlur(width);
      }
    });
  }

  public void setShadowColor(final Color shadowColor) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.setShadowColor(shadowColor);
      }
    });
  }

  public void setShadowColor(final String color) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.setShadowColor(color);
      }
    });
  }

  public void setShadowOffsetX(final double x) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.setShadowOffsetX(x);
      }
    });
  }

  public void setShadowOffsetY(final double y) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.setShadowOffsetY(y);
      }
    });
  }

  public void setStrokeColor(final PaintStyle p) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.setStrokeColor(p);
      }
    });
  }

  
  public void setTextLayerBounds(final String textLayer,
      final Bounds textLayerBounds) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.setTextLayerBounds(textLayer, textLayerBounds);
      }
    });
  }

  public void setTransparency(final float value) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.setTransparency(value);
      }
    });
  }

  public void setVisibility(final boolean visibility) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.setVisibility(visibility);
      }
    });
  }

  public int stringHeight(String string, String font, String bold,
      String size) {
    return layer.stringHeight(string, font, bold, size);
  }

  public int stringWidth(String string, String font, String bold, String size) {
    return layer.stringWidth(string, font, bold, size);
  }

  public void stroke() {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.stroke();
      }
    });
  }

  public void translate(final double x, final double y) {
    cmdBuffer.add(new Cmd() {
      public void exec() {
        layer.translate(x, y);
      }
    });
  }

  private static void log(String msg){
    System.out.println("DefaultDisplayListImpl> "+msg);
  }
}
