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
import net.saucefactory.swing.SFSpeedLabel;
import net.saucefactory.swing.common.SFMetalTheme;

public class SFPanelListCellRenderer implements ListCellRenderer {
  public SFPanelListCellRenderer() {}

  public Component getListCellRendererComponent(JList list, Object value, int index,
      boolean isSelected, boolean cellHasFocus) {
    if(value != null && value instanceof JPanel) {
      if(isSelected)
        ((JPanel)value).setBackground(SFMetalTheme.YELLOW_MEDIUM);
      else
        ((JPanel)value).setBackground(list.getBackground());
      return(JPanel)value;
    }
    else {
      SFSpeedLabel tmpLabel = new SFSpeedLabel();
      tmpLabel.setText("Error");
      return tmpLabel;
    }
  }
}
