package org.timepedia.chronoscope.client.util;

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
 * 
 *
 */
public class Interval {
  private double start, end;
  
  public Interval(double start, double end) {
    setEndpoints(start, end);
  }
  
  /**
   * Returns true if <tt>value</tt> is a member of this interval,
   * <b>including endpoints</b> (in other words, this method treats
   * this interval as a <i>closed interval</i>).
   */
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
  public Interval copy() {
    return new Interval(this.start, this.end);
  }

  /**
   * Copies the state of this interval into the target interval.
   */
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
  public void expand(Interval interval) {
    expand(interval.start);
    expand(interval.end);
  }
  
  /**
   * The value of the ending point of this interval.
   */
  public double getEnd() {
    return end;
  }

  /**
   * The value of the starting point of this interval.
   */
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
   * The length of this interval (i.e. <tt>end - start</tt>).
   */
  public double length() {
    return end - start;
  }

  /**
   * The midpoint of this interval.
   */
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
  public void slide(double amount) {
    this.start += amount;
    this.end += amount;
  }

  public String toString() {
    return "[" + start + ", " + end + "]";
  }
  
}
