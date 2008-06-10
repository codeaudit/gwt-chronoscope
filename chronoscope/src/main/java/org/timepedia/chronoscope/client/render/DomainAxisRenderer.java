package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.DateAxis;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;

import java.util.Date;

/**
 * Renders zoomable dates on x-axis
 */
public class DomainAxisRenderer implements AxisRenderer, GssElement {

  private static class DaysTickLabelFormatter extends AbstractTickLabelFormatter
      implements TickLabelFormatter {

    private final TickLabelFormatter superFormatter;

    private final HoursTickLabelFormatter subFormatter;

    public DaysTickLabelFormatter(TickLabelFormatter superFormatter) {

      super("XX XXX");
      this.superFormatter = superFormatter;
      this.subFormatter = new HoursTickLabelFormatter(this);
    }

    public String formatFullTick(double domainPoint) {
      Date d = new Date((long) domainPoint);
      return pad(d.getDate()) + " " + monthLabels[d.getMonth()] + "'" + (
          d.getYear() + 1900);
    }

    public String formatRelativeTick(double domainPoint) {
      Date d = new Date((long) domainPoint);
      return pad(d.getDate()) + " " + monthLabels[d.getMonth()];
    }

    public double getInterval() {
      return MONTH;
    }

    public int getMaxTicks(double start, double end) {
      return 31;
    }

    public int getMinTicksBeforeSubInterval() {
      return 4;
    }

    public TickLabelFormatter getSubIntervalFormatter() {
      return subFormatter;
    }

    public TickLabelFormatter getSuperIntervalFormatter() {
      return superFormatter;
    }

    public double getTick(double origin, double intervalEnd, int tickNum,
        int numTicks, int maxTicks) {
      return origin + 31 / numTicks * tickNum * DAY;
    }

    public boolean inInterval(double domainStart, double domainEnd) {
      double dsize = domainEnd - domainStart;
      return dsize >= DAY && dsize <= MONTH;
    }

    public int quantizeTicks(double ticks) {
      if (ticks >= 28) {
        return (int) ticks;
      }
      if (ticks >= 15) {
        return 15;
      }
      if (ticks >= 7) {
        return 7;
      }
      if (ticks >= 4) {
        return 4;
      }
      if (ticks >= 2) {
        return 2;
      }
      return 1;
    }

    public double quantizeToNearest(double dO) {
      Date d = new Date((long) dO);
      return new Date(d.getYear(), d.getMonth(), d.getDate(), 0, 0, 0)
          .getTime();
    }
  }

  private static class HoursTickLabelFormatter
      extends AbstractTickLabelFormatter implements TickLabelFormatter {

    private final TickLabelFormatter superFormatter;

    private final MinutesTickLabelFormatter subFormatter;

    public HoursTickLabelFormatter(TickLabelFormatter superFormatter) {
      super("XX:00");
      this.superFormatter = superFormatter;
      this.subFormatter = new MinutesTickLabelFormatter(this);
    }

    public String formatFullTick(double domainPoint) {
      Date d = new Date((long) domainPoint);
      return pad(d.getDate()) + " " + monthLabels[d.getMonth()] + "'"
          + (d.getYear() + 1900) + " " + pad(d.getHours()) + ":00";
    }

    public String formatRelativeTick(double domainPoint) {
      Date d = new Date((long) domainPoint);
      return pad(d.getHours()) + ":00";
    }

    public double getInterval() {
      return DAY;
    }

    public int getMaxTicks(double start, double end) {
      return 24;
    }

    public int getMinTicksBeforeSubInterval() {
      return 2;
    }

    public TickLabelFormatter getSubIntervalFormatter() {
      return subFormatter;
    }

    public TickLabelFormatter getSuperIntervalFormatter() {
      return superFormatter;
    }

    public double getTick(double origin, double intervalEnd, int tickNum,
        int numTicks, int maxTicks) {
      return origin + 24 / numTicks * tickNum * HOUR;
    }

    public boolean inInterval(double domainStart, double domainEnd) {
      double dsize = domainEnd - domainStart;
      return dsize >= HOUR && dsize <= DAY;
    }

    public int quantizeTicks(double ticks) {
      if (ticks >= 24) {
        return (int) ticks;
      }
      if (ticks >= 12) {
        return 12;
      }
      if (ticks >= 6) {
        return 6;
      }
      if (ticks >= 4) {
        return 4;
      }
      if (ticks >= 2) {
        return 2;
      }
      return 1;
    }

    public double quantizeToNearest(double dO) {
      Date d = new Date((long) dO);
      return new Date(d.getYear(), d.getMonth(), d.getDate(), 0, 0, 0)
          .getTime();
    }
  }

  private static class MinutesTickLabelFormatter
      extends AbstractTickLabelFormatter implements TickLabelFormatter {

    private final TickLabelFormatter superFormatter;

    private final SecondsTickLabelFormatter subFormatter;

    public MinutesTickLabelFormatter(TickLabelFormatter superFormatter) {

      super("XX:XX");
      this.superFormatter = superFormatter;
      this.subFormatter = new SecondsTickLabelFormatter(this);
    }

    public String formatFullTick(double domainPoint) {
      Date d = new Date((long) domainPoint);
      return pad(d.getDate()) + " " + monthLabels[d.getMonth()] + "'"
          + (d.getYear() + 1900) + " " + pad(d.getHours()) + ":" + pad(
          d.getMinutes());
    }

    public String formatRelativeTick(double domainPoint) {
      Date d = new Date((long) domainPoint);
      return pad(d.getHours()) + ":" + pad(d.getMinutes());
    }

    public double getInterval() {
      return HOUR;
    }

    public int getMaxTicks(double start, double end) {
      return 60;
    }

    public int getMinTicksBeforeSubInterval() {
      return 2;
    }

    public TickLabelFormatter getSubIntervalFormatter() {
      return subFormatter;
    }

    public TickLabelFormatter getSuperIntervalFormatter() {
      return superFormatter;
    }

    public double getTick(double origin, double intervalEnd, int tickNum,
        int numTicks, int maxTicks) {
      return origin + 60 / numTicks * tickNum * MINUTE;
    }

    public boolean inInterval(double domainStart, double domainEnd) {
      double dsize = domainEnd - domainStart;
      return dsize >= MINUTE && dsize <= HOUR;
    }

    public int quantizeTicks(double ticks) {
      if (ticks >= 60) {
        return (int) ticks;
      }
      if (ticks >= 30) {
        return 30;
      }
      if (ticks >= 20) {
        return 20;
      }
      if (ticks >= 15) {
        return 15;
      }
      if (ticks >= 12) {
        return 12;
      }
      if (ticks >= 6) {
        return 6;
      }
      if (ticks >= 5) {
        return 5;
      }
      if (ticks >= 4) {
        return 4;
      }
      if (ticks >= 3) {
        return 3;
      }
      if (ticks >= 2) {
        return 2;
      }
      return 1;
    }

    public double quantizeToNearest(double dO) {
      Date d = new Date((long) dO);
      return new Date(d.getYear(), d.getMonth(), d.getDate(), d.getHours(), 0,
          0).getTime();
    }
  }

  private static class MonthsTickLabelFormatter
      extends AbstractTickLabelFormatter implements TickLabelFormatter {

    private TickLabelFormatter subFormatter = null;

    private TickLabelFormatter superFormatter = null;

    public MonthsTickLabelFormatter(TickLabelFormatter rootTickLabelFormatter) {
      super("XXX'XX");
      superFormatter = rootTickLabelFormatter;
      subFormatter = new DaysTickLabelFormatter(this);
    }

    public String formatFullTick(double domainPoint) {
      Date d = new Date((long) domainPoint);
      return monthLabels[d.getMonth()] + "'" + (d.getYear() + 1900);
    }

    public String formatRelativeTick(double domainPoint) {
      Date d = new Date((long) domainPoint);
      String yr = String.valueOf(d.getYear() + 1900);
      return monthLabels[d.getMonth()] + "'" + yr.substring(yr.length() - 2);
    }

    public double getInterval() {
      return YEAR;
    }

    public int getMaxTicks(double start, double end) {
      return 12;
    }

    public int getMinTicksBeforeSubInterval() {
      return 3;
    }

    public TickLabelFormatter getSubIntervalFormatter() {
      return subFormatter;
    }

    public TickLabelFormatter getSuperIntervalFormatter() {
      return superFormatter;
    }

    public double getTick(double origin, double intervalEnd, int tickNum,
        int numTicks, int maxTicks) {
      Date d = new Date((long) origin);
      Date d2 = new Date(d.getYear(), 12 / numTicks * tickNum, 1);
      return d2.getTime();
    }

    public boolean inInterval(double domainStart, double domainEnd) {
      double dSize = domainEnd - domainStart;
      return dSize >= MONTH && dSize < YEAR;
    }

    public int quantizeTicks(double ticks) {
      if (ticks >= 12) {
        return 12;
      }
      if (ticks >= 6) {
        return 6;
      }
      if (ticks >= 4) {
        return 4;
      }
      if (ticks >= 3) {
        return 3;
      }
      if (ticks >= 2) {
        return 2;
      }
      return 1;
    }

    public double quantizeToNearest(double dO) {
      Date d = new Date((long) dO);
      return new Date(d.getYear(), d.getMonth(), 1).getTime();
    }
  }

  private static class RootTickLabelFormatter extends AbstractTickLabelFormatter
      implements TickLabelFormatter {

    private TickLabelFormatter monthsTickLabelFormatter = null;

    private RootTickLabelFormatter() {
      super("XXXX");
      this.monthsTickLabelFormatter = new MonthsTickLabelFormatter(this);
    }

    public String formatFullTick(double domainPoint) {
      return formatRelativeTick(domainPoint);
    }

    public String formatRelativeTick(double domainPoint) {
      Date d = new Date((long) domainPoint);
      return String.valueOf(d.getYear() + 1900);
    }

    public double getInterval() {
      return YEAR;
    }

    public int getMaxTicks(double start, double end) {
      Date d1 = new Date((long) start);
      Date d2 = new Date((long) end);
      return Math.min(20, d2.getYear() - d1.getYear());
    }

    public int getMinTicksBeforeSubInterval() {
      return 1;
    }

    public TickLabelFormatter getSubIntervalFormatter() {
      return monthsTickLabelFormatter;
    }

    public TickLabelFormatter getSuperIntervalFormatter() {
      return null;
    }

    public double getTick(double origin, double intervalEnd, int tickNum,
        int numTicks, int maxTicks) {
      Date d = new Date((long) origin);
      Date d2 = new Date((long) intervalEnd);
      int year = d2.getYear() - d.getYear();
      int left = year % numTicks;
      int interval = year / numTicks;
      if (left >= interval) {
        interval++;
      }

      Date d3 = new Date(d.getYear() + interval * tickNum, 0, 1);
      return d3.getTime();
    }

    public boolean inInterval(double domainStart, double domainEnd) {
      return (domainEnd - domainStart) >= YEAR;
    }

    public int quantizeTicks(double ticks) {
      return (int) ticks;
    }

    public double quantizeToNearest(double dO) {
      return new Date(new Date((long) dO).getYear(), 0, 1).getTime();
    }
  }

  private static class SecondsTickLabelFormatter
      extends AbstractTickLabelFormatter implements TickLabelFormatter {

    private final TickLabelFormatter superFormatter;

    private final TenthsTickLabelFormatter subFormatter;

    public SecondsTickLabelFormatter(TickLabelFormatter superFormatter) {

      super("XX:XX:XX");
      this.superFormatter = superFormatter;
      this.subFormatter = new TenthsTickLabelFormatter(this);
    }

    public String formatFullTick(double domainPoint) {
      Date d = new Date((long) domainPoint);
      return pad(d.getDate()) + " " + monthLabels[d.getMonth()] + "'"
          + (d.getYear() + 1900) + " " + pad(d.getHours()) + ":"
          + pad(d.getMinutes()) + ":" + pad(d.getSeconds());
    }

    public String formatRelativeTick(double domainPoint) {
      Date d = new Date((long) domainPoint);
      return pad(d.getHours()) + ":" + pad(d.getMinutes()) + ":" + pad(
          d.getSeconds());
    }

    public double getInterval() {
      return MINUTE;
    }

    public int getMaxTicks(double start, double end) {
      return 60;
    }

    public int getMinTicksBeforeSubInterval() {
      return 2;
    }

    public TickLabelFormatter getSubIntervalFormatter() {
      return subFormatter;
    }

    public TickLabelFormatter getSuperIntervalFormatter() {
      return superFormatter;
    }

    public double getTick(double origin, double intervalEnd, int tickNum,
        int numTicks, int maxTicks) {
      return origin + 60 / numTicks * tickNum * SECOND;
    }

    public boolean inInterval(double domainStart, double domainEnd) {
      double dsize = domainEnd - domainStart;
      return dsize >= SECOND && dsize <= MINUTE;
    }

    public int quantizeTicks(double ticks) {
      if (ticks >= 60) {
        return (int) ticks;
      }
      if (ticks >= 30) {
        return 30;
      }
      if (ticks >= 20) {
        return 20;
      }
      if (ticks >= 15) {
        return 15;
      }
      if (ticks >= 12) {
        return 12;
      }
      if (ticks >= 6) {
        return 6;
      }
      if (ticks >= 5) {
        return 5;
      }
      if (ticks >= 4) {
        return 4;
      }
      if (ticks >= 3) {
        return 3;
      }
      if (ticks >= 2) {
        return 2;
      }
      return 1;
    }

    public double quantizeToNearest(double dO) {
      Date d = new Date((long) dO);
      return new Date(d.getYear(), d.getMonth(), d.getDate(), d.getHours(),
          d.getMinutes(), 0).getTime();
    }
  }

  private static class TenthsTickLabelFormatter
      extends AbstractTickLabelFormatter implements TickLabelFormatter {

    private final TickLabelFormatter superFormatter;

    public TenthsTickLabelFormatter(TickLabelFormatter superFormatter) {

      super("XX:XX:XX");
      this.superFormatter = superFormatter;
    }

    public String formatFullTick(double domainPoint) {
      Date d = new Date((long) domainPoint);
      return pad(d.getDate()) + " " + monthLabels[d.getMonth()] + "'"
          + (d.getYear() + 1900) + " " + pad(d.getHours()) + ":"
          + pad(d.getMinutes()) + ":" + pad(d.getSeconds());
    }

    public String formatRelativeTick(double domainPoint) {
      Date d = new Date((long) domainPoint);
      return pad(d.getHours()) + ":" + pad(d.getMinutes()) + ":"
          + pad(d.getSeconds()) + "." + pad((int) (d.getTime() / 100 % 10));
    }

    public double getInterval() {
      return SECOND;
    }

    public int getMaxTicks(double start, double end) {
      return 10;
    }

    public int getMinTicksBeforeSubInterval() {
      return 2;
    }

    public TickLabelFormatter getSubIntervalFormatter() {
      return null;
    }

    public TickLabelFormatter getSuperIntervalFormatter() {
      return superFormatter;
    }

    public double getTick(double origin, double intervalEnd, int tickNum,
        int numTicks, int maxTicks) {
      return origin + 10 / numTicks * tickNum * 100;
    }

    public boolean inInterval(double domainStart, double domainEnd) {
      double dsize = domainEnd - domainStart;
      return dsize >= 100 && dsize <= SECOND;
    }

    public int quantizeTicks(double ticks) {
      if (ticks >= 10) {
        return (int) ticks;
      }
      if (ticks >= 5) {
        return 5;
      }
      if (ticks >= 2) {
        return 2;
      }

      return 1;
    }

    public double quantizeToNearest(double dO) {
      Date d = new Date((long) dO);
      return new Date(d.getYear(), d.getMonth(), d.getDate(), d.getHours(),
          d.getMinutes(), d.getSeconds())
          .getTime();
    }
  }

  public static final double SECOND = 1000;

  public static final double MINUTE = SECOND * 60;

  public static final double HOUR = MINUTE * 60;

  public static final double DAY = HOUR * 24;

  public static final double MONTH = DAY * 31;

  public static final double YEAR = DAY * 365.25;

  private static final String[] monthLabels = {"Jan", "Feb", "Mar", "Apr",
      "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

  private static int[] evenDivisors = {1, 2, 3, 4, 4, 6, 6, 6, 6, 6, 6, 12};

  private static final TickLabelFormatter rootFormatter
      = new RootTickLabelFormatter();

  private static final String CREDITS = "Powered by Timepedia Chronoscope";

  private static final String CREDITS_FONT = "Verdana";

  private static final String CREDITS_WEIGHT = "normal";

  private static final String CREDITS_SIZE = "9pt";

  private static final String TIME_LABEL = ""; // (Time)

  private static String pad(int num) {
    return num < 10 ? "0" + num : "" + num;
  }

  private GssProperties axisProperties;

  private DateAxis axis;

  private GssProperties labelProperties;

  private GssProperties tickProperties;

  private GssProperties gridProperties;

  private boolean boundsSet = false;

  private String textLayerName;

  private int creditsWidth;

  private int creditsHeight;

  public DomainAxisRenderer(DateAxis domainAxis) {
    axis = domainAxis;
  }

  public void drawAxis(XYPlot plot, Layer layer, Bounds bounds,
      boolean gridOnly) {
    View view = plot.getChart().getView();
    init(view);

    double cd = plot.getCurrentDomain();
    double dO = plot.getDomainOrigin();

    if (!gridOnly) {
      clearAxis(layer, bounds);
      drawHorizontalLine(layer, dO, dO + cd, bounds);
    }

    TickLabelFormatter tlf = findInterval(rootFormatter, dO, cd);
    if (tlf.getSuperIntervalFormatter() == null) {
      drawInterval(plot, layer, bounds, tlf, dO, dO + cd, true);
    } else {
      double int1 = tlf.quantizeToNearest(dO);
      double int2 = tlf.quantizeToNearest(dO + cd);
      drawInterval(plot, layer, bounds, tlf, int1, int1 + tlf.getInterval(),
          true);
      if ((dO + cd) - int1 > tlf.getInterval()) {
        drawInterval(plot, layer, bounds, tlf, int2, int2 + tlf.getInterval(),
            true);
      }
    }
    if (labelProperties.visible) {
      drawAxisLabel(layer, bounds);
    }
  }

  public int getLabelHeight(View view, String str) {
    return view.getCanvas().getRootLayer().stringHeight(str,
        axisProperties.fontFamily, axisProperties.fontWeight,
        axisProperties.fontSize);
  }

  public int getLabelWidth(View view, String str) {
    return view.getCanvas().getRootLayer().stringWidth(str,
        axisProperties.fontFamily, axisProperties.fontWeight,
        axisProperties.fontSize);
  }

  public GssElement getParentGssElement() {
    return axis.getAxisPanel();
  }

  public String getType() {
    return "axis";
  }

  public String getTypeClass() {
    return "domain";
  }

  public void init(View view) {
    if (axisProperties == null) {
      axisProperties = view.getGssProperties(this, "");
      labelProperties = view
          .getGssProperties(new GssElementImpl("label", this), "");
      tickProperties = view
          .getGssProperties(new GssElementImpl("tick", this), "");
      gridProperties = view
          .getGssProperties(new GssElementImpl("grid", this), "");
      creditsWidth = view.getCanvas().getRootLayer()
          .stringWidth(CREDITS, CREDITS_FONT, CREDITS_WEIGHT, CREDITS_SIZE);
      creditsHeight = view.getCanvas().getRootLayer()
          .stringHeight(CREDITS, CREDITS_FONT, CREDITS_WEIGHT, CREDITS_SIZE);
      textLayerName = axis.getAxisPanel().getPanelName() + axis.getAxisPanel()
          .getAxisNumber(axis);
    }
  }

  public boolean isAxisLabelVisible() {
    return labelProperties.visible;
  }

  private void clearAxis(Layer layer, Bounds bounds) {

    layer.save();
    layer.setFillColor(axisProperties.bgColor);
    layer.setStrokeColor("rgba(0,0,0,0)");
    layer.setShadowBlur(0);
    layer.setShadowOffsetX(0);
    layer.setShadowOffsetY(0);
    layer.translate(bounds.x, bounds.y);
    layer.scale(bounds.width, bounds.height);
    layer.beginPath();
    layer.rect(0, 0, 1, 1);
    layer.stroke();
    layer.fill();
    if (!boundsSet) {
      layer.setTextLayerBounds(textLayerName, bounds);
      boundsSet = true;
    }
    layer.clearTextLayer(textLayerName);
    layer.restore();
  }

  private double domainToScreenX(XYPlot plot, double dataX, Bounds bounds) {
    return bounds.x + axis.dataToUser(dataX) * bounds.width;
  }

  private void drawAxisLabel(Layer layer, Bounds bounds) {
    layer.setFillColor(labelProperties.bgColor);
    layer.setStrokeColor(labelProperties.color);
    double center = bounds.x + (bounds.width / 2);
    double halfLabelWidth = axis.getAxisLabelWidth() / 2;
    layer.drawText(center - halfLabelWidth,
        bounds.y + axis.getMaxLabelHeight() + 5, TIME_LABEL,
        labelProperties.fontFamily, labelProperties.fontWeight,
        labelProperties.fontSize, textLayerName);
    // only show if enabled and a collision with the axis label is avoided
    if (Chronoscope.isShowCreditsEnabled()
        && center + halfLabelWidth < bounds.x + bounds.width - creditsWidth) {
      layer.save();
      layer.setTransparency(0.2f);
      layer.drawText(bounds.x + bounds.width - creditsWidth,
          bounds.y + bounds.height - creditsHeight, CREDITS, CREDITS_FONT,
          CREDITS_WEIGHT, CREDITS_SIZE, textLayerName);
      layer.restore();
    }
  }

  private void drawHorizontalLine(Layer layer, double origin, double end,
      Bounds bounds) {
    layer.setStrokeColor(tickProperties.color);
    layer.setLineWidth(tickProperties.lineThickness);
    layer.moveTo(bounds.x, bounds.y);
    layer.lineTo(bounds.x + bounds.width, bounds.y);
    layer.stroke();
  }

  private void drawInterval(XYPlot plot, Layer layer, Bounds bounds,
      TickLabelFormatter tlf, double intervalStart, double intervalEnd,
      boolean isRoot) {

    if (tlf == null) {
      return;
    }

    double quantizedOrigin = tlf.quantizeToNearest(intervalStart);
    double startX = domainToScreenX(plot, quantizedOrigin, bounds);
    int maxTicks = tlf.getMaxTicks(intervalStart, intervalEnd);
    double nextOrigin = tlf.quantizeToNearest(intervalEnd);

    double endX = domainToScreenX(plot, nextOrigin, bounds);
    double superLabelWidth = tlf.getSuperIntervalFormatter() == null ? 0
        : tlf.getSuperIntervalFormatter()
            .getMaxDimensionDummyTick(layer, axisProperties);

    double labelWidth = tlf.getMaxDimensionDummyTick(layer, axisProperties);

    double emptySpace = endX - startX - superLabelWidth;
    int numTicks = tlf
        .quantizeTicks(Math.min(emptySpace / (labelWidth + 20), maxTicks));

    if (numTicks < tlf.getMinTicksBeforeSubInterval()) {
      return;
    }

    if (emptySpace / (labelWidth + 20) < 1) {
      return;
    }

    int i = isRoot ? 0 : 1;
    int loop = tlf.getSuperIntervalFormatter() == null ? numTicks + 1
        : numTicks;
    for (; i < loop; i++) {
      double tickPos = tlf
          .getTick(intervalStart, intervalEnd, i, numTicks, maxTicks);
      if(!axis.isVisible(tickPos)) continue;

      drawTick(plot, layer, tickPos, bounds, tlf);
      if (i <= numTicks) {
        drawInterval(plot, layer, bounds, tlf.getSubIntervalFormatter(),
            tickPos,
            tlf.getTick(intervalStart, intervalEnd, i + 1, numTicks, maxTicks),
            false);
      }
    }
  }

  private void drawLabel(XYPlot plot, Layer layer, double tickLoc,
      Bounds bounds, TickLabelFormatter tlf) {

    double ux = domainToScreenX(plot, tickLoc, bounds);
    double nextLoc = domainToScreenX(plot, tickLoc + tlf.getInterval(), bounds);

    String label = tlf.formatRelativeTick(tickLoc);
    layer.setStrokeColor(labelProperties.color);
    layer.setFillColor(labelProperties.bgColor);

    double labelWidth = tlf.getMaxDimensionDummyTick(layer, axisProperties);
    double cx = (ux + nextLoc) / 2;
    if (cx > bounds.x + bounds.width) {
//            return;
    }
    layer.drawText(ux - labelWidth / 2, bounds.y + 5, label,
        axisProperties.fontFamily, axisProperties.fontWeight,
        axisProperties.fontSize, textLayerName);
  }

  private void drawTick(XYPlot plot, Layer layer, double tickLocation,
      Bounds bounds, TickLabelFormatter tlf) {

    double ux = domainToScreenX(plot, tickLocation, bounds);
    if (ux >= bounds.x && ux <= bounds.x + bounds.width) {
      layer.save();
      layer.setFillColor(tickProperties.color);

      layer.fillRect(ux, bounds.y, tickProperties.lineThickness, 5);

      if (gridProperties.visible) {
        Layer player = plot.getPlotLayer();

        player.save();
        player.setFillColor(gridProperties.color);
        player.setTransparency((float) gridProperties.transparency);
        player.fillRect(ux - bounds.x, 0, gridProperties.lineThickness,
            plot.getPlotBounds().height);
        player.restore();
      }

      layer.restore();
      drawLabel(plot, layer, tickLocation, bounds, tlf);
    }
  }

  private TickLabelFormatter findInterval(TickLabelFormatter tlf, double dO,
      double cd) {
    if (tlf == null) {
      return null;
    }
    if (tlf.inInterval(dO, dO + cd)) {
      return tlf;
    } else {
      return findInterval(tlf.getSubIntervalFormatter(), dO, cd);
    }
  }
}
