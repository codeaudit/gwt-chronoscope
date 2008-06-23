package org.timepedia.chronoscope.gviz.api.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 */
public final class DataTable extends JavaScriptObject {

  protected DataTable() {
  }

  public static DataTable as(JavaScriptObject data) {
    return data.cast();
  }
  
  public native int getNumberOfRows() /*-{
    return this.getNumberOfRows();
  }-*/;

  public native int getNumberOfColumns() /*-{
    return this.getNumberOfColumns();
  }-*/;
  
 public native String getValueString(int row, int column) /*-{
    return this.getValue(row, column);
  }-*/; 
  
  public native double getValueDate(int row, int column) /*-{
    var d=this.getValue(row, column);
    return d ? d.getTime() : 1/0;
  }-*/; 
  
  public native double getValueNumber(int row, int column) /*-{
    var n=this.getValue(row, column);
    var num = parseFloat(n);
    return num;
  }-*/; 
  
  public native String getColumnType(int column) /*-{
    return this.getColumnType(column);
  }-*/;

  public native String getColumnLabel(int col) /*-{
    return this.getColumnLabel(col);
  }-*/;
}
