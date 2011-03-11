package org.timepedia.chronoscope.client.browser;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.ChronoscopeOptions;
import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.canvas.AbstractLayer;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.CanvasImage;
import org.timepedia.chronoscope.client.canvas.CanvasPattern;
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.PaintStyle;
import org.timepedia.chronoscope.client.canvas.RadialGradient;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.render.LinearGradient;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;

/**
 * An implementation of the Layer interface using the Safari/WHATWG Javascript
 * CANVAS.
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class BrowserLayer extends AbstractLayer {

  private static final String[] compositeModes = {"copy", "source-atop",
      "source-in", "source-out", "source-over", "destination-atop",
      "destination-in", "destination-out", "destination-over", "darker",
      "lighter", "xor"};

  private static final String[] TEXT_ALIGN = {
      "start", "end", "left", "right", "center"};

  private static final String[] TEXT_BASELINE = {
      "top", "hanging", "middle", "alphabetic", "ideographic", "bottom"};


  private JavaScriptObject ctx;

  private String strokeColor;
  private Color _strokeColor=Color.GRAY;

  private String fillColor;
  private Color _fillColor=Color.TRANSPARENT;

  private LinearGradient linearGradient;
  private RadialGradient radialGradient;

  private Bounds bounds = new Bounds();

  private String layerId, divElementId, canvasElementId;

  private Element layerDivElement, layerCanvasElement;

  private int zorder;

  private int scrollLeft;

  private float transparency;

  // DEBUG HACK
  public boolean SHOW_BOXES = false;

  public BrowserLayer(BrowserCanvas canvas, String layerId, Bounds bounds) {
    super(canvas);
    String baseId = canvas.getView().getViewId()+"bl"+(int)(99.9 * Math.random());
    divElementId = baseId +"_"+ layerId;
    canvasElementId = baseId + "cv_" + layerId;
    this.layerId=layerId;
    createLayerDiv(canvas, bounds);
    setLayerOrder(Layer.Z_ORDER.indexOf(layerId) * 3);
    setBounds(bounds);

    if (canvas.isAttached() && layerCanvasElement!=null) {
      onAttach();
    }
  }

  public void createLayerDiv(BrowserCanvas canvas, Bounds bounds) {
    layerDivElement = DOM.createElement("div");
    // layerDivElement.addClassName("chrono-layer");
    DOM.setStyleAttribute(layerDivElement, "overflow", "hidden");
    DOM.setElementAttribute(layerDivElement, "id", divElementId);
    initDivElement(layerDivElement, bounds);

    layerCanvasElement = DOM.createElement("canvas");
    DOM.setElementAttribute(layerCanvasElement, "id", canvasElementId);
    initCanvasElement(layerCanvasElement);

    positionDomElements(layerDivElement, layerCanvasElement, bounds);

    Element parent = canvas.getElement();
    if ((parent != null) && (layerDivElement != null) && (layerCanvasElement != null)) {
      DOM.appendChild(layerDivElement, layerCanvasElement);
      DOM.appendChild(parent, layerDivElement);
    }

    if(SHOW_BOXES) {
      DOM.setStyleAttribute(layerDivElement, "border", "1px dashed orange");
    }
    // return layerDivElement;
  }

  private static void positionDomElements(Element div, Element can, Bounds bounds) {
    DOM.setStyleAttribute(div, "width", "" + (int) bounds.width + "px");
    DOM.setStyleAttribute(div, "height", "" + (int) bounds.height + "px");
    DOM.setStyleAttribute(div, "top", (int) bounds.y + "px");
    DOM.setStyleAttribute(div, "left", (int) bounds.x + "px");

    DOM.setElementPropertyInt(can, "width", (int)bounds.width);
    DOM.setElementPropertyInt(can, "height", (int)bounds.height);
  }

  private static void initDivElement(Element div, Bounds bounds) {
    DOM.setStyleAttribute(div, "position", "absolute");
    DOM.setStyleAttribute(div, "visibility", "visible");
  }

  private static void initCanvasElement(Element can) {
    DOM.setStyleAttribute(can, "position", "absolute");
    DOM.setStyleAttribute(can, "top", "0px");
    DOM.setStyleAttribute(can, "left", "0px");
    // DOM.setStyleAttribute(can, "top", (int) bounds.y + "px");
    // DOM.setStyleAttribute(can, "left", (int) bounds.x + "px");
  }


  public void dispose() {
    linearGradient = null;
    radialGradient = null;
    bounds = null;
    ctx = null;

    // remove from the id2layer map in BrowserCanvas
    ((BrowserCanvas)getCanvas()).remove(layerId);

    // remove from the DOM
    if (null != layerCanvasElement ) {
      if (layerCanvasElement.hasParentElement()) {
        layerCanvasElement.removeFromParent();
      }
      layerCanvasElement = null;
    }
    if (null != layerDivElement) {
      if (layerDivElement.hasParentElement()) {
        layerDivElement.removeFromParent();
      }
      layerDivElement = null;
    }
  }

  public native void arc(double x, double y, double radius, double startAngle,
      double endAngle, int clockwise) /*-{
        this.@org.timepedia.chronoscope.client.browser.BrowserLayer::ctx.arc(x,y,radius,startAngle, endAngle, clockwise);
    }-*/;

  public native void beginPath() /*-{
        this.@org.timepedia.chronoscope.client.browser.BrowserLayer::ctx.beginPath();
    }-*/;

  /*public void arc(double x, double y, double radius, double startAngle, double endAngle, int clockwise) {

      arc0(ctx, x, y, radius, startAngle, endAngle, clockwise);
  }*/

  public void clearRect(double x, double y, double width, double height) {
    log(layerId + " clearRect "+x+", "+y+" w:"+width+" h:"+height);
    if (null == ctx) {
      log(layerId + " clearRect null ctx");
      return;
    }
    if (width != 0 && height != 0) {
      clearRect0(ctx, x, y, width, height);
    }
  }

  public void clear() {
    if ((null==ctx)||(null==layerCanvasElement)) { return; }
    log(layerId+".clear()");
    clear0(ctx, layerCanvasElement);
  }

  public void clearTextLayer(String textLayer) {
      log(layerId+" cleartTextLayer "+textLayer);
      // clear0(ctxText, canvasText);
  }

  public void clip(double x, double y, double width, double height) {
    super.clip(x, y, width, height);
    beginPath();
    rect(x, y, width, height);
    clip0(ctx);
  }

  public void closePath() {
    closePath0(ctx);
  }

  public LinearGradient createLinearGradient(double x, double y, double w, double h) {
    return new BrowserLinearGradient(this, x, y, w, h);
  }

  public PaintStyle createPattern(String imageUri) {
    return new BrowserCanvasPattern(this, imageUri);
  }

  public RadialGradient createRadialGradient(double x0, double y0, double r0,
      double x1, double y1, double r1) {
    return new BrowserRadialGradient(this, x0, y0, r0, x1, y1, r1);
  }

  public void drawImage(Layer layer, double x, double y, double width,
      double height) {
    if (layer instanceof BrowserCanvas) {
      drawImage0(ctx, ((BrowserLayer) ((BrowserCanvas) layer).getRootLayer()).getElement(),
              x, y, width, height);
    } else {
      drawImage0(ctx, ((BrowserLayer) layer).getElement(),
              x, y, width, height);
    }
  }

  public void drawImage(Layer layer, double sx, double sy, double swidth,
      double sheight, double dx, double dy, double dwidth, double dheight) {

    if (layer instanceof BrowserCanvas) {
      drawImageSrcDest0(ctx, ((BrowserLayer) ((BrowserCanvas) layer).getRootLayer()).getElement(),
              sx, sy, swidth, sheight,
              dx, dy, dwidth, dheight);
    } else {
      drawImageSrcDest0(ctx, ((BrowserLayer) layer).getElement(),
              sx, sy, swidth, sheight,
              dx, dy, dwidth, dheight);
    }
  }

  public void drawImage(CanvasImage image, double dx, double dy, double dwidth, double dheight) {
    Image im = ((BrowserCanvasImage)image).getNative();
    drawImageSrcDest0(ctx, im.getElement(),
            im.getOriginLeft(), im.getOriginTop(), im.getWidth(), im.getHeight(),
            dx, dy, dwidth, dheight);
  }

  public void fill() {
    if (null == ctx) { return; }
    fill0(ctx);
  }

  public native void fillRect(double x, double y, double w, double h) /*-{
       this.@org.timepedia.chronoscope.client.browser.BrowserLayer::ctx.fillRect(x, y, w, h);
    }-*/;

  public Bounds getBounds() {
    return bounds;
  }

  public void setBounds(Bounds bounds) {
    log("setBounds "+layerId + " bounds: "+ bounds);
    if (null == bounds) { return; }
    this.bounds = new Bounds(bounds);
    if (null != layerDivElement) {
      positionDomElements(layerDivElement, layerCanvasElement, bounds);
    }
  }

  @Override
  public int stringWidth(String label, String fontFamily,
      String fontWeight, String fontSize) {
    return csw(ctx, label, fontSize + " "+ fontFamily);
  }

  private native int csw(JavaScriptObject ctx, String label, String font) /*-{
    ctx.font = font;
    return ctx.measureText(label).width;
  }-*/;

  /* this is a hack because there is no height
     ... but it usually works. */
  @Override
  public int stringHeight(String label, String fontFamily,
      String fontWeight, String fontSize) {
    if (null == ctx) { return 12; } // FIXME
    return csw(ctx, "h", fontSize + " "+ fontFamily)*2;
  }
  
  public void drawRotatedText(double x, double y, double angle, String label,
      String fontFamily, String fontWeight, String fontSize, String layerName,
      Chart chart) {
    // translate(bounds.width / 2, bounds.height / 2);
    translate(x, y);
    rotate(angle);
    // int w = stringWidth(label, fontFamily, fontWeight, fontSize);
    // int h = stringHeight(label, fontFamily, fontWeight, fontSize);
    drawText(0, 0, label, fontFamily, fontWeight, fontSize, layerName, null);
    rotate(- angle);
    translate(-x, -y);
    // translate(- bounds.width / 2, - bounds.height / 2);
  }

  // TODO - alignment
  public void drawText(double x, double y, String label, String fontFamily,
      String fontWeight, String fontSize, String layerName,
      Cursor cursorStyle) {
    log(layerId + " drawText " +x+", "+y+" " + label + " " +fontSize + " " + layerName + " "+cursorStyle);
    Color _prevStrokeColor = _strokeColor;
    Color _prevFillColor = _fillColor;

    if (cursorStyle == Cursor.CONTRASTED) {
        setLineWidth(2);
        setStrokeColor(Color.WHITE);
        strokeText(label, x, y, fontFamily, fontSize, TEXT_BASELINE[TEXT_BASELINE_ALPHABETIC]);
        setStrokeColor(_prevStrokeColor);
    } else {
        setLineWidth(1);
        setStrokeColor(Color.WHITE);
        strokeText(label, x, y, fontFamily, fontSize, TEXT_BASELINE[TEXT_BASELINE_ALPHABETIC]);
        setStrokeColor(_prevStrokeColor);
    }

    setFillColor(_strokeColor);
    fillText(label, x, y, fontFamily, fontSize, TEXT_BASELINE[TEXT_BASELINE_ALPHABETIC]);
    setFillColor(_prevFillColor);

    if (cursorStyle == Cursor.CLICKABLE) {
      // DOM.setStyleAttribute(textDiv, "textDecoration", "underline");
      // DOM.setStyleAttribute(textDiv, "cursor", "pointer");
    }

  }

  protected void fillText(String label, double x, double y, String fontFamily,
      String fontSize, String baseline) {
    fillText0(ctx, label, x,y, fontSize+" "+fontFamily, baseline);
  }
  
  private native void fillText0(JavaScriptObject ctx, String label, double x,
      double y, String font, String baseline) /*-{
    ctx.font = font;
    ctx.textBaseline = baseline;
    ctx.fillText(label, x, y);
  }-*/;

  // TODO - alignment
  protected void strokeText(String label, double x, double y, String fontFamily,
      String fontSize, String baseline) {
    strokeText0(ctx, label, x,y, fontSize+" "+fontFamily, baseline);
  }

  private native void strokeText0(JavaScriptObject ctx, String label, double x,
      double y, String font, String baseline) /*-{
    ctx.font = font;
    ctx.textBaseline = baseline;
    ctx.strokeText(label, x, y);
  }-*/;
  public Element getElement() {
    return layerCanvasElement;
  }

  public double getHeight() {
    return bounds.height;
  }

  public float getLayerAlpha() {
    return DOM.getIntStyleAttribute(layerDivElement, "opacity");
  }

  public Element getLayerDivElement() {
    return layerDivElement;
  }

  public Element getLayerCanvasElement() {
    return layerCanvasElement;
  }

  public String getLayerId() {
    return layerId;
  }

  public int getLayerOrder() {
    return zorder;
  }

  public int getScrollLeft() {
    return scrollLeft;
  }

  public String getStrokeColor() {
    return strokeColor;
  }

  public String getTransparency() {
    return getTransparency0(ctx);
  }

  public double getWidth() {
    return bounds.width;
  }

  public boolean isVisible() {
    return DOM.getStyleAttribute(DOM.getElementById(divElementId), "visibility")
        .equals("visible");
  }

  public native void lineTo(double x, double y) /*-{
        this.@org.timepedia.chronoscope.client.browser.BrowserLayer::ctx.lineTo(x, y);
    }-*/;

  public native void moveTo(double x, double y) /*-{
        this.@org.timepedia.chronoscope.client.browser.BrowserLayer::ctx.moveTo(x, y);
    }-*/;

  public void rect(double x, double y, double width, double height) {
    rect0(ctx, x, y, width, height);
  }

  @Override
  public void rotate(double angle) {
    rotate0(ctx, angle);
  }
  
  private native void rotate0(JavaScriptObject ctx, double angle) /*-{
    ctx.rotate(angle);
  }-*/;

  public void restore() {
    restore0(ctx);
  }

  public void save() {
    save0(ctx);
  }

  public void scale(double sx, double sy) {
    log(layerId+ "scle "+sx+", "+sy );
     assert sx > 0.0 : "Scale X is zero";
      assert sy > 0.0 : "Scale Y is zero";
      scale0(sx, sy);
  }
  
  public native void scale0(double sx, double sy) /*-{
        this.@org.timepedia.chronoscope.client.browser.BrowserLayer::ctx.scale(sx, sy);
    }-*/;

  public void setCanvasPattern(CanvasPattern canvasPattern) {
    setCanvasPattern0(ctx, ((BrowserCanvasPattern) canvasPattern).getNative());
  }

  public void setComposite(int mode) {
    setComposite0(ctx, compositeModes[mode]);
  }

  public void setFillColor(Color c) {
    _fillColor = c;
    String color = c.getCSSColor();

    if ("transparent".equals(color)) {
      color = "rgba(0,0,0,0)";
    }

    try {
      fillColor = color;
      setFillColor0(ctx, color);
    } catch (Throwable t) {
      if (ChronoscopeOptions.isErrorReportingEnabled()) {
        Window.alert("Error is " + t + " for color " + color);
      }
    }
  }

  public void setLayerAlpha(float alpha) {
    DOM.setStyleAttribute(DOM.getElementById(divElementId), "opacity", "" + alpha);
  }

  /**
   * Sets the z-index (z order) for the canvas element and enclosing div.
   * Because the enclosing div elements are +1, the Layer.Z_ORDER.indexOf(layerId) should be
   * multiplied by, for example, 3 in order to make room for the div elements, etc.
   *
   * @param zorder z-index of the canvas DOM element, the DIV wrapper z-index will be zOrder+1
   */
  public void setLayerOrder(int zorder) {
    if (zorder < 0) { return; }
    DOM.setIntStyleAttribute(layerCanvasElement, "zIndex", zorder);
    DOM.setIntStyleAttribute(layerDivElement, "zIndex", zorder+1);

    this.zorder = zorder;
  }

  public void setLinearGradient(LinearGradient lingrad) {
    linearGradient = lingrad;
    if (null==ctx) {
      return;
    }
    try {
      setGradient0(ctx, ((BrowserLinearGradient) lingrad).getNative());
    } catch (Throwable t) {
      if (ChronoscopeOptions.isErrorReportingEnabled()) {
        Window.alert("setLinearGradient: " + t);
      }
    }
  }

  public native void setLineWidth(double width) /*-{
        this.@org.timepedia.chronoscope.client.browser.BrowserLayer::ctx.lineWidth=width;
    }-*/;

  public void setRadialGradient(RadialGradient radialGradient) {
    this.radialGradient = radialGradient;
    if (null != ctx) {
      setGradient0(ctx, ((BrowserRadialGradient) radialGradient).getNative());
    }
  }

  public void setScrollLeft(int i) {
    scrollLeft = i;
    DOM.setStyleAttribute(layerCanvasElement, "left", i + "px");
  }

  public void setShadowBlur(double width) {

    setShadowBlur0(ctx, width);
  }

  public void setShadowColor(String color) {

    setShadowColor0(ctx, color);
  }

  public void setShadowOffsetX(double x) {
    setShadowOffsetX0(ctx, x);
  }

  public void setShadowOffsetY(double y) {
    setShadowOffsetY0(ctx, y);
  }

  public void setStrokeColor(Color c) {
    _strokeColor = c;
    String color = c.getCSSColor();
    if ("transparent".equals(color)) {
      color = "rgba(0,0,0,0)";
    }

    try {
      strokeColor = color;
      setStrokeColor0(ctx, color);
    } catch (Throwable t) {
      if (ChronoscopeOptions.isErrorReportingEnabled()) {
        Window.alert("Error is " + t + " for strokecolor " + color);
      }
    }
  }

  public void setTransparency(float value) {
    transparency = value;
    if (null == ctx) { return; }
    setTransparency0(ctx, value);
  }

  public void setVisibility(boolean visibility) {
    DOM.setStyleAttribute(layerDivElement, "visibility", visibility ? "visible" : "hidden");
  }

  public native void stroke() /*-{
        this.@org.timepedia.chronoscope.client.browser.BrowserLayer::ctx.stroke();
    }-*/;

  public void translate(double x, double y) {
    log(layerId + "translate "+x+", "+y);
    translate0(ctx, x, y);
  }

  public JavaScriptObject getContext() {
    if ((ctx == null) && (null != layerCanvasElement)) {
      onAttach();
    }
    return ctx;
  }

  public void setTextLayerBounds(String layerName, Bounds bounds) {
    log(getLayerId()+getBounds() + " setTextLayerBounds" + bounds);
    setBounds(bounds);
    setVisibility(true); // TODO - move this somewhere else
  }

  /**
   * For future reference:
   *
   *  ChartPanel.onAttach
   *   PlotPanel.onAttach
   *    BrowserView.onAttach
   *     View.onAttach
   *      BrowserCanvas.attach
   *       Canvas.attach
   *        View.onCanvasReady
   *         View.allCanvasReady
   *          BrowserView.init
   *           BrowserCanvas.onAttach
   *            BrowserLayer.onAttach : you are here
   */
  public void onAttach() {
    if (null != layerCanvasElement) {
      ctx = getCanvasContext(layerCanvasElement);
      if (null != ctx) {
        if (null != linearGradient) {
            setLinearGradient(linearGradient);
        }
        if (null != radialGradient){
            setRadialGradient(radialGradient);
        }
      }
    }
  }
  /*
  void onAttach(Bounds b) {
    this.bounds = new Bounds(b);
    Element layerDivElement = DOM.createElement("div");
    layerDivElement.setId("lc"+layerId);
    // DOM.setElementAttribute(layerDivElement, "id","lc"+layerId);
    Element canvas = DOM.createElement("canvas");
    canvas.setId(layerId);
    // DOM.setElementAttribute(canvas, "id", getLayerId());

    DOM.setElementAttribute(canvas, "width", "" + b.width);
    DOM.setElementAttribute(canvas, "height", "" + b.height);
    DOM.setStyleAttribute(canvas, "width", "" + b.width + "px");
    DOM.setStyleAttribute(canvas, "height", "" + b.height + "px");
    DOM.setStyleAttribute(canvas, "position", "relative");

    DOM.setStyleAttribute(layerDivElement, "width", "" + b.width + "px");
    DOM.setStyleAttribute(layerDivElement, "height", "" + b.height + "px");

    DOM.setStyleAttribute(layerDivElement, "visibility", "visible");
    DOM.setStyleAttribute(layerDivElement, "position", "absolute");

    DOM.setStyleAttribute(layerDivElement, "overflow", "hidden");
    DOM.setStyleAttribute(layerDivElement, "top", b.y + "px");
    DOM.setStyleAttribute(layerDivElement, "left", b.x + "px");
    DOM.setStyleAttribute(layerDivElement, "overflow", "visible");

    ctx = getCanvasContext(canvas);
    layerDivElement.appendChild(canvas);
    // DOM.appendChild(layerDivElement, canvas);
  } */

  private native void clear0(JavaScriptObject ctx, Element can) /*-{
       ctx.clearRect(0, 0, can.width, can.height);
       var _width = can.width;
       can.width = 1;
       can.width = _width;
    }-*/;


  private native void clearRect0(JavaScriptObject ctx, double x, double y,
      double width, double height) /*-{
        ctx.clearRect(x,y,width,height);
    }-*/;

  private native void clip0(JavaScriptObject ctx) /*-{
        ctx.clip();
    }-*/;

  private native void closePath0(JavaScriptObject ctx) /*-{
        ctx.closePath();
    }-*/;

  private native void drawImage0(JavaScriptObject ctx, JavaScriptObject image,
      double x, double y, double w, double h) /*-{
          ctx.drawImage(image, x, y, w, h);
    }-*/;

  private native void drawImageSrcDest0(JavaScriptObject ctx, Element canvas,
      double sx, double sy, double swidth, double sheight,
      double dx, double dy, double dwidth, double dheight) /*-{
         ctx.drawImage(canvas, sx, sy, swidth, sheight, dx, dy, dwidth, dheight);

    }-*/;

  private native void fill0(JavaScriptObject ctx) /*-{
        ctx.fill();
    }-*/;

  private native JavaScriptObject getCanvasContext(Element elem) /*-{
        if (!!elem. getContext){
          return elem.getContext("2d");
        }
    }-*/;

  private String getFillColor() {
    return fillColor;
  }

  private native String getTransparency0(JavaScriptObject ctx) /*-{
        return ""+ctx.globalAlpha;
    }-*/;

  private native void rect0(JavaScriptObject ctx, double x, double y,
      double width, double height) /*-{
        ctx.rect(x,y,width,height);
    }-*/;

  private native void restore0(JavaScriptObject ctx)/*-{
         ctx.restore();
    }-*/;

  private native void save0(JavaScriptObject ctx) /*-{
        ctx.save();
    }-*/;

  private native void setCanvasPattern0(JavaScriptObject ctx,
      JavaScriptObject pattern) /*-{
            if(pattern != null) {
               ctx.fillStyle = pattern;
                }
    }-*/;

  private native void setComposite0(JavaScriptObject ctx, String compositeMode) /*-{
        ctx.globalCompositeOperation=compositeMode;
    }-*/;

  private native void setFillColor0(JavaScriptObject ctx, String color) /*-{
        ctx.fillStyle = color;
    }-*/;

  private native void setGradient0(JavaScriptObject ctx,
      JavaScriptObject lingrad) /*-{
        ctx.fillStyle = lingrad;
    }-*/;

  private native void setShadowBlur0(JavaScriptObject ctx, double width) /*-{
        ctx.shadowBlur=width;
    }-*/;

  private native void setShadowColor0(JavaScriptObject ctx, String color) /*-{

        ctx.shadowColor=color;
    }-*/;

  private native void setShadowOffsetX0(JavaScriptObject ctx, double x) /*-{
        ctx.shadowOffsetX=x;
    }-*/;

  private native void setShadowOffsetY0(JavaScriptObject ctx, double y) /*-{
        ctx.shadowOffsetY=y;
    }-*/;

  private native void setStrokeColor0(JavaScriptObject ctx, String color) /*-{
       ctx.strokeStyle=color;
    }-*/;

  private native void setTransparency0(JavaScriptObject ctx, float value) /*-{
        ctx.globalAlpha=value;
    }-*/;

  private native void translate0(JavaScriptObject ctx, double x, double y) /*-{
        ctx.translate(x,y);
    }-*/;

    private static void log (String msg) {
      System.out.println("BrowserLayer> "+ msg);
    }

}
