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
public class OverlayChangeEvent extends PlotEvent<OverlayChangeHandler> implements Exportable {

  public static Type<OverlayChangeHandler> TYPE
      = new Type<OverlayChangeHandler>();

  @Export
  public Overlay getOverlay() {
    return overlay;
  }

  private Overlay overlay;

  public OverlayChangeEvent(XYPlot plot, Overlay o) {
    super(plot);
    this.overlay = o;
  }

  public Type getAssociatedType() {
    return TYPE;
  }

  protected void dispatch(OverlayChangeHandler overlayChangeHandler) {
    overlayChangeHandler.onOverlayChanged(this);
  }
}