package org.timepedia.chronoscope.gviz.client;

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
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.StyleElement;

import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.data.ArrayXYDataset;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.client.overlays.OverlayClickListener;
import org.timepedia.chronoscope.gviz.client.theme.chrome.ThemeStyleInjector;

import java.util.ArrayList;

/**
 *
 */
@Gadget.ModulePrefs(title = "Chronoscope", author = "Ray Cromwell",
    author_email = "ray@timefire.com")
public class GVizChronoscopeWidget extends Gadget<GVizPreferences>
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
    ThemeStyleInjector.injectTheme();
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
    GoogleLoader.load(intrinsics, "visualization", "1", new Command() {

      public void execute() {
        Chronoscope.setErrorReporting(false);
        Chronoscope.setFontBookRendering(true);
        GadgetHelper.create().createQueryFromPrefs()
            .send(new QueryResponseHandler() {
              public void onQueryResponse(QueryResponse response) {
                try {
                  DataTable table = response.getDataTable();
                  XYDataset ds[] = parseDatasets(table);
                  final Marker ms[] = parseMarkers(table);

                  ChartPanel cp = Chronoscope.createTimeseriesChart(ds,
                      Window.getClientWidth(), Window.getClientHeight());
                  cp.setGssContext(new GVizGssContext());
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
                  RootPanel.get().add(new Label(
                      "There was an error parsing the spreadsheet data."));
                }
              }
            });
      }
    });

//    RootPanel.get().add(new Label("Hello"));

  }

  private Marker[] parseMarkers(DataTable table) {
    int startRow = -1;
    int curSeries = -1;
    ArrayList<Marker> markers = new ArrayList<Marker>();

    for (int row = 0; row < table.getNumberOfRows(); row++) {
      if (!Double.isNaN(table.getValueDate(row, 0))) {
        startRow = row;
        break;
      }
    }

    for (int i = 1; i < table.getNumberOfColumns(); i++) {

      if (!Double.isNaN(table.getValueNumber(startRow, i))) {
        curSeries++;
      } else {
        if ("markers".equalsIgnoreCase(table.getColumnLabel(i))) {
          for (int row = startRow; row < table.getNumberOfRows(); row++) {
            final Marker m = new Marker(table.getValueDate(row, 0), .0,
                "" + (char) ('A' + markers.size()), curSeries);
            final String info = table.getValueString(row, i);
            final String info2 = info != null ? info.trim() : "";

            if (!"".equals(info2)) {
              m.addOverlayClickListener(new OverlayClickListener() {
                public void onOverlayClick(Overlay overlay, int i, int i1) {
                  m.openInfoWindow(info2);
                }
              });
              markers.add(m);
            }
          }
        }
      }
    }
    return markers.toArray(new Marker[markers.size()]);
  }

  private XYDataset[] parseDatasets(DataTable table) {

    int startRow = -1;
    for (int row = 0; row < table.getNumberOfRows(); row++) {
      if (!Double.isNaN(table.getValueDate(row, 0))) {
        startRow = row;
        break;
      }
    }

    double domain[] = table2domain(table, startRow);
    int numCols = 0;
    for (int i = 1; i < table.getNumberOfColumns(); i++) {
      if (!Double.isNaN(table.getValueNumber(startRow, i))) {
        numCols++;
      }
    }

    XYDataset[] ds = new XYDataset[numCols];
    numCols = 0;
    for (int i = 1; i < table.getNumberOfColumns(); i++) {
      if (Double.isNaN(table.getValueNumber(startRow, i))) {
        continue;
      }
      String label = table.getColumnLabel(i);
      if (label == null || "".equals(label)) {
        label = "Series " + numCols;
      }
      label = label.trim();
      int ind = label.indexOf("(");
      int end = label.indexOf(")");

      String units = label;
      if (ind != -1 && end != -1 && end > ind) {
        units = label.substring(ind + 1, end).trim();
        label = label.substring(0, ind);
      }

      double range[] = table2range(table, startRow, i);
      ds[numCols++] = new ArrayXYDataset("col" + i, domain, range, label,
          units);
    }

    return ds;
  }

  private double[] table2range(DataTable table, int startRow, int col) {
    double r[] = new double[table.getNumberOfRows() - startRow];
    for (int i = startRow; i < r.length; i++) {
      r[i] = table.getValueNumber(i, col);
    }
    return r;
  }

  private double[] table2domain(DataTable table, int startRow) {
    double d[] = new double[table.getNumberOfRows() - startRow];
    for (int i = startRow; i < d.length; i++) {
      d[i] = table.getValueDate(i, 0);
    }
    return d;
  }

  public void initializeFeature(IdiFeature feature) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void initializeFeature(LockedDomainFeature feature) {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
