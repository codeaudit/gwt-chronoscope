package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Focus;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.data.tuple.BasicTuple2D;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;

/**
 * Implements a XYPlotRenderer that uses multiresolution datasets to minimize
 * the amount of XYRenderer invocations.
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class ScalableXYPlotRenderer<S extends Tuple2D, T extends Dataset<S>> 
    extends XYPlotRenderer<S,T> {

  protected RenderState renderState;
  
  private BasicTuple2D reusablePoint = new BasicTuple2D(0, 0);
  
  public ScalableXYPlotRenderer() {
    renderState = new RenderState();
  }
  
  public void drawDataset(int datasetIndex, Layer layer, XYPlot<S,T> plot) {
    Dataset<S> dataSet = plot.getDatasets().get(datasetIndex);
    
    DatasetRenderer<S,T> renderer = plot.getRenderer(datasetIndex);
    
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
      Tuple2D dataPt = dataSet.getFlyweightTuple(i, mipLevel);
      double x = dataPt.getFirst();
      double y = dataPt.getSecond();
      reusablePoint.setCoordinates(x, y);
      renderState.setFocused(focusSeries == datasetIndex && focusPoint == i);
      renderState.setHovered(hoverPoints[datasetIndex] == i);
      
      // FIXME: refactor to remove cast
      renderer.drawCurvePart(plot, layer, (S)reusablePoint, datasetIndex, renderState);
    }
    renderer.endCurve(plot, layer, datasetIndex, renderState);

    // Render hover and focus points on the curve
    renderer.beginPoints(plot, layer, renderState);
    end = Math.min(domainEnd + 1, numSamples);
    for (int i = Math.max(0, domainStart - 2); i < end; i += inc) {
      Tuple2D dataPt = dataSet.getFlyweightTuple(i, mipLevel);
      double x = dataPt.getFirst();
      double y = dataPt.getSecond();
      reusablePoint.setCoordinates(x, y);
      renderState.setFocused(focusSeries == datasetIndex && focusPoint == i);
      renderState.setHovered(hoverPoints[datasetIndex] == i);
      
      // FIXME: refactor to remove cast
      renderer.drawPoint(plot, layer, (S)reusablePoint, datasetIndex, renderState);
    }
    renderer.endPoints(plot, layer, datasetIndex, renderState);
  }
}
