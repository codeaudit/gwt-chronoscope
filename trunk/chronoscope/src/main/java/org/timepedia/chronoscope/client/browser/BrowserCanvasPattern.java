package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.JavaScriptObject;
import org.timepedia.chronoscope.client.canvas.CanvasPattern;
import org.timepedia.chronoscope.client.canvas.PaintStyle;

/**
 * An implementation of CanvasPattern using Javascript CANVAS
 */
public class BrowserCanvasPattern implements CanvasPattern, PaintStyle {
    private final JavaScriptObject nativePattern;

    public BrowserCanvasPattern(BrowserLayer layer, String imageUri) {
        nativePattern = createNativePattern(this, layer.getContext(), imageUri, "repeat");


    }

    private native JavaScriptObject createNativePattern(BrowserCanvasPattern bcp, JavaScriptObject ctx, String uri,
                                                        String repeat) /*-{
         var img = new Image();
         img.onload = function()
         {
           var pat= ctx.createPattern(img, repeat);
           bcp.@org.timepedia.chronoscope.client.browser.BrowserCanvasPattern::nativePattern=pat;
         }
         img.src=uri;
    
         return null;
    }-*/;

    public JavaScriptObject getNative() {
        return nativePattern;
    }


}
