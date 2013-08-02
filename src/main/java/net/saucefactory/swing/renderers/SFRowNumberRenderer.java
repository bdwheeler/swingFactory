package net.saucefactory.swing.renderers;

import java.awt.*;
import javax.swing.*;

public class SFRowNumberRenderer extends JLabel implements ISFTableRenderer {

  private Color fgColor = null;
  private Color bgColor = null;

  public SFRowNumberRenderer() {
    this(JLabel.LEFT, null, null);
  }

  public SFRowNumberRenderer(int cellAllignment) {
    this(cellAllignment, null, null);
  }

  public Color getFgColor() {
    return fgColor == null ? UIManager.getColor("TableHeader.foreground") : fgColor;
  }

  public Color getBgColor() {
    return bgColor == null ? UIManager.getColor("TableHeader.background") : bgColor;
  }

  public SFRowNumberRenderer(int cellAllignment, Color fgColor, Color bgColor) {
    setOpaque(true);
    this.fgColor = fgColor;
    this.bgColor = bgColor;
    setForeground(fgColor);
    setBackground(bgColor);
    setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createEmptyBorder(0, 3, 0, 3)));
    setHorizontalAlignment(cellAllignment);
    setVerticalAlignment(SwingConstants.TOP);
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    setFont(table.getTableHeader().getFont());
    setText(String.valueOf(row + 1));
    setBackground(getBgColor());
    setForeground(getFgColor());
    return this;
  }

  public String getStringValue(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    return String.valueOf(row + 1);
  }

}
