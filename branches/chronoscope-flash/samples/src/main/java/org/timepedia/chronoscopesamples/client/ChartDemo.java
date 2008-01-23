package org.timepedia.chronoscopesamples.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.*;
import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.overlays.DomainBarMarker;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.client.overlays.OverlayClickListener;
import org.timepedia.chronoscope.client.overlays.RangeBarMarker;

/**
 * @author Ray Cromwell <ray@timepedia.org>
 */
public class ChartDemo implements EntryPoint {
    private static final String TIMEPEDIA_FONTBOOK_SERVICE = "http://api.timepedia.org/fr";

    public void onModuleLoad() {

        double GOLDEN_RATIO = 1.618;
        // you must specify the chart dimensions for now, rather than have the chart grow to fill its container
        int chartWidth = 600, chartHeight = (int) ( chartWidth / GOLDEN_RATIO );

        Chronoscope.enableHistorySupport(true);
        Chronoscope.setFontBookRendering(true);
        Chronoscope.initialize();

//        TabPanel vp = new TabPanel();
        VerticalPanel vp=new VerticalPanel();
        XYDataset[] ds = new XYDataset[1];
        ds[0] = Chronoscope.createXYDataset(getJson("unratedata"));
        ChartPanel chartPanel = Chronoscope.createTimeseriesChart(ds, chartWidth, chartHeight);
//        vp.add(chartPanel, "Simple XYDataSource");
        vp.add(chartPanel);

//        XYDataset[] ds2 = new XYDataset[2];
//        ds2[0] = Chronoscope.createXYDataset(getJson("unratedata"));
//        ds2[1] = Chronoscope.createXYDataset(getJson("dffdata"));
//        ChartPanel chartPanel2 = Chronoscope.createTimeseriesChart(ds2, chartWidth, chartHeight);
//        vp.add(chartPanel2, "Two XYDataSources on separtate axes");


//        XYDataset[] ds4 = new XYDataset[1];
//        ds4[0] = Chronoscope.createXYDataset(getJson("unratedata"));
//        final ChartPanel chartPanel4 = Chronoscope.createTimeseriesChart(ds4, chartWidth, chartHeight);
//        Marker marker = new Marker("1975/10/10", 0, "A");
//        marker.addOverlayClickListener(new OverlayClickListener() {
//            public void onOverlayClick(Overlay overlay, int x, int y) {
//                ( (Marker) overlay ).openInfoWindow("You clicked on 'A'");
//            }
//        });
//        vp.add(chartPanel4, "Markers");
        RootPanel.get("chartdemo").add(vp);
        //currently, because of design issues in the initialization process,
        // these must come after attachment

//        chartPanel2.getChart().getPlot().setOverviewEnabled(false);
//
//        chartPanel4.getChart().getPlot().addOverlay(marker);
//        chartPanel4.getChart().getPlot().addOverlay(new RangeBarMarker(4.0, 6.0, "Desired Range"));
//        chartPanel4.getChart().getPlot().addOverlay(new DomainBarMarker("1970/1/1", "1979/12/31", "The 70s"));
//        chartPanel4.getChart().redraw();
//        vp.selectTab(0);

        // hack to fix current bug when chart is hidden with display:none in a tab panel
//        vp.addTabListener(new TabListener() {
//            public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
//                chartPanel4.getChart().redraw();
//                return true;
//            }

//            public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
//
//            }
//        });


    }

    private static native JavaScriptObject getJson(String varName) /*-{
       return $wnd[varName];   
    }-*/;
}
