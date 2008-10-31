package org.timepedia.chronoscope.client.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for obtaining a {@link MipMapStrategy} instance by name.
 * 
 * @author chad takahashi
 */
public final class MipMapStrategyFactory {
  private static MipMapStrategyFactory singleton;
  
  public static Map<String,MipMapStrategy> name2strategy = 
    new HashMap<String,MipMapStrategy>();
  
  public static MipMapStrategyFactory newInstance() {
    if (singleton == null) {
      singleton = new MipMapStrategyFactory();
    }
    return singleton;
  }
  
  private MipMapStrategyFactory() {
    name2strategy.put("binary", BinaryMipMapStrategy.MEAN);
  }
  
  public MipMapStrategy get(String strategyName) {
    strategyName = strategyName.toLowerCase();
    MipMapStrategy strategy = name2strategy.get(strategyName);
    if (strategy != null) {
      return strategy;
    }
    
    throw new RuntimeException("Unrecognized MipMapStrategy identifier: '" 
        + strategyName + "'");
  }
}
