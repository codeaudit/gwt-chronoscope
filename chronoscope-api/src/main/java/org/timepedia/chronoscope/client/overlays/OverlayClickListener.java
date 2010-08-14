package org.timepedia.chronoscope.client.overlays;

import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.ExportClosure;

/**
 * Implemented by classes which want to be notifed when the user clicks on an
 * overlay
 *
 */
@ExportPackage("chronoscope")
@ExportClosure
public interface OverlayClickListener extends Exportable {

  void onOverlayClick(Overlay overlay, int x, int y);
}
