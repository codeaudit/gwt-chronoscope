package org.timepedia.chronoscope.client.browser;

import com.google.gwt.user.client.Element;

import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.gss.GssContext;

/**
 * View types that live in the browser
 */
public interface DOMView {

  public void exportFunctions();

  public Element getElement();

  public void initialize(final Element element, final int width,
      final int height, final boolean interactive, GssContext gssContext,
      final ViewReadyCallback callback);
}
