package org.timepedia.chronoscope.client.plot;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.render.CompositeAxisPanel;
import org.timepedia.chronoscope.client.render.Panel;
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
  private boolean isDrawn = false;
  private CompositeAxisPanel leftPanel, rightPanel;
  private Layer layer;
  private Bounds myBounds;
  
  // Maps a dataset id to the RangeAxis to which it has been bound.
  // E.g. rangeAxes[2] returns the RangeAxis that datasets.get(2) is 
  // bound to.  The relationship from dataset to axis is 
  // many-to-one.
  private RangeAxis[] rangeAxes;
  
  public RangePanel() {
    myBounds = new Bounds(0, 0, 30, 10); // default bounds
  }
  
  public void bindDatasetsToRangeAxes() {
    rangeAxes = bindDatasetsToRangeAxes(this.plot.getDatasets());
    clearDrawCaches();
  }
  
  @Override
  public void clearDrawCaches() {
    isDrawn = false;
  }

  public Bounds getBounds() {
    return myBounds;
  }
  
  public int getChildCount() {
    return getChildren().size();
  }
  
  public List<Panel> getChildren() {
    List<Panel> l = new ArrayList<Panel>();
    l.add(leftPanel);
    l.add(rightPanel);
    return l;
  }
  
  public Layer getLayer() {
    return this.layer;
  }
  
  public double getLayerOffsetX() {
    return 0;
    //return this.layer.getBounds().x;
  }

  public double getLayerOffsetY() {
    return 0;
    //return this.layer.getBounds().y;
  }

  public Panel getParent() {
    return null;
  }

  public CompositeAxisPanel getLeftSubPanel() {
    return leftPanel;
  }

  public RangeAxis[] getRangeAxes() {
    return this.rangeAxes;
  }
  
  public CompositeAxisPanel getRightSubPanel() {
    return rightPanel;
  }
  
  public void initLayer() {
    // assumes that leftPanel and rightPanel share the same height.
    Bounds layerBounds = new Bounds(myBounds);
    
    layer = plot.initLayer(layer, "verticalAxis", layerBounds);
    layer.setLayerOrder(Layer.Z_LAYER_AXIS);
    layer.setFillColor(Color.TRANSPARENT);
    layer.clear();
  
    leftPanel.setLayer(layer);
    rightPanel.setLayer(layer);
  }

  @Override
  public void layout() {
    if (layer == null) {
      initLayer();
    }
    
    leftPanel.layout();
    rightPanel.layout();
    
    leftPanel.setPosition(0, 0);
    rightPanel.setPosition(myBounds.width - rightPanel.getBounds().width, 0);
  }
  
  public void setWidth(double width) {
    ArgChecker.isNonNegative(width, "width");
    this.myBounds.width = width;
    this.layout();
  }
  
  public void setHeight(double height) {
    ArgChecker.isNonNegative(height, "height");
    this.myBounds.height = height;
    
    for (RangeAxis rangeAxis : rangeAxes) {
      rangeAxis.getAxisPanel().getBounds().height = height;
    }
    layout();
  }
  
  public void setLayerOffset(double x, double y) {
    throw new UnsupportedOperationException();
  }

  public final void setPosition(double x, double y) {
    boolean positionChanged = !(x == myBounds.x && y == myBounds.y);
    
    if (positionChanged) {
      myBounds.x = x;
      myBounds.y = y;
    }
    
    if (layer == null || positionChanged) {
      initLayer();
    }
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

  @Override
  protected void initHook() {
    final String leftPanelName = "rangeAxisLayerLeft" + plot.plotNumber;
    leftPanel = new CompositeAxisPanel(leftPanelName,
        CompositeAxisPanel.Position.LEFT, plot, view);
    leftPanel.setParent(this);
    leftPanel.setStringSizer(stringSizer);
    
    final String rightPanelName = "rangeAxisLayerRight" + plot.plotNumber;
    rightPanel = new CompositeAxisPanel(rightPanelName,
        CompositeAxisPanel.Position.RIGHT, plot, view);
    rightPanel.setParent(this);
    rightPanel.setStringSizer(stringSizer);
    
    rangeAxes = bindDatasetsToRangeAxes(plot.getDatasets());
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

  private RangeAxis[] bindDatasetsToRangeAxes(Datasets datasets) {
    ArgChecker.isNotNull(view, "view");
    ArgChecker.isNotNull(datasets, "datasets");

    Map<String, RangeAxis> id2rangeAxis = new HashMap<String, RangeAxis>();
    List<RangeAxis> rangeAxes = new ArrayList<RangeAxis>();
    
    leftPanel.clear();
    rightPanel.clear();
    
    for (int i = 0; i < datasets.size(); i++) {
      Dataset dataset = datasets.get(i);

      RangeAxis rangeAxis = id2rangeAxis.get(dataset.getAxisId(0));
      
      if (rangeAxis != null) {
        rangeAxis.adjustAbsRange(dataset);
      } else {
        int numLeftAxes = leftPanel.getChildCount();
        int numRightAxes = rightPanel.getChildCount();
        boolean useLeftPanel = (numLeftAxes <= numRightAxes);
        CompositeAxisPanel currRangePanel = 
            useLeftPanel ? leftPanel : rightPanel;
        
        rangeAxis = new RangeAxis(plot, view, dataset, i);
        RangeAxisPanel axisPanel = new RangeAxisPanel();
        axisPanel.setValueAxis(rangeAxis);
        rangeAxis.setAxisPanel(axisPanel);
        currRangePanel.add(axisPanel);
        id2rangeAxis.put(rangeAxis.getAxisId(), rangeAxis);
      }

      rangeAxes.add(rangeAxis);
    }

    return (RangeAxis[])rangeAxes.toArray(new RangeAxis[0]);
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

  
  private static void log(Object msg) {
    System.out.println("RangePanel> " + msg);
  }
}
