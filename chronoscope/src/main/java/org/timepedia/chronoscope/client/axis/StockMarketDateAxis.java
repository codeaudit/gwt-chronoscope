package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.Chart;

/**
 *
 */
public class StockMarketDateAxis extends DateAxis {

  private Timeline timeline;

  public StockMarketDateAxis(Chart chart, CompositeAxisPanel domainPanel) {
    super(chart.getPlot());
    timeline = SegmentedTimeline.newFifteenMinuteTimeline();
  }

//  public double dataToUser(double dataValue) {
//    double high = timeline.toTimelineValue(getRangeHigh());
//    double low = timeline.toTimelineValue(getRangeLow());
//    double result = (timeline
//        .toTimelineValue(dataValue)-low) / (high - low);
//    return result;
//  }

//  public boolean isVisible(double tickPos) {
//    return timeline.containsDomainValue(tickPos);
//  }
}
