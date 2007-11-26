package org.timepedia.chronoscope.client.util;

/**
 * Interface used by tasks that want to be scheduled by a PortableTimer
 *
 * @gwt.exportClosure
 */
public interface PortableTimerTask {
    public void run(PortableTimer timer);
}
