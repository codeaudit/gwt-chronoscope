package org.timepedia.chronoscope.client.render.domain;

import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;

public class TenthsOfSecondTickFormatter extends DateTickFormatter {

  public TenthsOfSecondTickFormatter(DateTickFormatter superFormatter) {
    super("XX:XX:XX");
    this.superFormatter = superFormatter;
    this.subFormatter = null;
    this.possibleTickSteps = new int[] {1, 2, 5, 10};
    this.timeUnitTickInterval = TimeUnit.TENTH_SEC;
  }

  public String format(ChronoDate tick) {
    return dateFormat.tenthOfSecond(tick);
  }

  @Override
  public void resetToQuantizedTick(double dO, int idealTickStep) {
    currTick.setTime(dO);
    currTick.truncate(TimeUnit.SEC);
  }

  @Override
  public boolean isBoundary(int tickStep) {
    return currTick.getSecond() % 10 == 0;
  }
}
