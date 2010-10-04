package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.axis.RangeAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.exporter.client.Exportable;

import java.util.ArrayList;
import org.timepedia.chronoscope.client.gss.GssProperties;

/**
 * UI panel containing the dataset legend (a colored line followed by the
 * dataset's name).
 *
 * @author Chad Takahashi
 */
public class DatasetLegendPanel extends AbstractPanel
  implements GssElement, Exportable {

  // Dictates the X-padding between a given legend icon and its
  //associated dataset name
  private static final double LEGEND_ICON_PAD = 3;

  // Dictates the X-padding between each dataset legend item
  static final int DATASET_LEGEND_PAD = 8;

  // Dictates the Y-padding between legend labels and plot
  static final int LEGEND_PLOT_PAD = 2;

  private double lblHeight;

  private double[]  maxLabelWidths;

  private int colSpacing=20;

  private boolean colAlignment=true;

  private GssProperties legendLabelsProperties;

  private XYPlot plot;

  private View view;

  public String getType() {
    return "legend";
  }

  public String getTypeClass() {
    return null;  
  }

  public final GssElement getParentGssElement() {
    return (LegendAxisPanel)this.parent;
  }

  public void init() {
    ArgChecker.isNotNull(plot, "plot");
    ArgChecker.isNotNull(view, "view");
    ArgChecker.isNotNull(gssProperties, "gssProperties");

    lblHeight = stringSizer.getHeight("X", gssProperties);

    this.maxLabelWidths = calcInitialLabelWidths(plot, layer);

    bounds.width = view.getWidth();

    Bounds b = calcBounds(layer, plot.getDatasets().size());
    this.bounds.height = b.height;
    this.bounds.width = view.getWidth();
  }

  public void setPlot(XYPlot plot) {
    this.plot = plot;
  }

  public void setView(View view) {
    this.view = view;
  }

  /**
   * If colAlignment=true,the legend labels column alignment
   * Otherwise the legend labels arranged one after another(Columns don't align)
   */
  public void draw() {
    checkForChanges();
    if(colAlignment){
          drawColumnAlignment(layer);
      }else{
          draw(layer, plot.getDatasets().size(), null);
      }
  }

  private void checkForChanges() {
    int numLabels = 0;
    for(int i=0; i<plot.getDatasets().size(); i++) {
      numLabels += plot.getDatasetRenderer(i).getLegendEntries(plot.getDatasets().get(i)).length;
    }
    if (numLabels != maxLabelWidths.length) {
      this.maxLabelWidths = calcInitialLabelWidths(plot, layer);
    }
  }

  private Bounds calcBounds(Layer layer, int numDatasets) {
    Bounds b = new Bounds();
    draw(layer, numDatasets, b);
    return b;
  }

  /**
   * The legend labels column alignment
   */
  private void drawColumnAlignment(Layer layer) {
    double xCursor = bounds.x;
    double yCursor = bounds.y;
    setIconSize();
    double maxLabelWidth = calcColumnWidth();
    int columnCount = calcColumnCount(maxLabelWidth);
    //Draw legend label
    int col = 0;
    for (int i = 0; i < plot.getDatasets().size(); i++) {
      DatasetRenderer renderer = plot.getDatasetRenderer(i);
      for (int d : renderer.getLegendEntries(plot.getDatasets().get(i))) {
        if (col > columnCount) {
          col = 0;
          xCursor = bounds.x;
          yCursor += lblHeight;
        }
        renderer.drawLegendIcon(layer, xCursor, yCursor + lblHeight / 2, d);
        double iconWidth = renderer.calcLegendIconWidth(view);
        layer.setStrokeColor(legendLabelsProperties.color);
        int hoverPoint = plot.getHoverPoints()[i];
	      String seriesLabel = createDatasetLabel(plot, i, hoverPoint, d,legendLabelsProperties.valueVisible);
	      String s = stringSizer.wrapText(seriesLabel, gssProperties, maxLabelWidth - iconWidth - LEGEND_ICON_PAD);
        layer.drawText(xCursor + iconWidth + LEGEND_ICON_PAD, yCursor, s, legendLabelsProperties.fontFamily, legendLabelsProperties.fontWeight, legendLabelsProperties.fontSize, textLayerName, Cursor.DEFAULT);
        xCursor += maxLabelWidth + LEGEND_ICON_PAD;
        col++;
      }
    }
  }

  /**
   * Find the maximum column width
   */
  private double calcColumnWidth() {
    double maxLabelWidth = 0, maxIconWidth = 0;
    if (legendLabelsProperties.columnWidth.equals("auto")) {
      int count = 0;
      for (int i = 0; i < plot.getDatasets().size(); i++) {
        Dataset ds = plot.getDatasets().get(i);
        for (int d  : plot.getDatasetRenderer(i).getLegendEntries(ds)) {
          for (int l = 0; l < maxLabelWidths.length; l++) {
            if (maxLabelWidth < maxLabelWidths[l]) {
              maxLabelWidth = maxLabelWidths[l];
            }
            double iconWidth = plot.getDatasetRenderer(i).calcLegendIconWidth(view);
            if (maxIconWidth < iconWidth) {
              maxIconWidth = iconWidth;
            }
          }
        }
      }   
      //
      if ("auto".equals(gssProperties.iconWidth)) {
        maxLabelWidth += maxIconWidth + colSpacing;
      } else {
        int iwidth = Math.min(Integer.valueOf(gssProperties.iconWidth).intValue(), (int)maxLabelWidth);
        maxLabelWidth +=  iwidth + colSpacing;
      }

    } else {
      String width = legendLabelsProperties.columnWidth;
      maxLabelWidth = Double.valueOf(width.substring(0, width.length() - 2));
    }
    return maxLabelWidth;
  }

  /**
   * Calculate the number of columns
   */
  private int calcColumnCount(double maxLabelWidth){
      int colCount = 1;
      if(legendLabelsProperties.columnCount.equals("auto")){
          colCount = (int) Math.floor(bounds.width / maxLabelWidth);
      }else{
          colCount = Integer.valueOf(legendLabelsProperties.columnCount);
      }
      return colCount - 1;
  }

  private void setIconSize(){
      RenderState rs = new RenderState();
      for(int i=0;i<plot.getDatasets().size();i++){
          rs.setPassNumber(i);
          DatasetRenderer renderer = plot.getDatasetRenderer(i);
          renderer.getLegendProperties(i, rs).iconWidth=legendLabelsProperties.iconWidth;
          renderer.getLegendProperties(i, rs).iconHeight=legendLabelsProperties.iconHeight;
      }
  }
  
  private void draw(Layer layer, int numDatasets, Bounds b) {
    final boolean onlyCalcSize = (b != null);
    double xCursor = bounds.x;
    double yCursor = bounds.y;
    for (int i = 0; i < numDatasets; i++) {
      DatasetRenderer renderer = plot.getDatasetRenderer(i);
      for (int d : renderer.getLegendEntries(plot.getDatasets().get(i))) {
        double lblWidth = drawLegendLabel(xCursor, yCursor, layer, i,
            onlyCalcSize, d);
        boolean enoughRoomInCurrentRow = (lblWidth >= 0);

        if (enoughRoomInCurrentRow) {
          xCursor += lblWidth;
        } else {
          xCursor = bounds.x;
          yCursor += lblHeight;
          xCursor += drawLegendLabel(xCursor, yCursor, layer, i, onlyCalcSize,
              d);
        }
        xCursor += DATASET_LEGEND_PAD;
      }
    }

    if (b != null) {
      b.x = this.bounds.x;
      b.y = this.bounds.y;
      b.width = xCursor - b.x;
      // Note: since the (x,y) coordinate refers to the upper-left corner of the
      // bounds, we need to add 'lblHeight' to the final yCursor value to obtain
      // the total height of all legend item rows.
      b.height = yCursor - b.y + lblHeight;
    }
  }

  /**
   * Draws a single legend label (consists of legend icon and text label).
   *
   * @param lblX          - x coordinate of label
   * @param lblY          - y coordinate of label
   * @param layer         - the graphics layer on which this artifact will be
   *                      drawn
   * @param datasetIdx    - corresponding the dataset index
   * @param onlyCalcWidth - if true, only width is calculated; otherwise the
   *                      label is actually drawn as well.
   * @return the full width of the label, or -1 if the label was too wide to fit
   *         in the panel given the specified lblX and lblY coordinates.
   */
  private double drawLegendLabel(double lblX, double lblY, Layer layer,
      int datasetIdx, boolean onlyCalcWidth, int dimension) {
    DatasetRenderer renderer = plot.getDatasetRenderer(datasetIdx);

    int hoverPoint = plot.getHoverPoints()[datasetIdx];
    String seriesLabel = createDatasetLabel(plot, datasetIdx, hoverPoint,
        dimension,legendLabelsProperties.valueVisible);

    // Compute the width of the dataset text label, taking into account historical
    // widths of this label.
    double txtWidth = stringSizer.getWidth(seriesLabel, gssProperties);
    int count = 0;
    for(int i=0; i<datasetIdx; i++) {
      count += plot.getDatasetRenderer(i).getLegendEntries(plot.getDatasets().get(i)).length;
    }
    
    if (txtWidth > maxLabelWidths[datasetIdx]) {
      maxLabelWidths[count+dimension] = txtWidth;
    } else {
      txtWidth = maxLabelWidths[count+dimension];
    }

    double iconWidth = renderer.calcLegendIconWidth(view);
    double totalWidth = txtWidth + LEGEND_ICON_PAD + iconWidth;

    if (lblX + totalWidth >= bounds.rightX()) {
      return -1;
    }

    if (!onlyCalcWidth) {
      renderer.drawLegendIcon(layer, lblX, lblY + lblHeight / 2, dimension);

      layer.setStrokeColor(gssProperties.color);
      layer.drawText(lblX + iconWidth + LEGEND_ICON_PAD, lblY, seriesLabel, legendLabelsProperties.fontFamily,
            legendLabelsProperties.fontWeight, legendLabelsProperties.fontSize, textLayerName, Cursor.DEFAULT);

    }

    return totalWidth;
  }

  /**
   * Calculates the width of the dataset label for the median point of each
   * dataset in the plot and returns the resulting array of label widths.
   */
  private double[] calcInitialLabelWidths(XYPlot plot, Layer layer) {
    Datasets datasets = plot.getDatasets();
    int numEntries = 0;
    for (int i = 0; i < datasets.size(); i++) {
      Dataset ds = datasets.get(i);
      DatasetRenderer renderer = plot.getDatasetRenderer(i);
      numEntries += renderer.getLegendEntries(ds).length;
    }
    double[] estMaxWidths = new double[numEntries];
    int c = 0;
    for (int i = 0; i < datasets.size(); i++) {
      Dataset ds = datasets.get(i);
      int medianIdx = ds.getNumSamples() >> 1;
      DatasetRenderer renderer = plot.getDatasetRenderer(i);
      for (int d : renderer.getLegendEntries(ds)) {
        String lbl = createDatasetLabel(plot, i, medianIdx, d ,legendLabelsProperties.valueVisible);
        if(colAlignment){
            estMaxWidths[c++] = stringSizer.getWidth(lbl, legendLabelsProperties);
        }else{
            estMaxWidths[c++] = stringSizer.getWidth(lbl, gssProperties);
        }
      }
    }

    return estMaxWidths;
  }

  
  /**
   * Generates the dataset label for a given point on a dataset.  The point
   * index is needed in order to determine the range value to be displayed for
   * hovered data points. If pointIdx == -1, then the range value is omitted.
   */
  public static String createDatasetLabel(XYPlot plot, int datasetIdx,
      int pointIdx, int dimension,boolean valueVisible) {
    Dataset ds = plot.getDatasets().get(datasetIdx);
    RangeAxis rangeAxis = plot.getRangeAxis(datasetIdx);
    ArrayList<Dataset> sdatasets = (ArrayList<Dataset>) ds.getUserData("datasets");
    String rlabel = ds.getRangeLabel();
    if (sdatasets != null && dimension < sdatasets.size()) {
      Dataset dataset = sdatasets.get(dimension);
      if (dataset != null) {
        rlabel = dataset.getRangeLabel();
      }
    }
    String lbl = rlabel + rangeAxis.getLabelSuffix();

    final boolean doShowRangeValue = (pointIdx > -1);
    if (doShowRangeValue && valueVisible) {
      double yData = rangeAxis.isCalcRangeAsPercent() ? plot
          .calcDisplayY(datasetIdx, pointIdx, dimension)
          : plot.getDataCoord(datasetIdx, pointIdx, dimension);
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
    
}
