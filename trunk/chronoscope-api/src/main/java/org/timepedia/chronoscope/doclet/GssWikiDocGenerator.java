package org.timepedia.chronoscope.doclet;

import org.timepedia.chronoscope.client.gss.GssPropertyManager;

import java.util.Iterator;

/**
 * Print Gss Docs
 */
public class GssWikiDocGenerator {

  public static void main(String[] args) {
    GssWikiDocGenerator gssDoc = new GssWikiDocGenerator();
    gssDoc.generateGssDocs();
  }

  public void generateGssDocs() {
    p("= Chronoscope GSS Reference =");
    p("== Elements ==");
    p("|| Element || Description || Child Elements || Properties || Sample ||");

    Iterator<GssPropertyManager.GssElementType> gssIt = GssPropertyManager
        .getAllElements();
    while (gssIt.hasNext()) {
      GssPropertyManager.GssElementType gssElem = gssIt.next();
      String line = "||  ====" + gssElem.getName() + "====  " +
        "|| " + gssElem.getDocString() + " || ";

      for (GssPropertyManager.GssElementType child : gssElem.getChildTypes()) {
        line += "\n  * [#" + child.getName() + " " + child.getName() + "] ";
      }
      
      line += " || ";

      for (GssPropertyManager.GssPropertyType prop : gssElem.getProperties()) {
        line += "\n  * [#" + prop.getName() + " "+ prop.getName() + "] ";
      }
      line += "{{{"+ gssElem.getExampleString() + " }}}  ||";

      p(line);
    }


    p("== Properties ==");

    p("|| Property Name || Description || Units ||");

    Iterator<GssPropertyManager.GssPropertyType> propIt = GssPropertyManager
        .getAllProperties();
    while (propIt.hasNext()) {
      GssPropertyManager.GssPropertyType prop = propIt.next();
      String line = "||  ==== "+ prop.getName() + " ====  || ";
      line += prop.getDocString() + " || ";
      line += " [#" + prop.getValueType() + " " + prop.getValueType() + "] ||";
    }

    p("=== Units ===");

    p("|| Unit Type || Description ||");
    for (GssPropertyManager.GssPropertyType.TypeUnits tu : GssPropertyManager
        .GssPropertyType.TypeUnits.values()) {
      p("|| ==== " + tu + " ==== || " + tu.getDocString() + " || ");
    }

  }


  protected void p(String str) {
    System.out.println(str);
  }
}