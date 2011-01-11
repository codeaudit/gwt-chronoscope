package org.timepedia.chronoscope.client.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.exporter.client.Exportable;

/**
 * UI panel containing the dataset legend (a colored line followed by the
 * dataset's name).
 * 
 * @author Chad Takahashi
 */
public class DatasetLegendPanel extends AbstractPanel implements GssElement, Exportable {

  // Dictates the X-padding between a legend icon and dataset label
  public static final double LEGEND_ICON_PAD = 2;
  // Default icon width or height 
  public static final double LEGEND_ICON_SIZE = 6;

  // Dictates the X-padding between each dataset legend item
  static final int DATASET_LEGEND_PAD = 6;

  // Dictates the Y-padding between legend labels and plot
  static final int LEGEND_PLOT_PAD = 5;

  private double lblHeight;

  private int colSpacing = DATASET_LEGEND_PAD;

  private boolean colAlignment = false;

  private GssProperties legendLabelsProperties;

  private XYPlot<?> plot;

  private View view;

  public String getType() {
    return "legend";
  }

  public String getTypeClass() {
    return null;
  }

  public final GssElement getParentGssElement() {
    return (LegendAxisPanel) this.parent;
  }

  public void init() {
    ArgChecker.isNotNull(plot, "plot");
    ArgChecker.isNotNull(view, "view");
    ArgChecker.isNotNull(gssProperties, "gssProperties");

    lblHeight = stringSizer.getHeight("X", gssProperties);
    colAlignment = legendLabelsProperties.columnAligned;

    this.bounds.width = view.getWidth();

    Bounds b = calcBounds(layer, plot.getDatasets().size());
    this.bounds.height = b.height;
  }

  public void setPlot(XYPlot<?> plot) {
    this.plot = plot;
  }

  public void setView(View view) {
    this.view = view;
  }

  private Bounds calcBounds(Layer layer, int numDatasets) {
    Bounds b = new Bounds();
    draw(layer, b);
    return b;
  }

  /**
   * If colAlignment=true,the legend labels column alignment Otherwise the
   * legend labels arranged one after another(Columns don't align)
   */
  public void draw() {
    draw(layer, null);
  }

  private void draw(Layer layer, Bounds b) {

    final boolean onlyCalcSize = (b != null);
    double xCursor = bounds.x;
    double yCursor = bounds.y + lblHeight;
    int numDatasets = plot.getDatasets().size();
    double padding = calcLegendIconWidth() + LEGEND_ICON_PAD;

    List<Item> items = new LinkedList<Item>();
    for (int i = 0; i < numDatasets; i++) {
      DatasetRenderer<?> renderer = plot.getDatasetRenderer(i);
      renderer = plot.getDatasetRenderer(i);
      for (int d : renderer.getLegendEntries(plot.getDatasets().get(i))) {
        int hoverPoint = plot.getHoverPoints()[i];

        String seriesLabel = createDatasetLabel(plot, i, hoverPoint, d,  legendLabelsProperties.valueVisible);
        double lblWidth = stringSizer.getWidth(seriesLabel, legendLabelsProperties);

        if (lblWidth <= 0) {
          lblWidth = bounds.width;
        }
        items.add(new Item(padding, lblWidth, i, d, seriesLabel));
      }
    }

    int ncols;
    if ("auto".equalsIgnoreCase(legendLabelsProperties.columnCount)) {
      ncols = colAlignment ? ALIGNED_COLS : UNALIGNED_COLS;
    } else {
      ncols = Integer.valueOf(legendLabelsProperties.columnCount);
    }
    List<List<Item>> rows = DatasetLegendPanel.getLegendRows(items, bounds.width, colSpacing, ncols);

    if (onlyCalcSize) {
      b.x = this.bounds.x;
      b.y = this.bounds.y;
      b.width = bounds.width;
      b.height = (rows.size() - 1) * lblHeight + LEGEND_PLOT_PAD;
    } else {
      double colwidth = (bounds.width - (colSpacing * (ncols - 1))) / ncols;
      for (List<Item> row : rows) {
        xCursor = bounds.x;
        for (int i = 0; i < row.size(); i++) {
          Item item = row.get(i);

          double width = colwidth;
          if (ncols == UNALIGNED_COLS) {
            width = item.len + item.pad;
          } else if (ncols == ALIGNED_COLS) {
            Item firstRowItem = rows.get(0).get(i % rows.get(0).size());
            width = firstRowItem.len + firstRowItem.pad;
          }

          drawLegend(layer, xCursor, yCursor, width, item);
          xCursor += width + colSpacing -1;
        }
        yCursor += lblHeight;
      }
    }

  }

  private void drawLegend(Layer layer, double xCursor, double yCursor,
                          double width, Item item) {
    double iconHeight = Math.min(calcLegendIconHeight(), lblHeight);
    double iconWidth = calcLegendIconWidth();
    DatasetRenderer<?> renderer = plot.getDatasetRenderer(item.idx);
    
    double yPos = yCursor - Math.ceil((lblHeight - iconHeight)/2);  // + 2 + Math.ceil(((lblHeight - iconHeight)/2));
    renderer.drawLegendIcon(layer, xCursor, yPos , iconWidth, iconHeight, item.dimension);
    layer.setStrokeColor(legendLabelsProperties.color);
    String seriesLabel = item.label;
    double labelSpace = width - item.pad;
    if (labelSpace < item.len) {
      seriesLabel = stringSizer.wrapText(seriesLabel, gssProperties, labelSpace);
    }
    layer.drawText(xCursor + item.pad, yCursor, seriesLabel,
        legendLabelsProperties.fontFamily, legendLabelsProperties.fontWeight,
        legendLabelsProperties.fontSize, textLayerName, Cursor.DEFAULT);
  }

  /**
   * Calculates the pixel width of the legend icon.
   */
  public double calcLegendIconWidth() {
    String width = legendLabelsProperties.iconWidth;
    return "auto".equals(width) ? LEGEND_ICON_SIZE : Double.valueOf(width.substring(0, width.length() - 2));
  }
  
  /**
   * Calculates the pixel height of the legend icon.
   */
  public double calcLegendIconHeight() {
    String width = legendLabelsProperties.iconHeight;
    return "auto".equals(width) ? LEGEND_ICON_SIZE : Double.valueOf(width.substring(0, width.length() - 2));
  }

  /**
   * Generates the dataset label for a given point on a dataset. The point index
   * is needed in order to determine the range value to be displayed for hovered
   * data points. If pointIdx == -1, then the range value is omitted.
   */
  public static String createDatasetLabel(XYPlot<?> plot, int datasetIdx,
      int pointIdx, int dimension, boolean valueVisible) {
    Dataset<?> ds = plot.getDatasets().get(datasetIdx);
    RangeAxis rangeAxis = plot.getRangeAxis(datasetIdx);
    @SuppressWarnings({"rawtypes", "unchecked"})
    ArrayList<Dataset> sdatasets = (ArrayList<Dataset>) ds.getUserData("datasets");
    String rlabel = ds.getRangeLabel();
    if (sdatasets != null && dimension < sdatasets.size()) {
      Dataset<?> dataset = sdatasets.get(dimension);
      if (dataset != null) {
        rlabel = dataset.getRangeLabel();
      }
    }
    String lbl = rlabel + rangeAxis.getLabelSuffix();

    final boolean doShowRangeValue = (pointIdx > -1);
    if (doShowRangeValue && valueVisible) {
      double yData = rangeAxis.isCalcRangeAsPercent() ? plot.calcDisplayY(
          datasetIdx, pointIdx, dimension) : plot.getDataCoord(datasetIdx,
          pointIdx, dimension);
      lbl += " (" + rangeAxis.getFormattedLabel(yData) + ")";
    }
    return lbl;
  }

  public void setColAlignment(boolean colAlignment) {
    this.colAlignment = colAlignment;
  }

  public void setColSpacing(int colSpacing) {
    this.colSpacing = colSpacing;
  }

  public void setLegendLabelsProperties(GssProperties legendLabelsProperties) {
    this.legendLabelsProperties = legendLabelsProperties;
  }

  public static int UNALIGNED_COLS = -1;
  public static int ALIGNED_COLS = 0;

  static public List<List<Item>> getLegendRows(List<Item> items,
                                               double maxWidth, double padding, int cols) {
    if (cols == UNALIGNED_COLS) {
      return getLeyendRowsUnalignedColums(items, maxWidth, padding);
    } else if (cols == ALIGNED_COLS) {
      return getLegendRowsAlignedColumns(items, maxWidth, padding);
    } else {
      return getLegendRowsFixedColumns(items, cols);
    }
  }

  static private List<List<Item>> getLegendRowsFixedColumns(List<Item> items, int ncols) {
    List<List<Item>> ret = new LinkedList<List<Item>>();
    if (items == null || items.size() == 0) {
      return ret;
    }

    while (items.size() > 0) {
      List<Item> row = new LinkedList<Item>();
      ret.add(row);
      for (int i = 0; i < ncols && items.size() > 0; i++) {
        row.add(items.remove(0));
      }
    }

    return ret;
  }

  static private List<List<Item>> getLegendRowsAlignedColumns(List<Item> items,
                                                              double maxWidth, double padding) {
    Collections.sort(items);
    List<List<Item>> ret = new LinkedList<List<Item>>();
    if (items == null || items.size() == 0) {
      return ret;
    }

    List<List<Item>> cols = new LinkedList<List<Item>>();
    List<Item> col = new LinkedList<Item>();
    cols.add(col);
    col.add(items.get(0));
    double remainingWidth = maxWidth - items.get(0).len - padding;
    for (int i = 1; i < items.size(); i++) {
      Item it = items.get(i);
      double width = it.len + it.pad;
      if (width <= remainingWidth) {
        remainingWidth -= width + padding;
        col = new LinkedList<Item>();
        cols.add(col);
        col.add(it);
      } else {
        col.add(it);
      }
    }

    for (int i = cols.size() - 1; i > 0; i--) {
      List<Item> clm = cols.get(i);
      List<Item> clma = cols.get(i - 1);
      while (clm.size() > clma.size()) {
        clma.add(clm.remove(clm.size() - 1));
      }
    }

    int nrows = cols.get(0).size();

    for (int r = 0; r < nrows; r++) {
      List<Item> row = new LinkedList<Item>();
      ret.add(row);
      for (List<Item> cl : cols) {
        if (cl.size() > 0) {
          row.add(cl.remove(0));
        }
      }
    }

    return ret;
  }

  static private List<List<Item>> getLeyendRowsUnalignedColums(
      List<Item> items, double maxWidth, double padding) {
    List<List<Item>> ret;
    ret = new LinkedList<List<Item>>();
    if (items == null || items.size() == 0) {
      return ret;
    }
    while (items.size() > 0) {
      List<Item> row = new LinkedList<Item>();
      double remainingWidth = maxWidth;
      Item i = getMostSuitableLabel(items, remainingWidth, true);
      row.add(i);
      remainingWidth -= i.len + i.pad + padding;
      while (remainingWidth > 0) {
        i = getMostSuitableLabel(items, remainingWidth, false);
        if (i == null) {
          break;
        }
        row.add(i);
        remainingWidth -= i.len + i.pad + padding;
      }
      ret.add(row);
    }
    return ret;
  }

  static public class Item implements Comparable<Item> {
    public Double len;
    public Double pad;
    public int idx;
    public int dimension;
    public String label;

    public Item(double pad, double len, int idx, int dimension, String label) {
      this.pad = pad;
      this.len = len;
      this.idx = idx;
      this.label = label;
      this.dimension = dimension;
    }

    public int compareTo(Item o) {
      return o.len.compareTo(this.len);
    }

    public String toString() {
      return "pad: " + pad + "len:" + len + " idx:" + idx + " dim:" + dimension  + " label:" + label;
    }
  }

  static private Item getMostSuitableLabel(List<Item> items, double size,
      boolean noNull) {
    if (items == null || items.size() == 0) {
      return null;
    }
    Collections.<Item> sort(items);
    for (Item i : items) {
      if (i.len <= size) {
        items.remove(i);
        return i;
      }
    }
    if (!noNull) {
      return null;
    }
    return items.remove(0);
  }
}
