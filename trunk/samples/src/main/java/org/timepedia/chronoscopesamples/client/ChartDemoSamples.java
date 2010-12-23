package org.timepedia.chronoscopesamples.client;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.browser.ChartPanel;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.client.overlays.OverlayClickListener;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.render.LineXYRenderer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ChartDemoSamples implements EntryPoint {

  private static volatile double GOLDEN__RATIO = 1.618;

  public void onModuleLoad() {
    try {
      
      VerticalPanel v = new VerticalPanel();
      v.setSpacing(20);
      RootPanel.get().add(v);
      
      ChartPanel chartPanel = Chronoscope.createTimeseriesChartWithDatasetVarName(
          "interestRates01", "interestRates02");
      chartPanel.getElement().getStyle().setCursor(Cursor.POINTER);
      chartPanel.setViewReadyCallback(new ViewReadyCallback() {
        public void onViewReady(final View view) {
          Dataset<?> dataset = view.getChart().getPlot().getDatasets().get(0);
          final Marker m = new Marker(dataset.getDomainExtrema().midpoint(),
              "A", 0);
          m.addOverlayClickListener(new OverlayClickListener() {
            public void onOverlayClick(Overlay overlay, int x, int y) {
              m.openInfoWindow("Hello");
            }
          });

          XYPlot<?> plot = view.getChart().getPlot();
          plot.setOverviewVisible(true);
          plot.setDatasetRenderer(1, new LineXYRenderer());
          plot.addOverlay(m);
          ((DefaultXYPlot<?>)plot).redraw(true);
        }
      });
      v.add(chartPanel);

      final ChartPanel chartPanel2 = Chronoscope.createTimeseriesChartWithDatasetVarName(
          "interestRates01", "interestRates02");
      int chartWidth = 600;
      int chartHeight = (int) (chartWidth / GOLDEN__RATIO);
      chartPanel2.setDimensions(chartWidth, chartHeight);
      chartPanel2.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
      chartPanel2.getElement().getStyle().setPadding(5, Unit.PX);
      v.add(chartPanel2);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
