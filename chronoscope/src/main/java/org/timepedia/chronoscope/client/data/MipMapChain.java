package org.timepedia.chronoscope.client.data;

import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.util.Array2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An ordered set of {@link MipMap} objects, where each MipMap represents
 * a compressed version of the raw {@link Dataset}.
 *   
 * @author chad takahashi
 */
public class MipMapChain {
  private Map<String,MipMap> name2mipmap;
  private List<MipMap> mipMaps;
  private final int rangeTupleSize;
  
  private Array2D mipMappedDomain;
  private List<Array2D> mipMappedRangeTuples;

  public MipMapChain(Array2D mipMappedDomain, List<Array2D> mipMappedRangeTuples) {
    this(mipMappedDomain, mipMappedRangeTuples, null);
  }

  public MipMapChain(Array2D mipMappedDomain, List<Array2D> mipMappedRangeTuples,
        List<String> mipLevelNames) {
    
    validate(mipMappedDomain, mipMappedRangeTuples);
    this.rangeTupleSize = mipMappedRangeTuples.size();
    
    final int numMipLevels = mipMappedDomain.numRows();
    
    this.mipMaps = new ArrayList<MipMap>();
    for (int i = 0; i < numMipLevels; i++) {
      MipMap mipMap = new MipMap(mipMappedDomain, mipMappedRangeTuples, i);
      this.mipMaps.add(mipMap);
    }
    
    if (mipLevelNames != null) {
      if (mipLevelNames.size() != numMipLevels) {
        throw new IllegalArgumentException("mipLevelNames.size() != numMipLevels");
      }
      this.name2mipmap = new HashMap<String,MipMap>();
      for (int i = 0; i < numMipLevels; i++) {
        this.name2mipmap.put(mipLevelNames.get(i), this.mipMaps.get(i));
      }
    }
    
    this.mipMappedDomain = mipMappedDomain;
    this.mipMappedRangeTuples = mipMappedRangeTuples;
  }
  
  void addMipLevel(int mipLevel) {
    MipMap mipMap = new MipMap(mipMappedDomain, mipMappedRangeTuples, mipLevel);
    this.mipMaps.add(mipMap);
  }
  
  /**
   * Returns the index-th mapmap in this chain, where index 0 represents
   * the raw data.
   */
  public MipMap getMipMap(int index) {
    return mipMaps.get(index);
  }
  
  /**
   * Returns the mipmap bound to the specified name.
   */
  public MipMap getMipMap(String name) {
    ArgChecker.isNotNull(name, "name");
    if (name2mipmap != null) {
      return name2mipmap.get(name);
    }
    else {
      throw new UnsupportedOperationException("named MipMaps not supported for this object");
    }
  }
  
  /**
   * Returns the number of elements in each range tuple.
   */
  public int getRangeTupleSize() {
    return this.rangeTupleSize;
  }
  
  /**
   * Returns the number of {@MipMap} objects in this chain.
   */
  public int size() {
    return mipMaps.size();
  }
  
  /**
   * Returns the {@link Array2D} object that backs the domain values
   * in this object.  This method is intended for use with JUnit tests.
   */
  Array2D getMipMappedDomain() {
    return this.mipMappedDomain;
  }
  
  /**
   * Returns the list of {@link Array2D} objects that back the range
   * tuple values in this object.  This method is intended for use with 
   * JUnit tests.
   */
  List<Array2D> getMipMappedRangeTuples() {
    return this.mipMappedRangeTuples;
  }  
  
  private void validate(Array2D mipMappedDomain, List<Array2D> mipMappedRangeTuple) {
    ArgChecker.isNotNull(mipMappedDomain, "mipMappedDomain");
    ArgChecker.isNotNull(mipMappedRangeTuple, "mipMappedRangeTuple");
    
    if (mipMappedRangeTuple.isEmpty()) {
      throw new IllegalArgumentException("mipMappedRangeTuple list was empty");
    }
    
    for (int i = 0; i < mipMappedRangeTuple.size(); i++) {
      Array2D mipMappedRange = mipMappedRangeTuple.get(i);
      if (!mipMappedDomain.isSameSize(mipMappedRange)) {
        throw new IllegalArgumentException("mipMappedDoamin and " +
            "mipMappedRange(" + i + ") are difference sizes");
      }
    }
  }
}
