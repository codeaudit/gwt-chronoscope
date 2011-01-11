package org.timepedia.chronoscope.client.render;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.timepedia.chronoscope.client.render.DatasetLegendPanel.Item;

public class LeyendTest extends TestCase {
  String[] labels = new String[]{
      "4444", "22", "1", "333", "22", "333", "55555", "666666", "666666",
      "7777777", "999999999"};

  private List<Item> generateItems() {
    List<Item> items = new LinkedList<Item>();
    for (int i = 0; i < labels.length; i++) {
      items.add(new Item(0, (double) labels[i].length(), i, 0, labels[i]));
    }
    return items;
  }

  public void testDistributeLeyendItemsAlignedColums() {
    List<List<Item>> rows = DatasetLegendPanel.getLegendRows(generateItems(), 12, 0, DatasetLegendPanel.UNALIGNED_COLS);
    Assert.assertEquals(4, rows.size());
    Assert.assertEquals(2, rows.get(0).size());
    Assert.assertEquals(5, rows.get(3).size());
  }

  public void testDistributeLeyendUnlignedColums() {
    List<List<Item>> rows = DatasetLegendPanel.getLegendRows(generateItems(), 18, 0, DatasetLegendPanel.ALIGNED_COLS);
    Assert.assertEquals(4, rows.size());
    Assert.assertEquals(3, rows.get(0).size());

    rows = DatasetLegendPanel.getLegendRows(generateItems(), 15, 0, DatasetLegendPanel.ALIGNED_COLS);
    Assert.assertEquals(6, rows.size());

    rows = DatasetLegendPanel.getLegendRows(generateItems(), 12, 0, DatasetLegendPanel.ALIGNED_COLS);
    Assert.assertEquals(6, rows.size());
    Assert.assertEquals(2, rows.get(0).size());
    Assert.assertEquals(2, rows.get(1).size());
    Assert.assertEquals(2, rows.get(2).size());

    rows = DatasetLegendPanel.getLegendRows(generateItems(), 11, 0, DatasetLegendPanel.ALIGNED_COLS);
    Assert.assertEquals(8, rows.size());
  }

  public void testDistributeLeyendItemsFixedColums() {
    List<List<Item>> rows = DatasetLegendPanel.getLegendRows(generateItems(), 0, 0, 4);
    Assert.assertEquals(3, rows.size());
    Assert.assertEquals(4, rows.get(0).size());
    Assert.assertEquals(3, rows.get(2).size());
  }

  @SuppressWarnings("unused")
  private void printLeyendRows(List<List<Item>> rows) {
    for (List<Item> row : rows) {
      String s = "";
      for (Item i : row) {
        if (i != null) {
          s += i.label + "(" + i.len + ")";
        }
      }
      System.out.println(s);
    }
  }

  @SuppressWarnings("unused")
  private void printRow(List<Item> row) {
    String s = "";
    for (Item i : row) {
      s += i.label + " ";
    }
    System.out.println(s);
  }
}
