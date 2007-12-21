package org.timepedia.chronoscope.client.data;

import java.util.Date;

/**
 * This class implements general purpose code to pre-process an XY Dataset into multiresolution representation allowing
 * the choice of strategies for partitioning the domain, and computing functions on range values with the partition.
 * <p/>
 * For example, a domain may be partitioned into weeks, months, quarters, years, etc and for each partition,
 * you may apply sum, avg, max, etc to compute a representative value for each partition.
 */
public class XYMultiresolution2 {
    private double[] domain;
    private double[] range;
    private double[][] multiDomain;
    private double[][] multiRange;
    private double rangeBottom = Double.MAX_VALUE, rangeTop = Double.MIN_VALUE;
    private int[][] previousIndices;

    public XYMultiresolution2(double[] domain, double[] range) {

        this.domain = domain;
        this.range = range;
    }


    public int getIndexFromUpperIndice(int level, int uindex) {
      if(level == 0) return uindex;
      return previousIndices[level][uindex];
    }

    private void compute(XYPartitionStrategy strategy, XYAggregateFunction function) {

        int levels = strategy.getNumLevels(this) + 1;
        int startLevel = strategy.findBestStartingLevel(this);


        double intervalStart = domain[0];
        double intervalEnd = domain[domain.length - 1];
        if(levels < startLevel) levels=startLevel;
        
        multiDomain = new double[levels - startLevel][];
        multiRange = new double[levels - startLevel][];
        previousIndices = new int[levels-startLevel][];

        for (int level = 0; level < multiDomain.length; level++) {
            if (level == 0) {
                multiDomain[0]=new double[domain.length];
                multiRange[0]=new double[range.length];
                for (int index = 0; index < domain.length; index++) {
                    multiDomain[0][index] = domain[index];
                    multiRange[0][index] = range[index];
                    rangeBottom = Math.min(rangeBottom, multiRange[0][index]);
                    rangeTop = Math.max(rangeTop, multiRange[0][index]);
                }
            } else {
                XYPartitioner partitioner = strategy.getPartitioner(this, level + startLevel - 1);
                int numSamples = partitioner.getNumPartitions(this, intervalStart, intervalEnd);
                previousIndices[level]=new int[numSamples];

                System.out.println("Got "+numSamples+" for partitioner "+partitioner);
                multiDomain[level] = new double[numSamples];
                multiRange[level] = new double[numSamples];
                int currentPartition = 0;
                int index = 0;
                Partition part = new Partition();
                int startIndex = 0;
                partitioner.getPartition(currentPartition, this, intervalStart, intervalEnd, part);
                while (index < domain.length && currentPartition < numSamples) {
                   if(domain[index] >= part.partitionStart && domain[index] <= part.partitionEnd && ++index < domain.length) {
                   } else {
                       System.out.println("Start index "+startIndex+" end index "+index+" for partition "+part);
                       multiDomain[level][currentPartition]=part.partitionStart;
                       multiRange[level][currentPartition]=function.computeAggregate(this,
                               startIndex, index, level-1, 0, startIndex, index);
                       startIndex = index;
                       previousIndices[level][currentPartition]=startIndex;
                       currentPartition++;
                       partitioner.getPartition(currentPartition, this, intervalStart, intervalEnd, part);
                   }
                }
            }
            System.out.println("Done with level "+level);
            
        }
    }


    private int getNumSamples() {
        return domain.length;
    }

    public double getX(int index) {
        return domain[index];
    }

    public double getY(int index) {
        return range[index];
    }

    public double getDomain() {
        return domain[domain.length - 1] - domain[0];
    }

    public static class Partition {
        XYMultiresolution2 data;
        double partitionStart;
        double partitionEnd;
        public String toString() {
            return("StartPartition: "+date(partitionStart)+" - EndPartition: "+date(partitionEnd));
        }
    }

    /**
     * Returns the number of multiresolution levels that should be created
     */
    public interface XYPartitionStrategy {
        int getNumLevels(XYMultiresolution2 xy);

        int findBestStartingLevel(XYMultiresolution2 xy);

        XYPartitioner getPartitioner(XYMultiresolution2 xy, int level);
    }

    public interface XYPartitioner {
        public int getNumPartitions(XYMultiresolution2 xy, double intervalStart,
                                    double intervalEnd);

        public void getPartition(int partitionNumber, XYMultiresolution2 xy, double intervalStart,
                                 double intervalEnd, Partition result);

    }

    public interface XYAggregateFunction {
        /**
         * This function is given data, a previous (lower level) interval, the requested interval indices,
         * and is expected to return an aggregate function of the given interval. The previous interval is given
         * in case computations can be reused. For example, if you are computing a partition which spans a month,
         * and you have previously computed 4 week partitions, then you may be able to perform a linear combination of
         * the previous aggregate value plus some fraction of the remaining 0-3 days in the month.
         *
         * @param data
         * @param lowerStartInterval
         * @param lowerEndInterval
         * @param lowerLevel
         * @param previousAggregate
         * @param startInterval
         * @param endInterval
         * @return
         */
        double computeAggregate(XYMultiresolution2 data,
                                int lowerStartInterval,
                                int lowerEndInterval,
                                int lowerLevel,
                                int previousAggregate,
                                int startInterval,
                                int endInterval
        );
    }

    public static class DateQuantizedXYPartitionStrategy implements XYPartitionStrategy {


        public static final long SECOND = 1000,
                QUARTERMINUTE = SECOND * 15,
                HALFMINUTE = QUARTERMINUTE * 2,
                MINUTE = 60 * SECOND,
                QUARTERHOUR = MINUTE * 15,
                HALFHOUR = MINUTE * 30,
                HOUR = 60 * MINUTE,
                QUARTERDAY = HOUR * 6,
                HALFDAY = HOUR * 12,
                DAY = 24 * HOUR,
                WEEK = DAY * 7,
                HALFMONTH = DAY * 15,
                MONTH = 31 * DAY,
                QUARTER = MONTH * 3,
                BIANNUAL = QUARTER * 2,
                YEAR = DAY * 365,
                BIYEAR = YEAR * 2,
                FIVEYEAR = YEAR * 5,
                DECADE = YEAR * 5,
                QUARTERCENTURY = FIVEYEAR * 5,
                HALFCENTURY = QUARTERCENTURY * 2,
                CENTURY = DECADE * 10,
                QUARTERMILLENIUM = DECADE * 25,
                HALFMILLENIUM = QUARTERMILLENIUM * 2,
                MILLENIUM = CENTURY * 10;

        public static DateQuantizedXYPartitioner partitioners[] = {
                new DateQuantizedXYPartitioner(SECOND),
                new DateQuantizedXYPartitioner(QUARTERMINUTE),
                new DateQuantizedXYPartitioner(HALFMINUTE),
                new DateQuantizedXYPartitioner(MINUTE),
                new DateQuantizedXYPartitioner(QUARTERHOUR),
                new DateQuantizedXYPartitioner(HALFHOUR),
                new DateQuantizedXYPartitioner(HOUR),
                new DateQuantizedXYPartitioner(QUARTERDAY),
                new DateQuantizedXYPartitioner(HALFDAY),
                new DateQuantizedXYPartitioner(DAY),
                new DateQuantizedXYPartitioner(WEEK),
//                new DateQuantizedXYPartitioner(HALFMONTH),
//                new DateQuantizedXYPartitioner(MONTH),
//                new DateQuantizedXYPartitioner(QUARTER),
//                new DateQuantizedXYPartitioner(BIANNUAL),
//                new DateQuantizedXYPartitioner(YEAR),
//                new DateQuantizedXYPartitioner(BIYEAR),
//                new DateQuantizedXYPartitioner(FIVEYEAR),
//                new DateQuantizedXYPartitioner(DECADE),
//                new DateQuantizedXYPartitioner(QUARTERCENTURY),
//                new DateQuantizedXYPartitioner(HALFCENTURY),
//                new DateQuantizedXYPartitioner(CENTURY),
//                new DateQuantizedXYPartitioner(QUARTERMILLENIUM),
//                new DateQuantizedXYPartitioner(HALFMILLENIUM),
//                new DateQuantizedXYPartitioner(MILLENIUM),
        };

        public int findBestStartingLevel(XYMultiresolution2 xy) {
            int dists[] = new int[partitioners.length];

            for (int i = 1; i < xy.getNumSamples(); i++) {
                double d = xy.getX(i) - xy.getX(i - 1);
                for (int j = 0; j < dists.length; j++) {
                    if (d < partitioners[j].interval) {
                        dists[j]++;
                        break;
                    }
                }
            }
            int max = -1, maxVal = -1;
            for (int i = 0; i < dists.length; i++) {
                if (dists[i] > maxVal) {
                    max = i;
                    maxVal = dists[i];
                }
            }
            return max;
        }

        public int getNumLevels(XYMultiresolution2 xy) {
//            double domain = xy.getDomain();
//            for (int i = 0; i < partitioners.length; i++) {
//                if (!partitioners[i].containsInterval(domain)) {
//                    return partitioners.length - i + 1;
//                }
//            }
//            return 0;
            return partitioners.length;
        }

        public XYPartitioner getPartitioner(XYMultiresolution2 xy, int level) {
            return partitioners[level];
        }

        public static class DateQuantizedXYPartitioner implements XYPartitioner {
            private long interval;

            public DateQuantizedXYPartitioner(long interval) {

                this.interval = interval;
            }

            public int getNumPartitions(XYMultiresolution2 xy, double intervalStart, double intervalEnd) {
                if (interval < WEEK) return nonDateSpecificPartition((long) intervalStart, (long) intervalEnd);
                Date date, dateEnd;
                double quantizedBegin, quantizedEnd;
                date = new Date((long) intervalStart);
                dateEnd = new Date((long) intervalEnd);


                if (interval == WEEK) {
                    quantizedBegin = date.getTime() - date.getHours() * HOUR - date.getMinutes() * MINUTE -  date.getTimezoneOffset() * MINUTE
                            - date.getSeconds() * SECOND - date.getDay() * DAY - date.getTimezoneOffset() * HOUR;
                    quantizedBegin = quantizedBegin - quantizedBegin % 1000;
                    quantizedEnd = dateEnd.getTime() - dateEnd.getHours() * HOUR - dateEnd.getMinutes() * MINUTE - date.getTimezoneOffset() * MINUTE
                            - dateEnd.getSeconds() * SECOND + (7 - dateEnd.getDay()) * DAY - dateEnd.getTimezoneOffset() * HOUR;
                    if (quantizedEnd < intervalEnd) quantizedEnd += WEEK;
                    quantizedEnd = quantizedEnd - quantizedEnd % 1000;
                    return (int) ((quantizedEnd - quantizedBegin) / WEEK);
                } else throw new UnsupportedOperationException("Not done yet");
            }


            private int nonDateSpecificPartition(long intervalStart, long intervalEnd) {
                long endRemain = intervalEnd % interval;
                if (endRemain > 0) endRemain = interval - endRemain - 1;
                long startRemain = intervalStart % interval;
                if (startRemain > 0) startRemain = interval - startRemain;
                System.out.println("QIntervalStart "+date(intervalStart-startRemain)+ " QIntervalEnd "+date(intervalEnd+endRemain));
                return (int) (((intervalEnd + endRemain) - (intervalStart - startRemain)) / interval)+1;
            }

            public void getPartition(int partitionNumber, XYMultiresolution2 xy, double intervalStart, double intervalEnd, Partition result) {
                result.data = xy;
                result.partitionStart = quantizeTo(intervalStart) + interval * partitionNumber;
                result.partitionEnd = result.partitionStart + interval;


            }

            private long quantizeTo(double domainVal) {
                if (interval < WEEK) {
                    long remain = (long) (domainVal % interval);
                    return (long) (domainVal - remain);
                }
                Date date = new Date((long) domainVal);
                return date.getTime() - date.getHours() * HOUR - date.getMinutes() * MINUTE - date.getTimezoneOffset() * MINUTE
                        - date.getSeconds() * SECOND - date.getDay() * DAY;


            }

            public boolean containsInterval(double domain) {
                return interval < domain;
            }
        }


    }

    public static void main(String[] args) {
        System.out.println("CurrentMillis: "+System.currentTimeMillis());
        int numPtns = 1000;
        double domain[] = new double[numPtns];
        double range[] = new double[numPtns];

        double start = System.currentTimeMillis();
        for (int i = 0; i < domain.length; i++) {
            domain[i] = start;
            range[i]=Math.floor(Math.random() * 10);
            start += Math.random() * XYMultiresolution2.DateQuantizedXYPartitionStrategy.HOUR;
        }


        System.out.println("Start is " + date(domain[0]));
        System.out.println("End is " + date(domain[domain.length - 1]));
        XYMultiresolution2 xy = new XYMultiresolution2(domain, range);

        xy.compute(new  XYMultiresolution2.DateQuantizedXYPartitionStrategy(),
                new XYAggregateFunction() {
                    public double computeAggregate(XYMultiresolution2 data, int lowerStartInterval, int lowerEndInterval, int lowerLevel, int previousAggregate, int startInterval, int endInterval) {
                        double sum=0;
                        for(int i=startInterval; i<endInterval; i++)
                            sum+=data.getY(i);

                        return sum;
                    }
                });

    }

    public static String date(double v) {
        return new Date((long) v).toGMTString();
    }

}
