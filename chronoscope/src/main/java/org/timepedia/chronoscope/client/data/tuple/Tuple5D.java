package org.timepedia.chronoscope.client.data.tuple;


/**
 * A Tuple of 5 coordinates. Most useful for candlestick style financial charts,
 * such as tuples of (timestamp, open, high, low, last)
 */
public interface Tuple5D extends Tuple3D {
  
  double getFourth();
  
  double getFifth();

}
