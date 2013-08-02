package net.saucefactory.swing.table;

public interface ISFTableExportAdapter {
  public void initExportAdapter(SFTable table);
  public Object getTableValueAt(int row, int column);
  public boolean isReady();
  public void appendPageStart(StringBuffer buf);
  public void appendBodyStart(StringBuffer buf);
  public void appendTitleData(StringBuffer buf);
  public void appendSummaryData(StringBuffer buf);
  public void appendContentStart(StringBuffer buf);
  public void appendHeaderStart(StringBuffer buf);
  public void appendHeaderColumnStart(StringBuffer buf, SFTableColumn header, int column);
  public void appendHeaderColumnValue(StringBuffer buf, SFTableColumn header, int column);
  public void appendHeaderColumnEnd(StringBuffer buf, SFTableColumn header, int column);
  public void appendHeaderEnd(StringBuffer buf);
  public void appendDataStart(StringBuffer buf);
  public void appendDataRowStart(StringBuffer buf, int row);
  public void appendDataColumnStart(StringBuffer buf, Object columnValue, int row, int column);
  public void appendDataColumnValue(StringBuffer buf, Object columnValue, int row, int column);
  public void appendDataColumnEnd(StringBuffer buf, Object columnValue, int row, int column);
  public void appendDataRowEnd(StringBuffer buf, int row);
  public void appendDataEnd(StringBuffer buf);
  public void appendContentEnd(StringBuffer buf);
  public void appendFooter(StringBuffer buf);
  public void appendBodyEnd(StringBuffer buf);
  public void appendPageEnd(StringBuffer buf);
}
