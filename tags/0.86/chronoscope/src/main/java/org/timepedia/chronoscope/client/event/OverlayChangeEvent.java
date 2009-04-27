package org.timepedia.chronoscope.client.event;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.Export;

/**
 *
 */
@ExportPackage("chronoscope")
public class OverlayChangeEvent extends PlotEvent implements Exportable {

  public static Type<OverlayChangeEvent, OverlayChangeHandler> TYPE
      = new Type<OverlayChangeEvent, OverlayChangeHandler>() {
    @Override
    protected void fire(OverlayChangeHandler changeHandler,
        OverlayChangeEvent event) {
      changeHandler.onOverlayChanged(event);
    }
  };

  @Export
  public Overlay getOverlay() {
    return overlay;
  }

  private Overlay overlay;

  public OverlayChangeEvent(XYPlot plot, Overlay o) {
    super(plot);
    this.overlay = o;
  }

  protected Type getType() {
    return TYPE;
  }
}