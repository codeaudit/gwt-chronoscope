package org.timepedia.chronoscope.client.render.domain;

import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;

public class SecondsTickFormatter extends TickFormatter {

  public SecondsTickFormatter(TickFormatter superFormatter) {
    super("XX:XX:XX");
    this.superFormatter = superFormatter;
    this.subFormatter = new TenthsOfSecondTickFormatter(this);
    this.possibleTickSteps = new int[] {1, 5, 15, 30, 60};
    this.timeUnitTickInterval = TimeUnit.SEC;
  }

  public String formatRelativeTick(ChronoDate d) {
    if (d.getHour() == 0 && d.getMinute() == 0 && d.getSecond() == 0) {
      return dateFormat.dayAndMonth(d);
    }
    else {
      return dateFormat.hourMinuteSecond(d);
    }
  }

  public int getSubTickStep(int primaryTickStep) {
    switch (primaryTickStep) {
      case 60:
        return 4;
      case 30:
        return 2;
      case 15:
        return 3;
      case 1:
        return 10;
      default:
        return super.getSubTickStep(primaryTickStep);
    }
  }

}
