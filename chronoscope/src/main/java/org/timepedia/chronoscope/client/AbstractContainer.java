package org.timepedia.chronoscope.client;

import com.google.gwt.libideas.event.virtual.shared.EventData;
import com.google.gwt.libideas.event.virtual.shared.VirtualEvent;
import com.google.gwt.libideas.event.virtual.shared.VMouseEvent;
import com.google.gwt.libideas.event.virtual.shared.VKeyboardEvent;
import com.google.gwt.libideas.event.shared.AbstractEvent;

import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class AbstractContainer<S extends Component, T extends Container>
    implements Container<S, T> {

  private ArrayList<S> components = new ArrayList<S>();

  private T container;

  private Bounds bounds;

  public Iterator<S> iterator() {
    return components.iterator();
  }

  public boolean contains(double localx, double localy) {
    return true;
  }

  public Bounds getBounds() {
    return bounds;
  }

  public T getContainer() {
    return container;
  }

  public void onEvent(VirtualEvent event) {
    if(event instanceof VMouseEvent) {
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
    for(S component : components) {
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

  public void setBounds(Bounds bounds) {
    this.bounds=bounds;
  }

  public void setContainer(T container) {
    this.container = container;
  }

  public void add(S component) {
    components.add(component);
    onComponentAdded(component);
  }

  /**
   * Subclasses implement to perform layout, etc.
   */
  protected abstract void onComponentAdded(S component);

  public void remove(S component) {
    components.remove(component);
    onComponentRemoved(component);
  }

  /**
   * Subclasses implement to perform layout, etc.
   */
  protected abstract void onComponentRemoved(S component);
}
