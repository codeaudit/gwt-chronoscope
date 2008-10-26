package org.timepedia.chronoscope.java2d.canvas;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.canvas.AbstractLayer;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.canvas.CanvasImage;
import org.timepedia.chronoscope.client.canvas.CanvasPattern;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.RadialGradient;
import org.timepedia.chronoscope.client.render.LinearGradient;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class LayerJava2D extends AbstractLayer {

  private static final boolean DEBUG = false;

  static class State {

    public Graphics2D ctx;

    public java.awt.Paint strokeColor;

    public java.awt.Paint fillColor;

    public BasicStroke currentStroke;

    public State(Graphics2D ctx, java.awt.Paint strokeColor,
        java.awt.Paint fillColor, BasicStroke currentStroke) {

      this.ctx = ctx;
      this.strokeColor = strokeColor;
      this.fillColor = fillColor;
      this.currentStroke = currentStroke;
    }
  }

  Pattern rgbPattern = Pattern
      .compile("rgb\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)");

  Pattern rgbaPattern = Pattern.compile(
      "rgba\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)");

  Stack<State> ctxStack = new Stack<State>();

  private String layerId;

  private Bounds bounds;

  private BufferedImage img;

  private Graphics2D ctx;

  private GeneralPath currentShape = new GeneralPath();

  private java.awt.Paint fillColor;

  private java.awt.Paint strokeColor;

  private BasicStroke currentStroke = new BasicStroke();

  private int compMode;

  private int zorder;

  private float layerAlpha;

  private boolean visible;

  public LayerJava2D(Canvas canvas, String layerId, Bounds b) {
    super(canvas);
    this.layerId = layerId;
    bounds = b;
    layerAlpha = 1.0f;
    visible = true;
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsConfiguration gc = ge.getDefaultScreenDevice()
        .getDefaultConfiguration();
//        int imageType = layerId.equals("backing") && false ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
//        img = new BufferedImage((int)b.width, (int)b.height, imageType);
    img = gc.createCompatibleImage((int) b.width, (int) b.height, layerId
        .equals("backing") ? Transparency.OPAQUE : Transparency.TRANSLUCENT);
    ctx = (Graphics2D) img.createGraphics();
    ctx.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    ctx.setRenderingHint(RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_QUALITY);
    ctx.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
        RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    setFillColor("rgba(0,0,0,0)");
    clearRect(0, 0, b.width, b.height);
  }

  public void arc(double x, double y, double radius, double startAngle,
      double endAngle, int clockwise) {

    //     Arc2D.Double a2d=new Arc2D.Double(x, y, radius, radius, startAngle/360.0*Math.PI*2.0, endAngle/360.0*2.0*Math.PI, Arc2D.OPEN);
    Ellipse2D.Double a2d = new Ellipse2D.Double(x - radius, y - radius,
        radius * 2, radius * 2);
    if (currentShape.getPathIterator(ctx.getTransform()).isDone()) {
      currentShape.moveTo((float) x, (float) y);
    }
    currentShape.append(a2d, false);
  }

  public void beginPath() {
//        currentShape.reset();
    currentShape = new GeneralPath();
  }

  public void clearRect(double x, double y, double width, double height) {

    ctx.setPaint(fillColor);
    save();
    setComposite(COPY);
    ctx.fillRect((int) x, (int) y, (int) width, (int) height);
    restore();
  }

  public void clearTextLayer(String layer) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void clip(int x, int y, int width, int height) {
    ctx.clipRect(x, y, width, height);
  }

  public void closePath() {
    currentShape.closePath();
  }

  public LinearGradient createLinearGradient(double startx, double starty,
      double endx, double endy) {
    return new LinearGradientJava2D(startx, starty, endx, endy);
  }

  public org.timepedia.chronoscope.client.canvas.PaintStyle createPattern(
      String imageUri) {
    return null;  //NI
  }

  public RadialGradient createRadialGradient(double x0, double y0, double r0,
      double x1, double y1, double r1) {
    return null;  //NI
  }

  public void dipose() {
    ctx.dispose();
  }

  public void drawImage(Layer backingCanvas, double x, double y, double width,
      double height) {

    ctx.drawImage(((LayerJava2D) backingCanvas).getImage(), (int) x, (int) y,
        (int) width, (int) height, null);
  }

  public void drawImage(Layer layer, double sx, double sy, double swidth,
      double sheight, double dx, double dy, double dwidth, double dheight) {
    ctx.drawImage(((LayerJava2D) layer).getImage(), (int) dx, (int) dy,
        (int) dx + (int) dwidth, (int) dy + (int) dheight, (int) sx, (int) sy,
        (int) sx + (int) swidth, (int) sy + (int) sheight,
        new Color(0, 0, 0, 0), null);
//        AffineTransform xform=new AffineTransform();
//        xform.translate(-sx, -sy);
//        xform.scale((double)dwidth/(double)swidth, (double)dheight/(double)sheight);
//        ctx.drawImage((BufferedImage)((CanvasJava2D)canvas).getImage(),
//                      new AffineTransformOp(xform, AffineTransformOp.TYPE_BILINEAR),
//                      dx, dy);
  }

  public void drawImage(CanvasImage image, double dx, double dy, double dwidth,
      double dheight) {
    //TODO: not implemented
  }

  public void drawRotatedText(double x, double y, double angle, String label,
      String fontFamily, String fontWeight, String fontSize, String layerName,
      Chart chart) {
    Font font = new Font(fontFamily, Font.PLAIN, Integer
        .parseInt(fontSize.substring(0, fontSize.length() - 2)) * 12 / 9);
    save();
    ctx.setFont(font);
    ctx.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    ctx.setRenderingHint(RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_QUALITY);
    ctx.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
        RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    ctx.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
        RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    ctx.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    FontMetrics fm = ctx.getFontMetrics();

    int width = fm.stringWidth(label);
    ctx.translate(x + width / 2 * Math.cos(angle),
        y + width / 2 * Math.sin(Math.abs(angle)));
    ctx.rotate(angle);
    ctx.translate(-width / 2, fm.getMaxAscent());
    ctx.setPaint(strokeColor);
    ctx.drawString(label, 0, 0);
    restore();
  }

  public void drawText(double x, double y, String label, String fontFamily,
      String fontWeight, String fontSize, String layer, Cursor cursor) {
    Font font = new Font(fontFamily, Font.PLAIN,
        Integer.parseInt(fontSize.substring(0, fontSize.length() - 2)));
    ctx.setPaint(strokeColor);
    ctx.setFont(font);

    ctx.drawString(label, (int) x,
        (int) y + ctx.getFontMetrics().getMaxAscent());
    if (DEBUG && label != null && !"".equals(label.trim())) {
      ctx.setPaint(Color.RED);

      TextLayout tl = new TextLayout(label, font, ctx.getFontRenderContext());
      Rectangle2D b = tl.getBounds();
      int h = (int)(tl.getAscent() + tl.getDescent() + tl.getLeading());
      ctx.drawRect((int)x, (int)y, (int)b.getWidth(), h);
    }
//    System.out.println("Drawing text " + label + " at " + x + ", "
//        + (y + ctx.getFontMetrics().getMaxAscent()) + " y=" + y + ", maxAscent="
//        + ctx.getFontMetrics().getMaxAscent() + " leading is " + ctx
//        .getFontMetrics().getLeading());

  }

  public void fill() {
    ctx.setPaint(fillColor);
    currentShape.closePath();
    ctx.fill(currentShape);
  }

  public void fillRect(double x, double y, double w, double h) {
    Rectangle2D.Double rect = new Rectangle2D.Double(x, y, w, h);
    ctx.setPaint(fillColor);
    ctx.fill(rect);
  }

  public Bounds getBounds() {
    return bounds;
  }

  public double getHeight() {
    return img.getHeight();
  }

  public Image getImage() {
    return img;
  }

  public float getLayerAlpha() {
    return layerAlpha;
  }

  public String getLayerId() {
    return layerId;
  }

  public int getLayerOrder() {
    return zorder;
  }

  public int getScrollLeft() {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String getStrokeColor() {
    return strokeColor.toString();
  }

  public String getTransparency() {
    return "1.0"; //NI
  }

  public double getWidth() {
    return img.getWidth();
  }

  public boolean isVisible() {
    return visible;
  }

  public void lineTo(double x, double y) {
    currentShape.lineTo((float) x, (float) y);
  }

  public void moveTo(double x, double y) {
    currentShape.moveTo((float) x, (float) y);
  }

  public void rect(double x, double y, double width, double height) {
    Rectangle2D.Double r2d = new Rectangle2D.Double(x, y, width, height);
    currentShape.append(r2d, true);
  }

  public void restore() {
    super.restore();
    State s = ctxStack.pop();
    ctx = s.ctx;
    strokeColor = s.strokeColor;
    fillColor = s.fillColor;
    currentStroke = s.currentStroke;
  }

  public void save() {
    super.save();
    Graphics2D newCtx = (Graphics2D) ctx.create();
    State s = new State(ctx, strokeColor, fillColor, currentStroke);
    ctxStack.push(s);
    ctx = newCtx;
  }

  public void scale(double sx, double sy) {
    ctx.scale(sx, sy);
  }

  public void setCanvasPattern(CanvasPattern canvasPattern) {
    //NI
  }

  public void setComposite(int mode) {
    Composite comp = ctx.getComposite();
    float alpha = comp instanceof AlphaComposite ? ((AlphaComposite) comp)
        .getAlpha() : 1.0f;
    switch (mode) {
      case SRC_ATOP:
        compMode = AlphaComposite.SRC_ATOP;
        break;
      case SRC_IN:
        compMode = AlphaComposite.SRC_IN;
        break;
      case SRC_OUT:
        compMode = AlphaComposite.SRC_OUT;
        break;
      case SRC_OVER:
        compMode = AlphaComposite.SRC_OVER;
        break;
      case DEST_ATOP:
        compMode = AlphaComposite.DST_ATOP;
        break;
      case DEST_IN:
        compMode = AlphaComposite.DST_IN;
        break;
      case DEST_OUT:
        compMode = AlphaComposite.DST_OUT;
        break;
      case DEST_OVER:
        compMode = AlphaComposite.DST_OVER;
        break;
      case XOR:
        compMode = AlphaComposite.XOR;
        break;
      case COPY:
        compMode = AlphaComposite.SRC;
        break;
      default:
        compMode = AlphaComposite.SRC_OVER;
    }
    ctx.setComposite(AlphaComposite.getInstance(compMode, alpha));
  }

  public void setFillColor(String color) {

    Matcher m = rgbPattern.matcher(color);
    Matcher m2 = rgbaPattern.matcher(color);

    if (m.matches()) {
      fillColor = new Color(Integer.parseInt(m.group(1)),
          Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)));
    } else if (m2.matches()) {
      fillColor = new Color((float) Integer.parseInt(m2.group(1)) / 255.0f,
          (float) Integer.parseInt(m2.group(2)) / 255.0f,
          (float) Integer.parseInt(m2.group(3)) / 255.0f,
          (float) Integer.parseInt(m2.group(4)) / 255.0f);
    } else if (color.equals("transparent")) {
      fillColor = new Color(0, 0, 0, 0);
    } else {

      fillColor = Color.decode(color);
    }
  }

  public void setLayerAlpha(float alpha) {
    this.layerAlpha = alpha;
  }

  public void setLayerOrder(int zorder) {
    this.zorder = zorder;
  }

  public void setLinearGradient(LinearGradient lingrad) {
//        ctx.setPaint(((LinearGradientJava2D)lingrad).getNative());
    fillColor = ((LinearGradientJava2D) lingrad).getNative();
  }

  public void setLineWidth(double width) {
    currentStroke = new BasicStroke((float) width);
  }

  public void setRadialGradient(RadialGradient radialGradient) {
    //NI
  }

  public void setScrollLeft(int i) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void setShadowBlur(double width) {
    //NI
  }

  public void setShadowColor(String color) {
    //NI
  }

  public void setShadowOffsetX(double x) {
    //NI
  }

  public void setShadowOffsetY(double y) {
    //NI
  }

  public void setStrokeColor(String color) {

    Matcher m = rgbPattern.matcher(color);
    Matcher m2 = rgbaPattern.matcher(color);

    if (m.matches()) {
      strokeColor = new Color(Integer.parseInt(m.group(1)),
          Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)));
    } else if (m2.matches()) {
      strokeColor = new Color(Integer.parseInt(m2.group(1)),
          Integer.parseInt(m2.group(2)), Integer.parseInt(m2.group(3)),
          Integer.parseInt(m2.group(4)));
    } else if (color.equals("transparent")) {
      strokeColor = new Color(0, 0, 0, 0);
    } else {
      strokeColor = Color.decode(color);
    }
  }

  public void setTextLayerBounds(String layerName, Bounds panelPosition) {
    //NI
  }

  public void setTransparency(float value) {

    ctx.setComposite(
        AlphaComposite.getInstance(AlphaComposite.SRC_OVER, value));
  }

  public void setVisibility(boolean visibility) {
    visible = visibility;
  }

  public int stringHeight(String string, String font, String bold,
      String size) {
    ctx.setFont(new Font(font, Font.PLAIN,
        Integer.parseInt(size.substring(0, size.length() - 2))));
    FontMetrics fm = ctx.getFontMetrics();
    Font f = ctx.getFont();
    TextLayout tl = new TextLayout(string, f, ctx.getFontRenderContext());
    Rectangle2D b = tl.getBounds();
    int h = (int) (tl.getAscent() + tl.getDescent()+tl.getLeading());
       // b.getHeight();//(int) fm.getMaxAscent() + fm.getMaxDescent() + 2;
//    System.out.println("height of " + string + " is " + h + "  vs " + fm
//        .getStringBounds(string, ctx));
    return h;
//        .getHeight();
  }

  public int stringWidth(String string, String font, String bold, String size) {
    ctx.setFont(new Font(font, Font.PLAIN,
        Integer.parseInt(size.substring(0, size.length() - 2))));
    return ctx.getFontMetrics().stringWidth(string);
  }

  public void stroke() {
    ctx.setStroke(currentStroke);
    ctx.setPaint(strokeColor);
    ctx.draw(currentShape);
  }

  public void translate(double x, double y) {
    ctx.translate(x, y);
  }
}
