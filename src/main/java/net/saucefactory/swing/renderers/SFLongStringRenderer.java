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
import net.saucefactory.swing.table.*;
import net.saucefactory.swing.common.SFMultiLineLabel;

public class SFLongStringRenderer extends SFMultiLineLabel implements ISFTableRenderer
{
  int startRowHeight;
  SFStringRenderer shortRenderer = new SFStringRenderer();

  public SFLongStringRenderer() {
    this(SwingConstants.LEFT, SwingConstants.TOP);
  }

  public SFLongStringRenderer(int hAlign, int vAlign) {
    shortRenderer.setOpaque(true);
    shortRenderer.setVerticalAlignment(vAlign);
    shortRenderer.setHorizontalAlignment(hAlign);
    shortRenderer.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
    setOpaque(true);
    setVerticalAlignment(vAlign);
    setHorizontalAlignment(hAlign);
    setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
  }

  public Component getTableCellRendererComponent(Font font, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    setFont(font);
    if(((ISFTable)table).hasColorAdapter()) {
        ISFTableColorAdapter adapter = ((ISFTable)table).getColorAdapter();
        setForeground(adapter.getForeground(table, isSelected, hasFocus, row, column));
        setBackground(adapter.getBackground(table, isSelected, hasFocus, row, column));
    }
    else {
      if(isSelected && (!hasFocus || (hasFocus && !table.isCellEditable(row, column)))) {
        setForeground(table.getSelectionForeground());
        setBackground(table.getSelectionBackground());
      }
      else {
        setForeground(table.getForeground());
        setBackground(table.getBackground());
      }
    }
    int lineCount = getLineCount();
    if(lineCount > 1) {
      int newHeight = getTextHeight();
      if(newHeight > table.getRowHeight(row))
        table.setRowHeight(row, newHeight + 1);
    }
    else
      return shortRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
          column);
    return this;
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    return getTableCellRendererComponent(table.getFont(), table, value, isSelected, hasFocus, row, column);
  }

  public String getStringValue(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    return value == null ? "" : value.toString();
  }

}
