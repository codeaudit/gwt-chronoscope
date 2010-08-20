package org.timepedia.chronoscope.java2d.canvas;

import org.timepedia.chronoscope.client.ChronoscopeMenu;
import org.timepedia.chronoscope.client.InfoWindow;
import org.timepedia.chronoscope.client.InfoWindowClosedHandler;
import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.util.DateFormatter;
import org.timepedia.chronoscope.client.util.PortableTimer;
import org.timepedia.chronoscope.client.util.PortableTimerTask;
import org.timepedia.chronoscope.client.util.date.DateFormatterFactory;
import org.timepedia.chronoscope.java2d.JDKDateFormatter;

import java.util.TimerTask;

/**
 * Java2D View implementation, by default, no interactivity is supported to
 * allow simple servlet based rendering
 */
public class ViewJava2D extends View {

  public ViewJava2D() {
    DateFormatterFactory
        .setDateFormatterFactory(new DateFormatterFactory() {
          public DateFormatter getDateFormatter(String format) {
            return new JDKDateFormatter(format);
          }
        });
  }

  abstract static class MockTimerTask extends TimerTask
      implements PortableTimer {

    public MockTimerTask() {
    }

    public void cancelTimer() {
    }

    public double getTime() {
      return System.currentTimeMillis();
    }

    public void schedule(int delayMillis) {
      run();
    }

    public void scheduleRepeating(int periodMillis) {
      run();
    }
  }

  public ChronoscopeMenu createChronoscopeMenu(int x, int y) {
    return new MockChronoscopeMenuJava2D(x, y);
  }

  public PortableTimer createTimer(final PortableTimerTask run) {
    return new MockTimerTask() {

      public void run() {
        run.run(this);
      }
    };
  }

  /**
   * Create a view with the given imensions, GssContext, calling the
   * ViewReadyCallback when all Canvases are created and the view layer is
   * ready.
   */
  public void initialize(final int width, final int height,
      boolean doubleBuffered, final GssContext gssContext,
      final ViewReadyCallback callback) {
    super.initialize(width, height, doubleBuffered, gssContext, callback);
  }

  public InfoWindow createInfoWindow(String html, double x, double y) {
    // do nothing, override specifically for Swing apps
    // a fancy server side implement could generate image maps that open HTML
    return new InfoWindow() {
      public void close() {
      }

      public void setPosition(double x, double y) {
      }

      public void addInfoWindowClosedHandler(InfoWindowClosedHandler handler) {
      }

      public void open() {

      }
    };
  }

  protected Canvas createCanvas(int width, int height) {
    return new CanvasJava2D(this, width, height);
  }
}
