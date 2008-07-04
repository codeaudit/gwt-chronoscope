package org.timepedia.chronoscope.gviz.gadget.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.gadgets.client.Gadget;
import com.google.gwt.gadgets.client.IntrinsicFeature;
import com.google.gwt.gadgets.client.NeedsIntrinsics;
import com.google.gwt.gadgets.client.NeedsSetPrefs;
import com.google.gwt.gadgets.client.NeedsSetTitle;
import com.google.gwt.gadgets.client.SetPrefsFeature;
import com.google.gwt.gadgets.client.SetTitleFeature;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.StyleElement;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.gviz.api.client.GoogleLoader;
import org.timepedia.chronoscope.gviz.gadget.client.GadgetHelper;
import org.timepedia.chronoscope.gviz.api.client.QueryResponseHandler;
import org.timepedia.chronoscope.gviz.api.client.QueryResponse;
import org.timepedia.chronoscope.gviz.api.client.DataTable;
import org.timepedia.chronoscope.gviz.api.client.GVizGssContext;
import org.timepedia.chronoscope.gviz.api.client.DataTableParser;
import org.timepedia.chronoscope.gviz.api.client.Query;

/**
 *
 */
@Gadget.ModulePrefs(title = "Chronoscope", author = "Timefire.com",
    author_email = "support+gadget@timefire.com", 
    author_aboutme="Timefire is a company focused on organizing the world's historical data.",
    author_link="http://timepedia.org/chronoscope/gviz",
    author_location="Mountain View, CA",
    author_quote="Time is the fire in which we burn.",
    description = "The Chronoscope Annotated Timeseries Gadget is a Google Visualization Gadget capable of rendering very large timeseries, Google Maps style markers, and multiple timeseries on multiple axes.",
    directory_title="Chronoscope Timeseries Gadget",
    title_url="http://timepedia.org/chronoscope",
    screenshot = "http://timepedia.org/chronoscope/docs/gviz/screenshot.png",
    thumbnail = "http://timepedia.org/chronoscope/docs/gviz/screenshot-120x60.png"
    )
public class GVizChronoscopeGadget extends Gadget<GVizPreferences>
    implements NeedsIntrinsics, NeedsSetPrefs, NeedsSetTitle, NeedsIdi,
    NeedsLockedDomain {

  IntrinsicFeature intrinsics;

  SetPrefsFeature setPrefs;

  SetTitleFeature setTitle;

  public void initializeFeature(IntrinsicFeature feature) {
    intrinsics = feature;
  }

  public void initializeFeature(SetPrefsFeature feature) {
    this.setPrefs = feature;
  }

  public void initializeFeature(SetTitleFeature feature) {
    this.setTitle = feature;
  }

  protected void init(final GVizPreferences prefs) {
    Chronoscope.setUrlResolver(new Chronoscope.URLResolver() {
      public String resolveURL(String s) {
         return intrinsics.getCachedUrl(s);
      }
    });
    
    final HeadElement he = HeadElement.as(Document.get().getElementsByTagName("head").getItem(0));
    StyleElement se = Document.get().createStyleElement();
    se.setAttribute("href", "Chronoscope.css");
    se.setAttribute("src", "Chronoscope.css");
    he.appendChild(se);
    
    GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
      public void onUncaughtException(Throwable e) {
        RootPanel.get()
            .add(new Label("There was an error parsing the spreadsheet data."));
      }
    });
    GoogleLoader.load(new Chronoscope.URLResolver() {
      public String resolveURL(String url) {
        return intrinsics.getCachedUrl(url);
      }
    }, "visualization", "1", new Command() {

      public void execute() {
        Chronoscope.setErrorReporting(false);
        Chronoscope.setFontBookRendering(true);
        Query q=GadgetHelper.create().createQueryFromPrefs();
        
        q.send(new QueryResponseHandler() {
              public void onQueryResponse(QueryResponse response) {
                try {
                  setTitle.setTitle(prefs.chartTitle().getValue());
                  DataTable table = response.getDataTable();
                  XYDataset ds[] = DataTableParser.parseDatasets(table, null);
                  final Marker ms[] = DataTableParser.parseMarkers(table, table, null);

                  ChartPanel cp = Chronoscope.createTimeseriesChart(ds,
                      Window.getClientWidth(), Window.getClientHeight());
                  cp.setGssContext(prefs.chartStyle().getValue().getGssContext());
                  cp.setReadyListener(new ViewReadyCallback() {
                    public void onViewReady(View view) {
                      view.getChart().getPlot().setOverviewEnabled(prefs.overviewEnabled().getValue());
                      view.getChart().getPlot().setLegendEnabled(prefs.legendEnabled().getValue());
                      for (Marker m : ms) {
                        view.getChart().getPlot().addOverlay(m);
                      }
                      view.getChart().reloadStyles();
                      view.getChart().redraw();
                    }
                  });
                  RootPanel.get().add(cp);
                } catch (Throwable e) {
                  setTitle.setTitle("Chronoscope: Error in Data");
                  RootPanel.get().add(new HTML(
                      "There was an error parsing the spreadsheet data. See <a href=\"http://spreadsheets.google.com/pub?key=pChH4pq_9_M63rOylnhf0KQ\">this Google Spreadsheet</a> for an example how of to format the data correctly. The data range must include the header rows as well as the data rows."));
                }
              }
            });
      }
    });

//    RootPanel.get().add(new Label("Hello"));

  }

  public void initializeFeature(IdiFeature feature) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void initializeFeature(LockedDomainFeature feature) {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
