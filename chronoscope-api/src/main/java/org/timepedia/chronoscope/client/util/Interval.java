package org.timepedia.chronoscope.client.util;

import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Export;


/**
 * A mathematical interval representing a connected portion of a real line. 
 * An interval has 2 endpoints: {@link #getStart()} and {@link #getEnd()}.
 * <p>
 * {@link Interval} can represent anything conforming to this general
 * description, including a time interval on the domain axis, pixel bounds, or
 * whatever.
 * 
 * @author chad takahashi
 */
@ExportPackage("chronoscope")
public class Interval implements Exportable {
  private double start, end;
  
  public Interval(double start, double end) {
    setEndpoints(start, end);
  }
  
  /**
   * Returns true if <tt>value</tt> is a member of this interval,
   * <b>including endpoints</b> (in other words, this method treats
   * this interval as a <i>closed interval</i>).
   */
  @Export
  public boolean contains(double value) {
    return value >= start && value <= end; 
  }

  /**
   * Returns true if <tt>value</tt> is a member of this interval,
   * <b>excluding endpoints</b> (in other words, this method treats
   * this interval as an <i>open interval</i>).
   */
  public boolean containsOpen(double value) {
    return value > start && value < end; 
  }

  /**
   * Returns a copy of this object.
   */
  @Export
  public Interval copy() {
    return new Interval(this.start, this.end);
  }

  /**
   * Copies the state of this interval into the target interval.
   */
  @Export
  public void copyTo(Interval target) {
    target.start = this.start;
    target.end = this.end;
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof Interval)) {
      return false;
    }
    
    Interval i = (Interval)obj;
    return this.start == i.start && this.end == i.end;
  }

  /* approximately equal if start, end differ by small fraction of interval span */
  public boolean approx(Object obj) {
    if (!(obj instanceof Interval)) {
      return false;
    }
    // TODO - use addressable pixel equivalent for approximation
    double CLOSE_ENOUGH = 0.001; // a tenth of a percent
    Interval i = (Interval)obj;
    double delta = i.end - i.start;
    double epsilon = delta * CLOSE_ENOUGH;
    return (Math.abs(this.start - i.start) < epsilon) && (Math.abs(this.end - i.end) < epsilon);
  }

  
  /**
   * Expands this interval so that the specified value is guaranteed fall within
   * it after this method call.  If this interval already contains <tt>value</tt>,
   * then nothing changes.
   * <p>
   * For example, if i represents the interval [-5, 3], then i.expand(7) expands
   * the interval to be [-5, 7].  Similarly, i.expand(-7) results in the interval
   * [-7, 3].  i.expand(1), however, does not change the state of interval i, since
   * 1 is already contained in the interval [-5, 3].  
   */
  @Export
  public void expand(double value) {
    if (value < start) {
      start = value;
    }
    else if (value > end) {
      end = value;
    }
  }
  
  /**
   * Expands this interval so that the specified interval falls inside this interval.
   */
  @Export("expandWithInterval")
  public void expand(Interval interval) {
    expand(interval.start);
    expand(interval.end);
  }
  
  /**
   * Returns a value in the range [0..1] representing the ratio of
   * the specified point to the length of this interval.  For example,
   * if interval i is [10, 20], then <tt>i.getRatioFromPoint(15)</tt>
   * would return <tt>0.5</tt>.
   */
  public double getRatioFromPoint(double pt) {
    if (end == start) {
      return 0.0;
    }
    else {
      return (pt - start) / (end - start);
    }
  }
  
  /**
   * Returns a point on this interval that represents the specified
   * ratio.  For example, if interval i is [10, 20], then
   * <tt>getPointFromRatio(0.5)</tt> would return 15.
   */
  public double getPointFromRatio(double ratio) {
    if (end == start) {
      return start;
    }
    else {
      return start + (ratio * (end - start));
    }
  }

  /**
   * Returns a value in the range [-1..1] representing the percent change
   * between a reference point and a delta point on this interval.  For example,
   * if interval i is [10, 20], then <tt>i.getPercentChange(15,10)</tt>
   * would return <tt>-0.5</tt>.
   */
  public double getPercentChange(double ref, double delta) {
    if (end == start) {
      return 0.0;
    }
    else {
      return ((delta - start) - (ref - start)) / (end - start);
    }
  }

  /**
   * The value of the ending point of this interval.
   */
  @Export
  public double getEnd() {
    return end;
  }

  /**
   * The value of the starting point of this interval.
   */
  @Export
  public double getStart() {
    return start;
  }

  public int hashCode() {
    int hashCode = 1;
    hashCode = (31 * hashCode + (int)this.start) | 0;
    hashCode = (31 * hashCode + (int)this.end) | 0;
    return hashCode;
  }
  
  /**
   * Returns true if the specified interval intersects this interval.
   */
  @Export
  public boolean intersects(Interval i) {
    return i.start <= this.end && i.end >= this.start;
  }
  
  /**
   * The length of this interval (i.e. <tt>end - start</tt>).
   */
  @Export
  public double length() {
    return Math.abs(end - start);
  }

  /**
   * The midpoint of this interval.
   */
  @Export
  public double midpoint() {
    return start + (length() / 2.0);
  }

  /**
   * Assigns the end points of this interval.
   */
  public void setEndpoints(double start, double end) {
    this.start = start;
    this.end = end;
  }

  /**
   * Slides this interval in either a positive or negative direction by the
   * specified amount. For example, let S = interval [3, 7] (i.e. it starts
   * at 3, ends at 7, and has a length of 4). Then S.slide(2) would change its
   * state to [5, 9]. Similarly, S.shift(-5) would have changed its state to
   * [-2, 2]. Note that {@link #length()} is unaffected by this method
   * invocation.
   */
  @Export
  public void slide(double amount) {
    this.start += amount;
    this.end += amount;
  }

  public String toString() {
    return "[" + start + ", " + end + "]";
  }
  
}
