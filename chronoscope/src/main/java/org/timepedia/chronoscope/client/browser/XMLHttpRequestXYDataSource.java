package org.timepedia.chronoscope.client.browser;

import com.google.gwt.http.client.*;
import org.timepedia.chronoscope.client.data.AbstractXYDataSource;
import org.timepedia.chronoscope.client.data.DataSourceCallback;

/**
 * @author Ray Cromwell &lt;ray@timepedia.org&gt;
 */
public class XMLHttpRequestXYDataSource extends AbstractXYDataSource {
    public XMLHttpRequestXYDataSource(String uri) {
        super(uri);
    }

    public void loadAsCSV(final DataSourceCallback async) {
        RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, uri);
        try {
            rb.sendRequest("", new RequestCallback() {
                public void onResponseReceived(Request request, Response response) {
                    parseCSV(response.getText(), async);
                }

                public void onError(Request request, Throwable exception) {
                    async.onFailure(exception);
                }
            });
        } catch (RequestException e) {
            async.onFailure(e);
        }
    }

    public void loadAsJSON(final DataSourceCallback async) {
        RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, uri);
        try {
            rb.sendRequest("", new RequestCallback() {
                public void onResponseReceived(Request request, Response response) {
                    parseJSON(response.getText(), async);
                }

                public void onError(Request request, Throwable exception) {
                    async.onFailure(exception);
                }
            });
        } catch (RequestException e) {
            async.onFailure(e);
        }
    }

    public void loadAsXML(final DataSourceCallback async) {
        RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, uri);
        try {
            rb.sendRequest("", new RequestCallback() {
                public void onResponseReceived(Request request, Response response) {
                    parseXML(response.getText(), async);
                }

                public void onError(Request request, Throwable exception) {
                    async.onFailure(exception);
                }
            });
        } catch (RequestException e) {
            async.onFailure(e);
        }
    }

}
