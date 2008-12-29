package org.timepedia.chronoscope.client.render.domain;

import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;

/**
 *
 */
public class MilleniumTickFormatter extends TickFormatter {
  public MilleniumTickFormatter() {
    super("XXXXXX XXX");
    this.superFormatter = null;
    this.subFormatter = new YearsTickFormatter(this);
    this.possibleTickSteps = new int[]{1, 2, 5, 10, 20, 25, 50, 100, 250, 500,
        1000, 2500, 5000, 10000, 25000, 50000, 100000};
    this.timeUnitTickInterval = TimeUnit.MILLENIUM;
  }

  public String formatRelativeTick(ChronoDate d) {
    int year = d.getYear();
    return String.valueOf(year) + (year < -9999 ? "y"
        : (year < 0 ? " BCE" : ""));
  }

  public int getSubTickStep(int primaryTickStep) {
    int x = primaryTickStep;
    if (x == 1) {
      return 4;
    } else if (x < 10) {
      return x;
    } else if (x == 10) {
      return 2;
    } else if (x == 20) {
      return 8;
    } else if (x == 25) {
      return 1;
    } else if (x == 50) {
      return 5;
    } else if (x == 100) {
      return 4;
    } else {
      // Catch-all: remaining tick steps will be (they better be) multiples of 2. 
      return 2;
    }
  }
}
