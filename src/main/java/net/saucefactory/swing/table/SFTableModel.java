package net.saucefactory.swing.table;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

public class SFTableModel<T extends Object> extends AbstractTableModel
{
  protected SFTableColumn[] columns;
  protected SFTableColumn[] allColumns;
  protected Object[] tableData;
  protected int[] sortIndexArray = null;
  protected ISFTableEditAdapter editAdapter = null;

  public SFTableModel() {}

  public SFTableModel(SFTableColumn[] columns) {
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

  public T getObjectAtRow(int r) {
    if(tableData == null)
      return null;
    if(sortIndexArray != null && r < sortIndexArray.length)
      r = sortIndexArray[r];
    return (T)tableData[r];
  }

  public Object getValueIgnorSort(int r, int c) {
    if(tableData == null)
      return null;
    try {
      return columns[c].getValue(tableData[r]);
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

  public SFTableColumn getColumnForId(int id) {
    if(columns != null) {
      for(int i = 0; i < columns.length; i++) {
        if(columns[i].columnID == id)
          return columns[i];
      }
    }
    return null;
  }

  public int indexOfColumnId(int id) {
    if(columns != null) {
      for(int i = 0; i < columns.length; i++) {
        if(columns[i].columnID == id)
          return i;
      }
    }
    return -1;
  }

  public int getColumnWidth(int c) {
    if(columns == null)
      return 0;
    return columns[c].getColumnWidth();
  }

  public SFTableColumn[] getColumnHeaders() {
    return columns;
  }

  public SFTableColumn[] getAllColumnHeaders() {
    return allColumns;
  }

  public void removeTableColumn(SFTableColumn column, boolean fireEvent) {
    int removeIndex = -1;
    for(int i = 0; i < columns.length; i++) {
      if(columns[i] == column) {
        removeIndex = i;
        break;
      }
    }
    if(removeIndex > -1) {
      if(columns.length > 0) {
        SFTableColumn[] newColumns = new SFTableColumn[columns.length - 1];
        int rowCount = 0;
        for(int i = 0; i < columns.length; i++) {
          if(i != removeIndex) {
            newColumns[rowCount] = columns[i];
            rowCount++;
          }
        }
        columns = newColumns;
      }
      else {
        columns = new SFTableColumn[0];
      }
      if(fireEvent) {
        fireTableStructureChanged();
        fireTableChanged(new TableModelEvent(this));
      }
    }
  }

  public void addTableColumn(SFTableColumn column, boolean fireEvent) {
    boolean found = false;
    for(int i = 0; i < columns.length; i++) {
      if(columns[i] == column) {
        found = true;
        break;
      }
    }
    if(!found) {
      SFTableColumn[] newColumns = new SFTableColumn[columns.length + 1];
      for(int i = 0; i < columns.length; i++)
        newColumns[i] = columns[i];
      newColumns[columns.length] = column;
      columns = newColumns;
      if(fireEvent) {
        fireTableStructureChanged();
        fireTableChanged(new TableModelEvent(this));
      }
    }
  }

  public SFTableColumn getColumnHeader(int column) {
    if(columns != null && column > -1 && column < columns.length)
      return columns[column];
    return null;
  }

  public void clearTableData() {
    tableData = null;
    fireTableChanged(new TableModelEvent(this));
  }

  public void clear() {
    columns = null;
    tableData = null;
    fireTableChanged(new TableModelEvent(this));
  }

  public void updateTable() {
    fireTableChanged(new TableModelEvent(this));
  }

  public void setColumnHeaders(SFTableColumn[] columns) {
    setColumnHeaders(columns, true);
  }

  public void setColumnHeaders(SFTableColumn[] columns, boolean allColumns) {
    this.columns = columns;
    if(allColumns)
      this.allColumns = columns;
    //fireTableChanged(new TableModelEvent(this));
  }

  public void setEditAdapter(ISFTableEditAdapter editAdapter) {
    this.editAdapter = editAdapter;
  }

  public void setTableData(Object[] tableData) {
    this.tableData = tableData;
    fireTableChanged(new TableModelEvent(this));
  }

  public Object[] getTableData() {
    return tableData;
  }

  public Class<?> getColumnClass(int column) {
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
    if(editAdapter != null)
      return editAdapter.isCellEditable(r, c);
    if(columns != null)
      return columns[c].getEditable();
    return false;
  }

  public boolean internalIsCellEditable(int r, int c) {
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
      columns[c].setValue(tableData[r], newVal);
    }
    catch(Exception e) {
    }
  }
}
