package org.timepedia.chronoscope.client.overlays;

import com.google.gwt.gen2.event.shared.HandlerRegistration;
import com.google.gwt.gen2.event.shared.HandlerManager;
import com.google.gwt.gen2.event.dom.client.ChangeHandler;

import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.event.ChartDragEndEvent;
import org.timepedia.chronoscope.client.event.ChartDragEndHandler;
import org.timepedia.chronoscope.client.event.ChartDragEvent;
import org.timepedia.chronoscope.client.event.ChartDragHandler;
import org.timepedia.chronoscope.client.event.ChartDragStartEvent;
import org.timepedia.chronoscope.client.event.ChartDragStartHandler;
import org.timepedia.chronoscope.client.event.OverlayChangeHandler;
import org.timepedia.chronoscope.client.event.OverlayChangeEvent;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.Export;

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

  public void setPlot(XYPlot plot) {
    if (plot != null) {
      this.plot = plot;
      dsh = ((DefaultXYPlot) plot).addHandler(ChartDragStartEvent.TYPE, this);
      dh = ((DefaultXYPlot) plot).addHandler(ChartDragEvent.TYPE, this);
      deh = ((DefaultXYPlot) plot).addHandler(ChartDragEndEvent.TYPE, this);
    } else if (dsh != null && dh != null && deh != null) {
      dsh.removeHandler();
      dh.removeHandler();
      deh.removeHandler();
    }
  }

  @Export("addChangeHandler")
  public void addOverlayChangeHandler(OverlayChangeHandler ch) {
     if(manager == null) {
       manager = new HandlerManager(this);
     }
  }
  
  public void onDragStart(ChartDragStartEvent event) {
    plot.setAnimating(true);
  }

  public void onDragEnd(ChartDragEndEvent event) {
    plot.setAnimating(false);
    if(manager != null) {
      manager.fireEvent(new OverlayChangeEvent(plot, this));
    }
  }

  public void onDrag(ChartDragEvent event) {
    ((DefaultXYPlot) plot).redraw(true);
  }

  public boolean isDraggable() {
    return draggable;
  }
}
