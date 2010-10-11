package org.timepedia.chronoscope.client.render.domain;

import java.util.Date;

import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;
import org.timepedia.chronoscope.client.util.date.DateFormatHelper;

public class MonthsTickFormatter extends DateTickFormatter {

  public MonthsTickFormatter(DateTickFormatter superTickFormatter) {
    super("XXX'XX");
    this.superFormatter = superTickFormatter;
    this.subFormatter = new DaysTickFormatter(this);
    this.possibleTickSteps = new int[] {1, 3, 6};
    this.timeUnitTickInterval = TimeUnit.MONTH;
  }

  public String format(ChronoDate tick) {
    //return dateFormat.monthAndYear(tick);
    return DateFormatHelper.yearMonthFormatter.format(tick.getTime());
  }

  public int getSubTickStep(int primaryTickStep) {
    if (primaryTickStep == 1) {
      // Place a subtick between each month
      return 2;
    } else {
      // otherwise, place a subtick at 1 month intervals between
      // the labeled ticks.
      return primaryTickStep;
    }
  }
  
  @Override
  public boolean isBoundary(int tickStep) {
    return currTick.getMonth() == 0;
  }

  
  @SuppressWarnings("deprecation")
  @Override
  public void resetToQuantizedTick(double timeStamp, int tickStep) {
    Date d = new Date ((long)(timeStamp));
    d.setMonth(0);
    currTick.setTime(d.getTime());
  }
}
