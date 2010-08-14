package org.timepedia.chronoscope.client.util;

/**
 * A function to be applied to an array of primitive <tt>double</tt> values
 * within a {@link Array1D} object.  Implementing classes are responsible for
 * defining whatever return value is necessary as an accessor method.
 * 
 * @see Array1D#execFunction(ArrayFunction)
 * 
 * @author chad takahashi
 */
public interface ArrayFunction {
  
  void exec(double[] data, int arrayLength);
  
}
