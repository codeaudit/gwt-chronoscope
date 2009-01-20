package org.timepedia.chronoscope.client.gss.parser;

import org.timepedia.chronoscope.client.gss.GssElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single selector formed by simple selectors separated by
 * combinators.
 */
public class GssSelector {

  public static final String SPACE_COMBINATOR = " ";

  private List<GssSimpleSelector> simpleSelectors
      = new ArrayList<GssSimpleSelector>();

  private int specificity = -1;

  public void push(String elementName, String combinator, String className) {
    simpleSelectors
        .add(new GssSimpleSelector(elementName, combinator, className));
  }

  public int getSpecificity() {
    if (specificity == -1) {
      specificity = calcSpecificity();
    }

    return specificity;
  }

  private int calcSpecificity() {
    int lspecificity = 0;
    //specificity is (classAttributes * 256, numElementNames)
    for (GssSimpleSelector sel : simpleSelectors) {
      //
      if (sel.getClassName() != null) {
        lspecificity += 256;
      }
      if (!"*".equals(sel.getElementName())) {
        lspecificity += 1;
      }
      ;
    }
    return lspecificity;
  }

  public boolean matches(GssElement gssElem) {
    int endIdx = simpleSelectors.size() - 1;
    while (endIdx >= 0) {
      if (gssElem == null) {
        return false;
      }

      GssSimpleSelector gssSimple = simpleSelectors.get(endIdx);
      if (!gssSimple.matches(gssElem)) {
        return false;
      }
      gssElem = gssElem.getParentGssElement();
      endIdx--;
    }
    return true;
  }

  private class GssSimpleSelector {

    private String elementName;

    private String combinator;

    private String className;


    public String getClassName() {
      return className;
    }

    public String getCombinator() {
      return combinator;
    }

    public String getElementName() {
      return elementName;
    }

    public GssSimpleSelector(String elementName, String combinator,
        String className) {

      this.elementName = elementName;
      this.combinator = combinator;
      this.className = className;
    }

    public boolean matches(GssElement gssElem) {
      if (elementName.equals(gssElem.getType()) || "*".equals(elementName)) {
        if (className == null) {
          return true;
        } else {
          return gssElem.getTypeClass().contains(className);
        }
      }
      return false;
    }
  }
}
