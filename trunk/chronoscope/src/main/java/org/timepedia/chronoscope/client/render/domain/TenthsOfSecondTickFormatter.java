package org.timepedia.chronoscope.client.render.domain;

import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;

public class TenthsOfSecondTickFormatter extends TickFormatter {

  public TenthsOfSecondTickFormatter(TickFormatter superFormatter) {
    super("XX:XX:XX");
    this.superFormatter = superFormatter;
    this.subFormatter = null;
    this.possibleTickSteps = new int[] {1, 2, 5, 10};
    this.tickInterval = TimeUnit.TENTH_SEC;
  }

  public String formatRelativeTick(ChronoDate d) {
    return dateFormat.tenthOfSecond(d);
  }

  @Override
  public ChronoDate quantizeDate(double dO, int idealTickStep) {
    return ChronoDate.get(dO).truncate(TimeUnit.SEC);
  }

}
