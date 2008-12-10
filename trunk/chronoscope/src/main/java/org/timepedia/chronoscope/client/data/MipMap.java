package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.data.tuple.Tuple2D;
import org.timepedia.chronoscope.client.data.tuple.Tuple5D;
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
  private SimpleTuple flyweightTuple = new SimpleTuple();
  
  MipMap(Array2D multiResDomain, Array2D[] multiResRangeTuple, int mipLevel) {
    this.mipLevel = mipLevel;
    this.domain = multiResDomain.getRow(mipLevel);
    
    this.rangeTuples = new Array1D[multiResRangeTuple.length];
    for (int i = 0; i < this.rangeTuples.length; i++) {
      this.rangeTuples[i] = multiResRangeTuple[i].getRow(mipLevel);
    }
  }
  
  public Array1D getDomain() {
    return this.domain;
    //return this.multiResDomain.getRow(mipLevel);
  }
  
  /**
   * Returns the ordinal mip level of this mipmap object.  Level 0
   * represents the "raw" dataset data.
   */
  public int getLevel() {
    return this.mipLevel;
  }
  
  public Tuple2D getTuple(int dataPointIndex) {
    ArgChecker.isLTE(dataPointIndex, this.size() - 1, "dataPointIndex");
    this.flyweightTuple.init(dataPointIndex, this.domain, this.rangeTuples);
    return this.flyweightTuple;
  }
  
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
      final SimpleTuple tuple = new SimpleTuple(startIndex - 1, domain, rangeTuples);
      
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
  
  private static final class SimpleTuple implements Tuple5D {
    private int index;
    private double[][] tupleData;
    private int tupleLength;
    
    public SimpleTuple() {
      // do nothing
    }
    
    public SimpleTuple(int index, Array1D domain, Array1D[] rangeTuples) {
      init(index, domain, rangeTuples);
    }
    
    public void init(int index, Array1D domain, Array1D[] rangeTuples) {
      this.index = index;
      this.tupleLength = 1 + rangeTuples.length;
      if (tupleData == null || tupleData.length != this.tupleLength) {
        tupleData = new double[tupleLength][];
      }
      tupleData[0] = domain.backingArray();
      for (int i = 1; i < this.tupleLength; i++) {
        tupleData[i] = rangeTuples[i - 1].backingArray();
      }
    }
    
    public double get(int tupleIndex) {
      return tupleData[tupleIndex][this.index];
    }

    public int size() {
      return tupleLength;
    }

    public double getFirst() {
      return tupleData[0][this.index];
    }

    public double getSecond() {
      return tupleData[1][this.index];
    }

    public double getThird() {
      return tupleData[2][this.index];
    }

    public double getFourth() {
      return tupleData[3][this.index];
    }

    public double getFifth() {
      return tupleData[4][this.index];
    }
    
    public void next() {
      ++this.index;
    }
  }
}
