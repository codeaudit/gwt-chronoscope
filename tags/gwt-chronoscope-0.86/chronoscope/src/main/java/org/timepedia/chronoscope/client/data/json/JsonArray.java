package org.timepedia.chronoscope.client.data.json;

/**
 * Interface modeling a Json array containing arbtirary JsonObjects.
 */
public interface JsonArray<T extends JsonObject> {
  T get(int i);
  int length();
}