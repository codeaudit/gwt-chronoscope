package org.timepedia.chronoscope.client.render.domain;

import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;

/**
 * @author chad takahashi
 */
public class HoursTickFormatter extends TickFormatter {

  public HoursTickFormatter(TickFormatter superFormatter) {
    super("00xx"); // e.g. "12pm"
    this.superFormatter = superFormatter;
    this.subFormatter = new MinutesTickFormatter(this);
    this.possibleTickSteps = new int[] {1, 3, 6, 12};
    this.timeUnitTickInterval = TimeUnit.HOUR;
  }

  public String formatRelativeTick(ChronoDate d) {
    int hourOfDay = d.getHour();
    switch (hourOfDay) {
      case 0:
        return dateFormat.dayAndMonth(d);
      default:
        return dateFormat.hour(hourOfDay);
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
