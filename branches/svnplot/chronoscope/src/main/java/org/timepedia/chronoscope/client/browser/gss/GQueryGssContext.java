package org.timepedia.chronoscope.client.browser.gss;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Command;

import org.timepedia.chronoscope.client.browser.CssGssContext;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.gss.MockGssContext;

import java.util.ArrayList;


/**
 *
 */
public class GQueryGssContext extends CssGssContext {

  private String css;

  public GQueryGssContext(Element element) {
    super((com.google.gwt.user.client.Element) element);
  }

  public GQueryGssContext(String id) {
    super(id);
  }

  private boolean cssParsed = false;

//  protected GssProperties parseGssProperties(GssElement elem, String pseudoElt,
//      Element styleElem) {
//    if (!cssParsed) {
//      parseCss(new Command() {
//
//        public void execute() {
//          applyGss();
//        }
//      });
//      return new MockGssContext().getProperties(elem, pseudoElt);
//    }
//    applyGss();
//    return new GQueryGssProperties(styleElem);
//  }

//  private void applyGss() {
//    for (Rule rule : rules) {
//      for (Element e : GQuery.$(rule.getSelector()).elements()) {
//        String nodeName = e.getNodeName();
//        if (!nodeName.matches(
//            "tick|axis|label|axislegend|overview|.*marker|plot|line|bar|axes")) {
//          continue;
//        }
//        for (String property : rule.getProperties()) {
//          String keyval[] = property.split(":", 2);
//          e.setPropertyString(keyval[0], keyval[1]);
//        }
//      }
//    }
//  }

  private void parseCss(final Command continuation) {
     getAllCss(new Command() {

      public void execute() {
        String rules[] = css.split("\\}");
        for (String rule : rules) {
          String parts[] = rule.split("\\{", 2);
          String selector = parts[0];
          String properties[] = parts[1].split(";");
          GQueryGssContext.this.rules.add(new Rule(selector, properties));
        }
       
        cssParsed = true;
        continuation.execute();
      }
    });
  }

  private void getAllCss(Command continuation) {
    NodeList<Element> styles = Document.get().getElementsByTagName("style");
    //NodeList<Element> links = Document.get().getElementsByTagName("link");

    String css = "";
    for (int i = 0; i < styles.getLength(); i++) {
      css += styles.getItem(i).getInnerText();
    }

  }

  public static class Rule {

    private String selector;

    private String propertes[];

    private String[] properties;

    public Rule(String selector, String[] propertes) {
      this.selector = selector;
      this.propertes = propertes;
    }

    public String getSelector() {
      return selector;
    }

    public String[] getProperties() {
      return properties;
    }
  }

  private ArrayList<Rule> rules = new ArrayList<Rule>();
}
