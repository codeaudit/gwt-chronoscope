package org.timepedia.chronoscope.client.render.domain;

/**
 * Factory for obtaining a suitable {@link DateTickFormatter} object for a given
 * domain span.
 * 
 * @author chad takahashi
 */
public final class TickFormatterFactory {

  /**
   * A value in the range (0.0, 1.0], which determines how readily the
   * {@link #findBestFormatter(double)} algorithm will "jump down to" the next
   * sub-formatter. The larger the factor, the more "affinity" the algorithm
   * will have for the current formatter.
   */
  private static final double AFFINITY_FACTOR = 0.35;

  private static TickFormatterFactory factory;
  private static TickFormatter rootFormatter = new MilleniumTickFormatter();

  private double cachedDomainWidth = Double.NEGATIVE_INFINITY;
  private TickFormatter cachedFormatter = null;

  /**
   * Returns a singleton instance of this factory.
   */
  public static TickFormatterFactory get() {
    if (factory == null) {
      factory = new TickFormatterFactory();
    }
    return factory;
  }

  /**
   * Finds the smallest-scale {@link DateTickFormatter} that engulfs the 
   * specified domain interval.
   */
  public TickFormatter findBestFormatter(double domainWidth) {
    if (domainWidth == cachedDomainWidth) {
      return cachedFormatter;
    }

    TickFormatter tlf = rootFormatter;

    while (!tlf.isLeafFormatter()) {
      if (tlf.inInterval(domainWidth * AFFINITY_FACTOR)) {
        break;
      }
      tlf = tlf.subFormatter;
    }

    cachedDomainWidth = domainWidth;
    cachedFormatter = tlf;
    return tlf;
  }
  
  public TickFormatter getLeafFormatter() {
    TickFormatter formatter = rootFormatter;
    while (!formatter.isLeafFormatter()) {
      formatter = formatter.subFormatter;
    }
    return formatter;
  }

  public TickFormatter getRootFormatter() {
    return rootFormatter;
  }
  
  private static void log(Object msg) {
    System.out.println("TESTING:TickFormatterFactory> " + msg);
  }

}
