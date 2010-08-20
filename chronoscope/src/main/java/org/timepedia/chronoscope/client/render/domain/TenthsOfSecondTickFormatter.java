package org.timepedia.chronoscope.client.render.domain;

import org.timepedia.chronoscope.client.util.TimeUnit;

public class TenthsOfSecondTickFormatter extends DateTickFormatter {

  public TenthsOfSecondTickFormatter(DateTickFormatter superFormatter) {
    super("XX:XX:XX");
    this.superFormatter = superFormatter;
    this.subFormatter = null;
    this.possibleTickSteps = new int[] {1, 2, 5, 10};
    this.timeUnitTickInterval = TimeUnit.TENTH_SEC;
  }

  public String formatTick() {
    return dateFormat.tenthOfSecond(currTick);
  }

  @Override
  public void resetToQuantizedTick(double dO, int idealTickStep) {
    currTick.setTime(dO);
    currTick.truncate(TimeUnit.SEC);
  }

}
