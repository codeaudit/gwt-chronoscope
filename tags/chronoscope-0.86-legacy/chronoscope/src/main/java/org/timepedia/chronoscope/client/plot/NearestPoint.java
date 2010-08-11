package org.timepedia.chronoscope.client.plot;

/**
 * Represents the point nearest to some specified data point.
 * 
 * @author chad takahashi
 */
final class NearestPoint {

  public int pointIndex;

  public double dist;

  public String toString() {
    return "pointIndex=" + pointIndex + ";dist=" + dist;
  }
}

