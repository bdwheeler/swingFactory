package net.saucefactory.swing.table;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import java.util.ArrayList;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class SFDefaultTableTreeModel implements ISFTableTreeModel
{
  protected Object root;
  protected ArrayList listeners = new ArrayList();

  public SFDefaultTableTreeModel(Object _root) {
    root = _root;
  }

  public Object getRoot() {
    return root;
  }

  public void setRoot(Object _root) {
    root = _root;
  }

  public boolean isLeaf(Object node) {
    return getChildCount(node) == 0;
  }

  public Class getColumnClass(int column) {
    return Object.class;
  }

  public boolean isCellEditable(Object node, int column) {
    return getColumnClass(column) == ISFTableTreeModel.class;
  }

  public void setValueAt(Object aValue, Object node, int column) {}

  public void valueForPathChanged(TreePath path, Object newValue) {}

  public Object getChild(Object parent, int index) {
    return ((TreeNode)parent).getChildAt(index);
  }

  public int getChildCount(Object parent) {
    return ((TreeNode)parent).getChildCount();
  }

  public int getColumnCount() {
    return 0;
  }

  public String getColumnName(int column) {
    return "";
  }

  public Object getValueAt(Object node, int column) {
    return null;
  }

  public int getIndexOfChild(Object parent, Object child) {
    int numKids = getChildCount(parent);
    for(int i = 0; i < numKids; i++) {
      if(getChild(parent, i).equals(child))
        return i;
    }
    return -1;
  }

  public void addTreeModelListener(TreeModelListener l) {
    listeners.add(l);
  }

  public void removeTreeModelListener(TreeModelListener l) {
    listeners.remove(l);
  }

  protected void fireTreeNodesChanged(Object source, Object[] path,
      int[] childIndices, Object[] children) {
    TreeModelListener[] aryListeners = new TreeModelListener[listeners.size()];
    aryListeners = (TreeModelListener[])listeners.toArray(aryListeners);
    TreeModelEvent e = null;
    for(int x = 0; x < aryListeners.length; ++x) {
      if(e == null)
        e = new TreeModelEvent(source, path, childIndices, children);
      aryListeners[x].treeNodesChanged(e);
    }
  }

  protected void fireTreeNodesInserted(Object source, Object[] path,
      int[] childIndices, Object[] children) {
    TreeModelListener[] aryListeners = new TreeModelListener[listeners.size()];
    aryListeners = (TreeModelListener[])listeners.toArray(aryListeners);
    TreeModelEvent e = null;
    for(int x = 0; x < aryListeners.length; ++x) {
      if(e == null)
        e = new TreeModelEvent(source, path, childIndices, children);
      aryListeners[x].treeNodesInserted(e);
    }
  }

  protected void fireTreeNodesRemoved(Object source, Object[] path,
      int[] childIndices, Object[] children) {
    TreeModelListener[] aryListeners = new TreeModelListener[listeners.size()];
    aryListeners = (TreeModelListener[])listeners.toArray(aryListeners);
    TreeModelEvent e = null;
    for(int x = 0; x < aryListeners.length; ++x) {
      if(e == null)
        e = new TreeModelEvent(source, path, childIndices, children);
      aryListeners[x].treeNodesRemoved(e);
    }
  }

  protected void fireTreeStructureChanged(Object source, Object[] path,
      int[] childIndices, Object[] children) {
    TreeModelListener[] aryListeners = new TreeModelListener[listeners.size()];
    if(listeners.size() > 0)
      aryListeners = (TreeModelListener[])listeners.toArray(aryListeners);
    TreeModelEvent e = null;
    for(int x = 0; x < aryListeners.length; ++x) {
      if(e == null)
        e = new TreeModelEvent(source, path, childIndices, children);
      aryListeners[x].treeStructureChanged(e);
    }
  }
}
