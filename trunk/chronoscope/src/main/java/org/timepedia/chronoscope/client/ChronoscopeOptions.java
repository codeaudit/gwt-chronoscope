package org.timepedia.chronoscope.client;

import org.timepedia.exporter.client.Export;
import org.timepedia.chronoscope.client.overlays.Marker;

/**
 * Global options for Chronoscope.
 */
public class ChronoscopeOptions {

  public static boolean showCreditsEnabled = true;

  public static boolean errorReportingEnabled = true;

  public static boolean historySupport = false;

  private static int maxDynamicDatapoints = 100;

  private static int maxStaticDatapoints = 1000;

  /**
   * A factory function to create a push-pin marker given a Date, then the
   * dataset index to attach this marker to, and a label
   *
   * @gwt.export
   */
  @Export
  public static Marker createMarker(String date, int seriesNum, String label) {
    return new Marker(date, seriesNum, label);
  }

  public static boolean isErrorReportingEnabled() {
    return errorReportingEnabled;
  }

  public static boolean isHistorySupportEnabled() {
    return historySupport;
  }

  public static boolean isShowCreditsEnabled() {
    return showCreditsEnabled;
  }

  /**
   * @gwt.export
   */
  @Export
  public static void setErrorReporting(boolean enabled) {
    errorReportingEnabled = enabled;
  }

  /**
   * @gwt.export
   */
  @Export
  public static void setShowCredits(boolean enabled) {
    showCreditsEnabled = enabled;
  }

  public static void setMaxDynamicDatapoints(int maxDynamicDatapoints) {
    ChronoscopeOptions.maxDynamicDatapoints = maxDynamicDatapoints;
  }

  public static void setMaxStaticDatapoints(int maxStaticDatapoints) {
    ChronoscopeOptions.maxStaticDatapoints = maxStaticDatapoints;
  }

  /**
   * Max number of datapoints to draw when chart is being moved.
   * @return
   */
  public static int getMaxDynamicDatapoints() {
    return maxDynamicDatapoints;
  }

  /**
   * Maximum number of datapoints to draw when chart is not moving.
   * @return
   */
  public static int getMaxStaticDatapoints() {
    return maxStaticDatapoints;
  }
}
