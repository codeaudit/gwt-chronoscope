package org.timepedia.chronoscope.client.plot;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.GwtEvent;

import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.Export;

/**
 * A HandlerRegistration class that is exportable to Javascript.
 */
public class ExportableHandlerRegistration implements HandlerRegistration
    ,Exportable {

  private GwtEvent.Type type;

  private EventHandler handler;

  private HandlerManager manager;

  public <HandlerType extends EventHandler> ExportableHandlerRegistration(
      HandlerManager exportableHandlerManager, GwtEvent.Type type,
      HandlerType handlerType) {
    this.manager = exportableHandlerManager;
    this.type = type;
    this.handler = handlerType;
  }

  @Export
  public void removeHandler() {
    manager.removeHandler(type, handler);
  }
}
