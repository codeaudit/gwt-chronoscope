package org.timepedia.chronoscope.client.overlays;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.InfoWindow;
import org.timepedia.chronoscope.client.InfoWindowClosedHandler;
import org.timepedia.chronoscope.client.InfoWindowEvent;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.event.ChartDragEvent;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.render.GssElementImpl;
import org.timepedia.chronoscope.client.util.DateFormatter;
import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.chronoscope.client.util.date.DateFormatterFactory;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import java.util.ArrayList;
import java.util.Date;

@ExportPackage("chronoscope")
public class Marker extends DraggableOverlay implements GssElement, Exportable {

  private InfoWindow currentWindow;

  private InfoWindow wasOpenWindow;

  private String typeClass;

  protected GssProperties guideLineProps;

  protected DateFormatter guideLineDateFmt;

  private static enum MarkerShape {

    BALLOON, TEARDROP
  }

  ;

  // Determines how high (stretched-out) the marker is.
  private static final int MARKER_HEIGHT = 15;

  private ArrayList<OverlayClickListener> clickListeners;

  private int datasetIdx = -1;

  protected double domainX;

  private boolean isScreenPropsSet = false;

  private String label;

  protected GssProperties markerProps;

  private MarkerShape markerShape;

  protected double rangeY;

  protected int labelWidth, labelHeight;

  protected boolean guideLine = true;

  protected String guideLineDate = null;

  @Export
  public boolean isGuideLine() {
    return guideLine;
  }

  @Export
  public void setGuideLine(boolean guideLine) {
    this.guideLine = guideLine;
  }

  public String getGuideLineDate() {
    return guideLineDate;
  }

  @Export
  public void setGuideLineDateFormat(String guideLineDate) {
    this.guideLineDate = guideLineDate;
    if (guideLineDate != null) {
      this.guideLineDateFmt = DateFormatterFactory.getInstance()
          .getDateFormatter(guideLineDate);
    }
  }

  public Marker(double domainX, String label, int datasetIdx) {
    this.domainX = domainX;
    this.label = label;
    typeClass = label;
    // Silently fix an invalid dataset index
    this.datasetIdx = Math.max(0, datasetIdx);
  }

  public Marker(double domainX, String label, int datasetIdx,
      String typeClass) {
    this.domainX = domainX;
    this.label = label;
    this.typeClass = typeClass;
    // Silently fix an invalid dataset index
    this.datasetIdx = Math.max(0, datasetIdx);
  }

  /**
   */
  @Export
  public Marker(String date, int datasetIdx, String label) {
    this(Date.parse(date), label, datasetIdx);
  }

  @Export
  public Marker(String date, int datasetIdx, String label, String typeClass) {
    this(Date.parse(date), label, datasetIdx, typeClass);
  }

  /**
   */
  @Export("addOverlayListener")
  public void addOverlayClickListener(OverlayClickListener ocl) {
    if (clickListeners == null) {
      clickListeners = new ArrayList<OverlayClickListener>();
    }
    clickListeners.add(ocl);
  }

  public void click(int x, int y) {
    if (clickListeners != null) {
      for (OverlayClickListener l : clickListeners) {
        l.onOverlayClick(this, x, y);
      }
    }
  }

  public void draw(Layer backingCanvas, String layer) {
    if (plot == null) {
      throw new IllegalStateException("plot not set");
    }
    if (handleInfoWindowVisibility()) {
      return;
    }

    lazyInitScreenProps(backingCanvas);

    double x = plot.domainToScreenX(domainX, datasetIdx);
    double yp = plot.rangeToScreenY(rangeY, datasetIdx);
    double y = yp;

    double proposedMarkerTop = y - MARKER_HEIGHT - labelHeight;
    if (proposedMarkerTop > plot.getInnerBounds().y) {
      y = proposedMarkerTop;
      markerShape = MarkerShape.BALLOON;
    } else {
      y += MARKER_HEIGHT;
      markerShape = MarkerShape.TEARDROP;
    }

    backingCanvas.save();
    if ((guideLine || guideLineProps.visible) && !isDragging()) {
      drawGuideLine(backingCanvas, (int) x);
    }

    int arcDirection = (y < yp) ? 1 : 0;
    if (currentWindow != null) {
      currentWindow.setPosition(plot.domainToWindowX(domainX, datasetIdx),
          plot.rangeToWindowY(rangeY, datasetIdx) + 5);
    }
    x = drawOval(labelWidth + 1, labelHeight - 3, markerProps, backingCanvas,
        x, y, yp, arcDirection);

    backingCanvas
        .drawText(x, y, label, markerProps.fontFamily, markerProps.fontWeight,
            markerProps.fontSize, layer, Cursor.CONTRASTED);
    backingCanvas.restore();
  }

  protected boolean handleInfoWindowVisibility() {
    // if no longer visible, close window
    if (!plot.getDomain().containsOpen(domainX)) {
      if (currentWindow != null) {
        wasOpenWindow = currentWindow;
        currentWindow.close();
      }
      return true;
    }

    // if window was hidden, and marker is now visible, show it
    if (currentWindow == null && wasOpenWindow != null) {
      currentWindow = wasOpenWindow;
      wasOpenWindow = null;
      currentWindow.open();
    }
    return false;
  }

  public int getDatasetIndex() {
    return this.datasetIdx;
  }

  @Export
  public double getDomainX() {
    return domainX;
  }

  public GssElement getParentGssElement() {
    return null;
  }

  @Export
  public double getRangeY() {
    return rangeY;
  }

  public String getType() {
    return "marker";
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getTypeClass() {
    return typeClass;
  }
  // NOTE - DatasetRenderer.drawFocusPointGuideLine is for points with guidelines (eg point:focus guideline) this is for marker guidelines
  // TODO - DRY
   public void drawGuideLine(Layer layer, int x) {
    if(guideLineProps == null) return;
    
    layer.save();
    layer.setFillColor(guideLineProps.color);
    double lt = Math.max(guideLineProps.lineThickness, 1);
    int coffset = (int) Math.floor(lt / 2.0);

    layer.fillRect(x - coffset, 0, lt, layer.getBounds().height);
    if (guideLineDate != null) {
      layer.setStrokeColor(Color.BLACK);
      int hx = x;
      double dx = ((DefaultXYPlot) plot)
          .windowXtoDomain(hx + ((DefaultXYPlot) plot).getBounds().x);
      String label = guideLineDateFmt.format(dx);
      hx += dx < plot.getDomain().midpoint() ? 1.0
          : -1 - layer.stringWidth(label, "Helvetica", "", "8pt");
      // TODO - factor hard-coded font out 
      layer.drawText(hx, 0, label, "Helvetica", "", "8pt", "overlays", Cursor.CLICKABLE);
    }
    layer.restore();
  }

  public boolean isHit(int x, int y) {
    if (plot == null) {
      throw new IllegalStateException("plot not set");
    }

    final double mx = plot.domainToWindowX(domainX, datasetIdx);
    final double xPad = labelWidth / 2 + 3;
    final boolean isHitX = MathUtil.isBounded(x, mx - xPad, mx + xPad);

    final double my = plot.rangeToWindowY(rangeY, datasetIdx);
    boolean isHitY;
    if (markerShape == MarkerShape.BALLOON) {
      double topOfBalloon = my - MARKER_HEIGHT - labelHeight;
      isHitY = MathUtil.isBounded(y, topOfBalloon, my);
    } else { // assumed to be TEARDROP
      double bottomOfTeardrop = my + MARKER_HEIGHT + labelHeight;
      isHitY = MathUtil.isBounded(y, my, bottomOfTeardrop);
    }

    return isHitX && isHitY;
  }

  /**
   */
  @Export
  public InfoWindow openInfoWindow(String html) {
    if (plot == null) {
      throw new IllegalStateException("plot not set");
    }
    if (currentWindow != null) {
      currentWindow.close();
    }
    wasOpenWindow = null;
    currentWindow = plot.openInfoWindow(html, domainX, rangeY, datasetIdx);
    currentWindow.addInfoWindowClosedHandler(new InfoWindowClosedHandler() {
      public void onInfoWindowClosed(InfoWindowEvent event) {
        currentWindow = null;
      }
    });
    return currentWindow;
  }

  public void removeOverlayClickListener(OverlayClickListener listener) {
    if (clickListeners != null) {
      clickListeners.remove(listener);
    }
  }

  public void setDatasetIndex(int datasetIndex) {
    this.datasetIdx = datasetIndex;
  }

  public void setPlot(XYPlot plot) {
    super.setPlot(plot);
    if (plot != null) {
      rangeY = interpolateRangeY(domainX, datasetIdx);
    }
  }

  public String toString() {
    return this.label;
  }

  public static double drawOval(int width, int height,
      GssProperties markerProperties, Layer backingCanvas, double x, double y,
      double yp, int dir) {
    backingCanvas.setStrokeColor(markerProperties.color);
    backingCanvas.setTransparency(1.0f);
    backingCanvas.beginPath();

    x -= width / 2;
    backingCanvas.setShadowOffsetX(0);
    backingCanvas.setShadowOffsetY(0);
    backingCanvas.setShadowBlur(0);

    /*
    double startAngle = Math.PI * 2.0 - Math.PI / 2 + Math.PI / 8;
    double endAngle = Math.PI * 2.0 - Math.PI / 2 - Math.PI / 8;
    if (dir == 0) {
      startAngle = Math.PI * 2.0 - Math.PI / 2 - Math.PI / 4 - Math.PI / 8;
      endAngle = Math.PI * 2.0 - Math.PI / 2 + Math.PI / 4 + Math.PI / 8;
    }
    */

    backingCanvas
        .arc(x + width / 2, y + height / 2, width + 1, 0, Math.PI, dir);
    backingCanvas.lineTo(x + (width + 1) / 2, yp);
    backingCanvas.closePath();

    backingCanvas.setFillColor(markerProperties.bgColor);
    backingCanvas.fill();
    backingCanvas.setLineWidth(markerProperties.lineThickness);
    backingCanvas.setShadowOffsetX(3);
    backingCanvas.setShadowOffsetY(3);
    backingCanvas.setShadowBlur(3);
    backingCanvas.stroke();
    return x;
  }

  private int drawBox(Layer backingCanvas, int x, int y) {
    backingCanvas.setStrokeColor(Color.BLACK);
    backingCanvas.setTransparency(1.0f);
    backingCanvas.beginPath();

    x -= labelWidth / 2;
    backingCanvas.setShadowOffsetX(0);
    backingCanvas.setShadowOffsetY(0);
    backingCanvas.setShadowBlur(0);
    backingCanvas.moveTo(x - 1, y);
    backingCanvas.lineTo(x + labelWidth + 3, y);
    backingCanvas.lineTo(x + labelWidth + 3, y + labelHeight);
    backingCanvas.lineTo(x - 1, y + labelHeight);
    backingCanvas.closePath();
    backingCanvas.setFillColor(new Color(200, 200, 200));
    backingCanvas.fill();
    backingCanvas.setLineWidth(1);
    backingCanvas.stroke();
    return x;
  }

  protected double interpolateRangeY(double domainX, int datasetIdx) {
    int p = plot.getNearestVisiblePoint(domainX, datasetIdx) - 1;
    p = Math.max(p, 0);

    // linearly interpolate rangeY from domainX and its surrounding 2 data points
    double d0 = plot.getDataX(datasetIdx, p);
    double d1 = plot.getDataX(datasetIdx, p + 1);
    double r0 = plot.getDataY(datasetIdx, p);
    double r1 = plot.getDataY(datasetIdx, p + 1);

    double interplatedRangeY = r0 + (domainX - d0) / (d1 - d0) * (r1 - r0);
    return interplatedRangeY;
  }

  protected void lazyInitScreenProps(Layer layer) {
    if (!isScreenPropsSet) {
      View view = plot.getChart().getView();
      markerProps = view.getGssProperties(this, "");
      labelWidth = layer.stringWidth(label, markerProps.fontFamily, "normal",
          markerProps.fontSize);
      labelHeight = layer.stringHeight(label, markerProps.fontFamily, "normal",
          markerProps.fontSize) + 2;
      guideLineProps = view
          .getGssProperties(new GssElementImpl("guideline", this), "");

      setGuideLine(guideLineProps.visible);
      setGuideLineDateFormat(guideLineProps.dateFormat);
      isScreenPropsSet = true;
    }
  }

  @Override
  public void onDrag(ChartDragEvent event) {

    int point = plot.getNearestVisiblePoint(
        ((DefaultXYPlot) plot).windowXtoDomain(event.getCurrentX()),
        datasetIdx);
//    rangeY = interpolateRangeY(domainX, getDatasetIndex());
    if (point > -1) {
      domainX = plot.getDataX(datasetIdx, point);
      rangeY = plot.getDataY(datasetIdx, point);
    }
    super.onDrag(event);
  }
}
