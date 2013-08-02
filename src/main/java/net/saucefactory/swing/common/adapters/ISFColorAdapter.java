package net.saucefactory.swing.common.adapters;

import java.awt.Color;
import java.awt.Component;

public interface ISFColorAdapter {
  public Color getForeground(Component parent, boolean isSelected, boolean hasFocus, int row, int col);
  public Color getBackground(Component parent, boolean isSelected, boolean hasFocus, int row, int col);
}
