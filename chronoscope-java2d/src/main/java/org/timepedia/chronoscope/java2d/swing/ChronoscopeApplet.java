package org.timepedia.chronoscope.java2d.swing;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.data.mock.MockDatasetFactory;
import org.timepedia.chronoscope.java2d.ServerChronoscope;

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
        new Dataset[]{new MockDatasetFactory(ServerChronoscope.get().getComponentFactory().getDatasetFactory()).getBasicDataset()});

    Container cp = getContentPane();
    cp.setLayout(new BorderLayout());
    cp.add(BorderLayout.CENTER, scp);
  }
}
