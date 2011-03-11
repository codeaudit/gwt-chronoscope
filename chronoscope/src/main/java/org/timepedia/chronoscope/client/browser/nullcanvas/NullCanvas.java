package org.timepedia.chronoscope.client.browser.nullcanvas;

import org.timepedia.chronoscope.client.canvas.*;

public class NullCanvas extends Canvas {

    public NullCanvas(View view) {
        super(view);
    }

    public void dispose() {
        // ...
    }

    @Override
    public Layer createLayer(String layerId, Bounds bounds) {
        return null;
    }

    @Override
    public void disposeLayer(String layerId) {

    }

    @Override
    public Layer getLayer(String layerId) {
        return null;
    }

    @Override
    public Layer getRootLayer() {
        return null;
    }

    @Override
    public CanvasImage createImage(String url) {
        return null;
    }
}
