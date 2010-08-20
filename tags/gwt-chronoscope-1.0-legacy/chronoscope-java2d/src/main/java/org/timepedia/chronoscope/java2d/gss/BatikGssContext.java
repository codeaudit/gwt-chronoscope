package org.timepedia.chronoscope.java2d.gss;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.StyleSheet;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Implements GSS elements using the Batik DOM
 */
public class BatikGssContext extends GssContext {

  protected static void addStyleName(Element elem, String style) {

    boolean add = true;

    // Get the current style string.
    String oldStyle = elem.getAttribute("className");
    int idx;
    if (oldStyle == null) {
      idx = -1;
      oldStyle = "";
    } else {
      idx = oldStyle.indexOf(style);
    }

    // Calculate matching index.
    while (idx != -1) {
      if (idx == 0 || oldStyle.charAt(idx - 1) == ' ') {
        int last = idx + style.length();
        int lastPos = oldStyle.length();
        if ((last == lastPos) || ((last < lastPos) && (oldStyle.charAt(last)
            == ' '))) {
          break;
        }
      }
      idx = oldStyle.indexOf(style, idx + 1);
    }

    if (add) {
      // Only add the style if it's not already present.
      if (idx == -1) {
        elem.setAttribute("className", oldStyle + " " + style);
      }
    } else {
      // Don't try to remove the style if it's not there.
      if (idx != -1) {
        String begin = oldStyle.substring(0, idx);
        String end = oldStyle.substring(idx + style.length());
        elem.setAttribute("className", begin + end);
      }
    }
  }

  Document doc;

  Element viewElement;

  HashMap pseudo2Context = new HashMap();

  private GVTBuilder builder;

  private BridgeContext ctx = null;

  private UserAgent userAgent;

  private DocumentLoader loader;

  public BatikGssContext(String userStyleSheet) {

    String styleSheets[] = userStyleSheet.split("\\$");
    String inlineCss = null;
    String altCss = null;
    for (String cssUri : styleSheets) {
      if (cssUri.startsWith("css:")) {
        inlineCss = cssUri.substring(4);
      } else if (cssUri.endsWith(".css")) {
        altCss = cssUri;
      }
    }
    doc = SVGDOMImplementation.getDOMImplementation()
        .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);

    viewElement = doc.getDocumentElement();

    this.userAgent = new UserAgentAdapter();
    this.loader = new DocumentLoader(userAgent);

    ctx = new BridgeContext(userAgent, loader);
    ctx.setDynamicState(BridgeContext.DYNAMIC);
    builder = new GVTBuilder();

    builder.build(ctx, doc);

    StyleSheet ss = null;
    CSSEngine engine = ((SVGOMDocument) doc).getCSSEngine();
    try {
      if (inlineCss != null) {
        InputSource source = new InputSource(new StringReader(inlineCss));
        ss = engine.parseStyleSheet(source, new URL("http://www.timepedia.org"),
            "screen");
        engine.setUserAgentStyleSheet(ss);
      } else if (altCss != null) {
        ss = engine.parseStyleSheet(new InputSource(altCss),
            new URL("http://www.timepedia.org"), "screen");
        engine.setUserAgentStyleSheet(ss);
      } else {
        InputStream asStream = getClass().getClassLoader().getResourceAsStream(
            "org/timepedia/chronoscope/public/Chronoscope.css");
        InputSource source = new InputSource(new InputStreamReader(asStream));
        ss = engine.parseStyleSheet(source, new URL("http://www.timepedia.org"),
            "screen");
        engine.setUserAgentStyleSheet(ss);
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  public void dispose(Element styleElem) {
  }

  public GssProperties getProperties(GssElement elem, String pseudoElt) {
    HashMap contextMap = getContextMap(pseudoElt);
    Element styleElem = (Element) contextMap.get(elem);
    if (styleElem == null) {
      styleElem = createAndAddStyleElements(elem, pseudoElt);
    }

    ctx = new BridgeContext(userAgent, loader);
    ctx.setDynamicState(BridgeContext.DYNAMIC);
    builder = new GVTBuilder();

    builder.build(ctx, doc);
    BatikGssProperties gss = new BatikGssProperties();
    gss.init(this, doc, elem, styleElem, pseudoElt, view);
    return gss;
  }

  private Element createAndAddStyleElements(GssElement elem, String pseudoElt) {
    GssElement parent = elem.getParentGssElement();
    HashMap contextMap = getContextMap(pseudoElt);

    Element parStyleElem = (Element) contextMap.get(parent);
    if (parent != null && parStyleElem == null) {
      parStyleElem = createAndAddStyleElements(parent, pseudoElt);
    } else if (parent == null) {
      parStyleElem = viewElement;
    }

    Element styleElement = doc.createElementNS(
        BatikDomExtension.TIMEPEDIA_NAMESPACE_URI, elem.getType());

    if (!"".equals(pseudoElt)) {
      addStyleName(styleElement, pseudoElt);
    }
    String typeClass = elem.getTypeClass();
    if (typeClass != null) {
      addStyleName(styleElement, typeClass);
    }

    parStyleElem.appendChild(styleElement);
    contextMap.put(elem, styleElement);
    return styleElement;
  }

  private HashMap getContextMap(String pseudoElt) {
    HashMap contextMap = (HashMap) pseudo2Context.get(pseudoElt);
    if (contextMap == null) {
      contextMap = new HashMap();
      pseudo2Context.put(pseudoElt, contextMap);
    }
    return contextMap;
  }
}
