package net.saucefactory.swing.table;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import javax.swing.tree.TreeModel;

public interface ISFTableTreeModel extends TreeModel
  {
    public int getColumnCount();
    public String getColumnName(int column);
    public Class getColumnClass(int column);
    public Object getValueAt(Object node, int column);
    public boolean isCellEditable(Object node, int column);
    public void setValueAt(Object aValue, Object node, int column);
  }
