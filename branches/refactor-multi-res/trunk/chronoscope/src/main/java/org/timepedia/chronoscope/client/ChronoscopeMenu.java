package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.browser.ChronoscopeClickListener;
import org.timepedia.exporter.client.Exportable;

/**
 * Implement this and ChronoscopeMenuFactory to create alternative popup menus for Chronoscope
 *
 * @gwt.export
 * @gwt.exportPackage chronoscope
 */
public interface ChronoscopeMenu extends Exportable {
    void show(int x, int y);

    public void addMenuItem(final String label, final ChronoscopeClickListener ccl);

    public void addMenuBar(String label, ChronoscopeMenu subMenu);

    void hide();

}
