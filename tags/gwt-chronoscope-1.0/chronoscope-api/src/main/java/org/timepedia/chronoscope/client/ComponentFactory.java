package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.data.DatasetFactory;
import org.timepedia.chronoscope.client.data.MipMapStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for obtaining a family of components that are tied to a given version
 * of Chronoscope (i.e. the free vs. the commercial version).
 *
 * @author chad takahashi
 */
public abstract class ComponentFactory {

  protected Map<String, MipMapStrategy> name2mipmapStrategy
      = new HashMap<String, MipMapStrategy>();

  protected ComponentFactory() {
  }

  public abstract DatasetFactory getDatasetFactory();

  public final MipMapStrategy getMipMapStrategy(String name) {
    //GWT.log("TESTING: ComponentFactory: available mipmap strategies: " + name2mipmapStrategy.keySet(), null);
    MipMapStrategy mms = name2mipmapStrategy.get(name);
    if (mms == null) {
      throw new IllegalArgumentException(
          "Can't find MipMapStrategy associated with " + "name '" + name + "'");
    }
    return mms;
  }
}
