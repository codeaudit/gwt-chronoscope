package org.timepedia.chronoscope.client;

/**
 * Implement this interface to track state changes in a Plot
 */
public interface XYPlotListener {

  public int DRAGGED = 0, PAGED = 1, ZOOMED = 2, CENTERED = 3;

  /**
   * Called when the user triggers the context menu (typically right-click)
   */
  public void onContextMenu(int x, int y);

  /**
   * Called when the user clicks a point, or hits TAB/shift-TAB or otherwise
   * moves the focus
   *
   * @param plot        the plot for which the focus point changed
   * @param focusSeries the dataset number which changed
   * @param focusPoint  the point index within the dataset which changed
   */
  public void onFocusPointChanged(XYPlot plot, int focusSeries, int focusPoint);

  /**
   * Called when the visible plot region is moved
   *
   * @param plot the plot which changed
   * @param amt  the amount by which it was dragged, paged, zoomed
   * @param type the type of movement, DRAGGED, PAGED, ZOOMED, CENTERED
   */
  public void onPlotMoved(XYPlot plot, double amt, int seriesNum, int type,
      boolean animated);
}
