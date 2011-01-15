package org.timepedia.chronoscope.client.gss;

import org.timepedia.chronoscope.client.canvas.Color;
import org.timepedia.chronoscope.client.canvas.PaintStyle;
import org.timepedia.chronoscope.client.render.RangeAxisPanel.TickPosition;
import org.timepedia.chronoscope.client.render.RangeAxisPanel.TickAlignment;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

/**
 * A GSS analogue of CssProperties for a GssElement. Only a small subset of
 * properties are needed.
 */
@ExportPackage("chronoscope")
public class GssProperties implements Exportable {

  public PaintStyle bgColor = Color.WHITE;

  public Color color = Color.BLACK;

  public String fontFamily = "Helvetica";

  public String fontSize = "9pt";

  public String fontWeight = "normal";

  public int height;

  public int left = 0;

  public double lineThickness = 1;

  public double borderTop = -1;

  public double borderBottom = -1;

  public double borderLeft = -1;

  public double borderRight = -1;

  public double shadowBlur = 0;

  public Color shadowColor = Color.LIGHTGRAY;

  public double shadowOffsetX = 0;

  public double shadowOffsetY = 0;

  public double size = 5;

  public String tickAlign = TickAlignment.MIDDLE.toString();

  public String tickPosition = TickPosition.OUTSIDE.toString();

  public int top = 0;

  public double transparency = 1.0;

  public boolean visible = true;

  public int width = 1;

  public String pointShape = "circle";

  public String display = "auto";

  public String dateFormat = null;

  public String numberFormat = null;

  public String group = null;
  
  public boolean gssSupplied = false;
  
  public String pointSelection = "domain";

  public String columnWidth = "auto";

  public String columnCount = "auto";

  public boolean columnAligned = false;

  public String iconWidth = "auto";

  public String iconHeight = "auto";

  public boolean valueVisible = false;

  public boolean labelVisible = true;

  public GssProperties setColor(Color color) {
    this.color = color;
    return this;
  }

  public GssProperties setTransparency(double transparency) {
    this.transparency = transparency;
    return this;
  }

  public String toString() {
    return "visible:" + visible + "\ncolor:" + color + "\nbgColor:" + bgColor
        + "\nlineThickness:" + lineThickness + "\nshadowBlur:" + shadowBlur
        + "\nshadowOffsetX:" + shadowOffsetX + "\nshadowOffsetY:"
        + shadowOffsetY + "\nshadowColor:" + shadowColor + "\nwidth:" + width
        + "\ntransparency:" + transparency + "\nsize:" + size + "\nleft:"
        + left + "\ntop:" + top
        + "\niconWidth:" + iconWidth + "\niconHeight:" + iconHeight
        + "\ncolumnWidth:" + columnWidth + "\ncolumnCount:" + columnCount + "\ncolumnAligned:" + columnAligned;
  }

  @Export
  public String getDateFormat() {
    return dateFormat;
  }

  @Export
  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }

  @Export
  public String getNumberFormat() {
    return numberFormat;
  }

  @Export
  public void setNumberFormat(String numberFormat) {
    this.numberFormat = numberFormat;
  }

  public String getFontFamily() {
    return fontFamily;
  }

  @Export
  public void setFontFamily(String fontFamily) {
    this.fontFamily = fontFamily;
  }

  public String getFontSize() {
    return fontSize;
  }

  @Export
  public void setFontSize(String fontSize) {
    this.fontSize = fontSize;
  }

  public String getFontWeight() {
    return fontWeight;
  }

  @Export
  public void setFontWeight(String fontWeight) {
    this.fontWeight = fontWeight;
  }

  public String getGroup() {
    return group;
  }

  @Export
  public void setGroup(String group) {
    this.group = group;
  }

  public int getHeight() {
    return height;
  }

  @Export
  public void setHeight(int height) {
    this.height = height;
  }

  public double getLineThickness() {
    return lineThickness;
  }

  @Export
  public void setLineThickness(double lineThickness) {
    this.lineThickness = lineThickness;
  }

  public int getLeft() {
    return left;
  }

  @Export
  public void setLeft(int left) {
    this.left = left;
  }

  public String getPointSelection() {
    return pointSelection;
  }

  @Export
  public void setPointSelection(String pointSelection) {
    this.pointSelection = pointSelection;
  }

  public String getPointShape() {
    return pointShape;
  }

  @Export
  public void setPointShape(String pointShape) {
    this.pointShape = pointShape;
  }

  public double getShadowBlur() {
    return shadowBlur;
  }

  @Export
  public void setShadowBlur(double shadowBlur) {
    this.shadowBlur = shadowBlur;
  }

  public double getShadowOffsetX() {
    return shadowOffsetX;
  }

  @Export
  public void setShadowOffsetX(double shadowOffsetX) {
    this.shadowOffsetX = shadowOffsetX;
  }

  public Color getShadowColor() {
    return shadowColor;
  }

  @Export
  public void setShadowColor(Color shadowColor) {
    this.shadowColor = shadowColor;
  }

  public double getShadowOffsetY() {
    return shadowOffsetY;
  }

  @Export
  public void setShadowOffsetY(double shadowOffsetY) {
    this.shadowOffsetY = shadowOffsetY;
  }

  public double getSize() {
    return size;
  }

  @Export
  public void setSize(double size) {
    this.size = size;
  }

  public String getTickAlign() {
    return tickAlign;
  }

  @Export
  public void setTickAlign(String tickAlign) {
    this.tickAlign = tickAlign;
  }

  public String getTickPosition() {
    return tickPosition;
  }

  @Export
  public void setTickPosition(String tickPosition) {
    this.tickPosition = tickPosition;
  }

  public int getTop() {
    return top;
  }

  @Export  
  public void setTop(int top) {
    this.top = top;
  }

  public boolean isVisible() {
    return visible;
  }

  @Export
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public int getWidth() {
    return width;
  }

  @Export
  public void setWidth(int width) {
    this.width = width;
  }

  @Export
  public void setValueVisibility(boolean visibility) {
    this.valueVisible = visibility;
  }

  public boolean getValueVisibility() {
    return valueVisible;
  }

  @Export
  public void setLabelVisibility(boolean visibility) {
    this.valueVisible = visibility;
  }

  public boolean getLabelVisibility() {
    return valueVisible;
  }
}
