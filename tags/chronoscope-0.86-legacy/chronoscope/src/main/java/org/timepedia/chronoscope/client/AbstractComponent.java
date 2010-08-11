package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.canvas.Bounds;

public abstract class AbstractComponent<T extends Container>
    implements Component<T> {

  private T container;

  private Bounds bounds = new Bounds(0, 0, 0, 0);

  public boolean contains(double localx, double localy) {
    return true;
  }

  public Bounds getBounds() {
    return bounds;
  }

  public T getContainer() {
    return container;
  }

  /**
   * Default implementation, take up 100% of parent's preferred bounds,
   * otherwise return zero.
   */
  public Bounds getPreferredBounds() {
    return container == null ? new Bounds(0, 0, 0, 0)
        : container.getPreferredBounds();
  }

  public void setBounds(Bounds bounds) {
    this.bounds = bounds;
  }

  public void setContainer(T container) {
    this.container = container;
  }
}
