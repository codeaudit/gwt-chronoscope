package org.timepedia.chronoscope.client.plot;

/**
 * Distance formulas needed by {@link DefaultXYPlot} for finding the point in
 * a {@link Dataset} that is closest to a given screen coordinate.
 * 
 * @author chad takahashi
 */
enum DistanceFormula {

  /**
   * The distance from point (x1,x2) to point (y1,y2) on an XY plane.
   */
  XY {double dist(double x1, double y1, double x2, double y2) {
    return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
  }},
  /**
   * Considers only the distance between x1 and x2, ignoring the y values of
   * points (x1,y1) and (x2,y2).
   */
  X_ONLY {double dist(double x1, double y1, double x2, double y2) {
    return Math.abs(x1 - x2);
  }};

  /**
   * The distance from points (x1,y1) to (x2,y2).
   */
  abstract double dist(double x1, double y1, double x2, double y2);
}
