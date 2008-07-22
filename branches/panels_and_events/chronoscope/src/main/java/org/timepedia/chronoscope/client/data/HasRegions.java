package org.timepedia.chronoscope.client.data;

/**
 * A dataset that is split up into regions. Each region contains a
 * multiresolution dataset, however it is truncated at the bottom. When a
 * request is made for data in a truncated mip level, the region containing the
 * domain being requested is loaded asynchronously. Regional datasets maintain a
 * global array of intervals which describe the (on network) layout of the full
 * dataset so that they know what to request.
 *
 * Regions are not guaranteed to be disjoint.
 */
public interface HasRegions {

  /**
   * Called when a region is finished loading.
   */
  void addRegionLoadListener(RegionLoadListener rll);

  /**
   * Find the region number containing the interval [start,end) or -1 if no
   * region can be found.
   */
  int findRegion(double start, double end);

  /**
   * Return the number of regions in this dataset.
   */
  int getNumRegions();

  /**
   * Returns the start of the region represented by this dataset.
   */
  double getRegionBegin();

  /**
   * Returns the start of the ith region.
   */
  double getRegionBegin(int i);

  /**
   * Returns the end of the region represented by this dataset.
   */
  double getRegionEnd();

  /**
   * Returns the end of the ith region.
   */
  double getRegionEnd(int i);

  /**
   * Returns the region number of this dataset. That is,
   * getRegionBegin(getRegionNumber()) == getRegionBegin() &&
   * getRegionEnd(getRegionNumber()) == getRegionEnd()
   */
  int getRegionNumber();

  /**
   * Load the ith region into the implementing dataset
   */
  void loadRegion(int i);
}
