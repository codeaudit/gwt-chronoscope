package org.timepedia.chronoscope.client.render.domain;

import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;

public class MonthsTickFormatter extends TickFormatter {

  public MonthsTickFormatter(TickFormatter superTickFormatter) {
    super("XXX'XX");
    this.superFormatter = superTickFormatter;
    this.subFormatter = new DaysTickFormatter(this);
    this.possibleTickSteps = new int[] {1, 3, 6};
    this.tickInterval = TimeUnit.MONTH;
  }

  public String formatRelativeTick(ChronoDate d) {
    return dateFormat.monthAndYear(d);
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

}
