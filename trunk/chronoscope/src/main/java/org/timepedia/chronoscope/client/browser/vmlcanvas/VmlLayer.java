package org.timepedia.chronoscope.client.browser.vmlcanvas;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.widgetideas.graphics.client.CanvasGradient;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

import org.timepedia.chronoscope.client.browser.DomTextLayer;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.CanvasImage;
import org.timepedia.chronoscope.client.canvas.CanvasPattern;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.PaintStyle;
import org.timepedia.chronoscope.client.canvas.RadialGradient;
import org.timepedia.chronoscope.client.render.LinearGradient;

public class VmlLayer extends DomTextLayer {

  private String layerId;

  private Element layerContainer;

  private Bounds bounds;

  private GWTCanvas canvasWidget;

  private Element canvasWidgetElement;

  private int layerCount;

  private float alpha;

  private int order;

  private String strokeColor;

  private boolean visible;

  private static final String[] compositeModes = {"copy", "source-atop",
      "source-in", "source-out", "source-over", "destination-atop",
      "destination-in", "destination-out", "destination-over", "darker",
      "lighter", "xor"};

  private PaintStyle fillColor;

  public VmlLayer(VmlCanvas vmlCanvas, String layerId, Bounds b) {
    super(vmlCanvas);
    this.layerId = layerId;
    init(b);
  }

  private void init(Bounds b) {
    layerContainer = DOM.createElement("div");
    this.bounds = new Bounds(b);
    canvasWidget = new GWTCanvas((int) b.width, (int) b.height);
    canvasWidgetElement = canvasWidget.getElement();

    String lc = String.valueOf(layerCount++);
    DOM.setElementAttribute(layerContainer, "id", "_lc_" + layerId + lc);
    DOM.setElementAttribute(canvasWidgetElement, "id", "_cv_" + layerId + lc);

    DOM.setElementAttribute(canvasWidgetElement, "width", "" + b.width);
    DOM.setElementAttribute(canvasWidgetElement, "height", "" + b.height);

    DOM.setStyleAttribute(layerContainer, "width", "" + b.width + "px");
    DOM.setStyleAttribute(layerContainer, "height", "" + b.height + "px");
    DOM.setStyleAttribute(canvasWidgetElement, "width", "" + b.width + "px");
    DOM.setStyleAttribute(canvasWidgetElement, "height", "" + b.height + "px");
    DOM.setStyleAttribute(canvasWidgetElement, "position", "absolute");
    DOM.setStyleAttribute(layerContainer, "visibility", "visible");
    DOM.setStyleAttribute(layerContainer, "position", "absolute");

    DOM.setStyleAttribute(layerContainer, "overflow", "hidden");
    DOM.setStyleAttribute(layerContainer, "top", b.y + "px");
    DOM.setStyleAttribute(layerContainer, "left", b.x + "px");
    DOM.setStyleAttribute(layerContainer, "overflow", "visible");
  }

  public Element getLayerElement() {
    return layerContainer;
  }

  public Element getElement() {
    return canvasWidgetElement;
  }

  public void arc(double x, double y, double radius, double startAngle,
      double endAngle, int clockwise) {
    canvasWidget.arc(x, y, radius, startAngle, endAngle, clockwise == 0);
  }

  public void beginPath() {
    canvasWidget.beginPath();
  }

  public void clear() {
    canvasWidget.clear();
  }

  public void clearRect(double x, double y, double width, double height) {
    canvasWidget.fillRect(x, y, width, height);
  }

  public void closePath() {
    canvasWidget.closePath();
  }

  public LinearGradient createLinearGradient(final double startx,
      final double starty, final double endx, final double endy) {
    return new VmlLinearGradient(startx, starty, endx, endy);
  }

  public PaintStyle createPattern(String imageUri) {
    return new CanvasPattern() {
    };
  }

  public RadialGradient createRadialGradient(final double x0, final double y0,
      final double r0, final double x1, final double y1, final double r1) {
    return new VmlRadialGradient(x0, y0, r0, x1, y1, r1);
  }

  public void drawImage(Layer layer, double x, double y, double width,
      double height) {
  }

  public void drawImage(Layer layer, double sx, double sy, double swidth,
      double sheight, double dx, double dy, double dwidth, double dheight) {
  }

  public void drawImage(CanvasImage image, double dx, double dy, double dwidth,
      double dheight) {
  }

  public void fill() {
    canvasWidget.fill();
  }

  public void fillRect(double startx, double starty, double width,
      double height) {
    canvasWidget.fillRect(startx, starty, width, height);
  }

  public Bounds getBounds() {
    return bounds;
  }

  public double getHeight() {
    return bounds.height;
  }

  public float getLayerAlpha() {
    return alpha;
  }

  public String getLayerId() {
    return layerId;
  }

  public int getLayerOrder() {
    return order;
  }

  public int getScrollLeft() {
    return 0;
  }

  public String getStrokeColor() {
    return strokeColor;
  }

  public String getTransparency() {
    return canvasWidget.getGlobalAlpha() + "";
  }

  public double getWidth() {
    return bounds.width;
  }

  public boolean isVisible() {
    return visible;
  }

  public void lineTo(double x, double y) {
    canvasWidget.lineTo(x, y);
  }

  public void moveTo(double x, double y) {
    canvasWidget.moveTo(x, y);
  }

  public void rect(double x, double y, double width, double height) {
    canvasWidget.rect(x, y, width, height);
  }

  public void restore() {
    canvasWidget.restoreContext();
  }

  public void save() {
    canvasWidget.restoreContext();
  }

  public void scale(double sx, double sy) {
    canvasWidget.scale(sx, sy);
  }

  public void setCanvasPattern(CanvasPattern canvasPattern) {
  }

  public void setComposite(int mode) {
    canvasWidget.setGlobalCompositeOperation(compositeModes[mode]);
  }

  public void setFillColor(org.timepedia.chronoscope.client.canvas.Color color) {
    fillColor = new org.timepedia.chronoscope.client.canvas.Color(color.getCSSColor());
    canvasWidget.setFillStyle(new Color(color.getCSSColor()));
  }

  public void setLayerAlpha(float alpha) {
    this.alpha = alpha;
  }

  public void setLayerOrder(int zorder) {
    order = zorder;
  }

  public void setLinearGradient(LinearGradient lingrad) {
    fillColor = lingrad;
    canvasWidget.setFillStyle(((VmlLinearGradient) lingrad).getNative());
  }

  public void setLineWidth(double width) {
    canvasWidget.setLineWidth(width);
  }

  public void setRadialGradient(RadialGradient radialGradient) {
    fillColor = radialGradient;
    canvasWidget.setFillStyle(((VmlRadialGradient) radialGradient).getNative());
  }

  public void setScrollLeft(int i) {

  }

  public void setShadowBlur(double width) {
  }

  public void setShadowColor(String color) {
  }

  public void setShadowOffsetX(double x) {
  }

  public void setShadowOffsetY(double y) {
  }

  public void setStrokeColor(org.timepedia.chronoscope.client.canvas.Color color) {
    this.strokeColor = color.getCSSColor();
    canvasWidget.setStrokeStyle(new Color(this.strokeColor));
  }

  public void setTransparency(float value) {
    canvasWidget.setGlobalAlpha(value);
  }

  public void setVisibility(boolean visibility) {
    this.visible = visibility;
  }

  public void stroke() {
    canvasWidget.stroke();
  }

  public void translate(double x, double y) {
    canvasWidget.translate(x, y);
  }

  public void onAttach() {
    RootPanel.get(layerContainer.getId()).add(canvasWidget);
  }

  private class VmlRadialGradient implements RadialGradient {

    CanvasGradient grad;

    private final double x0;

    private final double y0;

    private final double r0;

    private final double x1;

    private final double y1;

    private final double r1;

    public VmlRadialGradient(double x0, double y0, double r0, double x1,
        double y1, double r1) {
      this.x0 = x0;
      this.y0 = y0;
      this.r0 = r0;
      this.x1 = x1;
      this.y1 = y1;
      this.r1 = r1;
      grad = canvasWidget
          .createRadialGradient(x0, y0, r0, x1, y1, r1);
    }

    public void addColorStop(double position, String color) {
      grad.addColorStop(position, new Color(color));
    }

    public CanvasGradient getNative() {
      return grad;
    }
  }

  private class VmlLinearGradient implements LinearGradient {

    CanvasGradient grad;

    private final double startx;

    private final double starty;

    private final double endx;

    private final double endy;

    public VmlLinearGradient(double startx, double starty, double endx,
        double endy) {
      this.startx = startx;
      this.starty = starty;
      this.endx = endx;
      this.endy = endy;
      grad = canvasWidget
          .createLinearGradient(startx, starty, endx, endy);
    }

    public void addColorStop(double position, String color) {
      grad.addColorStop(position, new Color(color));
    }

    public CanvasGradient getNative() {
      return grad;
    }
  }
}
