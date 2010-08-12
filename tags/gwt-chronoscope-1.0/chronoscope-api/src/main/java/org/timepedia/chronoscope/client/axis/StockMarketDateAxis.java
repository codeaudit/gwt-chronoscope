package org.timepedia.chronoscope.client.axis;

import org.timepedia.chronoscope.client.XYPlot;

/**
 *
 */
public class StockMarketDateAxis extends DomainAxis {

  public StockMarketDateAxis(XYPlot plot) {
    super(plot);
  }

  //  private Timeline timeline;

//  public StockMarketDateAxis(XYPlot plot) {
//    super(plot, view);
//    timeline = SegmentedTimeline.newFifteenMinuteTimeline();
//  }

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
