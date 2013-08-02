package net.saucefactory.swing.renderers;

/**
 * <p>Title: SLIC Application</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: CAISO</p>
 * @author Brian Wheeler
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;

public class SFIntegerRenderer extends SFBaseRenderer implements ISFTableRenderer {
  public SFIntegerRenderer() {
    this(SwingConstants.LEFT, SwingConstants.TOP);
  }

  public SFIntegerRenderer(int hAlign, int vAlign) {
    super(hAlign, vAlign);
  }

  public Component getTableCellRendererComponent(Font font, JTable table, Object value,
      boolean isSelected,
      boolean hasFocus, int row, int column) {
    if(value != null && value instanceof Integer)
      return super.getTableCellRendererComponent(font, table,
          String.valueOf(((Integer)value).intValue()), isSelected, hasFocus, row, column);
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
    return (value != null && value instanceof Integer) ? String.valueOf(((Integer)value).intValue()) : "";
  }

}
