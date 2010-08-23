package org.timepedia.chronoscope.client.gss.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser for GSS text format, loosely based on CSS. Format is:
 *
 * gss ::= rule*
 *
 * rule ::= selector ("," selector) "{" property (";" property)* "}"
 *
 * selector ::= simple_selector (combinator simple_selector)*
 * simple_selector ::= element_name ("." class_name)? | "*"? "." class_name
 * element_name ::= IDENTIFIER
 * class_name ::= IDENTIFIER
 *
 * combinator ::= SPACE
 *
 * property ::= property-name ":" property-value
 * property-name ::= IDENTIFIER
 * property-value ::= IDENTIFIER
 *
 * IDENTIFIER = any ascii character except "{", ",", ">", SPACE, ";", "}".
 */ // TODO: /* comments in gss */
public class GssParser {

  public static List<GssRule> parse(String stylesheet)
      throws GssParseException {
    if (stylesheet == null || "".equals(stylesheet.trim())) {
      return new ArrayList<GssRule>();
    }
    // first separate rules by splitting on "}"
    String rules[] = removeComments(stylesheet).split("}\\s*;*\\s*");
    ArrayList<GssRule> gssRules = new ArrayList<GssRule>();
    for (String rule : rules) {
      if("".equals(rule.trim())) continue;
      GssRule gssRule = parseRule(rule + "}");
      gssRules.add(gssRule);
    }
    return gssRules;
  }
  
  public static String removeComments(String s) {
    // Remove c style comments
    return s.replaceAll("(?s)\\s*/\\*.*?\\*/\\s*", "").
    // Remove c++ style comments
             replaceAll("//.*\n", "\n");
  }


  public static GssRule parseRule(String rule) throws GssParseException {
    System.out.println("Parsing: " + rule);
    // split selector off by splitting on "{"
    int lbrace = rule.indexOf("{");
    int rbrace = rule.indexOf("}");
    if (lbrace == -1) {
      throw new GssParseException("Missing { in rule '" + rule + "'");
    }
    if (rbrace == -1) {
      throw new GssParseException("Missing } in rule '" + rule + "'");
    }
    String selector = rule.substring(0, lbrace).trim();
    String propertySet = rule.substring(lbrace + 1, rbrace).trim();
    List<GssSelector> selectors = parseSelectors(selector);
    List<GssProperty> gssproperties = null;
    try {
      gssproperties = parseProperties(propertySet);
    } catch (GssParseException e) {
      throw new GssParseException(e.getMessage() + ", for rule '" + rule + "'" , e);
    }
    return new GssRule(selectors, gssproperties);
  }

  private static List<GssProperty> parseProperties(String propertySet)
      throws GssParseException {
    // split by ";"
    String properties[] = propertySet.trim().split("\\s*[\r\n;\\}]+\\s*");
    ArrayList<GssProperty> gssProperties = new ArrayList<GssProperty>();
    for (String property : properties) {
      gssProperties.add(parseProperty(property.trim()));
    }
    return gssProperties;
  }

  private static GssProperty parseProperty(String property)
      throws GssParseException {
    // split by ":"
    int colonIndex = property.indexOf(":");
    if (colonIndex == -1) {
      throw new GssParseException("Missing : when parsing '" + property + "'");
    }
    String propertyName = property.substring(0, colonIndex).trim();
    String propertyValue = property.substring(colonIndex + 1).trim();
    return new GssProperty(propertyName, propertyValue);
  }

  public static List<GssSelector> parseSelectors(String selector) {
    // split selector by comma
    String selectors[] = selector.split("\\s*,\\s*");
    ArrayList<GssSelector> gssSelectors = new ArrayList<GssSelector>();
    for (String sel : selectors) {
      gssSelectors.add(parseSelector(sel.trim()));
    }
    return gssSelectors;
  }

  private static GssSelector parseSelector(String selector) {
    // split by combinator
    String simpleSelectors[] = selector.split("\\s+");
    GssSelector gssSelector = new GssSelector();
    for (String simpleSelector : simpleSelectors) {
      simpleSelector = simpleSelector.trim();
      int dotIndex = simpleSelector.indexOf(".");

      String elemName = null;
      if (simpleSelector.startsWith("*") || simpleSelector.startsWith(".")) {
        elemName = "*";
      }
      String clazz = null;
      if (dotIndex != -1) {
        clazz = simpleSelector.substring(dotIndex + 1).trim();
        if (elemName == null) {
          elemName = simpleSelector.substring(0, dotIndex);
        }
      } else if (elemName == null) {
        elemName = simpleSelector.trim();
      }
      gssSelector.push(elemName, GssSelector.SPACE_COMBINATOR, clazz);
    }
    return gssSelector;
  }

  public static void main(String[] args) throws GssParseException {
    List<GssRule> rules = parse("");
    System.err.println(rules);
  }
}
