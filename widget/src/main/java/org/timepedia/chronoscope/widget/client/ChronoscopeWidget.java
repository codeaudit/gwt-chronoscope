package org.timepedia.chronoscope.widget.client;

import com.google.gwt.core.client.EntryPoint;
import org.timepedia.chronoscope.client.browser.Chronoscope;

/**
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class ChronoscopeWidget implements EntryPoint {
    public void onModuleLoad() {
        Chronoscope.setMicroformatsEnabled(true);
        Chronoscope.setErrorReporting(false);
        Chronoscope chronoscope = new Chronoscope();
        chronoscope.init();
    }
}
