package org.timepedia.chronoscope.java2d;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import org.timepedia.chronoscope.client.Chronoscope;
import org.timepedia.chronoscope.client.ChronoscopeComponentFactory;
import org.timepedia.chronoscope.client.ComponentFactory;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.gss.parser.GssStylesheetGssContext;
import org.timepedia.chronoscope.java2d.StaticImageChartPanel;

/**
 */
@Singleton
public class ServerChronoscope extends Chronoscope<StaticImageChartPanel> {

  @Override
  public StaticImageChartPanel createChart(Datasets datasets, int width,
      int height, ViewReadyCallback callback) {
    StaticImageChartPanel sicp = new StaticImageChartPanel(datasets.toArray(),
        true, width, height, new GssStylesheetGssContext(""));
    return sicp;
  }


  public static Chronoscope<StaticImageChartPanel> get() {
    return new ServerChronoscope().getInjector().get();
  }

  @Override
  protected ChronoscopeInjector<StaticImageChartPanel> getInjector() {
    return new ChronoscopeInjector<StaticImageChartPanel>() {
      public Injector injector;

      {
        injector = Guice.createInjector(new ServerChronoscopeModule());
      }

      public Chronoscope<StaticImageChartPanel> get() {
        return injector.getInstance(Chronoscope.class);
      }

      public ComponentFactory getComponentFactory() {
        return injector.getInstance(ComponentFactory.class);
      }
    };
  }

  private class ServerChronoscopeModule extends AbstractModule {

    @Override
    protected void configure() {
      bind(Chronoscope.class).to(ServerChronoscope.class);
      bind(ComponentFactory.class).to(ChronoscopeComponentFactory.class);
    }
  }
}
