package org.timepedia.chronoscope.client.browser;

import org.timepedia.exporter.client.Exportable;

/**
 * Called whent he user clicks on a menu item
 *
 * @gwt.exportClosure
 * @gwt.exportPackage chronoscope
 */
public interface ChronoscopeClickListener extends Exportable {

  void click(String label);
}
