/**
 * 
 */
package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.util.ArgChecker;

/**
 * Represents one of the zoom intervals (1d, 1m, 1y, 10y, etc.) that appear in
 * the legend axis.
 * <p>
 * The natural ordering of {@link ZoomInterval} objects is ascending order based
 * first on their intervals, and second on their names.
 * 
 * @author chad takahashi &lt;chad@timepedia.org&gt;
 */
public class ZoomInterval implements Comparable<ZoomInterval> {
  private double interval;
  private String name;

  /**
   * Constructs a ZoomInterval having the specified label name and time interval.
   */
  public ZoomInterval(String name, double interval) {
    ArgChecker.isNotNull(name, "name");
    this.name = name;
    this.interval = ArgChecker.isGT(interval, 0, "interval");
  }

  public int compareTo(ZoomInterval o) {
    if (this.interval < o.interval) {
      return -1;
    } else if (this.interval > o.interval) {
      return 1;
    }
    
    return this.name.compareTo(o.name);
  }

  public boolean equals(Object obj) {
    if (obj == null || obj.getClass() != ZoomInterval.class) {
      return false;
    } else if (obj == this) {
      return true;
    }

    ZoomInterval other = (ZoomInterval) obj;
    return this.name.equals(other.name) && this.interval == other.interval;
  }

  /**
   * Returns the time interval, in milliseconds, that this ZoomInterval
   * represents.
   */
  public double getInterval() {
    return this.interval;
  }

  public String getName() {
    return this.name;
  }

  public int hashCode() {
    return this.name.hashCode();
  }

  public String toString() {
    return this.name;
  }

}
