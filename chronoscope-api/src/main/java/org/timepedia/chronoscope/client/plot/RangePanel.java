package org.timepedia.chronoscope.client.plot;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.render.CompositeAxisPanel;
import org.timepedia.chronoscope.client.render.Panel;
import org.timepedia.chronoscope.client.render.RangeAxisPanel;
import org.timepedia.chronoscope.client.util.ArgChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents the vertical range axes to the left and right of the center plot
 * panel.  Both the left and right panel are drawn onto the same {@link Layer}.
 *
 * @author chad takahashi
 */
final public class RangePanel extends AuxiliaryPanel {

  private boolean isDrawn = false;

  private CompositeAxisPanel leftPanel, rightPanel;

  // private Layer layer;

  private Bounds leftPanelBounds, rightPanelBounds;

  // Maps axisId->RangeAxis entries and keeps them around (conditionally,
  // based on the value of 'createNewAxesOnInit') between calls to initHook().
  private Map<String, RangeAxis> id2rangeAxis = new HashMap<String, RangeAxis>();

  private boolean createNewAxesOnInit = true;

  // Maps a dataset id to the RangeAxis to which it has been bound.
  // E.g. rangeAxes[2] returns the RangeAxis that datasets.get(2) is 
  // bound to.  The relationship from dataset to axis is 
  // many-to-one.
  private RangeAxis[] rangeAxes;

  public RangePanel() {
    // bounds = new Bounds(0, 0, 30, 10); // default bounds
  }

  public void dispose() {
    super.dispose();

    // if (null != layer) { layer.dispose(); }
    // layer = null;

    // bounds = null;
    rangeAxes = null;
    id2rangeAxis.clear();

    if (null != leftPanel) { leftPanel.dispose(); }
    if (null != rightPanel) { rightPanel.dispose(); }
  }

  public void remove(Panel panel) {
    if (null != panel) {
      if (panel.equals(rightPanel)) { rightPanel = null; } else
      if (panel.equals(leftPanel)) {leftPanel = null; }
    }
  }

  public void bindDatasetsToRangeAxes() {
    rangeAxes = bindDatasetsToRangeAxes(plot.getDatasets());
    clearDrawCaches();
  }

  @Override
  public void clearDrawCaches() {
    isDrawn = false;
  }

  public Bounds getBounds() {
    log("getBounds left:"+leftPanel.getBounds());
    if (null != leftPanel.getLayer()) {
        log("getBounds "+leftPanel.getLayer().getLayerId()+" "+leftPanel.getLayer().getBounds());
    }
    log("getBounds right:"+rightPanel.getBounds());
    if (null != rightPanel.getLayer()) {
        log("getBounds "+rightPanel.getLayer().getLayerId()+" "+rightPanel.getLayer().getBounds());
    }

    Bounds bounding = new Bounds(leftPanel.getBounds(), rightPanel.getBounds());
    log("getBounds bounding bounds:"+bounding);
    return bounding;
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
    log("RANGEPANEL getLayer shouldn't be called");
    return (Layer)null;
    // return layer;
  }

  public double getLayerOffsetX() {
    return 0;
    // return layer.getBounds().x;
  }

  public double getLayerOffsetY() {
    return 0;
    // return layer.getBounds().y;
  }

  public Panel getParent() {
    return null;
  }

  public CompositeAxisPanel getLeftSubPanel() {
    return leftPanel;
  }

  public RangeAxis[] getRangeAxes() {
    return rangeAxes == null ? new RangeAxis[]{}: rangeAxes;
  }

  public String[] getRangeAxisIds() {
    return rangeAxes == null ? new String[]{}: id2rangeAxis.keySet().toArray(new String[id2rangeAxis.keySet().size()]);
  }

  public RangeAxis getRangeAxis(String rangeAxisId) {
     return id2rangeAxis.get(rangeAxisId);
  }

  public CompositeAxisPanel getRightSubPanel() {
    return rightPanel;
  }

//  private void setLayer(Layer layer) {
//    if (null == layer) { return; } else
//    if (layer.equals(this.layer)) { return; } else
//    if (this.layer != null) {
//      this.layer.dispose();
//    }
//    log("rangePanel setLayer "+layer.getLayerId() + " bounds:" + layer.getBounds());
//
//    this.layer = layer;
//      if (null != leftPanel) { leftPanel.setLayer(layer); }
//      if (null != rightPanel) { rightPanel.setLayer(layer); }
//  }

  public void initLeftLayer() {
    log ("initLeftLayer leftPanelBounds:"+leftPanelBounds);
    if (null == leftPanelBounds) {
      leftPanelBounds = new Bounds(0,0,49,view.getHeight()*0.75);
    }
    leftPanel.setLayer(view.getCanvas().createLayer(Layer.RANGE_AXIS_LEFT, leftPanelBounds));
  }

  public void initRightLayer() {
    log ("initRightLayer rightPanelBounds:"+rightPanelBounds);
    if (null == rightPanelBounds) {
      rightPanelBounds = new Bounds(0,0,49,view.getHeight()*0.75);
    }

    rightPanel.setLayer(view.getCanvas().createLayer(Layer.RANGE_AXIS_RIGHT, rightPanelBounds));
  }

  @Override
  public void layout() {
    if (leftPanel == null) { initLeft(); } else
    if (leftPanel.getLayer() == null) { initLeftLayer(); }
    if (rightPanel == null) { initRight(); } else
    if (rightPanel.getLayer() == null) { initRightLayer(); }

    leftPanel.layout();
    rightPanel.layout();
  }

  public void setCreateNewAxesOnInit(boolean createNewAxesOnInit) {
    this.createNewAxesOnInit = createNewAxesOnInit;
  }

  public void setY(double y) {
    ArgChecker.isNonNegative(y, "Y");
    log("setY "+y);
    if (null != leftPanel) {
      leftPanel.setPosition(leftPanel.getBounds().x, y);
    }
    if (null != rightPanel) {
      rightPanel.setPosition(rightPanel.getBounds().x, y);
    }
    layout();
  }
  public void setHeight(double height) {
    ArgChecker.isNonNegative(height, "height");
    log("setHeight "+height);
    if (null != leftPanel) {
      leftPanel.setHeight(height);
    }
    if (null != rightPanel) {
      rightPanel.setHeight(height);
    }
    layout();
  }

  public void setLayerOffset(double x, double y) {
    throw new UnsupportedOperationException();
  }

  public void setLeftPanelBounds(Bounds leftPanelBounds) {
    this.leftPanelBounds = leftPanelBounds;
    if (null != leftPanel) { leftPanel.setBounds(leftPanelBounds); }
  }

  public void setRightPanelBounds(Bounds rightPanelBounds) {
    this.rightPanelBounds = rightPanelBounds;
    if (null != rightPanel) { rightPanel.setBounds(rightPanelBounds); }
  }

  public final void setPosition(double x, double y) {
    log("setPosition "+x+", "+y);
    if (leftPanel != null && leftPanel.getLayer() == null) {
      leftPanel.setPosition(x, y);
      initLeftLayer();
    }
    if (rightPanel != null && rightPanel.getLayer() == null) {
      rightPanel.setPosition(view.getWidth()-rightPanel.getBounds().width,y);
      initRightLayer();
    }
  }

  @Override
  protected void drawHook() {
    log("drawHook is draw required? "+isDrawRequired());
    if (!isDrawRequired()) {
      log("no range panel draw req'd");
      return;
    }

    // layer.save();
    // layer.setFillColor(Color.TRANSPARENT);
    // layer.clear();

    for (RangeAxis axis : rangeAxes) {
      axis.calcTickPositions();
    }

    leftPanel.draw();
    rightPanel.draw();

    // layer.restore();

    isDrawn = true;
  }

  private  void initLeft() {
    if (null == leftPanel) {
      // leftPanel = new CompositeAxisPanel(Layer.RANGE_AXIS_LEFT, CompositeAxisPanel.Position.LEFT, plot, view);
      leftPanel = new CompositeAxisPanel(Layer.RANGE_AXIS_LEFT, CompositeAxisPanel.Position.LEFT, plot, view);
    } else {
      leftPanel.reset(Layer.RANGE_AXIS_LEFT, CompositeAxisPanel.Position.LEFT, plot, view);
    }
    if (null != leftPanelBounds) {  leftPanel.setBounds(leftPanelBounds); }
    else { log("initLeft null leftPanelBounds"); }
    if (null == leftPanel.getLayer()) { initLeftLayer(); }
    else { log("initLeft layer:"+leftPanel.getLayer().getLayerId() + " "+leftPanel.getLayer().getBounds()); }
    leftPanel.setParent(this);
  }
  private void initRight() {
    if (null == rightPanel) {
      rightPanel = new CompositeAxisPanel(Layer.RANGE_AXIS_RIGHT,  CompositeAxisPanel.Position.RIGHT, plot, view);
    } else {
      rightPanel.reset(Layer.RANGE_AXIS_RIGHT,  CompositeAxisPanel.Position.RIGHT, plot, view);
    }
    if (null != rightPanelBounds) {  rightPanel.setBounds(rightPanelBounds); }
    else { log("initRight null rightPanelBounds"); }
    if (null == rightPanel.getLayer()) { initRightLayer(); }
    else { log("initRight layer:"+rightPanel.getLayer().getLayerId() + " "+rightPanel.getLayer().getBounds()); }
    rightPanel.setParent(this);
  }

  @Override
  protected void initHook() {
    // initLayer();
    if(!initialized) {
      initLeft();
      initRight();
      rangeAxes = bindDatasetsToRangeAxes(plot.getDatasets());
    } else {
      log("already initialized");
    }
  }

  @Override
  protected void setEnabledHook(boolean enabled) {
    if (enabled) {
      init();
    } else {
      leftPanel.setWidth(1);//dispose();
      rightPanel.setWidth(1);//dispose();
    }
  }

  private RangeAxis[] bindDatasetsToRangeAxes(Datasets datasets) {
    ArgChecker.isNotNull(view, "view");
    ArgChecker.isNotNull(datasets, "datasets");
//    if (createNewAxesOnInit) {
//      id2rangeAxis.clear();
//    }
    List<RangeAxis> rangeAxes = new ArrayList<RangeAxis>();
    if (datasets.isEmpty()) {
      return rangeAxes.toArray(new RangeAxis[0]);
    }

    // Keeps track of unique axisIds *within the scope of this method*
    Set<String> localRangeAxisIds = new HashSet<String>();

    RangeAxisPanel axisPanel = null;

    for (int i = 0; i < datasets.size(); i++) {
      Dataset dataset = datasets.get(i);

      final String rangeAxisId = dataset.getAxisId(0);
      RangeAxis rangeAxis = id2rangeAxis.get(rangeAxisId);

      // Determine if the rangeAxis should be added to the left or right range panel
      int numLeftAxes = leftPanel.getChildCount();
      int numRightAxes = rightPanel.getChildCount();
      boolean useLeftPanel = (numLeftAxes <= numRightAxes);
      CompositeAxisPanel compositePanel = useLeftPanel || !plot.isMultiaxis() ? leftPanel : rightPanel;

      if (rangeAxis == null) {
        rangeAxis = new RangeAxis(dataset.getRangeLabel(), rangeAxisId);
        rangeAxis.setPlot(plot);
        rangeAxis.setView(view);
        rangeAxis.setAxisIndex(i);
        id2rangeAxis.put(rangeAxisId, rangeAxis);
      }

      if (!localRangeAxisIds.contains(rangeAxisId)) {
        if (axisPanel == null || plot.isMultiaxis()) {
          axisPanel = new RangeAxisPanel();
          axisPanel.setValueAxis(rangeAxis);
          compositePanel.add(axisPanel);
        }
        rangeAxis.setAxisPanel(axisPanel);
        double tickLabelHeight = Math.min(axisPanel.getMaxLabelHeight(), 12);
        rangeAxis.setTickLabelHeight(tickLabelHeight);
        localRangeAxisIds.add(rangeAxisId);
        double h = getBounds().height;
        double rangeAxisHeight = h  < 20 ? view.getHeight()*0.75 : h;
        rangeAxisHeight -= rangeAxisHeight > tickLabelHeight*2 ? tickLabelHeight : 0;
        rangeAxis.setRangeAxisHeight(rangeAxisHeight);
      }

      rangeAxis.adjustAbsRange(dataset);
      rangeAxes.add(rangeAxis);
    }

    clearDrawCaches();
    return rangeAxes.toArray(new RangeAxis[rangeAxes.size()]);
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

  private static void log(String msg) {
    System.out.println("RangePanel> " + msg);
  }
}
