package org.timepedia.chronoscope.client.plot;

import com.google.gwt.gen2.event.shared.HandlerRegistration;
import com.google.gwt.gen2.event.shared.EventHandler;
import com.google.gwt.gen2.event.shared.HandlerManager;
import com.google.gwt.gen2.event.shared.AbstractEvent;

import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.Export;

/**
 * A HandlerRegistration class that is exportable to Javascript.
 */
public class ExportableHandlerRegistration extends HandlerRegistration
    implements Exportable {

  public <HandlerType extends EventHandler> ExportableHandlerRegistration(
      HandlerManager exportableHandlerManager, AbstractEvent.Type type,
      HandlerType handlerType) {
    super(exportableHandlerManager, type, handlerType);
  }

  @Override
  @Export
  public void removeHandler() {
    super.removeHandler();
  }
}
