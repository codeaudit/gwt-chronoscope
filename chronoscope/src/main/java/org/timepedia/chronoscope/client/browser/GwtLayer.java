package org.timepedia.chronoscope.client.browser;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import org.timepedia.chronoscope.client.canvas.AbstractLayer;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.canvas.Layer;

/**
 * Shared GWT DOM manipulation for DOM based Layer implementations
 */
public abstract class GwtLayer extends AbstractLayer implements Layer {
  protected Element layerDivElement;

  public GwtLayer() { }

  public GwtLayer(Canvas canvas) {
     super(canvas);
  }

  protected static final String[] compositeModes = {"copy", "source-atop",
      "source-in", "source-out", "source-over", "destination-atop",
      "destination-in", "destination-out", "destination-over", "darker",
      "lighter", "xor"};

  protected static final String[] TEXT_ALIGN = {
      "start", "end", "left", "right", "center"};

  protected static final String[] TEXT_BASELINE = {
      "top", "hanging", "middle", "alphabetic", "ideographic", "bottom"};

    protected void createLayerDiv(String divElementId, Bounds bounds) {
    layerDivElement = DOM.createElement("div");

    // layerDivElement.addClassName("chrono-layer");
    DOM.setElementAttribute(layerDivElement, "id", divElementId);
    GwtView.initDivElement(layerDivElement, bounds);
    GwtView.positionDivElement(layerDivElement, bounds);

      // return layerDivElement;
    }


    protected void initCanvasElement(Element can) {
    DOM.setStyleAttribute(can, "position", "absolute");
    DOM.setStyleAttribute(can, "top", "0px");
    DOM.setStyleAttribute(can, "left", "0px");
    // DOM.setStyleAttribute(can, "top", (int) bounds.y + "px");
    // DOM.setStyleAttribute(can, "left", (int) bounds.x + "px");
  }
}
