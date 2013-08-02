package net.saucefactory.swing.table;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.saucefactory.swing.SFSpeedLabel;
import net.saucefactory.swing.data.SFTableColumnViewState;
import net.saucefactory.swing.renderers.ISFTableRenderer;
import net.saucefactory.swing.renderers.SFStringRenderer;
import net.saucefactory.swing.utils.PrintingUtility;


public class SFListTable<T extends Object> extends SFTable<T>  {
	
  private static final long serialVersionUID = 1L;
	
  protected SFListTableModel<T> tableModel;
  
  public SFListTable() {
    super();
    SFTableHeaderRendererFactory.initTableHeaderRenderer(this);
    setAutoResizeMode(AUTO_RESIZE_OFF);
    addPropertyChangeListener(this);
  }

  public SFListTable(SFTableColumn[] columns) {
    super();
    SFTableHeaderRendererFactory.initTableHeaderRenderer(this);
    setTableColumns(columns);
  }

  public SFListTable(SFListTableModel<T> _model) {
    super();
    SFTableHeaderRendererFactory.initTableHeaderRenderer(this);
    tableModel = _model;
    setModel(_model);
    setAutoResizeMode(SFTable.AUTO_RESIZE_OFF);
    addPropertyChangeListener(this);
  }

  public SFListTable(SFTableColumn[] columns, SFListTableModel<T> _model) {
    super();
    SFTableHeaderRendererFactory.initTableHeaderRenderer(this);
    tableModel = _model;
    setModel(_model);
    this.setTableColumns(columns, _model);
  }

  public void setTableColumns(SFTableColumn[] columns) {
    if(tableModel == null)
      this.setTableColumns(columns, new SFListTableModel<T>(columns), false);
    else
      this.setTableColumns(columns, tableModel, true);
  }

  public void removeTableColumn(int column) {
    SFListTableModel<T> tmpModel = getTableModel();
    if(tmpModel != null) {
      removeTableColumn(tmpModel.getColumnHeader(convertColumnIndexToModel(column)));
    }
  }

  public void removeTableColumn(SFTableColumn column) {
    SFListTableModel<T> tmpModel = getTableModel();
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

  public void setTableColumns(SFTableColumn[] columns, SFListTableModel<T> model) {
    super.setTableColumns(columns, model, true);
  }
 
  public void setTableColumns(SFTableColumn[] columns, SFListTableModel<T> model, boolean redoColumnModel) {
    setTableColumns(columns, model, redoColumnModel, true);
  }

  public void setTableColumns(SFTableColumn[] columns, SFListTableModel<T> model, boolean redoColumnModel, boolean allColumns) {
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

  public void setTableData(SFTableColumn[] columns, List<T> tableData) {
    setTableColumns(columns);
    tableModel.setTableData(tableData);
    redoLastSort();
  }

  public void setTableData(List<T> tableData) {
    setTableData(tableData, true);
  }

  public void setTableData(List<T> tableData, boolean redoColumns) {
    tableModel.setTableData(tableData);
    if(!doAutoSize)
      setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    redoLastSort();
    if(redoColumns)
      redoColumnWidths();
  }

  public void addTableData(List<T> addData) {
    if(addData == null || addData.size() < 1)
      return;
    List<T> oldData = tableModel.getListTableData();
    if(oldData != null && oldData.size() > 0) {
      oldData.addAll(addData);
      tableModel.setTableData(oldData);
    }
    else {
      tableModel.setTableData(addData);
    }
    redoLastSort();
  }

  public boolean hasObject(Object testData) {
    List<T> data = tableModel.getListTableData();
    if(testData != null && data != null) {
      return data.contains(testData);
    }
    return false;
  }

  public void addTableData(T addData) {
    if(addData == null)
      return;
    List<T> oldData = tableModel.getListTableData();
    if(oldData != null) {
      oldData.add(addData);
      tableModel.setTableData(oldData);
    }
    else {
    	List<T> newData = new ArrayList<T>();
    	newData.add(addData);
      tableModel.setTableData(newData);
    }
    redoLastSort();
  }

  public void insertTableData(T addData, int index) {
    if(addData == null)
      return;
    List<T> oldData = tableModel.getListTableData();
    if(oldData != null) {
    	oldData.add(index, addData);
    }
    else {
    	List<T> newData = new ArrayList<T>();
    	newData.add(addData);
      tableModel.setTableData(newData);
    }
    redoLastSort();
  }

  public void updateTableDataAt(T newData, int index) {
    if(newData == null)
      return;
    List<T> oldData = tableModel.getListTableData();
    if(oldData != null && oldData.size() > 0 && index > -1 && index < oldData.size()) {
      oldData.set(index, newData);
      tableModel.setTableData(oldData);
    }
    redoLastSort();
  }

  public void deleteItem(int selectedItem) {
    if(selectedItem > -1 && tableModel.getListTableData() != null) {
      tableModel.getListTableData().remove(selectedItem);
    }
  }

  public void deleteItem(T item) {
    if(item != null && tableModel.getListTableData() != null)
    	tableModel.getListTableData().remove(item);
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
    if(index > -1 && tableModel.getListTableData() != null &&
    				index < tableModel.getListTableData().size()) {
      return tableModel.getListTableData().get(index);
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

  public SFListTableModel<T> getTableModel() {
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
      Rectangle r = getCellRect(row, 0, true);
      scrollRectToVisible(r);
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
