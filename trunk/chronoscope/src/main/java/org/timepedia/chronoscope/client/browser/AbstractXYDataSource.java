package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.JsArray;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.ComponentFactory;
import org.timepedia.chronoscope.client.XYDataSource;
import org.timepedia.chronoscope.client.data.DataSourceCallback;
import org.timepedia.chronoscope.client.data.DatasetFactory;
import org.timepedia.chronoscope.client.data.DatasetRequest;
import org.timepedia.chronoscope.client.browser.json.JsonDatasetJSO;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public abstract class AbstractXYDataSource extends XYDataSource {

  private static native JsArray<JsonDatasetJSO> getJson(String text) /*-{
       var a=eval(text);
       if(a instanceof Array) return a;
       return [a];
    }-*/;

  protected String uri;

  public AbstractXYDataSource(String uri) {

    this.uri = uri;
  }

  protected void parseCSV(String text, DataSourceCallback async) {
    if (text == null || "".equals(text)) {
      async.onFailure(new Exception("No data returned"));
      return;
    }

    String lines[] = text.split("\n");

    if (lines.length < 2) {
      async.onFailure(new Exception(
          "Less than two lines of CSV data, minimum CSV is a header row, and 1 data row"));
      return;
    }

    String labels[] = lines[0].split("\\s*,\\s*");
    ArrayList rows = new ArrayList();

    for (int i = 1; i < lines.length; i++) {
      parseCSVrow(lines[i], rows);
    }

    // find number of data columns (non-null)
    int numseries = -1;
    ArrayList firstRow = (ArrayList) rows.get(0);
    String ids[] = new String[labels.length];

    int l = -1;
    for (Iterator iterator = firstRow.iterator(); iterator.hasNext();) {
      Object o = iterator.next();
      if (o != null) {
        numseries++;
        if (numseries < ids.length) {
          ids[numseries] = labels[++l];
        }
      }
    }

    double domains[][] = new double[numseries][];
    double ranges[][] = new double[numseries][];
    for (int i = 0; i < numseries; i++) {
      domains[i] = new double[rows.size()];
      ranges[i] = new double[rows.size()];
    }
    for (int row = 1; row < rows.size(); row++) {
      ArrayList arow = (ArrayList) rows.get(row);
      String date = (String) arow.get(0);
      date = date.replace('-', '/');

      double dval = Date.parse(date);

      for (int i = 0; i < numseries; i++) {
        domains[i][row] = dval;
      }

      int col = 0;
      for (int i = 1; i < numseries; i++) {
        String val = (String) arow.get(i);
        if (val != null) {
          ranges[col++][row] = Double.parseDouble(val);
        }
      }
    }

    DatasetFactory dsFactory = ComponentFactory.get().getDatasetFactory();
    
    Dataset datasets[] = new Dataset[numseries];
    for (int i = 0; i < datasets.length; i++) {
      DatasetRequest.Basic request = new DatasetRequest.Basic();
      request.setDomain(domains[i]);
      request.addRangeTupleSlice(ranges[i]);
      request.setIdentifier(ids[i]);
      request.setRangeLabel(ids[i]);
      request.setAxisId("axis" + i);
      
      datasets[i] = dsFactory.create(request);
    }
    async.onSuccess(datasets);
  }

  protected void parseJSON(String text, DataSourceCallback async) {
    async.onSuccess(Chronoscope.getInstance().createDatasets(getJson(text)));
  }

  protected void parseXML(String text, DataSourceCallback async) {
  }

  private void parseCSVrow(String line, ArrayList rows) {
    if (line == null || "".equals(line)) {
      return;
    }
    String cols[] = line.split("\\s*,\\s*");
    ArrayList row = new ArrayList();
    for (int i = 0; i < cols.length; i++) {
      if (cols[i] != null && !cols[i].trim().equals("")) {
        row.add(cols[i]);
      } else {
        row.add(null);
      }
    }
    rows.add(row);
  }
}
