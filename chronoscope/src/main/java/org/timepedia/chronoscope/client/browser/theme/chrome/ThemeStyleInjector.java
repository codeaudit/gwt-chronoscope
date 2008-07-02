/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.timepedia.chronoscope.client.browser.theme.chrome;
import com.google.gwt.core.client.GWT;
import com.google.gwt.libideas.client.StyleInjector;
import com.google.gwt.libideas.resources.client.CssResource;
import com.google.gwt.libideas.resources.client.DataResource;
import com.google.gwt.libideas.resources.client.ImmutableResourceBundle;
import com.google.gwt.libideas.resources.client.ImageResource;

import org.timepedia.chronoscope.client.browser.theme.Theme;
import static org.timepedia.chronoscope.client.browser.theme.Theme.*;

public class ThemeStyleInjector  {

  /**
   * Resources used.
   */
  public interface Resources extends ImmutableResourceBundle {
    Resources INSTANCE = GWT.create(Resources.class);

    @Resource("corner.png")
    DataResource corner();

    @Resource("hborder.png")
    DataResource hborder();
    
    @Resource("vborder.png")
    DataResource vborder();
    
    @Resource("chrome.css")
    CssResource css();
    
   
   
  }

  /**
   * This is the worlds simplest.
   * @param theme
   */
  public static void injectTheme(Theme theme) {
    switch(theme) {
      case DARK:
      case STANDARD:
      case CHROME:
      default:
      StyleInjector.injectStylesheet(Resources.INSTANCE.css().getText());
    }
  }
}
