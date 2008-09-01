package org.timepedia.chronoscope.client.render.domain;

import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;

public class MinutesTickFormatter extends TickFormatter {

  public MinutesTickFormatter(TickFormatter superFormatter) {
    super("XX:XX");
    this.superFormatter = superFormatter;
    this.subFormatter = new SecondsTickFormatter(this);
    this.possibleTickSteps = new int[] {1, 5, 15, 30};
    this.tickInterval = TimeUnit.MIN;
  }

  public String formatRelativeTick(ChronoDate d) {
    if (d.getHour() == 0 && d.getMinute() == 0) {
      return dateFormat.dayAndMonth(d);
    }
    else {
      return dateFormat.hourAndMinute(d);
    }
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

}
