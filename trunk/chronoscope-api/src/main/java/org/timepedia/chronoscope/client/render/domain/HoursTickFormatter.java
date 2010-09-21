package org.timepedia.chronoscope.client.render.domain;

import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.DateFormatter;
import org.timepedia.chronoscope.client.util.date.DateFormatHelper;

/**
 * @author chad takahashi
 */
public class HoursTickFormatter extends DateTickFormatter {

  public HoursTickFormatter(DateTickFormatter superFormatter) {
    super("00xx"); // e.g. "12pm"
    this.superFormatter = superFormatter;
    this.subFormatter = new MinutesTickFormatter(this);
    this.possibleTickSteps = new int[] {1, 3, 6, 12};
    this.timeUnitTickInterval = TimeUnit.HOUR;
  }

  @Override    
  public String formatTick() {
    DateFormatter hourFormat = DateFormatHelper.getHourFormatter();
    int hourOfDay = Integer.valueOf(hourFormat.format(currTick.getTime()));
      
    switch (hourOfDay) {
      case 0:
        return dateFormat.dayAndMonth(currTick);
      default:
        return dateFormat.slowHour(currTick);
    }
  }

  public int getSubTickStep(int primaryTickStep) {
    switch (primaryTickStep) {
      case 12:
      case 1:
        return 4;
      case 6:
        return 2;
      default:
        return super.getSubTickStep(primaryTickStep);
    }
  }

}
