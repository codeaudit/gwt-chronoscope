package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.*;
import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.XYDataSource;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlotListener;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.data.ArrayXYDataset;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.overlays.DomainBarMarker;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.client.overlays.RangeBarMarker;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.Exporter;
import org.timepedia.exporter.client.ExporterBase;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Factory class and JS API interface for Chronoscope Charts
 * <p/>
 * This class exports methods that can be used by both Java and JS to create and configure charts, as well as being
 * a global sink for History events
 *
 * @gwt.exportPackage chronoscope
 */
public class Chronoscope implements Exportable, HistoryListener {


    static {
        XYDataSource.setFactory(new BrowserXYDataSourceFactory());
    }

    private static final HashMap charts = new HashMap();

    /**
     * Used to prevent double-triggering of history events
     */
    private static String previousHistory;


    /**
     * Request that Chronoscope pick the best canvas implementation for the current browser
     *
     * @gwt.export
     */
    public static final int TYPE_AUTO = 0,

            /**
             * Request that Chronoscope use a Safari-style CANVAS for rendering
             *
             * @gwt.export
             */
            TYPE_CANVAS = 1, /**
     * Request that Chronoscope use server-side image rendering combined with client-side panning
     *
     * @gwt.export
     */
    TYPE_HYBRID = 2, /**
     * Request that Chronoscope use Flash for rendering
     *
     * @gwt.export
     */
    TYPE_FLASH = 3, /**
     * Request that Chronoscope use Silverlight for rendering
     *
     * @gwt.export
     */
    TYPE_SILVERLIGHT = 4, /**
     * Request Chronoscope use a Java applet for rendering
     *
     * @gwt.export
     */
    TYPE_APPLET = 5, /**
     * Request that Chronoscope render the chart as a static image with clickable imagemap for zooms
     *
     * @gwt.export
     */
    TYPE_IMAGEMAP = 6;

    private static boolean microformatsEnabled = false;
    private static boolean showCreditsEnabled = true;
    private static boolean fontBookRenderingEnabled = false;
    private static boolean errorReportingEnabled = true;
    private static String fontBookServiceEndpoint;
    private static boolean historySupport = false;

    protected void exportFunctions() {
        Exporter exporter = (Exporter) GWT.create(Chronoscope.class);
        exporter.export();

        Exporter exporter2 = (Exporter) GWT.create(DefaultXYPlot.class);
        exporter2.export();

        Exporter exporter5 = (Exporter) GWT.create(BrowserChronoscopeMenu.class);
        exporter5.export();

        Exporter exporter3 = (Exporter) GWT.create(BrowserView.class);
        exporter3.export();
    }


    /**
     * Invoked after Chronoscope has exported the JS API and parsed microformats. An instance of Chronoscope
     * is passed to a Javascript callback.
     *
     * @param chronoscope
     */
    private native void chronoscopeLoaded(JavaScriptObject chronoscope) /*-{
        if($wnd.onChronoscopeLoaded)
          $wnd.onChronoscopeLoaded(chronoscope);
    }-*/;


    /**
     * A factory function to create a push-pin marker given a Date, then the dataset index to attach this marker to,
     * and a label
     *
     * @param date
     * @param seriesNum
     * @param label
     * @return
     * @gwt.export
     */
    public static Marker createMarker(String date, int seriesNum, String label) {
        return new Marker(date, seriesNum, label);
    }


    /**
     * A factory function to create a vertical marker given start and end dates, and a label;
     *
     * @param startDate
     * @param endDate
     * @param label
     * @return
     * @gwt.export
     */
    public static DomainBarMarker createBarMarker(String startDate, String endDate, String label) {
        return new DomainBarMarker(startDate, endDate, label);
    }

    /**
     * A factory function to create a horizontal span marker between two range values, with a given label.
     *
     * @param rangeLow
     * @param rangeHigh
     * @param label
     * @return
     * @gwt.export
     */
    public static RangeBarMarker createHorizontalBarMarker(double rangeLow, double rangeHigh, String label) {
        return new RangeBarMarker(rangeLow, rangeHigh, label);
    }

    /**
     * Parse a javascript array of JSON objects representing multiresolution XYDatasets
     * <p/>
     * See {@link #createXYDataset} for details of the format
     *
     * @param datasets
     * @return
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

    /**
     * Parse a JSON object representing a multiresolution XYDataset into a class implementing the XYDataset interface
     * <p/>
     * <p/>
     * The JSON format is as follows:
     * <pre>
     * dataset = {
     *    id: "unique id for this dataset",
     *    mipped: true,
     *    domain: [ [level 0 values], [level 1 values], ... ],
     *    range: [ [level 0 values], [level 1 values], ... ],
     *    rangeBottom: min over level 0 values,
     *    rangeTop: max over level 0 values,
     *    label: "default label for this dataset",
     *    axis: "an axis identifier (usually units). Datasets with like axis ids share the same range Axis"
     * }
     * </pre>
     *
     * @param json
     * @return
     * @gwt.export
     */
    public static XYDataset createXYDataset(JavaScriptObject json) {

        JavaScriptObject domain = JavascriptHelper.jsPropGet(json, "domain");
        JavaScriptObject range = JavascriptHelper.jsPropGet(json, "range");
        String mipped = JavascriptHelper.jsPropGetString(json, "mipped");

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
            return new ArrayXYDataset(JavascriptHelper.jsPropGetString(json, "id"), domains, ranges,
                                      JavascriptHelper.jsPropGetD(json, "rangeTop"), JavascriptHelper.jsPropGetD(json,
                                                                                                                 "rangeBottom"),
                                      JavascriptHelper.jsPropGetString(json, "label"), JavascriptHelper.jsPropGetString(
                    json, "axis"));

        } else {
            double domainVal[] = getArray(domain, 1000);
            double rangeVal[] = getArray(range, 1);
            return new ArrayXYDataset(JavascriptHelper.jsPropGetString(json, "id"), domainVal, rangeVal,
                                      JavascriptHelper.jsPropGetString(json, "label"), JavascriptHelper.jsPropGetString(
                    json, "axis")


            );
        }

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
     * Given the ID of the DOM element containing the chart, we construct a GssContext
     *
     * @param id
     * @return
     */
    protected static GssContext createGssContext(String id) {
        return new CssGssContext(id);
    }


    public void onHistoryChanged(String historyToken) {

        if (historyToken != null && historyToken.equals(previousHistory)) {
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


    public void init() {

        exportFunctions();

        onChronoscopeLoad();
        if (Chronoscope.isMicroformatsEnabled()) {
            Microformats.initializeMicroformats(this);
        }
        
        if (isHistorySupportEnabled()) {
            initHistory();
        }

    }

    private void initHistory() {
        History.addHistoryListener(this);
        String initToken = History.getToken();
        if (initToken.length() > 0) {
            onHistoryChanged(initToken);
        }
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

    /**
     * Create a chart inside the given DOM element with the given JSON datasets
     *
     * @param elem
     * @param jsonDatasets
     * @return
     * @gwt.export createTimeseriesChartByElement
     */
    public static ChartPanel createTimeseriesChart(Element elem, JavaScriptObject jsonDatasets, int chartWidth,
                                                   int chartHeight) {
        return createTimeseriesChart(elem, createXYDatasets(jsonDatasets), chartWidth, chartHeight);
    }

    /**
     * Create a chart inside the DOM element with the given ID with the given JSON datasets
     *
     * @param id
     * @param jsonDatasets
     * @param readyListener
     * @return
     * @gwt.export createTimeseriesChartById
     */
    public static ChartPanel createTimeseriesChart(String id, JavaScriptObject jsonDatasets, int chartWidth,
                                                   int chartHeight, ViewReadyCallback readyListener) {
        ChartPanel chart = createTimeseriesChart(DOM.getElementById(id), createXYDatasets(jsonDatasets), chartWidth,
                                                 chartHeight, readyListener);
        chart.onAttach();
        return chart;
    }

    public static ChartPanel createTimeseriesChart(XYDataset[] datasets, int chartWidth, int chartHeight) {
        return new ChartPanel(datasets, chartWidth, chartHeight);
    }

//    public static ChartPanel createStackedTimeseriesChart(XYDataset[] datasets, XYDataset[] datasets2) {
//        Chart chart = new Chart();
//        DefaultXYPlot top = new DefaultXYPlot(chart, datasets, true);
//        DefaultXYPlot bot = new DefaultXYPlot(chart, datasets2, true);
//        SharedDomainXYPlot shared = new SharedDomainXYPlot(0.5, top, bot, null);
//        return new ChartPanel(shared);
//    }

    public static ChartPanel createTimeseriesChart(Element elem, XYDataset[] datasets, int chartWidth,
                                                   int chartHeight) {
        return createTimeseriesChart(elem, datasets, chartWidth, chartHeight, null);
    }

    public static ChartPanel createTimeseriesChart(Element elem, XYDataset[] datasets, int chartWidth, int chartHeight,
                                                   ViewReadyCallback readyListener) {
        return new ChartPanel(elem, datasets, chartWidth, chartHeight, readyListener);
    }

    public static ChartPanel createTimeseriesChart(String elementId, XYDataset[] datasets, int chartWidth,
                                                   int chartHeight) {
        return createTimeseriesChart(elementId, datasets, chartWidth, chartHeight, null);
    }

    public static Chart getChartById(String id) {
        return (Chart) charts.get(id);
    }

    public static boolean isMicroformatsEnabled() {
        return microformatsEnabled;
    }

    public static void setMicroformatsEnabled(boolean microformatsEnabled) {
        Chronoscope.microformatsEnabled = microformatsEnabled;
    }

    private static int globalChartNumber = 0;

    public static String generateId() {
        return "ZZchrono" + globalChartNumber++;
    }

    public static void putChart(String id, Chart chart) {
        charts.put(id, chart);
        chart.setChartId(id);
    }

    /**
     * @param id
     * @param datasets
     * @param readyCallback
     * @return
     */
    public static ChartPanel createTimeseriesChart(String id, XYDataset[] datasets, int chartWidth, int chartHeight,
                                                   ViewReadyCallback readyCallback) {
        return new ChartPanel(DOM.getElementById(id), datasets, chartWidth, chartHeight, readyCallback);
    }

    public static boolean isShowCreditsEnabled() {
        return showCreditsEnabled;
    }

    /**
     * @param enabled
     * @gwt.export
     */
    public static void setShowCredits(boolean enabled) {
        showCreditsEnabled = enabled;
    }

    public static boolean isFontBookRenderingEnabled() {
        return fontBookRenderingEnabled;
    }

    /**
     * @param enabled
     * @gwt.export
     */
    public static void setFontBookRendering(boolean enabled) {
        fontBookRenderingEnabled = enabled;
    }

    public static boolean isErrorReportingEnabled() {
        return errorReportingEnabled;
    }

    /**
     * @param enabled
     * @gwt.export
     */
    public static void setErrorReporting(boolean enabled) {
        errorReportingEnabled = enabled;
    }

    public static String getFontBookServiceEndpoint() {
        return fontBookServiceEndpoint == null ? GWT.getModuleBaseURL() + "fr" : fontBookServiceEndpoint;
    }

    /**
     * Defaults to GWT.getModuleBaseURL() + "fr"
     *
     * @param endpoint
     * @gwt.export
     */
    public static void setFontBookServiceEndpoint(String endpoint) {
        fontBookServiceEndpoint = endpoint;
    }

    public static void enableHistorySupport(boolean enabled) {
        historySupport = enabled;
    }

    public static boolean isHistorySupportEnabled() {
        return historySupport;
    }

    public static void initialize() {
        Chronoscope instance = new Chronoscope();
        instance.init();
    }
}
