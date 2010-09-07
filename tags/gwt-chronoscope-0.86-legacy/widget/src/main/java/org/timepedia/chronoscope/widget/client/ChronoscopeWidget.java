package org.timepedia.chronoscope.widget.client;

import com.google.gwt.core.client.EntryPoint;

import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.ChronoscopeOptions;

/**
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class ChronoscopeWidget implements EntryPoint {

  public void onModuleLoad() {
    Chronoscope.setMicroformatsEnabled(true);
    ChronoscopeOptions.setErrorReporting(false);
    Chronoscope.setFontBookRendering(true);
    Chronoscope.getInstance();
  }
}