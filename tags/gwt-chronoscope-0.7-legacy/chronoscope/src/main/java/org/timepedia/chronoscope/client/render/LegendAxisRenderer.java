package org.timepedia.chronoscope.client.render;

import org.timepedia.chronoscope.client.XYPlot;
import org.timepedia.chronoscope.client.XYPlotListener;
import org.timepedia.chronoscope.client.axis.LegendAxis;
import org.timepedia.chronoscope.client.canvas.Bounds;
import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.View;
import org.timepedia.chronoscope.client.gss.GssElement;
import org.timepedia.chronoscope.client.gss.GssProperties;
import org.timepedia.chronoscope.client.plot.DefaultXYPlot;

import java.util.Date;

/**
 * Renderer used to draw Legend
 */
public class LegendAxisRenderer implements AxisRenderer, GssElement {
    private LegendAxis axis;
    private GssProperties axisProperties;
    private GssProperties labelProperties;
    private int lastSerNum;
    private int lastSerPer;

    private static final String ZOOM_COLON = "Zoom:";
    private static final String ZOOM_1D = "1d";
    private static final String ZOOM_5D = "5d";
    private static final String ZOOM_1M = "1m";
    private static final String ZOOM_3M = "3m";
    private static final String ZOOM_6M = "6m";

    private static final String ZOOM_1Y = "1y";
    private static final String ZOOM_5Y = "5y";
    private static final String ZOOM_10Y = "10y";
    private static final String ZOOM_MAX = "max";


    private static final String ZOOM_STRING = ZOOM_COLON + " " + ZOOM_1D + " " + ZOOM_5D + " " + ZOOM_1M + " " +
            ZOOM_3M + " " + ZOOM_6M + " " + ZOOM_1Y + " " + ZOOM_5Y + " " + ZOOM_10Y + " " + ZOOM_MAX;
    private int legendStringHeight = -1;
    private int zoomStringWidth = -1;
    private int zcolon;
    private int z1d;
    private int z5d;
    private int z1m;

    private int z3m;
    private int z6m;
    private int z1y;
    private int z5y;
    private int z10y;
    private int zmax;
    private Bounds bounds;
    private int zspace;
    private String textLayerName;

    public LegendAxisRenderer(LegendAxis axis) {
        this.axis = axis;
    }

    public GssElement getParentGssElement() {
        return axis.getAxisPanel();
    }

    public String getType() {
        return "axislegend";
    }

    public String getTypeClass() {
        return null;
    }

    public void init(XYPlot plot, LegendAxis axis) {
        begin(plot, axis);
    }

    public void begin(XYPlot plot, LegendAxis legendAxis) {
        if (axisProperties == null) {
            axis = legendAxis;

            axisProperties = plot.getChart().getView().getGssProperties(this, "");
            labelProperties = plot.getChart().getView().getGssProperties(new GssElementImpl("label", this), "");
            textLayerName = axis.getAxisPanel().getPanelName() + axis.getAxisPanel().getAxisNumber(axis);

        }


    }

    public int getLabelWidth(View view, String str) {
        return view.getCanvas().stringWidth(str, axisProperties.fontFamily, axisProperties.fontWeight,
                                            axisProperties.fontSize) + 12;
    }

    public int getLabelHeight(View view, String str) {
        return view.getCanvas().stringHeight(str, axisProperties.fontFamily, axisProperties.fontWeight,
                                             axisProperties.fontSize);
    }

    public Bounds getLegendLabelBounds(DefaultXYPlot plot, Layer layer, Bounds axisBounds) {
        Bounds b = new Bounds();
        int x = 0;
        View view = plot.getChart().getView();

        for (int i = 0; i < plot.getSeriesCount(); i++) {
            String seriesLabel = plot.getSeriesLabel(i);
            int x2 = getLabelWidth(view, seriesLabel);
            if (x + x2 < axisBounds.width) {
                x += x2;
                b.width = Math.max(b.width, x);
            } else {
                b.height += getLabelHeight(view, "X");
                x = 0;
            }
        }
        b.x = 0;
        b.y = 0;
        b.height += getLabelHeight(view, "X");
        return b;
    }

    public void drawLegend(XYPlot xyplot, Layer layer, Bounds axisBounds, boolean gridOnly) {
        DefaultXYPlot plot = (DefaultXYPlot) xyplot;
        bounds = new Bounds(axisBounds);
        clearAxis(layer, axisBounds);
        drawZoomLinks(plot, layer, axisBounds);
        double x = axisBounds.x;
        double y = axisBounds.y + getLabelHeight(plot.getChart().getView(), "X") + 5;

        for (int i = 0; i < plot.getSeriesCount(); i++) {


            double width = drawLegendLabel(x, y, plot, layer, i, textLayerName);
            if (width < 0) {
                x = axisBounds.x;
                y += getLabelHeight(plot.getChart().getView(), "X");
                x += drawLegendLabel(x, y, plot, layer, i, textLayerName);
            } else {
                x += width;
            }
        }

    }

    private double drawLegendLabel(double x, double y, DefaultXYPlot plot, Layer layer, int seriesNum,
                                   String layerName) {
        String seriesLabel = plot.getSeriesLabel(seriesNum);
        XYRenderer renderer = plot.getRenderer(seriesNum);


        double height = getLabelHeight(plot.getChart().getView(), "X");
        double lWidth = this.getLabelWidth(plot.getChart().getView(), seriesLabel);

        if (x + lWidth >= bounds.x + bounds.width) {
            return -1;
        }

        Bounds b = renderer.drawLegendIcon(plot, layer, x, y + height / 2, seriesNum);

        layer.setStrokeColor(labelProperties.color);
        layer.drawText(x + b.width + 2, y, seriesLabel, labelProperties.fontFamily, labelProperties.fontWeight,
                       labelProperties.fontSize, layerName);
        return b.width + lWidth + 20;
    }

    private void clearAxis(Layer layer, Bounds bounds) {

        layer.save();
        layer.setFillColor(axisProperties.bgColor);
        layer.translate(0, bounds.y);
        layer.scale(layer.getWidth(), bounds.height);
        layer.beginPath();
        layer.rect(0, 0, 1, 1);
        layer.fill();
        layer.restore();
        layer.clearTextLayer(textLayerName);
    }

    private void drawZoomLinks(DefaultXYPlot plot, Layer layer, Bounds axisBounds) {
        layer.setStrokeColor(labelProperties.color);
        layer.drawText(axisBounds.x, axisBounds.y, ZOOM_STRING, labelProperties.fontFamily, labelProperties.fontWeight,
                       labelProperties.fontSize, textLayerName);
        int serNum = plot.getHoverSeries();
        int serPer = plot.getHoverPoint();

        if (serPer == -1) {
            serNum = plot.getFocusSeries();
            serPer = plot.getFocusPoint();
        }

        lastSerNum = serNum;
        lastSerPer = serPer;

        if (lastSerNum != -1 && lastSerPer != -1) {
            String val = String.valueOf(plot.getDataY(lastSerNum, lastSerPer));
            String status = "X: " + asDate(plot.getDataX(lastSerNum, lastSerPer)) + ", Y: " + val.substring(0, Math.min(
                    4, val.length()));
            int width = layer.stringWidth(status, labelProperties.fontFamily, labelProperties.fontWeight,
                                          labelProperties.fontSize);

            layer.drawText(axisBounds.x + axisBounds.width - width, axisBounds.y, status, labelProperties.fontFamily,
                           labelProperties.fontWeight, labelProperties.fontSize, textLayerName);
        } else {

            String status = asDate(plot.getDomainOrigin()) + " - " + asDate(
                    plot.getDomainOrigin() + plot.getCurrentDomain());
            int width = layer.stringWidth(status, labelProperties.fontFamily, labelProperties.fontWeight,
                                          labelProperties.fontSize);

            layer.drawText(axisBounds.x + axisBounds.width - width - 5, axisBounds.y, status,
                           labelProperties.fontFamily, labelProperties.fontWeight, labelProperties.fontSize,
                           textLayerName);
        }
    }

    private String asDate(double dataX) {
        long l = (long) dataX;
        Date d = new Date(l);
        return fmt(d.getMonth() + 1) + "/" + fmt(d.getDate()) + "/" + fmty(d.getYear());
    }

    private String fmty(int year) {
        return "" + ( year + 1900 );
    }

    private String fmt(int num) {
        return num < 10 ? "0" + num : "" + num;
    }


    // Warning, Warning, total hack ahead. This will have to do until a retained mode shape layer which sits atop
    // the Canvas abstraction can provide hit detection
    public boolean click(XYPlot plot, int x, int y) {
        if (legendStringHeight == -1) {
            Layer layer = plot.getChart().getView().getCanvas();
            legendStringHeight = layer.stringHeight(ZOOM_STRING, labelProperties.fontFamily, labelProperties.fontWeight,
                                                    labelProperties.fontSize);
            zoomStringWidth = zw(ZOOM_STRING, layer);
            zcolon = zw(ZOOM_COLON, layer);
            z1d = zw(ZOOM_1D, layer);
            z5d = zw(ZOOM_5D, layer);
            z1m = zw(ZOOM_1M, layer);
            z3m = zw(ZOOM_3M, layer);
            z6m = zw(ZOOM_6M, layer);
            z1y = zw(ZOOM_1Y, layer);
            z5y = zw(ZOOM_5Y, layer);
            z10y = zw(ZOOM_10Y, layer);
            zmax = zw(ZOOM_MAX, layer);
            zspace = zw("&nbsp;", layer);
        }
        if (y >= bounds.y && y <= bounds.y + legendStringHeight) {
            double bx = bounds.x;
            double be = bounds.x + zoomStringWidth;
            if (x >= bx && x <= be) {
                bx = bounds.x + zcolon + zspace;
                be = bx + z1d;
                if (x >= bx && x <= be) {
                    return zoom(plot, 86400);
                }
                bx += z1d + zspace;
                be = bx + z5d;
                if (x >= bx && x <= be) {
                    return zoom(plot, 86400 * 5);
                }
                bx += z5d + zspace;
                be = bx + z1m;
                if (x >= bx && x <= be) {
                    return zoom(plot, 86400 * 30);
                }
                bx += z1m + zspace;
                be = bx + z3m;
                if (x >= bx && x <= be) {
                    return zoom(plot, 86400 * 30 * 3);

                }
                bx += z3m + zspace;
                be = bx + z6m;
                if (x >= bx && x <= be) {
                    return zoom(plot, 86400 * 30 * 6);

                }
                bx += z6m + zspace;
                be = bx + z1y;
                if (x >= bx && x <= be) {
                    return zoom(plot, 86400 * 365);

                }
                bx += z1y + zspace;
                be = bx + z5y;
                if (x >= bx && x <= be) {
                    return zoom(plot, 86400 * 365 * 5);

                }
                bx += z5y + zspace;
                be = bx + z10y;
                if (x >= bx && x <= be) {
                    return zoom(plot, 86400 * 365 * 10);

                }
                bx += z10y + zspace;
                be = bx + zmax;
                if (x >= bx && x <= be) {
                    plot.maxZoomOut();
                    return true;

                }
                return false;
            }
        } else {
            return false;
        }
        return false;
    }

    private boolean zoom(XYPlot plot, int secs) {
        double cd = (double) secs * 1000;
        double dc = plot.getDomainOrigin() + plot.getCurrentDomain() / 2;
        plot.animateTo(dc - cd / 2, cd, XYPlotListener.ZOOMED, null);
        return true;
    }

    private int zw(String zs, Layer layer) {
        return layer.stringWidth(zs, labelProperties.fontFamily, labelProperties.fontWeight, labelProperties.fontSize);
    }
}
