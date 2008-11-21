package org.timepedia.chronoscope.client.plot;

import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.util.ArgChecker;

/**
 * Represents one of the auxiliary axis panels located to the 
 * North/South/East/West of the main plot panel.
 * 
 * @author chad takahashi
 */
abstract class AncillaryPanel {
  protected DefaultXYPlot plot;
  protected View view;
  protected Layer layer;
  protected boolean isEnabled = true;
  
  protected abstract void initHook();
  
  /**
   * Draws this panel
   */
  public abstract void draw();
  
  public abstract void layout();
  
  public final void init() {
    ArgChecker.isNotNull(plot, "plot");
    ArgChecker.isNotNull(view, "view");
    initHook();
  }
  
  public void setPlot(DefaultXYPlot plot) {
    this.plot = plot;
  }
  
  public void setView(View view) {
    this.view = view;
  }
  
}
