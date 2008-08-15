package org.timepedia.chronoscope.client;

import com.google.gwt.libideas.event.virtual.shared.VirtualEvent;

import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.View;

public class ViewContainer extends AbstractContainer<Component, Container> {

  private View view;

  public ViewContainer(View view) {
    this.view = view;
    setBounds(new Bounds(0, 0, view.getViewWidth(), view.getViewHeight()));
  }

  public void onEvent(VirtualEvent event) {
  }

  public void repaint() {
    paint(view.getCanvas().getRootLayer());
  }

  protected void onComponentAdded(Component component) {
    component.setBounds(new Bounds(getBounds()));
  }

  protected void onComponentRemoved(Component component) {
  }
}
