package org.timepedia.chronoscope.gviz.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.gadgets.client.Gadget;
import com.google.gwt.gadgets.client.IntrinsicFeature;
import com.google.gwt.gadgets.client.NeedsIntrinsics;
import com.google.gwt.gadgets.client.NeedsSetPrefs;
import com.google.gwt.gadgets.client.NeedsSetTitle;
import com.google.gwt.gadgets.client.SetPrefsFeature;
import com.google.gwt.gadgets.client.SetTitleFeature;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.data.MockXYDataset;
import org.timepedia.chronoscope.client.data.ArrayXYDataset;
import org.timepedia.chronoscope.client.gss.MockGssContext;

/**
 *
 */
@Gadget.ModulePrefs(title = "Chronoscope", author = "Ray Cromwell",
    author_email = "ray@timefire.com")
public class GVizChronoscopeWidget extends Gadget<ChronoscopePreferences>
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

  

  protected void init(final ChronoscopePreferences prefs) {
    GoogleLoader.load(intrinsics, "visualization", "1", new Command() {

      public void execute() {
        Chronoscope.setErrorReporting(false);
        Chronoscope.setFontBookRendering(true);
        GadgetHelper.create().createQueryFromPrefs()
            .send(new QueryResponseHandler() {
              public void onQueryResponse(QueryResponse response) {
                DataTable table = response.getDataTable();
                double domain[] = table2domain(table);
                XYDataset[] ds = new XYDataset[table.getNumberOfColumns()-1];
                for(int i=1; i<table.getNumberOfColumns(); i++) {
                  double range[] = table2range(table, i);
                  ds[i-1] = new ArrayXYDataset("col"+i, domain, range,
                      "Series "+i, "Axis "+i);
                }
               
                ChartPanel cp = Chronoscope.createTimeseriesChart(ds,
                    Window.getClientWidth(), Window.getClientHeight());
                cp.setGssContext(new MockGssContext());
                RootPanel.get().add(cp);
              }

              
            });
      }
    });

//    RootPanel.get().add(new Label("Hello"));

  }

  private double[] table2range(DataTable table, int col) {
    double r[] = new double[table.getNumberOfRows()];
    for(int i=0; i<r.length; i++) {
      r[i]=table.getValueNumber(i, col);
    }
    return r;
  }

  private double[] table2domain(DataTable table) {
    double d[] = new double[table.getNumberOfRows()];
    for(int i=0; i<d.length; i++) {
      d[i]=table.getValueDate(i, 0);
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
