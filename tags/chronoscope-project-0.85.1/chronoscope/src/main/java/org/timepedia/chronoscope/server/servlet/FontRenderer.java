package org.timepedia.chronoscope.server.servlet;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.timepedia.chronoscope.client.canvas.FontRendererService;
import org.timepedia.chronoscope.client.canvas.RenderedFontMetrics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URLEncoder;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet which implements FontRenderService for returning font metrics as well
 * as rendering font books. <p/> This class is currently a hack, derived from a
 * prototype to see if it would work, and grafted into Chronoscope, see <a
 * href="http://timepedia.blogspot.com/2007/06/gwt-canvas-rendering-rotated-text.html">GWT
 * Canvas: Rendering Rotated Text</a>. As such, it deserves a rewrite.
 */
public class FontRenderer extends RemoteServiceServlet
    implements FontRendererService {

  private static final boolean DEBUG = false;

  public void doGet(final HttpServletRequest req, final HttpServletResponse res)
      throws ServletException, IOException {

    String json = req.getParameter("json");

    String font = req.getParameter("ff");
    int fontWeight = getFontWeight(req, "fw", 500);
    int fontSize = getFontSize(req, "fs", 9);
    // TODO: hack to fix Java font rendering issue for this font
    if ("Verdana".equalsIgnoreCase(font) && fontSize == 10) {
      fontSize = 9;
    }
    double angle = getDouble(req, "a", 0.0);
    Color color = getColor(req.getParameter("c"));

    if (json != null) {

      RenderedFontMetrics rfm = getRenderedFontMetrics(req, font,
          "" + fontWeight, "" + fontSize + "pt", req.getParameter("c"),
          (float) angle);
      res.setContentType("text/javascript");
      ServletOutputStream out = res.getOutputStream();
      writeJSON(out, rfm, json);
    } else {
      res.setContentType("image/png");
      ServletOutputStream out = res.getOutputStream();
      renderFontBook(req, font, fontWeight, fontSize, angle, color, out);
    }
  }

  public RenderedFontMetrics getRenderedFontMetrics(String fontFamily,
      String fontWeight, String fontSize, String color, float angle) {
    return getRenderedFontMetrics(getThreadLocalRequest(), fontFamily,
        fontWeight, fontSize, color, angle);
  }

  public RenderedFontMetrics getRenderedFontMetrics(HttpServletRequest req,
      String fontFamily, String fontWeight, String fontSize, String color,
      float angle) {
    if ("Verdana".equalsIgnoreCase(fontFamily) && fontWeight
        .equalsIgnoreCase("10pt")) {
      fontWeight = "9pt";
    }
    Font f = new Font(fontFamily, getFontWeight(Font.PLAIN, fontWeight),
        getFontSize(9, fontSize));
    BufferedImage nop = new BufferedImage(4, 1, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = nop.createGraphics();
    g2d.setFont(f);
    FontMetrics fm = g2d.getFontMetrics();
    Rectangle2D cb = getMaxCharBounds(g2d);
    cb = getMaxCharBounds(g2d);

    RenderedFontMetrics rfm = new RenderedFontMetrics();
    rfm.maxAdvance = fm.getMaxAdvance();
    rfm.maxAscent = fm.getMaxAscent();
    rfm.maxDescent = fm.getMaxDescent();
    rfm.leading = fm.getLeading();
    rfm.advances = fm.getWidths();
    rfm.maxBoundsWidth = (int) cb.getWidth();
    rfm.maxBoundsHeight = (int) cb.getHeight();

    if (req != null) {
      String host = req.getHeader("host");

      rfm.url = req.getScheme() + "://" + host + req.getRequestURI() + "?ff="
          + fontFamily + "&fw=" + fontWeight + "&fs=" + fontSize + "&c="
          + URLEncoder
          .encode(color) + "&a=" + angle;
    }
    return rfm;
  }

  private Color getColor(String parameter) {
    if (parameter == null) {
      return Color.black;
    }
    parameter = parameter.trim();

    try {
      if (parameter.startsWith("#")) {
        return Color.decode(parameter);
      } else if (parameter.startsWith("rgb(")) {
        parameter = parameter.substring(4);
        parameter = parameter.substring(0, parameter.length() - 1);
        String rgb[] = parameter.split("\\s*,\\s*");
        return new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]),
            Integer.parseInt(rgb[2]));
      }
    } catch (NumberFormatException e) {
      return Color.black;
    }
    return Color.black;
  }

  private double getDouble(HttpServletRequest request, String param,
      double def) {
    String sparam = request.getParameter(param);
    if (sparam == null || "".equals(sparam)) {
      return def;
    }
    try {
      return Double.parseDouble(sparam);
    } catch (NumberFormatException nfe) {
      return def;
    }
  }

  private int getFontSize(HttpServletRequest req, String param, int def) {
    String sparam = req.getParameter(param);
    return getFontSize(def, sparam);
  }

  private int getFontSize(int def, String sparam) {
    if (sparam == null || "".equals(sparam)) {
      return def;
    }
    try {
      if (sparam.endsWith("pt")) {
        sparam = sparam.substring(0, sparam.length() - 2);
      }
      return Math.min(Math.max(Integer.parseInt(sparam), 9), 30);
    } catch (NumberFormatException nfe) {
      return def;
    }
  }

  private int getFontWeight(HttpServletRequest req, String param, int def) {
    String sparam = req.getParameter(param);
    return getFontWeight(def, sparam);
  }

  private int getFontWeight(int def, String sparam) {
    if (sparam == null || "".equals(sparam)) {
      return def < 700 ? Font.PLAIN : Font.BOLD;
    }
    try {
      if (sparam.equalsIgnoreCase("bold")) {
        return Font.BOLD;
      } else if (sparam.equalsIgnoreCase("normal")) {
        return Font.PLAIN;
      } else if (sparam.equalsIgnoreCase("bolder")) {
        return Font.BOLD;
      } else if (sparam.equalsIgnoreCase("lighter")) {
        return Font.PLAIN;
      }

      return Integer.parseInt(sparam) < 700 ? Font.PLAIN : Font.BOLD;
    } catch (NumberFormatException nfe) {
      return def < 700 ? Font.PLAIN : Font.BOLD;
    }
  }

  private int getInt(HttpServletRequest request, String param, int def) {
    String sparam = request.getParameter(param);
    if (sparam == null || "".equals(sparam)) {
      return def;
    }
    try {
      return Integer.parseInt(sparam);
    } catch (NumberFormatException nfe) {
      return def;
    }
  }

  private Rectangle2D getMaxCharBounds(Graphics2D g2d) {
    int maxWidth = 0;
    int maxHeight = 0;
    for (char c = 0; c < 256; c++) {
      Font f = g2d.getFont();
      if (f.canDisplay(c)) {
        TextLayout tl = new TextLayout("" + c, f, g2d.getFontRenderContext());
        Rectangle2D b = tl.getBounds();
        maxWidth = (int) Math.max(maxWidth, b.getWidth() + 5);
        maxHeight = (int) Math.max(maxHeight, b.getHeight() + 5);
      }
    }
    int max = Math.max(maxWidth, maxHeight);
    return new Rectangle(0, 0, max, max);
  }

  private void renderFontBook(HttpServletRequest req, String font,
      int fontWeight, int fontSize, double angle, Color color,
      ServletOutputStream out) throws IOException {
    RenderedFontMetrics rfm = getRenderedFontMetrics(req, font,
        fontWeight == Font.PLAIN ? "normal" : "bold", fontSize + "pt",
        toHex(color), (float) angle);

    int width = rfm.maxBoundsWidth * 16;
    int lineHeight = rfm.maxBoundsHeight;
    int height = lineHeight * 16;
    long end = System.nanoTime();
    BufferedImage bi = new BufferedImage(width, height,
        BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = (Graphics2D) bi.getGraphics();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_QUALITY);
    g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
        RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
        RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    Font f = new Font(font, fontWeight, fontSize);
    g2.setPaint(new Color(0, 0, 0, 0));
    g2.fillRect(0, 0, width, height);

    g2.setColor(color);
    g2.setPaint(color);
    g2.setFont(f);

    AffineTransform t = g2.getTransform();
    int x = 0, y = 0;
    for (char c = 0; c < 256; c++) {
      if (c % 16 == 0) {
        x = 0;
      }

      AffineTransform at = g2.getTransform();
      at.setToIdentity();
      at.translate(x, y);
      g2.setTransform(at);
      // DEBUG
      if (DEBUG) {
        g2.setColor(Color.red);
        g2.draw(new Rectangle2D.Float(0, 0, rfm.maxBoundsWidth, lineHeight));
      }
      // end DEBUG
      int mid = (lineHeight) / 2;
      g2.translate(rfm.maxBoundsWidth / 2, mid);
      g2.rotate(angle);
      // DEBUG section
      if (DEBUG) {
        g2.setColor(Color.red);

        g2.draw(new Rectangle2D.Double(-1, -1, 1, 1));
        g2.setColor(color);
      }
      // end DEBUG
      g2.translate(-rfm.maxBoundsWidth / 2, -mid);

//            g2.translate(0, angle < 0 ? rfm.maxAscent : -rfm.maxDescent);
      g2.translate(0, rfm.maxAscent);
      g2.drawString(String.valueOf(c), 0, 0);

      x += rfm.maxBoundsWidth;
      if (c % 16 == 15) {
        y += lineHeight;
      }
    }

    ImageIO.write(bi, "png", out);
    out.flush();
    g2.dispose();
  }

  private String toHex(Color color) {
    return "#" + Integer.toHexString(color.getRed())
        + Integer.toHexString(color.getGreen()) + Integer
        .toBinaryString(color.getBlue());
  }

  private void writeJSON(ServletOutputStream out, RenderedFontMetrics rfm,
      String json) throws IOException {
    out.print(json + "(");
    out.println("{");
    out.println("leading: " + rfm.leading + ", ");
    out.println("maxAscent: " + rfm.maxAscent + ", ");
    out.println("maxAdvance: " + rfm.maxAdvance + ", ");
    out.println("maxDescent: " + rfm.maxDescent + ", ");
    out.println("maxBoundsHeight: " + rfm.maxBoundsHeight + ", ");
    out.println("maxBoundsWidth: " + rfm.maxBoundsWidth + ", ");
    out.println("url: \"" + rfm.url + "\", ");
    out.println("advances: [");
    for (int i = 0; i < rfm.advances.length; i++) {
      out.print(rfm.advances[i]);
      if (i < rfm.advances.length - 1) {
        out.print(", ");
      }
    }
    out.println("]");
    out.println("}");
    out.println(");");
    out.flush();
  }
}
