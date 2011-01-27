package org.timepedia.chronoscope.client.axis;


import org.timepedia.chronoscope.client.Fixtures;
import org.timepedia.chronoscope.client.DataShape;
import org.timepedia.chronoscope.client.ChronoscopeTestCaseBase;

import java.util.HashSet;

/**
 * Test range axis labels
 */

@SuppressWarnings({"NonJREEmulationClassesInClientCode"})
public class RangeAxisTest extends junit.framework.TestCase {
    private static final int LABEL_HEIGHT = 20;
    private static final int AXIS_HEIGHT = 400;
    private static final int LABEL_PADDING = 4;

    public String getModuleName() {
        return "org.timepedia.chronoscope.ChronoscopeTestSuite";
    }

    private int getPixelPosition(double[] label, int i, double min, double max) {
        return (int) ((label[i] - min) / (max - min) * AXIS_HEIGHT);
    }

    public void testLabelsAllDiffer() {
        DataShape ds[] = Fixtures.getDataShapes();
        // TODO - vary heights as well as valign {top, bottom, center}
        for (DataShape d : ds) {
            double labels[] = RangeAxis.computeLinearTickPositions(d.min, d.max, AXIS_HEIGHT, LABEL_HEIGHT);
            HashSet s=new HashSet();
            for(double l : labels) s.add(l);
            assertEquals(labels.length, s.size());
        }
    }

    public void testLabelsDoNotOverlap() {
        DataShape ds[] = Fixtures.getDataShapes();
        // TODO - vary heights as well as valign {top, bottom, center}
        for (DataShape d : ds) {
            double labels[] = RangeAxis.computeLinearTickPositions(d.min, d.max, AXIS_HEIGHT, LABEL_HEIGHT);
            for (int i = 0; i < labels.length - 1; i++) {
                int pixelPos = getPixelPosition(labels, i, d.min, d.max);
                int pixelPos2 = getPixelPosition(labels, i + 1, d.min, d.max);
                assertTrue("labels might overlap: pixelPos = " + pixelPos + " pixelPos2 = " + pixelPos2 +
                        " d.min = " + d.min + " d.max = " + d.max,
                        Math.abs(pixelPos - pixelPos2) >= 2 * LABEL_HEIGHT);
            }
        }
    }

    // theoretically visible is the test here, the render concern should probably be moved to RangeAxisRendererTest
    // TODO - a render test should catch the clipped zero issue, it's not a label problem
//    public void testLabelsAllVisible() {
//        DataShape ds[] = Fixtures.getDataShapes();
//        // TODO - vary heights as well as valign {top, bottom, center}
//        for (DataShape d : ds) {
//            double labels[] = RangeAxis.computeLinearTickPositions(d.min, d.max, AXIS_HEIGHT, LABEL_HEIGHT);
//            for (int i = 0; i < labels.length - 1; i++) {
//                int pixelPos = getPixelPosition(labels, i, d.min, d.max);
//                int pixelPosMax = pixelPos + LABEL_HEIGHT + LABEL_PADDING;
//                int pixelPosMin = pixelPos - LABEL_HEIGHT - LABEL_PADDING;
//                assertTrue(labels[i] +" might render out of bounds if pixelPosMax = "+ pixelPosMax +" > " + AXIS_HEIGHT,
//                        pixelPosMax < AXIS_HEIGHT );
//
//                assertTrue(labels[i] +" might render out of bounds if pixelPosMin = " + pixelPosMin +" < 0",
//                        pixelPosMin > 0 );
//            }
//        }
//    }

    // TODO - catch case of ragged 0, 0.5, 1, 1.5, 2, 2.5;   7 8 9 10 is ok;  and 0, .5, 1.0, 1.5, 2.0 is ok
    //                             1   3   1   3   1   3 ;   1 1 1  2      ;      1   2   3    3    3
    public void testLabelWidthsAreMonotonic() {
        DataShape ds[] = Fixtures.getDataShapes();
        // TODO - vary heights as well as valign {top, bottom, center}
        for (DataShape d : ds) {
            double labels[] = RangeAxis.computeLinearTickPositions(d.min, d.max, AXIS_HEIGHT, LABEL_HEIGHT);
            // TODO - assert monotonicity of RangeAxisRenderer.computeLabelFormat over labels 
        }
    }

    public void testCalcRangeAxisScale() {
        double min, max;
        int expect, int calc;
        String msg;
      /*
        scale (.00040404, .000808, -6);

        scale (.001, .999, 0);

        scale (32498732343, .000000000000000000000001, 9);

        scale (.0000000000123012302130, -0.0000002387238273, -12);

        scale (-10000000, 10000000, 6);

        scale(-100000000,1239812938, 6);

        scale (.000001, 232938293829839283, 15 * 3);

        scale (-.023, 100000, 3 * 3);
        */
    }

    private void scale(double min, double max, int expect) {
        calc = RangeAxis.calcRangeAxisScale(min, max);
        assertEquals("calcRangeAxisScale("+min+","+max+") = " + calc + " != " + expect,  expect,  actual);
    }
}
