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
}
