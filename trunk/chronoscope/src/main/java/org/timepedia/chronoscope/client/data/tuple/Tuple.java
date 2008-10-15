package org.timepedia.chronoscope.client.data.tuple;

/**
 * A tuple is a homogeneous vector of doubles of fixed dimension D returned by
 * the getDimension() method. The first dimension, by convention, is expected
 * to be timestamp values when used with DateAxis. 
 */
public interface Tuple {
  
  Tuple copy();

  double getCoordinate(int i);
  
  int getDimension();
  
}
