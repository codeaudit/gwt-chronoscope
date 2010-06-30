/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.timepedia.chronoscope.client.data;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

/**
* Class for passing back incremental data from JS.
*/
@Export
public class IncrementalDatasetResponseImpl implements IncrementalDataResponse, Exportable {

  private AbstractDataset dataset;

  public IncrementalDatasetResponseImpl(AbstractDataset dataset) {
    this.dataset = dataset;
  }

  @Override
  @Export
  public void addData(JsArrayNumber domain, JsArray<JsArrayNumber> range) {
    dataset.setIncrementalData(domain, range);
  }
}
