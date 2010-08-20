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
          "<a name=\"" + gssElem.getName() + "\">" + gssElem.getName());
      pc("elemdoc", gssElem.getDocString());
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
      pc("elemexample", gssElem.getExampleString());
      p("</tr>");
    }

    p("</table>");

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
      p("<a name=\"" + prop.getName() + "\">" + prop.getName());
      p("</td>");
      pc("propdoc", prop.getDocString());
      pc("propunit",
          "<a href=\"#" + prop.getValueType() + "\">" + prop.getValueType()
              + "</a>");
      p("</tr>");
    }
    p("</table>");

    p("<table class=\"gssunitdoc\" border=1 cellspacing=0><tr><th>Unit Type</th><th>Description</th></tr>");
    for (GssPropertyManager.GssPropertyType.TypeUnits tu : GssPropertyManager
        .GssPropertyType.TypeUnits.values()) {
      p("<tr>");
      p("<td class=unitname>");
      p("<a name=#\"" + tu + "\">" + tu);
      p("</td>");
      pc("unitdoc", tu.getDocString());
      p("</tr>");
    }

    p("</table>");
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
