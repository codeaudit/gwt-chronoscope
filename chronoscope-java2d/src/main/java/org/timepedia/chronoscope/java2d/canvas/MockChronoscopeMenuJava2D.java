package org.timepedia.chronoscope.java2d.canvas;

import org.timepedia.chronoscope.client.ChronoscopeMenu;
import org.timepedia.chronoscope.client.browser.ChronoscopeClickListener;

/**
 * By default, do nothing, so as to support Servlet rendering
 */
public class MockChronoscopeMenuJava2D implements ChronoscopeMenu {

  public MockChronoscopeMenuJava2D(int x, int y) {
  }

  public void addMenuBar(String label, ChronoscopeMenu subMenu) {
  }

  public void addMenuItem(final String label,
      final ChronoscopeClickListener ccl) {
  }

  public void hide() {
  }

  public void show(int x, int y) {
  }
}
