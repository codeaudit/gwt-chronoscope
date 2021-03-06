package org.timepedia.chronoscope.client.util;

import junit.framework.TestCase;

import org.timepedia.chronoscope.client.util.JavaArray2D;

/**
 * @author Chad Takahashi
 */
public class JavaArray2DTest extends TestCase {
  
  public void testDefaultConstructor() {
    assertEquals(0, new JavaArray2D().numRows());
    
    assertTrue(new JavaArray2D().isSameSize(new JavaArray2D()));
    
    try {
      new JavaArray2D().getRow(0);
      fail("Expected AssertionError");
    } catch (AssertionError e) {}
    
    JavaArray2D a = new JavaArray2D();
    a.set(0, 0, 100);
    a.set(2, 2, 300);
    assertEquals(100.0, a.get(0, 0));
    assertEquals(0, a.numColumns(1)); // row 1 has not been assigned anything
    assertEquals(300.0, a.get(2, 2));
    
    // Columns 0 and 1 not exlicitly assigned in this row; expects default value of 0.
    assertEquals(0.0, a.get(2, 0));
    assertEquals(0.0, a.get(2, 1));
  }
  
  public void testSingleRowConstructor() {
    double[] row = new double[] { 1, 3, 5};
    
    // make a copy of 'row' to use as expected array to guard against
    // case where JavaArray2D accidentally modifies the input array.
    double[] expectedRow = Util.copyArray(row);
    
    JavaArray2D a = new JavaArray2D(row);
    assertEquals(1, a.numRows());
    assertEquals(3, a.numColumns(0));
    for (int i = 0; i < expectedRow.length; i++) {
      assertEquals(expectedRow[i], a.get(0, i));
    }
  }
  
  public void testAddRow() {
    JavaArray2D a = new JavaArray2D();
    
    double[] row0 = new double[] {1, 2};
    double[] row1 = new double[] {3, 4};
    
    a.addRowByRef(row0);
    a.addRowByValue(row1);
    
    assertEquals(1.0, a.get(0, 0));
    assertEquals(2.0, a.get(0, 1));
    assertEquals(3.0, a.get(1, 0));
    assertEquals(4.0, a.get(1, 1));
    
    // Row0 was added by reference.  Verify that JavaArray2D actually points to row0
    row0[0] = 999.0;
    assertEquals(999.0, a.get(0, 0));
    
    // row1 was added by value.  Verify that JavaArray2D is not affected by changes
    // to row1's state.
    row1[0] = 999.0;
    assertEquals(3.0, a.get(1, 0));
    
    // Verify that access to the array elemts via the Array1D interface
    // works as expected
    for (int i = 0; i < a.numRows(); i++) {
      for (int j = 0; j < a.numColumns(i); j++) {
        assertEquals(a.get(i, j), a.getRow(i).get(j));
      }
    }
  }
  
  public void testDimensions() {
    double[][] data = new double[][] { {10, 20}, {30}};
    JavaArray2D a = new JavaArray2D(data);
    assertSameSize(data, a);
  }

  public void testIllegalConstructorCalls() {
    // null constructor arg
    try {
      new JavaArray2D((double[])null); // 1D array constructor
      fail("Expected IllegalArgumentException");
    }
    catch (IllegalArgumentException e) {}

    // null constructor arg
    try {
      new JavaArray2D((double[][])null); // 2D array constructor
      fail("Expected IllegalArgumentException");
    }
    catch (IllegalArgumentException e) {}

    // a[][] with 0 rows
    try {
      new JavaArray2D(new double[0][1]);
      fail("Expected IllegalArgumentException");
    }
    catch (IllegalArgumentException e) {}

    // a[][] with 1 or more null rows
    try {
      // 2nd row is null
      new JavaArray2D(new double[][] {{1, 2}, null});
      fail("Expected IllegalArgumentException");
    }
    catch (IllegalArgumentException e) {}
  }
  
  public void testGet() {
    double[][] data = new double[][] { {10, 20, 30, 40}, {100, 200}, {300}};

    // Access every legal element in the data[][] array and verify
    // that Array2D.get(row,col) returns the corresponding value.
    JavaArray2D a = new JavaArray2D(data);
    for (int i = 0; i < data.length; i++) {
      double[] row = data[i];
      for (int j = 0; j < row.length; j++) {
        assertEquals(row[j], a.get(i, j));
      }
    }

    try {
      a.get(-1, -1);
      fail();
    } catch (AssertionError e) {
      // expected
    }

    try {
      a.get(0, data[0].length);
      fail();
    } catch (AssertionError e) {
      // expected
    }

    try {
      a.get(data.length, 0);
      fail();
    } catch (AssertionError e) {
      // expected
    }

  }

  public void testGetRow() {
    double[][] data = new double[][] { {10, 20, 30, 40}, {100, 200}, {300}};
    JavaArray2D a = new JavaArray2D(data);
    
    for (int r = 0; r < data.length; r++) {
      Array1D row = a.getRow(r);
      assertEquals(data[r].length, row.size());
      for (int i = 0; i < row.size(); i++) {
        assertEquals(data[r][i], row.get(i));
      }
    }
  }

  public void testSet() {
    double[][] data = new double[][] { {10, 20}, {30}};

    JavaArray2D a = new JavaArray2D(copy(data));
    for (int i = 0; i < data.length; i++) {
      double[] row = data[i];
      for (int j = 0; j < row.length; j++) {
        double newValue = row[j] + 1.0;
        a.set(i, j, newValue);
        assertEquals(newValue, a.get(i, j));
      }
    }

    // dimensions should not have changed
    assertSameSize(data, a);
  }

  public void testGetIllegalIndex() {
    double[][] data = new double[][] { {10, 20}, {30, 40}};

    JavaArray2D a2d = new JavaArray2D(data);
    a2d.set(4, 4, 999); // force capacity growth

    try {
      // This should fail, since '5' is greater than the largest index specified
      a2d.get(4, 5);
      fail("Expected AssertionError");
    } catch (AssertionError e) {}
  }

  public void testSet_forceGrow() {
    // Original data has 2 rows: row 0 has 2 columns, row 1 has 1 column.
    double[][] data = new double[][] { {10, 20}, {30}};

    JavaArray2D a2d = new JavaArray2D(data);

    // set a cell that's bigger than the original size of the backing array
    final int newRowIdx = 5;
    final int newColIdx = 5;
    a2d.set(newRowIdx, newColIdx, 100.0);
    assertEquals(100.0, a2d.get(newRowIdx, newColIdx));
    
    // Original data should still be intact...
    assertEquals(10.0, a2d.get(0, 0));
    assertEquals(20.0, a2d.get(0, 1));
    assertEquals(30.0, a2d.get(1, 0));
    assertEquals(10.0, a2d.get(0, 0));
    
    // Verify new dimensions
    assertEquals(newRowIdx + 1, a2d.numRows());
    assertEquals(newColIdx + 1, a2d.numColumns(newRowIdx));
  }

  private static double[][] copy(double[][] a) {
    double[][] copy = new double[a.length][];
    for (int i = 0; i < a.length; i++) {
      copy[i] = new double[a[i].length];
      System.arraycopy(a[i], 0, copy[i], 0, a[i].length);
    }

    return copy;
  }

  private void assertSameSize(double[][] a1, JavaArray2D a2) {
    assertEquals(a1.length, a2.numRows());
    for (int i = 0; i < a1.length; i++) {
      assertEquals(a1[i].length, a2.numColumns(i));
    }
  }
  
}
