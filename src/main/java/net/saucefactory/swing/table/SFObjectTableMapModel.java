package net.saucefactory.swing.table;

/**
 * <p>Title: SLIC </p>
 * <p>Description: Outage management system</p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: CA ISO</p>
 * @author Jeremy Leng
 * @version 1.0
 */

import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

public class SFObjectTableMapModel extends SFObjectTableModel {

  public SFObjectTableMapModel() {
    super();
  }

  public SFObjectTableMapModel(SFObjectTableColumn[] columns) {
    super(columns);
  }

  public Object getValueAt(int r, int c) {
    if(tableData == null)
      return null;
    if(sortIndexArray != null && r < sortIndexArray.length)
      r = sortIndexArray[r];
    try {
      String fldName = columns[c].getFieldName();
      Map row = (Map)tableData[r];
      return row.get(fldName);
    }
    catch(Exception e) {
      return null;
    }
  }

  public Object getValueIgnorSort(int r, int c) {
    if(tableData == null)
      return null;
    try {
      String fldName = columns[c].getFieldName();
      Map row = (Map)tableData[r];
      return row.get(fldName);
    }
    catch(Exception e) {
      return null;
    }
  }

  public void setValueAt(Object newVal, int r, int c) {
    if(tableData == null)
      return;
    if(sortIndexArray != null && r < sortIndexArray.length)
	 r = sortIndexArray[r];
    try {
      String fldName = columns[c].getFieldName();
      Map row = (Map)tableData[r];
      try {
	row.remove(fldName);
      } catch (Exception ee) {}
      row.put(fldName, newVal);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    SFObjectTableMapModel SFObjectTableMapModel1 = new SFObjectTableMapModel();
  }
}