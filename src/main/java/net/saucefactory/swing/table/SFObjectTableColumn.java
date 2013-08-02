package net.saucefactory.swing.table;

/**
 * Title:        SLIC Application
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      CAISO
 * @author Brian Wheeler
 * @version 1.0
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.lang.reflect.Method;

public class SFObjectTableColumn {

  public static final int COL_TYPE_FIELD = 0;
  public static final int COL_TYPE_METHOD = 1;

  protected String columnName;
  protected int columnWidth;
  protected String fieldName;
  protected boolean editable;
  protected Color backgroundColor;
  protected Color foregroundColor;
  protected TableCellRenderer renderer;
  protected TableCellEditor editor;
  protected int cellAllignment = JLabel.LEADING;
  protected int headerAllignment = JLabel.LEFT;
  protected int columnID;
  public int colType = COL_TYPE_FIELD;
  protected Method method;
  public Class[] methodParams = null;

  public SFObjectTableColumn(String columnName,
      int columnWidth,
      String fieldName,
      int cellAllignment,
      boolean editable,
      Color backgroundColor,
      Color foregroundColor,
      TableCellRenderer renderer,
      TableCellEditor editor) {
    this.columnID = -1;
    this.columnName = columnName;
    this.columnWidth = columnWidth;
    this.fieldName = fieldName;
    this.editable = editable;
    this.backgroundColor = backgroundColor;
    this.foregroundColor = foregroundColor;
    this.renderer = renderer;
    this.editor = editor;
    setCellAlignemnt(cellAllignment);
  }

  public SFObjectTableColumn(int columnID, String columnName,
      int columnWidth,
      String fieldName,
      int cellAllignment,
      boolean editable,
      Color backgroundColor,
      Color foregroundColor,
      TableCellRenderer renderer,
      TableCellEditor editor) {
    this.columnID = columnID;
    this.columnName = columnName;
    this.columnWidth = columnWidth;
    this.fieldName = fieldName;
    this.editable = editable;
    this.backgroundColor = backgroundColor;
    this.foregroundColor = foregroundColor;
    this.renderer = renderer;
    this.editor = editor;
    setCellAlignemnt(cellAllignment);
  }

  public SFObjectTableColumn(String columnName,
      int columnWidth,
      String fieldName,
      int cellAllignment,
      boolean editable,
      Color backgroundColor,
      Color foregroundColor) {
    this(columnName, columnWidth, fieldName, cellAllignment, editable, backgroundColor,
        foregroundColor, null, null);
  }

  public SFObjectTableColumn(String columnName, int columnWidth, String fieldName,
      int cellAllignment, boolean editable) {
    this(columnName, columnWidth, fieldName, cellAllignment, editable, Color.white, Color.black, null, null);
  }

  public SFObjectTableColumn(int columnID, String columnName,
      int columnWidth,
      Method method,
      Class[] methodParams,
      int cellAllignment,
      boolean editable,
      Color backgroundColor,
      Color foregroundColor,
      TableCellRenderer renderer,
      TableCellEditor editor) {
    this.columnID = columnID;
    this.columnName = columnName;
    this.columnWidth = columnWidth;
    this.fieldName = "";
    this.method = method;
    this.methodParams = methodParams;
    this.colType = COL_TYPE_METHOD;
    this.editable = editable;
    this.backgroundColor = backgroundColor;
    this.foregroundColor = foregroundColor;
    this.renderer = renderer;
    this.editor = editor;
    setCellAlignemnt(cellAllignment);
  }

  private void setCellAlignemnt(int cellAllignment) {
    if(cellAllignment != -1) {
      this.cellAllignment = cellAllignment;
      if(renderer != null && renderer instanceof JLabel)
        ((JLabel)renderer).setHorizontalAlignment(cellAllignment);
    }
  }

  public String getColumnName() {
    return columnName;
  }

  public int getColumnID() {
    return columnID;
  }

  public int getColumnWidth() {
    return columnWidth;
  }

  public String getFieldName() {
    return fieldName;
  }

  public int getCellAllignment() {
    return cellAllignment;
  }

  public int getHeaderAllignment() {
    return headerAllignment;
  }

  public boolean getEditable() {
    return editable;
  }

  public Color getBackgroundColor() {
    return backgroundColor;
  }

  public Color getForegroundColor() {
    return foregroundColor;
  }

  public TableCellRenderer getRenderer() {
    return renderer;
  }

  public TableCellEditor getEditor() {
    return editor;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public void setColumnID(int columnID) {
    this.columnID = columnID;
  }

  public void setColumnWidth(int columnWidth) {
    this.columnWidth = columnWidth;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public void setCellAllignment(int cellAllignment) {
    this.cellAllignment = cellAllignment;
  }

  public void setHeaderAllignment(int headerAllignment) {
    this.headerAllignment = headerAllignment;
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  public void setBackgroundColor(Color backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public void setForegroundColor(Color foregroundColor) {
    this.foregroundColor = foregroundColor;
  }

  public void setRenderer(TableCellRenderer renderer) {
    this.renderer = renderer;
  }

  public void setEditor(TableCellEditor editor) {
    this.editor = editor;
  }

  public Object getValue(Object item) {
    try {
      if(colType == COL_TYPE_FIELD)
        return item.getClass().getField(fieldName).get(item);
      else
        return method.invoke(item, null);
    }
    catch(Exception e) {
      return null;
    }
  }

}
