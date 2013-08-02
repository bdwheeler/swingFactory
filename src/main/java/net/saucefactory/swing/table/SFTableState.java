package net.saucefactory.swing.table;

import net.saucefactory.swing.data.ISFDataAdapter;

public class SFTableState implements ISFTableState {

  private SFTable table;
  private ISFDataAdapter keyAdapter;
  private Object selectedValue = null;
  private int selectedRow;
  private boolean nextSelectionOverride = false;
  private boolean forceNextSelection = false;

  public SFTableState(SFTable table, ISFDataAdapter keyAdapter) {
    this.table = table;
    this.keyAdapter = keyAdapter;
  }

  public void saveSelectionState() {
    if(nextSelectionOverride && selectedValue != null)
      return;
    selectedRow = table.getSelectedRow();
    if(selectedRow > -1)
      selectedValue = keyAdapter.getValue(table.getSelectedObject());
    else
      selectedValue = null;
  }

  public void setNextSelection(Object selectedValue) {
    this.selectedValue = selectedValue;
    selectedRow = -1;
    nextSelectionOverride = true;
  }

  public void setNextSelection(int row) {}

  public void setForceNextSelection(boolean forceNextSelection) {
    this.forceNextSelection = forceNextSelection;
  }

  public void restoreSelectionState() {
    restoreSelectionState(forceNextSelection);
    forceNextSelection = false;
  }

  public void restoreSelectionState(boolean forceSelection) {
    try {
      boolean newNextOverride = false;
      if(selectedValue == null) {
        if(!forceSelection)
          return;
        if(selectedRow < 0)
          return;
      }
      int foundRow = -1;
      for(int i = 0; i < table.getRowCount(); i++) {
        Object value = keyAdapter.getValue(table.getObjectAtRow(i));
        if(value != null && value.equals(selectedValue)) {
          foundRow = i;
          break;
        }
      }
      if(foundRow > -1)
        table.setRowSelectionInterval(foundRow, foundRow);
      else {
        if(forceSelection) {
          int rowCount = table.getRowCount();
          if(selectedRow > -1 && rowCount > 0) {
            if(selectedRow >= rowCount)
              table.setRowSelectionInterval(rowCount - 1, rowCount -1);
            else if(table.getSelectedRow() != selectedRow)
              table.setRowSelectionInterval(selectedRow, selectedRow);
            else {
              int tmpSelection = selectedRow;
              clearRowSelection();
              table.setRowSelectionInterval(tmpSelection, tmpSelection);
            }
          }
          else if(selectedRow > -1 && rowCount == 0)
          	clearRowSelection();
        }
        else if(table.getSelectedRow() > -1) {
        	clearRowSelection();
          if(nextSelectionOverride)
            newNextOverride = true;
        }
      }
      nextSelectionOverride = newNextOverride;
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  private void clearRowSelection() {
  	//table.clearSelection();
  	//table.setRowSelectionInterval(-1, -1);
  	table.getSelectionModel().clearSelection();
  }
}
