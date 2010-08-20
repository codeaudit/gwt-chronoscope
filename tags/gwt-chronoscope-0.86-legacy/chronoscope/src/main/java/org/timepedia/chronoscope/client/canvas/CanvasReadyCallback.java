package org.timepedia.chronoscope.client.canvas;

/**
 * Invoked when the creation of a Canvas succeeds and is ready for rendering
 */
public interface CanvasReadyCallback {

  public void onCanvasReady(Canvas canvas);
}
