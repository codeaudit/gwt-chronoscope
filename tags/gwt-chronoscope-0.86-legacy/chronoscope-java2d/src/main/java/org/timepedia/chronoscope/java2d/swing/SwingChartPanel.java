package org.timepedia.chronoscope.java2d.swing;

import org.timepedia.chronoscope.client.Chart;
import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.canvas.ViewReadyCallback;
import org.timepedia.chronoscope.client.gss.DefaultGssContext;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.render.XYPlotRenderer;
import org.timepedia.chronoscope.java2d.canvas.CanvasJava2D;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * A ChartPanel analog implemented for Swing
 */
public class SwingChartPanel extends JPanel implements ViewReadyCallback,
    KeyListener, MouseWheelListener, MouseMotionListener, MouseListener,
    RedrawListener {

  private DefaultXYPlot plot;

  private Chart chart;

  private InteractiveViewJava2D view;

  private JLabel label;

  private int selStart;

  private boolean maybeDrag;

  private int startDragX;

  public SwingChartPanel(Dataset[] xyDatasets) {
    addKeyListener(this);


    label = new JLabel();

    chart = new Chart();
    plot = new DefaultXYPlot();
    plot.setDatasets(new Datasets(xyDatasets));
    XYPlotRenderer plotRenderer = new XYPlotRenderer();
    plot.setPlotRenderer(plotRenderer); 
    chart.setPlot(plot);

    add(label);
    label.addMouseWheelListener(this);
    label.addMouseMotionListener(this);
    label.addMouseListener(this);
  }

  public void addNotify() {
    super.addNotify();

    view = new InteractiveViewJava2D();
    view.initialize(getParent().getWidth(), getParent().getHeight(), false,
        new DefaultGssContext(), this, this);
    view.onAttach();
    
  }

  public boolean imageUpdate(Image img, int infoflags, int x, int y, int w,
      int h) {

    return super.imageUpdate(img, infoflags, x, y, w, h);
  }

  public boolean isFocusTraversable() {
    return true;
  }

  public void keyPressed(final KeyEvent e) {
    final char kchar = e.getKeyChar();

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if (kchar == '\t') {
          chart.nextFocus();
        } else if (kchar == 'z') {
          chart.nextZoom();
        } else if (kchar == 'x') {
          chart.prevZoom();
          redraw();
        } else if (kchar == 's') {
//          chart.getPlot()
//              .setSelectionMode(!chart.getPlot().isSelectionModeEnabled());
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
          chart.pageLeft(0.5);
          redraw();
        } else if (kchar == KeyEvent.VK_RIGHT) {
          chart.pageRight(0.5);
        } else if (kchar == KeyEvent.VK_PAGE_UP) {
          chart.pageLeft(1.0);
        } else if (kchar == KeyEvent.VK_PAGE_DOWN) {
          chart.pageRight(1.0);
        } else if (kchar == 'r') {
          chart.redraw();
        } else if (e.getKeyChar() == 'm') {
          ((DefaultXYPlot)chart.getPlot()).fireContextMenuEvent(0,0);
        }
      }
    });
  }

  public void keyReleased(KeyEvent e) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void keyTyped(KeyEvent e) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void mouseClicked(MouseEvent e) {

    final boolean isContextMenu = e.isPopupTrigger()
        || e.getButton() == MouseEvent.BUTTON3;
    final int mouseX = e.getX();
    final int mouseY = e.getY();
    final int clickCount = e.getClickCount();

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if (isContextMenu) {

          ((DefaultXYPlot)chart.getPlot()).fireContextMenuEvent(mouseX, mouseY);
        } else {
          if (clickCount >= 2) {
            chart.maxZoomTo(mouseX, mouseY);
          } else {
            chart.click(mouseX, mouseY);
          }
        }
      }
    });
  }

  public void mouseDragged(MouseEvent e) {
    mouseMoved(e);
  }

  public void mouseEntered(MouseEvent e) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mouseMoved(MouseEvent e) {
    final int x = e.getX();
    final int y = e.getY();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
//        if (chart.getPlot().isSelectionModeEnabled() && selStart > -1) {
//          chart.getPlot().setHighlight(selStart, x);
//        } else {
          if (maybeDrag && Math.abs(startDragX - x) > 10) {
            chart.scrollPixels(startDragX - x);
            startDragX = x;
          } else {
            chart.setHover(x, y);
          }
//        }
      }
    });
  }

  public void mousePressed(MouseEvent e) {
//    if (chart.getPlot().isSelectionModeEnabled()) {
//      selStart = e.getX();
//    } else {
      maybeDrag = true;
      startDragX = e.getX();
//    }
  }

  public void mouseReleased(MouseEvent e) {
//    if (chart.getPlot().isSelectionModeEnabled()) {
//      chart.getPlot().setSelectionMode(false);
//      selStart = -1;
//    } else if (maybeDrag) {
//    }
    maybeDrag = false;
  }

  public void mouseWheelMoved(MouseWheelEvent e) {
    if (e.getWheelRotation() < 0) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          chart.nextZoom();
        }
      });
    } else {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          chart.prevZoom();
        }
      });
    }
  }

  public void onRedraw() {
    Image im = ((CanvasJava2D) view.getCanvas()).getImage();
    ImageIcon ii = new ImageIcon(im);
    label.setIcon(ii);
  }

  public void onViewReady(View view) {
    plot.init(view);
    
    // configure chart
    chart = new Chart();
    chart.setPlot(plot);
    chart.setView(view);
    view.setChart(chart);
//    chart.init(view, plot);
    redraw();
  }

  private void redraw() {
    chart.redraw();
  }
}
