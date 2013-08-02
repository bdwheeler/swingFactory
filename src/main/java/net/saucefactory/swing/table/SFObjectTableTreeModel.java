package net.saucefactory.swing.table;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

public class SFObjectTableTreeModel extends SFObjectTableModel {
  protected JTree tree;
  protected ISFTableTreeModel model;

  public SFObjectTableTreeModel(ISFTableTreeModel model, JTree tree) {
    this.tree = tree;
    this.model = model;
    initHandlers();
  }

  public void initHandlers() {
    tree.addTreeExpansionListener(new TreeExpansionListener() {
      public void treeExpanded(TreeExpansionEvent event) {
        fireTableDataChanged();
      }

      public void treeCollapsed(TreeExpansionEvent event) {
        fireTableDataChanged();
      }
    });

    model.addTreeModelListener(new TreeModelListener() {
      public void treeNodesChanged(TreeModelEvent e) {
        fireTableDataChanged();
      }

      public void treeNodesInserted(TreeModelEvent e) {
        fireTableDataChanged();
      }

      public void treeNodesRemoved(TreeModelEvent e) {
        fireTableDataChanged();
      }

      public void treeStructureChanged(TreeModelEvent e) {
        fireTableStructureChanged();
      }
    });
  }

  public int getColumnCount() {
    return model.getColumnCount();
  }

  public String getColumnName(int c) {
    return model.getColumnName(c);
  }

  public Class getColumnClass(int c) {
    return model.getColumnClass(c);
  }

  public int getRowCount() {
    return tree.getRowCount();
  }

  public Object getValueAt(int r, int c) {
    if(sortIndexArray != null) {
      if(r < sortIndexArray.length)
        r = sortIndexArray[r];
    }
    return getValueIgnorSort(r, c);
  }

  public Object getValueIgnorSort(int r, int c) {
    return model.getValueAt(nodeForRow(r), c);
  }

  public void setValueAt(Object o, int r, int c) {
    model.setValueAt(o, nodeForRow(r), c);
  }

  public boolean isCellEditable(int r, int c) {
    Object tmpObj = nodeForRow(r);
    if(tmpObj == null)
      return false;
    return model.isCellEditable(tmpObj, c);
  }

  public int getChildCount(int r) {
    return model.getChildCount(nodeForRow(r));
  }

  // get the object at the end of the row...
  public Object nodeForRow(int r) {
    TreePath treePath = tree.getPathForRow(r);
    if(treePath != null)
      return treePath.getLastPathComponent();
    return null;
  }

  public boolean isRowExpanded(int r) {
    return tree.isExpanded(r);
  }
}

