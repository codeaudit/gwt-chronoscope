package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssProperties;

/**
 * A class used to help render zoomable axis labels
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
interface TickLabelFormatter {
    /**
     * Return a relative date, which assumes that a nearby fullTick is being rendered to give the user
     * a visual context. For example, if rendering a month, then a relative tick label would be 'Nov',
     * and if day, a relative day would be '13'.
     *
     * @param domainPoint
     * @return
     */
    public String formatRelativeTick(double domainPoint);

    /**
     * Return a fully qualified date, for example, if your ticks are years, then
     * the date would be '2007', if they are months, 'Nov'07', and if they are days,
     * '13 Nov'07'
     *
     * @param domainPoint
     * @return
     */
    public String formatFullTick(double domainPoint);

    /**
     * Return a dummy string whose font metrics will bound any possible value returned from formatFullTick
     *
     * @param layer
     * @param axisProperties
     * @return
     */
    public double getMaxDimensionDummyTick(Layer layer, GssProperties axisProperties);

    /**
     * Quantize a domain value to the nearest tick. For example, quantizing to year will return values which
     * begin on January 1, 00:00:00, and quantizing to month will return a value which begins on the first
     * day of the month.
     *
     * @param dO
     * @return
     */
    double quantizeToNearest(double dO);

    /**
     * Return the max number of divisions per interval for this formatter. For example, if it is an Hour tick formatter,
     * then this will return 24. if months, it will return 12.
     *
     * @param start
     * @param end
     * @return
     */
    int getMaxTicks(double start, double end);

    /**
     * The size of the interval represented by getMaxTicks, in seconds. For example, an Hour formatter will return
     * 86,400,000, the number of seconds in a day.
     *
     * @return
     */
    double getInterval();

    /**
     * Return the domain position of a tick, given the origin, and end interval, the maximize number of ticks begining
     * rendered, the number that will be rendered, and the tick number beging requested.
     * <p/>
     * For example, a day has 24 hours. The given interval between (origin, intervalEnd) may not be able to squeeze
     * 24 tick labels in the screen space alotted. Therefore, numTicks will represent how many can actually fit in
     * the given domain interval, say 4 ticks. tickNum will range from 0 to 3 then, representing 00:00, 06:00,
     * 12:00, and 18:00.
     *
     * @param origin
     * @param intervalEnd
     * @param tickNum
     * @param numTicks
     * @param maxTicks
     * @return
     */
    double getTick(double origin, double intervalEnd, int tickNum, int numTicks, int maxTicks);

    /**
     * Is this domain interval best represented by this tick formatter?
     *
     * @param domainStart
     * @param domainEnd
     * @return
     */
    boolean inInterval(double domainStart, double domainEnd);

    /**
     * Return the next zoom level of labels
     *
     * @return
     */
    public TickLabelFormatter getSubIntervalFormatter();

    /**
     * Return the previous zoom level of labels
     *
     * @return
     */
    public TickLabelFormatter getSuperIntervalFormatter();

    /**
     * How many ticks must capable of being rendered on the screen before this formatter should be used.
     * (i.e. if the user has zoomed out so much that only 2 divisions can be squeezed on the screen, and
     * this formatter says you must have 4, then the subinterval will not be rendered.
     *
     * @return
     */
    int getMinTicksBeforeSubInterval();

    /**
     * Quantize the number of ticks you propose to render to a value which is more aesthetically pleasing, typically
     * a divisor of the max number of ticks. Thus, for 12 months, it means you render either no ticks, 2 ticks, 3
     * ticks, 4 ticks, 6 ticks, or 12 ticks.
     *
     * @param ticks
     * @return
     */
    int quantizeTicks(double ticks);
}
