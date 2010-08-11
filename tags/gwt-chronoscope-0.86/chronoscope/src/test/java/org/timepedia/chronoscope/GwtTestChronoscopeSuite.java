package org.timepedia.chronoscope;

import com.google.gwt.junit.tools.GWTTestSuite;

import org.timepedia.chronoscope.client.GTestPlotEvents;

public class GwtTestChronoscopeSuite extends GWTTestSuite {

   public static GWTTestSuite suite() {
    GWTTestSuite suite = new GWTTestSuite("Tests for Chronoscope in Browser");
    suite.addTestSuite(GTestPlotEvents.class); 
    return suite;
  }
}
