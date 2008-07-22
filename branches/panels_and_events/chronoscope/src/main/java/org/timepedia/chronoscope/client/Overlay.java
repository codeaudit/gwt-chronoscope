package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.overlays.OverlayClickListener;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Export;

/**
 * Interface implemented by Markers and other clases which overlay the plot
 *
 * @gwt.exportPackage chronoscope
 */
@ExportPackage("chronoscope")
public interface Overlay extends Exportable {

  /**
   * Allows a caller to register for click events on this Overlay
   *
   * @gwt.export addOverlayListener
   */
  @Export
  void addOverlayClickListener(OverlayClickListener cl);

  /**
   * Fire a click event for this overlay
   */
  void click(int x, int y);

  /**
   * Draw the overlay on the given layer, with text rendered on the given
   * textLayer
   */
  void draw(Layer layer, String textLayer);

  double getDomainX();

  double getRangeY();

  /**
   * True if the screen coordinates (x,y) relative to the Plot bounds are inside
   * the Overlay
   */
  boolean isHit(int x, int y);

  /**
   * Removes an OverlayClickListener from this overlay
   */
  void removeOverlayClickListener(OverlayClickListener cl);

  /**
   * Sets the plot on which this Overlay is bound
   */
  void setPlot(XYPlot view);
}
