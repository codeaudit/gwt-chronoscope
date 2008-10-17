package org.timepedia.chronoscope.client;

import com.google.gwt.libideas.event.virtual.shared.VKeyboardEvent;
import com.google.gwt.libideas.event.virtual.shared.VMouseEvent;
import com.google.gwt.libideas.event.virtual.shared.VirtualEvent;

import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class AbstractContainer<S extends Component, T extends Container>
    extends AbstractComponent<T> implements Container<S, T> {

  private ArrayList<S> components = new ArrayList<S>();

  public void add(S component) {
    Container<S, T> parent = component.getContainer();
    if (parent != null) {
      parent.remove(component);
    }
    components.add(component);
    component.setContainer(this);
    onComponentAdded(component);
  }

  /**
   * If empty, returns parent container's preferred bounds. Else, assume
   * absolute positioning policy, returns max bounding rectangle of all
   * contained preferred bounds.
   */
  public Bounds getPreferredBounds() {
    if (components.isEmpty()) {
      return super.getPreferredBounds();
    }
    Bounds b = new Bounds(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0);

    for (Component c : components) {
      Bounds bc = c.getPreferredBounds();
      b.x = Math.min(b.x, bc.x);
      b.y = Math.min(b.y, bc.y);
      b.width = Math.max(b.width, bc.x + bc.width - b.x);
      b.height = Math.max(b.height, bc.y + bc.height - b.y);
    }
    return b;
  }

  public Iterator<S> iterator() {
    return components.iterator();
  }

  public void layout() {
    for (Component c : components) {
      if (c instanceof Container) {
        ((Container) c).layout();
      }
    }
  }

  public void onEvent(VirtualEvent event) {
    if (event instanceof VMouseEvent) {
      // if event has coordinates (mouse)
      //   find component that contains coordinates
      //   invoke onEvent on this component 
    } else if (event instanceof VKeyboardEvent) {
      // if event is keyboard event
      // check which component has focus
      // invoke onEvent on focused component
      //Êwe may need to take some ideas from this:
      //http://java.sun.com/j2se/1.5.0/docs/api/java/awt/doc-files/FocusSpec.html
      // to implement focus management
    }
    // else, check if propagation of event was stopped or default cancelled
    // otherwise, run any event handlers registered on this component
  }

  public void paint(Layer layer) {
    for (S component : components) {
      Bounds b = component.getBounds();
      // isolate component rendering
      layer.save();
      layer.translate(b.x, b.y);
      //TODO check performance of adding layer.clip()
      // layer.beginPath(); layer.rect(0, 0, b.width, b.height); 
      // layer.closePath(); layer.clip();
      component.paint(layer);
      layer.restore();
    }
  }

  public void remove(S component) {
    components.remove(component);
    component.setContainer(null);
    onComponentRemoved(component);
  }

  /**
   * Subclasses implement to perform layout, etc.
   */
  protected abstract void onComponentAdded(S component);

  /**
   * Subclasses implement to perform layout, etc.
   */
  protected abstract void onComponentRemoved(S component);
}
