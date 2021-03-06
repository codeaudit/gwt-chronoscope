package org.timepedia.chronoscope.client.render.domain;

import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;
import org.timepedia.chronoscope.client.util.date.DayOfWeek;

public class WeeksTickFormatter extends DateTickFormatter {

  public WeeksTickFormatter(DateTickFormatter superTickFormatter) {
    super("XXXX'XX");
    this.superFormatter = superTickFormatter;
    this.subFormatter = new DaysTickFormatter(this);
    this.possibleTickSteps = new int[] {1};
    this.timeUnitTickInterval = TimeUnit.WEEK;
  }

  public String format(ChronoDate tick) {
    return dateFormat.yearAndWeek(tick);
  }

  public int getSubTickStep(int primaryTickStep) {
    switch (primaryTickStep) {
      case 1:
        // Divide labeled week ticks into 7 subticks
        return 7;
      default:
        // If labeled ticks are more than 1 week part, then place
        // subticks at 1 day intervals.
        return primaryTickStep;
    }
  }
  
  @Override
  public boolean isBoundary(int tickStep) {
    return currTick.getDayOfWeek() == DayOfWeek.SUNDAY;
  }

}