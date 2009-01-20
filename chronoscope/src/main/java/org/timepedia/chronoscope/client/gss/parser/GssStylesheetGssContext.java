package org.timepedia.chronoscope.client.gss.parser;

import org.timepedia.chronoscope.client.gss.DefaultGssContext;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.canvas.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Gss context which relies on parsed GSS text stylesheets
 */
public class GssStylesheetGssContext extends DefaultGssContext {

  private List<GssRule> rules;

  private Map<GssElement, GssProperties> propertyMap
      = new HashMap<GssElement, GssProperties>();

  public GssStylesheetGssContext(String stylesheet) {
    try {
      parseStylesheet(stylesheet);
    } catch (GssParseException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public void parseStylesheet(String stylesheet) throws GssParseException {
    rules = GssParser.parse(stylesheet);
  }

  public void mergeStylesheet(String stylesheet) throws GssParseException {
    List<GssRule> mergeRules = GssParser.parse(stylesheet);
    rules.addAll(mergeRules);
  }

  @Override
  public GssProperties getProperties(GssElement gssElem, String pseudoElt) {
    GssProperties props = propertyMap.get(gssElem);
    if (props == null) {
      props = super.getProperties(gssElem, pseudoElt);
      propertyMap.put(gssElem, props);
    }
    List<GssRuleMatch> matched = findAllMatchingRules(gssElem, pseudoElt);
    Collections.sort(matched, new Comparator<GssRuleMatch>() {
      public int compare(GssRuleMatch gssRuleMatch,
          GssRuleMatch gssRuleMatch1) {
        return gssRuleMatch.getBestSpecificity() - gssRuleMatch1
            .getBestSpecificity();
      }
    });
    for(GssRuleMatch match : matched) {
      applyProperties(match.getProperties(), props);
    }
    return props;
  }

  private void applyProperties(List<GssProperty> properties, GssProperties props) {
    for(GssProperty property : properties) {
      applyProperty(property, props);
    }
  }

  private void applyProperty(GssProperty property, GssProperties props) {
    String pname = property.getPropertyName();
    String pval = property.getPropertyValue();

    if("color".equals(pname))
        props.color=new Color(pval);
    else if("background-color".equals(pname))
        props.bgColor=new Color(pval);
    else if("radius".equals(pname))
      props.size=Integer.parseInt(pval);
    else if("font-size".equals(pname))
      props.fontSize=pval;
    else if("line-width".equals(pname))
      props.lineThickness = Double.parseDouble(pval);
    else if("opacity".equals(pname))
      props.transparency = Double.parseDouble(pval);
  }

  private List<GssRuleMatch> findAllMatchingRules(GssElement gssElem,
      String pseudoElt) {
    ArrayList<GssRuleMatch> matched = new ArrayList<GssRuleMatch>();
    for (GssRule testRule : rules) {
      int bestSpecificity = -1;
      for (GssSelector testSelector : testRule.getSelectors()) {
        if (testSelector.matches(gssElem)) {
          bestSpecificity = Math
              .max(bestSpecificity, testSelector.getSpecificity());
        }
      }
      if (bestSpecificity > -1) {
        matched
            .add(new GssRuleMatch(bestSpecificity, testRule.getProperties()));
      }
    }
    return matched;
  }

  private class GssRuleMatch {

    private int bestSpecificity;

    private List<GssProperty> properties;

    public int getBestSpecificity() {
      return bestSpecificity;
    }

    public List<GssProperty> getProperties() {
      return properties;
    }

    public GssRuleMatch(int bestSpecificity, List<GssProperty> properties) {

      this.bestSpecificity = bestSpecificity;
      this.properties = properties;
    }
  }
}
