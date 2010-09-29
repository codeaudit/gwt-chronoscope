package org.timepedia.chronoscope.client.render.domain;

import org.timepedia.chronoscope.client.util.TimeUnit;

public class MonthsTickFormatter extends DateTickFormatter {

  public MonthsTickFormatter(DateTickFormatter superTickFormatter) {
    super("XXX'XX");
    this.superFormatter = superTickFormatter;
    this.subFormatter = new DaysTickFormatter(this);
    this.possibleTickSteps = new int[] {1, 3, 6};
    this.timeUnitTickInterval = TimeUnit.MONTH;
  }

  public String formatTick() {
    return dateFormat.monthAndYear(currTick);
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
  public boolean isBoundary() {
    return currTick.getMonth() == 0;
  }

}
