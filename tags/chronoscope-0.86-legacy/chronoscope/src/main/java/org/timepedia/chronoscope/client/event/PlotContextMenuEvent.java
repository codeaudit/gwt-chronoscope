package org.timepedia.chronoscope.client.event;

import org.timepedia.chronoscope.client.XYPlot;

/**
 * Fired by plot implementations when the context menu is trigged.
 */
public class PlotContextMenuEvent extends PlotEvent {

  public static Type<PlotContextMenuEvent, PlotContextMenuHandler> TYPE
      = new Type<PlotContextMenuEvent, PlotContextMenuHandler>() {
    @Override
    protected void fire(PlotContextMenuHandler contextMenuHandler,
        PlotContextMenuEvent event) {
      contextMenuHandler.onContextMenu(event);
    }
  };

  private int clickX;

  private int clickY;

  public PlotContextMenuEvent(XYPlot plot, int clickX, int clickY) {
    super(plot);
    this.clickX = clickX;
    this.clickY = clickY;
  }

  public int getClickX() {
    return clickX;
  }

  public int getClickY() {
    return clickY;
  }

  protected Type getType() {
    return TYPE;
  }
}