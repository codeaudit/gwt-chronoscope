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
public class ScalableXYPlotRenderer<T extends XYPlot>
    extends XYPlotRenderer<T> {

  protected RenderState renderState;

  public ScalableXYPlotRenderer(T plot) {
    super(plot);
    renderState = new RenderState();
  }

  public void drawDataset(int datasetIndex, Layer layer, T plot) {
    XYDataset dataSet = plot.getDatasets().get(datasetIndex);
    XYRenderer renderer = plot.getRenderer(datasetIndex);

    if (dataSet.getNumSamples(0) < 2) {
      return;
    }

    Focus focus = plot.getFocus();
    int focusSeries, focusPoint;
    if (focus == null) {
      focusSeries = -1;
      focusPoint = -1;
    } else {
      focusSeries = focus.getDatasetIndex();
      focusPoint = focus.getPointIndex();
    }

    renderState
        .setDisabled((focusSeries != -1) && (focusSeries != datasetIndex));

    renderer.beginCurve(plot, layer, renderState);

    int domainStart = this.domainStartIdxs[datasetIndex];
    int domainEnd = this.domainEndIdxs[datasetIndex];
    int mipLevel = plot.getCurrentMipLevel(datasetIndex);
    int numSamples = dataSet.getNumSamples(mipLevel);

    final int inc = 1;
    int[] hoverPoints = plot.getHoverPoints();

    // Render the data curve
    int end = Math.min(domainEnd + 1, numSamples);
    for (int i = Math.max(0, domainStart - 1); i < end; i += inc) {
      double x = dataSet.getX(i, mipLevel);
      double y = dataSet.getY(i, mipLevel);
      renderState.setFocused(focusSeries == datasetIndex && focusPoint == i);
      renderState.setHovered(hoverPoints[datasetIndex] == i);
      renderer.drawCurvePart(plot, layer, x, y, datasetIndex, renderState);
    }
    renderer.endCurve(plot, layer, datasetIndex, renderState);

    // Render hover and focus points on the curve
    renderer.beginPoints(plot, layer, renderState);
    end = Math.min(domainEnd + 1, numSamples);
    for (int i = Math.max(0, domainStart - 2); i < end; i += inc) {
      double x = dataSet.getX(i, mipLevel);
      double y = dataSet.getY(i, mipLevel);
      renderState.setFocused(focusSeries == datasetIndex && focusPoint == i);
      renderState.setHovered(hoverPoints[datasetIndex] == i);
      renderer.drawPoint(plot, layer, x, y, datasetIndex, renderState);
    }
    renderer.endPoints(plot, layer, datasetIndex, renderState);
  }
}
