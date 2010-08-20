package org.timepedia.chronoscope.client.data.json;

/**
 * Interface modeling a Json array containing only strings.
 */
public interface JsonArrayString extends JsonObject {
  String get(int i);
  int length();
}