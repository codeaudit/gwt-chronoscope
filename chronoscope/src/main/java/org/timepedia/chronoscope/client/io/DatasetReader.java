package org.timepedia.chronoscope.client.io;

import com.google.gwt.user.client.Window;

import org.timepedia.chronoscope.client.browser.JsArrayParser;
import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.ComponentFactory;
import org.timepedia.chronoscope.client.ChronoscopeOptions;
import org.timepedia.chronoscope.client.util.Array2D;
import org.timepedia.chronoscope.client.util.JavaArray2D;
import org.timepedia.chronoscope.client.util.ArgChecker;
import org.timepedia.chronoscope.client.data.json.JsonDataset;
import org.timepedia.chronoscope.client.data.json.JsonArrayNumber;
import org.timepedia.chronoscope.client.data.json.JsonArray;
import org.timepedia.chronoscope.client.data.DatasetRequest;

/**
 * Various utility methods for parsing and validating JSON format.
 */
public class DatasetReader {

  public static JsArrayParser jsArrayParser = new JsArrayParser();

  /**
   * Parse a JSON object representing a multiresolution dataset into a class
   * implementing the {@link org.timepedia.chronoscope.client.Dataset} interface. <p> The JSON format is as
   * follows:
   * <pre>
   * dataset = {
   *    id: "unique id for this dataset",
   *    mipped: true,
   *    domain: [ [level 0 values], [level 1 values], ... ],
   *    range: [ [level 0 values], [level 1 values], ... ],
   *    rangeBottom: min over level 0 values,
   *    rangeTop: max over level 0 values,
   *    label: "default label for this dataset",
   *    axis: "an axis identifier (usually units). Datasets with like axis ids
   * share the same range Axis"
   * }
   * </pre>
   */
  public static Dataset createDatasetFromJson(JsonDataset json) {
    validateJSON(json);

    DatasetRequest request;
    if (json.isMipped()) {
      request = buildPreMipmappedDatasetRequest(json);
    } else {
      request = buildDatasetRequest(json);
    }

    // Properties common to basic and multires datasets
    request.setIdentifier(json.getId());
    request.setLabel(json.getLabel());
    request.setAxisId(json.getAxisId());
    request.setPreferredRenderer(json.getPreferredRenderer());
    final double minInterval = json.getMinInterval();
    if (minInterval > 0) {
      request.setApproximateMinimumInterval(minInterval);
    }

    return ComponentFactory.get().getDatasetFactory().create(request);
  }

  public static Array2D createArray2D(double[][] a) {
    return new JavaArray2D(a);
  }

  public static DatasetRequest buildDatasetRequest(JsonDataset json) {
    DatasetRequest.Basic request = new DatasetRequest.Basic();
    final String dtformat = json.getDateTimeFormat();

    request.setDefaultMipMapStrategy(
        ComponentFactory.get().getMipMapStrategy(json.getPartitionStrategy()));

    double[] domainArray = null;
    if (dtformat != null) {
      domainArray = jsArrayParser
          .parseFromDate(json.getDomainString(), dtformat);
    } else {
      domainArray = jsArrayParser
          .parse(json.getDomain(), json.getDomainScale());
    }
    request.addTupleSlice(domainArray);

    JsonArray<JsonArrayNumber> tupleRange = json.getTupleRange();
    if (tupleRange != null) {
      for (int i = 0; i < tupleRange.length(); i++) {
        request.addTupleSlice(jsArrayParser.parse(tupleRange.get(i)));
      }
    } else {
      request.addTupleSlice(jsArrayParser.parse(json.getRange()));
    }

    return request;
  }

  public static DatasetRequest buildPreMipmappedDatasetRequest(
      JsonDataset json) {
    DatasetRequest.MultiRes request = new DatasetRequest.MultiRes();

    JsonArray<JsonArrayNumber> mdomain = json.getMultiDomain();
    JsonArray<JsonArrayNumber> mrange = json.getMultiRange();

    int dmipLevels = mdomain.length();
    int rmiplevel = mrange.length();
    if (dmipLevels != rmiplevel) {
      if (ChronoscopeOptions.isErrorReportingEnabled()) {
       throw new RuntimeException("Domain and Range dataset levels are not equal");
      }
    }

    double domains[][] = new double[dmipLevels][];
    double ranges[][] = new double[dmipLevels][];
    double domainScale = json.getDomainScale();
    for (int i = 0; i < dmipLevels; i++) {
      domains[i] = jsArrayParser.parse(mdomain.get(i), domainScale);
      ranges[i] = jsArrayParser.parse(mrange.get(i));
    }

    DatasetRequest.MultiRes mippedRequest = (DatasetRequest.MultiRes) request;
    request.setRangeTop(json.getRangeTop());
    request.setRangeBottom(json.getRangeBottom());
    mippedRequest.addMultiresTupleSlice(createArray2D(domains));
    mippedRequest.addMultiresTupleSlice(createArray2D(ranges));

    return request;
  }

  public static void validateJSON(JsonDataset jsonDataset) {
    ArgChecker.isNotNull(jsonDataset, "jsonDataset");
    if (jsonDataset.isMipped() && jsonDataset.getDateTimeFormat() != null) {
      throw new IllegalArgumentException(
          "dtformat and mipped cannot be used together in dataset with id "
              + jsonDataset.getId());
    }
  }
}
