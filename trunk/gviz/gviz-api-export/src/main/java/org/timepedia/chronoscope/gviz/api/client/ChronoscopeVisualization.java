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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import org.timepedia.chronoscope.client.Focus;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.XYPlotListener;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.browser.JavascriptHelper;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.data.DateParser;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExporterUtil;

import java.util.HashMap;
import java.util.Map;

import gwtquery.client.Properties;

/**
 *
 */
@ExportPackage("chronoscope")
public class ChronoscopeVisualization implements Exportable {

  private Element element;

  private ChartPanel cp;

  private boolean dontfire;

  private static int vizCount = 0;

  @Export("Visualization")
  public ChronoscopeVisualization(Element element) {
    this.element = element;
  }

  @Export
  public static DataTable microformatToDataTable(String id) {
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

  private static void assertTrue(boolean cont, String msg) {
    if (!cont) {
      throw new JavaScriptException(msg);
    }
  }

  private static void assertNotNull(Object obj, String msg) {
    if (obj == null) {
      throw new JavaScriptException(msg);
    }
  }

  @Export
  public JavaScriptObject getSelection() {
    Focus focus = cp.getChart().getPlot().getFocus();
    if (focus == null) {
      return JavaScriptObject.createArray();
    }

    return GVizEventHelper.selection(
        dataset2Column.get(focus.getDatasetIndex()), focus.getPointIndex());
  }

  @Export
  void setSelection(JavaScriptObject selection) {
    Properties sel = JavascriptHelper.jsArrGet(selection, 0).cast();
    dontfire = true;

    for (Map.Entry<Integer, Integer> e : dataset2Column.entrySet()) {
      if (e.getValue() == sel.getInt("col")) {
        Focus focus = new Focus();
        focus.setDatasetIndex(e.getKey());
        focus.setPointIndex(sel.getInt("row"));
        cp.getChart().getPlot().setFocus(focus);
      }
    }
    cp.getChart().redraw();
  }

  Map<Integer, Integer> dataset2Column = new HashMap<Integer, Integer>();

  @Export
  public void draw(final DataTable table, JavaScriptObject options) {
    String id = element.getId();
    if (id == null || "".equals(id)) {
      id = "__viz" + vizCount++;
      element.setId(id);
    }

    try {
      final Properties opts = options.cast();

      XYDataset ds[] = DataTableParser.parseDatasets(table, dataset2Column);
      final Marker ms[] = DataTableParser.parseMarkers(ExporterUtil.wrap(this), table, dataset2Column);

      cp = Chronoscope.createTimeseriesChart(ds,
          element.getPropertyInt("clientWidth"),
          element.getPropertyInt("clientHeight"));
      cp.setGssContext(new GVizGssContext());
      cp.setReadyListener(new ViewReadyCallback() {
        public void onViewReady(View view) {
          view.getChart().getPlot()
              .setOverviewEnabled(!"false".equals(opts.get("overview")));
          view.getChart().getPlot()
              .setLegendEnabled(!"false".equals(opts.get("legend")));
          for (Marker m : ms) {
            view.getChart().getPlot().addOverlay(m);
          }
          view.addViewListener(new XYPlotListener() {

            public void onContextMenu(int x, int y) {
              //To change body of implemented methods use File | Settings | File Templates.
            }

            public void onFocusPointChanged(XYPlot plot, int focusSeries,
                int focusPoint) {
              if (!dontfire) {
                GVizEventHelper
                    .trigger(ExporterUtil.wrap(ChronoscopeVisualization.this), GVizEventHelper.SELECT_EVENT, null);
              }
              dontfire = false;
            }

            public void onPlotMoved(XYPlot plot, double amt, int seriesNum,
                int type, boolean animated) {
              //To change body of implemented methods use File | Settings | File Templates.
            }
          });
          view.getChart().reloadStyles();
          view.getChart().redraw();
        }
      });

      RootPanel.get(id).add(cp);
    } catch (Throwable e) {
      RootPanel.get(id)
          .add(new Label("There was an error parsing the spreadsheet data. "));
    }
  }
}
