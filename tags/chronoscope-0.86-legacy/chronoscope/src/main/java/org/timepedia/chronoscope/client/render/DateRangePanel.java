package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.render.domain.DateTickFormatterFactory;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;
import org.timepedia.chronoscope.client.util.date.DateFormatHelper;

/**
 * Draws a date range (e.g. "11/25/1995 - 02/17/2007").
 *
 * @author Chad Takahashi
 */
public class DateRangePanel extends AbstractPanel implements SelfResizing {

  private static final String SPC = "\u00A0";

  private static final String DATE_DELIM_LONG = SPC + "-" + SPC;

  private static final String DATE_DELIM_SHORT = SPC + "-" + SPC;

  private static final double SHOW_DAY_THRESHOLD = TimeUnit.DAY.ms() * 28;

  private static final double SHOW_MONTH_THRESHOLD = TimeUnit.DAY.ms() * 360;

  private static final DateFormatHelper DATE_FMT = new DateFormatHelper();

  private String dateRangeActive, dateRangeShort, dateRangeLong;

  private Interval domainInterval;

  private ChronoDate startDate = ChronoDate.getSystemDate();

  private ChronoDate endDate = ChronoDate.getSystemDate();

  // If true, render the thin date range. Otherwise, use the more
  // verbose date range.
  private boolean compactMode = false;

  private boolean doShowDayInDate, doShowMonthInDate;

  private int typicalCharWidth;

  private boolean isDateDomain;

  public void init(Layer layer, double minDomainInterval, DomainAxisPanel domainAxisPanel) {
    isDateDomain = 
       domainAxisPanel.getTickFormatterFactory() instanceof DateTickFormatterFactory;

    doShowDayInDate = minDomainInterval < SHOW_DAY_THRESHOLD;
    doShowMonthInDate = minDomainInterval < SHOW_MONTH_THRESHOLD;

    final String typicalDateChars = "0123456789-";
    bounds.height = stringSizer.getHeight("X", gssProperties);
    typicalCharWidth = stringSizer.getWidth(typicalDateChars, gssProperties) / 
        typicalDateChars.length();

    if (isDateDomain) {
      if (compactMode) {
        final String typicalShortDateRange = "12/12/00" + DATE_DELIM_SHORT
            + "12/12/00";
        bounds.width = stringSizer.getWidth(typicalShortDateRange, gssProperties);
        //resizeToMinimalWidth();
      } else {
        final String typicalLongDateRange = "12/12/0000" + DATE_DELIM_LONG
            + "12/12/0000";
        bounds.width = stringSizer.getWidth(typicalLongDateRange, gssProperties);
        //resizeToIdealWidth();
      }
    } else {
      final String typicalIntRange = "00000 - 00000";
      bounds.width = stringSizer.getWidth(typicalIntRange, gssProperties);
    }
  }

  public void draw() {
    layer.setStrokeColor(gssProperties.color);

    layer.drawText(bounds.x, bounds.y, dateRangeActive, gssProperties.fontFamily,
        gssProperties.fontWeight, gssProperties.fontSize, textLayerName,
        Cursor.DEFAULT);
  }

  public void updateDomainInterval(Interval domainInterval) {
    ArgChecker.isNotNull(domainInterval, "domainInterval");
    
    final boolean domainIntervalChanged = !domainInterval.equals(this.domainInterval);
    
    if (this.domainInterval == null) {
      this.domainInterval = domainInterval.copy();
    }
    else {
      domainInterval.copyTo(this.domainInterval);
    }

    if (domainIntervalChanged) {
      if (isDateDomain) {
        startDate.setTime(domainInterval.getStart());
        endDate.setTime(domainInterval.getEnd());

        String longStartDate = formatLongDate(startDate);
        String longEndDate = formatLongDate(endDate);
        dateRangeLong = longStartDate + DATE_DELIM_LONG + longEndDate;
        String shortStartDate = formatShortDate(startDate);
        String shortEndDate = formatShortDate(endDate);
        dateRangeShort = shortStartDate + DATE_DELIM_SHORT + shortEndDate;

        dateRangeActive = compactMode ? dateRangeShort : dateRangeLong;
      } else {
        dateRangeActive = dateRangeLong = dateRangeShort =
            formatInt(domainInterval.getStart()) + DATE_DELIM_LONG + 
                formatInt(domainInterval.getEnd());
      }
    }
  }

  public void resizeToMinimalWidth() {
    compactMode = true;
    dateRangeActive = dateRangeShort;
    bounds.width = estimateStringWidth(dateRangeActive);
  }

  public void resizeToIdealWidth() {
    compactMode = false;
    dateRangeActive = dateRangeLong;
    bounds.width = estimateStringWidth(dateRangeActive);
  }

  private String formatLongDate(ChronoDate d) {
    String mo = doShowMonthInDate ? (DATE_FMT.pad(d.getMonth() + 1) + "/") : "";
    String dy = doShowDayInDate ? (DATE_FMT.pad(d.getDay()) + "/") : "";
    String yr = d.getYear() + "";

    return mo + dy + yr;
  }

  private String formatShortDate(ChronoDate d) {
    String mo = doShowMonthInDate ? (DATE_FMT.pad(d.getMonth() + 1) + "/") : "";
    String dy = doShowDayInDate ? (DATE_FMT.pad(d.getDay()) + "/") : "";
    String yr = DATE_FMT.twoDigitYear(d);

    return mo + dy + yr;
  }

  private int estimateStringWidth(String s) {
    return this.typicalCharWidth * s.length();
  }
  
  private static String formatInt(double value) {
    return Integer.toString((int)value);
  }
}
