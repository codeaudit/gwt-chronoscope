package org.timepedia.chronoscope.client.browser.event;

import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.overlays.Draggable;
import org.timepedia.chronoscope.client.render.OverviewAxisPanel;

import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

/**
 * @author Chad Takahashi
 */
public abstract class AbstractEventHandler<T extends EventHandler> {

  // protected static final int KEY_TAB = 9;

  // May get reassigned based on browser type
  protected int tabKeyEventCode = Event.ONKEYDOWN;

  public ChartState getChartState(DomEvent event) {
    return ChartState.getInstance();
  }

  public int getLocalX(DomEvent event) {
    return getChartState(event).getLocalX();
  }

  public int getLocalY(DomEvent event) {
    return getChartState(event).getLocalY();
  }

  /**
   * Handles TAB key and Shift-TAB key presses.
   */
  protected boolean handleTabKey(Event event, ChartState chartInfo, int keyCode,
      boolean isShiftKeyDown) {
    if (DOM.eventGetType(event) != chartInfo.getTabKeyEventCode()) {
      return false;
    }

    if (keyCode == KeyCodes.KEY_TAB) {
      if (isShiftKeyDown) {
        chartInfo.chart.prevFocus();
      } else {
        chartInfo.chart.nextFocus();
      }
      return true;
    }

    return false;
  }

  /**
   * Returns the component that the specified (x,y) chart coordinate is on. If
   * the (x,y) point does not fall on a recognized component, then null is
   * returned.
   */
  protected Object getComponent(int x, int y, XYPlot plot) {
    Bounds plotBounds = plot.getBounds();

    // First check if (x,y) hit the center plot
    if ((null != plotBounds) && (plotBounds.inside(x, y))) {
      Overlay o = plot.getOverlayAt(x, y);
      if (o != null && (o instanceof Draggable) && ((Draggable)o).isDraggable()) {
        return o;
      }
      return plot;
    }

    //
    // Now check if (x,y) hit the overview axis
    //

    OverviewAxisPanel oaPanel = plot.getOverviewAxisPanel();
    if (oaPanel != null && oaPanel.getLayer() != null) {
      Bounds layerBounds = oaPanel.getLayer().getBounds();
      Bounds oaPanelBounds = oaPanel.getBounds();
      double viewOffsetX = layerBounds.x + oaPanel.getLayerOffsetX();
      double viewOffsetY = layerBounds.y + oaPanel.getLayerOffsetY();
      Bounds oaPanelAbsBounds = new Bounds(viewOffsetX, viewOffsetY,
          oaPanelBounds.width, oaPanelBounds.height);
      if (oaPanelAbsBounds.inside(x, y)) {
        return oaPanel.getValueAxis();
      }
    }

    return null;
  }

  private static void log(Object msg) {
    System.out.println("AbstractEventHandler> " + msg);
  }
}
