package net.saucefactory.swing.common.adapters;

import java.awt.Color;
import java.awt.Component;

public class SFStripeColorAdapter implements ISFColorAdapter {

  private Color oddBackground = Color.white;
  private Color evenBackground = Color.white;
  private Color oddForeground = Color.black;
  private Color evenForeground = Color.black;
  private Color selectionForeground = Color.black;
  private Color selectionBackground = Color.white;

  public SFStripeColorAdapter() {}

  public void setOddBackground(Color oddBackground) {
    this.oddBackground = oddBackground;
  }

  public void setEvenBackground(Color evenBackground) {
    this.evenBackground = evenBackground;
  }

  public void setOddForeground(Color oddForeground) {
    this.oddForeground = oddForeground;
  }

  public void setEvenForeground(Color evenForeground) {
    this.evenForeground = evenForeground;
  }

  public void setSelectionBackground(Color selectionBackground) {
    this.selectionBackground = selectionBackground;
  }

  public void setSelectionForeground(Color selectionForeground) {
    this.selectionForeground = selectionForeground;
  }

  public Color getForeground(Component parent, boolean isSelected, boolean hasFocus, int row, int col) {
    if(isSelected)
      return selectionForeground;
    return isEven(row) ? evenForeground : oddForeground;
  }

  public Color getBackground(Component parent, boolean isSelected, boolean hasFocus, int row, int col) {
    if(isSelected)
      return selectionBackground;
    return isEven(row) ? evenBackground : oddBackground;
  }

  private boolean isEven(int i) {
    return (i % 2 == 0);
  }
}
