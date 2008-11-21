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
  

  // Maps a dataset id to the RangeAxis to which it has been bound.
  // E.g. rangeAxes[2] returns the RangeAxis that datasets.get(2) is 
  // bound to.  The relationship from dataset to axis is 
  // many-to-one.
  private List<RangeAxis> rangeAxes = new ArrayList<RangeAxis>();

  @Override
  public void clearDrawCaches() {
    isDrawn = false;
  }

  @Override
  public void draw() {
    if (!isDrawRequired()) {
      return;
    }
    
    layer.save();
    layer.setFillColor(Color.TRANSPARENT);
    layer.clear();
    
    Bounds leftPanelBounds = new Bounds(0, 0, leftPanel.getWidth(), 
        leftPanel.getHeight());
    leftPanel.draw(layer, leftPanelBounds);

    if (rightPanel.getAxisCount() > 0) {
      Bounds rightPanelBounds = new Bounds(plot.getBounds().rightX(), 0, 
          rightPanel.getWidth(), rightPanel.getHeight());
      rightPanel.draw(layer, rightPanelBounds);
    }
    
    layer.restore();
    
    isDrawn = true;
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
  public void initLayer() {
    // assumes that leftPanel and rightPanel share the same height.
    Bounds layerBounds = new Bounds(0, plot.getBounds().y,
        view.getWidth(), leftPanel.getHeight());
    
    layer = plot.initLayer(layer, "verticalAxis", layerBounds);
    layer.setLayerOrder(Layer.Z_LAYER_AXIS);
    layer.setFillColor(Color.TRANSPARENT);
    layer.clear();
  }
  
  @Override
  public void layout() {
    leftPanel.layout();
    rightPanel.layout();
  }

  @Override
  protected void initHook() {
    final String leftPanelName = "rangeAxisLayerLeft" + plot.plotNumber; 
    leftPanel = new CompositeAxisPanel(leftPanelName,
        CompositeAxisPanel.Position.LEFT, plot, view);
    
    final String rightPanelName = "rangeAxisLayerRight" + plot.plotNumber; 
    rightPanel = new CompositeAxisPanel(rightPanelName,
        CompositeAxisPanel.Position.RIGHT, plot, view);
    
    rangeAxes = autoAssignDatasetAxes(plot.getDatasets());
  }
  
  @Override
  protected void setEnabledHook(boolean enabled) {
    throw new UnsupportedOperationException("not implemented yet...");
  }

  private List<RangeAxis> autoAssignDatasetAxes(Datasets datasets) {
    ArgChecker.isNotNull(view, "view");
    ArgChecker.isNotNull(datasets, "datasets");

    List<RangeAxis> rangeAxes = new ArrayList<RangeAxis>();

    for (int i = 0; i < datasets.size(); i++) {
      Dataset ds = datasets.get(i);

      RangeAxis ra = id2rangeAxis.get(ds.getAxisId());

      if (ra == null) {
        int numLeftAxes = leftPanel.getAxisCount();
        int numRightAxes = rightPanel.getAxisCount();
        boolean useLeftPanel = (numLeftAxes <= numRightAxes);
        CompositeAxisPanel currRangePanel = useLeftPanel ? leftPanel
            : rightPanel;
        ra = new RangeAxis(plot, view, ds.getRangeLabel(), ds.getAxisId(), i,
            ds.getMinValue(1), ds.getMaxValue(1));
        RangeAxisPanel axisPanel = new RangeAxisPanel();
        axisPanel.setValueAxis(ra);
        ra.setAxisRenderer(axisPanel);
        currRangePanel.add(axisPanel);
        id2rangeAxis.put(ra.getAxisId(), ra);
      } else {
        ra.setInitialRange(
            Math.min(ra.getUnadjustedRangeLow(), ds.getMinValue(1)),
            Math.max(ra.getUnadjustedRangeHigh(), ds.getMaxValue(1)));
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
