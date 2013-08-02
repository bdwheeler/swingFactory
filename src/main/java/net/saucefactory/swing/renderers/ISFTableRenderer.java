package net.saucefactory.swing.renderers;

import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;

public interface ISFTableRenderer extends TableCellRenderer {

  public String getStringValue(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column);

}
