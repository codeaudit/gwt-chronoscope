package org.timepedia.chronoscope.client.canvas;

/**
 * Fundamental graphics rendering system of Chronoscope <p/> Canvas is the
 * fundamental graphics rendering system of Chronoscope. Developed as an
 * extended abstraction around the Browser (Safari) Canvas, it is used
 * throughout Chronoscope. Developers wishing to extend Chronoscope to other
 * rendering systems (Swing/Java2D, Flash, Silverlight, etc) should start here.
 * <p/> Canvas is an immediate mode abstraction which manages a collection of
 * named layers on which draw calls occur. Canvas permits a command buffering
 * abstraction through beginFrame() and endFrame(). beginFrame() may be used on
 * some implementations to lock a surface for update. No actual rendering is
 * guaranteed to take place until endFrame().
 */
public abstract class Canvas {

  private View view;

  protected Canvas(View view) {
    this.view = view;
  }

  /**
   * Attach the layer to the UI tree and invoke callback if and only if it is
   * ready for rendering operations. This is vitally important for plugin-based
   * canvas implementations like Flash, Silverlight, and Applets where invoking
   * native calls on unready plugins has caused browser crashes in our
   * experience.
   */
  public void attach(View view, CanvasReadyCallback canvasReadyCallback) {
    canvasReadyCallback.onCanvasReady(this);
  }

  /**
   * Called before the start of a drawing sequence
   */
  public void beginFrame() {
  }

  /**
   * Called after all canvases and all layers created, only used by Flash
   */
  public void canvasSetupDone() {
  }

  /**
   * Create a layer with the given id and bounds. Creating a layer that already
   * exists will return the previously created layer
   */
  public abstract Layer createLayer(String layerId, Bounds bounds);

  /**
   * Free the resources associated with a given layer
   */
  public abstract void disposeLayer(String layerId);

  public void disposeLayer(Layer layer) {
    disposeLayer(layer.getLayerId());
  }

  /**
   * Free the resources associated with a Canvas: all layers, etc
   */
   public abstract void dispose();

  /**
   * Flush the graphics pipline, flatten all layers, and render to the screen
   */
  public void endFrame() {
  }

  public Canvas getCanvas() {
    return this;
  }

  /**
   * Retrieve a layer (previously created) based on id
   */
  public abstract Layer getLayer(String layerId);

  public abstract Layer getRootLayer();

  /**
   * Returns the View which created this Canvas
   */
  public View getView() {
    return view;
  }

  public abstract CanvasImage createImage(String url);
}
