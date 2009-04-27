package org.timepedia.chronoscope.client.gss.parser;

import java.util.List;

/**
 * Represents a GSS rule defined by a list of selectors and corresponding
 * list of properties to be applied.
 */
public class GssRule {

  private List<GssSelector> selectors;

  private List<GssProperty> gssproperties;

  public GssRule(List<GssSelector> selectors, List<GssProperty> gssproperties) {
    this.selectors = selectors;
    this.gssproperties = gssproperties;
  }

  public List<GssSelector> getSelectors() {
    return selectors;
  }

  public List<GssProperty> getProperties() {
    return gssproperties;
  }
}
