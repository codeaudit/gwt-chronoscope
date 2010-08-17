package org.timepedia.chronoscope.doclet;

import org.timepedia.chronoscope.client.gss.GssPropertyManager;

import java.util.Iterator;

/**
 * Print Gss Docs
 */
public class GssDocGenerator {

  public static void main(String[] args) {
    GssDocGenerator gssDoc = new GssDocGenerator();
    gssDoc.generateGssDocs();
  }

  public void generateGssDocs() {
    p("<html><head><link rel='stylesheet' type='text/css' href='gssdoc.css' /></head><body>");

    p("<h2>Elements</h2>");
    p("<table class=\"gsselemdoc\" border=\"1\"cellspacing=0>\n" + "<tr>\n"
        + "    <th>\n" + "        Element \n" + "    </th>\n" + "    <th>\n"
        + "        Description\n" + "    </th>\n" + "    <th>\n"
        + "        Child Elements\n" + "    </th>\n" + "    <th>\n"
        + "        Properties\n" + "    </th>\n" + "    <th>Sample</th>\n"
        + "</tr>");

    Iterator<GssPropertyManager.GssElementType> gssIt = GssPropertyManager
        .getAllElements();
    while (gssIt.hasNext()) {
      GssPropertyManager.GssElementType gssElem = gssIt.next();
      p("<tr>");
      pc("elemname",
          "<div id=\"" + gssElem.getName() + "\">" + gssElem.getName() + "</div>");
      pc("elemdoc", "<div>" + gssElem.getDocString() + "</div>");
      p("<td class=elemchild><ul>");
      for (GssPropertyManager.GssElementType child : gssElem.getChildTypes()) {
        p("<li><a href=\"#" + child.getName() + "\">" + child.getName()
            + "</a>");
      }
      p("</ul></td>");

      p("<td class=elemprop><ul>");
      for (GssPropertyManager.GssPropertyType prop : gssElem.getProperties()) {
        p("<li><a href=\"#" + prop.getName() + "\">" + prop.getName() + "</a>");
      }
      p("</ul></td>");
      pc("elemexample", formatExample(gssElem.getExampleString()));
      p("</tr>");
    }

    p("</table>");

    p("<h2>Properties</h2>");
    p("<table class=\"gsspropdoc\" border=\"1\"cellspacing=0>\n" + "<tr>\n"
        + "    <th>\n" + "Property Name \n" + "    </th>\n" + "    <th>\n"
        + "        Description\n" + "    </th>\n" + "    <th>\n"
        + "        Units\n" + "    </th>\n" + "</tr>");

    Iterator<GssPropertyManager.GssPropertyType> propIt = GssPropertyManager
        .getAllProperties();
    while (propIt.hasNext()) {
      GssPropertyManager.GssPropertyType prop = propIt.next();
      p("<tr>");
      p("<td class=propaname>");
      p("<div id=\"" + prop.getName() + "\">" + prop.getName() + "</div>");
      p("</td>");
      pc("propdoc", prop.getDocString());
      pc("propunit",
          "<a href=\"#" + prop.getValueType() + "\">" + prop.getValueType()
              + "</a>");
      p("</tr>");
    }
    p("</table>");

    p("<h2>Units</h2>");
    p("<table class=\"gssunitdoc\" border=1 cellspacing=0><tr><th>Unit Type</th><th>Description</th></tr>");
    for (GssPropertyManager.GssPropertyType.TypeUnits tu : GssPropertyManager
        .GssPropertyType.TypeUnits.values()) {
      p("<tr>");
      p("<td class=unitname>");
      p("<div id=\"" + tu + "\">" + tu + "</div>");
      p("</td>");
      pc("unitdoc", tu.getDocString());
      p("</tr>");
    }

    p("</table></body></html>");
  }
  
  private String formatExample(String s) {
     String r = "";
     String a[] = s.split("/\\*");
     if (a.length == 2) {
       s = a[0];
       r = "/*" + a[1];
     }
     s = s.replaceAll("([\\{\\;])", "$1\n").replaceAll("([\\}])", "\n$1")
          .replaceAll("(?s)\\s+\n", "\n").replaceAll("([^;\\}\\{])\n", "$1;\n");
     
     return "<pre>\n" + r + "\n" + s + "\n" + "</pre>\n";
  }

  private void pc(String clz, String name) {
    p("<td class=\"" + clz + "\">");
    p(name);
    p("</td>");
  }

  protected void p(String str) {
    System.out.println(str);
  }
}
