package net.saucefactory.swing.table;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import net.saucefactory.swing.common.SFMetalTheme;

public class SFTableHeaderRenderer extends JLabel implements TableCellRenderer {
  int sortIndex = 0;
  private Color fgColor = null;
  private Color bgColor = null;

  public SFTableHeaderRenderer() {
    this(JLabel.LEFT, null, null);
  }

  public SFTableHeaderRenderer(int cellAllignment) {
    this(cellAllignment, null, null);
  }

  public Color getFgColor() {
    return fgColor == null ? UIManager.getColor("TableHeader.foreground") : fgColor;
  }

  public Color getBgColor() {
    return bgColor == null ? UIManager.getColor("TableHeader.background") : bgColor;
  }

  public SFTableHeaderRenderer(int cellAllignment, Color fgColor, Color bgColor) {
    setOpaque(true);
    this.fgColor = fgColor;
    this.bgColor = bgColor;
    setForeground(fgColor);
    setBackground(bgColor);
    setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createEmptyBorder(0, 3, 0, 3)));
    setHorizontalAlignment(cellAllignment);
    setVerticalAlignment(SwingConstants.TOP);
  }

  public void setSortIndex(int index) {
    sortIndex = index;
  }

  public int getSortIndex() {
    return sortIndex;
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    if(value != null) {
      if(table != null)
        setFont(table.getTableHeader().getFont());
      setText("<html>" + (String)value + "</html>");
      if(table != null)
        column = table.convertColumnIndexToModel(column);
      int lastSort = 0;
      SFTableColumn tmpCol = null;
      if(table != null) {
        lastSort = ((SFTable)table).getLastSortLevel(column);
        tmpCol = ((SFTable)table).getTableModel().getColumnHeader(column);
      }
      boolean sortableColumn = tmpCol == null ? false : tmpCol.isSortable();
      setSortIndex(lastSort);
      if(!sortableColumn) {
        if(tmpCol != null)
          setToolTipText(tmpCol.getDisplayName() + " - Not Sortable");
        else
          setToolTipText(null);
        setBackground(getBgColor());
        setForeground(getFgColor());
      }
      else if(lastSort == 0) {
        if(tmpCol != null)
          setToolTipText(tmpCol.getDisplayName());
        else
          setToolTipText(null);
        setBackground(getBgColor());
        setForeground(getFgColor());
      }
      else {
        String toolString = lastSort > 0 ? "Ascending" : "Descending";
        if(lastSort == 1 || lastSort == -1) {
          setBackground(SFMetalTheme.SF_SORT_PRIMARY);
          setForeground(Color.white);
          toolString = "Sort: Primary " + toolString;
        }
        else if(lastSort == 2 || lastSort == -2) {
          setBackground(SFMetalTheme.SF_SORT_SECONDARY);
          setForeground(Color.white);
          toolString = "Sort: Secondary " + toolString;
        }
        else {
          setBackground(SFMetalTheme.SF_SORT_TERNARY);
          setForeground(Color.white);
          toolString = "Sort: Ternary " + toolString;
        }
        if(tmpCol != null)
          setToolTipText(tmpCol.getDisplayName() + " - " + toolString);
        else
          setToolTipText(toolString);
      }
    }
    else
      setText(" ");
    return this;
  }

  public void paint(Graphics g) {
    super.paint(g);
    int lastSort = getSortIndex();
    Dimension size = getSize();
    if(lastSort != 0) {
      int[] xPoints = new int[] {size.width - 9, size.width - 6, size.width - 3};
      int[] yPoints = lastSort > 0 ? new int[] {8, 2, 8} : new int[] {2, 8, 2};
      g.setColor(Color.black);
      g.fillPolygon(xPoints, yPoints, 3);
    }
  }
}
