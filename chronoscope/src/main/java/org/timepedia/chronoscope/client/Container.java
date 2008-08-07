package org.timepedia.chronoscope.client;

public interface Container<S extends Component, T extends Container>
    extends Component<T>, Iterable<S> {

  /**
   * Adds a Component to this container. Component may be preconfigured with
   * Bounds (absolute positioning) or Container may enforce a layout and
   * adjust the Bounds of each component added, depending on layout policy.
   */
  void add(S c);

  /**
   * Removes a Coomponent from this container.
   */
  void remove(S c);
}
