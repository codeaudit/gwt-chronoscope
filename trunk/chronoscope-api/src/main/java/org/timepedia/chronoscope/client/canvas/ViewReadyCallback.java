package org.timepedia.chronoscope.client.canvas;

import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.ExportClosure;

/**
 * Interface used by View implements to notify clients when the View is ready
 * for use.
 *
 */
@ExportPackage("chronoscope")
@ExportClosure
public interface ViewReadyCallback extends Exportable {

  void onViewReady(View view);
}
