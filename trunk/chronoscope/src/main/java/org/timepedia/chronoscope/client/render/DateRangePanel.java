package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.util.date.ChronoDate;
import org.timepedia.chronoscope.client.util.date.DateFormatHelper;

/**
 * Draws a date range (e.g. "11/25/1995 - 02/17/2007").
 * 
 * @author Chad Takahashi
 */
public class DateRangePanel extends AbstractPanel {
  private static final String SPC = "\u00A0";
  private static final String DATE_DELIM_LONG = SPC + "-" + SPC;
  private static final String DATE_DELIM_SHORT = SPC + "-" + SPC;

  private static final DateFormatHelper DATE_FMT = new DateFormatHelper();
  
  private String dateRangeActive;
  private String dateRangeShort;
  private String dateRangeLong;

  private double startTimeStamp = -1;
  private ChronoDate startDate = ChronoDate.getSystemDate();
  private double endTimeStamp = -1;
  private ChronoDate endDate = ChronoDate.getSystemDate();

  // If true, render the thin date range. Otherwise, use the more
  // verbose date range.
  private boolean compactMode = false;
  
  private int typicalCharWidth;
  
  public void init(Layer layer) {
    final String typicalDateChars = "0123456789-";
    height = this.calcHeight("X", layer);
    typicalCharWidth = calcWidth(typicalDateChars, layer) / typicalDateChars.length();
    
    if (compactMode) {
      final String typicalShortDateRange = "12/12/00" + DATE_DELIM_SHORT + "12/12/00";
      this.width = this.calcWidth(typicalShortDateRange, layer);
      //resizeToMinimalWidth();
    }
    else {
      final String typicalLongDateRange = "12/12/0000" + DATE_DELIM_LONG + "12/12/0000";
      this.width = this.calcWidth(typicalLongDateRange, layer);
      //resizeToIdealWidth();
    }
  }

  public void draw(Layer layer) {
    layer.setStrokeColor(gssProperties.color);

    layer.drawText(x, y, dateRangeActive, gssProperties.fontFamily,
        gssProperties.fontWeight, gssProperties.fontSize, textLayerName,
        Cursor.DEFAULT);
  }

  public void updateDomainInterval(double startTimeStamp, double endTimeStamp) {
    if (startTimeStamp > endTimeStamp) {
      throw new IllegalArgumentException("startTimeStamp > endTimeStamp: "
          + startTimeStamp + ", " + endTimeStamp);
    }

    boolean dateRangeChanged = (startTimeStamp != this.startTimeStamp)
        || (endTimeStamp != this.endTimeStamp);

    if (dateRangeChanged) {
      this.startTimeStamp = startTimeStamp;
      this.endTimeStamp = endTimeStamp;

      startDate.setTime(startTimeStamp);
      endDate.setTime(endTimeStamp);
      
      String longStartDate = formatLongDate(startDate);
      String longEndDate = formatLongDate(endDate);
      dateRangeLong = longStartDate + DATE_DELIM_LONG + longEndDate;
      String shortStartDate = formatShortDate(startDate);
      String shortEndDate = formatShortDate(endDate);
      dateRangeShort = shortStartDate + DATE_DELIM_SHORT + shortEndDate;

      dateRangeActive = compactMode ? dateRangeShort : dateRangeLong;
    }
  }

  public void resizeToMinimalWidth() {
    compactMode = true;
    dateRangeActive = dateRangeShort;
    width = estimateStringWidth(dateRangeActive);
  }

  public void resizeToIdealWidth() {
    compactMode = false;
    dateRangeActive = dateRangeLong;
    width = estimateStringWidth(dateRangeActive);
  }
  
  private String formatLongDate(ChronoDate d) {
    String s = 
      DATE_FMT.pad(d.getMonth() + 1) + "/" + 
      DATE_FMT.pad(d.getDay()) + "/" + 
      d.getYear();
    return s;
  }

  private String formatShortDate(ChronoDate d) {
    String s = 
      DATE_FMT.pad(d.getMonth() + 1) + "/" + 
      DATE_FMT.pad(d.getDay()) + "/" + 
      DATE_FMT.twoDigitYear(d);
    return s;
  }
  
  private int estimateStringWidth(String s) {
    return this.typicalCharWidth * s.length();
  }
}
