package org.timepedia.chronoscope.client.gss;

import org.timepedia.chronoscope.client.canvas.Color;

/**
 * Canned property object used for testing
 */
public class MockGssProperties extends GssProperties {
    public MockGssProperties() {
        this.bgColor = new Color("#404040");
        this.color = new Color("#000000");
        this.width = 1;
        this.fontFamily = "Verdana";
        this.fontSize = "10pt";
        this.fontWeight = "normal";
        this.lineThickness = 1;
        this.left = 0;
        this.top = 0;
        this.shadowBlur = 0;
        this.shadowColor = new Color("#000000");
        this.shadowOffsetX = 0;
        this.shadowOffsetY = 0;
        this.size = 1;
        this.transparency = 1;
        this.visible = true;

    }
}
