package org.timepedia.chronoscope.client;

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

  private static boolean lowPerformance = false;

  /**
   * A factory function to create a push-pin marker given a Date, then the
   * dataset index to attach this marker to, and a label
   *
   * @gwt.export
   */
  public static Marker createMarker(String date, int seriesNum, String label) {
    return new Marker(date, seriesNum, label);
  }

  /**
   * Max number of datapoints to draw when chart is being moved.
   */
  public static int getMaxDynamicDatapoints() {
    return maxDynamicDatapoints;
  }

  /**
   * Maximum number of datapoints to draw when chart is not moving.
   */
  public static int getMaxStaticDatapoints() {
    return maxStaticDatapoints;
  }

  public static boolean isErrorReportingEnabled() {
    return errorReportingEnabled;
  }

  public static boolean isHistorySupportEnabled() {
    return historySupport;
  }

  public static boolean isLowPerformance() {
    return lowPerformance;
  }

  public static boolean isShowCreditsEnabled() {
    return showCreditsEnabled;
  }

  public static void setErrorReporting(boolean enabled) {
    errorReportingEnabled = enabled;
  }


  public static void setHistorySupport(boolean historySupport) {
    ChronoscopeOptions.historySupport = historySupport;
  }

  public static void setLowPerformance(boolean lowPerformance) {
    ChronoscopeOptions.lowPerformance = lowPerformance;
  }

  public static void setMaxDynamicDatapoints(int maxDynamicDatapoints) {
    ChronoscopeOptions.maxDynamicDatapoints = maxDynamicDatapoints;
  }

  public static void setMaxStaticDatapoints(int maxStaticDatapoints) {
    ChronoscopeOptions.maxStaticDatapoints = maxStaticDatapoints;
  }

  public static void setShowCredits(boolean enabled) {
    showCreditsEnabled = enabled;
  }
}
