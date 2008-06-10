package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;

import java.util.HashMap;

/**
 * Implements a GssContext by injecting hidden DOM elements for each
 * GssElement,pseudoElement pair that is queried.
 */
public class CssGssContext extends BrowserGssContext {

  protected static void addStyleName(Element elem, String style) {

    boolean add = true;

    // Get the current style string.
    String oldStyle = DOM.getElementAttribute(elem, "class");
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
        DOM.setElementAttribute(elem, "class", oldStyle + " " + style);
      }
    } else {
      // Don't try to remove the style if it's not there.
      if (idx != -1) {
        String begin = oldStyle.substring(0, idx);
        String end = oldStyle.substring(idx + style.length());
        DOM.setElementAttribute(elem, "class", begin + end);
      }
    }
  }

  Element viewElement;

  final HashMap pseudo2Context = new HashMap();

  public CssGssContext() {
  }

  public CssGssContext(String id) {

    viewElement = DOM.getElementById(id);
    initialize(viewElement);
  }

  public CssGssContext(Element element) {
    initialize(element);
  }

  // not really needed for now, investigate performance of removing injected styleElements later
  public void dispose(Element styleElem) {
  }

  public Element getElement() {
    return viewElement;
  }

  public GssProperties getProperties(GssElement elem, String pseudoElt) {
    HashMap contextMap = getContextMap(pseudoElt);
    Element styleElem = (Element) contextMap.get(elem);
    if (styleElem == null) {
      styleElem = createAndAddStyleElements(elem, pseudoElt);
    }

    GssProperties gss = parseGssProperties(elem, pseudoElt, styleElem);
    return gss;
  }

  protected GssProperties parseGssProperties(GssElement elem, String pseudoElt,
      Element styleElem) {
    CssGssProperties gss = (CssGssProperties) GWT
        .create(CssGssProperties.class);
    gss.init(this, elem, styleElem, pseudoElt, view);
    return gss;
  }

  public void initialize(Element viewElement) {
    this.viewElement = viewElement;
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

    Element styleElement = DOM.createElement(elem.getType());
    DOM.setStyleAttribute(styleElement, "position", "absolute");
    DOM.setStyleAttribute(styleElement, "zIndex", "-99");
    DOM.setStyleAttribute(styleElement, "borderStyle", "solid");
    DOM.setStyleAttribute(styleElement, "clip", "rect(0,0,0,0)");
    if (!"".equals(pseudoElt)) {
      addStyleName(styleElement, pseudoElt);
    }
    String typeClass = elem.getTypeClass();
    if (typeClass != null) {
      addStyleName(styleElement, typeClass);
    }

    DOM.appendChild(parStyleElem, styleElement);
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

  private native void setAttribute(Element styleElement, String name,
      String value) /*-{
       styleElement.setAttribute(name, value);
    }-*/;
}
