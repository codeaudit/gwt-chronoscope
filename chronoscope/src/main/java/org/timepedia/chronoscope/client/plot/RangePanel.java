package org.timepedia.chronoscope.client.plot;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.render.CompositeAxisPanel;
import org.timepedia.chronoscope.client.render.RangeAxisPanel;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Interval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the vertical range axes to the left and right of the
 * center plot panel.  Both the left and right panel are drawn onto
 * the same {@link Layer}.
 * 
 * @author chad takahashi
 */
final class RangePanel extends AuxiliaryPanel {
  private final Map<String, RangeAxis> id2rangeAxis
      = new HashMap<String, RangeAxis>();
  private boolean isDrawn = false;
  private CompositeAxisPanel leftPanel, rightPanel;
  private Layer layer;
  
  // The gap, in pixels, between the left and right range panels.
  private double centerGapWidth = 0;

  // Maps a dataset id to the RangeAxis to which it has been bound.
  // E.g. rangeAxes[2] returns the RangeAxis that datasets.get(2) is 
  // bound to.  The relationship from dataset to axis is 
  // many-to-one.
  private List<RangeAxis> rangeAxes = new ArrayList<RangeAxis>();

  @Override
  public void clearDrawCaches() {
    isDrawn = false;
  }

  public Bounds getBounds() {
    throw new UnsupportedOperationException();
  }
  
  public CompositeAxisPanel getLeftSubPanel() {
    return leftPanel;
  }

  public List<RangeAxis> getRangeAxes() {
    return this.rangeAxes;
  }
  
  public CompositeAxisPanel getRightSubPanel() {
    return rightPanel;
  }
  
  @Override
  public void layout() {
    leftPanel.layout();
    
    rightPanel.layout();
    Bounds leftPanelBounds = leftPanel.getBounds();
    rightPanel.setPosition(leftPanelBounds.rightX() + centerGapWidth, 
        leftPanelBounds.y);
  }
  
  public void setCenterGapWidth(double width) {
    ArgChecker.isNonNegative(width, "width");
    this.centerGapWidth = width;
  }
  
  public void setHeight(double h) {
    for (RangeAxis rangeAxis : rangeAxes) {
      rangeAxis.getAxisPanel().getBounds().height = h;
    }
    layout();
  }
  
  public final void setPosition(double x, double y) {
    leftPanel.setPosition(x, y);
    
    // This isn't correct.  rightPanel x-position should be
    // plot.bounds.rightX() I think.
    //rightPanel.setPosition(x, y);
    rightPanel.setPosition(leftPanel.getBounds().rightX() + centerGapWidth, y);
  }
  
  @Override
  protected void drawHook() {
    if (!isDrawRequired()) {
      return;
    }
    
    layer.save();
    layer.setFillColor(Color.TRANSPARENT);
    layer.clear();
    
    leftPanel.draw();
    rightPanel.draw();
    
    layer.restore();
    
    isDrawn = true;
  }

  boolean alreadyInitialized = false;
  @Override
  protected void initHook() {
    id2rangeAxis.clear();
    
    // assumes that leftPanel and rightPanel share the same height.
    Bounds layerBounds = new Bounds(0, 0, view.getWidth(), view.getHeight());
    
    layer = plot.initLayer(layer, "verticalAxis", layerBounds);
    layer.setLayerOrder(Layer.Z_LAYER_AXIS);
    layer.setFillColor(Color.TRANSPARENT);
    layer.clear();
  
    final String leftPanelName = "rangeAxisLayerLeft" + plot.plotNumber;
    leftPanel = new CompositeAxisPanel(leftPanelName,
        CompositeAxisPanel.Position.LEFT, plot, view);
    leftPanel.setLayer(layer);
    
    final String rightPanelName = "rangeAxisLayerRight" + plot.plotNumber;
    rightPanel = new CompositeAxisPanel(rightPanelName,
        CompositeAxisPanel.Position.RIGHT, plot, view);
    rightPanel.setLayer(layer);
    
    rangeAxes = autoAssignDatasetAxes(plot.getDatasets());
  
    alreadyInitialized = true;
}
  
  @Override
  protected void setEnabledHook(boolean enabled) {
    if (enabled) {
      init();
    }
    else {
      leftPanel.clear();
      rightPanel.clear();
    }
  }

  private List<RangeAxis> autoAssignDatasetAxes(Datasets datasets) {
    ArgChecker.isNotNull(view, "view");
    ArgChecker.isNotNull(datasets, "datasets");

    List<RangeAxis> rangeAxes = new ArrayList<RangeAxis>();

    for (int i = 0; i < datasets.size(); i++) {
      Dataset ds = datasets.get(i);

      RangeAxis ra = id2rangeAxis.get(ds.getAxisId(0));

      if (ra == null) {
        int numLeftAxes = leftPanel.getAxisCount();
        int numRightAxes = rightPanel.getAxisCount();
        boolean useLeftPanel = (numLeftAxes <= numRightAxes);
        CompositeAxisPanel currRangePanel = useLeftPanel ? leftPanel
            : rightPanel;
        
        Interval rangeAxisInterval = ds.getPreferredRangeAxisInterval();
        if (rangeAxisInterval == null) {
          rangeAxisInterval = ds.getRangeExtrema(0);
        }
        
        ra = new RangeAxis(plot, view, ds.getRangeLabel(), ds.getAxisId(0), i,
            rangeAxisInterval);
        RangeAxisPanel axisPanel = new RangeAxisPanel();
        axisPanel.setValueAxis(ra);
        ra.setAxisPanel(axisPanel);
        currRangePanel.add(axisPanel);
        id2rangeAxis.put(ra.getAxisId(), ra);
      } else {
        Interval yInterval = ds.getRangeExtrema(0);
        ra.setInitialRange(
            Math.min(ra.getUnadjustedRangeLow(), yInterval.getStart()),
            Math.max(ra.getUnadjustedRangeHigh(), yInterval.getEnd()));
      }

      rangeAxes.add(ra);
    }

    return rangeAxes;
  }
  
  private boolean isDrawRequired() {
    if (!isDrawn) {
      return true;
    }
    
    for (RangeAxis axis : rangeAxes) {
      if (axis.isAutoZoomVisibleRange()) {
        return true;
      }
    }
    
    return false;
  }
}
