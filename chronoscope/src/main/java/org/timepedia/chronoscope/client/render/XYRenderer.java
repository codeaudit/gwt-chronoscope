package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;

/**
 * Abstract class used classes which render a curve using data
 */
public abstract class XYRenderer {
    /**
     * Called before first data is plotted, typically drawing state is setup, or a path is begun
     *
     * @param plot
     * @param layer
     * @param inSelection
     * @param disabled
     */
    public abstract void beginCurve(XYPlot plot, Layer layer, boolean inSelection, boolean disabled);

    /**
     * Called after last data is plotted (last call to drawCurvePart), typically when stroke() or fill() is invoked
     *
     * @param plot
     * @param layer
     * @param inSelection
     * @param disabled
     * @param seriesNum
     */
    public abstract void endCurve(XYPlot plot, Layer layer, boolean inSelection, boolean disabled, int seriesNum);

    /**
     * Called for each visible data point, typically a segment is added to the current drawing path, unless a more
     * sophisticated shape like a bar chart is being rendered
     *
     * @param plot
     * @param layer
     * @param dataX
     * @param dataY
     * @param seriesNum
     * @param isFocused
     * @param isHovered
     * @param inSelection
     * @param isDisabled
     */
    public abstract void drawCurvePart(XYPlot plot, Layer layer, double dataX, double dataY, int seriesNum,
                                       boolean isFocused, boolean isHovered, boolean inSelection, boolean isDisabled);

    /**
     * The maximum number of datapoints that should be drawn in the view and maintain interactive framerates for this
     * renderer
     *
     * @param plot
     * @return
     */
    public int getMaxDrawableDatapoints(XYPlot plot) {
        return plot.getMaxDrawableDataPoints();
    }


    /**
     * Render a small icon or sparkline representing this curve at the given x,y screen coordinates, and return the
     * the Bounds of the icon
     *
     * @param plot
     * @param layer
     * @param x
     * @param y
     * @param seriesNum
     * @return
     */
    public abstract Bounds drawLegendIcon(XYPlot plot, Layer layer, double x, double y, int seriesNum);

    /**
     * Called before points are plotted, typically to setup drawing state (colors, etc)
     *
     * @param plot
     * @param layer
     * @param inSelection
     * @param disabled
     */
    public abstract void beginPoints(XYPlot plot, Layer layer, boolean inSelection, boolean disabled);

    /**
     * Called after all points are plotted, typically to cleanup state (restore() after a save() )
     *
     * @param plot
     * @param layer
     * @param inSelection
     * @param disabled
     * @param seriesNum
     */
    public abstract void endPoints(XYPlot plot, Layer layer, boolean inSelection, boolean disabled, int seriesNum);

    /**
     * Draw an individual point of the given domain and range values
     *
     * @param plot
     * @param layer
     * @param domainx
     * @param rangey
     * @param seriesNum
     * @param focused
     * @param hovered
     * @param inSelection
     * @param disabled
     */
    public abstract void drawPoint(XYPlot plot, Layer layer, double domainx, double rangey, int seriesNum,
                                   boolean focused, boolean hovered, boolean inSelection, boolean disabled);
}
