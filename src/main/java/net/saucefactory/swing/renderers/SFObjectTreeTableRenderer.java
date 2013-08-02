package net.saucefactory.swing.renderers;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import net.saucefactory.swing.table.*;
import javax.swing.tree.*;

public class SFObjectTreeTableRenderer extends JTree implements TableCellRenderer {
  private SFObjectTableTree parentTable;
  private int currentRow;
  private boolean dirtyPaint = true;

  public SFObjectTreeTableRenderer(SFObjectTableTree table, TreeModel model) {
    super(model);
    parentTable = table;
    setCellRenderer(new SFTreeCellStringRenderer());
  }

  public void setBounds(int x, int y, int w, int h) {
    super.setBounds(x, 0, w, parentTable.getHeight());
  }

  public void paint(Graphics g) {
    if(!dirtyPaint) {
      g.translate(0, -currentRow * getRowHeight());
      super.paint(g);
      dirtyPaint = true;
    }
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column) {
    setFont(table.getFont());
    try {
      SFObjectTable oTable = (SFObjectTable)table;
      ISFTableColorAdapter adapter = oTable.getColorAdapter();
      setForeground(adapter.getForeground(table, isSelected, hasFocus, row, column));
      setBackground(adapter.getBackground(table, isSelected, hasFocus, row, column));
    }
    catch(Exception e) {
      if(isSelected && (!hasFocus || (hasFocus && !table.isCellEditable(row, column)))) {
        setForeground(table.getSelectionForeground());
        setBackground(table.getSelectionBackground());
      }
      else {
        setForeground(table.getForeground());
        setBackground(table.getBackground());
      }
    }
    currentRow = row;//parentTable.convertRowToSort(row);
    dirtyPaint = false;
    return this;
  }

  public void setCurrentRow(int row) {
    this.currentRow = row;
  }

  public void expandOneLevel() {
    int count = getRowCount();
    for(int i = count; i > 0; i--)
      expandRow(i - 1);
  }

  public void collapseOneLevel() {
    for(int i = 0; i < getRowCount(); i++)
      collapseRow(i);
  }

  public void expandAllLevels() {
    int rowCount = getRowCount();
    if(rowCount > 0) {
      int lastRowCount = -1;
      while(lastRowCount != rowCount) {
        rowCount = getRowCount();
        for(int i = rowCount; i > 0; i--) {
          if(!isExpanded(i - 1)) {
            expandRow(i - 1);
          }
        }
        treeDidChange();
        lastRowCount = getRowCount();
      }
    }
  }

  public void collapseAllLevels() {
    int rowCount = getRowCount();
    if(rowCount > 0) {
      int lastRowCount = -1;
      while(lastRowCount != rowCount) {
        rowCount = getRowCount();
        for(int i = rowCount; i > 0; i--) {
          if(isExpanded(i - 1)) {
            collapseRow(i - 1);
          }
        }
        treeDidChange();
        lastRowCount = getRowCount();
      }
    }
  }

  public Object getCellValue(int row, int column) {
    return parentTable.getValueAt(parentTable.convertRowToSort(row), column);
  }

  class SFTreeCellStringRenderer extends DefaultTreeCellRenderer
  {
    public SFTreeCellStringRenderer() {
      super();
      setOpaque(true);
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
        boolean isSelected, boolean isExpanded, boolean isLeaf, int row, boolean hasFocus) {
      //isSelected = (row == parentTable.getSelectedRowWithSort());
      //row = parentTable.convertSortToRow(row);
      JLabel rtnLabel = (JLabel)super.getTreeCellRendererComponent(tree, value, isSelected,
          isExpanded, isLeaf, row, hasFocus);
      try {
        ISFTableColorAdapter adapter = parentTable.getColorAdapter();
        rtnLabel.setForeground(adapter.getForeground(parentTable, isSelected, hasFocus, row, 0));
        rtnLabel.setBackground(adapter.getBackground(parentTable, isSelected, hasFocus, row, 0));
      }
      catch(Exception e) {
        if(isSelected && (!hasFocus || (hasFocus && !parentTable.isCellEditable(row, 0)))) {
          rtnLabel.setForeground(parentTable.getSelectionForeground());
          rtnLabel.setBackground(parentTable.getSelectionBackground());
        }
        else {
          rtnLabel.setForeground(parentTable.getForeground());
          rtnLabel.setBackground(parentTable.getBackground());
        }
      }
      return rtnLabel;
    }
  }
}
