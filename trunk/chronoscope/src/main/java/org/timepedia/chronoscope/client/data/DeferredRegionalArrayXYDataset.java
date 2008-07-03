package org.timepedia.chronoscope.client.data;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.user.client.Timer;

import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.browser.JavascriptHelper;

import java.util.ArrayList;

/**
 *
 */
public class DeferredRegionalArrayXYDataset extends ArrayXYDataset
    implements HasRegions {

  private double regionBegin;

  private double regionEnd;

  private double[] intervals;

  private String filePrefix;

  private static long requestTimestamp = -1;

  private static Timer queueTimer;

  public DeferredRegionalArrayXYDataset(String identifier, double[][] domains,
      double[][] ranges, double top, double bottom, String label, String axisId,
      double regionBegin, double regionEnd, double intervals[],
      String filePrefix, double domainBegin, double domainEnd,
      double approximateMinInterval) {
    this(identifier, domains, ranges, top, bottom, label, axisId, regionBegin,
        regionEnd, intervals, filePrefix, domainBegin, domainEnd);
    this.approximateMinimumInterval = approximateMinInterval;
  }

  public double getDomainBegin() {
    return domainBegin;
  }

  public double getDomainEnd() {
    return domainEnd;
  }

  private double domainBegin;

  private double domainEnd;

  public DeferredRegionalArrayXYDataset(String identifier, double[][] domains,
      double[][] ranges, double top, double bottom, String label, String axisId,
      double regionBegin, double regionEnd, double intervals[],
      String filePrefix, double domainBegin, double domainEnd) {
    super(identifier, domains, ranges, top, bottom, label, axisId);
    this.regionBegin = regionBegin;
    this.regionEnd = regionEnd;
    this.intervals = intervals;
    this.filePrefix = filePrefix;
    this.domainBegin = domainBegin;
    this.domainEnd = domainEnd;
  }

  public double getRegionBegin() {
    return regionBegin;
  }

  public double getRegionEnd() {
    return regionEnd;
  }

  public int getNumRegions() {
    return intervals.length;
  }

  public double getRegionBegin(int i) {
    return intervals[i];
  }

  public double getRegionEnd(int i) {
    return i == intervals.length - 1 ? getDomainEnd() : intervals[i + 1];
  }

  public int getRegionNumber() {
    for (int i = 0; i < getNumRegions(); i++) {
      if (getRegionBegin() >= getRegionBegin(i)
          && getRegionEnd() <= getRegionEnd(i)) {
        return i;
      }
    }
    return -1;
  }

  public void loadRegion(int i) {
    GWT.log("Loading region " + i, null);
    queue.add(new RegionLoadRequest(filePrefix + "." + i + ".js", this));
    processQueue();
    // queue.add pointer to this object instance/handler
    // if script request not in progress or timed out,
    // peek start of queue, add script request
    // script request dequeues and calls handler
    // handler replaces multiresolutiond datastructure from json,
    //     and fires region load listener
    // RegionLoadListener redraw()
    // redraw loop checks if domain start/end inside region if HasRegions
    // if not, find deepest miplevel containing start/end that is renderable
    // and invoke loadRegion with appropriate region
    // if a region cannot be found, back off to lower resolution
  }

  private static void processQueue() {
    if (queueTimer != null) {
      queueTimer = new Timer() {

        public void run() {
          processQueue();
        }
      };
      queueTimer.scheduleRepeating(5000);
    }

    if (requestTimestamp == -1 ||
        System.currentTimeMillis() - requestTimestamp > 5000
            && queue.size() > 0) {
      RegionLoadRequest req = queue.get(0);
      queue.remove(req);
      GWT.log("Added script request", null);
      addScriptRequest(req);
    }
  }

  private static void addScriptRequest(RegionLoadRequest req) {
    requestTimestamp = System.currentTimeMillis();
    setupHandlerFunction(req.getDeferredRegionalArrayXYDataset());
    injectScript(req.getFile());
  }

  private static void injectScript(String file) {
    ScriptElement se = Document.get().createScriptElement();
    se.setType("text/javascript");
    se.setSrc(file);
    se.setId("regionLoader");
    Document.get().getBody().appendChild(se);
  }

  private void onRegionLoaded(JavaScriptObject data) {
    GWT.log("Region Loaded!", null);
    Element e = Document.get().getElementById("regionLoader");
    if (e != null) {
      e.getParentNode().removeChild(e);
    }
    requestTimestamp = -1;
    parseData(data);
    GWT.log("Firing load events", null);
    fireRegionLoadListenerEvent();
  }

  private void parseData(JavaScriptObject json) {
    ArrayXYDataset dataset = (ArrayXYDataset) Chronoscope.createXYDataset(json);
    domain = dataset.domain;
    range = dataset.range;
    multiDomain = dataset.multiDomain;
    multiRange = dataset.multiRange;
    length = dataset.length;
    multiLengths = dataset.multiLengths;
    filePrefix = JavascriptHelper.jsPropGetString(json, "prefix");
    intervals = Chronoscope
        .getArray(JavascriptHelper.jsPropGet(json, "intervals"), 1);
    regionBegin = domain[0];
    regionEnd = domain[domain.length - 1];
    domainBegin = JavascriptHelper.jsPropGetD(json, "domainBegin");
    domainEnd = JavascriptHelper.jsPropGetD(json, "domainEnd");
  }

  private void fireRegionLoadListenerEvent() {
    if (listeners != null) {
      for (RegionLoadListener rll : listeners) {
        rll.onRegionLoaded(this, getRegionNumber());
      }
    }
  }

  private static native void setupHandlerFunction(
      DeferredRegionalArrayXYDataset handler) /*-{
    $wnd.onRegionLoaded = function(json) {
      handler.@org.timepedia.chronoscope.client.data.DeferredRegionalArrayXYDataset::onRegionLoaded(Lcom/google/gwt/core/client/JavaScriptObject;)(json);
    }
  }-*/;

  public void addRegionLoadListener(RegionLoadListener rll) {
    if (listeners == null) {
      listeners = new ArrayList<RegionLoadListener>();
    }

    listeners.add(rll);
  }

  public int findRegion(double start, double end) {
    for (int i = 0; i < intervals.length - 1; i++) {
      if (start >= intervals[i] && end <= intervals[i + 1]) {
        return i;
      }
    }
    if (start >= intervals[intervals.length - 1]) {
      return intervals.length - 1;
    }
    return -1;
  }

  private ArrayList<RegionLoadListener> listeners = null;

  private static ArrayList<RegionLoadRequest> queue
      = new ArrayList<RegionLoadRequest>();

  private class RegionLoadRequest {

    private String file;

    private DeferredRegionalArrayXYDataset deferredRegionalArrayXYDataset;

    public DeferredRegionalArrayXYDataset getDeferredRegionalArrayXYDataset() {
      return deferredRegionalArrayXYDataset;
    }

    public String getFile() {
      return file;
    }

    public RegionLoadRequest(String file,
        DeferredRegionalArrayXYDataset deferredRegionalArrayXYDataset) {

      this.file = file;
      this.deferredRegionalArrayXYDataset = deferredRegionalArrayXYDataset;
    }
  }
}
