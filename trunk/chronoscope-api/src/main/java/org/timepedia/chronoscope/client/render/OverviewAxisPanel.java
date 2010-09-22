package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.Cursor;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.gss.GssProperties;

/**
 * Renders the overview axis.
 */
public class OverviewAxisPanel extends AxisPanel {

  public static final int OVERVIEW_HEIGHT = 48;
  
  // The singleton avoids excess creation of Bounds objects
  private Bounds highlightBounds, highlightBoundsSingleton;
  
  private Layer overviewLayer;

  private boolean initialized = false;

  public boolean visible = true;

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
    // layer.drawImage(overviewLayer, 0, 0, overviewLayer.getWidth(),
    //  overviewLayer.getHeight(), bounds.x, bounds.y, bounds.width,
    // bounds.height);

    layer.drawImage(overviewLayer, 0, 0, overviewLayer.getWidth(), overviewLayer.getHeight(),
                    bounds.x, bounds.y, bounds.width, bounds.height);

    highlightBounds = calcHighlightBounds(plot, bounds);
    
if (highlightBounds != null) {
      layer.save();
      layer.setFillColor(gssProperties.bgColor);
      layer.setTransparency((float) Math.max(0.5f, gssProperties.transparency));
      layer.fillRect(highlightBounds.x, highlightBounds.y,
          highlightBounds.width, highlightBounds.height);
      layer.setStrokeColor(gssProperties.color);
      layer.setTransparency(1.0f);
      layer.setLineWidth(gssProperties.lineThickness);
      layer.beginPath();
      final double halfLineWidth = gssProperties.lineThickness / 2;
      // fix for Opera, on Firefox/Safari, rect() has implicit moveTo
      layer.moveTo(highlightBounds.x, highlightBounds.y + halfLineWidth);
      layer.rect(highlightBounds.x, highlightBounds.y + halfLineWidth,
          highlightBounds.width, highlightBounds.height - gssProperties.lineThickness);

      // layer.stroke();

     //  layer.restore();


    

      //plot.getChart().setCursor(Cursor.SELECTING);

      // layer.setFillColor(gssProperties.bgColor);
      // layer.setTransparency((float) Math.max(0.5f, gssProperties.transparency));

      // the old way - dimming the highlight
      //  layer.fillRect(highlightBounds.x, highlightBounds.y,
      //      highlightBounds.width, highlightBounds.height);

      // left side
      // this would fill left side up to focus
      // layer.fillRect(0, highlightBounds.y, highlightBounds.x, highlightBounds.height);

      // this 'fills' a small handlebar instead
      // layer.setFillColor(gssProperties.bgColor);

      // layer.fillRect(highlightBounds.x-gssProperties.getLineThickness() , highlightBounds.y, gssProperties.getLineThickness(), highlightBounds.height);

      // layer.clip(0, highlightBounds.y, highlightBounds.x, highlightBounds.height);


      // right side
      // layer.fillRect(highlightBounds.x + highlightBounds.width, highlightBounds.y, overviewLayer.getWidth()-(highlightBounds.x + highlightBounds.width), highlightBounds.height);

      // this 'fills' a small handlebar instead
      // layer.fillRect(highlightBounds.x + highlightBounds.width, highlightBounds.y, gssProperties.getLineThickness(), highlightBounds.height);

      // layer.clip(highlightBounds.x, highlightBounds.y, highlightBounds.width, highlightBounds.height);        
      // layer.clip(highlightBounds.x + highlightBounds.width, highlightBounds.y, overviewLayer.getWidth()-(highlightBounds.x + highlightBounds.width), highlightBounds.height);

      // layer.setLayerAlpha(1.0f);


      // clip the focus


      // layer.drawImage(overviewLayer, 0, overviewLayer.getHeight()-OverviewAxisPanel.OVERVIEW_HEIGHT,
          //              overviewLayer.getWidth(), OverviewAxisPanel.OVERVIEW_HEIGHT,
            //            bounds.x, bounds.y, bounds.width, bounds.height);

      // layer.drawImage(overviewLayer, 0, 0, overviewLayer.getWidth(), overviewLayer.getHeight(),
        //        bounds.x, bounds.y, bounds.width, bounds.height);


      // vertical line
//      layer.setStrokeColor(gssProperties.color);
//      layer.setLineWidth(gssProperties.lineThickness);
//      layer.moveTo(highlightBounds.x, highlightBounds.y);
//      layer.lineTo(highlightBounds.x, overviewLayer.getHeight());
//      layer.stroke();
//
//      layer.moveTo(highlightBounds.x + highlightBounds.width, highlightBounds.y);
//      layer.lineTo(highlightBounds.x + highlightBounds.width, overviewLayer.getHeight());
//      layer.stroke();

      // layer.beginPath();
      // final double halfLineWidth = gssProperties.lineThickness / 2;
      // fix for Opera, on Firefox/Safari, rect() has implicit moveTo

      // layer.moveTo(highlightBounds.x, highlightBounds.y + halfLineWidth);

      // layer.rect(highlightBounds.x, highlightBounds.y + halfLineWidth,
         //  highlightBounds.width, highlightBounds.height - gssProperties.lineThickness);
        //  layer.stroke();

       //  layer.moveTo(highlightBounds.x, highlightBounds.y);



        plot.getChart().setCursor(Cursor.SELECTING);

        layer.restore();
      
    }
    else {
      plot.getChart().setCursor(Cursor.DEFAULT);
    }
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
      bounds.height = OVERVIEW_HEIGHT; // TODO - fix top of bottom layer vs top of overview issue
      // if (bounds.height < OVERVIEW_HEIGHT) {
      //   bounds.height = OVERVIEW_HEIGHT;
      // }
    } else {
        bounds.height = 0;
    }
    
    //bounds.width = view.getWidth();
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
      b.x = beginHighlight;
      b.y = axisBounds.y;
      b.width = endHighlight - beginHighlight;
      b.height = axisBounds.height;
    }
    
    return b;
  }

}
