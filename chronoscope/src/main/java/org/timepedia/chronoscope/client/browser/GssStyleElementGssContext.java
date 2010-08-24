package org.timepedia.chronoscope.client.browser;

import java.util.ArrayList;

import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.gss.parser.GssParseException;
import org.timepedia.chronoscope.client.gss.parser.GssStylesheetGssContext;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.StyleElement;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Element;

/**
 * Retrieves gss styles from <STYLE type="text/gss"> 
 * and <link type="text/gss">
 */
public class GssStyleElementGssContext extends BrowserGssContext implements
    CssGssViewSupport {

  /**
   * Recursively load a set of Gss urls. At the end it runs the
   * OnGssInitializedCallback.
   */
  protected static class GssLoader implements RequestCallback {

    ArrayList<String> urls;
    OnGssInitializedCallback callback;
    String url;
    static ArrayList<GssLoader> gssqueue = new ArrayList<GssLoader>();
    static boolean running = false;

    GssLoader(ArrayList<String> urls, OnGssInitializedCallback callback) {
      this.urls = urls;
      this.callback = callback;
      url = urls.remove(0);
      if (running) {
        queue();
      } else {
        running = true;
        run();
      }
    }
    
    private void dequeue() {
      if (!gssqueue.isEmpty()) {
        gssqueue.remove(0).run();
      }
    }
    
    private void next() {
      running = false;
      if (!urls.isEmpty()) {
        new GssLoader(urls, callback);
      } else {
        callback.run();
        dequeue();
      }
    }
    
    @Override
    public void onError(Request request, Throwable exception) {
      gssLoaded.add(url);
      next();
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
      try {
        gssContext.mergeStylesheet(response.getText());
      } catch (Exception e) {
      }
      gssLoaded.add(url);
      next();
    }

    private void queue() {
      if (!gssqueue.contains(this)){
        gssqueue.add(this);
      }
    }

    protected void run() {
      if (!gssLoaded.contains(url)) {
        RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, url);
        try {
          rb.sendRequest("", this);
        } catch (Exception e) {
          next();
        }
      } else {
        next();
      }
    }
  }

  private static GssStylesheetGssContext gssContext = new GssStylesheetGssContext();

  private static ArrayList<String> gssLoaded = new ArrayList<String>();

  final StringBuilder gss = new StringBuilder();

  public GssStyleElementGssContext() {
  }

  public Element getGssCssElement() {
    return super.getElement();
  }

  @Override
  public GssProperties getProperties(GssElement gssElem, String pseudoElt) {
    return gssContext.getProperties(gssElem, pseudoElt);
  }

  @Override
  public GssProperties getPropertiesBySelector(String gssSelector) {
    return gssContext.getPropertiesBySelector(gssSelector);
  }

  @Override
  public void initialize(Element cssgss,
      final OnGssInitializedCallback mcallback) {
    
    super.initialize(cssgss, mcallback);
    
    // look for all style tags with content-type text/gss in the document
    NodeList<com.google.gwt.dom.client.Element> nl = Document.get().getElementsByTagName("style");
    final ArrayList<String> toLoad = new ArrayList<String>();
    for (int i = 0; i < nl.getLength(); i++) {
      Element e = (Element) nl.getItem(i);
      if ("text/gss".equalsIgnoreCase(e.getAttribute("type"))) {
        gss.append(e.getInnerHTML());
      }
    }

    // look for all link tags with content-type text/gss in the head
    nl = Document.get().getElementsByTagName("head");
    if (nl.getLength() > 0) {
      nl = nl.getItem(0).getChildNodes().cast();
    }
    for (int i = 0; i < nl.getLength(); i++) {
      if (nl.getItem(i).getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }
      Element e = (Element) nl.getItem(i);
      if ((StyleElement.is(e) || LinkElement.is(e))
          && "text/gss".equalsIgnoreCase(e.getAttribute("type"))
          && e.hasAttribute("href")) {
        toLoad.add(e.getAttribute("href"));
      }
    }
    
    // process in-lined gss elements 
    String gssStr = gss.toString().trim();
    if (gssStr.length() > 0) {
      try {
        gssContext.parseStylesheet(gssStr);
      } catch (GssParseException e) {
        throw new RuntimeException(e.getMessage());
      }
    }

    // load asynchronously external gss links
    if (!toLoad.isEmpty()) {
      new GssLoader(toLoad, mcallback);
    } else {
      mcallback.run();
    }
  }
  
  public String toString() {
   return gssContext.toString(); 
  }
}
