package net.saucefactory.swing.table;

public interface ISFTableSortAdapter {
  public void setTable(SFTable table);
  public void redoLastSort();
  public int getLastSortLevel(int column);
  public int getLastSortColumn();
  public void reset();
  public void clearSort();
  public int getRowCount();
  public int rowToSortIndex(int row);
  public void sortByColumn(int column);
  public void doManualSort(int column, int level, boolean ascending);
  public void reverseSortOrder(int level);
  public void setSortDepth(int depth);
  public int getSortDepth();
  public int getMaxSortDepth();
  public void setSortOrderArray(boolean[] sortOrder);
  public String storeSort();
  public void restoreSort(String sort);
  public String getDisplayString();
}
