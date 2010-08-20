package org.timepedia.chronoscope.client;

import com.google.inject.Singleton;

import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;

/**
 * Cross-platform Factory class for Chronoscope Charts.
 */
public abstract class Chronoscope<T> {

  protected interface ChronoscopeInjector<T> {

    Chronoscope<T> get();
    ComponentFactory getComponentFactory();
  }

  public static Chronoscope get() {
    throw new UnsupportedOperationException(
        "You must used a platform specific factory");
  }

  public abstract T createChart(Datasets datasets, int width, int height,
      ViewReadyCallback callback);

  protected abstract ChronoscopeInjector<T> getInjector();

  public ComponentFactory getComponentFactory() {
    return getInjector().getComponentFactory();
  }
}
