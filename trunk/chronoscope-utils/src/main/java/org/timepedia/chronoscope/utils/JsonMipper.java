package org.timepedia.chronoscope.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.timepedia.chronoscope.client.ChronoscopeComponentFactory;
import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.data.json.JsonArray;
import org.timepedia.chronoscope.client.data.json.JsonArrayNumber;
import org.timepedia.chronoscope.client.data.json.JsonArrayString;
import org.timepedia.chronoscope.client.data.json.JsonDataset;
import org.timepedia.chronoscope.client.io.DatasetReader;
import org.timepedia.chronoscope.client.util.DateFormatter;
import org.timepedia.chronoscope.client.util.date.DateFormatterFactory;

import com.google.gwt.i18n.client.TimeZone;

/**
 * Utility class to pre-mip a json data string.
 * 
 * @author manolo carrasco monino
 */
public class JsonMipper {
  private static Date d = new Date();

  public static class GwtJsonArrayNumber implements JsonArrayNumber {
    private JSONArray jsa;
    
    public GwtJsonArrayNumber (JSONArray jsa) {
      this.jsa = jsa;
    }
    
    public double get(int i) {
      return jsa.getDouble(i);
    }

    public int length() {
      return jsa.size();
    }
  }
  
  public static class GwtJsonArrays implements JsonArray<JsonArrayNumber> {
    private JSONArray jsa;
    
    public GwtJsonArrays (JSONArray jsa){
      this.jsa = jsa;
    }

    public JsonArrayNumber get(int i) {
      return new GwtJsonArrayNumber(jsa.getJSONArray(i));
    }

    public int length() {
      return jsa.size();
    }
  };
  
  public static class GwtJsonArrayString implements JsonArrayString {
    private JSONArray jsa;
    
    public GwtJsonArrayString (JSONArray jsa){
      this.jsa = jsa;
    }
    
    public String get(int i) {
      return jsa.getString(i);
    } 

    public int length() {
      return jsa.size();
    }
  }  
  
  public static class GwtJsonDataset implements JsonDataset {
    private JSONObject jso;

    public GwtJsonDataset(JSONObject jso) {
      this.jso = jso;
    }

    public String getAxisId() {
      return jso.getString("axis");
    }

    public String getDateTimeFormat() {
      return jso.has("dtformat") ? jso.getString("dtformat") : null;
    }

    public JsonArrayNumber getDomain() {
      return new GwtJsonArrayNumber(jso.getJSONArray("domain"));
    }

    public double getDomainScale() {
      return jso.has("domainscale") ? jso.getDouble("domainscale") : 1;
    }

    public JsonArrayString getDomainString() {
      return new GwtJsonArrayString(jso.getJSONArray("domain"));
    }

    public String getId() {
      return jso.has("id") ? jso.getString("id") : null;
    }

    public String getLabel() {
      return jso.has("label") ? jso.getString("label") : null;
    }

    public JsonArray<JsonArrayNumber> getMultiDomain() {
      return new GwtJsonArrays(jso.getJSONArray("domain"));   
    }

    public JsonArray<JsonArrayNumber> getMultiRange() {
      return new GwtJsonArrays(jso.getJSONArray("range"));   
    }

    public String getPartitionStrategy() {
      return jso.has("partitionStrategy") ? jso.getString("partitionStrategy") : "binary";
    }

    public String getPreferredRenderer() {
      return jso.has("preferredRenderer") ? jso.getString("preferredRenderer") : "line";
    }

    public JsonArrayNumber getRange() {
      return new GwtJsonArrayNumber(jso.getJSONArray("range"));
    }

    public double getRangeBottom() {
      return jso.has("rangeBotton") ? jso.getDouble("rangeBotton") : Double.NaN;
    }

    public double getRangeTop() {
      return jso.has("rangeTop") ? jso.getDouble("rangeTop") : Double.NaN;
    }

    public JsonArray<JsonArrayNumber> getTupleRange() {
      return jso.has("tupleRange") ? new GwtJsonArrays(jso.getJSONArray("tupleRange")) : null;   
    }

    public boolean hasRangeInformation() {
      return jso.has("rangeTop") ? jso.getBoolean("rangeTop") : false;
    }

    public boolean isMipped() {
      return jso.has("mipped") ? jso.getBoolean("mipped") : false;
    }
    
  }
  
  public static class JDKDateFormatter implements DateFormatter {

    private SimpleDateFormat fmt;

    public JDKDateFormatter(String format) {
        fmt = new SimpleDateFormat(format);
    }

    public String format(double timestamp) {
      d.setTime((long) timestamp);
      return fmt.format(d);
    }

    public String format(double timestamp, TimeZone timezone) {
      d.setTime((long) timestamp);
      return fmt.format(d); // FIXME: TZ
    }

    public double parse(String date) {
        try {
            return fmt.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
  }
  
  public static class JDKDateFormatterFactory extends DateFormatterFactory {
    public DateFormatter getDateFormatter(String format) {
      return new JDKDateFormatter(format);
    }
  }

  
  private static ChronoscopeComponentFactory componentFactory = new ChronoscopeComponentFactory();
  
  
  private static DatasetReader datasetReader = new DatasetReader(componentFactory);

  static {
    DateFormatterFactory.setDateFormatterFactory(new JDKDateFormatterFactory());
  }
  
  private static String readFileAsString(String file)
  throws java.io.IOException{
      StringBuffer fileData = new StringBuffer();
      BufferedReader reader = new BufferedReader(new FileReader(file));
      char[] buf = new char[4096];
      int read;
      while((read = reader.read(buf)) != -1){
          fileData.append(buf, 0, read);
      }
      reader.close();
      return fileData.toString().trim();
  }
  
  public static void main(String...args) throws Exception {
    JsonMipper mipper = new JsonMipper(); 
    for (String s: args) {
      System.out.println(mipper.mipJsonFile(s));
    }
  }
  
  public String mipJsonFile(String filename) throws IOException {
    return mipJsonString(readFileAsString(filename));
  }
  
  public String mipJsonString(String jsonTxt) {
    boolean multiple = jsonTxt.startsWith("[");
    String varName = null;
    if (jsonTxt.matches("(?s)^\\s*([\\w]+)\\s*=\\s*.*")) {
      varName = jsonTxt.replaceFirst("(?s)^\\s*([\\w]+)\\s*=\\s*.*", "$1");
    }
    if (varName != null) {
      jsonTxt = jsonTxt.replaceFirst("(?s)\\s*([\\w]+)\\s*=\\s*(.*?)\\s*\\;?\\s*$", "$2");
    }
    
    if (jsonTxt.startsWith("{")) {
      jsonTxt = "{datasets: [" + jsonTxt + "]}";
    } else if (jsonTxt.startsWith("[")) {
      jsonTxt = "{datasets: " + jsonTxt + "}";
    }
    
    JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonTxt); 
    JSONArray  sets = json.getJSONArray("datasets");
    
    Datasets dsets = new Datasets();
    for (int i=0; i < sets.size(); i++) {
      JSONObject set = sets.getJSONObject(i);
      GwtJsonDataset s = new GwtJsonDataset(set);
      Dataset d = datasetReader.createDatasetFromJson(s);
      dsets.add(d);
    }
    
    String mippedJson;
    if (multiple) {
      mippedJson = dsets.toJson();
    } else {
      mippedJson = dsets.get(0).toJson();
    }
    
    if (varName != null) {
      mippedJson = varName + " = " + mippedJson + ";";
    }
    
    return mippedJson;
  }
  
}
