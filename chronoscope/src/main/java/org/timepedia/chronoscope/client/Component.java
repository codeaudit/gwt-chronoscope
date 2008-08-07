package org.timepedia.chronoscope.client;

import com.google.gwt.libideas.event.shared.AbstractEvent;
import com.google.gwt.libideas.event.virtual.shared.VirtualEvent;

import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;

public interface Component<T extends Container> {

  /**
   * Returns true if (localx,localy) is contained within this component's
   * shape.
   */
  boolean contains(double localx, double localy);

  /**
   * Return the bounds of this component within the parent component's
   * coordinate system. Do not modify, treat as immutable.
   */
  Bounds getBounds();

  /**
   * Return the parent container of this component, or null if it is a root.
   */
  T getContainer();

  /**
   * Fires any event handlers related to the incoming event.
   */
  void onEvent(VirtualEvent event);

  /**
   * Paint the component. The component's coordinate system origin will be
   * adjusted so that 0,0 is the upper-left point, and drawing will be clipped
   * to a rectangle spanning the width/height.
   */
  void paint(Layer layer);

  /**
   * Set the bounds of this component.  (Treated as immutable)
   */
  void setBounds(Bounds bounds);

  /**
   * Called when this component is added to a container.
   */
  void setContainer(T container);
}
