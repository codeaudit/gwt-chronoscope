package org.timepedia.chronoscope.gviz.client;

import com.google.gwt.gadgets.client.GadgetFeature;
import com.google.gwt.gadgets.client.SetTitleFeature;

/**
 *
 */
@GadgetFeature.FeatureName("idi")
public interface NeedsIdi {
  void initializeFeature(IdiFeature feature);
}
