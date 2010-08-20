package org.timepedia.chronoscope.client.util;


/**
 * A 2-dimensional array that can contain a variable number of column elements
 * for a given row.
 * 
 * @author Chad Takahashi
 */
public interface Array2D {

  /**
   * Adds a new row to this array by reference.
   */
  public void addRowByRef(double[] row);
  
  /**
   * Adds a new row to this array by value.
   */
  public void addRowByValue(double[] row);

  /**
   * Returns the value at the specified row and column
   */
  double get(int row, int column);
  
  Array1D getRow(int rowIndex);
  
  /**
   * Returns true only if the other {@link Array2D} object has the same
   * number of rows, as well as the same number of columns-per-row, as this
   * object.
   */
  boolean isSameSize(Array2D other);

  /**
   * Returns the number of columns in the specified row (rows can have differing
   * number of columns).
   */
  int numColumns(int rowIndex);

  /**
   * Returns the number of rows in this array
   */
  int numRows();

  /**
   * Assigns the value at the specified row and column
   */
  void set(int rowIdx, int colIdx, double value);

}
