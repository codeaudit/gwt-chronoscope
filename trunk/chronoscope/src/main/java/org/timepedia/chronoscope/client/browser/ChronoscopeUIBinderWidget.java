package org.timepedia.chronoscope.client.browser;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import org.timepedia.chronoscope.client.ChronoscopeComponentFactory;
import org.timepedia.chronoscope.client.Dataset;
import org.timepedia.chronoscope.client.Datasets;
import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.browser.json.GwtJsonDataset;
import org.timepedia.chronoscope.client.browser.json.JsonDatasetJSO;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.io.DatasetReader;
import org.timepedia.chronoscope.client.util.Interval;

public class ChronoscopeUIBinderWidget extends Widget{

    private ChronoscopeUIBinderExportable exportable = new ChronoscopeUIBinderExportable();
    private DatasetReader datasetReader = new DatasetReader(new ChronoscopeComponentFactory());
    private Element element;
    private ChartPanel chartPanel;
    private XYPlot xyPlot;
    private View view;
    private final Dataset[] datasets;
    private int width = 400, height = 250;  
    
    public ChronoscopeUIBinderWidget(){
        setElement(DOM.createDiv());
        element = getElement();
        exportable.exportFunctions();
        JsArray<JsonDatasetJSO> jsonDatasets = exportable.onChronoscopeShimLoaded();
        datasets = jsonDatasetsConverter(jsonDatasets);
        createChartPanel();
    }

    /**
     * Create chart panel , init the chart panel and attach.
     */
    private void createChartPanel() {
        chartPanel = newChartPanel();
        chartPanel.setDatasets(datasets);
        chartPanel.setDomElement(element);
        chartPanel.setDimensions(width, height);
        chartPanel.init();
        chartPanel.attach();
        xyPlot = chartPanel.getPlot();
        view = chartPanel.getView();
    }

    protected ChartPanel newChartPanel() {
    return new ChartPanel();
  }


    /**
     * JsArray<JsonDatasetJSO> to Dataset[] Converter
     */
   public Dataset[] jsonDatasetsConverter(JsArray<JsonDatasetJSO> jsonDatasets) {
       if (jsonDatasets != null) {
           int numDatasets = jsonDatasets.length();
           Dataset ds[] = new Dataset[numDatasets];
           for (int i = 0; i < numDatasets; i++) {
               ds[i] = datasetReader.createDatasetFromJson(new GwtJsonDataset(jsonDatasets.get(i)), true);
           }
           return ds;
       }
       return null;
  }

   /**
    * Initial Datasets, when xyPlot has dataset id 'shim__'
    */
  public boolean setInitialDatasets(JsArray<JsonDatasetJSO> jsonDatasets) {
      if (xyPlot != null) {
          Datasets xyPlotDatasets = xyPlot.getDatasets();
          if (xyPlotDatasets.size() == 1 && xyPlotDatasets.get(0).getIdentifier().equals("shim__")) {
              addedDataset(jsonDatasets);
              removedDataset(this.datasets);
              return true;
          }
      }
      return false;
  }

  /**
   * Added new dataset
   * @param jsonDatasets( JsArray JsonDatasetJSO )
   * @return Dataset[]( Added to the xyplot )
   */
   public Dataset[] addedDataset(JsArray<JsonDatasetJSO> jsonDatasets) {
       Dataset[] addDatasets = jsonDatasetsConverter(jsonDatasets);
       boolean addFinish = addedDataset(addDatasets);
       if (addFinish) {
           return addDatasets;
       }
       return null;
  }

   public boolean addedDataset(Dataset[] addDatasets) {
       int addLen = addDatasets.length;
      if (xyPlot != null && addLen > 0) {
          for (int i = 0; i < addLen; i++) {
              xyPlot.getDatasets().add(addDatasets[i]);
          }
          return true;
      }
      return false;
   }

   /**
    * Changed existing dataset(Id must be the same)
    * @param jsonDatasets (  JsArray<JsonDatasetJSO> )
    * @return Dataset[]( Changed to the xyplot )
    */
     public Dataset[] changedDataset(JsArray<JsonDatasetJSO> jsonDatasets) {
        Dataset[] changedDatasets = jsonDatasetsConverter(jsonDatasets);
        boolean changeFinish=changedDataset(changedDatasets);
        if(changeFinish){
            return changedDatasets;
        }
        return null;
    }

     public boolean changedDataset(Dataset[] changedDatasets) {
        int changeLen = changedDatasets.length;
        if (xyPlot != null && changeLen > 0) {
            for (int i = 0; i < changeLen; i++) {
                 Interval changedDatasetInterval=changedDatasets[i].getDomainExtrema();
                 xyPlot.getDatasets().fireChanged(changedDatasets[i], changedDatasetInterval);
            }
            return true;
        }
        return false;
     }

     /**
      * Removed existing dataset(Id must be the same)
      * @param jsonDatasets (  JsArray<JsonDatasetJSO> )
      */
    public boolean removedDataset(JsArray<JsonDatasetJSO> jsonDatasets) {
        Dataset[] removeDatasets = jsonDatasetsConverter(jsonDatasets);
        return removedDataset(removeDatasets);
  }

    /**
     * Removed existing dataset(Id must be the same)
     * @param removeDatasets( When changed data, the situation is not JSON  )
     */
   public boolean removedDataset(Dataset[] removeDatasets) {
       if (xyPlot != null && removeDatasets != null) {
           int len = removeDatasets.length;
           for (int i = 0; i < len; i++) {
               Datasets xyPlotDatasets=xyPlot.getDatasets();
               if (xyPlotDatasets.size() == 1 &&( !xyPlotDatasets.get(0).equals(this.datasets[0]))) {
                   addedDataset(this.datasets);
               }
               xyPlotDatasets.remove(xyPlotDatasets.indexOf(removeDatasets[i]));
           }
           return true;
       }
       return false;
  }

   public void setSize(int width , int height  ){
       this.width=width;
       this.height=height;
       if(view != null){
           view.resize(width, height);
       }
   }

    @Override
    public void onBrowserEvent(Event evt) {
        chartPanel.getPlotPanel().onBrowserEvent(evt);
    }

}
