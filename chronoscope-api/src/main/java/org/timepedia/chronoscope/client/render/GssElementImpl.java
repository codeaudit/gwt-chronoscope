package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.gss.GssElement;

/**
 * Convenient class for creating named GssElements
 */
public class GssElementImpl implements GssElement {

  private final String type;

  private final GssElement parent;

  private String typeClass = null;

  public GssElementImpl(String type, GssElement parent) {
    this.type = type;
    this.parent = parent;
  }

  public GssElementImpl(String type, GssElement parent, String typeClass) {
    this(type, parent);
    this.typeClass = typeClass;
  }

  public GssElement getParentGssElement() {
    return parent;
  }

  public String getType() {
    return type;
  }

  public String getTypeClass() {
    return typeClass;
  }
}
