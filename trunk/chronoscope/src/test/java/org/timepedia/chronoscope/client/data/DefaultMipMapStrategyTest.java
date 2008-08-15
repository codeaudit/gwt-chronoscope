package org.timepedia.chronoscope.client.data;

import junit.framework.TestCase;

import org.timepedia.chronoscope.client.data.Array2D;
import org.timepedia.chronoscope.client.data.DefaultMipMapStrategy;
import org.timepedia.chronoscope.client.data.MipMapStrategy;
import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.util.junit.OODoubleArray;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author chad takahashi
 */
public class DefaultMipMapStrategyTest extends TestCase {
  private MipMapStrategy mipmap;
  private Collection<SimpleDataset> testDatasets;
  
  private enum ConstructType {
    DEFAULT {
      public Array2D buildMultiDomain(MipMapStrategy mipmap, double[] data) {
        return mipmap.calcMultiDomain(data);
      }
      public Array2D buildMultiRange(MipMapStrategy mipmap, double[] data) {
        return mipmap.calcMultiRange(data);
      }
    },
    APPEND {
      public Array2D buildMultiDomain(MipMapStrategy mipmap, double[] data) {
        OODoubleArray a = new OODoubleArray(data);
        Array2D multiresData = mipmap.calcMultiDomain(a.removeLast().getArray());
        mipmap.appendDomainValue(a.getLast(), multiresData);
        /*
        Array2D multiresData = mipmap.calcMultiDomain(a.getSubArray(0, 0).getArray());
        for (int i = 1; i < data.length; i++) {
          mipmap.appendDomainValue(data[i], multiresData);
        }
        */
        return multiresData;
      }
      public Array2D buildMultiRange(MipMapStrategy mipmap, double[] data) {
        OODoubleArray a = new OODoubleArray(data);
        Array2D multiresData = mipmap.calcMultiRange(a.removeLast().getArray());
        mipmap.appendRangeValue(a.getLast(), multiresData);
        /*
        Array2D multiresData = mipmap.calcMultiRange(a.getSubArray(0, 0).getArray());
        for (int i = 1; i < data.length; i++) {
          mipmap.appendRangeValue(data[i], multiresData);
        }
        */
        return multiresData;
      }
    };
    
    public abstract Array2D buildMultiDomain(MipMapStrategy mipmap, double[] data);

    public abstract Array2D buildMultiRange(MipMapStrategy mipmap, double[] data);

  }
  
  public DefaultMipMapStrategyTest(String name) {
    super(name);
  }

  public void setUp() {
    mipmap = DefaultMipMapStrategy.MAX;
    
    testDatasets = new ArrayList<SimpleDataset>();
    testDatasets.add(new SimpleDataset("ChadChartDemo",
        new double[] {1000, 2000, 3000, 4000},
        new double[] {10, 50, 20, 60}));
    
    /*
    testDatasets.add(new SimpleDataset("testcase_1",
        new double[] {1000, 2000},
        new double[] {90, 20}));
    testDatasets.add(new SimpleDataset("testcase_2",
        new double[] {1000, 2000, 3000},
        new double[] {90, 20, 40}));
    testDatasets.add(new SimpleDataset("testcase_3",
        new double[] {1000, 2000, 3000, 4000},
        new double[] {90, 20, 40, 60}));
    testDatasets.add(new SimpleDataset("testcase_4",
        new double[] {1000, 2000, 3000, 4000, 5000},
        new double[] {90, 20, 40, 60, 100}));
     */
  }

  public void testCalcMultiDomain() {
    for (SimpleDataset ds : testDatasets) {
      // For each dataset, test multiDomain content for each ConstructType.
      // Need to assert that the same multiDomain structure results from all
      // supported mutations as well as the immutable construction.
      for (ConstructType t : ConstructType.values()) {
        //log("Testing constructType " + t);
        Array2D multiDomain = t.buildMultiDomain(mipmap, ds.domain);
        verifyMultiDomain(ds, multiDomain, t);
      }
    }
/*
    double[] bigData = new double[100000];
    double v = 1000;
    for (int i = 0; i < bigData.length; i++) {
      bigData[i] = i;
      i += 1000;
    }
    testCalcMultiDomain(bigData);
*/
  }
  
  public void testCalcMultiRange() {
    for (SimpleDataset ds : testDatasets) {
      for (ConstructType t : ConstructType.values()) {
        Array2D multiRange = t.buildMultiRange(mipmap, ds.range);
        verifyMultiRange(ds, multiRange, t);
      }
    }
  }
  
  private void verifyMultiDomain(SimpleDataset ds, Array2D multiDomain, ConstructType t) {
    final String testCaseId = getTestCaseId(ds, t);

    double[] data = ds.domain;
    int expectedNumLevels = (int) (MathUtil.log2(data.length) + 1);
    assertEquals(testCaseId, expectedNumLevels, multiDomain.numRows());

    log("ds=" + ds + "; t=" + t + "; expectedNumLevels=" + expectedNumLevels);
    
    int expectedColumns = multiDomain.numColumns(0);
    int multiplier = 1;
    for (int i = 0; i < expectedNumLevels; i++) {
      log("level=" + i + "; expectedColumns=" + expectedColumns);
      assertEquals(testCaseId, expectedColumns, multiDomain.numColumns(i));

      for (int j = 0; j < expectedColumns; j++) {
        double expectedValue = data[j * multiplier];
        log("\tlevel=" + i + "; j=" + j + "; domainValue=" + multiDomain.get(i, j));
        assertEquals(testCaseId, expectedValue, multiDomain.get(i, j));
      }

      expectedColumns /= 2;
      multiplier *= 2;
    }
  }
  
  private void verifyMultiRange(SimpleDataset ds, Array2D multiRange, ConstructType t) {
    final String testCaseId = getTestCaseId(ds, t);

    double[] data = ds.range;
    int expectedNumLevels = (int) (MathUtil.log2(data.length) + 1);
    assertEquals(testCaseId, expectedNumLevels, multiRange.numRows());

    log("ds=" + ds + "; t=" + t + "; expectedNumLevels=" + expectedNumLevels);
    
    int expectedColumns = multiRange.numColumns(0);
    int multiplier = 1;
    double[] prevLevel = null;
    for (int i = 0; i < expectedNumLevels; i++) {
      assertEquals(testCaseId, expectedColumns, multiRange.numColumns(i));
      log("level=" + i + "; expectedColumns=" + expectedColumns);

      for (int j = 0; j < expectedColumns; j++) {
        double expectedValue;
        if (i == 0) {
          expectedValue = data[j];
        }
        else {
          expectedValue = Math.max(prevLevel[j * 2], prevLevel[j * 2 + 1]);
        }
        
        log("\tlevel=" + i + "; j=" + j + "; rangeValue=" + multiRange.get(i, j));
        assertEquals(testCaseId, expectedValue, multiRange.get(i, j));
        
      }
      
      // Store current row for use with next level up.
      prevLevel = getRow(multiRange, i);
      
      
      expectedColumns /= 2;
      multiplier *= 2;
    }
  }

  private double[] getRow(Array2D a, int rowIndex) {
    double[] row = new double[a.numColumns(rowIndex)];
    for (int i = 0; i < row.length; i++) {
      row[i] = a.get(rowIndex, i);
    }
    return row;
  }
  
  private static String getTestCaseId(SimpleDataset ds, ConstructType ct) {
    return "[" + ds.id + " " + ct + "]";
  }
  
  private static void log(Object msg) {
    System.out.println("> " + msg);
  }
}
