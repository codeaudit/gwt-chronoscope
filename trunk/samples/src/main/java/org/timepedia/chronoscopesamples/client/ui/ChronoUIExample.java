package org.timepedia.chronoscopesamples.client.ui;

/**
 * An example of how to use ui-builder with Chronoscope.
 */
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.ui.ChronoUI;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ChronoUIExample extends Composite {

  private static ChronoUIBinder uiBinder = GWT.create(ChronoUIBinder.class);

  interface ChronoUIBinder extends UiBinder<Widget, ChronoUIExample> {
  }

  @UiField
  Label label;

  @UiField
  ChronoUI chart;

  @UiField
  Button button;

  public ChronoUIExample() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public ChartPanel getChartPanel() {
    return chart.getChart();
  }

  int base = 100;

  @UiHandler("button")
  public void click(ClickEvent e) {
    chart.setSize("" + (base + 200), "" + (base + 100));
    base = base > 300 ? 100 : base + 100;
  }

}
