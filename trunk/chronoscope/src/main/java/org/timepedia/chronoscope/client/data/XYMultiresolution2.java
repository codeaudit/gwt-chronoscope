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

    public XYMultiresolution2(double[] domain, double[] range) {

        this.domain = domain;
        this.range = range;
    }

    interface XYPartitionFunction {

    }

    private void compute(XYPartitionStrategy strategy, XYPartitionFunction function) {

//        int levels = strategy.getNumLevels(this);
//        double intervalStart = domain[0];
//        double intervalEnd = domain[domain.length - 1];
//        multiDomain = new double[levels][];
//        multiRange = new double[levels][];
//        for (int level = 0; level < levels; level++) {
//            XYPartitioner partitioner = strategy.getPartitioner(this, level);
//            int numSamples = partitioner.getNumPartitions(this, intervalStart, intervalEnd);
//            multiDomain[level] = new double[numSamples];
//            multiRange[level] = new double[numSamples];
//            for (int index = 0; index < multiDomain[level].length; index++) {
//                multiDomain[level][index] = strategy.getDomainValue(this, level, index);
//                multiRange[level][index] = strategy.getRangeValue(this, level, index);
//                if (level == 0) {
//                    rangeBottom = Math.min(rangeBottom, multiRange[level][index]);
//                    rangeTop = Math.max(rangeTop, multiRange[level][index]);
//                }
//            }
//        }
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
    }

    /**
     * Returns the number of multiresolution levels that should be created
     */
    public interface XYPartitionStrategy {
        int getNumLevels(XYMultiresolution2 xy);

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

      public  static DateQuantizedXYPartitioner partitioners[] = {
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
          //      new DateQuantizedXYPartitioner(HALFMONTH),
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

        public int getNumLevels(XYMultiresolution2 xy) {
            double domain = xy.getDomain();
            for (int i = 0; i < partitioners.length; i++) {
                if (!partitioners[i].containsInterval(domain)) {
                    return partitioners.length - i + 1;
                }
            }
            return 0;
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
                if(interval < WEEK) return nonDateSpecificPartition((long)intervalStart, (long)intervalEnd);
                Date date, dateEnd;
                double quantizedBegin, quantizedEnd;
                date = new Date((long) intervalStart);
                dateEnd = new Date((long) intervalEnd);


                if(interval == WEEK) {
                        quantizedBegin = date.getTime() - date.getHours() * HOUR - date.getMinutes() * MINUTE -
                                date.getSeconds() * SECOND - date.getDay() * DAY - date.getTimezoneOffset()*HOUR;
                        quantizedBegin = quantizedBegin - quantizedBegin % 1000;
                        quantizedEnd = dateEnd.getTime() - dateEnd.getHours() * HOUR - dateEnd.getMinutes() * MINUTE -
                                dateEnd.getSeconds() * SECOND + (7-dateEnd.getDay()) * DAY - dateEnd.getTimezoneOffset()*HOUR;
                    if(quantizedEnd < intervalEnd) quantizedEnd += WEEK;
                    quantizedEnd = quantizedEnd - quantizedEnd % 1000;    
                        return (int) ((quantizedEnd-quantizedBegin)/WEEK);
                }
                else throw new UnsupportedOperationException("Not done yet");
            }



            private int nonDateSpecificPartition(long intervalStart, long intervalEnd) {
                long endRemain  = intervalEnd % interval;
                if(endRemain > 0) endRemain = interval - endRemain -1;
                long startRemain = intervalStart % interval;
                if(startRemain > 0) startRemain = interval-startRemain;
                return (int) (((intervalEnd + endRemain) - (intervalStart - startRemain))/interval);
            }

            public void getPartition(int partitionNumber, XYMultiresolution2 xy, double intervalStart, double intervalEnd, Partition result) {
                result.data = xy;
                result.partitionStart = quantizeTo(intervalStart) + interval * partitionNumber;
                result.partitionEnd = result.partitionStart + interval;


            }

            private long quantizeTo(double domainVal) {
                if(interval < WEEK) {
                   long remain = (long) (domainVal % interval);
                   return (long) (domainVal - remain);
                }
                Date date=new Date((long)domainVal);
                return date.getTime() - date.getHours() * HOUR - date.getMinutes() * MINUTE -
                                date.getSeconds() * SECOND - date.getDay() * DAY;


            }

            public boolean containsInterval(double domain) {
                return interval < domain;
            }
        }


    }

    public static void main(String[] args) {
        int numPtns = 100;
        double domain[] = new double[numPtns];

        double start = System.currentTimeMillis();
        for(int i=0; i<domain.length; i++) {
            domain[i] = start;
            start += Math.random() * XYMultiresolution2.DateQuantizedXYPartitionStrategy.WEEK;
        }

        System.out.println("Start is "+date(domain[0]));
        System.out.println("End is "+date(domain[domain.length-1]));
        XYMultiresolution2 xy=new XYMultiresolution2(domain, domain);

        Partition z= new Partition();
        for(int i=9; i< XYMultiresolution2.DateQuantizedXYPartitionStrategy.partitioners.length; i++) {
            DateQuantizedXYPartitionStrategy.DateQuantizedXYPartitioner p = DateQuantizedXYPartitionStrategy.partitioners[i];
            for(int j=0; j<p.getNumPartitions(xy,domain[0], domain[domain.length-1]); j++) {
               p.getPartition(j, xy, domain[0], domain[domain.length-1], z);
                System.out.println("Partition: "+date(z.partitionStart)+" to "+date(z.partitionEnd));
            }

        }
    }

    public static String date(double v) {
        return new Date((long)v).toGMTString();
    }

}
