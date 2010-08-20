package org.timepedia.chronoscope.client;

/**
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public interface XYDataSourceFactory {

  XYDataSource getInstance(String uri);
}
