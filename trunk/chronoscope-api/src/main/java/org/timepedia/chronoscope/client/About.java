package org.timepedia.chronoscope.client;

public class About {

  public final static String REVISION = "$Revision$";

  public final static String RELEASE = "1.86";

  /**
   * Get the current subversion revision number of the Chronoscope library, or
   * return 0 if it is unable to do so.
   */
  public static int getRevision() {
    int index = REVISION.indexOf(":");
    int lastDollar = REVISION.lastIndexOf('$');
    if (index != -1 && lastDollar != -1) {
      String revStr = REVISION.substring(index + 1, lastDollar).trim();
      try {
        return Integer.parseInt(revStr);
      } catch (NumberFormatException e) {
        return 0;
      }
    }
    return 0;
  }
}
