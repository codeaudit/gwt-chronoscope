package org.timepedia.chronoscope.client.canvas.mock;

import org.timepedia.chronoscope.client.ChronoscopeMenu;
import org.timepedia.chronoscope.client.InfoWindow;
import org.timepedia.chronoscope.client.browser.ChronoscopeClickListener;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.util.PortableTimer;
import org.timepedia.chronoscope.client.util.PortableTimerTask;

/**
 *
 */
public class MockView extends View {

  public ChronoscopeMenu createChronoscopeMenu(int x, int y) {
    return new ChronoscopeMenu() {

      public void addMenuBar(String label, ChronoscopeMenu subMenu) {
      }

      public void addMenuItem(String label, ChronoscopeClickListener ccl) {
      }

      public void removeAllMenuItems() {
      }

      public void hide() {
      }

      public void show(int x, int y) {
      }
    };
  }

  public PortableTimer createTimer(final PortableTimerTask run) {
    return new PortableTimer() {
      public void cancelTimer() {
      }

      public void schedule(int delayMillis) {
        run.run(this);
      }

      public void scheduleRepeating(int periodMillis) {
        run.run(this);
      }

      public double getTime() {
        return System.currentTimeMillis();
      }
    };
  }

  public InfoWindow openInfoWindow(String html, double x, double y) {
    return new InfoWindow() {
      public void close() {

      }

      public void setPosition(double x, double y) {
      }

      public void addInfoWindowClosedHandler(InfoWindowClosedHandler handler) {
      }
    };
  }

  protected Canvas createCanvas(int width, int height) {
    return new MockCanvas(this, width, height);
  }
}
