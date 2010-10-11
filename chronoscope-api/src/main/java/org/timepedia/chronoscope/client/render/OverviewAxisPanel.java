package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;

/**
 * Renders the overview axis.
 */
public class OverviewAxisPanel extends AxisPanel {

  public static final int OVERVIEW_HEIGHT = 42;
  
  // The singleton avoids excess creation of Bounds objects
  private Bounds highlightBounds, highlightBoundsSingleton;
  
  private Layer overviewLayer;

  private boolean initialized = false;

  public boolean visible = true;

  public GssProperties gssLensProperties;

  public OverviewAxisPanel() {
    highlightBoundsSingleton = new Bounds();
  }
  
  /**
   * Returns the bounds of the highlighted area of the overview axis, or
   * null if nothing is highlighted.
   */
  public Bounds getHighlightBounds() {
    return highlightBounds;
  }
  
  public void draw() {

    gssLensProperties = view.getGssProperties(new GssElementImpl("lens", this), "");
    layer.drawImage(overviewLayer,
            0, 0, overviewLayer.getWidth(), bounds.height,
            bounds.x, bounds.y, bounds.width, bounds.height);

    if (visible) { highlightBounds = calcHighlightBounds(plot, bounds); }

    if (highlightBounds != null) {
      layer.save();
      layer.setFillColor(gssProperties.bgColor);
      layer.setTransparency((float) Math.max(0.5f, gssProperties.transparency));
      //draw left rect
      layer.fillRect(bounds.x, bounds.y,
          highlightBounds.x-bounds.x, highlightBounds.height);
      //draw right rect
      layer.fillRect(highlightBounds.x+highlightBounds.width, highlightBounds.y,
          bounds.width-(highlightBounds.x-bounds.x+highlightBounds.width), highlightBounds.height);
      layer.setStrokeColor(gssProperties.color);
      layer.setTransparency(1.0f);
      layer.setLineWidth(gssLensProperties.lineThickness);

      final double halfLineWidth = gssProperties.lineThickness / 2;

      layer.beginPath();

     if (gssLensProperties.borderTop < 0 && gssLensProperties.borderBottom < 0 && gssLensProperties.borderLeft < 0 && gssLensProperties.borderRight < 0) {
          // fix for Opera, on Firefox/Safari, rect() has implicit moveTo
          layer.moveTo(highlightBounds.x, highlightBounds.y + halfLineWidth);
          layer.rect(highlightBounds.x, highlightBounds.y + halfLineWidth,
                  highlightBounds.width, highlightBounds.height - gssLensProperties.lineThickness);
          layer.stroke();
      }else{
          drawRect();
      }


        plot.getChart().setCursor(Cursor.SELECTING);

        layer.restore();
      
    }
    else {
      plot.getChart().setCursor(Cursor.DEFAULT);
    }
  }

  /**
   * Draw a Rectangle with different Line Thickness
   */
  private void drawRect(){
//      double highlightBoundsX = highlightBounds.x;
//      double highlightBoundsY = highlightBounds.y;
//      double highlightBoundsXAddWidth = highlightBounds.x + highlightBounds.width;
//      double highlightBoundsYAddHeight = highlightBounds.y + highlightBounds.height;
//      List<RectLine> listLine = new ArrayList<RectLine>();

      // if borders < 0 use linethickness
      double borderTop = gssLensProperties.borderTop < 0 ? gssLensProperties.lineThickness : gssLensProperties.borderTop;
      double borderBottom = gssLensProperties.borderBottom < 0 ? gssLensProperties.lineThickness : gssLensProperties.borderBottom;
      double borderLeft = gssLensProperties.borderLeft < 0 ? gssLensProperties.lineThickness : gssLensProperties.borderLeft;
      double borderRight = gssLensProperties.borderRight < 0 ? gssLensProperties.lineThickness : gssLensProperties.borderRight;



      layer.setFillColor(gssLensProperties.color);

      // borderTop
      layer.setLineWidth(borderTop);
      fillRectPixelAligned(highlightBounds.x, highlightBounds.y,
                            highlightBounds.width + borderLeft/2 + borderRight/2, borderTop);

      // borderBottom
      layer.setLineWidth(borderBottom);
      fillRectPixelAligned(highlightBounds.x, highlightBounds.y + highlightBounds.height - borderBottom,
                            highlightBounds.width + borderLeft/2 + borderRight/2, borderBottom);

      // borderLeft
      layer.setLineWidth(borderLeft);
      fillRectPixelAligned(highlightBounds.x - borderLeft/2, highlightBounds.y,
                            borderLeft, highlightBounds.height );

      // borderRight
      layer.setLineWidth(borderRight);
      fillRectPixelAligned(highlightBounds.x + highlightBounds.width + borderRight/2, highlightBounds.y,
                            borderRight, highlightBounds.height );



   }


   public GssProperties getGssProperties(){
      return gssProperties;
  }

  
  public String getType() {
    return "overview";
  }
  
  public String getTypeClass() {
    return null;
  }
  
  @Override
  public void layout() {
    if (visible) {
      // if (bounds.height < OVERVIEW_HEIGHT) { bounds.height = OVERVIEW_HEIGHT; }
      bounds.height = OVERVIEW_HEIGHT;
    } else {
      bounds.height = 1; // TEMP
    }

    // default width for now
    if (bounds.width <= 0) {
      bounds.width = view.getWidth();
    }
  }
  
  public void setOverviewLayer(Layer overviewLayer) {
    this.overviewLayer = overviewLayer;
  }

  @Override
  protected void initHook() {
    if (!initialized) { // guard visible from being reset back to initial gss value
      visible = gssProperties.visible;
      initialized = true;
    }
  }

  /*
   * Calculates the bounds of the highlighted area of the overview axis.
   * 
   * @return the bounds of the highlighted area, or <tt>null</tt> if no highlight
   * should be drawn.
   */
  private boolean clean = true;
  private Bounds calcHighlightBounds(XYPlot plot, Bounds axisBounds) {
    double globalDomainMin = plot.getWidestDomain().getStart();
    double globalDomainWidth = plot.getWidestDomain().length();
    double visibleDomainMin = plot.getDomain().getStart();
    double visibleDomainWidth = plot.getDomain().length();
    
    Bounds b;
    // TODO - use same calc as limiting zoom out, rather than just x%
    // TODO - global bounds should allow edge point +max radii for max(hover,focus,etc)
    if (!visible || ((globalDomainWidth - .05*visibleDomainWidth) <= visibleDomainWidth)) {
      // The viewport (i.e. the portion of the domain that is visible within the
      // plot area) is at least as wide as the global domain, so don't highlight.
        if (highlightBounds != null) {
            layer.beginPath();
            if (clean) {
                clean = false;
                ((DefaultXYPlot) plot).redraw(true);
                clean = true;
            }
      }
      b = null;
    }
    else {
      double beginHighlight = axisBounds.x +
          ((visibleDomainMin - globalDomainMin) / globalDomainWidth * axisBounds.width);
      beginHighlight = Math.max(beginHighlight, axisBounds.x);
      
      double endHighlight = axisBounds.x +
          ((visibleDomainMin - globalDomainMin + visibleDomainWidth) / globalDomainWidth * axisBounds.width);
      endHighlight = Math.min(endHighlight, axisBounds.rightX());
      
      b = highlightBoundsSingleton;
      //The border can not block data,increase the width of the highlightBounds
      b.x = beginHighlight- gssLensProperties.borderLeft/2;
      b.y = axisBounds.y;
      b.width = endHighlight - beginHighlight + (gssLensProperties.borderLeft + gssLensProperties.borderRight) / 2;
      b.height = axisBounds.height;
    }
    
    return b;
  }

  private void fillRectPixelAligned(double x, double y, double w, double h) {
    layer.fillRect(Math.floor(x), Math.floor(y), Math.ceil(w), Math.floor(h));
  }

}
