package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.event.PlotFocusEvent;
import org.timepedia.chronoscope.client.event.PlotFocusHandler;
import org.timepedia.chronoscope.client.event.PlotHoverEvent;
import org.timepedia.chronoscope.client.event.PlotHoverHandler;
import org.timepedia.chronoscope.client.event.PlotMovedEvent;
import org.timepedia.chronoscope.client.event.PlotMovedHandler;
import org.timepedia.chronoscope.client.util.PortableTimer;
import org.timepedia.chronoscope.client.util.PortableTimerTask;

public class GTestPlotEvents extends ChronoscopeTestCaseBase {

  boolean focusTrigger = false;

  boolean hoverTrigger = false;

  boolean moveTrigger = false;

  public void testFoo() {
    focusTrigger = false;
    hoverTrigger = false;
    moveTrigger = false;

    runChronoscopeTest(Fixtures.getTestDataset(), new ViewReadyCallback() {
      public void onViewReady(View view) {
        XYPlot plot = view.getChart().getPlot();
        plot.addPlotFocusHandler(new PlotFocusHandler() {
          public void onFocus(PlotFocusEvent event) {
            focusTrigger = true;
          }
        });

        plot.addPlotHoverHandler(new PlotHoverHandler() {
          public void onHover(PlotHoverEvent event) {
            hoverTrigger = true;
          }
        });

        plot.addPlotMovedHandler(new PlotMovedHandler() {
          public void onMoved(PlotMovedEvent event) {
            moveTrigger = true;
          }
        });

        plot.setFocusXY(300, 200);
        assertTrue("Focus event not triggered by setFocusXY", focusTrigger);

        Dataset[] ds = Fixtures.getTestDataset();
        double dx = ds[0].getX(3);
        double dy = ds[0].getFlyweightTuple(3).getSecond();
        double x = plot.domainToWindowX(dx, 0);
        double y = plot.rangeToWindowY(dy, 0);
        
        plot.setHover((int)x, (int)y);
        assertTrue("Hover event not triggered by setHover", hoverTrigger);

        //MockView/Canvas currently inadequate to test this
        plot.animateTo(plot.getDomain().getStart() * 1.1,
            plot.getDomain().length(), PlotMovedEvent.MoveType.DRAGGED,
            new PortableTimerTask() {
              public void run(PortableTimer timer) {
                assertTrue("Move event not triggered by animateTo",
                    moveTrigger);
              }
            });

        finishTest();
      }
    });
  }
}
