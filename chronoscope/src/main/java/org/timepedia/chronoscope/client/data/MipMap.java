package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.util.Array1D;
import org.timepedia.chronoscope.client.util.Array2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a version of some {@link Dataset} at a decreased level
 * of resolution.
 * 
 * @author chad takahashi
 */
public class MipMap {
  private int mipLevel;
  private Array2D mipMappedDomain;
  private List<Array2D> mipMappedRangeTuple = new ArrayList<Array2D>();
  
  MipMap(Array2D mipMappedDomain, List<Array2D> mipMappedRangeTuple, int mipLevel) {
    this.mipMappedDomain = mipMappedDomain;
    this.mipMappedRangeTuple = mipMappedRangeTuple;
    this.mipLevel = mipLevel;
  }
  
  public Array1D getDomain() {
    return this.mipMappedDomain.getRow(mipLevel);
  }
  
  public Array1D getRange(int tupleIndex) {
    return this.mipMappedRangeTuple.get(tupleIndex).getRow(mipLevel);
  }
  
  /**
   * Returns the number of elements in each range tuple within this mipmap. 
   */
  public int getRangeTupleSize() {
    return mipMappedRangeTuple.size();
  }
  
  /**
   * The number of data points in this mipmap.
   */
  public int size() {
    return this.mipMappedDomain.numColumns(mipLevel);
  }
}
