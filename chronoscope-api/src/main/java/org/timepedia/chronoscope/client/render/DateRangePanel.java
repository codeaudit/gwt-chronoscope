package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
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
  private int PADDING = 8;
  private String dateRangeActive;
  private String dateDelim = " - ";
  private String dateDelimCompact = " - ";

  private DateFormatter dateFormatter;
  private DateFormatter dateFormatterCompact;

  private GssProperties labelProperties;

  protected XYPlot<?> plot;
  protected View view;

  public void dispose() {
    super.dispose();
    dateFormatter = null;
    dateFormatterCompact = null;
    labelProperties = null;
    plot=null;
    view=null;
  }

  public void reset() {
    super.reset();
    labelProperties = null;
    plot = null;
    view=null;
  }

  public void remove (Panel panel) {
    return;
  }

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

  public void init() {
    initGssProperties(view.getGssProperties(this, ""));
    labelProperties = view.getGssProperties(new GssElementImpl(
        "label", getParentGssElement()), "");
    resizeToIdealWidth();
  }

  public void draw() {
    layer.save();

    layer.setStrokeColor(labelProperties.color);
    layer.drawText(0, bounds.bottomY(), dateRangeActive,
        labelProperties.fontFamily, labelProperties.fontWeight,
        labelProperties.fontSize, textLayerName, Cursor.DEFAULT);

    layer.restore();
  }
  
  private TickFormatter<?> getTickFormatter() {
    return DomainAxisPanel.getTickFormatterFactory().findBestFormatter(plot.getDomain().length());
  }
  
  public void resizeToMinimalWidth() {
    Interval i = plot.getDomain();
    if (dateFormatter != null) {
      dateRangeActive = dateFormatterCompact.format(i.getStart()) + dateDelimCompact + dateFormatter.format(i.getEnd());
    } else {
      dateRangeActive = getTickFormatter().getRangeLabelCompact(plot.getDomain());
    }
    bounds.width = StringSizer.getWidth(layer, dateRangeActive, labelProperties);
    bounds.width += PADDING;
    bounds.x = view.getWidth() - bounds.width;
    layer.setBounds(bounds);
  }

  public void resizeToIdealWidth() {
    Interval i = plot.getDomain();
    if (dateFormatter != null) {
      dateRangeActive = dateFormatter.format(i.getStart()) + dateDelim + dateFormatter.format(i.getEnd());
    } else {
      dateRangeActive = getTickFormatter().getRangeLabel(i);
    }
    bounds.width = StringSizer.getWidth(layer, dateRangeActive, labelProperties);
    bounds.height = StringSizer.getHeight(layer, dateRangeActive, labelProperties);
    bounds.width += PADDING;
    bounds.x = view.getWidth() - bounds.width;

    if (null != bounds && null != layer && !bounds.equals(layer.getBounds())) {
      layer.save();
      layer.setBounds(bounds);
      layer.restore();
    }
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
      dateFormatterCompact = DateFormatHelper.getDateFormatter(compactDateFormat);
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

  public void setPlot(XYPlot<?> plot) {
    this.plot = plot;
  }

  public void setView(View view) {
    this.view = view;
  }
}
