package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;

/**
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public interface DataSourceCallback {

  void onSuccess(Dataset[] datasets);

  void onFailure(Throwable e);
}
