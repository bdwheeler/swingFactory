package net.saucefactory.swing.table;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.saucefactory.swing.SFSpeedLabel;
import net.saucefactory.swing.data.ISFDataAdapter;
import net.saucefactory.swing.data.SFTableColumnViewState;
import net.saucefactory.swing.renderers.ISFTableRenderer;
import net.saucefactory.swing.renderers.SFStringRenderer;
import net.saucefactory.swing.utils.PrintingUtility;

public class SFTable<T extends Object> extends JTable implements PropertyChangeListener, ComponentListener, ISFTable {
	
  protected SFTableModel<T> tableModel;
  protected ISFTableState tableState;
  protected ISFTableSortAdapter sortAdapter = null;
  protected ISFTableColorAdapter colorAdapter = null;
  protected ActionListener actionHandler;
  protected String aeLabel = "";
  protected boolean doAutoSize = false;
  protected SFTableHeaderPopup headerPopup = null;
  protected SFTableHeaderListener headerListener = null;
  boolean overrideTabKey = false;


  public SFTable() {
    super();
    SFTableHeaderRendererFactory.initTableHeaderRenderer(this);
    setAutoResizeMode(AUTO_RESIZE_OFF);
    addPropertyChangeListener(this);
  }

  public SFTable(SFTableColumn[] columns) {
    super();
    SFTableHeaderRendererFactory.initTableHeaderRenderer(this);
    setTableColumns(columns);
  }

  public SFTable(SFTableModel<T> _model) {
    super();
    SFTableHeaderRendererFactory.initTableHeaderRenderer(this);
    tableModel = _model;
    setModel(_model);
    setAutoResizeMode(SFTable.AUTO_RESIZE_OFF);
    addPropertyChangeListener(this);
  }

  public SFTable(SFTableColumn[] columns, SFTableModel<T> _model) {
    super();
    SFTableHeaderRendererFactory.initTableHeaderRenderer(this);
    tableModel = _model;
    setModel(_model);
    setTableColumns(columns, _model);
  }

  public void setDoAutoResize(boolean doAutoSize) {
    this.doAutoSize = doAutoSize;
    if(doAutoSize)
      setAutoResizeMode(AUTO_RESIZE_LAST_COLUMN);
    else
      setAutoResizeMode(AUTO_RESIZE_OFF);
  }

  public void installTableSortAdapter(ISFTableSortAdapter sortAdapter) {
    addTableHeaderMouseListener();
    headerListener.setSortInstalled(true);
    this.sortAdapter = sortAdapter;
  }

  public void installTableSortAdapter() {
    if(sortAdapter == null)
      sortAdapter = new SFTableSortAdapter(this);
    installTableSortAdapter(sortAdapter);
    /*if(tableModel != null) {
      if(sortAdapter == null)
        sortAdapter = new SFTableSortAdapter(this);
      else
        ((SFTableSortAdapter)sortAdapter).setTableModel(tableModel);
      ((SFTableSortAdapter)sortAdapter).addTableHeaderMouseListener();
    }*/
  }

  public void addTableHeaderMouseListener() {
    if(headerListener == null) {
      headerListener = new SFTableHeaderListener(this);
      headerListener.setHeader(getTableHeader());
    }
    else
      headerListener.setHeader(getTableHeader());
  }

  public void installHeaderPopup() {
    installHeaderPopup(false);
  }

  public void installHeaderPopup(boolean chooseColumnPopup) {
    addTableHeaderMouseListener();
    if(headerPopup == null)
      headerPopup = new SFTableHeaderPopup(this, chooseColumnPopup);
    headerListener.setPopupInstalled(true);
  }

  public SFTableHeaderPopup getHeaderPopup() {
    return headerPopup;
  }

  public void installTableEditAdapter(ISFTableEditAdapter editAdapter) {
    if(tableModel != null) {
      tableModel.setEditAdapter(editAdapter);
    }
  }

  public void installTableState(ISFDataAdapter keyAdapter) {
    tableState = new SFTableState(this, keyAdapter);
  }

  public void installTableState(ISFTableState tableState) {
    this.tableState = tableState;
  }

  public ISFTableState getTableState() {
    return tableState;
  }

  public void setColorAdapter(ISFTableColorAdapter colorAdapter) {
    this.colorAdapter = colorAdapter;
  }

  public boolean hasColorAdapter() {
    return (colorAdapter != null);
  }

  public ISFTableColorAdapter getColorAdapter() {
    return colorAdapter;
  }

  public int getColumnID(int column) {
    if(tableModel != null)
      return tableModel.getColumnID(convertColumnIndexToModel(column));
    return -1;
  }

  public SFTableColumn getColumnForID(int id) {
    if(tableModel != null)
      return tableModel.getColumnForId(id);
    return null;
  }

  public void setTableColumns(SFTableColumn[] columns) {
    if(tableModel == null)
      setTableColumns(columns, new SFTableModel<T>(columns), false);
    else
      setTableColumns(columns, tableModel, true);
  }

  public void removeTableColumn(int column) {
    SFTableModel tmpModel = getTableModel();
    if(tmpModel != null) {
      removeTableColumn(tmpModel.getColumnHeader(convertColumnIndexToModel(column)));
    }
  }

  public void removeTableColumn(SFTableColumn column) {
    SFTableModel tmpModel = getTableModel();
    if(tmpModel != null) {
      SFTableColumnViewState[] viewStates = getColumnViewStates(tmpModel.getColumnHeaders());
      tmpModel.removeTableColumn(column, false);
      setActiveColumns(tmpModel.getColumnHeaders());
      applyColumnViewStates(viewStates);
    }
  }

  /*public void removeTableColumn(int column) {
    SFTableModel tmpModel = getTableModel();
    if(tmpModel != null) {
      removeTableColumn(tmpModel.getColumnHeader(convertColumnIndexToModel(column)));
    }
  }*/

  public SFTableColumnViewState[] getColumnViewStates(SFTableColumn[] currentColumns) {
    if(currentColumns != null && currentColumns.length > 0) {
      SFTableColumnViewState[] rtnArray = new SFTableColumnViewState[currentColumns.length];
      for(int i = 0; i < currentColumns.length; i++) {
        int tmpView = convertColumnIndexToView(i);
        rtnArray[i] = new SFTableColumnViewState(i, tmpView, currentColumns[i].getColumnID());
        //System.out.println("viewstate - " + rtnArray[i]);
      }
      return rtnArray;
    }
    return null;
  }

  public void applyColumnViewStates(SFTableColumnViewState[] viewStates) {
    SFTableModel tmpModel = getTableModel();
    if(tmpModel != null) {
      SFTableUtility.sortColumnViewStates(viewStates);
      SFTableColumn[] tmpColumns = tmpModel.getColumnHeaders();
      int missingCount = 0;
      for(int i = 0; i < viewStates.length; i++) {
        int tmpModelIndex = SFTableUtility.hasColumn(viewStates[i].columnId, tmpColumns);
        if(tmpModelIndex > -1) {
          int tmpViewIndex = convertColumnIndexToView(tmpModelIndex);
          //System.out.println("moving - " + tmpViewIndex + " to " + (viewStates[i].viewIndex - missingCount));
          if(tmpViewIndex != viewStates[i].viewIndex - missingCount)
            getColumnModel().moveColumn(tmpViewIndex, viewStates[i].viewIndex - missingCount);
          //moveColumn(tmpModelIndex, viewStates[i].viewIndex - missingCount);
        }
        else {
          //System.out.println("not moving");
          missingCount++;
        }
      }
      //SFTableColumnViewState[] newStates =  getColumnViewStates(tmpModel.getColumnHeaders());
    }
  }

  /*public void removeTableColumn(SFTableColumn column) {
    SFTableModel tmpModel = getTableModel();
    if(tmpModel != null) {
      SFTableColumn[] tmpCols = getSortedTableColumns();
      if(tmpCols != null) {
        int removeIndex = -1;
        for(int i = 0; i < tmpCols.length; i++) {
          if(tmpCols[i] == column) {
            removeIndex = i;
            break;
          }
        }
        if(removeIndex > -1) {
          if(tmpCols.length > 0) {
            SFTableColumn[] newColumns = new SFTableColumn[tmpCols.length - 1];
            int rowCount = 0;
            for(int i = 0; i < tmpCols.length; i++) {
              if(i != removeIndex) {
                newColumns[rowCount] = tmpCols[i];
                rowCount++;
              }
            }
            setActiveColumns(newColumns);
          }
        }
      }
    }
  }*/

  public void addTableColumn(SFTableColumn column) {
    SFTableModel tmpModel = getTableModel();
    if(tmpModel != null) {
      SFTableColumnViewState[] viewStates = getColumnViewStates(tmpModel.getColumnHeaders());
      tmpModel.addTableColumn(column, false);
      setActiveColumns(tmpModel.getColumnHeaders());
      applyColumnViewStates(viewStates);
    }
  }

  /*public void addTableColumn(SFTableColumn column) {
    SFTableModel tmpModel = getTableModel();
    if(tmpModel != null) {
      boolean found = false;
      SFTableColumn[] tmpCols = getSortedTableColumns();
      if(tmpCols != null) {
        for(int i = 0; i < tmpCols.length; i++) {
          if(tmpCols[i] == column) {
            found = true;
            break;
          }
        }
        if(!found) {
          SFTableColumn[] newColumns = new SFTableColumn[tmpCols.length + 1];
          for(int i = 0; i < tmpCols.length; i++)
            newColumns[i] = tmpCols[i];
          newColumns[tmpCols.length] = column;
          setActiveColumns(newColumns);
        }
      }
    }
  }*/

  public void setTableColumns(SFTableColumn[] columns, SFTableModel<T> model) {
    setTableColumns(columns, model, true);
  }

  public void setTableColumns(SFTableColumn[] columns, SFTableModel<T> model, boolean redoColumnModel) {
    setTableColumns(columns, model, redoColumnModel, true);
  }

  public void setActiveColumns(SFTableColumn[] columns) {
    SFTableModel<T> tmpModel = getTableModel();
    if(tmpModel != null)
      setTableColumns(columns, tmpModel, true, false);
  }

  public void showAllColumns() {
    SFTableModel<T> tmpModel = getTableModel();
    if(tmpModel != null)
      setTableColumns(tmpModel.getAllColumnHeaders(), tmpModel, true, false);
  }

  public SFTableColumnState getColumnState() {
    SFTableColumnState tmpState = new SFTableColumnState();
    tmpState.storeColumnState(this);
    return tmpState;
  }

  public void setColumnState(SFTableColumnState columnState) {
    columnState.restoreColumnState(this);
  }

  public void setColumnState(String columnState) {
    SFTableColumnState tmpState = new SFTableColumnState();
    tmpState.fromString(columnState);
    tmpState.restoreColumnState(this);
  }

  public void setTableColumns(SFTableColumn[] columns, SFTableModel<T> model, boolean redoColumnModel, boolean allColumns) {
    model.setColumnHeaders(columns, allColumns);
    tableModel = model;
    setModel(model);
    if(redoColumnModel)
      tableChanged(new TableModelEvent(model, -1, -1));
    int tableWidth = 0;
    if(getParent() != null)
      tableWidth = getParent().getWidth();
    int runningWidth = 0;
    for(int i = 0; i < columns.length; i++) {
      TableColumn tempCol = getColumn(columns[i].getColumnName());
      int tmpWidth = columns[i].getColumnWidth();
      if(columns[i].headerRenderer != null)
        tempCol.setHeaderRenderer(columns[i].headerRenderer);
      if(columns[i].getRenderer() != null)
        tempCol.setCellRenderer(columns[i].getRenderer());
      if(columns[i].getEditor() != null)
        tempCol.setCellEditor(columns[i].getEditor());
      if(tmpWidth > -1) {
        runningWidth += tmpWidth;
        if(i == columns.length - 1 && tableWidth > 0) {
          if(runningWidth < tableWidth)
            tmpWidth += (tableWidth - runningWidth);
        }
        tempCol.setPreferredWidth(tmpWidth);
        tempCol.setWidth(tmpWidth);
      }
      tempCol.addPropertyChangeListener(this);
    }
  }

  public void redoColumnWidths() {
    try {
      int tableWidth = getParent().getWidth();
      int widthCount = 0;
      int tempWidth = 0;
      SFTableColumn[] headers = tableModel.getColumnHeaders();
      for(int i = 0; i < headers.length; i++) {
        int tmpIndex = convertColumnIndexToModel(i);
        tempWidth = headers[tmpIndex].getColumnWidth();
        widthCount += tempWidth;
        if(tempWidth > -1) {
          if(i == (headers.length - 1)) {
            if(widthCount < tableWidth) {
              getColumnModel().getColumn(i).setPreferredWidth(tempWidth + tableWidth - widthCount);
              getColumnModel().getColumn(i).setWidth(tempWidth);
            }
            else {
              getColumnModel().getColumn(i).setPreferredWidth(tempWidth);
              getColumnModel().getColumn(i).setWidth(tempWidth);
            }
          }
          else {
            getColumnModel().getColumn(i).setPreferredWidth(tempWidth);
            getColumnModel().getColumn(i).setWidth(tempWidth);
          }
        }
      }
    }
    catch(Exception e) { //Columns not set, ignoring.
    }
  }

  public void redoColumnHeaders() {
    SFTableColumn[] headers = tableModel.getColumnHeaders();
    for(int i = 0; i < headers.length; i++) {
      TableColumn tempCol = getColumn(headers[i].getColumnName());
      if(headers[i].headerRenderer != null)
        tempCol.setHeaderRenderer(headers[i].headerRenderer);
    }
  }

  public void setTableData(SFTableColumn[] columns, Object[] tableData) {
    setTableColumns(columns);
    tableModel.setTableData(tableData);
    redoLastSort();
  }

  public void setTableData(Object[] tableData) {
    setTableData(tableData, true);
  }

  public void setTableData(Object[] tableData, boolean redoColumns) {
    tableModel.setTableData(tableData);
    if(!doAutoSize)
      setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    redoLastSort();
    if(redoColumns)
      redoColumnWidths();
  }

  public void addTableData(Object[] addData) {
    if(addData == null || addData.length < 1)
      return;
    Object[] oldData = tableModel.getTableData();
    if(oldData != null && oldData.length > 0) {
      @SuppressWarnings("unchecked")
    	Object[] newData = new Object[oldData.length + addData.length];
      System.arraycopy(oldData, 0, newData, 0, oldData.length);
      System.arraycopy(addData, 0, newData, oldData.length, addData.length);
      tableModel.setTableData(newData);
    }
    else {
      tableModel.setTableData(addData);
    }
    redoLastSort();
  }

  public boolean hasObject(Object testData) {
    Object[] data = tableModel.getTableData();
    if(testData != null && data != null) {
      for(int i = 0; i < data.length; i++) {
        if(data[i] != null && data[i].equals(testData))
            return true;
      }
    }
    return false;
  }

  public void addTableData(T addData) {
    if(addData == null)
      return;
    Object[] oldData = tableModel.getTableData();
    if(oldData != null && oldData.length > 0) {
      @SuppressWarnings("unchecked")
    	Object[] newData = new Object[oldData.length + 1];
      System.arraycopy(oldData, 0, newData, 0, oldData.length);
      newData[oldData.length] = addData;
      tableModel.setTableData(newData);
    }
    else {
    	@SuppressWarnings("unchecked")
    	Object[] newData = new Object[1];
    	newData[0] = addData;
      tableModel.setTableData(newData);
    }
    redoLastSort();
  }

  public void insertTableData(T addData, int index) {
    if(addData == null)
      return;
    Object[] oldData = tableModel.getTableData();
    if(oldData != null && oldData.length > 0) {
    	@SuppressWarnings("unchecked")
    	Object[] newData = new Object[oldData.length + 1];
      if(index > 0)
        System.arraycopy(oldData, 0, newData, 0, index);
      newData[index] = addData;
      if(index < oldData.length)
        System.arraycopy(oldData, index, newData, index + 1, oldData.length - index);
      tableModel.setTableData(newData);
    }
    else {
    	@SuppressWarnings("unchecked")
    	Object[] newData = new Object[1];
    	newData[0] = addData;
      tableModel.setTableData(newData);
    }
    redoLastSort();
  }

  public void updateTableDataAt(T newData, int index) {
    if(newData == null)
      return;
    Object[] oldData = tableModel.getTableData();
    if(oldData != null && oldData.length > 0 && index > -1 && index < oldData.length) {
      oldData[index] = newData;
      tableModel.setTableData(oldData);
    }
    redoLastSort();
  }

  public void deleteSelectedItem() {
    int selectedItem = this.getSelectedRow();
    if(selectedItem > -1)
      deleteItem(selectedItem);
  }

  public void deleteItem(int selectedItem) {
    if(selectedItem > -1) {
      Object[] oldData = tableModel.getTableData();
      if(oldData.length > 1) {
      	@SuppressWarnings("unchecked")
      	Object[] newData = new Object[oldData.length - 1];
        if(selectedItem > 0)
          System.arraycopy(oldData, 0, newData, 0, selectedItem);
        if(selectedItem < newData.length)
          System.arraycopy(oldData, selectedItem + 1, newData, selectedItem,
              newData.length - selectedItem);
        tableModel.setTableData(newData);
        redoLastSort();
      }
      else {
        tableModel.setTableData(null);
      }
    }
  }

  public void deleteItem(T item) {
    Object[] oldData = tableModel.getTableData();
    int selectedItem = -1;
    if(item != null && oldData != null) {
      for(int i = 0; i < oldData.length; i++) {
        if(oldData[i] != null && oldData[i].equals(item)) {
          selectedItem = i;
          break;
        }
      }
    }
    if(selectedItem > -1) {
      deleteItem(selectedItem);
    }
  }

  public void setOverrideTabKey(boolean overrideTabKey) {
    this.overrideTabKey = overrideTabKey;
  }

  protected void processKeyEvent(KeyEvent e) {
    if(!overrideTabKey)
      super.processKeyEvent(e);
    else if(e.getKeyChar() == KeyEvent.VK_TAB) {
      if(e.getID() == KeyEvent.KEY_PRESSED) {
        if(e.isShiftDown())
          KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent();
        else
          KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
      }
      e.consume();
    }
    else
      super.processKeyEvent(e);
  }

  public void setSortIndexArray(int[] sortIndexArr) {
    tableModel.setSortIndexArray(sortIndexArr);
  }

  public ISFTableSortAdapter getSortAdapter() {
    return sortAdapter;
  }

  public int[] getSortIndexArray() {
    if(tableModel != null)
      return tableModel.getSortIndexArray();
    return null;
  }

  public int getLastSortColumn() {
    if(sortAdapter != null)
      return sortAdapter.getLastSortColumn();
    return -1;
  }

  public int getLastSortLevel(int column) {
    if(sortAdapter == null)
      return 0;
    else
      return sortAdapter.getLastSortLevel(column);
  }

  public void resetSortAdapter() {
    if(sortAdapter != null)
      sortAdapter.reset();
  }

  public void redoLastSort() {
    if(sortAdapter != null) {
      sortAdapter.redoLastSort();
      revalidate();
      repaint();
    }
  }

  public void setActionHandler(ActionListener handler) {
    this.actionHandler = handler;
    addMouseListener(new MouseHandler());
  }

  public int getSelectedRowWithSort() {
    int index = super.getSelectedRow();
    if(sortAdapter != null)
      index = sortAdapter.rowToSortIndex(index);
    return index;
  }

  public int[] getSelectedRowsWithSort() {
    int[] indices = super.getSelectedRows();
    if(indices != null && sortAdapter != null) {
      for(int i = 0; i < indices.length; i++)
        indices[i] = sortAdapter.rowToSortIndex(indices[i]);
    }
    return indices;
  }

  public void clear() {
    if(tableModel != null)
      tableModel.clear();
  }

  public void clearTableData() {
    if(tableModel != null)
      tableModel.clearTableData();
  }

  public void update() {
    tableModel.updateTable();
  }

  public Object getSelectedObject() {
    int index = getSelectedRowWithSort();
    if(index > -1) {
      Object[] tmpData = tableModel.getTableData();
      if(tmpData != null && index < tmpData.length)
        return tmpData[index];
    }
    return null;
  }

  public Object[] getSelectedObjects() {
    int[] indexes = getSelectedRows();//getSelectedRowsWithSort();
    if(indexes != null && indexes.length > 0) {
      Object[] rtnData = new Object[indexes.length];
      for(int i = 0; i < indexes.length; i++)
        rtnData[i] = tableModel.getObjectAtRow(indexes[i]);
      return rtnData;
    }
    return null;
  }

  public boolean isItemSelected() {
    return getSelectedRow() > -1;
  }

  public Object getObjectAtRow(int row)
  {
    return tableModel.getObjectAtRow(row);
  }

  public Object[] getTableData()
  {
    return tableModel.getTableData();
  }

  public void tableColorUpdate()
  {
    tableModel.fireTableChanged(new TableModelEvent(tableModel));
  }

  public String getStringValueAt(int r, int c, boolean quoted) {
    try {
      String QUOTE_STR = quoted ? "\"" : "";
      Object tmpValue = getValueAt(r, c);
      if(tmpValue == null)
        return "";
      //System.out.println("row: " + r + " col: " + c);
      TableCellRenderer tmpRenderer = getCellRenderer(r, c);
      if(tmpRenderer == null)
        return "";
      if(tmpRenderer instanceof ISFTableRenderer)
        return ((ISFTableRenderer)tmpRenderer).getStringValue(this, tmpValue, false, false, r, c);
      Component tmpComp = tmpRenderer.getTableCellRendererComponent(this, tmpValue, false, false, r,
          c);
      if(tmpComp == null)
        return "";
      else if(tmpComp instanceof SFStringRenderer)
        return QUOTE_STR + ((JLabel)tmpComp).getText() + QUOTE_STR;
      else if(tmpComp instanceof JLabel)
        return((JLabel)tmpComp).getText();
      else if(tmpComp instanceof SFSpeedLabel)
        return((SFSpeedLabel)tmpComp).getText();
      else if(tmpComp instanceof JCheckBox)
        return((JCheckBox)tmpComp).isSelected() ? "Y" : "N";
      else
        return tmpComp.toString();
    }
    catch(Exception e) {
      e.printStackTrace();
      return "";
    }
  }

  public String getPrintValueAt(int r, int c) {
    try {
      Object tmpValue = getValueAt(r, c);
      if(tmpValue == null)
        return "";
      //System.out.println("row: " + r + " col: " + c);
      TableCellRenderer tmpRenderer = getCellRenderer(r, c);
      if(tmpRenderer == null)
        return "";
      Component tmpComp = tmpRenderer.getTableCellRendererComponent(this, tmpValue, false, false, r,
          c);
      if(tmpComp == null)
        return "";
      else if(tmpComp instanceof SFStringRenderer)
        return((JLabel)tmpComp).getText();
      else if(tmpComp instanceof JLabel)
        return((JLabel)tmpComp).getText();
      else if(tmpComp instanceof SFSpeedLabel)
        return((SFSpeedLabel)tmpComp).getText();
      else if(tmpComp instanceof JCheckBox)
        return null;
      else
        return tmpComp.toString();
    }
    catch(Exception e) {
      e.printStackTrace();
      return "";
    }
  }

  public void printValueAt(Graphics g, int r, int c, int x, int y, int width, int height) {
    try {
      Object tmpValue = getValueAt(r, c);
      if(tmpValue == null)
        return;
      TableCellRenderer tmpRenderer = getCellRenderer(r, c);
      if(tmpRenderer == null)
        return;
      Component tmpComp = tmpRenderer.getTableCellRendererComponent(this, tmpValue, false, false, r, c);
      if(tmpComp == null)
        return;
      else if(tmpComp instanceof JCheckBox) {
        boolean tmpSelected = ((JCheckBox)tmpComp).isSelected();
        PrintingUtility.paintCheckBox(g, x, y, width, height, tmpSelected);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }



  public String getStringValueAtIgnoreSort(int r, int c, boolean quoted) {
    try {
      String QUOTE_STR = quoted ? "\"" : "";
      Object tmpValue = tableModel.getValueIgnorSort(r, c);
      if(tmpValue == null)
        return "";
      TableCellRenderer tmpRenderer = getCellRenderer(r, convertColumnIndexToView(c));
      if(tmpRenderer == null)
        return "";
      if(tmpRenderer instanceof ISFTableRenderer)
        return ((ISFTableRenderer)tmpRenderer).getStringValue(this, tmpValue, false, false, r, c);
      Component tmpComp = tmpRenderer.getTableCellRendererComponent(this, tmpValue, false, false, r, c);
      if(tmpComp == null)
        return "";
      else if(tmpComp instanceof SFStringRenderer)
        return QUOTE_STR + ((JLabel)tmpComp).getText() + QUOTE_STR;
      else if(tmpComp instanceof JLabel)
        return((JLabel)tmpComp).getText();
      else if(tmpComp instanceof SFSpeedLabel)
        return((SFSpeedLabel)tmpComp).getText();
      else if(tmpComp instanceof JCheckBox)
        return((JCheckBox)tmpComp).isSelected() ? "Y" : "N";
      else
        return tmpComp.toString();
    }
    catch(Exception e) {
      e.printStackTrace();
      return "";
    }
  }

  public String exportTable(ISFTableExportAdapter exportAdapter) {
    try {
      if(exportAdapter != null) {
        exportAdapter.initExportAdapter(this);
        if(exportAdapter.isReady()) {
          StringBuffer buf = new StringBuffer();
          //start page
          exportAdapter.appendPageStart(buf);
          exportAdapter.appendBodyStart(buf);
          exportAdapter.appendTitleData(buf);
          exportAdapter.appendSummaryData(buf);
          exportAdapter.appendContentStart(buf);
          //append headers
          SFTableColumn[] columns = tableModel.getColumnHeaders();
          if(columns != null) {
            exportAdapter.appendHeaderStart(buf);
            for(int i = 0; i < columns.length; i++) {
              int index = convertColumnIndexToModel(i);
              SFTableColumn tmpCol = columns[index];
              exportAdapter.appendHeaderColumnStart(buf, tmpCol, i);
              exportAdapter.appendHeaderColumnValue(buf, tmpCol, i);
              exportAdapter.appendHeaderColumnEnd(buf, tmpCol, i);
            }
            exportAdapter.appendHeaderEnd(buf);
          }
          //append data
          int rowCount = getRowCount();
          exportAdapter.appendDataStart(buf);
          for(int i = 0; i < rowCount; i++) {
            exportAdapter.appendDataRowStart(buf, i);
            for(int j = 0; j < columns.length; j++) {
              int index = j;//convertColumnIndexToModel(j);
              Object tmpVal = exportAdapter.getTableValueAt(i, index);
              exportAdapter.appendDataColumnStart(buf, tmpVal, i, index);
              exportAdapter.appendDataColumnValue(buf, tmpVal, i, index);
              exportAdapter.appendDataColumnEnd(buf, tmpVal, i, index);
            }
            exportAdapter.appendDataRowEnd(buf, i);
          }
          exportAdapter.appendDataEnd(buf);
          //end page
          exportAdapter.appendContentEnd(buf);
          exportAdapter.appendBodyEnd(buf);
          exportAdapter.appendPageEnd(buf);
          return buf.toString();
        }
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public String getHTMLView() {
    String rtn = exportTable(new SFTableHtmlExportAdapter());
    return rtn == null ? "" : rtn;
  }

  public String getTextView() {
    String rtn = exportTable(new SFTableTextExportAdapter());
    return rtn == null ? "" : rtn;
    /*try {
      SFTableColumn[] textCols = null;
      if(tableModel != null)
        textCols = tableModel.getColumnHeaders();
      if(textCols == null)
        return "";
      StringBuffer buf = new StringBuffer();
      for(int i = 0; i < textCols.length; i++) {
        buf.append(textCols[i].getColumnName().replaceAll("\n", " ") + "\t");
      }
      buf.append("\n");
      int rowCount = getRowCount();
      for(int i = 0; i < rowCount; i++) {
        for(int j = 0; j < textCols.length; j++) {
          buf.append(getStringValueAt(i, j, false) + "\t");
        }
        buf.append("\n");
      }
      return buf.toString();
    }
    catch(Exception e) {
      e.printStackTrace();
      return "";
    }*/
  }

  public String getCSVView() {
    String rtn = exportTable(new SFTableCsvExportAdapter());
    return rtn == null ? "" : rtn;
    /*try {
      SFTableColumn[] csvCols = null;
      if(tableModel != null)
        csvCols = tableModel.getColumnHeaders();
      if(csvCols == null)
        return "";
      StringBuffer buf = new StringBuffer();
      int colLength = csvCols.length;
      for(int i = 0; i < colLength; i++) {
        String tmpTitle = csvCols[i].getColumnName();
        int newIndex = tmpTitle.indexOf('\n');
        if(newIndex > -1)
          tmpTitle = tmpTitle.substring(0, newIndex) + " " + tmpTitle.substring(newIndex + 1);
        buf.append(tmpTitle);
        if(i < (colLength - 1))
          buf.append(",");
        else
          buf.append("\n");
      }
      int rowCount = getRowCount();
      for(int i = 0; i < rowCount; i++) {
        for(int j = 0; j < colLength; j++) {
          if(j == 0)
            buf.append("\"");
          buf.append(getStringValueAt(i, j, true));
          if(j < (colLength - 1))
            buf.append("\",\"");
        }
        buf.append("\"\n");
      }
      return buf.toString();
    }
    catch(Exception e) {
      e.printStackTrace();
      return "";
    }*/
  }

  class MouseHandler extends MouseAdapter {
    public void mouseClicked(MouseEvent e) {
      if(e.getClickCount() == 1)
        actionHandler.actionPerformed(new ActionEvent(e.getSource(), 0, (aeLabel + "SINGLECLICK")));
      else if(e.getClickCount() == 2)
        actionHandler.actionPerformed(new ActionEvent(e.getSource(), 0, (aeLabel + "DOUBLECLICK")));
      else
        actionHandler.actionPerformed(new ActionEvent(e.getSource(), 0, (aeLabel + "MULTICLICK")));
    }

    public void mousePressed(MouseEvent e) {
      actionHandler.actionPerformed(new ActionEvent(this, 0, (aeLabel + "SINGLECLICK")));
    }

    public void mouseReleased(MouseEvent e) {}
  }

  public SFTableModel<T> getTableModel() {
    return tableModel;
  }

  public void propertyChange(PropertyChangeEvent evt) {
    try {
      String propName = evt.getPropertyName();
      if(propName.equals("width") || propName.equals("columWidth") || propName.equals("columnWidth")) {
        adjustColumnWidths(getParent().getWidth());
      }
    }
    catch(Exception e) {
    }
  }

  public void adjustColumnWidths(int parentWidth) {
    try {
      int width = parentWidth - 1;
      int totalWidth = 0;
      for(int i = 0; i < getColumnCount(); i++) { // headers.length; i++) {
        TableColumn tempCol = getColumnModel().getColumn(i); //getColumn(headers[i].getColumnName());
        totalWidth += tempCol.getWidth();
        if(i == (getColumnCount() - 1)) { //headers.length - 1) {
          if(totalWidth < width) {
            tempCol.setWidth(tempCol.getWidth() - totalWidth + width);
            tempCol.setPreferredWidth(tempCol.getWidth());
          }
        }
      }
    }
    catch(Exception e) {
      System.out.println("error");
    }
  }

  public void ensureRowVisible(int row) {
    try {
      Rectangle tmpRec = getCellRect(row, 0, true);
      Rectangle visible = getVisibleRect();
      if(!visible.contains(tmpRec) && !visible.intersects(tmpRec)) {
	      tmpRec.x = visible.x;
	      tmpRec.width = visible.width;
	      scrollRectToVisible(tmpRec);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void refreshRowSelection(int lastSelection) {
    if(lastSelection == -1)
      return;
    int index = -1;
    int[] sortArray = tableModel.getSortIndexArray();
    if(sortArray != null) {
      for(int i = 0; i < sortArray.length; i++) {
        if(sortArray[i] == lastSelection) {
          index = i;
          break;
        }
      }
    }
    if(index > -1 && index < tableModel.getRowCount())
      setRowSelectionInterval(index, index);
  }

  public void setParentContainer(Component component) {
    component.addComponentListener(this);
  }

  public void removeParentContainer(Component component) {
    component.removeComponentListener(this);
  }

  public void componentHidden(ComponentEvent ce) {}
  public void componentMoved(ComponentEvent ce) {}
  public void componentShown(ComponentEvent ce) {}
  public void componentResized(ComponentEvent ce) {
    redoColumnWidths();
  }
}
