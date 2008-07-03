package org.timepedia.chronoscope.gviz.api.client;

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
public enum GVizAPIStyle {

    CLEAN {
        public GssContext getGssContext() {
            return new DefaultGssContext();
        }
    },

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
                if ("disabled".equals(pseudoElt)) {
                    props.bgColor = new Color("rgba(0,0,0,0)");

                } else {
                    String seriesNum = gssElem.getParentGssElement().getTypeClass();
                    if (seriesNum.matches(".*\\bs\\d+\\b")) {
                        int ind = seriesNum.indexOf("s");
                        if (ind != -1) {
                            int snum = 0;
                            try {
                                snum = Integer.parseInt(seriesNum.substring(ind + 1).trim());
                            } catch (NumberFormatException e) {
                            }
                            props.bgColor = new Color(colors[Math.min(snum, colors.length - 1)]);

                        }
                    } else {
                        props.bgColor = new Color("rgb(255,0,255)");
                    }
                    props.transparency = 0.3;
                }
                return props;
            }
            return super.getProperties(gssElem, pseudoElt);
        }
    }
}