package net.saucefactory.swing.table;

public interface ISFTableState {

  public void saveSelectionState();
  public void setNextSelection(Object selectedValue);
  public void setNextSelection(int row);
  public void setForceNextSelection(boolean forceNextSelection);
  public void restoreSelectionState();
  public void restoreSelectionState(boolean forceSelection);
}
