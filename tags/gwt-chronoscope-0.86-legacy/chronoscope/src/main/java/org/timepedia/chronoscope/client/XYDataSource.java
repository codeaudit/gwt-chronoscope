package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.data.DataSourceCallback;

/**
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public abstract class XYDataSource {

  private static XYDataSourceFactory factoryInstance;

  public static XYDataSource getInstance(String uri) {
    return factoryInstance.getInstance(uri);
  }

  public static void setFactory(XYDataSourceFactory factoryInstance) {
    XYDataSource.factoryInstance = factoryInstance;
  }

  public abstract void loadAsCSV(DataSourceCallback async);

  public abstract void loadAsJSON(DataSourceCallback async);

  public abstract void loadAsXML(DataSourceCallback async);
}
