package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import org.timepedia.chronoscope.client.ChronoscopeOptions;
import org.timepedia.chronoscope.client.browser.ChronoscopeUIBinderWidget;
import org.timepedia.chronoscope.client.browser.json.JsonDatasetJSO;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.Exporter;
import org.timepedia.exporter.client.ExporterUtil;

@ExportPackage("chronoscope")
public class ChronoscopeUIBinder extends Composite implements Exportable {

    private static ChronoscopeWidgetUIBinder uiBinder = GWT.create(ChronoscopeWidgetUIBinder.class);

    interface ChronoscopeWidgetUIBinder extends
            UiBinder<Widget, ChronoscopeUIBinder> {
    }

    @UiField
    ChronoscopeUIBinderWidget chronoscopeUIBinderWidget;

    public ChronoscopeUIBinder() {
        init();
    }

    public void init() {
        exportFunctions();
        initWidget(uiBinder.createAndBindUi(this));
        // chronoscopeUIBinderWidget.setSize(800, 400);
        onChronoscopeLoad();
        chronoscopeUIBinderWidget.setInitialDatasets(jsonInitialDatasets);
    }

    private JsArray<JsonDatasetJSO> jsonInitialDatasets;
    private JsArray<JsonDatasetJSO> jsonChangedDatasets;
    private int setDatasetNum = 0;

    @Export("setElementId")
    public void setElementId(String elementId) {
            ChronoscopeUIBinderEntryPoint.elementId=elementId;
    }

    @Export("setDatasets")
    public void setDatasets(JsArray<JsonDatasetJSO> jsonDatasets) {
        if (setDatasetNum == 0) {
            jsonInitialDatasets = jsonDatasets;
            setDatasetNum++;
        } else if (setDatasetNum == 1) {
            jsonChangedDatasets = jsonDatasets;
        }
    }

    protected void exportFunctions() {
        Exporter exporter = (Exporter) GWT.create(ChronoscopeUIBinder.class);
        View v = (View) GWT.create(DOMView.class);
        ((DOMView) v).exportFunctions();
    }

    protected void onChronoscopeLoad() {
        try {
            JavaScriptObject foo = ExporterUtil.wrap(this);
            chronoscopeLoaded(foo);
        } catch (Exception e) {
            if (ChronoscopeOptions.isErrorReportingEnabled()) {
                Window.alert("Chronoscope Failed to Initialize because " + e);
            }
        }
    }


    private native void chronoscopeLoaded(JavaScriptObject chronoscope) /*-{
    if($wnd.onChronoscopeLoaded)
    $wnd.onChronoscopeLoaded(chronoscope);
    }-*/;
}
