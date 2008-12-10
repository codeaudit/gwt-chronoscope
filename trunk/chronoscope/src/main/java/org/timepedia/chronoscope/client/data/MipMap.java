package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Array1D;
import org.timepedia.chronoscope.client.util.Array2D;

import java.util.Iterator;

/**
 * Represents a version of a {@link Dataset} at a decreased level
 * of resolution.
 * 
 * @see {@link MipMapChain}
 * @see {@link Dataset#getMipMapChain()}
 * 
 * @author chad takahashi
 */
public class MipMap {
  private int mipLevel;
  private Array1D domain;
  private Array1D[] rangeTuples;
  private FlyweightTuple flyweightTuple = new FlyweightTuple();
  
  MipMap nextMipMap;
  
  MipMap(Array2D multiResDomain, Array2D[] multiResRangeTuple, int mipLevel) {
    this.mipLevel = mipLevel;
    this.domain = multiResDomain.getRow(mipLevel);
    
    this.rangeTuples = new Array1D[multiResRangeTuple.length];
    for (int i = 0; i < this.rangeTuples.length; i++) {
      this.rangeTuples[i] = multiResRangeTuple[i].getRow(mipLevel);
    }
  }
  
  /**
   * Returns the array of domain values within this {@link MipMap}.
   */
  public Array1D getDomain() {
    return this.domain;
  }
  
  /**
   * Returns the ordinal mip level of this mipmap object.  Level 0
   * represents the "raw" dataset data.
   */
  public int getLevel() {
    return this.mipLevel;
  }
  
  /**
   * Returns the datapoint tuple at the specified index within this {@link MipMap}.
   */
  public Tuple2D getTuple(int dataPointIndex) {
    ArgChecker.isLTE(dataPointIndex, this.size() - 1, "dataPointIndex");
    this.flyweightTuple.init(dataPointIndex, this.domain, this.rangeTuples);
    return this.flyweightTuple;
  }
  
  /**
   * Returns the array of range values for a specific tuple element
   *  within this {@link MipMap}.
   */
  public Array1D getRange(int tupleIndex) {
    return this.rangeTuples[tupleIndex];
  }
  
  /**
   * Returns the number of elements in each range tuple within this mipmap. 
   */
  public int getRangeTupleSize() {
    return this.rangeTuples.length;
  }
  
  /**
   * Returns the next {@link MipMap} in this chain.
   */
  public MipMap next() {
    return nextMipMap;
  }
  
  /**
   * The number of data points in this mipmap.
   */
  public int size() {
    return this.domain.size();
  }
  
  /**
   * Returns an interator over tuple data points at the resolution
   * of this mipmap.
   */
  public Iterator<Tuple2D> getTupleIterator(final int startIndex) {
    final Array1D domain = this.domain;
    final Array1D[] rangeTuples = this.rangeTuples;
    
    return new Iterator<Tuple2D>() {
      final FlyweightTuple tuple = new FlyweightTuple(startIndex - 1, domain, rangeTuples);
      
      public boolean hasNext() {
        // TODO: implement this
        throw new UnsupportedOperationException();
      }

      public Tuple2D next() {
        tuple.next();
        return tuple;
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
  
}
