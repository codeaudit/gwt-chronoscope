package org.timepedia.chronoscope.client.util;

import org.timepedia.exporter.client.ExportClosure;

/**
 * Interface used by tasks that want to be scheduled by a PortableTimer
 *
 */
@ExportClosure
public interface PortableTimerTask {

  public void run(PortableTimer timer);
}
