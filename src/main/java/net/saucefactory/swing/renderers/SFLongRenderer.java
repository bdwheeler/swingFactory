package net.saucefactory.swing.renderers;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;

public class SFLongRenderer extends SFBaseRenderer implements ISFTableRenderer
{
  public SFLongRenderer() {
    this(SwingConstants.LEFT, SwingConstants.TOP);
  }

  public SFLongRenderer(int hAlign, int vAlign) {
    super(hAlign, vAlign);
  }

  public Component getTableCellRendererComponent(Font font, JTable table, Object value,
      boolean isSelected,
      boolean hasFocus, int row, int column) {
    if(value != null && value instanceof Long)
      return super.getTableCellRendererComponent(font, table,
          String.valueOf(((Long)value).longValue()), isSelected, hasFocus, row, column);
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
    return (value != null && value instanceof Long) ? String.valueOf(((Long)value).longValue()) : "";
  }

}

