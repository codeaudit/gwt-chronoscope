package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.event.PlotMovedEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages list of instantiated charts, can return serialized state of a chart,
 * and maintain a stack of states for undo/redo capability. Wen a previous
 * history state is restored, the HistoryManager deserializes state to all
 * registered charts.
 */
public class HistoryManager {

  public static final Map<String, Chart> id2chart = new HashMap<String, Chart>()
      ;

  public static void setHistoryManagerImpl(
      HistoryManagerImpl historyManagerImpl) {
    HistoryManager.historyManagerImpl = historyManagerImpl;
  }

  private static HistoryManagerImpl historyManagerImpl
      = new HistoryManagerImpl() {
    public void push(String historyToken) {
      // STUB implementation by default;
    }
  };

  /**
   * Used to prevent double-triggering of history events
   */
  public static String previousHistory;

  public static void pushHistory() {
    if (ChronoscopeOptions.isHistorySupportEnabled()) {
      String newToken = "";
      for (Chart chart : id2chart.values()) {
        newToken += chart.getPlot().getHistoryToken();
      }
      previousHistory = newToken;
      historyManagerImpl.push(newToken);
    }
  }

  public static void restoreHistory(String historyToken) {
    if (historyToken != null && historyToken
        .equals(HistoryManager.previousHistory)) {
      return;
    }

    previousHistory = historyToken;

    if (historyToken != null && historyToken.indexOf(")") != -1) {
      String targets[] = historyToken.split("\\)");
      for (int j = 0; j < targets.length; j++) {
        String target = targets[j];
        String viewId = target.substring(0, target.indexOf("("));
        String[] var = target.substring(target.indexOf("(") + 1).split("\\,");
        Chart chart = (Chart) id2chart.get(viewId);
        double dO = chart.getPlot().getDomain().getStart();
        double cD = chart.getPlot().getDomain().length();
        boolean changed = false;

        if (chart != null) {
          for (int i = 0; i < var.length; i++) {

            if (var[i].startsWith("O")) {
              dO = Double.parseDouble(var[i].substring(1));
              changed = true;
            } else if (var[i].startsWith("D")) {
              cD = Double.parseDouble(var[i].substring(1));
              changed = true;
            }
          }
          if (changed) {
            if (targets.length == 1) {
              chart.getPlot()
                  .animateTo(dO, cD, PlotMovedEvent.MoveType.ZOOMED, null);
            } else {
              chart.getPlot().getDomain().setEndpoints(dO, dO + cD);
            }
          }

          chart.redraw();
        }
      }
    }
  }

  public static Chart getChartById(String id) {
    return (Chart) id2chart.get(id);
  }

  public static void putChart(String id, Chart chart) {
    id2chart.put(id, chart);
    chart.setChartId(id);
  }

  public interface HistoryManagerImpl {

    void push(String historyToken);
  }
}
