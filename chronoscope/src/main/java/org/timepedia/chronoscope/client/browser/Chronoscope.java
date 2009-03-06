package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

import org.timepedia.chronoscope.client.ChronoscopeOptions;
import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.XYDataSource;
import org.timepedia.chronoscope.client.data.ArrayDataset2D;
import org.timepedia.chronoscope.client.io.DatasetReader;
import org.timepedia.chronoscope.client.browser.json.GwtJsonDataset;
import org.timepedia.chronoscope.client.browser.json.JsonDatasetJSO;
import org.timepedia.chronoscope.client.browser.theme.Theme;
import org.timepedia.chronoscope.client.browser.theme.chrome.ThemeStyleInjector;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.overlays.DomainBarMarker;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.client.overlays.RangeBarMarker;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.render.DatasetRenderer;
import org.timepedia.chronoscope.client.render.LineXYRenderer;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.Exporter;
import org.timepedia.exporter.client.ExporterUtil;

/**
 * Factory class and JS API interface for Chronoscope Charts. <p> This class
 * exports methods that can be used by both Java and JS to create and configure
 * charts, as well as being a global sink for History events.
 *
 * @gwt.exportPackage chronoscope
 */
@ExportPackage("chronoscope")
public class Chronoscope implements Exportable {

  public interface URLResolver {

    public String resolveURL(String url);
  }

  static class NopURLResolver implements URLResolver {

    public String resolveURL(String url) {
      return url;
    }
  }

  /**
   * @gwt.export
   */
  @Export
  public static final int RENDERER_XYLINE = 0;

  /**
   * @gwt.export
   */
  @Export
  public static final int RENDERER_XYBAR = 1;

  public static final int IMMUTABLE = 0, APPENDABLE = 1, RANGEMUTABLE = 2;

  static URLResolver urlResolver = new NopURLResolver();

  private static Theme currentTheme;

  private static boolean microformatsEnabled = false;

  private static boolean fontBookRenderingEnabled = false;

  private static String fontBookServiceEndpoint;

  private static Chronoscope instance;

  private static int globalChartNumber = 0;

  /**
   * A factory function to create a vertical marker given start and end dates,
   * and a label;
   *
   * @gwt.export
   */
  @Export
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
  @Export
  public static RangeBarMarker createHorizontalBarMarker(double rangeLow,
      double rangeHigh, String label) {
    return new RangeBarMarker(rangeLow, rangeHigh, label);
  }

  protected Chronoscope() {
    // no-op
  }

  /**
   * Create a chart inside the given DOM element with the given JSON datasets
   *
   * @gwt.export createTimeseriesChartByElement
   */
  @Export("createTimeseriesChartByElement")
  public ChartPanel createTimeseriesChart(Element elem,
      JsArray<JsonDatasetJSO> jsonDatasets, int chartWidth, int chartHeight) {
    return createTimeseriesChart(elem, createDatasets(jsonDatasets), chartWidth,
        chartHeight);
  }

  /**
   * Legacy method to support old API. Preferred method is to use instance
   * passed to onChronoscopeLoaded, which allows behavior to be overriden.
   *
   * @Deprecated
   */
  @Export
  public static ChartPanel createTimeseriesChartById(String id,
      JsArray<JsonDatasetJSO> jsonDatasets, int chartWidth, int chartHeight,
      ViewReadyCallback readyListener) {
    return getInstance()
        .createTimeseriesChart(id, jsonDatasets, chartWidth, chartHeight,
            readyListener);
  }

  /**
   * Create a chart inside the DOM element with the given ID with the given JSON
   * datasets
   *
   * @gwt.export createTimeseriesChartById
   */
  @Export("createTimeseriesChartById")
  public ChartPanel createTimeseriesChart(String id,
      JsArray<JsonDatasetJSO> jsonDatasets, int chartWidth, int chartHeight,
      ViewReadyCallback readyListener) {
    ChartPanel chart = createTimeseriesChart(DOM.getElementById(id),
        createDatasets(jsonDatasets), chartWidth, chartHeight, readyListener);
    return chart;
  }

  public static ChartPanel createTimeseriesChart(Dataset[] datasets,
      int chartWidth, int chartHeight) {
    return getInstance()
        .createChartPanel((Element) null, datasets, chartWidth, chartHeight,
            null);
  }

//    public static PlotPanel createStackedTimeseriesChart(Dataset[] datasets, Dataset[] datasets2) {
//        Chart chart = new Chart();
//        DefaultXYPlot top = new DefaultXYPlot(chart, datasets, true);
//        DefaultXYPlot bot = new DefaultXYPlot(chart, datasets2, true);
//        SharedDomainXYPlot shared = new SharedDomainXYPlot(0.5, top, bot, null);
//        return new PlotPanel(shared);
//    }

  public static ChartPanel createTimeseriesChart(Element elem,
      Dataset[] datasets, int chartWidth, int chartHeight) {
    return createTimeseriesChart(elem, datasets, chartWidth, chartHeight, null);
  }

  public static ChartPanel createTimeseriesChart(Element elem,
      Dataset[] datasets, int chartWidth, int chartHeight,
      ViewReadyCallback readyListener) {
    return getInstance()
        .createChartPanel(elem, datasets, chartWidth, chartHeight,
            readyListener);
  }

  public static ChartPanel createTimeseriesChart(String elementId,
      Dataset[] datasets, int chartWidth, int chartHeight) {
    return createTimeseriesChart(elementId, datasets, chartWidth, chartHeight,
        null);
  }

  /**
   * @param id
   * @param datasets
   * @param readyCallback
   * @return
   */
  public static ChartPanel createTimeseriesChart(String id, Dataset[] datasets,
      int chartWidth, int chartHeight, ViewReadyCallback readyCallback) {
    return createTimeseriesChart(DOM.getElementById(id), datasets, chartWidth,
        chartHeight, readyCallback);
  }

  @Export
  public Dataset createDataset(JsonDatasetJSO json) {
    return DatasetReader.createDatasetFromJson(new GwtJsonDataset(json));
  }
  
  @Export
  public Dataset createMutableDataset(JsonDatasetJSO json) {
    return DatasetReader.createDatasetFromJson(new GwtJsonDataset(json), true);
  }

  /**
   * Parse a javascript array of JSON objects representing multiresolution
   * Datasets. <p> See {@link #createDataset(org.timepedia.chronoscope.client.browser.json.GwtJsonDataset)}
   * for details of the format.
   */
  public Dataset[] createDatasets(JsArray<JsonDatasetJSO> jsonDatasets) {
    if (jsonDatasets == null) {
      return new Dataset[0];
    }

    int numDatasets = jsonDatasets.length();
    Dataset ds[] = new Dataset[numDatasets];
    for (int i = 0; i < numDatasets; i++) {
      ds[i] = DatasetReader.createDatasetFromJson(new GwtJsonDataset(jsonDatasets.get(i)));
    }
    return ds;
  }

  public static void enableHistorySupport(boolean enabled) {
    ChronoscopeOptions.historySupport = enabled;
  }

  public static String generateId() {
    return "chart" + globalChartNumber++;
  }

  public static String getFontBookServiceEndpoint() {
    return fontBookServiceEndpoint == null ? "http://api.timepedia.org/widget/"
        + "fr" : fontBookServiceEndpoint;
  }

  public static Chronoscope getInstance() {
    if (instance == null) {
      instance = new Chronoscope();
      instance.init();
    }
    return instance;
  }

  public static String getURL(String url) {
    return urlResolver.resolveURL(url);
  }

  public static void initialize() {
    getInstance();
  }

  protected static void setInstance(Chronoscope instance) {
    Chronoscope.instance = instance;
  }

  public static boolean isFontBookRenderingEnabled() {
    return fontBookRenderingEnabled;
  }

  public static boolean isMicroformatsEnabled() {
    return microformatsEnabled;
  }

  @Export
  public static void setShowCredits(boolean enabled) {
    ChronoscopeOptions.setShowCredits(enabled);
  }

  @Export
  public static void setHistorySupport(boolean enabled) {
    ChronoscopeOptions.setHistorySupport(enabled);
  }

  @Export
  public static void setErrorReporting(boolean enabled) {
    ChronoscopeOptions.setErrorReporting(enabled);
  }

  @Export
  public static void setFontBookRendering(boolean enabled) {
    fontBookRenderingEnabled = enabled;
  }

  /**
   * Defaults to GWT.getModuleBaseURL() + "fr"
   */
  @Export
  public static void setFontBookServiceEndpoint(String endpoint) {
    fontBookServiceEndpoint = endpoint;
  }

  public static void setMicroformatsEnabled(boolean microformatsEnabled) {
    Chronoscope.microformatsEnabled = microformatsEnabled;
  }

  public static void setUrlResolver(URLResolver urlr) {
    urlResolver = urlr;
  }

  public static void useGwtTheme(Theme theme) {
    currentTheme = theme;
    ThemeStyleInjector.injectTheme(theme);
  }

  static {
    XYDataSource.setFactory(new BrowserXYDataSourceFactory());
  }

  /**
   */
  public ChartPanel createChartPanel(String id,
      JsArray<JsonDatasetJSO> datasets, ViewReadyCallback listener) {
    Element elem = DOM.getElementById(id);
    int width = DOM.getElementPropertyInt(elem, "clientWidth");
    int height = DOM.getElementPropertyInt(elem, "clientHeight");
    return createChartPanel(elem, createDatasets(datasets), width, height,
        listener);
  }

  /**
   * @gwt.export createTimeseriesChartByIdSized
   */
  @Export("createTimeseriesChartByIdSized")
  public ChartPanel createChartPanel(String id, Dataset[] datasets,
      int chartWidth, int chartHeight, ViewReadyCallback readyListener) {
    return createChartPanel(DOM.getElementById(id), datasets, chartWidth,
        chartHeight, readyListener);
  }

  /**
   * @gwt.export createTimeseriesChartWithElement
   */
  @Export("createTimeseriesChartWithElement")
  public ChartPanel createChartPanel(Element elem, Dataset[] datasets,
      int chartWidth, int chartHeight, ViewReadyCallback readyListener) {

    boolean wasDomElementProvided = (elem != null);

    if (!wasDomElementProvided) {
      elem = DOM.createDiv();
    }

    ChartPanel cpanel = newChartPanel();
    cpanel.setDatasets(datasets);
    cpanel.setDomElement(elem);
    cpanel.setViewReadyCallback(readyListener);
    cpanel.setDimensions(chartWidth, chartHeight);
    cpanel.init();

    if (wasDomElementProvided) {
      if (Document.get().getBody().isOrHasChild(elem)) {
        cpanel.attach();
      }
    }

    return cpanel;
  }

  protected void init() {
    try {
      //TODO: hack, we need a more general purpose way of ensuring this
      //stuff is injected on a per platform basis (not GWT specific)
      // Force initialization of platform specific factories
      GWT.create(DOMView.class);
      if (currentTheme == null) {
        Chronoscope.useGwtTheme(Theme.CHROME);
      }

//      checkForChronoscopeCSS();
//        tryInjectChronoscopeCSS(new Command() {
//            public void execute() {
      exportFunctions();

      onChronoscopeLoad();
      if (Chronoscope.isMicroformatsEnabled()) {
        Microformats.initializeMicroformats(Chronoscope.this);
      }
      
      if (ChronoscopeOptions.isHistorySupportEnabled()) {
        GwtHistoryManagerImpl.initHistory();
      }
//            }
//        });
    } catch (Exception e) {
      if (ChronoscopeOptions.isErrorReportingEnabled()) {
        Window.alert(e.getMessage());
      }
      throw new RuntimeException(e);
    }
  }

  protected ChartPanel newChartPanel() {
    return new ChartPanel();
  }

  protected void exportFunctions() {
    Exporter exporter = (Exporter) GWT.create(Chronoscope.class);
    exporter.export();

    Exporter dexporter = GWT.create(ArrayDataset2D.class);
    dexporter.export();
    
    Exporter exporterMarker = (Exporter) GWT.create(Marker.class);
    exporterMarker.export();

    Exporter exporterRangeMarker = (Exporter) GWT.create(RangeBarMarker.class);
    exporterRangeMarker.export();

    Exporter exporterDomainMarker = (Exporter) GWT.create(DomainBarMarker.class)
        ;
    exporterDomainMarker.export();

    Exporter exporter2 = (Exporter) GWT.create(DefaultXYPlot.class);
    exporter2.export();

    Exporter exporter5 = (Exporter) GWT.create(BrowserChronoscopeMenu.class);
    exporter5.export();

    Exporter exporter7 = (Exporter) GWT.create(DatasetRenderer.class);
    exporter7.export();

    Exporter exporter4 = (Exporter) GWT.create(LineXYRenderer.class);
    exporter4.export();

//    Exporter exporter6 = (Exporter) GWT.create(BarChartXYRenderer.class);
//    exporter6.export();
//
    View v = (View) GWT.create(DOMView.class);
    ((DOMView) v).exportFunctions();
  }

  protected void onChronoscopeLoad() {
    try {
      JavaScriptObject foo = ExporterUtil.wrap(this);
      chronoscopeLoaded(foo);
    } catch (Exception e) {
      if (ChronoscopeOptions.isErrorReportingEnabled()) {
        Window.alert("Chronoscope Failed to Initialize because " + e);
      }
    }
  }

  private void checkForChronoscopeCSS() {
    if (!isCssIncluded("Chronoscope.css")
        && ChronoscopeOptions.errorReportingEnabled) {
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
           try {
             var ss = $doc.styleSheets[i];
             if(isIncluded(ss)) return true;
           } catch(e) {
             // thrown exception when SS cross-domain
           }
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
