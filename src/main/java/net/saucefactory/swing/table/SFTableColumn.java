package net.saucefactory.swing.table;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import net.saucefactory.swing.data.ISFDataAdapter;
import net.saucefactory.swing.data.SFObjectDataAdapter;
import net.saucefactory.swing.data.SFFieldDataAdapter;
import net.saucefactory.swing.data.SFMethodDataAdapter;
import net.saucefactory.swing.data.SFSubMethodDataAdapter;
import net.saucefactory.swing.data.SFMethodChainDataAdapter;
import net.saucefactory.swing.data.SFSimpleMethodDataAdapter;
import net.saucefactory.swing.renderers.ISFTableRenderer;

public class SFTableColumn {

  protected int columnID;
  protected String columnName;
  protected String displayName;
  protected int columnWidth;
  protected boolean editable;
  protected boolean sortable;
  protected boolean sortCaseInsensitive = false;
  protected Color headerBackground;
  protected Color headerForeground;
  protected TableCellRenderer renderer;
  protected TableCellEditor editor;
  protected int headerAllignment = SwingConstants.LEFT;
  protected ISFDataAdapter dataAdapter;
  protected TableCellRenderer headerRenderer;

  public SFTableColumn(int columnID,
      String columnName,
      int columnWidth,
      int headerAllignment,
      boolean editable,
      Color headerBackground,
      Color headerForeground,
      TableCellRenderer renderer,
      TableCellEditor editor,
      ISFDataAdapter dataAdapter) {
    this.columnID = columnID;
    this.columnName = columnName;
    this.displayName = columnName;
    this.columnWidth = columnWidth;
    this.editable = editable;
    this.headerBackground = headerBackground;
    this.headerForeground = headerForeground;
    this.renderer = renderer;
    this.editor = editor;
    this.headerAllignment = headerAllignment;
    this.dataAdapter = dataAdapter;
    sortable = true;
    initHeaderRenderer();
  }

  public SFTableColumn(int columnID,
      String columnName,
      int columnWidth,
      TableCellRenderer renderer,
      ISFDataAdapter dataAdapter) {
    this(columnID,columnName,columnWidth, SwingConstants.LEFT, false,
        null, null, renderer, null, dataAdapter);
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public static SFTableColumn getObjectInstance(int columnID,
      String columnName,
      int columnWidth,
      TableCellRenderer renderer) {
    return new SFTableColumn(columnID, columnName, columnWidth, renderer,
        new SFObjectDataAdapter());
  }

  public static SFTableColumn getFieldInstance(int columnID,
      String columnName,
      int columnWidth,
      TableCellRenderer renderer,
      String fieldName) {
    return new SFTableColumn(columnID, columnName, columnWidth, renderer,
        new SFFieldDataAdapter(fieldName));
  }

  public static SFTableColumn getMethodInstance(int columnID,
      String columnName,
      int columnWidth,
      TableCellRenderer renderer,
      Class className, String getName) {
    return new SFTableColumn(columnID, columnName, columnWidth, renderer,
        new SFMethodDataAdapter(className, getName, null, null, null));
  }

  public static SFTableColumn getMethodInstance(int columnID,
      String columnName,
      int columnWidth,
      TableCellRenderer renderer,
      Class className, String getName,
      Class[] getParams, String setName,
      Class[] setParams) {
    return new SFTableColumn(columnID, columnName, columnWidth, renderer,
        new SFMethodDataAdapter(className, getName, getParams, setName, setParams));
  }

  public static SFTableColumn getSubMethodInstance(int columnID,
      String columnName,
      int columnWidth,
      TableCellRenderer renderer,
      Class className, String getName,
      Class subClassName, String subGetName) {
    return new SFTableColumn(columnID, columnName, columnWidth, renderer,
        new SFSubMethodDataAdapter(className, getName, subClassName, subGetName));
  }

  public static SFTableColumn getMethodChainInstance(int columnID,
      String columnName,
      int columnWidth,
      TableCellRenderer renderer,
      Class[] classNames, String[] getNames) {
    return new SFTableColumn(columnID, columnName, columnWidth, renderer,
        new SFMethodChainDataAdapter(classNames, getNames));
  }

  public static SFTableColumn getSimpleMethodInstance(int columnID,
      String columnName,
      int columnWidth,
      TableCellRenderer renderer,
      String getNames) {
    return new SFTableColumn(columnID, columnName, columnWidth, renderer,
        new SFSimpleMethodDataAdapter(getNames));
  }

  public static SFTableColumn getInstance(int columnID,
      String columnName,
      int columnWidth,
      TableCellRenderer renderer,
      ISFDataAdapter adapter) {
    return new SFTableColumn(columnID, columnName, columnWidth, renderer,
        adapter);
  }

  private void initHeaderRenderer() {
    headerRenderer = SFTableHeaderRendererFactory.getIndividualHeaderRenderer(headerAllignment, headerBackground, headerForeground);
       //new SFTableHeaderRenderer(headerAllignment, headerForeground, headerBackground);
  }

  public TableCellRenderer getHeaderRenderer() {
    return headerRenderer;
  }

  public void setHeaderRenderer(TableCellRenderer headerRenderer) {
    this.headerRenderer = headerRenderer;
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

  public int getHeaderAllignment() {
    return headerAllignment;
  }

  public boolean getEditable() {
    return editable;
  }

  public boolean isSortable() {
    return sortable;
  }

  public void setSortable(boolean sortable) {
    this.sortable = sortable;
  }

  public Color getHeaderBackground() {
    return headerBackground;
  }

  public Color getHeaderForeground() {
    return headerForeground;
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

  public void setHeaderAllignment(int headerAllignment) {
    this.headerAllignment = headerAllignment;
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  public void setHeaderBackground(Color headerBackground) {
    this.headerBackground = headerBackground;
  }

  public void setHeaderForegroundColor(Color headerForeground) {
    this.headerForeground = headerForeground;
  }

  public void setRenderer(ISFTableRenderer renderer) {
    this.renderer = renderer;
  }

  public void setEditor(TableCellEditor editor) {
    this.editor = editor;
  }

  public Object getValue(Object item) {
    return dataAdapter.getValue(item);
  }

  public void setValue(Object item, Object value) {
    dataAdapter.setValue(item, value);
  }

  public ISFDataAdapter getDataAdapter() {
    return dataAdapter;
  }

  public void setDataAdapter(ISFDataAdapter dataAdapter) {
    this.dataAdapter = dataAdapter;
  }

	
	public boolean isSortCaseInsensitive() {
		return sortCaseInsensitive;
	}

	
	public void setSortCaseInsensitive(boolean sortCaseInsensitive) {
		this.sortCaseInsensitive = sortCaseInsensitive;
	}
}
