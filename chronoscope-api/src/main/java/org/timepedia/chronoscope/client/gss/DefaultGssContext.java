package org.timepedia.chronoscope.client.gss;


/**
 * Hardcoded default stylesheet.
 */
public class DefaultGssContext extends MockGssContext {
  private boolean showAxisLabels = true;

    public void setShowAxisLabels(boolean showAxisLabels) {
        this.showAxisLabels = showAxisLabels;
    }

    public GssProperties getProperties(GssElement gssElem, String pseudoElt) {
        GssProperties p = super.getProperties(gssElem, pseudoElt);
        if ("label".equals(gssElem.getType()) && "axis".equals(gssElem.getParentGssElement().getType())) {
          p.visible = this.showAxisLabels;
        }
        return p;
    }

}
