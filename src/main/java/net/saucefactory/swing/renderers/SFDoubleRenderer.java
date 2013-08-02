package net.saucefactory.swing.renderers;

/**
 * <p>Title: SLIC Application</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: SauceFactory Inc.</p>
 * @author Brian Wheeler
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;
import java.text.NumberFormat;

public class SFDoubleRenderer extends SFBaseRenderer implements ISFTableRenderer {
  public static NumberFormat nf;

  static {
    nf = NumberFormat.getNumberInstance();
    nf.setMaximumFractionDigits(2);
    nf.setMinimumFractionDigits(1);
    nf.setGroupingUsed(false);
  }

  public SFDoubleRenderer() {
    this(SwingConstants.LEFT, SwingConstants.TOP);
  }

  public SFDoubleRenderer(int hAlign, int vAlign) {
    super(hAlign, vAlign);
  }

  public Component getTableCellRendererComponent(Font font, JTable table, Object value,
      boolean isSelected,
      boolean hasFocus, int row, int column) {
    if(value != null && value instanceof Double)
      return super.getTableCellRendererComponent(font, table, nf.format(((Double)value).doubleValue()),
          isSelected, hasFocus, row, column);
    else
      return super.getTableCellRendererComponent(font, table, null, isSelected, hasFocus, row,
          column);
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    return getTableCellRendererComponent(table.getFont(), table, value, isSelected, hasFocus, row,
        column);
  }

  public String getStringValue(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    return (value != null && value instanceof Double) ? nf.format(((Double)value).doubleValue()) : "";
  }

}
