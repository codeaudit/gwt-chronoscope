package org.timepedia.chronoscope.client;

import org.timepedia.exporter.client.Exportable;

/**
 * An interface modeling a multiresolution dataset of two coordinates
 * <p/>
 * A multiresolution dataset is a dataset consisting of multiple levels, with level 0 being the
 * bottom most level containing the original datasets. Levels 1 and above represent a 'zoomed out' view of the data,
 * filtered, interpolated, scaled, or decimated as appropriate.  The only requirement is that if M and N are two
 * levels in the dataset, and N > M, then getNumSamples(M) < getNumSamples(N).  A useful visualization is to
 * imagine levels stacking on top of one another like a pyramid. There is no requirement that the pyramid contain
 * a level Z such that getNumSamples(Z) == 1, but in practice, getNumSamples(Z) should be less than
 * XYPlot.getMaxDrawablePoints()
 * <p/>
 * It is recommended to use a method that makes successive levels half the size of former levels, so that the
 * height of the pyramid is log_2(num_samples)
 *
 * @gwt.exportPackage chronoscope
 * @gwt.export
 */
public interface XYDataset extends Exportable {
    /**
     * Returns the number of samples in the dataset (level 0)
     *
     * @return
     */
    public int getNumSamples();

    /**
     * Return the number of samples for level
     *
     * @param level
     * @return
     */
    public int getNumSamples(int level);

    /**
     * Return the X value for the given index on level 0
     *
     * @param index
     * @return
     */
    public double getX(int index);

    /**
     * Return the Y value for the given index on level 0
     *
     * @param index
     * @return
     */
    public double getY(int index);

    /**
     * Return the X value for the given index on the given leven
     *
     * @param index
     * @param level
     * @return
     */
    public double getX(int index, int level);

    /**
     * Return the Y value for the given index on the given leven
     *
     * @param index
     * @param level
     * @return
     */
    public double getY(int index, int level);

    /**
     * Return the maximum Y value of level 0
     *
     * @return
     */
    double getRangeTop();

    /**
     * Return the minum Y value of level 0
     *
     * @return
     */
    double getRangeBottom();

    /**
     * Return a unique identifier for this dataset
     *
     * @return
     */
    String getIdentifier();

    /**
     * Return a label that may be used in a chart legend for this dataset
     *
     * @return
     */
    String getRangeLabel();

    /**
     * Return an id used to identify the axis this dataset should be assigned to, typically the physical units
     * (e.g. meters/second). Datasets with identical axis ids will be default, be allocated on the same axis.
     *
     * @return
     */
    String getAxisId();

}
