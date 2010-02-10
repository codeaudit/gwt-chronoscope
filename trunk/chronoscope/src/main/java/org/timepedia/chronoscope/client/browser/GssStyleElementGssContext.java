package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.StyleElement;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Element;

import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.gss.parser.GssParseException;
import org.timepedia.chronoscope.client.gss.parser.GssStylesheetGssContext;

import java.util.ArrayList;

/**
 * Retrieves styles from <STYLE type="text/gss"> elements
 */
public class GssStyleElementGssContext extends BrowserGssContext
    implements CssGssViewSupport {

  GssStylesheetGssContext gssContext = new GssStylesheetGssContext();

  public GssStyleElementGssContext() {
  }

  final StringBuilder gss = new StringBuilder();

  @Override
  public void initialize(Element cssgss) {
    super.initialize(cssgss);
    NodeList nl = Document.get().getElementsByTagName("style");
    final ArrayList<String> toLoad = new ArrayList<String>();
    for (int i = 0; i < nl.getLength(); i++) {
      Element e = (Element) nl.getItem(i);
      if ("text/gss".equals(e.getAttribute("type"))) {
        gss.append(e.getInnerHTML());
      }
    }

    nl = Document.get().getElementsByTagName("head");
    if (nl.getLength() > 0) {
      nl = nl.getItem(0).getChildNodes();
    }
    for (int i = 0; i < nl.getLength(); i++) {
      if (nl.getItem(i).getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }
      Element e = (Element) nl.getItem(i);
      if ((StyleElement.is(e)|| LinkElement.is(e))
          && "text/gss".equals(e.getAttribute("type")) && e
          .hasAttribute("href")) {
        toLoad.add(e.getAttribute("href"));
      }
    }
    String gssStr = gss.toString();
    if (!gssStr.trim().equals("")) {
      try {
        gssContext.parseStylesheet(gssStr);
      } catch (GssParseException e) {
        throw new RuntimeException(e.getMessage());
      }
    }

    if (!toLoad.isEmpty()) {
      RequestBuilder rb = new RequestBuilder(RequestBuilder.GET,
          toLoad.remove(0));
      try {
        RequestCallback callback = new RequestCallback() {
          public void onResponseReceived(Request request, Response response) {
            gss.append(response.getText());
            if (!toLoad.isEmpty()) {
              try {
                RequestBuilder lrb = new RequestBuilder(RequestBuilder.GET,
                    toLoad.remove(0));
                lrb.sendRequest("", this);
              } catch (RequestException e) {

              }
            } else {
              try {
                gssContext.parseStylesheet(gss.toString());
                getView().getChart().reloadStyles();
              } catch (GssParseException e) {
                
              }
            }
          }

          public void onError(Request request, Throwable throwable) {
          }
        };
        rb.sendRequest("", callback);
      } catch (RequestException e1) {

      }
    }
  }

  @Override
  public GssProperties getProperties(GssElement gssElem, String pseudoElt) {
    return gssContext.getProperties(gssElem, pseudoElt);
  }

  public Element getGssCssElement() {
    return super.getElement();
  }
}
