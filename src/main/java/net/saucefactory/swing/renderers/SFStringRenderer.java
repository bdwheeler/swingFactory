package net.saucefactory.swing.renderers;

/**
 * Title:        SLIC Application
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

public class SFStringRenderer extends SFBaseRenderer implements ISFTableRenderer
{
  public SFStringRenderer() {
    this(SwingConstants.LEFT, SwingConstants.TOP);
  }

  public SFStringRenderer(int hAlign, int vAlign) {
    super(hAlign, vAlign);
  }

  public Component getTableCellRendererComponent(Font font, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    return super.getTableCellRendererComponent(font, table, value, isSelected, hasFocus, row, column);
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    return getTableCellRendererComponent(table.getFont(), table, value, isSelected, hasFocus, row, column);
  }

}
