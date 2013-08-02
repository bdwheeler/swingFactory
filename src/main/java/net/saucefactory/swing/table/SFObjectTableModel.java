package net.saucefactory.swing.table;

/**
 * Title:        SLIC Application
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1.0
 */

import javax.swing.table.*;
import javax.swing.event.*;

public class SFObjectTableModel extends AbstractTableModel
{
  protected SFObjectTableColumn[] columns;
  protected Object[] tableData;
  protected int[] sortIndexArray = null;

  public SFObjectTableModel() {}

  public SFObjectTableModel(SFObjectTableColumn[] columns) {
    super();
    setColumnHeaders(columns);
  }

  public void setSortIndexArray(int[] sortIndexArray) {
    this.sortIndexArray = sortIndexArray;
  }

  public int[] getSortIndexArray() {
    return this.sortIndexArray;
  }

  public int getRowCount() {
    if(tableData == null)
      return 0;
    return tableData.length;
  }

  public int getColumnCount() {
    if(columns == null)
      return 0;
    return columns.length;
  }

  public Object getValueAt(int r, int c) {
    if(tableData == null)
      return null;
    if(sortIndexArray != null && r < sortIndexArray.length)
      r = sortIndexArray[r];
    return getValueIgnorSort(r, c);
  }

  public Object getObjectAtRow(int r) {
    if(tableData == null)
      return null;
    if(sortIndexArray != null && r < sortIndexArray.length)
      r = sortIndexArray[r];
    return tableData[r];
  }

  public Object getValueIgnorSort(int r, int c) {
    if(tableData == null)
      return null;
    try {
      return tableData[r].getClass().getField(columns[c].getFieldName()).get(tableData[r]);
    }
    catch(Exception e) {
      return null;
    }
  }

  public String getColumnName(int c) {
    if(columns == null)
      return "";
    return columns[c].getColumnName();
  }

  public int getColumnID(int c) {
    if(columns == null)
      return -1;
    return columns[c].getColumnID();
  }

  public int getColumnWidth(int c) {
    if(columns == null)
      return 0;
    return columns[c].getColumnWidth();
  }

  public SFObjectTableColumn[] getColumnHeaders() {
    return columns;
  }

  public void clear() {
    columns = null;
    tableData = null;
    fireTableChanged(new TableModelEvent(this));
  }

  public void updateTable() {
    fireTableChanged(new TableModelEvent(this));
  }

  public void setColumnHeaders(SFObjectTableColumn[] columns) {
    this.columns = columns;
    fireTableChanged(new TableModelEvent(this));
  }

  public void setTableData(Object[] tableData) {
    this.tableData = tableData;
    fireTableChanged(new TableModelEvent(this));
  }

  public Object[] getTableData() {
    return tableData;
  }

  public Class getColumnClass(int column) {
    if(tableData != null) {
      for(int i = 0; i < tableData.length; i++) {
        Object tmpObj = getValueIgnorSort(i, column);
        if(tmpObj != null)
          return tmpObj.getClass();
      }
    }
    return new String().getClass();
  }

  public boolean isCellEditable(int r, int c) {
    if(columns != null)
      return columns[c].getEditable();
    return false;
  }

  public void setValueAt(Object newVal, int r, int c) {
    if(tableData == null)
      return;
    if(sortIndexArray != null && r < sortIndexArray.length)
      r = sortIndexArray[r];
    try {
      tableData[r].getClass().getField(columns[c].getFieldName()).set(tableData[r], newVal);
    }
    catch(Exception e) {
    }
  }
}
