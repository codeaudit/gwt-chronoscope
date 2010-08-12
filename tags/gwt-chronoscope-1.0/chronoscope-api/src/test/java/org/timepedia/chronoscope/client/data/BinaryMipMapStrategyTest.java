package org.timepedia.chronoscope.client.data;

import junit.framework.TestCase;

import org.timepedia.chronoscope.client.util.Array2D;
import org.timepedia.chronoscope.client.util.MathUtil;
import org.timepedia.chronoscope.client.util.junit.OODoubleArray;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author chad takahashi
 */
public class BinaryMipMapStrategyTest extends TestCase {
  private MipMapStrategy mipmap;
  private Collection<JUnitDataset> testDatasets;
  
  private enum ConstructType {
    DEFAULT {
      public MipMapChain mipmap(MipMapStrategy mipmapper, double[] domain, double[] range) {
        return mipmapper.mipmap(domain, range);
      }
    },
    APPEND {
      public MipMapChain mipmap(MipMapStrategy mipmapper, double[] domain, double[] range) {
        OODoubleArray ooDomain = new OODoubleArray(domain);
        OODoubleArray ooRange = new OODoubleArray(range);
        
        double[] modifiedDomain = ooDomain.removeLast().getArray();
        double[] modifiedRange = ooRange.removeLast().getArray();
        MipMapChain mipMapChain = mipmapper.mipmap(modifiedDomain, modifiedRange);
        
        mipmapper.appendXY(ooDomain.getLast(), ooRange.getLast(), mipMapChain);
        //mipmapper.appendDomainValue(ooDomain.getLast(), mipMapChain.getMipMappedDomain());
        //mipmapper.appendRangeValue(ooRange.getLast(), mipMapChain.getMipMappedRangeTuples().get(0));
        return mipMapChain;
      }
    };
    
    public abstract MipMapChain mipmap(MipMapStrategy mipmapper, double[] domain, double[] range);
  }
  
  public void setUp() {
    mipmap = BinaryMipMapStrategy.MAX;
    
    testDatasets = new ArrayList<JUnitDataset>();
    testDatasets.add(new JUnitDataset("ChadChartDemo",
        new double[] {1000, 2000, 3000, 4000},
        new double[] {10, 50, 20, 60}));
    
    testDatasets.add(new JUnitDataset("testcase_1",
        new double[] {1000, 2000},
        new double[] {90, 20}));
    testDatasets.add(new JUnitDataset("testcase_2",
        new double[] {1000, 2000, 3000},
        new double[] {90, 20, 40}));
    testDatasets.add(new JUnitDataset("testcase_3",
        new double[] {1000, 2000, 3000, 4000},
        new double[] {90, 20, 40, 60}));
    testDatasets.add(new JUnitDataset("testcase_4",
        new double[] {1000, 2000, 3000, 4000, 5000},
        new double[] {90, 20, 40, 60, 100}));
  }

  public void testCalcMultiDomain() {
    for (JUnitDataset ds : testDatasets) {
      // For each dataset, test multiDomain content for each ConstructType.
      // Need to assert that the same multiDomain structure results from all
      // supported mutations as well as the immutable construction.
      for (ConstructType t : ConstructType.values()) {
        //log("Testing constructType " + t);
        MipMapChain mipmapChain = t.mipmap(mipmap, ds.domain, ds.range);
        verifyMultiDomain(ds, mipmapChain.getMipMappedDomain(), t);
      }
    }
  }
  
  public void testCalcMultiRange() {
    for (JUnitDataset ds : testDatasets) {
      for (ConstructType t : ConstructType.values()) {
        MipMapChain mipmapChain = t.mipmap(mipmap, ds.domain, ds.range);
        verifyMultiRange(ds, mipmapChain.getMipMappedRangeTuples()[0], t);
      }
    }
  }
  
  private void verifyMultiDomain(JUnitDataset ds, Array2D multiDomain, ConstructType t) {
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
  
  private void verifyMultiRange(JUnitDataset ds, Array2D multiRange, ConstructType t) {
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
          expectedValue = MathUtil.max(prevLevel[j * 2], prevLevel[j * 2 + 1]);
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
  
  private static String getTestCaseId(JUnitDataset ds, ConstructType ct) {
    return "[" + ds.id + " " + ct + "]";
  }
  
  private static void log(Object msg) {
    //System.out.println("> " + msg);
  }
}
