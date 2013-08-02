package net.saucefactory.swing.renderers;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class SFStringListRenderer extends JLabel implements ListCellRenderer {
  public SFStringListRenderer() {
    setOpaque(true);
    setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 0));
  }

  public Component getListCellRendererComponent(JList list, Object value, int index,
      boolean isSelected, boolean cellHasFocus) {
    if(isSelected && list.isEnabled()) {
      setForeground(list.getSelectionForeground());
      setBackground(list.getSelectionBackground());
    }
    else {
      setForeground(list.getForeground());
      setBackground(list.getBackground());
    }
    setEnabled(list.isEnabled());
    if(value == null)
      setText(" ");
    else {
      setText((String)value);
    }
    return this;
  }
}
