/**
 * 
 */
package org.timepedia.chronoscope.client.render;

/**
 * Listens for requests to zoom into or out of the chart.
 * 
 * @author Chad Takahashi
 */
public interface ZoomListener {
  
  void onZoom(double intervalInMillis);
  
}
