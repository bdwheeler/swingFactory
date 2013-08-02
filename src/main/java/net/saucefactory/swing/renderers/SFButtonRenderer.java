package net.saucefactory.swing.renderers;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;
import java.lang.reflect.*;
import net.saucefactory.swing.table.*;

public class SFButtonRenderer extends JButton implements ISFTableRenderer
{
  public SFButtonRenderer() {
    this(SwingConstants.LEFT, SwingConstants.TOP);
  }

  public SFButtonRenderer(int hAlign, int vAlign) {
    setOpaque(true);
    setMargin(new Insets(1,1,1,1));
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
    setText((value == null ? "" : value.toString()));
    return this;
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    return getTableCellRendererComponent(table.getFont(), table, value, isSelected, hasFocus, row, column);
  }

  public String getStringValue(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    return value == null ? "" : value.toString();
  }

}
