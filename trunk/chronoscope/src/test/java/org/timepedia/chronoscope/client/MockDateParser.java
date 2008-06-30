package org.timepedia.chronoscope.client;


import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 *
 */
public class MockDateParser {
   public static double parse(String fmt, String dateString) {
    if(fmt == null) return Date.parse(dateString);
    SimpleDateFormat dtf = new SimpleDateFormat(fmt);
    Date date = new Date(70, 0, 1, 0, 0 ,0);
     try {
       return dtf.parse(dateString).getTime();
     } catch (ParseException e) {
       return date.getTime();
     }
   }
}
