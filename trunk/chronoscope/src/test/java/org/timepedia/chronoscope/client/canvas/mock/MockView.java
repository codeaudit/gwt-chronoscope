package org.timepedia.chronoscope.client.canvas.mock;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.dom.client.Document;
import com.google.gwt.core.client.GWT;

import org.timepedia.chronoscope.client.ChronoscopeMenu;
import org.timepedia.chronoscope.client.InfoWindow;
import org.timepedia.chronoscope.client.InfoWindowClosedHandler;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.browser.ChronoscopeClickListener;
import org.timepedia.chronoscope.client.browser.DOMView;
import org.timepedia.chronoscope.client.browser.GwtView;
import org.timepedia.chronoscope.client.canvas.Canvas;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.util.PortableTimer;
import org.timepedia.chronoscope.client.util.PortableTimerTask;

import java.util.Date;

/**
 *
 */
public class MockView extends GwtView implements DOMView {

  public MockView() {
  }

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

  /**
   * Creates a PortableTimer based on GWT's Timer class.
   */
  private PortableTimer createGWTTimer(final PortableTimerTask run) {
    return new BrowserTimer() {
      public void cancelTimer() {
        cancel();
      }
      @Override
       public void schedule(int i) {
        super.schedule(50);   
      }

      public double getTime() {
        return new Date().getTime();
      }

      public void run() {
        run.run(this);
      }
    };
  }

  abstract static class BrowserTimer extends Timer implements PortableTimer {

  }

  public PortableTimer createTimer(final PortableTimerTask run) {
    if (GWT.isClient()) {
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
    } else {
      return createGWTTimer(run);
    }
  }

  public InfoWindow createInfoWindow(String html, double x, double y) {
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
    return new MockCanvas(this, width, height);
  }

  public void exportFunctions() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void focus() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public Element getElement() {
    return (Element) (Object) Document.get().getBody();
  }

  public void initialize(Element element, int width, int height,
      boolean interactive, GssContext gssContext, ViewReadyCallback callback) {
    super.initialize(width, height, false, gssContext, callback);
  }
}
