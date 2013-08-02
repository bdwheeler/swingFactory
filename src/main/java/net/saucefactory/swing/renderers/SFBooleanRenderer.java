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
import net.saucefactory.swing.table.*;

public class SFBooleanRenderer extends JCheckBox implements ISFTableRenderer {
  public SFBooleanRenderer() {
    this(SwingConstants.LEFT, SwingConstants.TOP);
  }

  public SFBooleanRenderer(int hAlign, int vAlign) {
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
      if(isSelected) {// && (!hasFocus || (hasFocus && !table.isCellEditable(row, column)))) {
        setForeground(table.getSelectionForeground());
        setBackground(table.getSelectionBackground());
      }
      else {
        setForeground(table.getForeground());
        setBackground(table.getBackground());
      }
    }
    setSelected((value == null ? false : ((Boolean)value).booleanValue()));
    return this;
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    return getTableCellRendererComponent(table.getFont(), table, value, isSelected, hasFocus, row, column);
  }

  public String getStringValue(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    return value == null ? "False" : (((Boolean)value).booleanValue() ? "True" : "False");
  }

}
