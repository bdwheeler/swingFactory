package net.saucefactory.swing.renderers;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.BorderFactory;
import net.saucefactory.swing.table.ISFTable;
import net.saucefactory.swing.table.ISFTableColorAdapter;
import javax.swing.ImageIcon;

public class SFIconRenderer extends JLabel implements ISFTableRenderer {

  private ImageIcon defaultIcon = null;

  public SFIconRenderer() {
    this(SwingConstants.LEFT, SwingConstants.TOP);
  }

  public SFIconRenderer(int hAlign, int vAlign) {
    setOpaque(true);
    setVerticalAlignment(vAlign);
    setHorizontalAlignment(hAlign);
    setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
  }

  public void setDefaultIcon(ImageIcon defaultIcon) {
    this.defaultIcon = defaultIcon;
  }

  public ImageIcon getDefaultIcon() {
    return defaultIcon;
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
    if(value instanceof ImageIcon)
      setIcon((ImageIcon)value);
    else
      setIcon(getIcon(table, value, isSelected, hasFocus, row, column));
    return this;
  }

  public ImageIcon getIcon(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    return defaultIcon;
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    return getTableCellRendererComponent(table.getFont(), table, value, isSelected, hasFocus, row, column);
  }

  public String getStringValue(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    return value == null ? "" : value.toString();
  }

}
