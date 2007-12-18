package org.timepedia.chronoscope.client.overlays;

import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.exporter.client.Exportable;

/**
 * Implemented by classes which want to be notifed when the user clicks on an overlay
 *
 * @gwt.exportPackage chronoscope
 * @gwt.exportClosure
 */
public interface OverlayClickListener extends Exportable {
    void onOverlayClick(Overlay overlay, int x, int y);
}
