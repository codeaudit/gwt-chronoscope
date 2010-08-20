package org.timepedia.chronoscope.gviz.gadget.client;

import com.google.gwt.gadgets.client.EnumPreference;
import org.timepedia.chronoscope.client.gss.GssContext;
import org.timepedia.chronoscope.client.gss.DefaultGssContext;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.render.LinearGradient;

/**
 *
 */
public enum GVizStyle {

    @EnumPreference.EnumDisplayValue("Clean")
    CLEAN {
        public GssContext getGssContext() {
            return new DefaultGssContext();
        }
    },

    @EnumPreference.EnumDisplayValue("Blue Gradient")
    BLUEGRADIENT {
        public GssContext getGssContext() {
            return new FilledDefaultGssContext() {
                public GssProperties getProperties(GssElement gssElem, String pseudoElt) {

                    if ("plot".equals(gssElem.getType())) {
                        GssProperties props = super.getProperties(gssElem, pseudoElt);
                        Layer plotLayer = this.getView().getChart().getPlot().getPlotLayer();
                        LinearGradient gradient = plotLayer.createLinearGradient(0, 0, 1, 1);
                        gradient.addColorStop(0, "#00ABEb");
                        gradient.addColorStop(1, "#FFFFFF");
                        props.bgColor = gradient;
                        return props;
                    }
                    return super.getProperties(gssElem, pseudoElt);
                }
            };
        }
    },

    @EnumPreference.EnumDisplayValue("Google Finance")
    GFINANCE {
        public GssContext getGssContext() {
            return new FilledDefaultGssContext();
        }
    };

    public abstract GssContext getGssContext
            ();

    private static class FilledDefaultGssContext extends DefaultGssContext {
        public GssProperties getProperties(GssElement gssElem, String pseudoElt) {

            if ("fill".equals(gssElem.getType())) {
                GssProperties props = super.getProperties(gssElem, pseudoElt);
                if (isDisabled(pseudoElt)) {
                    props.bgColor = Color.TRANSPARENT;

                } else {
                    props.bgColor = datasetColorMap.get(gssElem.getParentGssElement());
                    props.transparency = 0.3;
                }
                return props;
            }
            return super.getProperties(gssElem, pseudoElt);
        }
    }
}
