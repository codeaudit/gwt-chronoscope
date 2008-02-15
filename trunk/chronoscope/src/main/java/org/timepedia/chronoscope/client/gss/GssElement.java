package org.timepedia.chronoscope.client.gss;

/**
 * A GssElement represents a phantom element to which Graph Style Sheets are
 * applied. In the browser, these are realized as real DOM elements with the
 * given type as the element name, as type class as the class attribute. The CSS
 * engine of the browser is leveraged to target the (hidden) GSS DOM elements.
 * <p/> For a server side implementation, GssElement may be implemented as
 * specialized classes used by the Apache Batik CSS Engine. <p/> For some
 * applications, textual GSS can be avoided, and a hard coded implementation of
 * GssContext can be used to return fixed answers for a given GssElement. This
 * is most useful when deploying specialized J2ME phone versions of Chronoscope
 */
public interface GssElement {

  GssElement getParentGssElement();

  String getType();

  String getTypeClass();
}
