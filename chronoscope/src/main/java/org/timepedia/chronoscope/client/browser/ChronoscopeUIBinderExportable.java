package org.timepedia.chronoscope.client.browser;

import org.timepedia.exporter.client.Exportable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Window;
import org.timepedia.chronoscope.client.ChronoscopeOptions;
import org.timepedia.chronoscope.client.browser.json.JsonDatasetJSO;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.data.ArrayDataset2D;
import org.timepedia.chronoscope.client.data.IncrementalDatasetResponseImpl;
import org.timepedia.chronoscope.client.data.MutableDatasetND;
import org.timepedia.chronoscope.client.overlays.DomainBarMarker;
import org.timepedia.chronoscope.client.overlays.Marker;
import org.timepedia.chronoscope.client.overlays.RangeBarMarker;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;
import org.timepedia.chronoscope.client.render.DatasetRenderer;
import org.timepedia.chronoscope.client.render.LineXYRenderer;
import org.timepedia.chronoscope.client.render.domain.DateTickFormatterFactory;
import org.timepedia.chronoscope.client.render.domain.IntTickFormatterFactory;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exporter;
import org.timepedia.exporter.client.ExporterUtil;

@ExportPackage("chronoscope")
public class ChronoscopeUIBinderExportable implements Exportable {

    public void exportFunctions() {

        Exporter exporter = (Exporter) GWT.create(ChronoscopeUIBinderExportable.class);

        Exporter dexporter = GWT.create(ArrayDataset2D.class);

        Exporter exporterMarker = (Exporter) GWT.create(Marker.class);

        Exporter exporterRangeMarker = (Exporter) GWT.create(RangeBarMarker.class);

        Exporter exporterDomainMarker = (Exporter) GWT.create(DomainBarMarker.class);

        Exporter exporter2 = (Exporter) GWT.create(DefaultXYPlot.class);

        Exporter exporter5 = (Exporter) GWT.create(BrowserChronoscopeMenu.class);

        Exporter exporter7 = (Exporter) GWT.create(DatasetRenderer.class);

        Exporter exporter4 = (Exporter) GWT.create(LineXYRenderer.class);

        Exporter exporter8 = (Exporter) GWT.create(IntTickFormatterFactory.class);

        Exporter exporter9 = (Exporter) GWT.create(DateTickFormatterFactory.class);

        Exporter exporterInc = (Exporter) GWT.create(IncrementalDatasetResponseImpl.class);

        Exporter exporterMut = (Exporter) GWT.create(MutableDatasetND.class);

        View v = (View) GWT.create(DOMView.class);
        ((DOMView) v).exportFunctions();
    }

    /**
     * Import shim data
     */
    public JsArray<JsonDatasetJSO> onChronoscopeShimLoaded() {
        try {
            JavaScriptObject foo = ExporterUtil.wrap(this);
            return chronoscopeShimLoaded(foo);
        } catch (Exception e) {
            if (ChronoscopeOptions.isErrorReportingEnabled()) {
                Window.alert("Chronoscope Failed to Initialize because " + e);
            }
        }
        return null;
    }

    /**
     * Simulation data generated
     * @param chronoscope ( JavaScriptObject )
     * @return JsArray<JsonDatasetJSO>(  Id is SimulationLoaded)
     */
    private native JsArray<JsonDatasetJSO> chronoscopeShimLoaded(JavaScriptObject chronoscope) /*-{
     var data = [{id:"shim__","domain": [0,1],range: [0,0], "label": "", "axis": ""}]
     return data;
    }-*/;
}
