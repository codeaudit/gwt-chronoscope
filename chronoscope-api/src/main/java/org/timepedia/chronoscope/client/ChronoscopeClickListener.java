package org.timepedia.chronoscope.client;

import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.ExportPackage;

/**
 * Called whent he user clicks on a menu item
 *
 */
@ExportClosure
@ExportPackage("chronoscope")
public interface ChronoscopeClickListener extends Exportable {

  void click(String label);
}
