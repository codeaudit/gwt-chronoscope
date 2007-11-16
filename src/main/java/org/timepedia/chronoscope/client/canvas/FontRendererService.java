package org.timepedia.chronoscope.client.canvas;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import org.timepedia.chronoscope.client.browser.Chronoscope;
import org.timepedia.chronoscope.client.browser.FontRendererServiceAsyncJson;
import org.timepedia.chronoscope.client.util.Util;

/**
 * Service which uses Java2D to compute font metrics of a series of glyphs for the given font and rotation,
 * as well as returning the URL of a fontbook rendering of the glyphs
 */
public interface FontRendererService extends RemoteService {
    RenderedFontMetrics getRenderedFontMetrics(String fontFamily, String fontWeight, String fontSize, String color,
                                               float angle);

    public static class FontRenderServiceUtil {
        private static FontRendererServiceAsync ourInstance = null;

        public static synchronized FontRendererServiceAsync getInstance() {
            if (ourInstance == null) {
                String endpoint = Chronoscope.getFontBookServiceEndpoint();
                if (Util.isSameDomain(GWT.getHostPageBaseURL(), endpoint)) {
                    ourInstance = (FontRendererServiceAsync) GWT.create(FontRendererService.class);
                    ((ServiceDefTarget) ourInstance).setServiceEntryPoint(endpoint);

                } else {
                   ourInstance = new FontRendererServiceAsyncJson(endpoint);
                }
            }
            return ourInstance;
        }
    }
}
