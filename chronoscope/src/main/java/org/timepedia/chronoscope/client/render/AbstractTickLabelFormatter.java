package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssProperties;

/**
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public abstract class AbstractTickLabelFormatter implements TickLabelFormatter {

  protected int[] quantizedTickValues = null;

  protected TickLabelFormatter superFormatter, subFormatter;

  private final String dummyTick;

  private double maxLabelWidth = -1;
  
  public AbstractTickLabelFormatter(String s) {
    dummyTick = s;
  }
  
  public double getMaxDimensionDummyTick(Layer layer,
      GssProperties axisProperties) {
    if (maxLabelWidth == -1) {
      maxLabelWidth = tickLabelLength(dummyTick, layer, axisProperties);
    }
    return maxLabelWidth;
  }
  
  public final TickLabelFormatter getSubIntervalFormatter() {
    return subFormatter;
  }
  
  public final TickLabelFormatter getSuperIntervalFormatter() {
    return superFormatter;
  }

  public final int quantizeTicks(double dblTicks) {
    int ticks = (int)dblTicks;
    
    if (ticks >= quantizedTickValues[0]) {
      return ticks;
    }
    
    for (int i = 1; i < quantizedTickValues.length; i++) {
      int quantizedTickValue = quantizedTickValues[i];
      if (ticks >= quantizedTickValue) {
        return quantizedTickValue;
      }
    }
    
    return 1;
  }

  public boolean isRootFormatter() {
    return superFormatter == null;
  }
  
  private double tickLabelLength(String l, Layer layer,
      GssProperties axisProperties) {
    return layer.stringWidth(l, axisProperties.fontFamily,
        axisProperties.fontWeight, axisProperties.fontSize);
  }
}
