package org.timepedia.chronoscope.client.util;

import junit.framework.TestCase;

import org.timepedia.chronoscope.client.util.JavaArray2D;

/**
 * @author Chad Takahashi
 */
public class JavaArray2DTest extends TestCase {

  public void testDimensions() {
    double[][] data = new double[][] { {10, 20}, {30}};
    JavaArray2D a = new JavaArray2D(data);
    assertSameSize(data, a);
  }

  public void testIllegalConstructorCalls() {
    // null constructor arg
    try {
      new JavaArray2D(null);
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
