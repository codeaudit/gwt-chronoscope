package org.timepedia.chronoscope.client.browser;

import org.timepedia.chronoscope.client.data.AbstractXYDataSource;
import org.timepedia.chronoscope.client.data.DataSourceCallback;

/**
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class ScriptTagXYDataSource extends AbstractXYDataSource {

  public ScriptTagXYDataSource(String uri) {
    super(uri);
  }

  public void loadAsCSV(DataSourceCallback async) {
  }

  public void loadAsJSON(DataSourceCallback async) {
  }

  public void loadAsXML(DataSourceCallback async) {
  }
}
