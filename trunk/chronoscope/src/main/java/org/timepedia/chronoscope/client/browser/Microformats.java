package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.data.DateParser;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.client.overlays.OverlayClickListener;

import java.util.Date;

/**
 * Helper class used to parse microformat annotations in the DOM and turn those
 * into charts.
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class Microformats {

  static Command microformatReadyListener;

  public static void setMicroformatsReadyListener(Command rdy) {
    microformatReadyListener = rdy;
  }

  private static class MicroformatViewReadyCallback
      implements ViewReadyCallback {

    private final Element[] links;

    private final String id;

    private String chartId;

    private final XYDataset[] ds;

    private final Element elt;

    private MicroformatCountdownLatch latch;

    public MicroformatViewReadyCallback(Element[] links, String id,
        String chartId, XYDataset[] ds, Element elt,
        MicroformatCountdownLatch latch) {
      this.links = links;
      this.id = id;
      this.chartId = chartId;
      this.ds = ds;
      this.elt = elt;
      this.latch = latch;
    }

    public void onViewReady(View view) {
      Chart chart = Chronoscope.getChartById(id);

      for (int j = 0; j < links
          .length; j++) {
        String href = DOM.getElementAttribute(links[j], "href");
        int hash = href != null ? href.indexOf("#") : -1;
        if (hash != -1) {
          href = href.substring(hash);
        }

        if (href != null && href.startsWith("#" + chartId)) {

          String date = href.substring(("#" + chartId + ":").length());
          String label = getAttribute(links[j], "accesskey");
          if (label == null) {
            char c[] = new char[1];
            c[0] = (char) ('A' + charCounter);

            label = new String(c);
          }
          final Element[] infoWindow = getElementsByClassName(links[j], "span",
              CMF_PREFIX + "-infowindow");
          final Marker m = new Marker((double) Date.parse(date),
              (ds[0].getRangeTop() - ds[0].getRangeBottom()) / 2, label, 0);
          attachOnClick(links[j], m);

          if (infoWindow != null && infoWindow[0] != null) {
            m.addOverlayClickListener(new OverlayClickListener() {
              public void onOverlayClick(Overlay overlay, int x, int y) {
                m.openInfoWindow(DOM.getInnerHTML(infoWindow[0]));
              }
            });
          }
          XYPlot plot = chart.getPlot();
          plot.addOverlay(m);
          String[] hdrs = getTableHeaders(elt);
          if (hdrs != null && hdrs.length > 1) {
            plot.getDomainAxis().setLabel(hdrs[0]);
            for (int k = 1; k < hdrs.length; k++) {
              plot.getRangeAxis(k - 1).setLabel(hdrs[k]);
            }
          }
        }
      }
      latch.decrement();
      chart.redraw();
    }
  }

  private static final char charCounter = 0;

  private static final String CMF_PREFIX = "cmf";

  private static final String CMF_CHART = CMF_PREFIX + "-chart";

  public static void clickMicroformatLink(Element link, Marker marker) {
    marker.click(1, 1);
  }

  static class MicroformatParseResults {

    public JsArrayString columns = JsArrayString.createArray().cast();

    public JsArray<JsArrayNumber> data = JsArray.createArray().cast();

    public void addColumn(String title) {
      columns.set(columns.length(), title);
    }

    public void setValue(int row, int col, double val) {
      JsArrayNumber arr = data.get(col);
      if (arr == null) {
        arr = JsArrayNumber.createArray().cast();
        data.set(col, arr);
      }
      arr.set(row, val);
    }

    public int getNumDatasets() {
      return data.length();
    }
  }

  static MicroformatParseResults parseMicroformat(String id) {
    com.google.gwt.dom.client.Element e = Document.get().getElementById(id);

    MicroformatParseResults results = new MicroformatParseResults();

    assertTrue("table".equalsIgnoreCase(e.getNodeName()),
        "Table Element with id " + id + " doesn't exist.");

    TableElement te = TableElement.as(e);
    TableSectionElement thead = te.getTHead();
    assertNotNull(thead, "Table must contain THEAD element");
    NodeList<TableRowElement> hrows = thead.getRows();
    assertTrue(hrows.getLength() == 1, "Table THEAD must contain 1 TR element");

    int numCols = 0;

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
          results.addColumn(title);
        } else {
          results.addColumn(title);
        }
      }
    } else {
      throw new JavaScriptException("Table Element must ");
    }

    String dateFormat = "MM-dd-yy";
    NumberFormat numberFormats[] = new NumberFormat[numCols];

    NodeList<com.google.gwt.dom.client.Element> colGroup = te
        .getElementsByTagName("colgroup");
    assertTrue(colGroup != null && colGroup.getLength() == 1,
        "Table must have exactly one COLGROUP element");

    NodeList<com.google.gwt.dom.client.Element> cols = colGroup.getItem(0)
        .getElementsByTagName("col");
    assertTrue(cols != null && cols.getLength() == numCols,
        "COLGROUP must have one COL element for each TH in THEAD");

    for (int i = 0; i < cols.getLength(); i++) {
      com.google.gwt.dom.client.Element col = cols.getItem(i);
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
      for (int j = 0; j < drows.getLength(); j++) {
        TableRowElement row = drows.getItem(j);
        NodeList<TableCellElement> cells = row.getCells();
        assertTrue(cells.getLength() == numCols,
            "Number of TH header columns in THEAD must match number of TD columns in TBODY");

        for (int k = 0; k < cells.getLength(); k++) {
          TableCellElement cell = cells.getItem(k);
          if (k == 0) {
            results.setValue(totalAdded, k,
                DateParser.parse(dateFormat, cell.getInnerText().trim()));
          } else {
            String cellText = cell.getInnerText().trim();
            try {
              double value = numberFormats[k] == null ? Double
                  .parseDouble(cellText) : numberFormats[k].parse(cellText);
              results.setValue(totalAdded, k, value);
            } catch (NumberFormatException e1) {
              // TODO: (ray) ? silently ignore parse errors
            }
          }
        }
        totalAdded++;
      }
    }
    return results;
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

  public static native JavaScriptObject importMicroformatTable0(String id,
      Element table) /*-{

          var label="";
  
          var thead = table.getElementsByTagName("thead");
          var labels=[], axes=[], dateformat=null;
          var colgroup = table.getElementsByTagName("colgroup");
          var rangeFormats = [];
          var i = 0;
          if(colgroup && colgroup.item(0)) {
            var cols = colgroup.item(0).getElementsByTagName("col");
            if(cols && cols.length > 0) {
              var datecol=cols.item(0);
              if(datecol.className == "cmf-dateformat") {
                dateformat = datecol.getAttribute('title');
              }
              for(i=1; i<cols.length; i++) {
                var rcol = cols.item(i);
                if(rcol.className == "cmf-numberformat") {
                  rangeFormats[i] = rcol.getAttribute("title");
                }
              }
            }
          }
          
          if(thead && thead.item(0)) {
            var r = thead.item(0).getElementsByTagName("tr");
            var c= r.item(0).getElementsByTagName("th");
            for(i=0; i<c.length; i++) {
               label = c.item(i).innerHTML;
               labels.push(label);
               var axis=c.item(i).getAttribute("axis");
               if(!axis) { axis="default"; }
               axes.push(axis);
            }
          }
          var tbody = table.getElementsByTagName("tbody");

          if(tbody && tbody.item(0))
          {
            var rowList = tbody.item(0).getElementsByTagName("tr");

            var i;
            var domain=[], range=[];
            var numCols = rowList.item(0).getElementsByTagName("td").length;
            for(i=0; i<numCols-1; i++) {
                range.push([]);
            }
            for(i = 0; i<rowList.length; i++)
            {
             var colList = rowList.item(i).getElementsByTagName("td");
             var date = colList.item(0).innerHTML;
             var t = @org.timepedia.chronoscope.client.data.DateParser::parse(Ljava/lang/String;Ljava/lang/String;)(dateformat, date);
             domain.push(t);
              for(j = 1; j<colList.length; j++) {
                  range[j-1].push(parseFloat(colList.item(j).innerHTML));
              }
            }
            var result=[];
            for(i=0; i<numCols-1; i++) {
               result.push({ id: id, domain: domain, range: range[i], label: labels[i+1], axis: axes[i+1],
               format: rangeFormats[i+1]});
            }
            return result;
          }
          return [];
       }-*/;

  public static native JavaScriptObject importTable(String id) /*-{

       var table = $doc.getElementById(id);
       var rowList = table.getElementsByTagName("tr");
       var i;
       var domain=[], range=[];
       for(i = 0; i<rowList.length; i++)
       {
          var colList = rowList.item(i).getElementsByTagName("td");
          domain.push(colList.item(0).innerHTML);
          range.push(colList.item(1).innerHTML);
       }
       return { domain: domain, range: range }
    }-*/;

  public static void initializeMicroformats(Chronoscope chronoscope) {
    Element[] elements = getElementsByClassName(null, "table", CMF_CHART);
    final ChartPanel[] created = new ChartPanel[elements.length];

    final Element[] links = getElementsByClassName(null, "a",
        CMF_PREFIX + "-marker");

    MicroformatCountdownLatch latch = new MicroformatCountdownLatch(
        created.length, microformatReadyListener);
    for (int i = 0; i < elements.length; i++) {
      final Element elt = elements[i];
      final String id = DOM.getElementAttribute(elt, "id");
      if (Chronoscope.getChartById(id) == null) {
        final XYDataset[] ds = importMicroformatTable(id, elt);
        Element div = DOM.createDiv();
        final String cid = id + "chrono";
        DOM.setElementAttribute(div, "id", cid);
        DOM.setElementAttribute(div, "class", "chronoscope");
//                DOM.setStyleAttribute(div, "width", "600px");
//                DOM.setStyleAttribute(div, "height", "300px");

        Element par = DOM.getParent(elt);
        DOM.insertBefore(par, div, elt);
        DOM.setStyleAttribute(elt, "display", "none");

        int candidateWidth = DOM.getElementPropertyInt(elt, "clientWidth");
        if (candidateWidth < 600) {
          candidateWidth = 600;
        }
        int candidateHeight = (int) (candidateWidth / 1.618);
        ChartPanel cp = Chronoscope.createTimeseriesChart(div, ds,
            candidateWidth, candidateHeight,
            new MicroformatViewReadyCallback(links, cid, id, ds, elt, latch));
        created[i] = cp;
        if(!cp.isAttached()) cp.attach();
      }
    }
  }

  static class MicroformatCountdownLatch {

    private int latchCount;

    private Command microformatReadyListener;

    public MicroformatCountdownLatch(int length,
        Command microformatReadyListener) {
      latchCount = length;
      this.microformatReadyListener = microformatReadyListener;
    }

    public void decrement() {
      latchCount--;
      if (latchCount == 0 && microformatReadyListener != null) {
        microformatReadyListener.execute();
      }
    }
  }

  private static native void attachOnClick(Element link, Marker m) /*-{
         link.onclick = function() {
             @org.timepedia.chronoscope.client.browser.Microformats::clickMicroformatLink(Lcom/google/gwt/user/client/Element;Lorg/timepedia/chronoscope/client/overlays/Marker;)(link, m);
         }
    }-*/;

  private static native Element castToElement(JavaScriptObject elt) /*-{
           return elt;
       }-*/;

  private static native String getAttribute(Element link, String attr) /*-{
        return link.getAttribute(attr);
    }-*/;

  private static Element[] getElementsByClassName(Element elt, String tag,
      String className) {
    JavaScriptObject obj = getElementsByClassName0(elt, tag, className);
    Element elts[] = new Element[JavascriptHelper.jsArrLength(obj)];
    for (int i = 0; i < elts.length; i++) {
      elts[i] = castToElement(JavascriptHelper.jsArrGet(obj, i));
    }
    return elts;
  }

  private static native JavaScriptObject getElementsByClassName0(
      JavaScriptObject elt, String tag, String className) /*-{
           var result=[];
           var elem = elt == null ? $doc : elt;
           var elts = elem.getElementsByTagName(tag);
           var i;
           for(i=0; i<elts.length; i++) {
               var elt = elts.item(i);
               if(elt.className && elt.className.indexOf(className) != -1) {
                  result.push(elt);
               }
           }
           return result;
       }-*/;

  private static native JavaScriptObject getMicroformatHeaders(Element table) /*-{


          var thead = table.getElementsByTagName("thead");
          var hdrs=[];
          if(thead && thead.length > 0) {
             var th = thead.item(0).getElementsByTagName("th");
             var i;
             for(i=0; i<th.length; i++) { hdrs.push(th.item(i).innerHTML); }
          }
       return hdrs;
   }-*/;

  private static String[] getTableHeaders(Element table) {
    JavaScriptObject obj = getMicroformatHeaders(table);
    String hdrs[] = new String[JavascriptHelper.jsArrLength(obj)];
    for (int i = 0; i < hdrs.length; i++) {
      hdrs[i] = JavascriptHelper.jsArrGetS(obj, i);
    }
    return hdrs;
  }

  private static XYDataset[] importMicroformatTable(String id, Element elt) {
    return Chronoscope.createXYDatasets(importMicroformatTable0(id, elt));
  }
}
