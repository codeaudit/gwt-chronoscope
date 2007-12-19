package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;


/**
 * An ValueAxis is a class responsible for mapping points in data space to points in screen space, as well as
 * maintaining state related to drawing axis ticks and labels. A given axis may be horizontal or vertical in
 * orientation depending on the {@link AxisPanel} it is placed into, and rendered on the left/right or top/bottom
 * depending on the AxisPanel position as well.
 * <p/>
 * In GSS, an ValueAxis may be referred to using a CSS selector 'axis'. Each axis is numbered, can have several CSS
 * classes, depending on subtypes, like "axis.range" or "axis.domain". See {@link org.timepedia.chronoscope.client.render.AxisRenderer} for more details.
 *
 * @see org.timepedia.chronoscope.client.render.AxisRenderer
 */
public abstract class ValueAxis {
    private String label;
    protected AxisPanel axisPanel;
    private String unitLabel;
    private Chart chart;

    /**
     * Gets the short label representing the units of this axis (m/s, $, etc)
     */
    public String getUnitLabel() {
        return unitLabel;
    }

    /**
     * Set a short label representing the units of this axis (m/s, $, etc)
     */
    public void setUnitLabel(String unitLabel) {
        this.unitLabel = unitLabel;
    }

    /**
     * Sets the long descriptive label used for this axis (Billions of Dollars, Barrels of Oil)
     *
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;

    }


    /**
     * Gets the long descriptive label used for this axis (Billions of Dollars, Barrels of Oil)
     */
    public String getLabel() {
        return label;
    }

    public ValueAxis(Chart chart, String label, String unitLabel) {
        this.chart = chart;
        this.label = label;
        this.unitLabel = unitLabel;
    }

    /**
     * The width in pixels this axis will consume when rendered (including padding, margins, etc)
     */
    public abstract double getWidth();

    /**
     * The height in pixels this axis will consume when rendered (including padding, margins, etc)
     */
    public abstract double getHeight();


    /**
     * Draws the axis into the given layer, within the specified axisBounds, as well as drawing grid-lines on
     * the given DefaultXYPlot.
     *
     * @param plot       the plot to draw the gridlines into
     * @param layer      the layer to render the axis on
     * @param axisBounds the bounds within the layer into which the axis should be drawn
     * @param gridOnly   if true, only render gridlines into the plots, render nothing else
     */
    public abstract void drawAxis(XYPlot plot, Layer layer, Bounds axisBounds, boolean gridOnly);

    /**
     * Returns {@link AxisPanel#VERTICAL_AXIS} or {@link AxisPanel#HORIZONTAL_AXIS}
     */
    public int getOrientation() {
        return axisPanel.getOrientation();
    }


    public void setAxisPanel(AxisPanel axisPanel) {
        this.axisPanel = axisPanel;
    }

    public AxisPanel getAxisPanel() {
        return axisPanel;
    }


    /**
     * Returns the minimum data value on the axis
     *
     * @return
     */
    public abstract double getRangeLow();

    /**
     * Returns the maximum data value on the axis
     *
     * @return
     */
    public abstract double getRangeHigh();


    /**
     * Returns the range of the axis
     */
    public double getRange() {
        return getRangeHigh() - getRangeLow();
    }

    /**
     * Get the chart associated with this axis
     *
     * @return
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * Maps a given dataValue in the interval [rangeLow, rangeHigh] to a a user position in the range [0,1]
     *
     * @param dataValue the value to be mapped
     * @return
     */
    public abstract double dataToUser(double dataValue);

    /**
     * Maps a given user position in the range [0,1] into the interval [rangeLow, rangeHigh]
     *
     * @param userValue the user value to be mapped
     * @return
     */
    public abstract double userToData(double userValue);

    /**
     * Called after an axis is attached to an axis panel
     */
    public abstract void init();
}
