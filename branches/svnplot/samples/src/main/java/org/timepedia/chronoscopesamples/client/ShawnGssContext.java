package org.timepedia.chronoscopesamples.client;

import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.gss.*;

/**
 *
 */
public class ShawnGssContext extends DefaultGssContext {

    public GssProperties getProperties(GssElement gssElem, String pseudoElt) {


        if ("fill".equals(gssElem.getType())) {
            return new ShawnFillGssProperties(gssElem.getParentGssElement(), pseudoElt);
        }
        return super.getProperties(gssElem, pseudoElt);
    }


    private class ShawnFillGssProperties extends GssProperties {

        private ShawnFillGssProperties(GssElement parentGssElement, String pseudoElt) {
            if ("disabled".equals(pseudoElt)) {
                this.bgColor = new Color("rgba(0,0,0,0)");

            } else {
                String seriesNum = parentGssElement.getTypeClass();
                if (seriesNum.matches(".*\\bs\\d+\\b")) {
                    int ind = seriesNum.indexOf("s");
                    if (ind != -1) {
                        int snum = 0;
                        try {
                            snum = Integer.parseInt(seriesNum.substring(ind + 1).trim());
                        } catch (NumberFormatException e) {
                        }
                        this.bgColor = new Color(colors[Math.min(snum, colors.length - 1)]);

                    }
                } else {
                    this.bgColor = new Color("rgb(255,0,255)");
                }
                this.transparency = 0.3;
            }
        }
    }

}
