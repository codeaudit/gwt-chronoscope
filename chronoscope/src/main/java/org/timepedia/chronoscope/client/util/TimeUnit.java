package org.timepedia.chronoscope.client.util;

/**
 * Enumeration over common time units.
 * 
 * @author Chad Takahashi
 */
public enum TimeUnit {
  MS (1),
  SEC (1000),
  MIN (SEC.ms * 60),
  HR (MIN.ms * 60),
  DAY (HR.ms * 24),
  MONTH (DAY.ms * 365.2425 / 12.0), // avg # days in year is 365.2425
  YR (MONTH.ms * 12),
  DECADE (YR.ms * 10),
  CENTURY (DECADE.ms * 10),
  MILLENIUM (CENTURY.ms * 10);
  
  private final double ms;
  
  private TimeUnit(double lengthInMilliseconds) {
    this.ms = lengthInMilliseconds;
  }
  
  /**
   * Returns this time interval in milliseconds
   */
  public double ms() {
    return ms;
  }
  
  public static final void main(String[] args) {
    System.out.println((long)TimeUnit.YR.ms);
  }
}
