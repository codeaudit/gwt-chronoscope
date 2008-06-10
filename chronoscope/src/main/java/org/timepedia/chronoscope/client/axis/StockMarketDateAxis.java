package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.XYPlot;

/**
 *
 */
public class StockMarketDateAxis extends DateAxis {

  private Timeline timeline;

  public StockMarketDateAxis(XYPlot plot, AxisPanel domainPanel) {
    super(plot, domainPanel);
    timeline = SegmentedTimeline.newFifteenMinuteTimeline();
  }

//  public double dataToUser(double dataValue) {
//    double high = timeline.toTimelineValue(getRangeHigh());
//    double low = timeline.toTimelineValue(getRangeLow());
//    double result = (timeline
//        .toTimelineValue(dataValue)-low) / (high - low);
//    return result;
//  }

  public double userToData(double userValue) {
    return super.userToData(userValue);
  }

//  public boolean isVisible(double tickPos) {
//    return timeline.containsDomainValue(tickPos);
//  }
}
