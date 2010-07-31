package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.render.domain.DateTickFormatterFactory;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.DateFormatter;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.DateFormatHelper;
import org.timepedia.chronoscope.client.util.date.DateFormatterFactory;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

/**
 * Draws a date range (e.g. "11/25/1995 - 02/17/2007").
 *
 * @author Chad Takahashi
 */
public class DateRangePanel extends AbstractPanel implements
  SelfResizing, GssElement, Exportable {

  private static final String SPC = "\u00A0";

  private String DATE_DELIM_LONG = SPC + "-" + SPC;

  private String DATE_DELIM_SHORT = SPC + "-" + SPC;

  private static final double SHOW_DAY_THRESHOLD = TimeUnit.DAY.ms() * 28;

  private static final double SHOW_MONTH_THRESHOLD = TimeUnit.DAY.ms() * 360;

  private static final DateFormatHelper DATE_FMT = new DateFormatHelper();

  private String dateRangeActive, dateRangeShort, dateRangeLong;

  protected GssProperties compactProperties, labelProperties;

  protected DateFormatter dateFormatter, compactDateFormatter;

  private Interval domainInterval;

  // private ChronoDate startDate = ChronoDate.getSystemDate();

  // private ChronoDate endDate = ChronoDate.getSystemDate();

  // If true, render the thin date range. Otherwise, use the more
  // verbose date range.
  private boolean compactMode = false;

  private boolean doShowDayInDate, doShowMonthInDate;

  private int typicalCharWidth;

  private boolean isDateDomain;

  public String getType() {
    return "daterange";
  }

  public String getTypeClass() {
    return null; // TODO daterange vs daterange.compact
  }

  public final GssElement getParentGssElement() {
    return (LegendAxisPanel)this.parent;
  }

  private void initGssProperties(GssProperties props) {
    gssProperties = props;
    // FIXME - should take into account minimum increments, appropriate range context
    if (null == gssProperties.dateFormat) {
      gssProperties.dateFormat = "yy/MM/dd";
    }
    initDateFormat(gssProperties.dateFormat);
  }

  private void initDateFormat(String dateFormat) {
    gssProperties.dateFormat = dateFormat;
      if (dateFormat != null) {
        this.dateFormatter = DateFormatterFactory.getInstance()
          .getDateFormatter(dateFormat);
      }
  }

  private void initCompactProperties(GssProperties props) {
    compactProperties = props;
    if (null == compactProperties) {
      compactProperties = gssProperties;
    }
    if (null == this.compactProperties.dateFormat ||
        this.compactProperties.dateFormat.length() > 5) {
        // FIXME - take interval size into account
      this.compactProperties.dateFormat = "MM/dd";
    }

    initCompactDateFormat(compactProperties.dateFormat);
  }

  private void initCompactDateFormat(String compactFormat) {
    compactProperties.dateFormat = compactFormat;
    if (null == compactFormat) {
      // FIXME - take interval size into account
      this.compactProperties.dateFormat = "MM/dd";
    }

    this.compactDateFormatter = DateFormatterFactory.getInstance()
          .getDateFormatter(compactProperties.dateFormat);
  }


  public void init(Layer layer, double minDomainInterval, DomainAxisPanel domainAxisPanel) {
    initGssProperties(domainAxisPanel.view.getGssProperties(this, ""));
    initCompactProperties(domainAxisPanel.view.getGssProperties(this, "compact"));

    labelProperties = domainAxisPanel.view.getGssProperties (
      new GssElementImpl("label", getParentGssElement()), "");

    doShowDayInDate = minDomainInterval < SHOW_DAY_THRESHOLD;
    doShowMonthInDate = minDomainInterval < SHOW_MONTH_THRESHOLD;

    final String typicalDateChars = "0123456789-/";
    bounds.height = stringSizer.getHeight("X", labelProperties);
    typicalCharWidth = stringSizer.getWidth(typicalDateChars, labelProperties) /
        typicalDateChars.length();

    isDateDomain = domainAxisPanel.getTickFormatterFactory() instanceof DateTickFormatterFactory;

    if (isDateDomain) {
      if (compactMode) {
        final String typicalShortDateRange =
          compactDateFormatter.format(-2000000000) + DATE_DELIM_SHORT +
          compactDateFormatter.format(2000000000);
        bounds.width = stringSizer.getWidth(typicalShortDateRange, labelProperties);
        //resizeToMinimalWidth();
      } else {
        final String typicalLongDateRange =
          dateFormatter.format(-2000000000) + DATE_DELIM_LONG +
          dateFormatter.format(2000000000);
        bounds.width = stringSizer.getWidth(typicalLongDateRange, labelProperties);
        //resizeToIdealWidth();
      }
    } else {
      final String typicalIntRange = "00000 - 00000";
      bounds.width = stringSizer.getWidth(typicalIntRange, labelProperties);
    }
  }

  public void draw() {
    layer.setStrokeColor(labelProperties.color);

    layer.drawText(bounds.x, bounds.y, dateRangeActive, labelProperties.fontFamily,
        labelProperties.fontWeight, labelProperties.fontSize, textLayerName,
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
        String longStartDate = formatLongDate(domainInterval.getStart());
        String longEndDate = formatLongDate(domainInterval.getEnd());
        dateRangeLong = longStartDate + DATE_DELIM_LONG + longEndDate + SPC;
        String shortStartDate = formatShortDate(domainInterval.getStart());
        String shortEndDate = formatShortDate(domainInterval.getEnd());
        dateRangeShort = shortStartDate + DATE_DELIM_SHORT + shortEndDate + SPC;

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

  @Export
  public void setDateRangeFormat(String dateFormat) {
    initDateFormat(dateFormat);
  }

  @Export
  public void setCompactDateRangeFormat(String compactDateFormat) {
    compactProperties.dateFormat = compactDateFormat;
    if (compactDateFormat != null) {
      this.compactDateFormatter = DateFormatterFactory.getInstance()
          .getDateFormatter(compactDateFormat);
    }
  }

  @Export
  public void setDateDelim(String dateDelim) {
    this.DATE_DELIM_LONG = SPC + dateDelim + SPC;
  }

  @Export
  public void setCompactDateDelim(String compactDateDelim) {
    this.DATE_DELIM_SHORT = SPC + compactDateDelim + SPC;
  }

  private String formatLongDate(double d) {
    return dateFormatter.format(d);
  }

  private String formatShortDate(double d) {
    return compactDateFormatter.format(d);
  }

  private int estimateStringWidth(String s) {
    return this.typicalCharWidth * s.length();
  }
  
  private static String formatInt(double value) {
    return Integer.toString((int)value);
  }
}
