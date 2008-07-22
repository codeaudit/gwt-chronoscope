package org.timepedia.chronoscope.client.render;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.canvas.Layer;

import java.util.Date;

/**
 * Draws a date range (e.g. "11/25/1995 - 02/17/2007").
 * 
 * @author Chad Takahashi
 */
public class DateRangePanel extends AbstractPanel {
  private static final String SPC = "\u00A0";
  private static final String DATE_DELIM_LONG = SPC + "-" + SPC;
  private static final String DATE_DELIM_SHORT = SPC + "-" + SPC;

  private static final DateTimeFormat LONG_DATE_FORMAT = DateTimeFormat.getFormat("MM/dd/yyyy");
  private static final DateTimeFormat SHORT_DATE_FORMAT = DateTimeFormat.getFormat("MM/dd/yy");

  private String dateRangeActive;
  private String dateRangeShort;
  private String dateRangeLong;
  private int minPanelWidth;
  private int idealPanelWidth;

  private double startTimeStamp = -1;
  private Date startDate = new Date();
  private double endTimeStamp = -1;
  private Date endDate = new Date();

  // If true, render the thin date range. Otherwise, use the more
  // verbose date range.
  private boolean compactMode = false;

  public void init(Layer layer) {
    minPanelWidth = calcWidth("12/12/00" + DATE_DELIM_SHORT + "12/12/00", layer);
    idealPanelWidth = calcWidth("12/12/0000" + DATE_DELIM_LONG + "12/12/0000", layer);
    
    if (compactMode) {
      resizeToMinimalWidth();
    }
    else {
      resizeToIdealWidth();
    }

    height = this.calcHeight("X", layer);
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

      startDate.setTime((long) startTimeStamp);
      endDate.setTime((long) endTimeStamp);

      String longStartDate = LONG_DATE_FORMAT.format(startDate);
      String longEndDate = LONG_DATE_FORMAT.format(endDate);
      dateRangeLong = longStartDate + DATE_DELIM_LONG + longEndDate;
      String shortStartDate = SHORT_DATE_FORMAT.format(startDate);
      String shortEndDate = SHORT_DATE_FORMAT.format(endDate);
      dateRangeShort = shortStartDate + DATE_DELIM_SHORT + shortEndDate;

      dateRangeActive = compactMode ? dateRangeShort : dateRangeLong;
    }
  }

  public void resizeToMinimalWidth() {
    compactMode = true;
    width = minPanelWidth;
    dateRangeActive = dateRangeShort;
  }

  public void resizeToIdealWidth() {
    compactMode = false;
    width = idealPanelWidth;
    dateRangeActive = dateRangeLong;
  }
}
