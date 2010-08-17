package org.timepedia.chronoscope.doclet;

import junit.framework.TestCase;

public class ChronoscopeDocletTest extends TestCase {
  
  ChronoscopeDoclet c = new ChronoscopeDoclet();

  public void testFilter() {
    String s = c.filter("a {@link XYPlot} and a {@link View}.");
    assertEquals("a <a href=#XYPlot>XYPlot</a> and a <a href=#View>View</a>.", s);
    s = c.filter("a {@link org.chronoscope.XYPlot}.");
    assertEquals("a <a href=#XYPlot>XYPlot</a>.", s);
    s = c.filter("a {@link XYPlot} and\n a {@link View}.");
    assertEquals("a <a href=#XYPlot>XYPlot</a> and\n a <a href=#View>View</a>.", s);
    s = c.filter("a {@link\norg.chronoscope.XYPlot}.");
    assertEquals("a <a href=#XYPlot>XYPlot</a>.", s);
    s = c.filter("of {@link #getFlyweightTuple(int)}.");
    assertEquals("of getFlyweightTuple(int).", s);
  }

}
