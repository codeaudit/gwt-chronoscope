package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
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
             domain.push(t/1000);
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
        ChartPanel cp = Chronoscope.createTimeseriesChart(cid, ds,
            candidateWidth, candidateHeight,
            new MicroformatViewReadyCallback(links, cid, id, ds, elt, latch));
        created[i] = cp;
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
      if(latchCount == 0 && microformatReadyListener != null)
        microformatReadyListener.execute();
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
