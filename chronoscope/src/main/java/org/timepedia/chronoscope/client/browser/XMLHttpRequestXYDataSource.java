package org.timepedia.chronoscope.client.browser;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

import org.timepedia.chronoscope.client.browser.AbstractXYDataSource;
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
        public void onError(Request request, Throwable exception) {
          async.onFailure(exception);
        }
        
        public void onResponseReceived(Request request, Response response) {
          parseCSV(response.getText(), async);
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
        public void onError(Request request, Throwable exception) {
          async.onFailure(exception);
        }
        
        public void onResponseReceived(Request request, Response response) {
          parseJSON(response.getText(), async);
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
        public void onError(Request request, Throwable exception) {
          async.onFailure(exception);
        }
        
        public void onResponseReceived(Request request, Response response) {
          parseXML(response.getText(), async);
        }
      });
    } catch (RequestException e) {
      async.onFailure(e);
    }
  }
}
