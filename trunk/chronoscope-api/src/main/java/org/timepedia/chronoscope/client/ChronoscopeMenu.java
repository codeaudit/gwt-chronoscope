package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.browser.ChronoscopeClickListener;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.NoExport;

/**
 * Implement this and ChronoscopeMenuFactory to create alternative popup menus
 * for Chronoscope.
 *
 * @gwt.export
 * @gwt.exportPackage chronoscope
 */
@Export
@ExportPackage("chronoscope")
public interface ChronoscopeMenu extends Exportable {

  public void addMenuBar(String label, ChronoscopeMenu subMenu);

  public void addMenuItem(final String label,
      final ChronoscopeClickListener ccl);

  void removeAllMenuItems();
  
  void hide();

  void show(int x, int y);
}
