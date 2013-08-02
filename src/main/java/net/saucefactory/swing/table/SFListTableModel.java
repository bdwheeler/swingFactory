package net.saucefactory.swing.table;

import java.util.List;

import javax.swing.event.TableModelEvent;


public class SFListTableModel<T extends Object> extends SFTableModel<T> {
  
	private static final long serialVersionUID = 1L;
	
  protected List<T> tableData;
  
  public SFListTableModel() {}

  public SFListTableModel(SFTableColumn[] columns) {
    super();
    setColumnHeaders(columns);
  }

  public int getRowCount() {
    if(tableData == null)
      return 0;
    return tableData.size();
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
    return tableData.get(r);
  }

  public Object getValueIgnorSort(int r, int c) {
    if(tableData == null)
      return null;
    try {
      return columns[c].getValue(tableData.get(r));
    }
    catch(Exception e) {
      return null;
    }
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

  public void setTableData(List<T> tableData) {
    this.tableData = tableData;
    fireTableChanged(new TableModelEvent(this));
  }

  public List<T> getListTableData() {
    return tableData;
  }
  
  public Object[] getTableData() {
  	if(tableData == null)
  		return null;
  	Object[] rtn = new Object[tableData.size()];
  	int cnt = 0;
  	for(T tmpObj : tableData) {
  		rtn[cnt] = tmpObj;
  		cnt++;
  	}
  	return rtn;
  }

  public void setValueAt(Object newVal, int r, int c) {
    if(tableData == null)
      return;
    if(sortIndexArray != null && r < sortIndexArray.length)
      r = sortIndexArray[r];
    try {
      columns[c].setValue(tableData.get(r), newVal);
    }
    catch(Exception e) {
    }
  }
}
