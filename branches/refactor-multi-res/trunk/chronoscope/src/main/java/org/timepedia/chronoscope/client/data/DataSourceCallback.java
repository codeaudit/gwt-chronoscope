package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.XYDataset;

/**
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public interface DataSourceCallback {
    void onSuccess(XYDataset[] datasets);

    void onFailure(Throwable e);
}
