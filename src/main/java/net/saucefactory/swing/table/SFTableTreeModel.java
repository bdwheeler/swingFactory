package net.saucefactory.swing.table;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;


public class SFTableTreeModel extends SFTableModel {
  protected JTree tree;
  protected ISFTableTreeModel model;

  public SFTableTreeModel(ISFTableTreeModel model, JTree tree) {
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

