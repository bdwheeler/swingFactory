package net.saucefactory.swing.table;

import java.util.StringTokenizer;
import java.util.Vector;

public class SFTableColumnState {

  private int[] columnState = null;

  public SFTableColumnState() {}

  public int[] getColumnState() {
    return columnState;
  }

  public void storeColumnState(SFTable tgtTable) {
    int count = tgtTable.getColumnCount();
    columnState = new int[count];
    for(int i = 0; i < count; i++) {
      int index = tgtTable.convertColumnIndexToModel(i);
      SFTableColumn tmpCol = tgtTable.getTableModel().getColumnHeader(index);
      int tmpId = tmpCol.getColumnID();
      columnState[i] = tmpId;
    }
  }

  public void restoreColumnState(SFTable tgtTable) {
    Vector tmpVec = new Vector();
    SFTableColumn[] columns = tgtTable.getTableModel().getAllColumnHeaders();
    int cnt = 0;
    for(int i = 0; i < columnState.length; i++) {
      int tmpId = columnState[i];
      for(int j = 0; j < columns.length; j++) {
        if(columns[j].columnID == tmpId) {
          tmpVec.add(cnt, columns[j]);
          cnt++;
          break;
        }
      }
    }
    if(tmpVec.size() > 0) {
      SFTableColumn[] newColumns = new SFTableColumn[tmpVec.size()];
      newColumns = (SFTableColumn[])tmpVec.toArray(newColumns);
      tgtTable.setActiveColumns(newColumns);
    }
  }

  public void fromString(String state) {
    try {
      if(state == null || state.equals(""))
        return;
      StringTokenizer tok = new StringTokenizer(state, ";");
      columnState = new int[tok.countTokens()];
      int cnt = 0;
      while(tok.hasMoreTokens()) {
        String tmpTok = tok.nextToken();
        columnState[cnt] = Integer.parseInt(tmpTok);
        cnt++;
      }
    }
    catch(Exception e) {}
  }

  public String toString() {
    String rtn = "";
    if(columnState != null) {
      for(int i = 0; i < columnState.length; i++) {
        if(i == 0)
          rtn = String.valueOf(columnState[i]);
        else
          rtn = rtn + ";" + String.valueOf(columnState[i]);
      }
    }
    return rtn;
  }
}
