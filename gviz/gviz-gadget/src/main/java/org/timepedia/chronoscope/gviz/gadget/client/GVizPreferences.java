package org.timepedia.chronoscope.gviz.gadget.client;

import com.google.gwt.gadgets.client.BooleanPreference;
import com.google.gwt.gadgets.client.EnumPreference;
import com.google.gwt.gadgets.client.StringPreference;
import com.google.gwt.gadgets.client.UserPreferences;

/**
 *
 */
public interface GVizPreferences extends UserPreferences {

  

//  @PreferenceAttributes(display_name = "Publish data to Timepedia.org",
//      default_value = "false", options = PreferenceAttributes.Options.NORMAL)
//  BooleanPreference allowSharing();

  
  
  @PreferenceAttributes(display_name = "Query URL",
      options = PreferenceAttributes.Options.REQUIRED)
  StringPreference _table_query_url();

 
  public static enum RefreshInterval {

    @EnumPreference.EnumDisplayValue("Do not refresh")
    ZERO(0),

    @EnumPreference.EnumDisplayValue("1")
    ONEMINUTE(60),

    @EnumPreference.EnumDisplayValue("5")
    FIVEMINUTES(5 * 60),

    @EnumPreference.EnumDisplayValue("30")
    HALFHOUR(30 * 60);

    private final int interval;

    private RefreshInterval(int interval) {
      this.interval = interval;
    }

   

    public int getInterval() {
      return interval;
    }
  }
  
   public static enum Style {

    @EnumPreference.EnumDisplayValue("Clean")
    CLEAN,

    @EnumPreference.EnumDisplayValue("Blue Gradient")
    BLUEGRADIENT,

    @EnumPreference.EnumDisplayValue("Google Finance")
    GFINANCE;

    
  }

  @PreferenceAttributes(display_name="Chart Title", default_value="Chronoscope")
  StringPreference chartTitle();
  
  @PreferenceAttributes(display_name = "Refresh Interval", default_value="ZERO", options = PreferenceAttributes.Options.NORMAL)
  EnumPreference<RefreshInterval> _table_query_refresh_interval();

//  @PreferenceAttributes(display_name = "Display Style", default_value="CLEAN", options = PreferenceAttributes.Options.NORMAL)
//  EnumPreference<Style> chartStyle();
  
//  @PreferenceAttributes(display_name = "Same Units share Same Axis?", default_value="true", options = PreferenceAttributes.Options.NORMAL)
//  BooleanPreference sameUnitsShareSameAxis();
  
//  @PreferenceAttributes(display_name = "Axis Labels?", default_value="true", options = PreferenceAttributes.Options.NORMAL)
//  BooleanPreference axisLabels();
  
  @PreferenceAttributes(display_name = "Overview enabled?", default_value="true", options = PreferenceAttributes.Options.NORMAL)
  BooleanPreference overviewEnabled();
  
  @PreferenceAttributes(display_name = "Legend enabled?", default_value="true", options = PreferenceAttributes.Options.NORMAL)
  BooleanPreference legendEnabled();
  
  
}
