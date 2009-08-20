package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.GWT;

import org.timepedia.chronoscope.client.XYDataSource;
import org.timepedia.chronoscope.client.XYDataSourceFactory;

/**
 * Uses SCRIPT tag for cross domain, and XMLHttpRequest for same domain
 *
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class BrowserXYDataSourceFactory implements XYDataSourceFactory {

  public XYDataSource getInstance(String uri) {
    if (isSameDomain(GWT.getHostPageBaseURL(), uri)) {
      return new XMLHttpRequestXYDataSource(uri);
    } else {
      return new ScriptTagXYDataSource(uri);
    }
  }

  private boolean isSameDomain(String uri, String uri2) {
    return true;
  }
}
