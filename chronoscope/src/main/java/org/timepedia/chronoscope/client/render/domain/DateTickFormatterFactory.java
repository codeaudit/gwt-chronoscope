package org.timepedia.chronoscope.client.render.domain;

import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Export;

/**
 * Factory for obtaining {@link DateTickFormatter} objects that provide 
 * calendar logic for rendering date/time domain ticks.
 * 
 * @author chad takahashi
 */
@ExportPackage("chronoscope")
public final class DateTickFormatterFactory extends TickFormatterFactory implements
    Exportable {

  @Export
  public DateTickFormatterFactory() {
  }

  @Override
  protected TickFormatter createRootTickFormatter() {
    return new MilleniumTickFormatter();
  }
  
}
