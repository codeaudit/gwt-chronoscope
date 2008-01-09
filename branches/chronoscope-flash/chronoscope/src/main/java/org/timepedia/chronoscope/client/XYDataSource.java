package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.data.DataSourceCallback;

/**
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public abstract class XYDataSource {

    public static void setFactory(XYDataSourceFactory factoryInstance) {
        XYDataSource.factoryInstance = factoryInstance;
    }

    private static XYDataSourceFactory factoryInstance;

    public abstract void loadAsCSV(DataSourceCallback async);

    public abstract void loadAsJSON(DataSourceCallback async);

    public abstract void loadAsXML(DataSourceCallback async);

    public static XYDataSource getInstance(String uri) {
        return factoryInstance.getInstance(uri);
    }


}
