package org.timepedia.chronoscope.client.util;

/**
 * Abstraction for running scheduled tasks, independent of JRE environment
 */
public interface PortableTimer {

  public void cancelTimer();

  public void schedule(int delayMillis);

  public void scheduleRepeating(int periodMillis);

  double getTime();
}
