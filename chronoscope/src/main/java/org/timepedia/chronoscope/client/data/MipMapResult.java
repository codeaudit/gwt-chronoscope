/**
 * 
 */
package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.util.Array2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Result object that holds the mipmapped domain and tuple range values.
 * 
 * @author chad takahashi
 *
 */
public class MipMapResult {

  public Array2D domain;
  
  public List<Array2D> tupleRange = new ArrayList<Array2D>();
  
}
