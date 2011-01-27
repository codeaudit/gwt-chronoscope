package org.timepedia.chronoscope.client.data;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Array1D;
import org.timepedia.chronoscope.client.util.Array2D;
import org.timepedia.chronoscope.client.util.ExtremaArrayFunction;
import org.timepedia.chronoscope.client.util.Interval;
import org.timepedia.chronoscope.client.util.Util;

/**
 * Represents a version of a {@link Dataset} at a decreased level
 * of resolution.
 * 
 * @see MipMapChain
 * @see Dataset#getMipMapChain()
 * 
 * @author chad takahashi
 */
public class MipMap {

  private static Interval[] computeExtrema(Array1D[] rangeTuples) {
    Interval[] extrema = new Interval[rangeTuples.length];
    // Assign min/max range-Y values
    ExtremaArrayFunction extremaFn = new ExtremaArrayFunction();
    for (int i = 0; i < extrema.length; i++) {
      Array1D rangeVals = rangeTuples[i];
      rangeVals.execFunction(extremaFn);
      extrema[i] = extremaFn.getExtrema();
    }
    return extrema;
  }
  
  MipMap nextMipMap;
  private Array1D domain;
  private FlyweightTuple flyweightTuple;
  private int mipLevel;
  private Array1D[] rangeTuples;

  private Interval[] rangeExtrema;
  
  public String toString() {
    String ret = "";
    ret += " mipLevel:" + mipLevel;
    ret += " domain: " + Util.arrayToString(domain.toArray());
    for (Interval i : rangeExtrema) {
      ret += ", start:" + i.getStart() + ", end:" + i.getEnd();
    }
    for (Array1D a : rangeTuples) {
      ret += ", tuples: " + Util.arrayToString(a.toArray());
    }
    ret += ", flyweightTuple: {" + flyweightTuple + "}";
    return ret; 
  }

  public MipMap(Array1D domain, Array1D[] rangeTuples, Interval[] extrema) {
    assert rangeTuples.length == extrema.length;
    ArgChecker.isNotNull(domain, "domain");
    ArgChecker.isNotNull(rangeTuples, "rangeTuples");

    this.mipLevel = 0;
    this.domain = domain;
    this.rangeTuples = rangeTuples;
    this.rangeExtrema = extrema;
    this.flyweightTuple = new FlyweightTuple(this.domain, this.rangeTuples);
  }

  public MipMap(Array1D domain, Array1D[] rangeTuples) {
    this(domain, rangeTuples, computeExtrema(rangeTuples));
  }

  public MipMap(Array2D multiResDomain, Array2D[] multiResRangeTuple, int mipLevel) {
    ArgChecker.isNotNull(multiResDomain, "multiResDomain");
    ArgChecker.isNotNull(multiResRangeTuple, "multiResRangeTuple");
    ArgChecker.isNonNegative(mipLevel, "mipLevel");
    
    this.mipLevel = mipLevel;
    this.domain = multiResDomain.getRow(mipLevel);
    
    this.rangeTuples = new Array1D[multiResRangeTuple.length];
    for (int i = 0; i < this.rangeTuples.length; i++) {
      this.rangeTuples[i] = multiResRangeTuple[i].getRow(mipLevel);
    }
    this.flyweightTuple = new FlyweightTuple(this.domain, this.rangeTuples);
    this.rangeExtrema = computeExtrema(this.rangeTuples);
  }

  public MipMap(Array1D domain, Array1D[] range, int mipLevel, MipMap next) {
    this(domain, range);
    this.mipLevel = mipLevel;
    this.nextMipMap = next;
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
   * Returns the array of range values for a specific tuple element
   *  within this {@link MipMap}.
   */
  public Array1D getRange(int tupleIndex) {
    return this.rangeTuples[tupleIndex];
  }

  /**
   * Return the range extreme for this mipmap

   */
  public Interval getRangeExtrema(int mipMapIndex) {
    return this.rangeExtrema[mipMapIndex];
  }

  /**
   * Returns the number of elements in each range tuple within this mipmap. 
   */
  public int getRangeTupleSize() {
    return this.rangeTuples.length;
  }
  
  /**
   * Returns the datapoint tuple at the specified index within this {@link MipMap}.
   */
  public Tuple2D getTuple(int dataPointIndex) {
    ArgChecker.isLTE(dataPointIndex, this.size() - 1, "dataPointIndex");
    this.flyweightTuple.setDomainAndRange(domain, rangeTuples);
    this.flyweightTuple.setDataPointIndex(dataPointIndex);
    return this.flyweightTuple;
  }
  
  /**
   * Returns an iterator over tuple data points at the resolution
   * of this mipmap.
   */
  public Iterator<Tuple2D> getTupleIterator(final int startIndex) {
    final Array1D domain = this.domain;
    final Array1D[] rangeTuples = this.rangeTuples;
    
    return new Iterator<Tuple2D>() {
      final FlyweightTuple tuple = new FlyweightTuple(domain, rangeTuples);
      final int endIndex = domain.size() - 1;
      int ptr = startIndex;
      
      public boolean hasNext() {
        return ptr <= endIndex;
      }

      public Tuple2D next() {
        if (!hasNext()) {
          throw new NoSuchElementException("ptr=" + ptr + ", endIndex=" + endIndex);
        }
        tuple.setDataPointIndex(ptr++);
        return tuple;
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
  
  public boolean isEmpty() {
    return this.domain.isEmpty();
  }
  
  /**
   * Returns the next {@link MipMap} in this chain.
   */
  public MipMap next() {
    return this.nextMipMap;
  }
  
  /**
   * The number of data points in this mipmap.
   */
  public int size() {
    return this.domain.size();
  }
  
  public void clear() {
    domain.clear();
    domain = null;
    flyweightTuple.clear();
    flyweightTuple = null;
    rangeExtrema = null;
    for (Array1D a: rangeTuples) {
      a.clear();
    }
    rangeTuples = null;
  }
  
}
