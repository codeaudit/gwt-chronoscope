package org.timepedia.chronoscope.client.browser;

import org.timepedia.chronoscope.client.ChronoscopeComponentFactory;
import org.timepedia.chronoscope.client.ChronoscopeOptions;
import org.timepedia.chronoscope.client.ComponentFactory;
import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.XYDataSource;
import org.timepedia.chronoscope.client.browser.json.GwtJsonDataset;
import org.timepedia.chronoscope.client.browser.json.JsonDatasetJSO;
import org.timepedia.chronoscope.client.browser.theme.Theme;
import org.timepedia.chronoscope.client.browser.theme.chrome.ThemeStyleInjector;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.data.ArrayDataset2D;
import org.timepedia.chronoscope.client.data.IncrementalDatasetResponseImpl;
import org.timepedia.chronoscope.client.data.MutableDatasetND;
import org.timepedia.chronoscope.client.io.DatasetReader;
import org.timepedia.chronoscope.client.overlays.DomainBarMarker;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.client.overlays.RangeBarMarker;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.render.DatasetRenderer;
import org.timepedia.chronoscope.client.render.LineXYRenderer;
import org.timepedia.chronoscope.client.render.domain.DateTickFormatterFactory;
import org.timepedia.chronoscope.client.render.domain.IntTickFormatterFactory;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.Exporter;
import org.timepedia.exporter.client.ExporterUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

/**
 * Factory class and JS API interface for Chronoscope Charts. <p> This class
 * exports methods that can be used by both Java and JS to create and configure
 * charts, as well as being a global sink for History events.
 *
 */
@ExportPackage("chronoscope")
@Singleton
public class Chronoscope
    extends org.timepedia.chronoscope.client.Chronoscope<ChartPanel>
    implements Exportable {

  private DatasetReader datasetReader;

  protected static ChronoscopeBrowserInjector injector;

  private static boolean alreadyRan;

  @GinModules(ChronoscopeBrowserModule.class)
  public interface ChronoscopeBrowserInjector extends Ginjector {

    org.timepedia.chronoscope.client.Chronoscope<ChartPanel> get();

    ComponentFactory getComponentFactory();
  }

  public static class ChronoscopeBrowserModule extends AbstractGinModule {

    public void configure() {
      try {
        bind(
            new TypeLiteral<org.timepedia.chronoscope.client.Chronoscope<ChartPanel>>() {
            }).toProvider(ChronoscopeProvider.class).in(Singleton.class);
        bind(ComponentFactory.class).to(ChronoscopeComponentFactory.class);
        bind(URLResolver.class).to(NopURLResolver.class);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static class ChronoscopeProvider implements Provider<Chronoscope> {

    private DatasetReader reader;

    private URLResolver resolver;

    @Inject
    public ChronoscopeProvider(DatasetReader reader, URLResolver resolver) {
      this.reader = reader;
      this.resolver = resolver;
    }

    public Chronoscope get() {
      Chronoscope c = new Chronoscope();
      c.setDatasetReader(reader);
      c.setUrlResolver(resolver);
      c.init();
      return c;
    }
  }

  public static Chronoscope get() {
    ChronoscopeInjector injector = getBrowserInjector();
    return (Chronoscope) injector.get();
  }

  private static ChronoscopeInjector getBrowserInjector() {
    if (injector == null) {
      injector = GWT.create(ChronoscopeBrowserInjector.class);
    }
    return new ChronoscopeInjector() {
      public org.timepedia.chronoscope.client.Chronoscope get() {
        return injector.get();
      }

      public ComponentFactory getComponentFactory() {
        return injector.getComponentFactory();
      }
    };
  }

  @Override
  public ChartPanel createChart(Datasets datasets, int width, int height,
      ViewReadyCallback callback) {
    return createChartPanel((Element) null, datasets.toArray(), width, height,
        callback);
  }

  @Override
  protected org.timepedia.chronoscope.client.Chronoscope.ChronoscopeInjector<ChartPanel> getInjector() {
    return getBrowserInjector();
  }

  public interface URLResolver {

    public String resolveURL(String url);
  }

  public static class NopURLResolver implements URLResolver {

    public String resolveURL(String url) {
      return url;
    }
  }

  URLResolver urlResolver;

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
   */
  @Export
  public static DomainBarMarker createBarMarker(String startDate,
      String endDate, String label) {
    return new DomainBarMarker(startDate, endDate, label);
  }

  /**
   * A factory function to create a vertical marker given start and end dates,
   * and a label with a gss class.
   *
   */
  @Export
  public static DomainBarMarker createBarMarkerWithClass(String startDate,
      String endDate, String label, String gssClass) {
    return new DomainBarMarker(startDate, endDate, label, gssClass);
  }

  /**
   * A factory function to create a horizontal span marker between two range
   * values, with a given label.
   *
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
   */
  @Export("createTimeseriesChartByElement")
  public ChartPanel createTimeseriesChart(Element elem,
      JsArray<JsonDatasetJSO> jsonDatasets, int chartWidth, int chartHeight) {
    return createTimeseriesChart(elem, createDatasets(jsonDatasets), chartWidth,
        chartHeight);
  }


  /**
   * Create a chart inside the DOM element with the given ID with the given JSON
   * datasets
   *
   */
  @Export("createTimeseriesChartById")
  public ChartPanel createTimeseriesChart(String id,
      JsArray<JsonDatasetJSO> jsonDatasets, int chartWidth, int chartHeight,
      ViewReadyCallback readyListener) {
    ChartPanel chart = createTimeseriesChart(DOM.getElementById(id),
        createDatasets(jsonDatasets), chartWidth, chartHeight, readyListener);
    return chart;
  }
 
  /**
  * Replace the datasets on a chart and redraw all the elements in the chart.
  * It is like doing a detach and a create of a graph but the performance is better specially 
  * with flash canvas.
  *  
  */
  @Export
  public ChartPanel replaceDatasets(ChartPanel chartPanel, JsArray<JsonDatasetJSO> jsonDatasets) {
    chartPanel.replaceDatasets(createDatasets(jsonDatasets));
    return chartPanel;
  }
  
  public static ChartPanel createTimeseriesChart(Dataset[] datasets,
      int chartWidth, int chartHeight) {
    return getInstance()
        .createChartPanel((Element) null, datasets, chartWidth, chartHeight,
            null);
  }
  
  public static ChartPanel createTimeseriesChartWithDatasetVarName(String... varname) {
    ChartPanel c = new ChartPanel();
    c.setDatasetVarName(varname);
    return c;
  }

  public static ChartPanel createTimeseriesChartWithDatasetsVarName(String name) {
    ChartPanel c = new ChartPanel();
    c.setDatasetsVarName(name);
    return c;
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
   */
  public static ChartPanel createTimeseriesChart(String id, Dataset[] datasets,
      int chartWidth, int chartHeight, ViewReadyCallback readyCallback) {
    return createTimeseriesChart(DOM.getElementById(id), datasets, chartWidth,
        chartHeight, readyCallback);
  }

  public DatasetReader getDatasetReader() {
    return datasetReader;
  }

  @Export
  public Dataset createDataset(JsonDatasetJSO json) {
    return datasetReader.createDatasetFromJson(new GwtJsonDataset(json));
  }

  @Export
  public Dataset createMutableDataset(JsonDatasetJSO json) {
    return datasetReader.createDatasetFromJson(new GwtJsonDataset(json), true);
  }

  /**
   * Parse a javascript array of JSON objects representing multiresolution
   * Datasets. <p> See {@link #createDataset(org.timepedia.chronoscope.client.browser.json.JsonDatasetJSO)}
   * for details of the format.
   */
  public Dataset[] createDatasets(JsArray<JsonDatasetJSO> jsonDatasets) {
    if (jsonDatasets == null) {
      return new Dataset[0];
    }

    int numDatasets = jsonDatasets.length();
    Dataset ds[] = new Dataset[numDatasets];
    for (int i = 0; i < numDatasets; i++) {
      ds[i] = datasetReader
          .createDatasetFromJson(new GwtJsonDataset(jsonDatasets.get(i)), true);
    }
    return ds;
  }

  public static void enableHistorySupport(boolean enabled) {
    ChronoscopeOptions.historySupport = enabled;
  }

  public static String generateId() {
    return "chart" + globalChartNumber++;
  }

  public static Chronoscope getInstance() {
    if(instance == null) {
      instance = get();
    }
    return instance;
  }

  public static String getURL(String url) {
    return Chronoscope.get().urlResolver.resolveURL(url);
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

  /**
   * Set's the default aggregate function used to create zoomed out data.
   * Default is "mean", other possible values are "min", "max", "extrema"
   */
  @Export
  public static void setDefaultAggregateFunction(String name) {
    ChronoscopeOptions.setDefaultAggregateFunction(name);
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
  public static void setVerticalCrosshair(boolean enabled) {
    ChronoscopeOptions.setVerticalCrosshairEnabled(enabled);
  }

  @Export
  public static void setHorizontalCrosshair(boolean enabled) {
    ChronoscopeOptions.setHorizontalCrosshairEnabled(enabled);
  }

  @Export
  public static void setCrosshairLabelsFormat(String format) {
    ChronoscopeOptions.setCrosshairDateTimeFormat(format);
  }


  @Export
  public static void setDefaultMultiaxisMode(boolean mode) {
    ChronoscopeOptions.setDefaultMultiaxisMode(mode);
  }

  @Export
  public static void setAnimationPreview(boolean enabled) {
    ChronoscopeOptions.setAnimationPreview(enabled);
  }

  @Export
  public static void isAnimationPreview(boolean enabled) {
    ChronoscopeOptions.isAnimationPreview();
  }

  public static void setMicroformatsEnabled(boolean microformatsEnabled) {
    Chronoscope.microformatsEnabled = microformatsEnabled;
  }

  /**
   * Maximum number of datapoints to attempt to render when not moving, before dropping to lower resolution.
   */
  @Export
  public static void setMaxStaticDatapoints(int max) {
    ChronoscopeOptions.setMaxStaticDatapoints(max);
  }
  
  /**
   * Maximum number of datapoints to attempt to render when animating, before dropping to lower resolution.
   */
  @Export
  public static void setMaxDynamicDatapoints(int max) {
    ChronoscopeOptions.setMaxDynamicDatapoints(max);
  }

  /**
   * The default is to fall back to a Flash canvas implementation when there isn't an HTML5 canvas available.
   */
  @Export
  public static boolean isFlashFallbackEnabled() {
    return ChronoscopeOptions.isFlashFallbackEnabled();
  }

  @Export
  public static void setFlashFallbackEnabled(boolean enabled) {
    ChronoscopeOptions.setFlashFallbackEnabled(enabled);
  }

//  @Export
//  public static void setOverviewVisible(boolean visible) {
//    ChronoscopeOptions.setOverviewVisibile(visible);
//  }




  /**
   * Legacy method to support old API. Preferred method is to use instance
   * passed to onChronoscopeLoaded, which allows behavior to be overriden.
   *
   * @deprecated
   */
  @Export @Deprecated
  public static ChartPanel createTimeseriesChartById(String id,
    JsArray<JsonDatasetJSO> jsonDatasets, int chartWidth, int chartHeight, ViewReadyCallback readyListener) {
    return getInstance().createTimeseriesChart(id, jsonDatasets, chartWidth, chartHeight, readyListener);
  }

  /**
   * Used to default to GWT.getModuleBaseURL() + "fr"
   * Deprecated once modern browsers supported text rotation
   */
  @Export @Deprecated
  public static void setFontBookServiceEndpoint(String endpoint) {
    fontBookServiceEndpoint = endpoint;
  }

  @Export @Deprecated
  public static void setFontBookRendering(boolean enabled) {
    fontBookRenderingEnabled = enabled;
  }

  @Deprecated
  public static String getFontBookServiceEndpoint() {
    return fontBookServiceEndpoint == null ? "http://api.timepedia.org/widget/"
        + "fr" : fontBookServiceEndpoint;
  }



  @Inject
  public void setUrlResolver(URLResolver urlr) {
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
  public ChartPanel createChartPanel(String id, JsArray<JsonDatasetJSO> datasets, ViewReadyCallback listener) {
    Element elem = DOM.getElementById(id);
    /*
    ChartPanel panel = createChartPanel(elem, createDatasets(datasets),
            DOM.getElementPropertyInt(elem, "clientWidth"),
            DOM.getElementPropertyInt(elem, "clientHeight"),
            listener);
    elem = null;
    return panel;*/

    int width = DOM.getElementPropertyInt(elem, "clientWidth");
    int height = DOM.getElementPropertyInt(elem, "clientHeight");
    return createChartPanel(elem, createDatasets(datasets), width, height, listener);
  }

  /**
   */
  @Export("createTimeseriesChartByIdSized")
  public ChartPanel createChartPanel(String id, Dataset[] datasets,
      int chartWidth, int chartHeight, ViewReadyCallback readyListener) {
    return createChartPanel(DOM.getElementById(id), datasets, chartWidth,
        chartHeight, readyListener);
  }

  @Inject
  public void setDatasetReader(DatasetReader datasetReader) {
    this.datasetReader = datasetReader;
  }

  /**
   */
  @Export("createTimeseriesChartWithElement")
  public ChartPanel createChartPanel(Element elem, Dataset[] datasets,
      int chartWidth, int chartHeight, final ViewReadyCallback readyListener) {

    final ChartPanel cpanel = newChartPanel();
    cpanel.setDatasets(datasets);
    cpanel.setDomElement(elem);
    cpanel.setDimensions(chartWidth, chartHeight);
    cpanel.setViewReadyCallback(new ViewReadyCallback() {
      // Wait until async gss has been loaded.
      public void onViewReady(View view) {
        if (readyListener != null) {
          readyListener.onViewReady(view);
        }
      }
    });
    cpanel.initialize();
    return cpanel;
  }

  protected void init() {
    try {
      if(alreadyRan) {
        return;
      }
      alreadyRan = true;
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
        e.printStackTrace();
      }
      throw new RuntimeException(e);
    }
  }

  protected ChartPanel newChartPanel() {
    return new ChartPanel();
  }

  protected void exportFunctions() {
    Exporter exporter = (Exporter) GWT.create(Chronoscope.class);

    Exporter dexporter = GWT.create(ArrayDataset2D.class);

    Exporter exporterMarker = (Exporter) GWT.create(Marker.class);

    Exporter exporterRangeMarker = (Exporter) GWT.create(RangeBarMarker.class);

    Exporter exporterDomainMarker = (Exporter) GWT.create(DomainBarMarker.class);

    Exporter exporter2 = (Exporter) GWT.create(DefaultXYPlot.class);

    Exporter exporter5 = (Exporter) GWT.create(BrowserChronoscopeMenu.class);

    Exporter exporter7 = (Exporter) GWT.create(DatasetRenderer.class);

    Exporter exporter4 = (Exporter) GWT.create(LineXYRenderer.class);

    Exporter exporter8 = (Exporter) GWT.create(IntTickFormatterFactory.class);

    Exporter exporter9 = (Exporter) GWT.create(DateTickFormatterFactory.class);

    Exporter exporterInc = (Exporter) GWT.create(IncrementalDatasetResponseImpl.class);

    Exporter exporterMut = (Exporter) GWT.create(MutableDatasetND.class);

//    Exporter exporter6 = (Exporter) GWT.create(BarChartXYRenderer.class);
//    exporter6.export();
//
    View v = (View) GWT.create(DOMView.class);
    ((DOMView) v).exportFunctions();
  }

  protected void onChronoscopeLoad() {
    try {
      chronoscopeLoaded(ExporterUtil.wrap(this));
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
