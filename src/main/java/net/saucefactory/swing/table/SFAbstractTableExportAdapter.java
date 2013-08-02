package net.saucefactory.swing.table;

import java.awt.Color;
import net.saucefactory.swing.utils.ScreenUtility;

public class SFAbstractTableExportAdapter implements ISFTableExportAdapter {

  protected SFTable table = null;
  protected ISFTableColorAdapter colorAdapter = null;
  protected boolean ready = false;
  private Color defaultBackground = Color.white;
  private Color defaultForeground = Color.black;
  private Color defaultHeaderBackground = Color.white;
  private Color defaultHeaderForeground = Color.black;
  private String defaultBackgroundHex = "FFFFFF";
  private String defaultForegroundHex = "000000";
  private String defaultHeaderBackgroundHex = "FFFFFF";
  private String defaultHeaderForegroundHex = "000000";


  public SFAbstractTableExportAdapter() {}

  public void setColorAdapter(ISFTableColorAdapter colorAdapter) {
    this.colorAdapter = colorAdapter;
  }

  public void initExportAdapter(SFTable table) {
    this.table = table;
  }

  public void setForegroundColor(Color color) {
    defaultForeground = color;
    defaultForegroundHex = ScreenUtility.colorToHexString(color);
  }

  public void setBackgroundColor(Color color) {
    defaultBackground = color;
    defaultBackgroundHex = ScreenUtility.colorToHexString(color);
  }

  public void setHeaderBackgroundColor(Color color) {
    defaultHeaderBackground = color;
    defaultHeaderBackgroundHex = ScreenUtility.colorToHexString(color);
  }

  public void setHeaderForegroundColor(Color color) {
    defaultHeaderForeground = color;
    defaultHeaderForegroundHex = ScreenUtility.colorToHexString(color);
  }

  public boolean isReady() {
    return ready;
  }

  public Object getTableValueAt(int row, int column) {
    return table.getStringValueAt(row, column, false);
  }

  public void appendPageStart(StringBuffer buf) {}

  public void appendTitleData(StringBuffer buf) {}

  public void appendPageEnd(StringBuffer buf) {}

  public void appendSummaryData(StringBuffer buf) {}

  public void appendBodyStart(StringBuffer buf) {}

  public void appendBodyEnd(StringBuffer buf) {}

  public void appendContentStart(StringBuffer buf) {}

  public void appendFooter(StringBuffer buf) {}

  public void appendContentEnd(StringBuffer buf) {}

  public void appendHeaderStart(StringBuffer buf) {}

  public void appendHeaderColumnStart(StringBuffer buf, SFTableColumn header, int column) {}

  public void appendHeaderColumnValue(StringBuffer buf, SFTableColumn header, int column) {
    String tmpTitle = header.getColumnName();
    buf.append(tmpTitle);
  }

  public void appendHeaderColumnEnd(StringBuffer buf, SFTableColumn header, int column) {}

  public void appendHeaderEnd(StringBuffer buf) {}

  public void appendDataStart(StringBuffer buf) {}

  public void appendDataRowStart(StringBuffer buf, int row) {}

  public void appendDataColumnStart(StringBuffer buf, Object columnValue, int row, int column) {}

  public void appendDataColumnValue(StringBuffer buf, Object columnValue, int row, int column) {
    buf.append(columnValue.toString());
  }

  public void appendDataColumnEnd(StringBuffer buf, Object columnValue, int row, int column) {}

  public void appendDataRowEnd(StringBuffer buf, int row) {}

  public void appendDataEnd(StringBuffer buf) {}

  public Color getBackground(int row, int column) {
    if(colorAdapter != null)
      return colorAdapter.getBackground(table, false, false, row, column);
    return defaultBackground;
  }

  public String getBackgroundHex(int row, int column) {
    if(colorAdapter != null)
      return ScreenUtility.colorToHexString(colorAdapter.getBackground(table, false, false, row, column));
    return defaultBackgroundHex;
  }

  public Color getForeground(int row, int column) {
    if(colorAdapter != null)
      return colorAdapter.getForeground(table, false, false, row, column);
    return defaultForeground;
  }

  public String getForegroundHex(int row, int column) {
    if(colorAdapter != null)
      return ScreenUtility.colorToHexString(colorAdapter.getForeground(table, false, false, row, column));
    return defaultForegroundHex;
  }

  public Color getHeaderBackground() {
    return defaultHeaderBackground;
  }

  public String getHeaderBackgroundHex() {
    return defaultHeaderBackgroundHex;
  }

  public Color getHeaderForeground() {
    return defaultHeaderForeground;
  }

  public String getHeaderForegroundHex() {
    return defaultHeaderForegroundHex;
  }
}
