package org.timepedia.chronoscope.client.canvas;

import org.timepedia.exporter.client.Exportable;

/**
 * Interface used by View implements to notify clients when the View is ready
 * for use.
 *
 * @gwt.exportPackage chronoscope
 * @gwt.exportClosure
 */
public interface ViewReadyCallback extends Exportable {

  void onViewReady(View view);
}
