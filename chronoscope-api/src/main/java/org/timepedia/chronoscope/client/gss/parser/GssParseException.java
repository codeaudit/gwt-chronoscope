package org.timepedia.chronoscope.client.gss.parser;

/**
 */
public class GssParseException extends Exception {

  public GssParseException(String parseError) {
    super(parseError);
  }

  public GssParseException(String parseError, Throwable cause) {
    super(parseError, cause);
  }
}
