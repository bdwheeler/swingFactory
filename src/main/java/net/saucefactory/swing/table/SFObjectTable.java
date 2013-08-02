package net.saucefactory.swing.table;

/**
 * Title:        SLIC Application
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1.0
 */

import java.util.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import net.saucefactory.swing.renderers.*;
import net.saucefactory.swing.utils.*;
import net.saucefactory.swing.SFSpeedLabel;

public class SFObjectTable extends JTable implements PropertyChangeListener, ComponentListener, ISFTable
{
  protected SFObjectTableModel tableModel;
  protected SFObjectTableSortAdapter sortAdapter = null;
  protected ISFTableColorAdapter colorAdapter = null;
  protected ActionListener actionHandler;
  protected String aeLabel = "";
  protected boolean doAutoSize = false;

  public SFObjectTable() {
    super();
    setAutoResizeMode(AUTO_RESIZE_OFF);
    addPropertyChangeListener(this);
  }

  public SFObjectTable(SFObjectTableColumn[] columns) {
    super();
    setTableColumns(columns);
  }

  public SFObjectTable(SFObjectTableModel _model) {
    super();
    tableModel = _model;
    setModel(_model);
    setAutoResizeMode(this.AUTO_RESIZE_OFF);
    addPropertyChangeListener(this);
  }

  public SFObjectTable(SFObjectTableColumn[] columns, SFObjectTableModel _model) {
    super();
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

  public void installTableSortAdapter() {
    if(tableModel != null) {
      if(sortAdapter == null)
        sortAdapter = new SFObjectTableSortAdapter(this);
      else
        sortAdapter.setTableModel(tableModel);
      sortAdapter.addTableHeaderMouseListener();
    }
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

  public void setTableColumns(SFObjectTableColumn[] columns) {
    if(tableModel == null)
      setTableColumns(columns, new SFObjectTableModel(columns));
    else
      setTableColumns(columns, tableModel);
  }

  public void setTableColumns(SFObjectTableColumn[] columns, SFObjectTableModel model) {
    model.setColumnHeaders(columns);
    tableModel = model;
    setModel(model);
    int tableWidth = 0;
    if(getParent() != null)
      tableWidth = getParent().getWidth();
    int runningWidth = 0;
    for(int i = 0; i < columns.length; i++) {
      TableColumn tempCol = getColumn(columns[i].getColumnName());
      SFObjectTableHeaderRenderer tmpRender = new SFObjectTableHeaderRenderer();
      int tmpWidth = columns[i].getColumnWidth();
      if(columns[i].getHeaderAllignment() > -1)
        tmpRender.setHorizontalAlignment(columns[i].getHeaderAllignment());
      tempCol.setHeaderRenderer(tmpRender);
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
      SFObjectTableColumn[] headers = tableModel.getColumnHeaders();
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
    SFObjectTableColumn[] headers = tableModel.getColumnHeaders();
    for(int i = 0; i < headers.length; i++) {
      TableColumn tempCol = getColumn(headers[i].getColumnName());
      tempCol.setHeaderRenderer(new SFObjectTableHeaderRenderer());
    }
  }

  public void setTableData(SFObjectTableColumn[] columns, Object[] tableData) {
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

  public void addTableData(Object addData) {
    if(addData == null)
      return;
    Object[] oldData = tableModel.getTableData();
    if(oldData != null && oldData.length > 0) {
      Object[] newData = new Object[oldData.length + 1];
      System.arraycopy(oldData, 0, newData, 0, oldData.length);
      newData[oldData.length] = addData;
      tableModel.setTableData(newData);
    }
    else {
      tableModel.setTableData(new Object[] {
          addData});
    }
    redoLastSort();
  }

  public void insertTableData(Object addData, int index) {
    if(addData == null)
      return;
    Object[] oldData = tableModel.getTableData();
    if(oldData != null && oldData.length > 0) {
      Object[] newData = new Object[oldData.length + 1];
      if(index > 0)
        System.arraycopy(oldData, 0, newData, 0, index);
      newData[index] = addData;
      if(index < oldData.length)
        System.arraycopy(oldData, index, newData, index + 1, oldData.length - index);
      tableModel.setTableData(newData);
    }
    else {
      tableModel.setTableData(new Object[] {
          addData});
    }
    redoLastSort();
  }

  public void updateTableDataAt(Object newData, int index) {
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
      Object[] tableData = tableModel.getTableData();
      Object[] newData;
      if(tableData.length > 1) {
        newData = new Object[tableData.length - 1];
        if(selectedItem > 0)
          System.arraycopy(tableData, 0, newData, 0, selectedItem);
        if(selectedItem < newData.length)
          System.arraycopy(tableData, selectedItem + 1, newData, selectedItem,
              newData.length - selectedItem);
        tableModel.setTableData(newData);
        redoLastSort();
      }
      else {
        tableModel.setTableData(null);
      }
    }
  }

  public void setSortIndexArray(int[] sortIndexArr) {
    tableModel.setSortIndexArray(sortIndexArr);
  }

  public SFObjectTableSortAdapter getSortAdapter() {
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
      sortAdapter.reallocateIndexes();
  }

  public void redoLastSort() {
    if(sortAdapter != null)
      sortAdapter.redoLastSort();
  }

  public void setActionHandler(ActionListener handler) {
    this.actionHandler = handler;
    addMouseListener(new MouseHandler());
  }

  public int getSelectedRowWithSort() {
    int index = super.getSelectedRow();
    if(sortAdapter != null && index > -1 && index < sortAdapter.indexes.length)
      index = sortAdapter.indexes[index];
    return index;
  }

  public int[] getSelectedRowsWithSort() {
    int[] indices = super.getSelectedRows();
    if(indices != null && sortAdapter != null && sortAdapter.indexes.length > 0) {
      for(int i = 0; i < indices.length; i++) {
        if(indices[i] > -1 && indices[i] < sortAdapter.indexes.length)
          indices[i] = sortAdapter.indexes[indices[i]];
      }
    }
    return indices;
  }

  public void clear() {
    tableModel.clear();
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

  public String getHTMLView() {
    try {
      SFObjectTableColumn[] htmlCols = null;
      if(tableModel != null)
        htmlCols = tableModel.getColumnHeaders();
      if(htmlCols == null)
        return "";
      int pageWidth = 900;
      double colWidthSum = 0;
      int runningPercent = 0;
      for(int i = 0; i < htmlCols.length; i++)
        colWidthSum += (double)htmlCols[i].getColumnWidth();
      StringBuffer buf = new StringBuffer();
      buf.append("<table cellpadding=2 cellspacing=0 border=1 width=" + pageWidth + ">\n");
      buf.append("<tr>\n");
      for(int i = 0; i < htmlCols.length; i++) {
        double tmpColWidth = (double)htmlCols[i].getColumnWidth();
        double tmpPercent = tmpColWidth / colWidthSum * 100;
        int tmpWidth = (int)tmpPercent;
        if(i == (htmlCols.length - 1))
          tmpWidth = 100 - runningPercent;
        else
          runningPercent += tmpWidth;
        String tmpTitle = htmlCols[i].getColumnName();
        int newIndex = tmpTitle.indexOf('\n');
        if(newIndex > -1)
          tmpTitle = tmpTitle.substring(0, newIndex) + "<br>" + tmpTitle.substring(newIndex + 1);
        buf.append("<td bgcolor=999999 align=left valign=top width=" + tmpWidth +
            "%><font size=2 type=arial>" + tmpTitle + "</font></td>\n");
      }
      buf.append("</tr>\n");
      int rowCount = getRowCount();
      for(int i = 0; i < rowCount; i++) {
        buf.append("<tr>\n");
        for(int j = 0; j < htmlCols.length; j++) {
          buf.append("<td align=left valign=top><font size=2 type=arial>" + getStringValueAt(i, j,
              false) + "</font></td>\n");
        }
        buf.append("</tr>\n");
      }
      return buf.toString();
    }
    catch(Exception e) {
      e.printStackTrace();
      return "";
    }
  }

  public String getTextView() {
    try {
      SFObjectTableColumn[] textCols = null;
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
    }
  }

  public String getCSVView() {
    try {
      SFObjectTableColumn[] csvCols = null;
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
    }
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

  public SFObjectTableModel getTableModel() {
    return tableModel;
  }

  public void propertyChange(PropertyChangeEvent evt) {
    try {
      String propName = evt.getPropertyName();
      if(propName.equals("width") || propName.equals("columWidth") || propName.equals("columnWidth")) {
        adjustColumnWidths(getParent().getWidth() - 1);
      }
    }
    catch(Exception e) {
    }
  }

  public void adjustColumnWidths(int parentWidth) {
    try {
      int width = parentWidth - 1;
      int totalWidth = 0;
      //CObjectTableColumnHeader[] headers = tableModel.getColumnHeaders();
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

  public static void main(String[] args) {
    try {
      SFObjectTableModel model = new SFObjectTableModel();
      SFObjectTable table = new SFObjectTable(model);
      table.installTableSortAdapter();
      System.out.println("cool");
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
}
