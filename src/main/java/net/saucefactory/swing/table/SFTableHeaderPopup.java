package net.saucefactory.swing.table;

import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import net.saucefactory.swing.SFTitleLabel;
import javax.swing.UIManager;
import java.awt.Font;
import net.saucefactory.swing.common.SFMessageHandler;
import net.saucefactory.swing.data.SFTableColumnViewState;

public class SFTableHeaderPopup {

  public SFTable tgtTable;
  private int lastMouseColumn = -1;
  private TableHeaderPopupHandler handler = new TableHeaderPopupHandler();
  private JPopupMenu popup = new JPopupMenu("Title");
  private JMenuItem itmHideColumn = new JMenuItem("Hide Column");
  private JMenuItem itmChooseColumns = new JMenuItem("Choose Columns");
  private JMenuItem itmShowAllColumns = new JMenuItem("Show All Columns");
  private JMenuItem itmClearSort = new JMenuItem("Clear Sort");
  private JMenuItem itmChangeOrder = new JMenuItem("Change Order");
  private JMenuItem[] orderItemArray = null;
  private SFTableColumn[] allColumns = null;
  private SFTableColumn[] currentColumns = null;
  private JCheckBoxMenuItem[] allColChecks = null;
  //private JMenuItem lblTitle = new JMenuItem();

  public SFTableHeaderPopup(SFTable tgtTable) {
    this(tgtTable, false);
  }

  public SFTableHeaderPopup(SFTable tgtTable, boolean chooseColumns) {
    this.tgtTable = tgtTable;
    initPopup();
    registerHandlers();
    initCommands();
    if(chooseColumns)
      initColumnItems();
  }

  private void initPopup() {
    /*popup.add(lblTitle);
    Font lblTitleFont = lblTitle.getFont();
    lblTitle.setFont(new Font(lblTitleFont.getName(), Font.BOLD, lblTitleFont.getSize()));
    lblTitle.setBackground(UIManager.getColor("TableHeader.background"));
    lblTitle.setEnabled(false);*/
    popup.add(itmHideColumn);
    popup.add(itmChooseColumns);
    popup.add(itmShowAllColumns);
    popup.addSeparator();
    popup.add(itmClearSort);
    popup.add(itmChangeOrder);
    popup.addSeparator();
  }

  public void initColumnItems() {
    allColumns = tgtTable.getTableModel() != null ? tgtTable.getTableModel().getAllColumnHeaders() : null;
    if(allColumns != null) {
      allColChecks = new JCheckBoxMenuItem[allColumns.length];
      for(int i = 0; i < allColumns.length; i++) {
        allColChecks[i] = new JCheckBoxMenuItem(cleanColumnName(allColumns[i].getDisplayName()));
        allColChecks[i].setActionCommand("COLUMN" + i);
        allColChecks[i].addActionListener(handler);
        popup.add(allColChecks[i]);
      }
      popup.addSeparator();
    }
  }

  private String cleanColumnName(String name) {
    if(name == null)
      return "";
    name = name.replaceAll("<BR>", " ");
    name = name.replaceAll("\n", " ");
    return name;
  }

  private void initCommands() {
    itmClearSort.setActionCommand("CLEAR");
    itmHideColumn.setActionCommand("HIDE");
    itmChooseColumns.setActionCommand("CHOOSE");
    itmChangeOrder.setActionCommand("ORDER");
    itmShowAllColumns.setActionCommand("RESTORE");
  }

  private void registerHandlers() {
    itmClearSort.addActionListener(handler);
    itmHideColumn.addActionListener(handler);
    itmChooseColumns.addActionListener(handler);
    itmChangeOrder.addActionListener(handler);
    itmShowAllColumns.addActionListener(handler);
  }

  private void clearSort() {
    if(tgtTable.getSortAdapter() != null) {
      tgtTable.getSortAdapter().clearSort();
      tgtTable.getTableHeader().revalidate();
      tgtTable.getTableHeader().repaint();
    }
  }

  private void hideColumn() {
    if(tgtTable.getTableModel().getColumnCount() > 1)
      tgtTable.removeTableColumn(tgtTable.convertColumnIndexToView(lastMouseColumn));
    else
      SFMessageHandler.sendWarningMessage(tgtTable, "One column must remain visible.", "Column Warning");
  }

  private void chooseColumns() {
    SFTableColumn[] newCols = ChooseColumnsDialog.chooseColumns(tgtTable, tgtTable.getTableModel().getAllColumnHeaders(),
        tgtTable.getTableModel().getColumnHeaders());
    if(newCols != null) {
      SFTableColumnViewState[] viewState = tgtTable.getColumnViewStates(tgtTable.getTableModel().getColumnHeaders());
      tgtTable.setActiveColumns(newCols);
      tgtTable.applyColumnViewStates(viewState);
    }
  }

  private void restoreAllColumns() {
    tgtTable.showAllColumns();
  }

  private void changeOrder() {
    if(tgtTable.getSortAdapter() != null) {
      int level = Math.abs(tgtTable.getSortAdapter().getLastSortLevel(lastMouseColumn));
      tgtTable.getSortAdapter().reverseSortOrder(level);
    }
  }

  public void setLastMouseColumn(int lastMouseColumn) {
    this.lastMouseColumn = lastMouseColumn;
  }

  public void addExtraMenuItem(JMenuItem addItem, int index) {
    if(addItem != null) {
      if(index > -1)
        popup.add(addItem, index);
      else
        popup.add(addItem);
    }
  }

  public void addExtraMenuItems(JMenuItem[] addItems) {
    if(addItems != null) {
      for(int i = 0; i < addItems.length; i++)
        popup.add(addItems[i]);
      popup.addSeparator();
    }
  }

  public void handleColumnCheckbox(String command) {
    try {
      command = command.replaceAll("COLUMN", "");
      int index = Integer.parseInt(command);
      if(allColChecks[index].isSelected())
        tgtTable.addTableColumn(allColumns[index]);
      else {
        if(tgtTable.getTableModel().getColumnCount() > 1)
          tgtTable.removeTableColumn(allColumns[index]);
        else
          SFMessageHandler.sendWarningMessage(tgtTable, "One column must remain visible.", "Column Warning");
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void showPopup(MouseEvent me) {
    boolean sortOn = tgtTable.getSortAdapter() != null;
    SFTableColumn tmpCol = tgtTable.getTableModel().getColumnHeader(lastMouseColumn);
    boolean sortableColumn = tmpCol == null ? false : tmpCol.isSortable();
    if(sortOn) {
      int cnt = tgtTable.getSortAdapter().getSortDepth();
      int lastLevel = tgtTable.getSortAdapter().getLastSortLevel(lastMouseColumn);
      int maxLevel = tgtTable.getSortAdapter().getMaxSortDepth();
      //String displayName = tgtTable.getTableModel().getColumnHeader(lastMouseColumn).getDisplayName();
      //lblTitle.setText(displayName);
      if(lastLevel == 0)
        itmChangeOrder.setEnabled(false);
      else {
        if(lastLevel > 0)
          itmChangeOrder.setText("Change to Descending");
        else
          itmChangeOrder.setText("Change to Ascending");
        itmChangeOrder.setEnabled(true);
      }
      if(sortableColumn) {
        lastLevel = Math.abs(lastLevel);
        setOrderArray(maxLevel, cnt, lastLevel);
        itmClearSort.setEnabled(cnt > 0);
      }
      else {
        lastLevel = Math.abs(lastLevel);
        setOrderArray(0, cnt, lastLevel);
        itmClearSort.setEnabled(cnt > 0);
      }

    }
    else {
      itmChangeOrder.setEnabled(false);
    }
    if(allColChecks != null) {
      currentColumns = tgtTable.getTableModel().getColumnHeaders();
      for(int i = 0; i < allColumns.length; i++) {
        if(isColumnSelected(allColumns[i]))
          allColChecks[i].setSelected(true);
        else
          allColChecks[i].setSelected(false);
      }
    }
    popup.show((Component)me.getSource(), me.getX(), me.getY());
  }

  private boolean isColumnSelected(SFTableColumn testColumn) {
    boolean rtn = false;
    if(currentColumns != null) {
      for(int i = 0; i < currentColumns.length; i++) {
        if(currentColumns[i] == testColumn) {
          rtn = true;
          break;
        }
      }
    }
    return rtn;
  }

  private void setOrderArray(int maxLevel, int currentDepth, int lastColLevel) {
    if(maxLevel > 0) {
      if(orderItemArray == null || orderItemArray.length != maxLevel) {
        if(orderItemArray != null) { //remove current
          for(int i = 0; i < orderItemArray.length; i++)
            popup.remove(orderItemArray[i]);
        }
        orderItemArray = new JMenuItem[maxLevel];
        for(int i = 0; i < maxLevel; i++) {
          if(i == 0)
            orderItemArray[i] = new JMenuItem("Sort Primary");
          else if(i == 1)
            orderItemArray[i] = new JMenuItem("Sort Secondary");
          else if(i == 2)
            orderItemArray[i] = new JMenuItem("Sort Ternary");
          else
            orderItemArray[i] = new JMenuItem("Sort " + (i + 1) + "th");
          orderItemArray[i].setActionCommand("SORT_" + i);
          orderItemArray[i].addActionListener(handler);
          popup.add(orderItemArray[i]);
        }
      }
      //set current state
      for(int i = 0; i < orderItemArray.length; i++) {
        if(i == 0)
          orderItemArray[i].setEnabled(1 != lastColLevel);
        else
          orderItemArray[i].setEnabled(currentDepth > (i - 1) && (i + 1) != lastColLevel);
      }
    }
    else { //clear out old.
      if(orderItemArray != null) { //remove current
        for(int i = 0; i < orderItemArray.length; i++)
          popup.remove(orderItemArray[i]);
        orderItemArray = null;
      }
    }
  }

  class TableHeaderPopupHandler implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      String command = ae.getActionCommand();
      if(command.equals("CLEAR")) {
        clearSort();
      }
      else if(command.equals("ORDER")) {
        changeOrder();
      }
      else if(command.equals("HIDE")) {
        hideColumn();
      }
      else if(command.equals("CHOOSE")) {
        chooseColumns();
      }
      else if(command.equals("RESTORE")) {
        restoreAllColumns();
      }
      else if(command.indexOf("SORT") > -1) {
        if(tgtTable.getSortAdapter() != null) {
          String level = command.substring(command.indexOf("_") + 1);
          int intLevel = Integer.parseInt(level);
          tgtTable.getSortAdapter().doManualSort(lastMouseColumn, intLevel, true);
        }
      }
      else if(command.indexOf("COLUMN") > -1) {
        handleColumnCheckbox(command);
      }
    }
  }
}
