package org.timepedia.chronoscope.client.util;


/**
 * Implementation of {@link Array2D} backed by a Java 2-dimensional array,
 * and is growable.
 * 
 * @author Chad Takahashi
 */
public final class JavaArray2D implements Array2D {
  private static final double GROWTH_FACTOR = 2;

  private static int newCapacity(int currLength, int requestedIndex) {
    double newLength = (double) Math.max(currLength, 1);
    while ((double) requestedIndex >= newLength) {
      newLength *= GROWTH_FACTOR;
    }
    return (int) newLength;
  }

  private double[][] a;
  private int[] columnCounts;
  private int rowCount;

  /**
   * Constructs an {@link JavaArray2D} object from the specified Java
   * 2-dimensional array.
   */
  public JavaArray2D(double[][] a) {
    // Validate a[][]:
    ArgChecker.isNotNull(a, "a");
    // array must have at least 1 row
    if (a.length == 0) {
      throw new IllegalArgumentException("a.length must be > 0");
    }
    // All rows must be non-null
    for (int i = 0; i < a.length; i++) {
      if (a[i] == null) {
        throw new IllegalArgumentException("a[" + i + "] was null");
      }
    }

    rowCount = a.length;
    columnCounts = new int[rowCount];

    for (int i = 0; i < rowCount; i++) {
      columnCounts[i] = a[i].length;
    }

    this.a = a;
  }

  /**
   * Returns the value at the specified row and column
   */
  public double get(int row, int column) {
    assert MathUtil.isBounded(row, 0, rowCount - 1) 
      : "row out of bounds: " + row;
    assert MathUtil.isBounded(column, 0, numColumns(row) - 1)
      : "column out of bounds: " + column;
    
    // gwt-chronoscope Issue #87 
    // (http://code.google.com/p/gwt-chronoscope/issues/detail?id=87)
    // Profiling revealed this method as exremely hot.  So switched to
    // using assertion, which can be compiled out in hosted and web mode.
    // The ChartBench.java test app, when running in web mode, indicated
    // a significant performance increase (21 FPS to 27 FPS).
    /*
    if (!MathUtil.isBounded(row, 0, rowCount - 1)) {
      throw new ArrayIndexOutOfBoundsException(row);
    }

    if (!MathUtil.isBounded(column, 0, numColumns(row) - 1)) {
      throw new ArrayIndexOutOfBoundsException(column);
    }
    */
    
    return a[row][column];
  }
  
  public Array1D getRow(int row) {
    assert MathUtil.isBounded(row, 0, rowCount - 1) 
      : "row out of bounds: " + row;
    
    // TODO: Cache these Array1D objects rather than creating a new one on each
    // invocation of getRow().
    return new Array1DImpl(a, row, columnCounts);
  }
  

  public boolean isSameSize(Array2D other) {
    ArgChecker.isNotNull(other, "other");
    if (numRows() != other.numRows()) {
      return false;
    }

    for (int i = 0; i < numRows(); i++) {
      if (numColumns(i) != other.numColumns(i)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Returns the number of columns in the specified row (rows can have differing
   * number of columns).
   */
  public int numColumns(int rowIndex) {
    return columnCounts[rowIndex];
  }

  /**
   * Returns the number of rows in this array
   */
  public int numRows() {
    return rowCount;
  }

  /**
   * Assigns the value at the specified row and column.
   */
  public void set(int rowIdx, int colIdx, double value) {
    int rowCapacity = a.length;
    boolean needMoreRowCapacity = (rowIdx >= rowCapacity);

    if (needMoreRowCapacity) {
      int newRowCapacity = newCapacity(rowCapacity, rowIdx);
      double[][] newA = new double[newRowCapacity][];
      for (int i = 0; i < rowCount; i++) {
        newA[i] = a[i];
      }
      for (int i = rowCount; i < newRowCapacity; i++) {
        newA[i] = new double[0];
      }
      a = newA;
      int[] newColumnCounts = new int[newRowCapacity];
      System.arraycopy(columnCounts, 0, newColumnCounts, 0, rowCapacity);
      columnCounts = newColumnCounts;
    }

    rowCount = Math.max(rowCount, rowIdx + 1);

    int colCapacity = a[rowIdx].length;
    boolean needMoreColumnCapacity = (colIdx >= colCapacity);
    if (needMoreColumnCapacity) {
      int newColCapacity = newCapacity(colCapacity, colIdx);
      double[] newRow = new double[newColCapacity];
      double[] row = a[rowIdx];
      System.arraycopy(row, 0, newRow, 0, row.length);
      a[rowIdx] = newRow;
    }

    columnCounts[rowIdx] = Math.max(columnCounts[rowIdx], colIdx + 1);

    a[rowIdx][colIdx] = value;
  }
  
  private static final class Array1DImpl implements Array1D {
    private double[] data;
    private int row;
    private int[] columnCounts;
    
    public Array1DImpl(double[][] data2d, int row, int[] columnCounts) {
      this.data = data2d[row];
      this.row = row;
      this.columnCounts = columnCounts;
    }
    
    public double get(int index) {
      assert (index < this.columnCounts[row]) 
          : "index out of bounds: " + index;
      
      return this.data[index];
    }
    
    public double getLast() {
      int arraySize = columnCounts[row];
      if (arraySize > 0) {
        return this.data[arraySize - 1];
      }
      else {
        throw new IllegalStateException("array is empty");
      }
    }
    
    public int size() {
      return this.columnCounts[row];
    }

    public void execFunction(ArrayFunction f) {
      f.exec(data, columnCounts[row]);
    }
    
  }
}
