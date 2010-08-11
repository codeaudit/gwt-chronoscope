package org.timepedia.chronoscope.client.render.domain;



/**
 * Formats domain axis ticks as plain integer values.
 * 
 * @author chad takahashi
 */
public class IntTickFormatter extends TickFormatter {
  private double tickValue;
  private double tickInterval;
  
  protected IntTickFormatter(int tickInterval, String longestPossibleLabel) {
    super(longestPossibleLabel);

    this.tickInterval = tickInterval;
    this.possibleTickSteps = new int[] {1, 2, 5, 10, 20, 50, 100, 200, 400, 500, 1000};
  }

  @Override
  public String formatTick() {
    return Long.toString((long)tickValue);
  }

  @Override
  public double getTickDomainValue() {
    return tickValue;
  }

  @Override
  public double getTickInterval() {
    return tickInterval;
  }

  @Override
  public int getSubTickStep(int primaryTickStep) {
    if (tickInterval == 1) {
      return primaryTickStep;
    }
    else if (tickInterval <= 10) {
      return (int)tickInterval;
    }
    else {
      return 2;
    }
  }

  @Override
  public int incrementTick(int numTickSteps) {
    tickValue += (numTickSteps * tickInterval);
    return numTickSteps;
  }

  @Override
  public void resetToQuantizedTick(double domainX, int tickStep) {
    final long dx = (long)domainX;
    final long t = ((long)tickStep * (long)tickInterval);
    
    this.tickValue = (double)(dx - (dx % t));
    /*
    System.out.println("TESTING: IntTickFormatter.resetToQuantizedTick: dx=" + dx +
        "; tick[idealStep=" + tickStep +
        "; interval=" + (long)tickInterval +
        "; QValue=" + (long)tickValue +
        "]");
    */
  }
  
  @Override
  public String toString() {
    return "tick:interval=" + (long)tickInterval;
  }
}
