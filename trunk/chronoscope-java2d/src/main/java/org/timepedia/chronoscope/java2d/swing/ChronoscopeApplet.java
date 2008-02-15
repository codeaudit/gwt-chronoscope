package org.timepedia.chronoscope.java2d.swing;

import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.data.MockXYDataset;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JApplet;

/**
 * Applet bootstrap
 */
public class ChronoscopeApplet extends JApplet {

  public void init() {
    super.init();
    SwingChartPanel scp = new SwingChartPanel(
        new XYDataset[]{new MockXYDataset()});

    Container cp = getContentPane();
    cp.setLayout(new BorderLayout());
    cp.add(BorderLayout.CENTER, scp);
  }
}
