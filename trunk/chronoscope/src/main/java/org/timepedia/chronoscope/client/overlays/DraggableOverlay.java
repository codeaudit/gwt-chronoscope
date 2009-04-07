package org.timepedia.chronoscope.client.overlays;

import com.google.gwt.gen2.event.shared.AbstractEvent;
import com.google.gwt.gen2.event.shared.HandlerManager;
import com.google.gwt.gen2.event.shared.HandlerRegistration;

import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.event.ChartDragEndEvent;
import org.timepedia.chronoscope.client.event.ChartDragEndHandler;
import org.timepedia.chronoscope.client.event.ChartDragEvent;
import org.timepedia.chronoscope.client.event.ChartDragHandler;
import org.timepedia.chronoscope.client.event.ChartDragStartEvent;
import org.timepedia.chronoscope.client.event.ChartDragStartHandler;
import org.timepedia.chronoscope.client.event.OverlayChangeEvent;
import org.timepedia.chronoscope.client.event.OverlayChangeHandler;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 *
 */
@ExportPackage("chronoscope")
public abstract class DraggableOverlay
    implements Overlay, Draggable, ChartDragStartHandler, ChartDragEndHandler,
    ChartDragHandler, Exportable {

  private HandlerRegistration dsh;

  private HandlerRegistration dh;

  private HandlerRegistration deh;

  protected XYPlot plot;

  private boolean draggable;

  private HandlerManager manager = null;

  @Export
  public void setDraggable(boolean draggable) {
    this.draggable = draggable;
  }

  public void fire(AbstractEvent event) {
    if (manager != null) {
      manager.fireEvent(event);
    }
  }

  public void setPlot(XYPlot plot) {
    if (plot != null) {
      this.plot = plot;
      ensureHandler();
      dsh = manager.addHandler(ChartDragStartEvent.TYPE, this);
      dh = manager.addHandler(ChartDragEvent.TYPE, this);
      deh = manager.addHandler(ChartDragEndEvent.TYPE, this);
    } else if (dsh != null && dh != null && deh != null) {
      dsh.removeHandler();
      dh.removeHandler();
      deh.removeHandler();
    }
  }

  @Export("addChangeHandler")
  public void addOverlayChangeHandler(OverlayChangeHandler ch) {
    ensureHandler();
    manager.addHandler(OverlayChangeEvent.TYPE, ch);
  }

  @Export("addDragHandler")
  public void addOverlayDragHandler(ChartDragHandler ch) {
    ensureHandler();
    manager.addHandler(ChartDragEvent.TYPE, ch);
  }

  private void ensureHandler() {
    if (manager == null) {
      manager = new HandlerManager(this);
    }
  }

  public void onDragStart(ChartDragStartEvent event) {
    plot.setAnimating(true);
  }

  public void onDragEnd(ChartDragEndEvent event) {
    plot.setAnimating(false);
    if (manager != null) {
      manager.fireEvent(new OverlayChangeEvent(plot, this));
    }
  }

  public void onDrag(ChartDragEvent event) {
    ((DefaultXYPlot) plot).redraw(true);
  }

  public void onDrag(int currentX) {
  }

  public boolean isDraggable() {
    return draggable;
  }
}
