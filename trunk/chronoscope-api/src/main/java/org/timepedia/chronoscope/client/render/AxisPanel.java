package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.ValueAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Export;

/**
 * Base class for all axis panels, which are panels that surround the center plot panel.
 * 1 or more axis panels can be added to a {@link CompositeAxisPanel}.
 */
@ExportPackage("chronoscope")
public abstract class AxisPanel extends AbstractPanel implements GssElement, Exportable {
  // if true, only render gridlines into the plots, render nothing else.
  public static final boolean GRID_ONLY = false;
  
  protected GssProperties labelProperties;
  protected ValueAxis valueAxis;
  protected XYPlot<?> plot;
  protected View view;

  public void dispose() {
    super.dispose();
    plot = null;
    view = null;
    valueAxis = null;
    labelProperties = null;
  }

  // reset re-uses layers, dispose disposes layers
  public void reset() {
    super.reset();
    log("reset");
    this.plot = null;
    this.view = null;
    this.valueAxis = null;
    this.labelProperties = null;
  }

  public void remove (Panel panel) {
    return; // no sub panels
  }

  public final GssElement getParentGssElement() {
    return (GssElement)this.parent;
  }

  @Export
  public final ValueAxis getValueAxis() {
    return this.valueAxis;
  }
  
  public void setBounds(Bounds b) {
    log("setBounds "+getType() + " " +b);
    this.bounds = new Bounds(b);
    if (null != layer) {
      layer.save();

      if (!b.equals(layer.getBounds())
       && null != parent && !layer.equals(parent.getLayer())) {

        layer.setBounds(b);
        log(layer.getLayerId() + " " + layer.getBounds());
      }

      layer.restore();
    }
  }

  public void setWidth(double width) {
    this.bounds.width = width;
    if (null != layer) {
      layer.save();

      if(!bounds.equals(layer.getBounds())) {
        Bounds lb = layer.getBounds();
        log(layer.getLayerId() + " was:" + this.layer.getBounds());
        layer.setBounds(new Bounds(lb.x, lb.y, width, lb.height));
        log(layer.getLayerId() + " now:" + this.layer.getBounds());
      }

      layer.restore();
    }
  }

  
  public final void setPlot(XYPlot<?> plot) {
    this.plot = plot;
  }
  
  public final void setValueAxis(ValueAxis valueAxis) {
    this.valueAxis = valueAxis;
  }
  
  public final void setView(View view) {
    this.view = view;
  }
  
  /**
   * Draws this axis within the specified axisBounds, as well as drawing grid-lines 
   * on the given {@link XYPlot}.
   */
  public abstract void draw();

  public final void init() {
    ArgChecker.isNotNull(view, "view");
    ArgChecker.isNotNull(plot, "plot");

    // CompositeAxisPanel parentAxisPanel = (CompositeAxisPanel)this.parent;
    // FIXME
    gssProperties = view.getGssProperties(this, "");
    labelProperties = view.getGssProperties(new GssElementImpl("label", this), "");
    // textLayerName =  parentAxisPanel.getName(); //  + parentAxisPanel.indexOf(this);
    initHook();
  }

  public abstract void layout();
  
  /**
   * Subclasses may provide additional initialization steps.
   */
  protected abstract void initHook();

  private static void log(String msg){
    System.out.println("AxisPanel> "+msg);
  }
}
