package net.saucefactory.swing.table;

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

public interface ISFTableColorAdapter {
  public Color getForeground(JTable table, boolean isSelected, boolean hasFocus, int row, int col);
  public Color getBackground(JTable table, boolean isSelected, boolean hasFocus, int row, int col);
}
