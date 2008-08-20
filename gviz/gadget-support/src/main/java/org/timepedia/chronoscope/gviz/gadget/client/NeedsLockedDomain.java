package org.timepedia.chronoscope.gviz.gadget.client;

import com.google.gwt.gadgets.client.GadgetFeature;

/**
 *
 */
@GadgetFeature.FeatureName("locked-domain")
public interface NeedsLockedDomain {
  void initializeFeature(LockedDomainFeature feature);
}