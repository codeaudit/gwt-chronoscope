package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Focus;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Layer;

/**
 * Implements a XYPlotRenderer that uses multiresolution datasets to minimize
 * the amount of XYRenderer invocations.
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class ScalableXYPlotRenderer extends XYPlotRenderer {

  public ScalableXYPlotRenderer(XYPlot plot) {
    super(plot);
  }

  public void drawDataset(Layer layer, XYDataset dataSet, XYRenderer renderer,
      int seriesNum, XYPlot plot) {

    Focus focus = plot.getFocus();
    int focusSeries, focusPoint;
    if (focus == null) {
      focusSeries = -1;
      focusPoint = -1;
    } else {
      focusSeries = focus.getDatasetIndex();
      focusPoint = focus.getPointIndex();
    }

    boolean disabled = focusSeries != -1 && focusSeries != seriesNum;

    renderer.beginCurve(plot, layer, false, disabled);

    int domainStart = this.domainStart[seriesNum];
    int domainEnd = this.domainEnd[seriesNum];
    int mipLevel = plot.getCurrentDatasetLevel(seriesNum);
    int numSamples = dataSet.getNumSamples(mipLevel);
    
    final int inc = 1;
    int[] hoverPoints = plot.getHoverPoints();
    
    // Render the data curve
    int end = Math.min(domainEnd + 1, numSamples);
    for (int i = Math.max(0, domainStart - 1); i < end; i += inc) {
      double x = dataSet.getX(i, mipLevel);
      double y = dataSet.getY(i, mipLevel);
      boolean focused = focusSeries == seriesNum && focusPoint == i;
      boolean hovered = hoverPoints[seriesNum] == i;
      renderer.drawCurvePart(plot, layer, x, y, seriesNum, focused, hovered,
          false, disabled);
    }
    renderer.endCurve(plot, layer, false, disabled, seriesNum);

    // Render hover and focus points on the curve
    renderer.beginPoints(plot, layer, false, disabled);
    end = Math.min(domainEnd + 1, numSamples);
    for (int i = Math.max(0, domainStart - 2); i < end; i += inc) {
      double x = dataSet.getX(i, mipLevel);
      double y = dataSet.getY(i, mipLevel);
      boolean focused = focusSeries == seriesNum && focusPoint == i;
      boolean hovered = hoverPoints[seriesNum] == i;
      renderer.drawPoint(plot, layer, x, y, seriesNum, focused, hovered, false,
          disabled);
    }
    renderer.endPoints(plot, layer, false, disabled, seriesNum);
  }
}
