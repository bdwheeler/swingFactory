package net.saucefactory.swing.renderers;

import java.awt.*;
import javax.swing.*;
import net.saucefactory.swing.table.*;
import net.saucefactory.swing.common.SFMultiStateCheckbox;

public class SFMultiStateCheckRenderer extends SFMultiStateCheckbox implements ISFTableRenderer {

  public SFMultiStateCheckRenderer() {
    this(SwingConstants.LEFT, SwingConstants.TOP);
  }

  public SFMultiStateCheckRenderer(int hAlign, int vAlign) {
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
    if(value == null)
      setSelected(false);
    else {
      int state = ((Integer)value).intValue();
      if(state == STATE_OFF)
        setSelected(false);
      else if(state == STATE_ON)
        setSelected(true);
      else
        setBetween(true);
    }
    return this;
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    return getTableCellRendererComponent(table.getFont(), table, value, isSelected, hasFocus, row, column);
  }

  public String getStringValue(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    return value == null ? "False" : (((Integer)value).intValue() == STATE_OFF ? "False" : "True");
  }

}
