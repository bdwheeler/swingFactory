package net.saucefactory.swing.table;

import net.saucefactory.swing.utils.TextUtility;

public class SFTableTextExportAdapter extends SFAbstractTableExportAdapter {

  private int colCount = 0;

  public SFTableTextExportAdapter() {
  }

  public void initExportAdapter(SFTable table) {
    super.initExportAdapter(table);
    SFTableColumn[] headers = table.getTableModel().getColumnHeaders();
    if(headers == null)
      ready = false;
    else {
      ready = true;
      colCount = headers.length;
    }
  }

  public Object getTableValueAt(int row, int column) {
    String rtn = table.getStringValueAt(row, column, false);
    if(rtn == null)
      rtn = "";
    return rtn;
  }

  public void appendHeaderColumnStart(StringBuffer buf, SFTableColumn header, int column) {
    if(column != 0)
      buf.append("\t");
  }

  public void appendHeaderColumnValue(StringBuffer buf, SFTableColumn header, int column) {
    String tmpTitle = header.getColumnName();
    tmpTitle = TextUtility.stripLineBreaks(tmpTitle);
    buf.append(tmpTitle);
  }

  public void appendHeaderColumnEnd(StringBuffer buf, SFTableColumn header, int column) {}

  public void appendHeaderEnd(StringBuffer buf) {
    buf.append("\n");
  }

  public void appendDataColumnStart(StringBuffer buf, Object columnValue, int row, int column) {
    if(column != 0)
      buf.append("\t");
  }

  public void appendDataColumnValue(StringBuffer buf, Object columnValue, int row, int column) {
    buf.append((String)columnValue);
  }

  public void appendDataColumnEnd(StringBuffer buf, Object columnValue, int row, int column) {}

  public void appendDataRowEnd(StringBuffer buf, int row) {
    buf.append("\n");
  }
}
