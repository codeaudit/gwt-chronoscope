package org.timepedia.chronoscope.client.gss;

import org.timepedia.chronoscope.client.canvas.Layer;
import org.timepedia.chronoscope.client.canvas.PaintStyle;
import org.timepedia.chronoscope.client.canvas.RadialGradient;
import org.timepedia.chronoscope.client.render.LinearGradient;

/**
 * Helper methods relating to GSS functionality.
 * 
 * @author chad takahashi
 */
public class GssUtil {
  private static final String LINGRADFULLURI
      = "http://timepedia.org/lineargradient/";

  private static final String RADGRADURI
      = "http://timepedia.org/radialgradient/";

  private static final String LINEGRADHASHURI = "#lineargradient/";

  private static final String RADGRADHASHURI = "#radialgradient/";

  public static PaintStyle createGradient(Layer layer, double w, double h,
      String backStr) {
    if (backStr.startsWith(LINGRADFULLURI)
        || backStr.indexOf(LINEGRADHASHURI) != -1) {
      return createLinearGradient(layer, 0.0, 0.0, 1.0, 1.0, backStr);
    } else {
      return createRadialGradient(layer, 0.0, 0.0, 1.0, 0.0, 0.0, w, backStr);
    }
  }

  private static LinearGradient createLinearGradient(Layer layer, double x1, double y1,
      double x2, double y2, String pointBackgroundStr) {
    String afterUrl = null;
    if (pointBackgroundStr.startsWith(LINGRADFULLURI)) {
      afterUrl = pointBackgroundStr.substring(
          pointBackgroundStr.indexOf(LINGRADFULLURI) + LINGRADFULLURI.length());
    } else {
      int ind = pointBackgroundStr.indexOf(LINEGRADHASHURI);
      afterUrl = pointBackgroundStr.substring(ind + LINEGRADHASHURI.length());
    }
    String[] pieces = afterUrl.split("/");
    String xy[] = pieces[0].split(",");
    String xy2[] = pieces[1].split(",");

    LinearGradient lingrad = layer.createLinearGradient(
        Double.parseDouble(xy[0]), Double.parseDouble(xy[1]),
        Double.parseDouble(xy2[0]), Double.parseDouble(xy2[1]));
    lingrad.addColorStop(Double.parseDouble(pieces[2]), "#" + pieces[3]);
    lingrad.addColorStop(Double.parseDouble(pieces[4]), "#" + pieces[5]);
    return lingrad;
  }

  private static PaintStyle createRadialGradient(Layer layer, double x0, double y0,
      double r0, double x1, double y1, double r1, String backStr) {
    String afterUrl = null;
    int i = backStr.indexOf(RADGRADURI);
    if (i != -1) {
      afterUrl = backStr
          .substring(backStr.indexOf(RADGRADURI) + RADGRADURI.length());
    } else {
      int ind = backStr.indexOf(RADGRADHASHURI);
      afterUrl = backStr.substring(ind + RADGRADHASHURI.length());
    }
    String[] pieces = afterUrl.split("/");
    String xyr[] = pieces[0].split(",");
    String xyr2[] = pieces[1].split(",");

    RadialGradient radialGradient = layer.createRadialGradient(
        Double.parseDouble(xyr[0]), Double.parseDouble(xyr[1]),
        Double.parseDouble(xyr[2]), Double.parseDouble(xyr2[0]),
        Double.parseDouble(xyr2[1]), Double.parseDouble(xyr2[2]));
    radialGradient.addColorStop(Double.parseDouble(pieces[2]), "#" + pieces[3]);
    radialGradient.addColorStop(Double.parseDouble(pieces[4]), "#" + pieces[5]);
    return radialGradient;
  }
}
