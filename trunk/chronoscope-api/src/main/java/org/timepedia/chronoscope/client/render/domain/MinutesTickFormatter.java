package org.timepedia.chronoscope.client.render.domain;

import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;
import org.timepedia.chronoscope.client.util.DateFormatter;
import org.timepedia.chronoscope.client.util.date.DateFormatHelper;
  
public class MinutesTickFormatter extends DateTickFormatter {

  public MinutesTickFormatter(DateTickFormatter superFormatter) {
    super("XX:XX");
    this.superFormatter = superFormatter;
    this.subFormatter = new SecondsTickFormatter(this);
    this.possibleTickSteps = new int[] {1, 5, 15, 30};
    this.timeUnitTickInterval = TimeUnit.MIN;
  }

  public String format() {
    DateFormatter hourFormat = DateFormatHelper.getHourFormatter();
    int hourOfDay = Integer.valueOf(hourFormat.format(currTick.getTime()));
    if (hourOfDay == 0 && currTick.getMinute() == 0) {
      return dateFormat.dayAndMonth(currTick);
    }
    return super.format();
  }
  
  public String format(ChronoDate tick) {
    return dateFormat.hourAndMinute(tick);
  }

  public int getSubTickStep(int primaryTickStep) {
    switch (primaryTickStep) {
      case 30:
        return 2;
      case 15:
        return 3;
      case 1:
        return 4;
      default:
        return super.getSubTickStep(primaryTickStep);
    }
  }
  
  @Override
  public boolean isBoundary(int tickStep) {
    return currTick.getMinute() == 0;
  }

}
