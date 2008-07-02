package org.timepedia.chronoscope.gviz.api.client;

import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.client.overlays.OverlayClickListener;
import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.data.ArrayXYDataset;

import java.util.ArrayList;
import java.util.Map;
import java.util.Arrays;

/**
 *
 */
public class DataTableParser {

    public static Marker[] parseMarkers(final DataTable table,
                                        Map<Integer, Integer> dataset2Column) {
        int startRow = -1;
        int curSeries = -1;
        ArrayList<Marker> markers = new ArrayList<Marker>();

        for (int row = 0; row < table.getNumberOfRows(); row++) {
            if (!Double.isNaN(table.getValueDate(row, 0))) {
                startRow = row;
                break;
            }
        }


        for (int i = 1; i < table.getNumberOfColumns(); i++) {

            if (!Double.isNaN(table.getValueNumber(startRow, i))) {
                curSeries++;
            } else {
                if ("markers".equalsIgnoreCase(table.getColumnLabel(i))) {
                    for (int row = startRow; row < table.getNumberOfRows(); row++) {
                        final Marker m = new Marker(table.getValueDate(row, 0), .0,
                                "" + (char) ('A' + markers.size()), curSeries);
                        final String info = table.getValueString(row, i);
                        final String info2 = info != null ? info.trim() : "";

                        if (!"".equals(info2)) {
                            m.addOverlayClickListener(new OverlayClickListener() {
                                public void onOverlayClick(Overlay overlay, int i, int i1) {
                                    m.openInfoWindow(info2);
                                    GVizEventHelper.trigger(table, GVizEventHelper.SELECT_EVENT, null);
                                }
                            });
                            markers.add(m);
                        }
                    }
                }
            }
        }
        return markers.toArray(new Marker[markers.size()]);
    }

    static class DataPair {
      public double domain[];
      public double range[];
    }
  
    public static XYDataset[] parseDatasets(DataTable table, Map<Integer, Integer> dataset2Column) {


        int startRow = -1;
        for (int row = 0; row < table.getNumberOfRows(); row++) {
            if (!Double.isNaN(table.getValueDate(row, 0))) {
                startRow = row;
                break;
            }
        }

        int numCols = 0;
        for (int i = 1; i < table.getNumberOfColumns(); i++) {
            if (!Double.isNaN(table.getValueNumber(startRow, i))) {
                numCols++;
            }
        }

        XYDataset[] ds = new XYDataset[numCols];
        numCols = 0;
        for (int i = 1; i < table.getNumberOfColumns(); i++) {
            if (Double.isNaN(table.getValueNumber(startRow, i))) {
                continue;
            }
            String label = table.getColumnLabel(i);
            if (label == null || "".equals(label)) {
                label = "Series " + numCols;
            }
            label = label.trim();
            int ind = label.indexOf("(");
            int end = label.indexOf(")");

            String units = label;
            if (ind != -1 && end != -1 && end > ind) {
                units = label.substring(ind + 1, end).trim();
                label = label.substring(0, ind);
            }

            
            DataPair pair = table2datapair(table, startRow, i);
            sortAscendingDate(pair);
            ds[numCols++] = new ArrayXYDataset("col" + i, pair.domain, pair.range, label,
                    units);
            if (dataset2Column != null) dataset2Column.put(numCols - 1, i);
        }

        return ds;
    }

  private static void sortAscendingDate(DataPair pair) {
    class Pair implements Comparable<Double> {
      public double x, y;

      public Pair(double x, double y) {
        this.x = x;
        this.y = y;
      }

      public int compareTo(Double o) {
        return (int) (this.x - o.doubleValue());
      }
    }
    
    Pair[] p = new Pair[pair.domain.length];
    for(int i=0; i<p.length; i++) 
      p[i]=new Pair(pair.domain[i], pair.range[i]);
    Arrays.sort(p);
    for(int i=0; i<p.length; i++) {
      pair.domain[i]=p[i].x;
      pair.range[i]=p[i].y;
    }
   
  }

    public static DataPair table2datapair(DataTable table, int startRow, int col) {
        DataPair pair=new DataPair();
      
        int rows=0;
        for (int i = startRow; i < table.getNumberOfRows(); i++) {
             double val=table.getValueNumber(i, col);
             if(!Double.isNaN(val)) rows++;
        }
        
        int row=0;
        pair.range = new double[rows];
        pair.domain = new double[rows];
      
        for (int i = startRow; i < table.getNumberOfRows(); i++) {
             double val=table.getValueNumber(i, col);
             if(!Double.isNaN(val)) {
               pair.range[row] = val;
               pair.domain[row] = table.getValueDate(i, 0);
               row++;
             }
        }
       
        return pair;
    }

    public static double[] table2domain(DataTable table, int startRow) {
        double d[] = new double[table.getNumberOfRows() - startRow];
        for (int i = startRow; i < table.getNumberOfRows(); i++) {
            d[i] = table.getValueDate(i, 0);
        }
        return d;
    }
}
