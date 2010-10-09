package org.timepedia.chronoscope.client.render.domain;

import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;

public class YearsTickFormatter extends DateTickFormatter {

  public YearsTickFormatter() {
    super("XXXX");
    this.superFormatter = null;
    this.subFormatter = new MonthsTickFormatter(this);
    // TODO: Really need to define a millennium formatter, and so on... 
    this.possibleTickSteps = 
      new int[] {1, 5, 10, 20, 25, 50, 100, 250, 500, 1000, 
                 2500, 5000, 10000, 25000, 50000, 100000, 500000,
                 1000000, 5000000, 10000000};
    this.timeUnitTickInterval = TimeUnit.YEAR;
  }

  public YearsTickFormatter(MilleniumTickFormatter milleniumTickFormatter) {
    this();
    this.superFormatter = milleniumTickFormatter;
  }

  public String format(ChronoDate tick) {
    return String.valueOf(tick.getYear());
  }

  public int getSubTickStep(int primaryTickStep) {
    int x = primaryTickStep;
    if (x == 1) {
      return 4;
    }
    else if (x < 10) {
      return x;
    }
    else if (x == 10) {
      return 2;
    }
    else if (x == 20) {
      return 8;
    }
    else if (x == 25) {
      return 1;
    }
    else if (x == 50) {
      return 5;
    }
    else if (x == 100) {
      return 4;
    }
    else {
      // Catch-all: remaining tick steps will be (they better be) multiples of 2. 
      return 2;
    }
  }
  
  @Override
  public boolean isBoundary(int tickStep) {
    if (tickStep < 10) {
      return currTick.getYear() % 10 == 0;  
    } else if (tickStep < 100 ) {
      return currTick.getYear() % 100 == 0;  
    } else if (tickStep < 1000 ) {
      return currTick.getYear() % 1000 == 0;  
    } 
    return false;
  }
  
}
