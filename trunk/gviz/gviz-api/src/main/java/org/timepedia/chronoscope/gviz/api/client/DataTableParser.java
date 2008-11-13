package org.timepedia.chronoscope.gviz.api.client;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.i18n.client.NumberFormat;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.ComponentFactory;
import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.data.DatasetFactory;
import org.timepedia.chronoscope.client.data.DatasetRequest;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.client.overlays.OverlayClickListener;
import org.timepedia.chronoscope.client.util.DateParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 *
 */
public class DataTableParser {
  
  static class DataPair {

    public double domain[];

    public double range[];
  }

  public static Dataset[] parseDatasets(DataTable table,
      Map<Integer, Integer> dataset2Column) {

    int startRow = -1;
    for (int row = 0; row < table.getNumberOfRows(); row++) {
      if (!Double.isNaN(table.getValueDate(row, 0))) {
        startRow = row;
        break;
      }
    }

    int numCols = 0;
    for (int i = 1; i < table.getNumberOfColumns(); i++) {
      if (!Double.isNaN(table.getValueNumber(startRow, i))) {
        numCols++;
      }
    }

    DatasetFactory dsFactory = ComponentFactory.get().getDatasetFactory();

    Dataset[] ds = new Dataset[numCols];
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

      DataPair pair = table2datapair(table, startRow, i);
      sortAscendingDate(pair);
      
      DatasetRequest.Basic request = new DatasetRequest.Basic();
      request.addTupleSlice(pair.domain);
      request.addTupleSlice(pair.range);
      request.setIdentifier("col" + i);
      request.setLabel(label);
      request.setAxisId(units);
      ds[numCols++] = dsFactory.create(request);
      
      if (dataset2Column != null) {
        dataset2Column.put(numCols - 1, i);
      }
    }

    return ds;
  }

  public static Marker[] parseMarkers(final JavaScriptObject eventSource,
      final DataTable table, Map<Integer, Integer> dataset2Column) {
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
            final Marker m = new Marker(table.getValueDate(row, 0),
                "" + (char) ('A' + markers.size()), curSeries);
            final String info = table.getValueString(row, i);
            final String info2 = info != null ? info.trim() : "";

            if (!"".equals(info2)) {
              m.addOverlayClickListener(new OverlayClickListener() {
                public void onOverlayClick(Overlay overlay, int i, int i1) {
                  m.openInfoWindow(info2);
                  GVizEventHelper
                      .trigger(eventSource, GVizEventHelper.SELECT_EVENT, null);
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

  public static DataPair table2datapair(DataTable table, int startRow,
      int col) {
    DataPair pair = new DataPair();

    int rows = 0;
    for (int i = startRow; i < table.getNumberOfRows(); i++) {
      double val = table.getValueNumber(i, col);
      if (!Double.isNaN(val)) {
        rows++;
      }
    }

    int row = 0;
    pair.range = new double[rows];
    pair.domain = new double[rows];

    for (int i = startRow; i < table.getNumberOfRows(); i++) {
      double val = table.getValueNumber(i, col);
      if (!Double.isNaN(val)) {
        pair.range[row] = val;
        pair.domain[row] = table.getValueDate(i, 0);
        row++;
      }
    }

    return pair;
  }

  public static double[] table2domain(DataTable table, int startRow) {
    double d[] = new double[table.getNumberOfRows() - startRow];
    for (int i = startRow; i < table.getNumberOfRows(); i++) {
      d[i] = table.getValueDate(i, 0);
    }
    return d;
  }

  static DataTable parseMicroformatIntoDataTable(String id) {
    Element e = Document.get().getElementById(id);

    assertTrue("table".equalsIgnoreCase(e.getNodeName()),
        "Table Element with id " + id + " doesn't exist.");

    TableElement te = TableElement.as(e);
    TableSectionElement thead = te.getTHead();
    assertNotNull(thead, "Table must contain THEAD element");
    NodeList<TableRowElement> hrows = thead.getRows();
    assertTrue(hrows.getLength() == 1, "Table THEAD must contain 1 TR element");

    int numCols = 0;

    DataTable table = DataTable.create();

    if (hrows.getLength() == 1) {
      TableRowElement tr = hrows.getItem(0);
      NodeList<TableCellElement> hcols = tr.getCells();
      assertTrue(hcols.getLength() > 1,
          "THEAD TR contains less than 2 columns");
      for (int i = 0; i < hcols.getLength(); i++) {
        TableCellElement th = hcols.getItem(i);
        assertTrue("th".equalsIgnoreCase(th.getNodeName()),
            "Only TH elements should occur in THEAD TR");
        String title = th.getInnerText().trim();
        numCols++;
        if (i == 0) {
          table.addColumn("date", title);
        } else {
          if ("markers".equalsIgnoreCase(title)) {
            table.addColumn("string", title);
          } else {
            table.addColumn("number", title);
          }
        }
      }
    } else {
      throw new JavaScriptException("Table Element must ");
    }

    String dateFormat = "MM-dd-yy";
    NumberFormat numberFormats[] = new NumberFormat[numCols];

    NodeList<Element> colGroup = te.getElementsByTagName("colgroup");
    assertTrue(colGroup != null && colGroup.getLength() == 1,
        "Table must have exactly one COLGROUP element");

    NodeList<Element> cols = colGroup.getItem(0).getElementsByTagName("col");
    assertTrue(cols != null && cols.getLength() == numCols,
        "COLGROUP must have one COL element for each TH in THEAD");

    for (int i = 0; i < cols.getLength(); i++) {
      Element col = cols.getItem(i);
      String fmt = col.getAttribute("title");
      String className = col.getClassName();
      if (i == 0) {
        assertTrue(fmt != null && !"".equals(fmt),
            "COL for column 0 must have TITLE attribute containing date");
        assertTrue("cmf-dateformat".equals(className),
            "COL for column 0 must have CLASS of cmf-dateformat");
      }
      if (i == 0) {
        dateFormat = fmt;
      } else {
        if (fmt != null && !"".equals(fmt)) {
          assertTrue("cmf-numberformat".equals(className),
              "Number format COL elements must have class of cmf-numberformat with title containing format according to GWT NumberFormat syntax at http://google-web-toolkit.googlecode.com/svn/javadoc/1.4/com/google/gwt/i18n/client/NumberFormat.html");
          numberFormats[i] = NumberFormat.getFormat(fmt);
        } else if ("cmf-numberformat".equals(className)) {
          assertTrue(fmt != null && !"".equals(fmt),
              "COL has class cmf-numberformat but missing title attribute with format string with syntax http://google-web-toolkit.googlecode.com/svn/javadoc/1.4/com/google/gwt/i18n/client/NumberFormat.html");
          numberFormats[i] = NumberFormat.getFormat(fmt);
        }
      }
    }

    NodeList<TableSectionElement> tbodies = te.getTBodies();
    assertNotNull(tbodies, "Table must contain TBODY elements");
    assertTrue(tbodies.getLength() > 0, "Table must contain TBODY elements");
    int totalAdded = 0;
    for (int i = 0; i < tbodies.getLength(); i++) {
      TableSectionElement tbody = tbodies.getItem(i);
      NodeList<TableRowElement> drows = tbody.getRows();
      table.addRows(drows.getLength());
      for (int j = 0; j < drows.getLength(); j++) {
        TableRowElement row = drows.getItem(j);
        NodeList<TableCellElement> cells = row.getCells();
        assertTrue(cells.getLength() == numCols,
            "Number of TH header columns in THEAD must match number of TD columns in TBODY");

        for (int k = 0; k < cells.getLength(); k++) {
          TableCellElement cell = cells.getItem(k);
          if (k == 0) {
            table.setValueDate(totalAdded, k,
                DateParser.parse(dateFormat, cell.getInnerText().trim()));
          } else {
            if ("string".equals(table.getColumnType(k))) {
              table.setValue(totalAdded, k, cell.getInnerText().trim());
            } else {
              String cellText = cell.getInnerText().trim();
              try {
                double value = numberFormats[k] == null ? Double
                    .parseDouble(cellText) : numberFormats[k].parse(cellText);
                table.setValue(totalAdded, k, value);
              } catch (NumberFormatException e1) {
                // TODO: (ray) ? silently ignore parse errors
              }
            }
          }
        }
        totalAdded++;
      }
    }
    return table;
  }

  private static void assertNotNull(Object obj, String msg) {
    if (obj == null) {
      throw new JavaScriptException(msg);
    }
  }

  private static void assertTrue(boolean cont, String msg) {
    if (!cont) {
      throw new JavaScriptException(msg);
    }
  }

  public static class Pair implements Comparable<Pair> {

       public double x, y;

       public Pair(double x, double y) {
         this.x = x;
         this.y = y;
       }

       public int compareTo(Pair o) {
         return (int) (this.x - o.x);
       }
     }

  private static void sortAscendingDate(DataPair pair) {

    Pair[] p = new Pair[pair.domain.length];
    for (int i = 0; i < p.length; i++) {
      p[i] = new Pair(pair.domain[i], pair.range[i]);
    }
    Arrays.sort(p);
    for (int i = 0; i < p.length; i++) {
      pair.domain[i] = p[i].x;
      pair.range[i] = p[i].y;
    }
  }
}
