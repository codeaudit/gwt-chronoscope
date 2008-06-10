package org.timepedia.chronoscope.gviz.client;

import com.google.gwt.gadgets.client.GadgetFeature;
import com.google.gwt.gadgets.client.SetTitleFeature;

/**
 *
 */
@GadgetFeature.FeatureName("locked-domain")
public interface NeedsLockedDomain {
  void initializeFeature(LockedDomainFeature feature);
}