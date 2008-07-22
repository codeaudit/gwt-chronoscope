package org.timepedia.chronoscope.client.data;

/**
 * Interface used to listen for region changes.
 */
public interface RegionLoadListener {

  /**
   * This method is called after a new Region has been loaded in a dataset,
   * replacing the currently loaded region. It is the responsibility of plots
   * to detect this change, and act accordingly.
   */
   void onRegionLoaded(HasRegions h, int regionNumber);
}
