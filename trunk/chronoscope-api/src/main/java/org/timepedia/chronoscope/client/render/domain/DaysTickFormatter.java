package org.timepedia.chronoscope.client.render.domain;

import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;

public class DaysTickFormatter extends DateTickFormatter {

  public DaysTickFormatter(DateTickFormatter superFormatter) {
    super("00/Xxx"); // e.g. "22-Aug"
    this.superFormatter = superFormatter;
    this.subFormatter = new HoursTickFormatter(this);
    this.possibleTickSteps = new int[] {1, 2, 7, 14};
    this.timeUnitTickInterval = TimeUnit.DAY;
  }

  public String format(ChronoDate tick) {
    return dateFormat.monthDay(tick);
  }

  public int getSubTickStep(int primaryTickStep) {
    switch (primaryTickStep) {
      case 1:
        // Divide labeled day ticks into 4 subticks
        return 4;
      default:
        // If labeled ticks are more than 1 day part, then place
        // subticks at 1 day intervals.
        return primaryTickStep;
    }
  }

  /**
   * Date incrementing for {@link TimeUnit#DAY} requires special handling
   * in order to ensure stable tick label rendering.  Other formatters don't 
   * require this because their quantized intervals are all factors of N, 
   * where N is the number of time units that compose the parent time unit. 
   */
  @Override
  public int incrementTick(int numTimeUnits) {
    ChronoDate d = this.currTick;
    final int dayOfMonth = d.getDay();
    boolean doSkipToNextMonth = false;
    switch (numTimeUnits) {
      case 2:
        int daysInMonth = d.getDaysInMonth();
        boolean isEven = (daysInMonth % 2 == 0);
        int dayThreshold = daysInMonth - (isEven ? 1 : 2);
        doSkipToNextMonth = (dayOfMonth >= dayThreshold);
        break;
      case 7:
        doSkipToNextMonth = dayOfMonth > (7 * 3); 
        break;
      case 14:
        doSkipToNextMonth = dayOfMonth >= 14; 
        break;
    }

    int actualIncrement;
    if (doSkipToNextMonth) {
      actualIncrement = gotoFirstOfNextMonth(d);
    }
    else {
      actualIncrement = numTimeUnits;
      d.add(this.timeUnitTickInterval, numTimeUnits);
    }
    
    return actualIncrement;
  }

  @Override
  public void resetToQuantizedTick(double dO, int idealTickStep) {
    currTick.setTime(dO);
    currTick.truncate(this.timeUnitTickInterval);
    final int dayIndex = currTick.getDay() - 1; // convert to 0-based day
    int normalizedValue = 1 + MathUtil.quantize(dayIndex, idealTickStep);
    currTick.set(this.timeUnitTickInterval, normalizedValue);
  }

  /**
   * Increments the specified date to the first day of the following month.
   * For example, if d represents the date 'April 19, 2005', then
   * gotoFirstOfNextMonth(d) would modify d to be May 1, 2005.
   *
   * @return the number of days that were incremented in order to arrive at the
   * first day of the following month.
   */
  private static int gotoFirstOfNextMonth(ChronoDate d) {
    int actualIncrement = d.getDaysInMonth() - d.getDay() + 1;
    d.set(TimeUnit.DAY, 1);
    d.add(TimeUnit.MONTH, 1);
    //System.out.println("TESTING: day=" + d.getDay() + "; actualIncrement=" + actualIncrement);
    return actualIncrement;
  }
  
  @Override
  public boolean isBoundary(int tickStep) {
    return currTick.getDay() == 1;
  }
}
