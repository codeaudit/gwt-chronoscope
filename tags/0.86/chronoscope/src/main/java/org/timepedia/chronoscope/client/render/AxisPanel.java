package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.ValueAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.util.ArgChecker;

/**
 * Base class for all axis panels, which are panels that surround the center plot panel.
 * 1 or more axis panels can be added to a {@link CompositeAxisPanel}.
 */
public abstract class AxisPanel extends AbstractPanel implements GssElement {

  // if true, only render gridlines into the plots, render nothing else.
  public static final boolean GRID_ONLY = false;
  
  protected GssProperties labelProperties;
  
  protected XYPlot plot;
  
  protected ValueAxis valueAxis;

  protected View view;
  
  public final GssElement getParentGssElement() {
    return (CompositeAxisPanel)this.parent;
  }
  
  public final ValueAxis getValueAxis() {
    return this.valueAxis;
  }
  
  public void setBounds(Bounds b) {
    this.bounds = new Bounds(b);
  }
  
  public final void setPlot(XYPlot plot) {
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
    
    CompositeAxisPanel parentAxisPanel = (CompositeAxisPanel)this.parent;
    
    gssProperties = view.getGssProperties(this, "");
    labelProperties = view.getGssProperties(new GssElementImpl("label", this), "");
    textLayerName = parentAxisPanel.getName() + parentAxisPanel.indexOf(this);    
    initHook();
  }
  
  public abstract void layout();
  
  /**
   * Subclasses may provide additional initialization steps.
   */
  protected abstract void initHook();
  
}
