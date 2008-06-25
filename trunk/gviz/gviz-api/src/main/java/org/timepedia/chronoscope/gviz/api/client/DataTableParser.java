package org.timepedia.chronoscope.gviz.api.client;

import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.client.overlays.OverlayClickListener;
import org.timepedia.chronoscope.client.Overlay;
import org.timepedia.chronoscope.client.XYDataset;
import org.timepedia.chronoscope.client.data.ArrayXYDataset;

import java.util.ArrayList;
import java.util.Map;

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

    public static XYDataset[] parseDatasets(DataTable table, Map<Integer, Integer> dataset2Column) {


        int startRow = -1;
        for (int row = 0; row < table.getNumberOfRows(); row++) {
            if (!Double.isNaN(table.getValueDate(row, 0))) {
                startRow = row;
                break;
            }
        }

        double domain[] = table2domain(table, startRow);
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

            double range[] = table2range(table, startRow, i);
            ds[numCols++] = new ArrayXYDataset("col" + i, domain, range, label,
                    units);
            if (dataset2Column != null) dataset2Column.put(numCols - 1, i);
        }

        return ds;
    }

    public static double[] table2range(DataTable table, int startRow, int col) {
        double r[] = new double[table.getNumberOfRows() - startRow];
        for (int i = startRow; i < r.length; i++) {
            r[i] = table.getValueNumber(i, col);
        }
        return r;
    }

    public static double[] table2domain(DataTable table, int startRow) {
        double d[] = new double[table.getNumberOfRows() - startRow];
        for (int i = startRow; i < d.length; i++) {
            d[i] = table.getValueDate(i, 0);
        }
        return d;
    }
}
