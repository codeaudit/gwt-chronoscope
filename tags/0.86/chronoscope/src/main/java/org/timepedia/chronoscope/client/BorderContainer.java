package org.timepedia.chronoscope.client;


import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;

import java.util.Iterator;

public class BorderContainer implements Container<Component, ViewContainer> {

  private Component center;

  private Component top;

  private Component bottom;

  private Component left;

  private Component right;

  public void add(Component c) {
    throw new UnsupportedOperationException(
        "BorderContainer.add() cannot be invoked, use BorderContainer.add(Position,Component) instead");
  }

  public void add(Position position, Component c) {
    switch (position) {
      case TOP:
        top = c;
        break;
      case BOTTOM:
        bottom = c;
        break;
      case LEFT:
        left = c;
        break;
      case RIGHT:
        right = c;
        break;
      case CENTER:
        center = c;
        break;
    }
    layout();
  }

  public void layout() {
    // start with center filling whole area
    Bounds center = new Bounds(getBounds());
    Bounds topBounds = top != null ? new Bounds(top.getPreferredBounds())
        : null;
    Bounds leftBounds = left != null ? new Bounds(left.getPreferredBounds())
        : null;
    Bounds rightBounds = right != null ? new Bounds(right.getPreferredBounds())
        : null;
    Bounds bottomBounds = bottom != null ? new Bounds(
        bottom.getPreferredBounds()) : null;

    // force center's left border to be after left component's right width
    if (leftBounds != null) {
      center.x = leftBounds.width;
      center.width -= center.x;
    }
    // ditto for right side
    if (right != null) {
      center.width -= rightBounds.width;
    }

    if (topBounds != null) {
      center.y = topBounds.height;
    }

    if (bottomBounds != null) {
      center.height -= bottomBounds.height;
    }

    if (leftBounds != null) {
      leftBounds.x = 0;
      leftBounds.y = center.y;
      leftBounds.height = center.height;
      left.setBounds(leftBounds);
      if (left instanceof Container) {
        ((Container) left).layout();
      }
    }

    if (rightBounds != null) {
      rightBounds.x = 0;
      rightBounds.y = center.y;
      rightBounds.height = center.height;
      right.setBounds(rightBounds);
      if (right instanceof Container) {
        ((Container) right).layout();
      }
    }

    if (topBounds != null) {
      topBounds.x = 0;
      topBounds.y = 0;
      top.setBounds(topBounds);
      if (top instanceof Container) {
        ((Container) top).layout();
      }
    }

    if (bottomBounds != null) {
      bottomBounds.x = 0;
      bottomBounds.y = 0;
      bottom.setBounds(bottomBounds);
      if (bottom instanceof Container) {
        ((Container) bottom).layout();
      }
    }
  }
  
  public void remove(Component c) {
    c.setContainer(null);
    if (top == c) {
      top = null;
    } else if (bottom == c) {
      bottom = null;
    } else if (left == c) {
      left = null;
    } else if (right == c) {
      right = null;
    } else if (center == c) {
      center = null;
    } else {
      throw new IllegalArgumentException(
          "Container.remove() called for Component which is not contained.");
    }
    layout();
  }

  public boolean contains(double localx, double localy) {
    return true;
  }

  public Bounds getBounds() {
    return getPreferredBounds();
  }

  public ViewContainer getContainer() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public Bounds getPreferredBounds() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

//  public void onEvent(VirtualEvent event) {
//    To change body of implemented methods use File | Settings | File Templates.
//  }

  public void paint(Layer layer) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void setBounds(Bounds bounds) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void setContainer(ViewContainer container) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public Iterator<Component> iterator() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public static enum Position {

    TOP, BOTTOM, LEFT, RIGHT, CENTER
  }
}
