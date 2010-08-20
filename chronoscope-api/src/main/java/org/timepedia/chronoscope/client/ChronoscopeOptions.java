package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.overlays.Marker;

/**
 * Global options for Chronoscope.
 */
public class ChronoscopeOptions {
  private static int DEFAULT_DYNAMIC_DATAPOINTS = 256;
  private static int DEFAULT_STATIC_DATAPOINTS = 1024;

  public static boolean showCreditsEnabled = true;

  public static boolean errorReportingEnabled = true;

  public static boolean historySupport = false;

  private static int maxDynamicDatapoints = DEFAULT_DYNAMIC_DATAPOINTS;

  private static int maxStaticDatapoints = DEFAULT_STATIC_DATAPOINTS;

  private static boolean lowPerformance = false;

  private static boolean verticalCrosshair = false;

  private static boolean horizontalCrosshair = false;

  private static String crossHairLabels = null;

  private static boolean defaultMultiaxisMode = true;
  
  private static String defaultAggregateFunction = "mean";

  private static boolean animationPreview = true;

  public static String getDefaultAggregateFunction() {
    return defaultAggregateFunction;
  }

  public static void setDefaultAggregateFunction(
      String defaultAggregateFunction) {
    ChronoscopeOptions.defaultAggregateFunction = defaultAggregateFunction;
  }

  /**
   * A factory function to create a push-pin marker given a Date, then the
   * dataset index to attach this marker to, and a label
   *
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

  public static boolean isHorizontalCrosshairEnabled() {
    return horizontalCrosshair;
  }

  public static boolean isLowPerformance() {
    return lowPerformance;
  }

  public static boolean isShowCreditsEnabled() {
    return showCreditsEnabled;
  }

  public static boolean isVerticalCrosshairEnabled() {
    return verticalCrosshair;
  }

  public static void setErrorReporting(boolean enabled) {
    errorReportingEnabled = enabled;
  }

  public static void setHistorySupport(boolean historySupport) {
    ChronoscopeOptions.historySupport = historySupport;
  }

  public static void setHorizontalCrosshairEnabled(
      boolean horizontalCrosshair) {
    ChronoscopeOptions.horizontalCrosshair = horizontalCrosshair;
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

  public static void setVerticalCrosshairEnabled(boolean verticalCrosshair) {
    ChronoscopeOptions.verticalCrosshair = verticalCrosshair;
  }

  public static boolean isCrosshairLabels() {
    return crossHairLabels != null;
  }

  public static String getCrossHairLabels() {
    return crossHairLabels;
  }

  public static void setCrosshairLabels(String fmt) {
    ChronoscopeOptions.crossHairLabels = fmt;
  }

  public static void setDefaultMultiaxisMode(boolean mode) {
    defaultMultiaxisMode = mode;
  }

  public static boolean getDefaultMultiaxisMode() {
    return defaultMultiaxisMode;
  }

  public static void setAnimationPreview(boolean enabled) {
    animationPreview = enabled;
    if (animationPreview) {
       setMaxDynamicDatapoints(DEFAULT_DYNAMIC_DATAPOINTS);
    } else {
       setMaxDynamicDatapoints(getMaxStaticDatapoints());
    }
  }

  public static boolean getAnimationPreview() {
    return animationPreview;
  }
    
}
