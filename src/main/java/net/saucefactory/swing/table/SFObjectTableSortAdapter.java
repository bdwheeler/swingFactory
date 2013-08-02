package net.saucefactory.swing.table;

/**
 * <p>Title: Sauce Factory Libraries</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Sauce Factory Inc</p>
 * @author Jeremy Leng
 * @version 1.0
 */

import java.lang.*;
import java.util.*;
import javax.swing.event.TableModelEvent;
import java.awt.event.*;
import javax.swing.JTable;
import javax.swing.table.*;

public class SFObjectTableSortAdapter {
  int[] indexes;
  LinkedList sortingColumns = new LinkedList(); //Vector sortingColumns = new Vector();
  //boolean ascending = true;
  int compares;
  public static final int MAX_SORT_DEPTH = 3;
  SFObjectTableModel tableModel;
  SFObjectTable mainTable;
  HeaderMouseListener headerMouseListener = null;
  int sortDepth = MAX_SORT_DEPTH;
  boolean[] sortOrder = null;

  public SFObjectTableSortAdapter() {
    indexes = new int[0];
  }

  public SFObjectTableSortAdapter(SFObjectTable table) {
    this.mainTable = table;
    this.tableModel = table.getTableModel();
    reallocateIndexes();
  }

  public void setTable(SFObjectTable table) {
    this.mainTable = table;
    this.tableModel = table.getTableModel();
    reallocateIndexes();
  }

  public void setSortOrderArray(boolean[] sortOrder) {
    this.sortOrder = sortOrder;
  }

  public void setTableModel(SFObjectTableModel model) {
    this.tableModel = model;
  }

  public void setSortDepth(int sortDepth) {
    this.sortDepth = sortDepth;
  }

  public int compareRowsByColumn(int row1, int row2, int column) {
    Class type = tableModel.getColumnClass(column);
    // Check for nulls.
    Object o1 = tableModel.getValueIgnorSort(row1, column);
    Object o2 = tableModel.getValueIgnorSort(row2, column);

    // If both values are null, return 0.
    if(o1 == null && o2 == null)
      return 0;
    else if(o1 == null)
      return -1;
    else if(o2 == null)
      return 1;

    if(type.getSuperclass() == java.lang.Number.class) {
      double d1 = ((Number)o1).doubleValue();
      double d2 = ((Number)o2).doubleValue();
      if(d1 < d2)
        return -1;
      else if(d1 > d2)
        return 1;
      else
        return 0;
    }
    else if(type == java.util.Date.class) {
      long n1 = ((Date)o1).getTime();
      long n2 = ((Date)o2).getTime();
      if(n1 < n2)
        return -1;
      else if(n1 > n2)
        return 1;
      else
        return 0;
    }
    else if(type == String.class) {
      String s1 = (String)o1;
      String s2 = (String)o2;
      int result = s1.compareToIgnoreCase(s2);
      if(result < 0)
        return -1;
      else if(result > 0)
        return 1;
      else
        return 0;
    }
    else if(type == Boolean.class) {
      boolean b1 = ((Boolean)o1).booleanValue();
      boolean b2 = ((Boolean)o2).booleanValue();
      if(b1 == b2)
        return 0;
      else if(b1)
        return 1;
      else
        return -1;
    }
    else {
      return doSpecialCompare(row1, row2, column);
    }
  }

  public int doSpecialCompare(int row1, int row2, int column) {
    String s1 = mainTable.getStringValueAtIgnoreSort(row1, column, false);
    String s2 = mainTable.getStringValueAtIgnoreSort(row2, column, false);
    int result = s1.compareToIgnoreCase(s2);
    if(result < 0)
      return -1;
    else if(result > 0)
      return 1;
    else
      return 0;
  }

  public int compare(int row1, int row2) {
    compares++;
    for(int level = 0; level < sortingColumns.size(); level++) {
      SortingColumn column = (SortingColumn)sortingColumns.get(level);
      int result = compareRowsByColumn(row1, row2, column.columnIndex);
      if(result != 0)
        return column.ascending ? result : -result;
    }
    return 0;
  }

  public void reallocateIndexes() {
    int rowCount = tableModel.getRowCount();
    if(rowCount > 0)
      indexes = new int[rowCount];
    else
      indexes = new int[0];
    for(int row = 0; row < rowCount; row++)
      indexes[row] = row;
    tableModel.setSortIndexArray(indexes);
  }

  public void tableChanged(TableModelEvent e) {
    reallocateIndexes();
    tableModel.fireTableChanged(new TableModelEvent(tableModel));
  }

  public void checkModel() {
    if(indexes.length != tableModel.getRowCount()) {
      reallocateIndexes();
      //System.err.println("Sorter not informed of a change in model.");
    }
  }

  public void sort(Object sender) {
    checkModel();
    compares = 0;
    // n2sort();
    // qsort(0, indexes.length-1);
    shuttlesort((int[])indexes.clone(), indexes, 0, indexes.length);
  }

  public void n2sort() {
    for(int i = 0; i < tableModel.getRowCount(); i++) {
      for(int j = i + 1; j < tableModel.getRowCount(); j++) {
        if(compare(indexes[i], indexes[j]) == -1)
          swap(i, j);
      }
    }
  }

  public void shuttlesort(int from[], int to[], int low, int high) {
    if(high - low < 2)
      return;
    int middle = (low + high) / 2;
    shuttlesort(to, from, low, middle);
    shuttlesort(to, from, middle, high);

    int p = low;
    int q = middle;

    if(high - low >= 4 && compare(from[middle - 1], from[middle]) <= 0) {
      for(int i = low; i < high; i++)
        to[i] = from[i];
      return;
    }
    // A normal merge.
    for(int i = low; i < high; i++) {
      if(q >= high || (p < middle && compare(from[p], from[q]) <= 0))
        to[i] = from[p++];
      else
        to[i] = from[q++];
    }
  }

  public void swap(int i, int j) {
    int tmp = indexes[i];
    indexes[i] = indexes[j];
    indexes[j] = tmp;
  }

  //public void sortByColumn(int column)
  //{
  //sortByColumn(column, true);
  //}

  public void redoLastSort() {
    sort(this);
    tableModel.setSortIndexArray(indexes);
    tableModel.fireTableChanged(new TableModelEvent(tableModel));
  }

  public int getLastSortLevel(int column) {
    int rtn = 0;
    for(int i = 0; i < sortingColumns.size(); i++) {
      SortingColumn tmpCol = (SortingColumn)sortingColumns.get(i);
      if(tmpCol.columnIndex == column) {
        rtn = i + 1;
        if(!tmpCol.ascending)
          rtn = -rtn;
      }
    }
    return rtn;
  }

  public int getLastSortColumn() {
    int rtnInt = -1;
    if(sortingColumns.size() > 0) {
      SortingColumn tmpCol = (SortingColumn)sortingColumns.getFirst();
      rtnInt = tmpCol.columnIndex;
    }
    return rtnInt;
  }

  public void sortByColumn(int column) { //, boolean ascending)
    //this.ascending = ascending;
    //sortingColumns.removeAllElements();
    //sortingColumns.addElement(new Integer(column));
    addSortColumn(column);
    sort(this);
    tableModel.setSortIndexArray(indexes);
    tableModel.fireTableChanged(new TableModelEvent(tableModel));
    //Set Last sort column
    //column += 1;
    //if(ascending)
    //column = 0 - column;
    //tableModel.setLastSortColumn(column);
  }

  public void addSortColumn(int column, boolean ascending) {
    boolean found = false;
    for(int i = 0; i < sortingColumns.size(); i++) {
      SortingColumn tmpCol = (SortingColumn)sortingColumns.get(i);
      if(tmpCol.columnIndex == column) {
        found = true;
        if(i == 0) {
          tmpCol.ascending = ascending;
        }
        else {
          sortingColumns.remove(i);
          tmpCol.ascending = ascending;
          sortingColumns.addFirst(tmpCol);
        }
      }
    }
    if(!found) {
      if(sortingColumns.size() >= sortDepth)
        sortingColumns.removeLast();
      sortingColumns.addFirst(new SortingColumn(column, ascending));
    }
  }

  public void addSortColumn(int column) { //, boolean ascending) {
    boolean found = false;
    for(int i = 0; i < sortingColumns.size(); i++) {
      SortingColumn tmpCol = (SortingColumn)sortingColumns.get(i);
      if(tmpCol.columnIndex == column) {
        found = true;
        if(i == 0) {
          tmpCol.ascending = !tmpCol.ascending;
        }
        else {
          sortingColumns.remove(i);
          sortingColumns.addFirst(tmpCol);
        }
      }
    }
    if(!found) {
      if(sortingColumns.size() >= sortDepth)
        sortingColumns.removeLast();
      boolean direction = sortOrder != null && sortOrder.length > column ? sortOrder[column] : true;
      sortingColumns.addFirst(new SortingColumn(column, direction));
    }
  }

  public void resetSortColumns() {
    sortingColumns.clear();
  }

  public void addTableHeaderMouseListener() {
    mainTable.setColumnSelectionAllowed(false);
    JTableHeader th = mainTable.getTableHeader();
    if(headerMouseListener == null) {
      headerMouseListener = new HeaderMouseListener();
      headerMouseListener.setHeader(th);
    }
    else {
      headerMouseListener.setHeader(th);
    }
  }

  public static void main(String[] args) {
    try {
      SFObjectTableSortAdapter a = new SFObjectTableSortAdapter();
    }
    catch(Exception e) {
      System.out.println("error in sort adapter");
      e.printStackTrace();
    }
  }

  public class SortingColumn {
    int columnIndex;
    boolean ascending;

    public SortingColumn(int _columnIndex, boolean _ascending) {
      columnIndex = _columnIndex;
      ascending = _ascending;
    }
  }

  public class HeaderMouseListener extends MouseAdapter {
    JTableHeader header = null;

    public HeaderMouseListener() {
    }

    public void setHeader(JTableHeader _header) {
      uninstallListener();
      header = _header;
      header.addMouseListener(this);
    }

    public void uninstallListener() {
      if(header != null)
        header.removeMouseListener(this);
    }

    public void mouseClicked(MouseEvent e) {
      try {
        TableColumnModel columnModel = mainTable.getColumnModel();
        int viewColumn = columnModel.getColumnIndexAtX(e.getX());
        int column = mainTable.convertColumnIndexToModel(viewColumn);
        if(e.getClickCount() == 1 && column != -1) {
          int lastRow = mainTable.getSelectedRowWithSort();
          //int lastSort = mainTable.getLastSortColumn();
          //boolean tmpBool = ((lastSort == 0) || ((-lastSort - 1) != column));
          sortByColumn(column); //, tmpBool);
          mainTable.refreshRowSelection(lastRow);
          mainTable.getTableHeader().revalidate();
          mainTable.getTableHeader().repaint();
        }
      }
      catch(Exception ex) {
        ex.printStackTrace();
        //sort went bad. eating it.
      }
    }
  }
}
