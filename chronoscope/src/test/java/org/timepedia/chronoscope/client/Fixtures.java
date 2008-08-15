package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.data.DefaultXYDatasetFactory;
import org.timepedia.chronoscope.client.data.XYDatasetFactory;
import org.timepedia.chronoscope.client.data.XYDatasetRequest;

/**
 * Some fixture data for testing
 */
public class Fixtures {
  
    private static final XYDatasetFactory dsFactory = new DefaultXYDatasetFactory();
    
    public static final String[] pre70s = {"1961", "1962", "1963", "1964", "1965"};

    public static final String[] post70s = {"1971", "1972", "1973", "1974", "1975"};

    public static final double[] rangeValuesAscending = {1.0, 2.0, 3.0, 4.0, 5.0};

    public static final double[] rangeValuesDescending = {5.0, 4.0, 3.0, 2.0, 1.0};

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
      XYDatasetRequest.Basic request = new XYDatasetRequest.Basic();
      request.setDomain(parseDomain(pre70s));
      request.setRange(rangeValuesAscending);
      request.setIdentifier("test");
      request.setAxisId("test");
      request.setLabel("test");
      return dsFactory.create(request);
    }

    public static XYDataset getPositiveDomainDescendingRange() {
      XYDatasetRequest.Basic request = new XYDatasetRequest.Basic();
      request.setDomain(parseDomain(post70s));
      request.setRange(rangeValuesDescending);
      request.setIdentifier("test");
      request.setAxisId("test");
      request.setLabel("test");
      return dsFactory.create(request);
    }

    private static double[] parseDomain(String[] dates) {
        double domain[] = new double[dates.length];
        for (int i = 0; i < dates.length; i++) {
            domain[i] = MockDateParser.parse("yyyy", dates[i]);
        }
        return domain;
    }

    public static DataShape[] getDataShapes () {
        DataShape shapes[] = new DataShape[12];
        shapes[0] = new DataShape(0, 1.04);
        shapes[1] = new DataShape(-34545435345.324234, 1239809892.34423423);
        shapes[2] = new DataShape(23, 98.6);
        shapes[3] = new DataShape(1.88, 9.84);
        shapes[4] = new DataShape(1.96, 3.97);
        shapes[5] = new DataShape(-.720006, 1.0005);
        shapes[6] = new DataShape(-1.49, .99998);
        shapes[7] = new DataShape(-1.49, -1.0008);
        shapes[8] = new DataShape(-.84, -.23);
        shapes[9] = new DataShape(-.0033, .0065);
        shapes[10] = new DataShape(1001, 2222);
        shapes[11] = new DataShape(-2938998239.3444, -923898.9894);

        return shapes;
    }

  public static XYDataset[] getTestDataset() {
    return new XYDataset[] { getPositiveDomainDescendingRange() };
  }
}
