package org.timepedia.chronoscope.client;

import org.timepedia.chronoscope.client.data.DatasetFactory;
import org.timepedia.chronoscope.client.data.MipMapStrategy;
import org.timepedia.chronoscope.client.util.ArgChecker;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for obtaining a family of components that are tied to a given version 
 * of Chronoscope (i.e. the free vs. the commercial version). 
 * 
 * @author chad takahashi
 */
public abstract class ComponentFactory {
  private static ComponentFactory singleton = new ChronoscopeComponentFactory();
  private static boolean componentFactoryAlreadyAssigned = false;
  
  protected Map<String,MipMapStrategy> name2mipmapStrategy = 
    new HashMap<String,MipMapStrategy>();

  protected ComponentFactory() {}
  
  /**
   * Obtains a singleton instance of this factory.
   */
  public static final ComponentFactory get() {
    if (singleton == null) {
      throw new IllegalStateException("FactoryLookup singleton object not assigned");
    }
    return singleton;
  }
  
  public abstract DatasetFactory getDatasetFactory();
  
  public final MipMapStrategy getMipMapStrategy(String name) {
    //GWT.log("TESTING: ComponentFactory: available mipmap strategies: " + name2mipmapStrategy.keySet(), null);
    MipMapStrategy mms = name2mipmapStrategy.get(name);
    if (mms == null) {
      throw new IllegalArgumentException("Can't find MipMapStrategy associated with " +
          "name '" + name + "'");
    }
    return mms;
  }
  
  public static void setComponentFactoryImpl(ComponentFactory factory) {
    ArgChecker.isNotNull(factory, "factory");
    
    // Only allow ComponentFactory to be set once
    if (componentFactoryAlreadyAssigned) {
      throw new IllegalStateException("ComponentFactory instance already set");
    }
    
    componentFactoryAlreadyAssigned = true;
    singleton = factory;
  }
}
