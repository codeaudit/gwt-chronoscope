package org.timepedia.chronoscope.client.render.domain;

/**
 * Factory for obtaining {@link DateTickFormatter} objects that provide 
 * calendar logic for rendering date/time domain ticks.
 * 
 * @author chad takahashi
 */
public final class DateTickFormatterFactory extends TickFormatterFactory {

  @Override
  protected TickFormatter createRootTickFormatter() {
    return new MilleniumTickFormatter();
  }
  
}
