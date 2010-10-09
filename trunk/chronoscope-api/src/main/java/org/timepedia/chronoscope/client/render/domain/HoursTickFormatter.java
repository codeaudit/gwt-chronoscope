package org.timepedia.chronoscope.client.render.domain;

import java.util.Date;

import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.chronoscope.client.util.TimeUnit;
import org.timepedia.chronoscope.client.util.date.ChronoDate;
import org.timepedia.chronoscope.client.util.date.DateFormatHelper;

/**
 * @author chad takahashi
 */
public class HoursTickFormatter extends DateTickFormatter {
  
  public HoursTickFormatter(DateTickFormatter superFormatter) {
    super("00:00"); // e.g. "01:00"
    this.superFormatter = superFormatter;
    this.subFormatter = new MinutesTickFormatter(this);
    this.possibleTickSteps = new int[] {1, 3, 6, 12};
    this.timeUnitTickInterval = TimeUnit.HOUR;
  }
  
  @Override
  public String format() {
    if (0 == Integer.valueOf(DateFormatHelper.getHourFormatter().format(currTick.getTime()))) {
        return dateFormat.dayAndMonth(currTick);
    }
    return format(currTick);
  }

  @Override
  public String format(ChronoDate tick) {
    return dateFormat.hourAndMinute(tick);
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
  
  @Override
  public boolean isBoundary(int tickStep) {
    return 0 == Integer.valueOf(DateFormatHelper.getHourFormatter().format(currTick.getTime()));
  }
 
  @SuppressWarnings("deprecation")
  @Override
  public void resetToQuantizedTick(double timeStamp, int tickStep) {
    Date d = new Date ((long)(timeStamp));
    int normalizedValue = MathUtil.quantize(d.getHours(), tickStep);
    d.setHours(normalizedValue);
    currTick.setTime(d.getTime());
    currTick.truncate(this.timeUnitTickInterval);
  }

}
