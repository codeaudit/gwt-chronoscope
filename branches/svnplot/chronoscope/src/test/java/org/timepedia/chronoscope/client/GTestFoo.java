package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.canvas.View;

public class GTestFoo extends ChronoscopeTestCaseBase {
   public void testFoo() {
     runChronoscopeTest(Fixtures.getTestDataset(), new ViewReadyCallback() {
       public void onViewReady(View view) {
         view.getChart().redraw();
         finishTest();
       }
     });
   }
}
