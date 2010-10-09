package org.timepedia.chronoscope.client.render.domain;

import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;

/**
 * Factory for obtaining a suitable {@link TickFormatter} object for a given
 * domain span.
 * 
 * @author chad takahashi
 */
@ExportPackage("chronoscope")
public abstract class TickFormatterFactory<T> implements Exportable {

  private double affinityFactor;
  private TickFormatter<T> rootFormatter;
  private double cachedDomainWidth = Double.NEGATIVE_INFINITY;
  private TickFormatter<T> cachedFormatter = null;
  
  public TickFormatterFactory() {
    this.affinityFactor = getAffinityFactor();
    this.rootFormatter = createRootTickFormatter();    
  }
  
  protected abstract TickFormatter<T> createRootTickFormatter();
  
  /**
   * A value in the range (0.0, 1.0], which determines how readily the
   * {@link #findBestFormatter(double)} algorithm will "jump down to" the next
   * sub-formatter. The larger the factor, the more "affinity" the algorithm
   * will have for the current formatter.
   */
  protected double getAffinityFactor() {
    return 0.35;
  }
  
  /**
   * Finds the smallest-scale {@link TickFormatter} that engulfs the 
   * specified domain interval.
   */
  public final TickFormatter<T> findBestFormatter(double domainWidth) {
    if (domainWidth == cachedDomainWidth) {
      return cachedFormatter;
    }

    TickFormatter<T> tlf = rootFormatter;

    while (!tlf.isLeafFormatter()) {
      if (tlf.inInterval(domainWidth * affinityFactor)) {
        break;
      }
      tlf = tlf.subFormatter;
    }

    cachedDomainWidth = domainWidth;
    cachedFormatter = tlf;
    return tlf;
  }
  
  public final TickFormatter<T> getLeafFormatter() {
    TickFormatter<T> formatter = rootFormatter;
    while (!formatter.isLeafFormatter()) {
      formatter = formatter.subFormatter;
    }
    return formatter;
  }

  public final TickFormatter<T> getRootFormatter() {
    return rootFormatter;
  }

}
