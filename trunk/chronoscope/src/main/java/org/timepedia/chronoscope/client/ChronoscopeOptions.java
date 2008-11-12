package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;

/**
 * Global options for Chronoscope.
 */
@ExportPackage("chronoscope")
public class ChronoscopeOptions implements Exportable {

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
  @Export
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

  /**
   * @gwt.export
   */
  @Export
  public static void setErrorReporting(boolean enabled) {
    errorReportingEnabled = enabled;
  }

  public static void setErrorReportingEnabled(boolean errorReportingEnabled) {
    ChronoscopeOptions.errorReportingEnabled = errorReportingEnabled;
  }

  @Export
  public static void setHistorySupport(boolean historySupport) {
    ChronoscopeOptions.historySupport = historySupport;
  }

  @Export
  public static void setLowPerformance(boolean lowPerformance) {
    ChronoscopeOptions.lowPerformance = lowPerformance;
  }

  public static void setMaxDynamicDatapoints(int maxDynamicDatapoints) {
    ChronoscopeOptions.maxDynamicDatapoints = maxDynamicDatapoints;
  }

  public static void setMaxStaticDatapoints(int maxStaticDatapoints) {
    ChronoscopeOptions.maxStaticDatapoints = maxStaticDatapoints;
  }

  /**
   * @gwt.export
   */
  @Export
  public static void setShowCredits(boolean enabled) {
    showCreditsEnabled = enabled;
  }

  public static void setShowCreditsEnabled(boolean showCreditsEnabled) {
    ChronoscopeOptions.showCreditsEnabled = showCreditsEnabled;
  }
}
