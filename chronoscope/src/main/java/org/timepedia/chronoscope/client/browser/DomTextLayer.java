package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.impl.ClippedImageImpl;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.canvas.AbstractLayer;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.canvas.CanvasFontMetrics;
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.RenderedFontMetrics;

import java.util.HashMap;

/**
 * A class that implements text rendering by positioning DIVs with text or
 * images over the canvas. Deprecated because most canvas implementations
 * include text support now.
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
@Deprecated
public abstract class DomTextLayer extends AbstractLayer {

  public static class TextLayer {

    public Element layerElem;

    public Bounds bounds;
  }

  private static final HashMap fontMetricsCache = new HashMap();

  public Element metricDiv;

  protected HashMap metricMap = new HashMap();

  protected final HashMap layers = new HashMap();

  private boolean printOnce = false;

  protected DomTextLayer(Canvas canvas) {
    super(canvas);
  }

  public void clearTextLayer(String layerName) {
    TextLayer layer = (TextLayer) layers.get(layerName);
    if (layer != null) {
      DOM.setInnerHTML(layer.layerElem, "");
    }
  }

  public Element createTextDiv() {
    Element textDiv = DOM.createElement("div");
    DOM.setStyleAttribute(textDiv, "position", "absolute");

    DOM.setStyleAttribute(textDiv, "backgroundColor", "transparent");
    return textDiv;
  }

  // overall, this routine needs to be more robust
  // and there probably should be a TextLayout class that can layout multiline text with wrapping in the presence
  // of rotations within an enclosed area

  public void drawRotatedText(final double x, final double y,
      final double angle, String label, String fontFamily,
      String fontWeight, String fontSize, final String layerName,
      final Chart chart) {
    save();
    double cx = (double)canvasStringWidth(label, fontFamily, fontWeight, fontSize) / 2.0;
    double cy = (stringHeight(label, fontFamily, fontWeight, fontSize))/2;
   
    translate(x, y);
    rotate(angle);
    setFillColor(new Color(getStrokeColor()));
    
   translate(angle > 0 ? 0 : -cx*2, angle < 0 ? 0 : -cy*2);
    
    fillText(label, 0, 0, fontFamily, fontSize, angle > 0 ? "top" : "top");
    
    
    restore();
  }

  protected abstract int canvasStringWidth(String label, String fontFamily,
      String fontWeight, String fontSize);

  protected abstract void fillText(String label, double x, double y,
      String fontFamily, String fontSize, String baseline);

  public void drawText(double x, double y, String label, String fontFamily,
      String fontWeight, String fontSize, String layerName,
      Cursor cursorStyle) {
    TextLayer layer = getTextLayer(layerName);
    Element layerElem = layer.layerElem;
    Element textDiv = createTextDiv();
    if (cursorStyle == Cursor.CONTRASTED) {
        DOM.setStyleAttribute(textDiv, "textShadow", "-1px 0 white, 0 1px white, 1px 0 white, 0 -1px white");
        double fontSizeValue = Double.valueOf(fontSize.substring(0, fontSize.length() - 2));
    }

    DOM.setStyleAttribute(textDiv, "left", (x - layer.bounds.x) + "px");
    DOM.setStyleAttribute(textDiv, "top", (y - layer.bounds.y) + "px");
    DOM.setStyleAttribute(textDiv, "fontFamily", fontFamily);
    DOM.setStyleAttribute(textDiv, "fontSize", fontSize);
    DOM.setStyleAttribute(textDiv, "fontWeight", fontWeight);
    DOM.setStyleAttribute(textDiv, "color", getStrokeColor());
    DOM.setStyleAttribute(textDiv, "opacity", getTransparency());
    if (cursorStyle == Cursor.CLICKABLE) {
      DOM.setStyleAttribute(textDiv, "textDecoration", "underline");
      DOM.setStyleAttribute(textDiv, "cursor", "pointer");
    }
    DOM.setInnerHTML(textDiv, "<nobr>" + label + "</nobr>");
    DOM.appendChild(layerElem, textDiv);
    // textDiv = null;
    // layerElem = null;
    // layer = null;
  }

   /**
    * Add Semi Transparent Background.  Deprecated.
    */
    public void addSemiTransparentBackground(double x, double y, String label, String fontFamily,
            String fontWeight, String fontSize, String opacity, TextLayer layer, Element layerElem) {
        Element textDivBackground = createTextDiv();
        DOM.setStyleAttribute(textDivBackground, "left", (x - layer.bounds.x) + "px");
        DOM.setStyleAttribute(textDivBackground, "top", (y - layer.bounds.y) + "px");
        DOM.setStyleAttribute(textDivBackground, "fontFamily", fontFamily);
        DOM.setStyleAttribute(textDivBackground, "fontSize", fontSize);
        DOM.setStyleAttribute(textDivBackground, "fontWeight", fontWeight);
        DOM.setStyleAttribute(textDivBackground, "color", getStrokeColor());
        DOM.setStyleAttribute(textDivBackground, "opacity", opacity);
        DOM.setStyleAttribute(textDivBackground, "backgroundColor", chooseBackgroundColor(getStrokeColor()));
        DOM.setStyleAttribute(textDivBackground, "textDecoration", "underline");
        DOM.setStyleAttribute(textDivBackground, "cursor", "pointer");
        DOM.setInnerHTML(textDivBackground, "<nobr>" + label + "</nobr>");
        DOM.appendChild(layerElem, textDivBackground);
        // textDivBackground = null;
    }

    /**
     * Select a background color different from the font color.
     */
    public String chooseBackgroundColor(String fontColor) {
        StringBuffer backgroundColor = new StringBuffer();
        if (fontColor.startsWith("rgba(")) {
            //Formats such as rgba(255,255,255,255)
            String[] backColor = fontColor.replace("rgba(", "").replace(")", "").split(",");
            backgroundColor.append("rgba(");
            for (int i = 0; i < backColor.length; i++) {
                Short value = Short.valueOf(backColor[i]);
                //Number of negation
                Short v = new Short((short) (~value.shortValue()));
                backgroundColor.append(Integer.toHexString(v).substring(6, 8));
                if (i != backColor.length - 1) {
                    backgroundColor.append(",");
                } else {
                    backgroundColor.append(")");
                }
            }
        } else if (fontColor.startsWith("#")) {
            //Formats such as #FFFFFF
            backgroundColor.append("#");
            for (int i = 1; i < fontColor.length() - 1; i = i + 2) {
                StringBuffer sb = new StringBuffer();
                sb.append(fontColor.charAt(i));
                sb.append(fontColor.charAt(i + 1));
                // 10 decimal converted to 16 hex
                Short val = Short.parseShort(sb.toString(), 16);
                //Number of negation, 16 hex converted to 10 decimal
                Short v = new Short((short) (~val.shortValue()));
                //Background color to highlight
                if(v<-100){
                    v=Integer.valueOf(v+100).shortValue();
                }
                backgroundColor.append(Integer.toHexString(v).substring(6, 8));
            }
        }
        return backgroundColor.toString();
    }

  public abstract Element getElement();

  public TextLayer getTextLayer(String layerName) {                            
    TextLayer layer = (TextLayer) layers.get(layerName);
    if (layer == null) {
      Element layerElem;
      layerElem = DOM.createElement("div");
      DOM.setStyleAttribute(layerElem, "position", "absolute");
      DOM.setIntStyleAttribute(layerElem, "left", 0);
      DOM.setIntStyleAttribute(layerElem, "top", 0);
      DOM.setIntStyleAttribute(layerElem, "width", (int) getWidth());
      DOM.setIntStyleAttribute(layerElem, "height", (int) getHeight());
      DOM.setStyleAttribute(layerElem, "backgroundColor", "transparent");
      DOM.setStyleAttribute(layerElem, "zIndex", "" + (getLayerOrder() + 1));
      DOM.setStyleAttribute(layerElem, "overflow", "visible");
      DOM.appendChild(DOM.getParent(getElement()), layerElem);
      layer = new TextLayer();
      layer.layerElem = layerElem;
      layer.bounds = new Bounds(0, 0, getWidth(), getHeight());
      layers.put(layerName, layer);
      // layerElem = null;
    }
    return layer;
  }

  public int rotatedStringHeight(String str, double rotationAngle,
      String fontFamily, String fontWeight, String fontSize) {
    String metricsKey = getStrokeColor() + fontFamily + fontWeight + fontSize
        + rotationAngle;
    CanvasFontMetrics rmt = (CanvasFontMetrics) fontMetricsCache
        .get(metricsKey);
    if (rmt != null && rmt.rfm != null) {
      return rmt.rfm.stringHeight(str.toCharArray());
    } else {
      return super
          .rotatedStringHeight(str, rotationAngle, fontFamily, fontWeight,
              fontSize);
    }
  }

  public int rotatedStringWidth(String str, double rotationAngle,
      String fontFamily, String fontWeight, String fontSize) {
    String metricsKey = getStrokeColor() + fontFamily + fontWeight + fontSize
        + rotationAngle;
    CanvasFontMetrics rmt = (CanvasFontMetrics) fontMetricsCache
        .get(metricsKey);
    if (rmt != null && rmt.rfm != null) {
      return rmt.rfm.stringWidth(str.toCharArray());
    } else {
      return super
          .rotatedStringWidth(str, rotationAngle, fontFamily, fontWeight,
              fontSize);
    }
  }

  public void setTextLayerBounds(String layerName, Bounds bounds) {
    TextLayer layer = getTextLayer(layerName);
    Element layerElem = layer.layerElem;
    layer.bounds = new Bounds(bounds);

    DOM.setStyleAttribute(layerElem, "width", bounds.width + "px");
    DOM.setStyleAttribute(layerElem, "height", bounds.height + "px");
    DOM.setStyleAttribute(layerElem, "left", bounds.x + "px");
    DOM.setStyleAttribute(layerElem, "top", bounds.y + "px");
    DOM.setStyleAttribute(layerElem, "overflow", "visible");
    DOM.setStyleAttribute(layerElem, "zIndex", "" + (getLayerOrder() + 1));
    DOM.setStyleAttribute(layerElem, "backgroundColor", "transparent");
    DOM.setStyleAttribute(layerElem, "visibility", "visible");
  }

  public int stringHeight(String string, String font, String bold,
      String size) {
    Element div = getMetricDiv();

    DOM.setStyleAttribute(div, "fontFamily", font);
    DOM.setStyleAttribute(div, "fontWeight", bold);
    DOM.setStyleAttribute(div, "fontSize", size);
    DOM.setInnerHTML(div, string);

    return DOM.getElementPropertyInt(div, "clientHeight");
  }

  public int stringWidth(String string, String font, String bold, String size) {
    Element div = getMetricDiv();
    DOM.setStyleAttribute(div, "fontFamily", font);
    DOM.setStyleAttribute(div, "fontWeight", bold);
    DOM.setStyleAttribute(div, "fontSize", size);
    DOM.setInnerHTML(div, string);

    return DOM.getElementPropertyInt(div, "clientWidth");
  }

  private void drawTextImage(double x, double y, String label, String layerName,
      double angle, RenderedFontMetrics rfm) {
    TextLayer layer = getTextLayer(layerName);
    Element layerElem = layer.layerElem;
    double x1 = x, y1 = y;
    int sign = 1;
    if (angle < 0) {
      int madv = rfm.stringWidth(label.toCharArray());
      x1 += madv * Math.cos(Math.abs(angle));
      y1 += madv * Math.sin(Math.abs(angle));
      x1 -= rfm.stringWidth(String.valueOf(label.charAt(0)).toCharArray())
          * Math.cos(Math.abs(angle));
      y1 -=
          rfm.stringWidth(String.valueOf(label.charAt(0)).toCharArray()) * Math
              .sin(Math.abs(angle)) + 5;
    }
    Bounds b = new Bounds();
    ClippedImageImpl ci = (ClippedImageImpl) GWT.create(ClippedImageImpl.class);

    for (int i = 0; i < label.length(); i++) {
      char c = label.charAt(i);
      rfm.getBounds(b, c);
      Element elem = (Element) ci
          .createStructure(rfm.url, (int) b.x, (int) (b.y + 1), (int) b.width,
              (int) b.height);

      DOM.setStyleAttribute(elem, "position", "absolute");
      DOM.setStyleAttribute(elem, "left", (x1 - layer.bounds.x) + "px");
      DOM.setStyleAttribute(elem, "top", (y1 - layer.bounds.y) + "px");
      DOM.setElementProperty(elem, "letter", "" + c);
      DOM.appendChild(layerElem, elem);
      int adv = rfm.getAdvance(c);
      x1 += sign * adv * Math.cos(angle);
      y1 += sign * adv * Math.sin(angle);
      //elem = null;
    }
    // ci = null;
    // b = null;
    // layerElem = null;
    // layer = null;
  }

  private native Element getDocumentElement() /*-{
         return $doc.body;
    }-*/;

  private Element getMetricDiv() {
    if (metricDiv == null) {
      Element gssCssElement = ((CssGssViewSupport) getCanvas().getView()).getGssCssElement();
      if (gssCssElement == null) {
        System.out.println("WARN: DomTextLayer.getMetricDiv " + getCanvas().getClass().getName() + ".getView()" + " has null gssCssElement.");
      } else {
        metricDiv = DOM.createDiv();
        DOM.setStyleAttribute(metricDiv, "position", "absolute");
        DOM.setStyleAttribute(metricDiv, "padding", "0px");
        DOM.setStyleAttribute(metricDiv, "margin", "0px");
        DOM.setStyleAttribute(metricDiv, "border", "0px");
        DOM.setStyleAttribute(metricDiv, "visibility", "hidden");
        DOM.appendChild(gssCssElement, metricDiv);
      }
    }
    return metricDiv;
  }
}
