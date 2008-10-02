package org.timepedia.chronoscope.client.util;

/**
 * A closed interval corresponding to a finite portion of an infinite line. A
 * line segment has 2 endpoints: {@link #getStart()} and {@link #getEnd()}.
 * <p>
 * {@link LineSegment} can represent anything conforming to this general
 * description, including a time interval on the domain axis, pixel bounds,
 * or whatever.
 * 
 * @author chad takahashi
 */
public class LineSegment {

  private double start, end;

  public LineSegment(double start, double end) {
    this.start = start;
    this.end = end;
  }
  
  /**
   * Returns a copy of this object.
   */
  public LineSegment copy() {
    return new LineSegment(this.start, this.end);
  }
  
  /**
   * Copies the state of this line segment into the target line segment.
   */
  public void copyTo(LineSegment targetLineSegment) {
    targetLineSegment.start = this.start;
    targetLineSegment.end = this.end;
  }
  
  /**
   * The value of the ending point of this line segment.
   */
  public double getEnd() {
    return end;
  }

  /**
   * The value of the starting point of this line segment.
   */
  public double getStart() {
    return start;
  }
  
  /**
   * The length of this line segment (i.e. <tt>end - start</tt>).
   */
  public double length() {
    return end - start;
  }

  /**
   * The midpoint of this line segment.
   */
  public double midpoint() {
    return start + (length() / 2.0);
  }

  /**
   * Assigns the end points of this line segment.
   */
  public void setEndpoints(double start, double end) {
    this.start = start;
    this.end = end;
  }

  /**
   * Slides this line segment in either a positive or negative direction
   * by the specified amount.  For example, let S = line segment [3, 7]
   * (i.e. it starts at 3, ends at 7, and has a length of 4).  Then
   * S.slide(2) would change its state to [5, 9].  Similarly, S.shift(-5)
   * would have changed its state to [-2, 2].  Note that {@link #length()}
   * is unaffected by this method invocation.
   * @param amount
   */
  public void slide(double amount) {
    this.start += amount;
    this.end += amount;
  }
  
  public String toString() {
    return "[" + (long) start + ", " + (long) end + "]";
  }
}
