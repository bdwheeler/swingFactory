package net.saucefactory.swing.renderers;

import java.awt.*;
import javax.swing.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class SFLongDateRenderer extends SFBaseRenderer implements ISFTableRenderer {
  private static final String FORMAT_STRING = "MM/dd/yyyy hh:mm:ss a";
  private SimpleDateFormat sdf;

  public SFLongDateRenderer() {
    this(FORMAT_STRING, JLabel.LEFT, JLabel.TOP);
  }

  public SFLongDateRenderer(String format) {
    this(format, JLabel.LEFT, JLabel.TOP);
  }

  public SFLongDateRenderer(String format, int hAlign, int vAlign) {
    super(hAlign, vAlign);
    sdf = new SimpleDateFormat(format);
    setOpaque(true);
    setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
  }

  public Component getTableCellRendererComponent(Font font, JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column) {
    if(value != null && value instanceof Long)
      return super.getTableCellRendererComponent(font, table, sdf.format(new Date(((Long)value).longValue())), isSelected, hasFocus, row, column);
    else
      return super.getTableCellRendererComponent(font, table, null, isSelected, hasFocus, row, column);
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    return getTableCellRendererComponent(table.getFont(), table, value, isSelected, hasFocus, row, column);
  }

  public String getStringValue(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    return (value != null && value instanceof Long) ? sdf.format(new Date(((Long)value).longValue())) : "";
  }

}
