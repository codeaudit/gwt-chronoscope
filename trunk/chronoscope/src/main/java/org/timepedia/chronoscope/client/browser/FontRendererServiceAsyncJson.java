package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.timepedia.chronoscope.client.canvas.FontRendererServiceAsync;
import org.timepedia.chronoscope.client.canvas.RenderedFontMetrics;

/**
 * Class to deal with cross-domain RPC
 */
public class FontRendererServiceAsyncJson implements FontRendererServiceAsync {
    private String endpoint;

    private static int jsonRequestNumber=0;
    public FontRendererServiceAsyncJson(String endpoint) {

        this.endpoint = endpoint;
    }

    public void getRenderedFontMetrics(String fontFamily, String fontWeight, String fontSize, String color, float angle, AsyncCallback async) {
        String reqCallBack = "__jsonCallback"+jsonRequestNumber++;

        String queryString = endpoint + "?ff="+fontFamily+"&fw="+fontWeight+"&fs="+fontSize+
                "&c="+color+"&a="+angle+"&json="+reqCallBack;
        String url = URL.encode(queryString);
        kickoffJsonRequest(url, reqCallBack, async);

    }

    private void handle(JavaScriptObject jso, AsyncCallback async) {
        RenderedFontMetrics rfm = new RenderedFontMetrics();
        rfm.leading = (int) JavascriptHelper.jsPropGetD(jso, "leading");
        rfm.maxAdvance = (int) JavascriptHelper.jsPropGetD(jso, "maxAdvance");
        rfm.maxAscent =  (int) JavascriptHelper.jsPropGetD(jso, "maxAscent");
        rfm.maxDescent = (int) JavascriptHelper.jsPropGetD(jso, "maxDescent");
        rfm.url =  JavascriptHelper.jsPropGetString(jso, "url");
        rfm.maxBoundsHeight = (int) JavascriptHelper.jsPropGetD(jso, "maxBoundsHeight");
        rfm.maxBoundsWidth = (int) JavascriptHelper.jsPropGetD(jso, "maxBoundsWidth");
        JavaScriptObject arr = JavascriptHelper.jsPropGet(jso, "advances");
        rfm.advances = new int[JavascriptHelper.jsArrLength(arr)];
        for(int i=0; i<rfm.advances.length; i++) {
           rfm.advances[i] = (int) JavascriptHelper.jsArrGetD(arr, i);
        }

        async.onSuccess(rfm);
    }

    private native void kickoffJsonRequest(String url, String reqCallBack, AsyncCallback async) /*-{
        var scriptElement = $doc.createElement("script");
        scriptElement.src = url;
        scriptElement.language = "javascript";

        var headElement = $doc.getElementsByTagName("head")[0];
        var outerthis=this;
        $wnd[reqCallBack] = function(json) {
            headElement.removeChild(scriptElement);
            delete $wnd[reqCallBack];
            outerthis.@org.timepedia.chronoscope.client.browser.FontRendererServiceAsyncJson::handle(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/user/client/rpc/AsyncCallback;)(json, async);
        }

        headElement.appendChild(scriptElement);

    }-*/;
}
