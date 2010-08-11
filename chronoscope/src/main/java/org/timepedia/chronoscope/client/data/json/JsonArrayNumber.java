package org.timepedia.chronoscope.client.data.json;

/**
 * Interface modeling a Json array containing only numbers.
 */
public interface JsonArrayNumber extends JsonObject {
  double get(int i);
  int length();
}
