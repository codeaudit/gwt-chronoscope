package org.timepedia.chronoscope.client.render.domain;

import org.timepedia.chronoscope.client.util.date.ChronoDate;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 * Factory for obtaining {@link DateTickFormatter} objects that provide 
 * calendar logic for rendering date/time domain ticks.
 * 
 * @author chad takahashi
 */
@ExportPackage("chronoscope")
public final class DateTickFormatterFactory extends TickFormatterFactory<ChronoDate> implements
    Exportable {

  @Export
  public DateTickFormatterFactory() {
  }

  @Override
  protected TickFormatter<ChronoDate> createRootTickFormatter() {
    return new MilleniumTickFormatter();
  }
  
}
