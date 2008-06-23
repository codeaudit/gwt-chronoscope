package org.timepedia.chronoscope.gviz.api.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.exporter.client.Exporter;

/**
 *
 */
public class GVizChronoscopeAPI implements EntryPoint {

  public void onModuleLoad() {
    Chronoscope.setMicroformatsEnabled(true);
    Chronoscope.setErrorReporting(false);
    Chronoscope.getInstance();
    Exporter gvizExporter = GWT.create(ChronoscopeVisualization.class);
    gvizExporter.export();    
  }
}
