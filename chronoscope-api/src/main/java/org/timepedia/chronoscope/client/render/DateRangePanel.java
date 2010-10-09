package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.render.domain.TickFormatter;
import org.timepedia.chronoscope.client.render.domain.TickFormatterFactory;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

/**
 * Draws a date range (e.g. "11/25/1995 - 02/17/2007").
 * 
 * @author Chad Takahashi
 */
public class DateRangePanel extends AbstractPanel implements SelfResizing,
    GssElement, Exportable {

  public String getType() {
    return "daterange";
  }

  public String getTypeClass() {
    return null;
  }

  public final GssElement getParentGssElement() {
    return (LegendAxisPanel) this.parent;
  }

  private void initGssProperties(GssProperties props) {
    gssProperties = props;
  }

  DomainAxisPanel domainAxisPanel;
  GssProperties labelProperties;
  String dateRangeActive;

  public void init(Layer layer, DomainAxisPanel domainAxisPanel) {
    this.domainAxisPanel = domainAxisPanel;
    initGssProperties(domainAxisPanel.view.getGssProperties(this, ""));
    labelProperties = domainAxisPanel.view.getGssProperties(new GssElementImpl(
        "label", getParentGssElement()), "");

    resizeToIdealWidth();
  }

  public void draw() {
    layer.setStrokeColor(labelProperties.color);
    layer.drawText(bounds.x, bounds.y, dateRangeActive,
        labelProperties.fontFamily, labelProperties.fontWeight,
        labelProperties.fontSize, textLayerName, Cursor.DEFAULT);
  }
  
  private TickFormatter<?> getTickFormater() {
    TickFormatterFactory<?> fact = domainAxisPanel.getTickFormatterFactory();
    double domainWidth = domainAxisPanel.plot.getDomain().length();
    return fact.findBestFormatter(domainWidth);
  }
  
  public void resizeToMinimalWidth() {
    dateRangeActive = getTickFormater().getRangeLabelCompact(domainAxisPanel.plot.getDomain());
    bounds.width = stringSizer.getWidth(dateRangeActive, labelProperties);
  }

  public void resizeToIdealWidth() {
    dateRangeActive = getTickFormater().getRangeLabel(domainAxisPanel.plot.getDomain());
    bounds.width = stringSizer.getWidth(dateRangeActive, labelProperties);
  }

  @Export
  @Deprecated
  public void setDateRangeFormat(String dateFormat) {
  }
  

  @Export
  @Deprecated
  public void setCompactDateRangeFormat(String compactDateFormat) {
  }

  @Export
  @Deprecated
  public void setDateDelim(String dateDelim) {
  }

  @Export
  @Deprecated
  public void setCompactDateDelim(String compactDateDelim) {
  }

}
