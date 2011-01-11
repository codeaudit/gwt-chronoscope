package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.render.domain.TickFormatter;
import org.timepedia.chronoscope.client.render.domain.TickFormatterFactory;
import org.timepedia.chronoscope.client.util.DateFormatter;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.date.DateFormatHelper;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

/**
 * Draws a date range
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
  DateFormatter dateFormatter;
  String dateDelim = " - ";
  DateFormatter dateFormaterCompact;
  String dateDelimCompact = " - ";

  public void init(Layer layer, DomainAxisPanel domainAxisPanel) {
    this.domainAxisPanel = domainAxisPanel;
    initGssProperties(domainAxisPanel.view.getGssProperties(this, ""));
    labelProperties = domainAxisPanel.view.getGssProperties(new GssElementImpl(
        "label", getParentGssElement()), "");
    resizeToIdealWidth();
  }

  public void draw() {
    layer.setStrokeColor(labelProperties.color);
    layer.drawText(bounds.x, bounds.bottomY(), dateRangeActive,
        labelProperties.fontFamily, labelProperties.fontWeight,
        labelProperties.fontSize, textLayerName, Cursor.DEFAULT);
  }
  
  private TickFormatter<?> getTickFormater() {
    TickFormatterFactory<?> fact = domainAxisPanel.getTickFormatterFactory();
    double domainWidth = domainAxisPanel.plot.getDomain().length();
    return fact.findBestFormatter(domainWidth);
  }
  
  public void resizeToMinimalWidth() {
    Interval i = domainAxisPanel.plot.getDomain();
    if (dateFormatter != null) {
      dateRangeActive = dateFormaterCompact.format(i.getStart()) + dateDelimCompact + dateFormatter.format(i.getEnd());
    } else {
      dateRangeActive = getTickFormater().getRangeLabelCompact(domainAxisPanel.plot.getDomain());
    }
    bounds.width = stringSizer.getWidth(dateRangeActive, labelProperties);
  }

  public void resizeToIdealWidth() {
    Interval i = domainAxisPanel.plot.getDomain();
    if (dateFormatter != null) {
      dateRangeActive = dateFormatter.format(i.getStart()) + dateDelim + dateFormatter.format(i.getEnd());
    } else {
      dateRangeActive = getTickFormater().getRangeLabel(i);
    }
    bounds.width = stringSizer.getWidth(dateRangeActive, labelProperties);
    bounds.height = stringSizer.getHeight(dateRangeActive, labelProperties);
  }

  @Export
  public void setDateRangeFormat(String dateFormat) {
    if (!"auto".equals(dateFormat)) {
      dateFormatter = DateFormatHelper.getDateFormatter(dateFormat);
    }
  }

  @Export
  public void setCompactDateRangeFormat(String compactDateFormat) {
    if (!"auto".equals(compactDateFormat)) {
      dateFormaterCompact = DateFormatHelper.getDateFormatter(compactDateFormat);
    }
  }

  @Export
  public void setDateDelim(String dateDelim) {
    this.dateDelim = dateDelim;
  }

  @Export
  public void setCompactDateDelim(String dateDelimCompact) {
    this.dateDelimCompact = dateDelimCompact;
  }

}
