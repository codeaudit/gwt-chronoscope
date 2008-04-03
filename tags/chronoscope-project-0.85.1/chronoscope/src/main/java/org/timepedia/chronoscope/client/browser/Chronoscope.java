package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.XYDataSource;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlotListener;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.data.AppendableXYDataset;
import org.timepedia.chronoscope.client.data.ArrayXYDataset;
import org.timepedia.chronoscope.client.data.RangeMutableArrayXYDataset;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.overlays.DomainBarMarker;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.client.overlays.RangeBarMarker;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.render.BarChartXYRenderer;
import org.timepedia.chronoscope.client.render.XYLineRenderer;
import org.timepedia.chronoscope.client.render.XYRenderer;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.Exporter;
import org.timepedia.exporter.client.ExporterBase;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Factory class and JS API interface for Chronoscope Charts <p/> This class
 * exports methods that can be used by both Java and JS to create and configure
 * charts, as well as being a global sink for History events
 *
 * @gwt.exportPackage chronoscope
 */
public class Chronoscope implements Exportable, HistoryListener {

  /**
   * @gwt.export
   */
  public static final int RENDERER_XYLINE = 0;

  /**
   * @gwt.export
   */
  public static final int RENDERER_XYBAR = 1;

  public static final int IMMUTABLE = 0, APPENDABLE = 1, RANGEMUTABLE = 2;

  private static final HashMap charts = new HashMap();

  /**
   * Used to prevent double-triggering of history events
   */
  private static String previousHistory;

  private static boolean microformatsEnabled = false;

  private static boolean showCreditsEnabled = true;

  private static boolean fontBookRenderingEnabled = false;

  private static boolean errorReportingEnabled = true;

  private static String fontBookServiceEndpoint;

  private static boolean historySupport = false;

  private static Chronoscope instance;

  private static int globalChartNumber = 0;

  /**
   * A factory function to create a vertical marker given start and end dates,
   * and a label;
   *
   * @gwt.export
   */
  public static DomainBarMarker createBarMarker(String startDate,
      String endDate, String label) {
    return new DomainBarMarker(startDate, endDate, label);
  }

  /**
   * A factory function to create a horizontal span marker between two range
   * values, with a given label.
   *
   * @gwt.export
   */
  public static RangeBarMarker createHorizontalBarMarker(double rangeLow,
      double rangeHigh, String label) {
    return new RangeBarMarker(rangeLow, rangeHigh, label);
  }

  /**
   * A factory function to create a push-pin marker given a Date, then the
   * dataset index to attach this marker to, and a label
   *
   * @gwt.export
   */
  public static Marker createMarker(String date, int seriesNum, String label) {
    return new Marker(date, seriesNum, label);
  }

  public static AppendableXYDataset createMutableXYDataset(
      JavaScriptObject json) {
    return (AppendableXYDataset) createXYDataset(json, true);
  }

  /**
   * Create a chart inside the given DOM element with the given JSON datasets
   *
   * @gwt.export createTimeseriesChartByElement
   */
  public static ChartPanel createTimeseriesChart(Element elem,
      JavaScriptObject jsonDatasets, int chartWidth, int chartHeight) {
    return createTimeseriesChart(elem, createXYDatasets(jsonDatasets),
        chartWidth, chartHeight);
  }

  /**
   * Create a chart inside the DOM element with the given ID with the given JSON
   * datasets
   *
   * @gwt.export createTimeseriesChartById
   */
  public static ChartPanel createTimeseriesChart(String id,
      JavaScriptObject jsonDatasets, int chartWidth, int chartHeight,
      ViewReadyCallback readyListener) {
    ChartPanel chart = createTimeseriesChart(DOM.getElementById(id),
        createXYDatasets(jsonDatasets), chartWidth, chartHeight, readyListener);
    return chart;
  }

  public static ChartPanel createTimeseriesChart(XYDataset[] datasets,
      int chartWidth, int chartHeight) {
    return getInstance().createChartPanel((Element) null, datasets, chartWidth,
        chartHeight, null);
  }

//    public static ChartPanel createStackedTimeseriesChart(XYDataset[] datasets, XYDataset[] datasets2) {
//        Chart chart = new Chart();
//        DefaultXYPlot top = new DefaultXYPlot(chart, datasets, true);
//        DefaultXYPlot bot = new DefaultXYPlot(chart, datasets2, true);
//        SharedDomainXYPlot shared = new SharedDomainXYPlot(0.5, top, bot, null);
//        return new ChartPanel(shared);
//    }

  public static ChartPanel createTimeseriesChart(Element elem,
      XYDataset[] datasets, int chartWidth, int chartHeight) {
    return createTimeseriesChart(elem, datasets, chartWidth, chartHeight, null);
  }

  public static ChartPanel createTimeseriesChart(Element elem,
      XYDataset[] datasets, int chartWidth, int chartHeight,
      ViewReadyCallback readyListener) {
    return getInstance().createChartPanel(elem, datasets, chartWidth,
        chartHeight, readyListener);
  }

  public static ChartPanel createTimeseriesChart(String elementId,
      XYDataset[] datasets, int chartWidth, int chartHeight) {
    return createTimeseriesChart(elementId, datasets, chartWidth, chartHeight,
        null);
  }

  /**
   * @param id
   * @param datasets
   * @param readyCallback
   * @return
   */
  public static ChartPanel createTimeseriesChart(String id,
      XYDataset[] datasets, int chartWidth, int chartHeight,
      ViewReadyCallback readyCallback) {
    return createTimeseriesChart(DOM.getElementById(id), datasets, chartWidth,
        chartHeight, readyCallback);
  }

  /**
   * Parse a JSON object representing a multiresolution XYDataset into a class
   * implementing the XYDataset interface <p/> <p/> The JSON format is as
   * follows:
   * <pre>
   * dataset = {
   *    id: "unique id for this dataset",
   *    mipped: true,
   *    domain: [ [level 0 values], [level 1 values], ... ],
   *    range: [ [level 0 values], [level 1 values], ... ],
   *    rangeBottom: min over level 0 values,
   *    rangeTop: max over level 0 values,
   *    label: "default label for this dataset",
   *    axis: "an axis identifier (usually units). Datasets with like axis ids
   * share the same range Axis"
   * }
   * </pre>
   *
   * @gwt.export
   */
  public static XYDataset createXYDataset(JavaScriptObject json) {
    return createXYDataset(json, false);
  }

  public static XYDataset createXYDataset(JavaScriptObject json,
      boolean mutable) {

    JavaScriptObject domain = JavascriptHelper.jsPropGet(json, "domain");
    JavaScriptObject range = JavascriptHelper.jsPropGet(json, "range");
    String mipped = JavascriptHelper.jsPropGetString(json, "mipped");
    ArrayXYDataset dataset = null;

    if (mipped != null && mipped.equals("true")) {

      int dmipLevels = JavascriptHelper.jsArrLength(domain);
      int rmiplevel = JavascriptHelper.jsArrLength(range);

      if (dmipLevels != rmiplevel) {
        if (Chronoscope.isErrorReportingEnabled()) {
          Window.alert("Domain and Range dataset levels are not equal");
        }
      }

      double domains[][] = new double[dmipLevels][];
      double ranges[][] = new double[dmipLevels][];

      for (int i = 0; i < dmipLevels; i++) {
        JavaScriptObject mdomain = JavascriptHelper.jsArrGet(domain, i);
        JavaScriptObject mrange = JavascriptHelper.jsArrGet(range, i);
        domains[i] = getArray(mdomain, 1000);
        ranges[i] = getArray(mrange, 1);
      }
      if (mutable) {
        dataset = new RangeMutableArrayXYDataset(
            JavascriptHelper.jsPropGetString(json, "id"), domains, ranges,
            JavascriptHelper.jsPropGetD(json, "rangeTop"),
            JavascriptHelper.jsPropGetD(json, "rangeBottom"),
            JavascriptHelper.jsPropGetString(json, "label"),
            JavascriptHelper.jsPropGetString(json, "axis"));
      } else {
        dataset = new ArrayXYDataset(
            JavascriptHelper.jsPropGetString(json, "id"), domains, ranges,
            JavascriptHelper.jsPropGetD(json, "rangeTop"),
            JavascriptHelper.jsPropGetD(json, "rangeBottom"),
            JavascriptHelper.jsPropGetString(json, "label"),
            JavascriptHelper.jsPropGetString(json, "axis"));
      }
    } else {
      double domainVal[] = getArray(domain, 1000);
      double rangeVal[] = getArray(range, 1);
      if (mutable) {
        dataset = new RangeMutableArrayXYDataset(
            JavascriptHelper.jsPropGetString(json, "id"), domainVal, rangeVal,
            JavascriptHelper.jsPropGetString(json, "label"),
            JavascriptHelper.jsPropGetString(json, "axis"));
      } else {
        dataset = new ArrayXYDataset(
            JavascriptHelper.jsPropGetString(json, "id"), domainVal, rangeVal,
            JavascriptHelper.jsPropGetString(json, "label"),
            JavascriptHelper.jsPropGetString(json, "axis")

        );
      }
    }
    return dataset;
  }

  /**
   * Parse a javascript array of JSON objects representing multiresolution
   * XYDatasets <p/> See {@link #createXYDataset} for details of the format
   */
  public static XYDataset[] createXYDatasets(JavaScriptObject datasets) {
    if (datasets == null) {
      return new XYDataset[0];
    }

    int arrLen = JavascriptHelper.jsArrLength(datasets);
    XYDataset ds[] = new XYDataset[arrLen];
    for (int i = 0; i < arrLen; i++) {
      ds[i] = createXYDataset(JavascriptHelper.jsArrGet(datasets, i));
    }
    return ds;
  }

  public static void enableHistorySupport(boolean enabled) {
    historySupport = enabled;
  }

  public static String generateId() {
    return "ZZchrono" + globalChartNumber++;
  }

  public static Chart getChartById(String id) {
    return (Chart) charts.get(id);
  }

  public static String getFontBookServiceEndpoint() {
    return fontBookServiceEndpoint == null ?
        "http://api.timepedia.org/api/widget/" + "fr" : fontBookServiceEndpoint;
  }

  public static Chronoscope getInstance() {
    if (instance == null) {
      instance = new Chronoscope();
      instance.init();
    }
    return instance;
  }

  public static void initialize() {
    getInstance();
  }

  public static boolean isErrorReportingEnabled() {
    return errorReportingEnabled;
  }

  public static boolean isFontBookRenderingEnabled() {
    return fontBookRenderingEnabled;
  }

  public static boolean isHistorySupportEnabled() {
    return historySupport;
  }

  public static boolean isMicroformatsEnabled() {
    return microformatsEnabled;
  }

  public static boolean isShowCreditsEnabled() {
    return showCreditsEnabled;
  }

  public static void pushHistory() {
    if (Chronoscope.isHistorySupportEnabled()) {
      Iterator i = charts.values().iterator();
      String newToken = "";
      while (i.hasNext()) {
        Chart v = (Chart) i.next();
        newToken += v.getHistoryToken();
      }
      previousHistory = newToken;
      History.newItem(newToken);
    }
  }

  public static void putChart(String id, Chart chart) {
    charts.put(id, chart);
    chart.setChartId(id);   
  }

  /**
   * @gwt.export
   */
  public static void setErrorReporting(boolean enabled) {
    errorReportingEnabled = enabled;
  }

  /**
   * @gwt.export
   */
  public static void setFontBookRendering(boolean enabled) {
    fontBookRenderingEnabled = enabled;
  }

  /**
   * Defaults to GWT.getModuleBaseURL() + "fr"
   *
   * @gwt.export
   */
  public static void setFontBookServiceEndpoint(String endpoint) {
    fontBookServiceEndpoint = endpoint;
  }

  public static void setMicroformatsEnabled(boolean microformatsEnabled) {
    Chronoscope.microformatsEnabled = microformatsEnabled;
  }

  /**
   * @gwt.export
   */
  public static void setShowCredits(boolean enabled) {
    showCreditsEnabled = enabled;
  }

  /**
   * Given the ID of the DOM element containing the chart, we construct a
   * GssContext
   */
  protected static GssContext createGssContext(String id) {
    return new CssGssContext(id);
  }

  static {
    XYDataSource.setFactory(new BrowserXYDataSourceFactory());
  }

  private static double[] getArray(JavaScriptObject jsArray, int preMul) {
    int len = JavascriptHelper.jsArrLength(jsArray);

    double aVal[] = new double[len];
    for (int i = 0; i < len; i++) {
      aVal[i] = JavascriptHelper.jsArrGetD(jsArray, i) * preMul;
    }
    return aVal;
  }

  /**
   * @gwt.export createTimeseriesChartById
   */
  public ChartPanel createChartPanel(String id, JavaScriptObject datasets,
      ViewReadyCallback listener) {
    Element elem = DOM.getElementById(id);
    int width = DOM.getElementPropertyInt(elem, "clientWidth");
    int height = DOM.getElementPropertyInt(elem, "clientHeight");
    return createChartPanel(elem, createXYDatasets(datasets), width, height,
        listener);
  }

  /**
   * @gwt.export createTimeseriesChartByIdSized
   */
  public ChartPanel createChartPanel(String id, XYDataset[] datasets,
      int chartWidth, int chartHeight, ViewReadyCallback readyListener) {
    return createChartPanel(DOM.getElementById(id), datasets, chartWidth,
        chartHeight, readyListener);
  }

  /**
   * @gwt.export createTimeseriesChartWithElement
   */
  public ChartPanel createChartPanel(Element elem, XYDataset[] datasets,
      int chartWidth, int chartHeight, ViewReadyCallback readyListener) {
    if (elem == null) {
      return new ChartPanel(datasets, chartWidth, chartHeight);
    }
    ChartPanel cp = new ChartPanel(elem, datasets, chartWidth, chartHeight,
        readyListener);
    cp.onAttach();
    return cp;
  }

  public void init() {

    try {
      checkForChronoscopeCSS();
//        tryInjectChronoscopeCSS(new Command() {
//            public void execute() {
      exportFunctions();

      onChronoscopeLoad();
      if (Chronoscope.isMicroformatsEnabled()) {
        Microformats.initializeMicroformats(Chronoscope.this);
      }

      if (isHistorySupportEnabled()) {
        initHistory();
      }
//            }
//        });
    } catch (Exception e) {
      if (isErrorReportingEnabled()) {
        Window.alert(e.getMessage());
      }
      throw new RuntimeException(e.getMessage());
    }
  }

  public void onHistoryChanged(String historyToken) {

    if (true || historyToken != null && historyToken.equals(previousHistory)) {
      return;
    }

    previousHistory = historyToken;

    if (historyToken != null && historyToken.indexOf(")") != -1) {
      String targets[] = historyToken.split("\\)");
      for (int j = 0; j < targets.length; j++) {
        String target = targets[j];
        String viewId = target.substring(0, target.indexOf("("));
        String[] var = target.substring(target.indexOf("(") + 1).split("\\,");
        Chart chart = (Chart) charts.get(viewId);
        double dO = chart.getPlot().getDomainOrigin();
        double cD = chart.getPlot().getCurrentDomain();
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
              chart.getPlot().animateTo(dO, cD, XYPlotListener.ZOOMED, null);
            } else {
              chart.getPlot().setDomainOrigin(dO);
              chart.getPlot().setCurrentDomain(cD);
            }
          }

          chart.redraw();
        }
      }
    }
  }

  protected void exportFunctions() {
    Exporter exporter = (Exporter) GWT.create(Chronoscope.class);
    exporter.export();

    Exporter exporter2 = (Exporter) GWT.create(DefaultXYPlot.class);
    exporter2.export();

    Exporter exporter5 = (Exporter) GWT.create(BrowserChronoscopeMenu.class);
    exporter5.export();

    Exporter exporter7 = (Exporter) GWT.create(XYRenderer.class);
    exporter7.export();

    Exporter exporter4 = (Exporter) GWT.create(XYLineRenderer.class);
    exporter4.export();

    Exporter exporter6 = (Exporter) GWT.create(BarChartXYRenderer.class);
    exporter6.export();

    View v = (View) GWT.create(DOMView.class);
    ((DOMView) v).exportFunctions();
  }

  protected void onChronoscopeLoad() {
    try {
      JavaScriptObject foo = ExporterBase.wrap(this);
      chronoscopeLoaded(foo);
    } catch (Exception e) {
      if (Chronoscope.isErrorReportingEnabled()) {
        Window.alert("Chronoscope Failed to Initialize because " + e);
      }
    }
  }

  private void checkForChronoscopeCSS() {
    if (!isCssIncluded("Chronoscope.css") && errorReportingEnabled) {
      throw new RuntimeException(
          "@import or inclusion of Chronoscope.css missing. To use Chronoscope, your host page, or CSS stylesheet must include Chronoscope.css");
    }
  }

  /**
   * Invoked after Chronoscope has exported the JS API and parsed microformats.
   * An instance of Chronoscope is passed to a Javascript callback.
   */
  private native void chronoscopeLoaded(JavaScriptObject chronoscope) /*-{
        if($wnd.onChronoscopeLoaded)
          $wnd.onChronoscopeLoaded(chronoscope);
    }-*/;

  private void initHistory() {
    History.addHistoryListener(this);
    String initToken = History.getToken();
    if (initToken.length() > 0) {
      onHistoryChanged(initToken);
    }
  }

  private native void injectCss(String s) /*-{
        var link = $doc.createElement("link");
        link.title = "Base Chronoscope Stylesheet";
        link.href = s;
        link.rel = "stylesheet";
        link.type = "text/css";
        $doc.getElementsByTagName("head")[0].appendChild(link);
    }-*/;

  private native boolean isCssIncluded(String css) /*-{
          function isIncluded(ss) {
            if(ss && ss.href.indexOf(css) != -1) {
                 return true;
             }
            var k = 0;
            if(ss.cssRules) {
              for(k = 0; k<ss.cssRules.length; k++) {
                  var rule = ss.cssRules[k];
                  if(rule.href && rule.stylesheet) {
                      if(rule.href.indexOf(css) != -1) return true;
                      var isInc=isIncluded(rule.styleSheet);
                      if(isInc) return true;
                  }
              }
            }
            if(ss.imports) {
              for(k=0; i<ss.imports.length; k++) {
                if(ss.imports[k].href.indexOf(css) != -1) {
                  return true;
                }
                var isInc = isIncluded(ss.imports[k]);
                if(isInc) return true;
              }
            }
           return false;
         }
  
         var i;
         for(i=0; i<$doc.styleSheets.length; i++) {
             var ss = $doc.styleSheets[i];
             if(isIncluded(ss)) return true;
         }
         return false;

        
    }-*/;

  private void tryInjectChronoscopeCSS(final Command cmd) {
    if (!isCssIncluded("Chronoscope.css")) {
      injectCss("Chronoscope.css");
    }
    Timer t = new Timer() {
      int tries = 0;

      public void run() {
        if (!isCssIncluded("Chronoscope.css") && tries < 15) {
          schedule(100);
        } else {
          cmd.execute();
        }
      }
    };
    t.schedule(10);
  }
}
