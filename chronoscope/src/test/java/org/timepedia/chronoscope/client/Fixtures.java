package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.data.ArrayXYDataset;
import org.timepedia.chronoscope.client.data.DateParser;

/**
 * Some fixture data for testing
 */
public class Fixtures {

  public static final String[] pre70s = {"1961", "1962", "1963", "1964",
      "1965"};

  public static final String[] post70s = {"1971", "1972", "1973", "1974",
      "1975"};

  public static final double[] rangeValuesAscending = {1.0, 2.0, 3.0, 4.0, 5.0};

  public static final double[] rangeValuesDescending = {5.0, 4.0, 3.0, 2.0,
      1.0};

  public static final String microformatData = "<table id=\"microformatdemo\" class=\"cmf-chart\">\n"
      + "    <colgroup>\n"
      + "      <col class=\"cmf-dateformat\" title=\"yyyy\">\n"
      + "    </colgroup>\n" + "    <thead>\n" + "        <tr>\n"
      + "            <th>Time</th>\n" + "            <th>GDP</th>\n"
      + "        </tr>\n" + "    </thead>\n" + "    <tbody>\n"
      + "        <tr>\n" + "            <td>1953</td>\n"
      + "            <td>1000</td>\n" + "        </tr>\n" + "        <tr>\n"
      + "            <td>1954</td>\n" + "            <td>3000</td>\n"
      + "        </tr>\n" + "        <tr>\n" + "            <td>1955</td>\n"
      + "            <td>3100</td>\n" + "        </tr>\n" + "        <tr>\n"
      + "            <td>1956</td>\n" + "            <td>3200</td>\n"
      + "        </tr>\n" + "        <tr>\n" + "            <td>1957</td>\n"
      + "            <td>3250</td>\n" + "        </tr>\n" + "        <tr>\n"
      + "            <td>1958</td>\n" + "            <td>3300</td>\n"
      + "        </tr>\n" + "        <tr>\n" + "            <td>1959</td>\n"
      + "            <td>3325</td>\n" + "        </tr>\n" + "        <tr>\n"
      + "            <td>1960</td>\n" + "            <td>1900</td>\n"
      + "        </tr>\n" + "        <tr>\n" + "            <td>1961</td>\n"
      + "            <td>1800</td>\n" + "        </tr>\n" + "        <tr>\n"
      + "            <td>1962</td>\n" + "            <td>2000</td>\n"
      + "        </tr>\n" + "        <tr>\n" + "            <td>1963</td>\n"
      + "            <td>2100</td>\n" + "        </tr>\n" + "        <tr>\n"
      + "            <td>1964</td>\n" + "            <td>2200</td>\n"
      + "        </tr>\n" + "    </tbody>\n" + "</table>";

  public static XYDataset getNegativeDomainAscendingRange() {
      return new ArrayXYDataset("test",
          parseDomain(pre70s), rangeValuesAscending, "test", "test");
    
  }

  public static XYDataset getPositiveDomainDescendingRange() {
       return new ArrayXYDataset("test",
           parseDomain(post70s), rangeValuesDescending, "test", "test");
    
   }

  
  private static double[] parseDomain(String[] dates) {
    double domain[] = new double[dates.length];
    for(int i=0; i<dates.length; i++) {
      domain[i]= DateParser.parse("yyyy", dates[i]);
    }
    return domain;
  }
}
