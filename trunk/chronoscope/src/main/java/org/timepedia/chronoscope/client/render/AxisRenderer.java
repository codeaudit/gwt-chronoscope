package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.axis.AxisPanel;
import org.timepedia.chronoscope.client.axis.ValueAxis;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.util.ArgChecker;

/**
 * Provides skeletal implementation for all sub-renderers.
 */
public abstract class AxisRenderer implements GssElement {

  protected ValueAxis valueAxis;

  protected GssProperties axisProperties, labelProperties;
  
  protected String textLayerName;

  protected View view;
  
  protected boolean isInitialized = false;
  
  public final GssElement getParentGssElement() {
    return valueAxis.getAxisPanel();
  }

  public final void setValueAxis(ValueAxis valueAxis) {
    this.valueAxis = valueAxis;
  }
  
  public final void setView(View view) {
    this.view = view;
  }
  
  public final void init() {
    ArgChecker.isNotNull(valueAxis, "valueAxis");
    ArgChecker.isNotNull(view, "view");
    
    AxisPanel axisPanel = valueAxis.getAxisPanel();
    axisProperties = view.getGssProperties(this, "");
    labelProperties = view
        .getGssProperties(new GssElementImpl("label", this), "");
    textLayerName = axisPanel.getPanelName() + axisPanel.getAxisNumber(valueAxis);    
    initHook();
    isInitialized = true;
  }
  
  /**
   * Subclasses may provide additional initialization steps.
   */
  protected abstract void initHook();
  
}
