package org.timepedia.chronoscope.client.data;

import com.google.gwt.core.client.JavaScriptObject;
import org.timepedia.chronoscope.client.XYDataSource;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.browser.Chronoscope;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public abstract class AbstractXYDataSource extends XYDataSource {
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

        XYDataset datasets[] = new XYDataset[numseries];
        for (int i = 0; i < datasets.length; i++) {
            datasets[i] = new ArrayXYDataset(ids[i], domains[i], ranges[i], ids[i], "axis" + i);
        }
        async.onSuccess(datasets);
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

    protected void parseJSON(String text, DataSourceCallback async) {
        async.onSuccess(Chronoscope.createXYDatasets(getJson(text)));
    }

    private static native JavaScriptObject getJson(String text) /*-{
       var a=eval(text);
       if(a instanceof Array) return a;
       return [a];
    }-*/;

    protected void parseXML(String text, DataSourceCallback async) {

    }
}
